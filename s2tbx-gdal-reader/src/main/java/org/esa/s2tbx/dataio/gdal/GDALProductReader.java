package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import javax.media.jai.JAI;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class GDALProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(GDALProductReader.class.getName());;

    private Product product;

    protected GDALProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Path inputFile = getFileInput(getInput());
        Dataset poDataset = gdal.Open(inputFile.toString(), gdalconst.GA_ReadOnly);
        if (poDataset == null) {
            // unknown file format
            throw new IOException("File '"+ inputFile.toString()+"' can not be opened.");
        }
        Vector<?> list = poDataset.GetMetadata_List();
        try {
            int imageWidth = poDataset.getRasterXSize();
            int imageHeight = poDataset.getRasterYSize();
            String productName = inputFile.getFileName().toString();
            String productType = "GDAL";
            this.product = new Product(productName, productType, imageWidth, imageHeight);

            int bandCount = poDataset.getRasterCount();

            MetadataElement metadataElement = new MetadataElement("Image info");
            metadataElement.setAttributeInt("width", imageWidth);
            metadataElement.setAttributeInt("height", imageHeight);
            metadataElement.setAttributeInt("numComponents", bandCount);

            MetadataElement metadataRoot = this.product.getMetadataRoot();
            metadataRoot.addElement(metadataElement);

            int pixels = imageWidth * imageHeight;
            Double[] max = new Double[1];
            Double[] min = new Double[1];

            for (int band = 0; band < bandCount; band++) {
                // Bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band poBand = poDataset.GetRasterBand(band + 1);

                int bufferType = poBand.getDataType();
                int bufferSize = pixels * gdal.GetDataTypeSize(bufferType) / 8;

                ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
                data.order(ByteOrder.nativeOrder());

                MetadataElement componentElement = new MetadataElement("Component");
                metadataElement.addElement(componentElement);

                componentElement.setAttributeString("data type", gdal.GetDataTypeName(poBand.getDataType()));
                componentElement.setAttributeString("color interpretation", gdal.GetColorInterpretationName(poBand.GetRasterColorInterpretation()));

                poBand.GetMinimum(min);
                poBand.GetMaximum(max);
                if (min[0] != null) {
                    componentElement.setAttributeDouble("minim", min[0].doubleValue());
                }
                if (max[0] != null) {
                    componentElement.setAttributeDouble("maximum", max[0].doubleValue());
                }

                int returnVal = poBand.ReadRaster_Direct(0, 0, poBand.getXSize(), poBand.getYSize(), imageWidth, imageHeight, bufferType, data);
                if (returnVal == gdalconstConstants.CE_None) {
                    int[] bankIndices = new int[] {0};
                    int[] bandOffsets = new int[] {0};

                    DataBuffer imgBuffer = null;
                    SampleModel sampleModel = null;
                    int data_type = 0;
                    int bandDataType = 0;
                    int precision = 0;
                    boolean signed = false;
                    if (bufferType == gdalconstConstants.GDT_Byte) {
                        precision = 8;
                        signed = true;
                        byte[] bytes = new byte[pixels];
                        data.get(bytes);
                        bandDataType = ProductData.TYPE_UINT8;
                        imgBuffer = new DataBufferByte(bytes, pixels);
                        sampleModel = new BandedSampleModel(DataBuffer.TYPE_BYTE, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                        data_type = (poBand.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) ? BufferedImage.TYPE_BYTE_INDEXED : BufferedImage.TYPE_BYTE_GRAY;
                    } else if (bufferType == gdalconstConstants.GDT_Int16 || bufferType == gdalconstConstants.GDT_UInt16) {
                        precision = 16;
                        signed = (bufferType == gdalconstConstants.GDT_Int16) ? true : false;
                        short[] shorts = new short[pixels];
                        data.asShortBuffer().get(shorts);
                        bandDataType = ProductData.TYPE_UINT16;
                        imgBuffer = new DataBufferShort(shorts, pixels);
                        sampleModel = new BandedSampleModel(DataBuffer.TYPE_USHORT, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                        data_type = BufferedImage.TYPE_USHORT_GRAY;
                    } else if (bufferType == gdalconstConstants.GDT_Int32) {
                        precision = 32;
                        signed = true;
                        int[] ints = new int[pixels];
                        data.asIntBuffer().get(ints);
                        bandDataType = ProductData.TYPE_INT32;
                        imgBuffer = new DataBufferInt(ints, pixels);
                        sampleModel = new BandedSampleModel(DataBuffer.TYPE_INT, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                        data_type = BufferedImage.TYPE_CUSTOM;
                    } else if (bufferType == gdalconstConstants.GDT_Float32) {
                        precision = 32;
                        signed = true;
                        float[] floats = new float[pixels];
                        data.asFloatBuffer().get(floats);
                        bandDataType = ProductData.TYPE_FLOAT32;
                        imgBuffer = new DataBufferFloat(floats, pixels);
                        sampleModel = new BandedSampleModel(DataBuffer.TYPE_FLOAT, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                        data_type = (poBand.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) ? BufferedImage.TYPE_BYTE_INDEXED : BufferedImage.TYPE_BYTE_GRAY;
                    } else {
                        throw new IOException("Unknown GDAL data type " + bufferType + ".");
                    }
                    componentElement.setAttributeInt("precision", precision);
                    componentElement.setAttributeString("signed", Boolean.toString(signed));

                    WritableRaster raster = Raster.createWritableRaster(sampleModel, imgBuffer, null);
                    BufferedImage img = null;
                    if (poBand.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) {
                        ColorModel cm = poBand.GetRasterColorTable().getIndexColorModel(gdal.GetDataTypeSize(bufferType));
                        img = new BufferedImage(cm, raster, false, null);
                    } else {
                        img = new BufferedImage(imageWidth, imageHeight, data_type);
                        img.setData(raster);
                    }

                    String bandName = "band_" + String.valueOf(band + 1);
                    Band virtualBand = new Band(bandName, bandDataType, imageWidth, imageHeight);
                    virtualBand.setSourceImage(img);
                    this.product.addBand(virtualBand);
                } else {
                    throw new IOException("Failed to read the product data.");
                }
            }
            this.product.setPreferredTileSize(JAI.getDefaultTileSize());
            this.product.setFileLocation(inputFile.toFile());
            this.product.setModified(false);
            return this.product;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            String msg = "Error while reading file '" + inputFile.toString() + "'.";
            throw new IOException(msg, ex);
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // do nothing
    }

    private static Path getFileInput(Object input) {
        if (input instanceof String) {
            return Paths.get((String) input);
        } else if (input instanceof File) {
            return ((File) input).toPath();
        } else if (input instanceof Path) {
            return (Path) input;
        }
        return null;
    }
}
