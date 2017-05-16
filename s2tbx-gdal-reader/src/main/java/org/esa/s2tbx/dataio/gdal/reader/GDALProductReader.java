package org.esa.s2tbx.dataio.gdal.reader;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
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
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.geotools.referencing.AbstractIdentifiedObject;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.cs.DefaultEllipsoidalCS;
import org.geotools.referencing.operation.DefaultOperationMethod;
import org.geotools.referencing.operation.DefiningConversion;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;

import javax.media.jai.JAI;
import java.awt.Color;
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
 * Generic reader for products using the GDAL library.
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

    public GDALProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Object input = getInput();

        logger.info("Loading the product using the GDAL plugin reader '"+getReaderPlugIn().getClass().getName()+"' from file '" + input.toString() + "'.");

        Path inputFile = getFileInput(input);
        if (inputFile == null) {
            throw new IOException("The file '"+ input + "' to load the product is invalid.");
        }

        Dataset gdalDataset = gdal.Open(inputFile.toString(), gdalconst.GA_ReadOnly);
        if (gdalDataset == null) {
            // unknown file format
            throw new IOException("The file '"+ inputFile.toString()+"' to load the product can not be opened.");
        }

        try {
            int imageWidth = gdalDataset.getRasterXSize();
            int imageHeight = gdalDataset.getRasterYSize();
            String productName = inputFile.getFileName().toString();
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

            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band gdalBand = gdalDataset.GetRasterBand(bandIndex + 1);
                int gdalDataType = gdalBand.getDataType();
                BufferTypeDescriptor dataBufferType = bufferTypes.get(gdalDataType);
                if (dataBufferType == null) {
                    throw new IllegalArgumentException("Unknown raster data type " + gdalDataType + ".");
                }
                int tileWidth = gdalBand.GetBlockXSize();
                if (tileWidth <= 1) {
                    tileWidth = imageWidth;
                }
                int tileHeight = gdalBand.GetBlockYSize();
                if (tileHeight <= 1) {
                    tileHeight = imageHeight;
                }
                int levels = gdalBand.GetOverviewCount() + 1;
                String colorInterpretationName = gdal.GetColorInterpretationName(gdalBand.GetRasterColorInterpretation());

                MetadataElement bandComponentElement = new MetadataElement("Component");
                metadataElement.addElement(bandComponentElement);
                bandComponentElement.setAttributeString("data type", gdal.GetDataTypeName(gdalDataType));
                bandComponentElement.setAttributeString("color interpretation", colorInterpretationName);
                bandComponentElement.setAttributeString("block size", tileWidth + "x" + tileHeight);
                bandComponentElement.setAttributeInt("precision", dataBufferType.precision);
                bandComponentElement.setAttributeString("signed", Boolean.toString(dataBufferType.signed));

                int overviewCount = gdalBand.GetOverviewCount();
                if (overviewCount > 0) {
                    StringBuilder str = new StringBuilder();
                    for (int iOverview = 0; iOverview < overviewCount; iOverview++) {
                        if (iOverview != 0) {
                            str.append(", ");
                        }
                        org.gdal.gdal.Band hOverview = gdalBand.GetOverview(iOverview);
                        str.append(hOverview.getXSize())
                                .append("x")
                                .append(hOverview.getYSize());
                    }
                    bandComponentElement.setAttributeInt("overview count", overviewCount);
                    if (str.length() > 0) {
                        bandComponentElement.setAttributeString("overviews", str.toString());
                    }
                }

                gdalBand.GetOffset(pass1);
                if (pass1[0] != null && pass1[0] != 0) {
                    bandComponentElement.setAttributeDouble("offset", pass1[0]);
                }

                gdalBand.GetScale(pass1);
                if (pass1[0] != null && pass1[0] != 1) {
                    bandComponentElement.setAttributeDouble("scale", pass1[0]);
                }

                if (gdalBand.GetUnitType() != null && gdalBand.GetUnitType().length() > 0) {
                    bandComponentElement.setAttributeString("unit type", gdalBand.GetUnitType());
                }

                String bandName;
                if (StringUtils.isNullOrEmpty(bandName = gdalBand.GetDescription())) {
                    bandName = String.format("band_%s", bandIndex + 1);
                } else {
                    bandName = bandName.replace(' ', '_');
                }
                Band productBand = new Band(bandName, dataBufferType.bandDataType, imageWidth, imageHeight);
                Double[] noData = new Double[1];
                gdalBand.GetNoDataValue(noData);
                if (noData[0] != null) {
                    productBand.setNoDataValue(noData[0]);
                    productBand.setNoDataValueUsed(true);
                }
                GDALMultiLevelSource source = new GDALMultiLevelSource(inputFile, bandIndex, bandCount,
                                                                       imageWidth, imageHeight, tileWidth,
                                                                       tileHeight, levels,
                                                                       dataBufferType.dataBufferType, geoCoding);

                productBand.setSourceImage(new DefaultMultiLevelImage(source));

                product.addBand(productBand);

                // add the mask
                org.gdal.gdal.Band maskBand = gdalBand.GetMaskBand();
                if (maskBand != null) {
                    String maskName = null;
                    final int maskFlags = gdalBand.GetMaskFlags();
                    if ((maskFlags & (gdalconstConstants.GMF_NODATA | gdalconstConstants.GMF_PER_DATASET)) != 0) {
                        maskName = "nodata_";
                    } else if ((maskFlags & (gdalconstConstants.GMF_PER_DATASET | gdalconstConstants.GMF_ALPHA)) != 0) {
                        maskName = "alpha_";
                    } else if ((maskFlags & (gdalconstConstants.GMF_NODATA | gdalconstConstants.GMF_PER_DATASET |
                            gdalconstConstants.GMF_ALPHA | gdalconstConstants.GMF_ALL_VALID)) != 0) {
                        maskName = "mask_";
                    }
                    if (maskName != null) {
                        Mask mask = Mask.BandMathsType.create(maskName + bandName, null, imageWidth, imageHeight,
                                                              bandName, Color.white, 0.5);
                        product.addMask(mask);
                    }
                }
            }
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

    private static CoordinateReferenceSystem getCRS(MathTransform transform, GeodeticDatum datum) {
        try {
            final CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            final DefaultOperationMethod operationMethod = new DefaultOperationMethod(transform);

            final Conversion conversion = new DefiningConversion(AbstractIdentifiedObject.getProperties(operationMethod),
                                                                 operationMethod, transform);

            final HashMap<String, Object> baseCrsProperties = new HashMap<>();
            baseCrsProperties.put("name", datum.getName().getCode());
            GeographicCRS baseCrs = crsFactory.createGeographicCRS(baseCrsProperties,
                                                                   datum,
                                                                   DefaultEllipsoidalCS.GEODETIC_2D);

            final HashMap<String, Object> projProperties = new HashMap<>();
            projProperties.put("name", conversion.getName().getCode() + " / " + datum.getName().getCode());
            return crsFactory.createProjectedCRS(projProperties, baseCrs, conversion, DefaultCartesianCS.PROJECTED);
        } catch (FactoryException ex) {
            logger.warning(ex.getMessage());
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
