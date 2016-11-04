package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.gdal.internal.BufferTypeDescriptor;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.util.StringUtils;
import org.gdal.gdal.*;
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

            this.product = new Product(productName, productType, imageWidth, imageHeight);
            this.product.setPreferredTileSize(JAI.getDefaultTileSize());
            this.product.setFileLocation(inputFile.toFile());

            int bandCount = gdalProduct.getRasterCount();

            MetadataElement metadataElement = buildMetadataElement(gdalProduct);
            MetadataElement metadataRoot = this.product.getMetadataRoot();
            metadataRoot.addElement(metadataElement);

            GeoCoding geoCoding = buildGeoCoding(gdalProduct);
            if (geoCoding != null) {
                this.product.setSceneGeoCoding(geoCoding);
            }

            Double[] pass1 = new Double[1];

            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // Bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band gdalBand = gdalProduct.GetRasterBand(bandIndex + 1);
                int gdalDataType = gdalBand.getDataType();
                BufferTypeDescriptor dataBufferType = bufferTypes.get(gdalDataType);
                if (dataBufferType == null) {
                    throw new IllegalArgumentException("Unknown raster data type " + gdalDataType + ".");
                }
                int tileWidth = gdalBand.GetBlockXSize();
                int tileHeight = gdalBand.GetBlockYSize();
                int levels = gdalBand.GetOverviewCount() + 1;
                String colorInterpretationName = gdal.GetColorInterpretationName(gdalBand.GetRasterColorInterpretation());

                MetadataElement componentElement = new MetadataElement("Component");
                metadataElement.addElement(componentElement);
                componentElement.setAttributeString("data type", gdal.GetDataTypeName(gdalDataType));
                componentElement.setAttributeString("color interpretation", colorInterpretationName);
                componentElement.setAttributeString("block size", tileWidth + "x" + tileHeight);
                componentElement.setAttributeInt("precision", dataBufferType.precision);
                componentElement.setAttributeString("signed", Boolean.toString(dataBufferType.signed));

                StringBuilder str = new StringBuilder();
                for (int iOverview = 0; iOverview < gdalBand.GetOverviewCount(); iOverview++) {
                    if (iOverview != 0) {
                        str.append(", ");
                    }
                    org.gdal.gdal.Band hOverview = gdalBand.GetOverview(iOverview);
                    str.append(hOverview.getXSize())
                       .append("x")
                       .append(hOverview.getYSize());
                }
                componentElement.setAttributeInt("overview count", gdalBand.GetOverviewCount());
                if (str.length() > 0) {
                    componentElement.setAttributeString("overviews", str.toString());
                }

                gdalBand.GetOffset(pass1);
                if (pass1[0] != null && pass1[0].doubleValue() != 0) {
                    componentElement.setAttributeDouble("offset", pass1[0].doubleValue());
                }

                gdalBand.GetScale(pass1);
                if (pass1[0] != null && pass1[0].doubleValue() != 1) {
                    componentElement.setAttributeDouble("scale", pass1[0].doubleValue());
                }

                if (gdalBand.GetUnitType() != null && gdalBand.GetUnitType().length() > 0) {
                    componentElement.setAttributeString("unit type", gdalBand.GetUnitType());
                }

                String bandName = colorInterpretationName;
                if (StringUtils.isNullOrEmpty(bandName)) {
                    bandName = String.format("band_%s", bandIndex + 1);
                } else if ("Undefined".equalsIgnoreCase(bandName)) {
                    bandName = bandName + "_" +Integer.toString(bandIndex + 1);
                }
                Band productBand = new Band(bandName, dataBufferType.bandDataType, imageWidth, imageHeight);

                GDALMultiLevelSource source = new GDALMultiLevelSource(inputFile, bandIndex, bandCount, imageWidth, imageHeight, tileWidth,
                                                                       tileHeight, levels, dataBufferType.dataBufferType, geoCoding);

                productBand.setSourceImage(new DefaultMultiLevelImage(source));

                this.product.addBand(productBand);

                // add the mask
                org.gdal.gdal.Band maskBand = gdalBand.GetMaskBand();
                if (maskBand != null) {
                    String maskName = "mask_" + String.valueOf(bandIndex + 1);
                    Mask mask = Mask.BandMathsType.create(maskName, null, imageWidth, imageHeight, bandName, Color.white, 0.5);
                    this.product.addMask(mask);
                }
            }
            this.product.setModified(false);
            return this.product;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            IOException exception = null;
            if (ex instanceof IOException) {
                exception = (IOException)ex;
            } else {
                String msg = "Error while reading file '" + inputFile.toString() + "'.";
                exception = new IOException(msg, ex);
            }
            throw exception;
        } finally {
            gdalProduct.delete();
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

    private static GeoCoding buildGeoCoding(Dataset gdalProduct) {
        String wellKnownText = gdalProduct.GetProjectionRef();
        if (!StringUtils.isNullOrEmpty(wellKnownText)) {
            int imageWidth = gdalProduct.getRasterXSize();
            int imageHeight = gdalProduct.getRasterYSize();
            double[] adfGeoTransform = new double[6];
            gdalProduct.GetGeoTransform(adfGeoTransform);
            double originX = adfGeoTransform[0];
            double originY = adfGeoTransform[3];
            double pixelSizeX = adfGeoTransform[1];
            double pixelSizeY = (adfGeoTransform[5] > 0) ? adfGeoTransform[5] : -adfGeoTransform[5];
            try {
                CoordinateReferenceSystem crs = CRS.parseWKT(wellKnownText);
                return new CrsGeoCoding(crs, imageWidth, imageHeight, originX, originY, pixelSizeX, pixelSizeY);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }

    private static MetadataElement buildMetadataElement(Dataset gdalProduct) {
        Driver hDriver = gdalProduct.GetDriver();
        int imageWidth = gdalProduct.getRasterXSize();
        int imageHeight = gdalProduct.getRasterYSize();
        MetadataElement metadataElement = new MetadataElement("Image info");
        metadataElement.setAttributeString("driver", hDriver.getShortName());
        metadataElement.setAttributeInt("width", imageWidth);
        metadataElement.setAttributeInt("height", imageHeight);

        double[] adfGeoTransform = new double[6];
        gdalProduct.GetGeoTransform(adfGeoTransform);
        double originX = adfGeoTransform[0];
        double originY = adfGeoTransform[3];
        double pixelSizeX = adfGeoTransform[1];
        double pixelSizeY = (adfGeoTransform[5] > 0) ? adfGeoTransform[5] : -adfGeoTransform[5];

        if (adfGeoTransform[2] == 0.0 && adfGeoTransform[4] == 0.0) {
            metadataElement.setAttributeString("origin", originX + "x" + originY);
            metadataElement.setAttributeString("pixel size", pixelSizeX + "x" + pixelSizeY);
        } else {
            String str1 = adfGeoTransform[0] + "," + adfGeoTransform[1]+ "," + adfGeoTransform[3];
            String str2 = adfGeoTransform[3] + "," + adfGeoTransform[4]+ "," + adfGeoTransform[5];
            metadataElement.setAttributeString("geo transform", str1 + " " + str2);
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
        return metadataElement;
    }
}
