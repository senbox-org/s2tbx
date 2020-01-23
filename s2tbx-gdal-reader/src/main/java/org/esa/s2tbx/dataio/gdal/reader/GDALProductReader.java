package org.esa.s2tbx.dataio.gdal.reader;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.VirtualFile;
import org.esa.s2tbx.dataio.gdal.drivers.Dataset;
import org.esa.s2tbx.dataio.gdal.drivers.Driver;
import org.esa.s2tbx.dataio.gdal.drivers.GDAL;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConst;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConstConstants;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.StringUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.JAI;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic reader for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GDALProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(GDALProductReader.class.getName());
    private static final Map<Integer, BufferTypeDescriptor> bufferTypes;

    static {
        bufferTypes = new HashMap<>();
        bufferTypes.put(GDALConstConstants.gdtByte(),
                new BufferTypeDescriptor(8, true, ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE));
        bufferTypes.put(GDALConstConstants.gdtInt16(),
                new BufferTypeDescriptor(16, true, ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT));
        bufferTypes.put(GDALConstConstants.gdtUint16(),
                new BufferTypeDescriptor(16, false, ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT));
        bufferTypes.put(GDALConstConstants.gdtInt32(),
                new BufferTypeDescriptor(32, true, ProductData.TYPE_INT32, DataBuffer.TYPE_INT));
        bufferTypes.put(GDALConstConstants.gdtUint32(),
                new BufferTypeDescriptor(32, false, ProductData.TYPE_UINT32, DataBuffer.TYPE_INT));
        bufferTypes.put(GDALConstConstants.gdtFloat32(),
                new BufferTypeDescriptor(32, true, ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT));
        bufferTypes.put(GDALConstConstants.gdtFloat64(),
                new BufferTypeDescriptor(64, true, ProductData.TYPE_FLOAT64, DataBuffer.TYPE_DOUBLE));
    }

    private VirtualFile virtualFile;

    public GDALProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    private static GeoCoding buildGeoCoding(Dataset gdalProduct) {
        String wellKnownText = gdalProduct.getProjectionRef();
        if (!StringUtils.isNullOrEmpty(wellKnownText)) {
            int imageWidth = gdalProduct.getRasterXSize();
            int imageHeight = gdalProduct.getRasterYSize();
            double[] adfGeoTransform = new double[6];
            gdalProduct.getGeoTransform(adfGeoTransform);
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
        Driver hDriver = gdalProduct.getDriver();
        int imageWidth = gdalProduct.getRasterXSize();
        int imageHeight = gdalProduct.getRasterYSize();
        MetadataElement metadataElement = new MetadataElement("Image info");
        metadataElement.setAttributeString("driver", hDriver.getShortName());
        metadataElement.setAttributeInt("width", imageWidth);
        metadataElement.setAttributeInt("height", imageHeight);

        double[] adfGeoTransform = new double[6];
        gdalProduct.getGeoTransform(adfGeoTransform);
        double originX = adfGeoTransform[0];
        double originY = adfGeoTransform[3];
        double pixelSizeX = adfGeoTransform[1];
        double pixelSizeY = (adfGeoTransform[5] > 0) ? adfGeoTransform[5] : -adfGeoTransform[5];

        if (adfGeoTransform[2] == 0.0 && adfGeoTransform[4] == 0.0) {
            metadataElement.setAttributeString("origin", originX + "x" + originY);
            metadataElement.setAttributeString("pixel size", pixelSizeX + "x" + pixelSizeY);
        } else {
            String str1 = adfGeoTransform[0] + "," + adfGeoTransform[1] + "," + adfGeoTransform[3];
            String str2 = adfGeoTransform[3] + "," + adfGeoTransform[4] + "," + adfGeoTransform[5];
            metadataElement.setAttributeString("geo transform", str1 + " " + str2);
        }

        Hashtable<?, ?> dict = gdalProduct.getMetadataDict("");
        Enumeration keys = dict.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = (String) dict.get(key);
            if (!StringUtils.isNullOrEmpty(key) && !StringUtils.isNullOrEmpty(value)) {
                metadataElement.setAttributeString(key, value);
            }
        }
        return metadataElement;
    }

    @Override
    public void close() throws IOException {
        if (this.virtualFile != null) {
            this.virtualFile.close();
        }

        super.close();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Path inputFile = BaseProductReaderPlugIn.convertInputToPath(getInput());

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Loading the product from the file '" + inputFile.toString() + "' using the GDAL plugin reader '" + getReaderPlugIn().getClass().getName() + "'.");
        }

        this.virtualFile = new VirtualFile(inputFile);

        Path localFile = this.virtualFile.getLocalFile();

        Dataset gdalDataset = GDAL.open(localFile.toString(), GDALConst.gaReadonly());
        if (gdalDataset == null) {
            // unknown file format
            throw new NullPointerException("Failed opening a dataset from the file '" + inputFile.toString() + "' to load the product.");
        }

        try {
            int imageWidth = gdalDataset.getRasterXSize();
            int imageHeight = gdalDataset.getRasterYSize();
            String productName = this.virtualFile.getFileName();
            String productType = "GDAL";

            Product product = new Product(productName, productType, imageWidth, imageHeight);
            product.setPreferredTileSize(JAI.getDefaultTileSize());
            product.setFileLocation(inputFile.toFile());

            int bandCount = gdalDataset.getRasterCount();

            MetadataElement metadataElement = buildMetadataElement(gdalDataset);
            MetadataElement metadataRoot = product.getMetadataRoot();
            metadataRoot.addElement(metadataElement);

            GeoCoding geoCoding = buildGeoCoding(gdalDataset);
            if (geoCoding != null) {
                product.setSceneGeoCoding(geoCoding);
            }

            Double[] pass1 = new Double[1];
            int numResolutions = 1;

            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // bands are not 0-base indexed, so we must add 1
                org.esa.s2tbx.dataio.gdal.drivers.Band gdalBand = gdalDataset.getRasterBand(bandIndex + 1);
                int gdalDataType = gdalBand.getDataType();
                BufferTypeDescriptor dataBufferType = bufferTypes.get(gdalDataType);
                if (dataBufferType == null) {
                    throw new IllegalArgumentException("Unknown raster data type " + gdalDataType + ".");
                }
                int tileWidth = gdalBand.getBlockXSize();
                if (tileWidth <= 1) {
                    tileWidth = imageWidth;
                }
                int tileHeight = gdalBand.getBlockYSize();
                if (tileHeight <= 1) {
                    tileHeight = imageHeight;
                }
                int levels = gdalBand.getOverviewCount() + 1;
                if (numResolutions >= levels) {
                    numResolutions = levels;
                }
                if (levels == 1) {
                    logger.fine("Optimizing read by building image pyramids");
                    if (!GDALConst.ceFailure().equals(gdalDataset.buildOverviews("NEAREST", new int[]{2, 4, 8, 16}))) {
                        gdalBand = gdalDataset.getRasterBand(bandIndex + 1);
                    } else {
                        logger.fine("Multiple levels not supported");
                    }
                }
                levels = gdalBand.getOverviewCount() + 1;
                product.setNumResolutionsMax(levels);
                String colorInterpretationName = GDAL.getColorInterpretationName(gdalBand.getRasterColorInterpretation());

                MetadataElement bandComponentElement = new MetadataElement("Component");
                metadataElement.addElement(bandComponentElement);
                bandComponentElement.setAttributeString("data type", GDAL.getDataTypeName(gdalDataType));
                bandComponentElement.setAttributeString("color interpretation", colorInterpretationName);
                bandComponentElement.setAttributeString("block size", tileWidth + "x" + tileHeight);
                bandComponentElement.setAttributeInt("precision", dataBufferType.precision);
                bandComponentElement.setAttributeString("signed", Boolean.toString(dataBufferType.signed));

                String bandName;
                bandName = gdalBand.getDescription();
                if (StringUtils.isNullOrEmpty(bandName)) {
                    bandName = String.format("band_%s", bandIndex + 1);
                } else {
                    bandName = bandName.replace(' ', '_');
                }
                Band productBand = new Band(bandName, dataBufferType.bandDataType, imageWidth, imageHeight);

                if (levels > 1) {
                    StringBuilder str = new StringBuilder();
                    for (int iOverview = 0; iOverview < levels - 1; iOverview++) {
                        if (iOverview != 0) {
                            str.append(", ");
                        }
                        org.esa.s2tbx.dataio.gdal.drivers.Band hOverview = gdalBand.getOverview(iOverview);
                        str.append(hOverview.getXSize())
                                .append("x")
                                .append(hOverview.getYSize());
                    }
                    bandComponentElement.setAttributeInt("overview count", levels - 1);
                    if (str.length() > 0) {
                        bandComponentElement.setAttributeString("overviews", str.toString());
                    }
                }

                gdalBand.getOffset(pass1);
                if (pass1[0] != null && pass1[0] != 0) {
                    bandComponentElement.setAttributeDouble("offset", pass1[0]);
                    productBand.setScalingOffset(pass1[0]);
                }

                gdalBand.getScale(pass1);
                if (pass1[0] != null && pass1[0] != 1) {
                    bandComponentElement.setAttributeDouble("scale", pass1[0]);
                    productBand.setScalingFactor(pass1[0]);
                }

                String unitType = gdalBand.getUnitType();
                if (unitType != null && unitType.length() > 0) {
                    bandComponentElement.setAttributeString("unit type", unitType);
                    productBand.setUnit(unitType);
                }

                Double[] noData = new Double[1];
                gdalBand.getNoDataValue(noData);
                if (noData[0] != null) {
                    productBand.setNoDataValue(noData[0]);
                    productBand.setNoDataValueUsed(true);
                }
                GDALMultiLevelSource source = new GDALMultiLevelSource(localFile, bandIndex, bandCount, imageWidth, imageHeight, tileWidth,
                        tileHeight, levels, dataBufferType.dataBufferType, geoCoding);

                productBand.setSourceImage(new DefaultMultiLevelImage(source));

                product.addBand(productBand);

                // add the mask
                org.esa.s2tbx.dataio.gdal.drivers.Band maskBand = gdalBand.getMaskBand();
                if (maskBand != null) {
                    String maskName = null;
                    final int maskFlags = gdalBand.getMaskFlags();
                    if ((maskFlags & (GDALConstConstants.gmfNodata() | GDALConstConstants.gmfPerDataset())) != 0) {
                        maskName = "nodata_";
                    } else if ((maskFlags & (GDALConstConstants.gmfPerDataset() | GDALConstConstants.gmfAlpha())) != 0) {
                        maskName = "alpha_";
                    } else if ((maskFlags & (GDALConstConstants.gmfNodata() | GDALConstConstants.gmfPerDataset() | GDALConstConstants.gmfAlpha() | GDALConstConstants.gmfAllValid())) != 0) {
                        maskName = "mask_";
                    }
                    if (maskName != null) {
                        Mask mask = Mask.BandMathsType.create(maskName + bandName, null, imageWidth, imageHeight, "'" + bandName + "'", Color.white, 0.5);
                        product.addMask(mask);
                    }
                }
            }
            product.setNumResolutionsMax(numResolutions);
            product.setModified(false);
            return product;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("Error while reading file '%s'", inputFile), ex);
            throw ex;
        } finally {
            gdalDataset.delete();
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // do nothing
    }

    private static class BufferTypeDescriptor {
        final int precision;
        final boolean signed;
        final int bandDataType;
        final int dataBufferType;

        BufferTypeDescriptor(int precision, boolean signed, int bandDataType, int dataBufferType) {
            this.precision = precision;
            this.signed = signed;
            this.bandDataType = bandDataType;
            this.dataBufferType = dataBufferType;
        }
    }
}
