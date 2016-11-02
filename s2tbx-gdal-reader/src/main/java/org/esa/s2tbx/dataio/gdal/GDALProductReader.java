package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.geotools.coverage.processing.operation.Exp;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.JAI;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic GDAL Reader.
 *
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
        Object input = getInput();

        logger.info("Loading the product using the GDAL reader from file '" + input.toString() + "'.");

        Path inputFile = getFileInput(input);
        if (inputFile == null) {
            throw new IOException("The file '"+ input + "' to load the product is invalid.");
        }

        Dataset poDataset = gdal.Open(inputFile.toString(), gdalconst.GA_ReadOnly);
        if (poDataset == null) {
            // unknown file format
            throw new IOException("The file '"+ inputFile.toString()+"' to load the product can not be opened.");
        }

        try {
            int imageWidth = poDataset.getRasterXSize();
            int imageHeight = poDataset.getRasterYSize();
            String productName = inputFile.getFileName().toString();
            String productType = "GDAL";
            Dimension preferredTileSize = JAI.getDefaultTileSize();

            this.product = new Product(productName, productType, imageWidth, imageHeight);
            this.product.setPreferredTileSize(preferredTileSize);
            this.product.setFileLocation(inputFile.toFile());

            int bandCount = poDataset.getRasterCount();

            MetadataElement metadataElement = new MetadataElement("Image info");
            metadataElement.setAttributeInt("width", imageWidth);
            metadataElement.setAttributeInt("height", imageHeight);
            metadataElement.setAttributeInt("numComponents", bandCount);

            MetadataElement metadataRoot = this.product.getMetadataRoot();
            metadataRoot.addElement(metadataElement);

            GeoCoding geoCoding = buildGeoCoding(poDataset, imageWidth, imageHeight);
            if (geoCoding != null) {
                this.product.setSceneGeoCoding(geoCoding);
            }

            Hashtable<?, ?> dict = poDataset.GetMetadata_Dict("");
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
                org.gdal.gdal.Band poBand = poDataset.GetRasterBand(bandIndex + 1);
                int gdalDataType = poBand.getDataType();
                int bandDataType = 0;
                int dataBufferType = 0;
                int precision = 0;
                boolean signed = false;

                MetadataElement componentElement = new MetadataElement("Component");
                metadataElement.addElement(componentElement);
                componentElement.setAttributeString("data type", gdal.GetDataTypeName(poBand.getDataType()));
                componentElement.setAttributeString("color interpretation", gdal.GetColorInterpretationName(poBand.GetRasterColorInterpretation()));

                poBand.GetMinimum(min);
                if (min[0] != null) {
                    componentElement.setAttributeDouble("minim", min[0].doubleValue());
                }

                poBand.GetMaximum(max);
                if (max[0] != null) {
                    componentElement.setAttributeDouble("maximum", max[0].doubleValue());
                }

                if (gdalDataType == gdalconstConstants.GDT_Byte) {
                    precision = 8;
                    signed = true;
                    bandDataType = ProductData.TYPE_UINT8;
                    dataBufferType = DataBuffer.TYPE_BYTE;
                } else if (gdalDataType == gdalconstConstants.GDT_Int16) {
                    precision = 16;
                    signed = true;
                    bandDataType = ProductData.TYPE_INT16;
                    dataBufferType = DataBuffer.TYPE_SHORT;
                } else if (gdalDataType == gdalconstConstants.GDT_UInt16) {
                    precision = 16;
                    signed = false;
                    bandDataType = ProductData.TYPE_UINT16;
                    dataBufferType = DataBuffer.TYPE_USHORT;
                } else if (gdalDataType == gdalconstConstants.GDT_Int32) {
                    precision = 32;
                    signed = true;
                    bandDataType = ProductData.TYPE_INT32;
                    dataBufferType = DataBuffer.TYPE_INT;
                } else if (gdalDataType == gdalconstConstants.GDT_Float32) {
                    precision = 32;
                    signed = true;
                    bandDataType = ProductData.TYPE_FLOAT32;
                    dataBufferType = DataBuffer.TYPE_FLOAT;
                } else {
                    throw new IllegalArgumentException("Unknown GDAL data type " + gdalDataType + ".");
                }
                componentElement.setAttributeInt("precision", precision);
                componentElement.setAttributeString("signed", Boolean.toString(signed));

                String bandName = "band_" + String.valueOf(bandIndex + 1);
                Band virtualBand = new Band(bandName, bandDataType, imageWidth, imageHeight);

                int tileWidth = preferredTileSize.width;
                int tileHeight = preferredTileSize.height;
                int levels = 1;//DefaultMultiLevelModel.getLevelCount(imageWidth, imageHeight);

                GDALMultiLevelSource source = new GDALMultiLevelSource(inputFile, bandIndex, bandCount, imageWidth, imageHeight, tileWidth,
                                                                       tileHeight, levels, dataBufferType, geoCoding);

                virtualBand.setSourceImage(new DefaultMultiLevelImage(source));

                this.product.addBand(virtualBand);

                // add the mask
                org.gdal.gdal.Band maskBand = poBand.GetMaskBand();
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

    private static GeoCoding buildGeoCoding(Dataset poDataset,int imageWidth, int imageHeight) {
        String wellKnownText = poDataset.GetProjectionRef();
        if (!StringUtils.isNullOrEmpty(wellKnownText)) {
            try {
                CoordinateReferenceSystem crs = CRS.parseWKT(wellKnownText);
                double[] adfGeoTransform = new double[6];
                poDataset.GetGeoTransform(adfGeoTransform);
                double originX = adfGeoTransform[0];
                double originY = adfGeoTransform[3];
                double pixelSizeX = adfGeoTransform[1];
                double pixelSizeY = adfGeoTransform[5];
                return new CrsGeoCoding(crs, imageWidth, imageHeight, originX, originY, pixelSizeX, pixelSizeY);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }
}
