package org.esa.s2tbx.gdal.reader;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.snap.engine_utilities.file.AbstractFile;
import org.esa.s2tbx.commons.VirtualFile;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.StringUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.JAI;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Generic reader for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GDALProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(GDALProductReader.class.getName());

    private static final Map<Integer, BufferTypeDescriptor> BUFFER_TYPES;
    static {
        BUFFER_TYPES = new HashMap<>();
        BUFFER_TYPES.put(gdalconstConstants.GDT_Byte, new BufferTypeDescriptor(8, true, ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE));
        BUFFER_TYPES.put(gdalconstConstants.GDT_Int16, new BufferTypeDescriptor(16, true, ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT));
        BUFFER_TYPES.put(gdalconstConstants.GDT_UInt16, new BufferTypeDescriptor(16, false, ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT));
        BUFFER_TYPES.put(gdalconstConstants.GDT_Int32, new BufferTypeDescriptor(32, true, ProductData.TYPE_INT32, DataBuffer.TYPE_INT));
        BUFFER_TYPES.put(gdalconstConstants.GDT_UInt32, new BufferTypeDescriptor(32, false, ProductData.TYPE_UINT32, DataBuffer.TYPE_INT));
        BUFFER_TYPES.put(gdalconstConstants.GDT_Float32, new BufferTypeDescriptor(32, true, ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT));
        BUFFER_TYPES.put(gdalconstConstants.GDT_Float64, new BufferTypeDescriptor(64, true, ProductData.TYPE_FLOAT64, DataBuffer.TYPE_DOUBLE));
    }

    private VirtualFile virtualFile;

    public GDALProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.virtualFile = new VirtualFile(productPath);
            Product product = readProduct(this.virtualFile.getLocalFile(), null);
            product.setFileLocation(productPath.toFile());

            success = true;

            return product;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (!success) {
                closeResources();
            }
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {
        // do nothing
    }

    public Product readProduct(Path localFile, Rectangle inputProductBounds) throws FactoryException, TransformException {
        if (localFile == null) {
            throw new NullPointerException("The local file is null.");
        }
        if (!AbstractFile.isLocalPath(localFile)) {
            throw new IllegalArgumentException("The file '" + localFile.toString() + "' is not a local file.");
        }

        org.gdal.gdal.Dataset gdalDataset = openGDALDataset(localFile);
        try {
            int defaultProductWidth = gdalDataset.getRasterXSize();
            int defaultProductHeight = gdalDataset.getRasterYSize();

            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = inputProductBounds;
            if (productBounds == null) {
                GeoCoding productDefaultGeoCoding = null;
                if(subsetDef != null){
                    productDefaultGeoCoding = buildGeoCoding(gdalDataset, null);
                }
                productBounds = ImageUtils.computeProductBounds(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, subsetDef);
            }
            if ((productBounds.x + productBounds.width) > defaultProductWidth) {
                throw new IllegalArgumentException("The coordinates are out of bounds: productBounds.x="+productBounds.x+", productBounds.width="+productBounds.width+", default product width=" + defaultProductWidth);
            }
            if ((productBounds.y + productBounds.height) > defaultProductHeight) {
                throw new IllegalArgumentException("The coordinates are out of bounds: productBounds.y="+productBounds.y+", productBounds.height="+productBounds.height+", default product height=" + defaultProductHeight);
            }

            Product product = new Product(localFile.getFileName().toString(), "GDAL", productBounds.width, productBounds.height, this);
            product.setPreferredTileSize(JAI.getDefaultTileSize());

            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                MetadataElement metadataElement = buildMetadataElement(gdalDataset);
                product.getMetadataRoot().addElement(metadataElement);
            }

            GeoCoding geoCoding = buildGeoCoding(gdalDataset, productBounds);
            if (geoCoding != null) {
                product.setSceneGeoCoding(geoCoding);
            }

            Double[] pass1 = new Double[1];
            int numResolutions = 1;

            int bandCount = gdalDataset.getRasterCount();
            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band gdalBand = gdalDataset.GetRasterBand(bandIndex + 1);
                String bandName = computeBandName(gdalBand, bandIndex);

                if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                    int gdalDataType = gdalBand.getDataType();
                    BufferTypeDescriptor dataBufferType = BUFFER_TYPES.get(gdalDataType);
                    if (dataBufferType == null) {
                        throw new IllegalArgumentException("Unknown raster data type " + gdalDataType + ".");
                    }

                    Dimension tileSize = computeBandTileSize(gdalBand, productBounds.width, productBounds.height);

                    int levelCount = gdalBand.GetOverviewCount() + 1;
                    if (numResolutions >= levelCount) {
                        numResolutions = levelCount;
                    }
                    if (levelCount == 1) {
                        logger.fine("Optimizing read by building image pyramids");
                        if (gdalconst.CE_Failure != gdalDataset.BuildOverviews("NEAREST", new int[]{2, 4, 8, 16})) {
                            gdalBand = gdalDataset.GetRasterBand(bandIndex + 1);
                        } else {
                            logger.fine("Multiple levels not supported");
                        }
                    }
                    levelCount = gdalBand.GetOverviewCount() + 1;
                    product.setNumResolutionsMax(levelCount);

                    String colorInterpretationName = gdal.GetColorInterpretationName(gdalBand.GetRasterColorInterpretation());
                    MetadataElement bandMetadataElement = new MetadataElement("Component");
                    bandMetadataElement.setAttributeString("data type", gdal.GetDataTypeName(gdalDataType));
                    bandMetadataElement.setAttributeString("color interpretation", colorInterpretationName);
                    bandMetadataElement.setAttributeString("block size", tileSize.width + "x" + tileSize.height);
                    bandMetadataElement.setAttributeInt("precision", dataBufferType.precision);
                    bandMetadataElement.setAttributeString("signed", Boolean.toString(dataBufferType.signed));
                    if (levelCount > 1) {
                        StringBuilder str = new StringBuilder();
                        for (int iOverview = 0; iOverview < levelCount - 1; iOverview++) {
                            if (iOverview != 0) {
                                str.append(", ");
                            }
                            org.gdal.gdal.Band hOverview = gdalBand.GetOverview(iOverview);
                            str.append(hOverview.getXSize())
                                    .append("x")
                                    .append(hOverview.getYSize());
                        }
                        bandMetadataElement.setAttributeInt("overview count", levelCount - 1);
                        if (str.length() > 0) {
                            bandMetadataElement.setAttributeString("overviews", str.toString());
                        }
                    }

                    Band productBand = new Band(bandName, dataBufferType.bandDataType, productBounds.width, productBounds.height);
                    productBand.setGeoCoding(geoCoding);

                    gdalBand.GetOffset(pass1);
                    if (pass1[0] != null && pass1[0] != 0) {
                        bandMetadataElement.setAttributeDouble("offset", pass1[0]);
                        productBand.setScalingOffset(pass1[0]);
                    }

                    gdalBand.GetScale(pass1);
                    if (pass1[0] != null && pass1[0] != 1) {
                        bandMetadataElement.setAttributeDouble("scale", pass1[0]);
                        productBand.setScalingFactor(pass1[0]);
                    }

                    String unitType = gdalBand.GetUnitType();
                    if (unitType != null && unitType.length() > 0) {
                        bandMetadataElement.setAttributeString("unit type", unitType);
                        productBand.setUnit(unitType);
                    }

                    Double[] noData = new Double[1];
                    gdalBand.GetNoDataValue(noData);
                    if (noData[0] != null) {
                        productBand.setNoDataValue(noData[0]);
                        productBand.setNoDataValueUsed(true);
                    }

                    GDALMultiLevelSource multiLevelSource = new GDALMultiLevelSource(localFile, dataBufferType.dataBufferType, productBounds, tileSize, bandIndex, levelCount, geoCoding);
                    productBand.setSourceImage(new DefaultMultiLevelImage(multiLevelSource));

                    if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                        product.getMetadataRoot().addElement(bandMetadataElement);
                    }

                    product.addBand(productBand);
                }

                // add the mask
                String maskName = computeMaskName(gdalBand, bandName);
                if (maskName != null && (subsetDef == null || subsetDef.isNodeAccepted(maskName))) {
                    Mask mask = Mask.BandMathsType.create(maskName, null, productBounds.width, productBounds.height, "'" + bandName + "'", Color.white, 0.5);
                    product.addMask(mask);
                }
            }
            product.setNumResolutionsMax(numResolutions);
            return product;
        } finally {
            gdalDataset.delete();
        }
    }

    private void closeResources() {
        if (this.virtualFile != null) {
            this.virtualFile.close();
            this.virtualFile = null;
        }
    }

    public static org.gdal.gdal.Dataset openGDALDataset(Path localProductPath) {
        org.gdal.gdal.Dataset gdalDataset = gdal.Open(localProductPath.toString(), gdalconst.GA_ReadOnly);
        if (gdalDataset == null) {
            // unknown file format
            throw new NullPointerException("Failed opening a dataset from the file '" + localProductPath.toString() + "' to load the product.");
        }
        return gdalDataset;
    }

    public static String computeMaskName(org.gdal.gdal.Band gdalBand, String bandName) {
        org.gdal.gdal.Band maskBand = gdalBand.GetMaskBand();
        if (maskBand != null) {
            int maskFlags = gdalBand.GetMaskFlags();
            String maskPrefix = null;
            if ((maskFlags & (gdalconstConstants.GMF_NODATA | gdalconstConstants.GMF_PER_DATASET)) != 0) {
                maskPrefix = "nodata_";
            } else if ((maskFlags & (gdalconstConstants.GMF_PER_DATASET | gdalconstConstants.GMF_ALPHA)) != 0) {
                maskPrefix = "alpha_";
            } else if ((maskFlags & (gdalconstConstants.GMF_NODATA | gdalconstConstants.GMF_PER_DATASET | gdalconstConstants.GMF_ALPHA | gdalconstConstants.GMF_ALL_VALID)) != 0) {
                maskPrefix = "mask_";
            }
            if (maskPrefix != null) {
                return maskPrefix + bandName;
            }
        }
        return null;
    }

    private static Dimension computeBandTileSize(org.gdal.gdal.Band gdalBand, int productWidth, int productHeight) {
        Dimension tileSize = new Dimension(gdalBand.GetBlockXSize(), gdalBand.GetBlockYSize());
        if (tileSize.width <= 1 || tileSize.width > productWidth) {
            tileSize.width = productWidth;
        }
        if (tileSize.height <= 1 || tileSize.height >productHeight) {
            tileSize.height = productHeight;
        }
        return tileSize;
    }

    public static String computeBandName(org.gdal.gdal.Band gdalBand, int bandIndex) {
        String bandName = gdalBand.GetDescription();
        if (StringUtils.isNullOrEmpty(bandName)) {
            bandName = String.format("band_%s", bandIndex + 1);
        } else {
            bandName = bandName.replace(' ', '_');
        }
        return bandName;
    }

    public static CrsGeoCoding buildGeoCoding(Dataset gdalDataset, Rectangle subsetBounds) throws FactoryException, TransformException {
        String wellKnownText = gdalDataset.GetProjectionRef();
        if (!StringUtils.isNullOrEmpty(wellKnownText)) {
            int imageWidth = gdalDataset.getRasterXSize();
            int imageHeight = gdalDataset.getRasterYSize();
            double[] adfGeoTransform = new double[6];
            gdalDataset.GetGeoTransform(adfGeoTransform);
            double originX = adfGeoTransform[0];
            double originY = adfGeoTransform[3];
            double resolutionX = adfGeoTransform[1];
            double resolutionY = (adfGeoTransform[5] > 0) ? adfGeoTransform[5] : -adfGeoTransform[5];
            CoordinateReferenceSystem mapCRS = CRS.parseWKT(wellKnownText);
            return ImageUtils.buildCrsGeoCoding(originX, originY, resolutionX, resolutionY, imageWidth, imageHeight, mapCRS, subsetBounds, 0.5d, 0.5d);
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

    private static class BufferTypeDescriptor {
        public int precision;
        public boolean signed;
        public int bandDataType;
        public int dataBufferType;

        BufferTypeDescriptor(int precision, boolean signed, int bandDataType, int dataBufferType) {
            this.precision = precision;
            this.signed = signed;
            this.bandDataType = bandDataType;
            this.dataBufferType = dataBufferType;
        }
    }
}
