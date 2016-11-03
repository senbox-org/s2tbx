package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.gdal.internal.BufferTypeDescriptor;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.StringUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.JAI;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic GDAL Reader.
 *
 * @author Jean Coravu
 */
public class GDALProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(GDALProductReader.class.getName());
    private static final Map<Integer, BufferTypeDescriptor> bufferTypes;

    private Product product;

    static {
        bufferTypes = new HashMap<>();
        bufferTypes.put(gdalconstConstants.GDT_Byte,
                        new BufferTypeDescriptor(8, true, ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE));
        bufferTypes.put(gdalconstConstants.GDT_Int16,
                        new BufferTypeDescriptor(16, true, ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT));
        bufferTypes.put(gdalconstConstants.GDT_UInt16,
                        new BufferTypeDescriptor(16, false, ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT));
        bufferTypes.put(gdalconstConstants.GDT_Int32,
                        new BufferTypeDescriptor(32, true, ProductData.TYPE_INT32, DataBuffer.TYPE_INT));
        bufferTypes.put(gdalconstConstants.GDT_UInt32,
                        new BufferTypeDescriptor(32, false, ProductData.TYPE_UINT32, DataBuffer.TYPE_INT));
        bufferTypes.put(gdalconstConstants.GDT_Float32,
                        new BufferTypeDescriptor(32, true, ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT));
        bufferTypes.put(gdalconstConstants.GDT_Float64,
                        new BufferTypeDescriptor(64, true, ProductData.TYPE_FLOAT64, DataBuffer.TYPE_DOUBLE));
    }

    protected GDALProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Object input = getInput();

        logger.info("Loading the product using the GDAL reader from file '" + input.toString() + "'.");

        Path inputFile = getFileInput(input);
        if (inputFile == null) {
            throw new IOException("The file '"+ input + "' to load the product is invalid.");
        }

        Dataset gdalProduct = gdal.Open(inputFile.toString(), gdalconst.GA_ReadOnly);
        if (gdalProduct == null) {
            // unknown file format
            throw new IOException("The file '"+ inputFile.toString()+"' to load the product can not be opened.");
        }

        try {
            int imageWidth = gdalProduct.getRasterXSize();
            int imageHeight = gdalProduct.getRasterYSize();
            String productName = inputFile.getFileName().toString();
            String productType = "GDAL";
            //Dimension preferredTileSize = JAI.getDefaultTileSize();

            this.product = new Product(productName, productType, imageWidth, imageHeight);
            this.product.setPreferredTileSize(JAI.getDefaultTileSize());
            this.product.setFileLocation(inputFile.toFile());

            int bandCount = gdalProduct.getRasterCount();

            MetadataElement metadataElement = new MetadataElement("Image info");
            metadataElement.setAttributeInt("width", imageWidth);
            metadataElement.setAttributeInt("height", imageHeight);
            metadataElement.setAttributeInt("numComponents", bandCount);

            MetadataElement metadataRoot = this.product.getMetadataRoot();
            metadataRoot.addElement(metadataElement);

            GeoCoding geoCoding = buildGeoCoding(gdalProduct, imageWidth, imageHeight);
            if (geoCoding != null) {
                this.product.setSceneGeoCoding(geoCoding);
            }

            Hashtable<?, ?> dict = gdalProduct.GetMetadata_Dict("");
            Enumeration keys = dict.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String)dict.get(key);
                if (!StringUtils.isNullOrEmpty(key) && !StringUtils.isNullOrEmpty(value)) {
                    metadataElement.setAttributeString(key, value);
                }
            }

            Double[] max = new Double[1];
            Double[] min = new Double[1];

            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // Bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band gdalBand = gdalProduct.GetRasterBand(bandIndex + 1);
                BufferTypeDescriptor dataBufferType = bufferTypes.get(gdalBand.getDataType());
                if (dataBufferType == null) {
                    throw new IllegalArgumentException("Unknown raster data type");
                }
                MetadataElement componentElement = new MetadataElement("Component");
                metadataElement.addElement(componentElement);
                componentElement.setAttributeString("data type", gdal.GetDataTypeName(gdalBand.getDataType()));
                componentElement.setAttributeString("color interpretation", gdal.GetColorInterpretationName(gdalBand.GetRasterColorInterpretation()));

                gdalBand.GetMinimum(min);
                if (min[0] != null) {
                    componentElement.setAttributeDouble("minim", min[0]);
                }

                gdalBand.GetMaximum(max);
                if (max[0] != null) {
                    componentElement.setAttributeDouble("maximum", max[0]);
                }

                componentElement.setAttributeInt("precision", dataBufferType.precision);
                componentElement.setAttributeString("signed", Boolean.toString(dataBufferType.signed));

                String bandName = String.format("band_%s", bandIndex + 1);
                Band productBand = new Band(bandName, dataBufferType.bandDataType, imageWidth, imageHeight);

                int tileWidth = gdalBand.GetBlockXSize();
                int tileHeight = gdalBand.GetBlockYSize();
                int levels = gdalBand.GetOverviewCount() + 1;

                GDALMultiLevelSource source = new GDALMultiLevelSource(inputFile, bandIndex, bandCount, imageWidth, imageHeight, tileWidth,
                                                                       tileHeight, levels, dataBufferType.dataBufferType, geoCoding);

                productBand.setSourceImage(new DefaultMultiLevelImage(source));

                this.product.addBand(productBand);

                // add the mask
                org.gdal.gdal.Band maskBand = gdalBand.GetMaskBand();
                if (maskBand != null) {
                    String maskName = "mask_" + String.valueOf(bandIndex + 1);
                    Mask mask = Mask.BandMathsType.create(maskName,
                            null,
                            imageWidth, imageHeight,
                            bandName,
                            Color.white,
                            0.5);
                    this.product.addMask(mask);
                }
            }
            this.product.setModified(false);
            return this.product;
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

    private static GeoCoding buildGeoCoding(Dataset pDataset,int imageWidth, int imageHeight) {
        String wellKnownText = pDataset.GetProjectionRef();
        if (!StringUtils.isNullOrEmpty(wellKnownText)) {
            try {
                CoordinateReferenceSystem crs = CRS.parseWKT(wellKnownText);
                double[] adfGeoTransform = new double[6];
                pDataset.GetGeoTransform(adfGeoTransform);
                double originX = adfGeoTransform[0];
                double originY = adfGeoTransform[3];
                double pixelSizeX = adfGeoTransform[1];
                double pixelSizeY = -adfGeoTransform[5];
                return new CrsGeoCoding(crs, imageWidth, imageHeight, originX, originY, pixelSizeX, pixelSizeY);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }
}
