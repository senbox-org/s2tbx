package org.esa.beam.dataio.rapideye.metadata;

import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.rapideye.RapidEyeConstants;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Specialized <code>XmlMetadata</code> for RapidEye.
 *
 * @author  Cosmin Cara
 * @see org.esa.beam.dataio.metadata.XmlMetadata
 */
public class RapidEyeMetadata extends XmlMetadata {

    public static final String TAG_MOSAIC_DECOMPOSITION = "mosaicDecomposition";
    public static final String TAG_MOSAIC_TILE = "mosaicTile";
    private float[] scaleFactors;

    public RapidEyeMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() { return name; }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            if (rootElement != null) {
                MetadataElement currentElement;
                if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                        ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                        ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                        ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null)) {
                    String value = currentElement.getAttributeString(RapidEyeConstants.TAG_NUM_BANDS);
                    try {
                        numBands = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        logger.severe(String.format("Incorrect band number in metadata. Found %s, expected numeric", value == null ? "NULL" : value));
                    }
                }
            }
        }
        return numBands;
    }

    @Override
    public String getProductName() {
        if (rootElement == null) {
            return null;
        }
        String name = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_META_DATA_PROPERTY)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_META_DATA)) != null)) {
            name = currentElement.getAttributeString(RapidEyeConstants.TAG_IDENTIFIER, null);
            rootElement.setDescription(name);
        }
        return name;
    }

    @Override
    public String getFormatName() {
        String formatName = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null)) {
                formatName = currentElement.getAttributeString(RapidEyeConstants.TAG_PRODUCT_FORMAT);
            }
        }
        return formatName;
    }

    @Override
    public String getMetadataProfile() {
        String profile = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_META_DATA_PROPERTY)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_META_DATA)) != null)) {
                profile = currentElement.getAttributeString(RapidEyeConstants.TAG_PRODUCT_TYPE);
            }
        }
        return profile;
    }

    @Override
    public int getRasterWidth() {
        if (width == 0 && rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null)) {
                String value = currentElement.getAttributeString(RapidEyeConstants.TAG_NUM_COLUMNS);
                try {
                    width = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    logger.severe(String.format("Incorrect product width in metadata. Found %s, expected numeric", value == null ? "NULL" : value));
                }
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0 && rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null)) {
                String value = currentElement.getAttributeString(RapidEyeConstants.TAG_NUM_ROWS);
                try {
                    height = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    logger.severe(String.format("Incorrect product height in metadata. Found %s, expected numeric", value == null ? "NULL" : value));
                }
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        return getRasterFileNames(true);
    }

    public String getBrowseFileName() {
        String fileName = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_BROWSE)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_BROWSE_INFORMATION)) != null)) {
                fileName = currentElement.getAttributeString(RapidEyeConstants.TAG_FILE_NAME, null);
                if (fileName == null) {
                    logger.warning("Browse file not found in metadata. The UDM masks may not be available");
                }
            }
        }
        return fileName;
    }

    public String getMaskFileName() {
        String fileName = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_BROWSE)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_BROWSE_INFORMATION)) != null)) {
                fileName = currentElement.getAttributeString(RapidEyeConstants.TAG_FILE_NAME, null);
                if (fileName == null) {
                    logger.warning("Browse file not found in metadata. The UDM masks may not be available");
                } else {
                    fileName = fileName.replace(RapidEyeConstants.BROWSE_FILE_SUFFIX, RapidEyeConstants.UDM_FILE_SUFFIX);
                }
            }
        }
        return fileName;
    }

    public String[] getRasterFileNames(boolean isL1Product) {
        String[] fileNames = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null)) {
                MetadataElement element = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION);
                // other than L3b products
                if (element != null) {
                    String baseName = element.getAttributeString(RapidEyeConstants.TAG_FILE_NAME);
                    if (isL1Product) {
                        if (baseName != null && !baseName.isEmpty()) {
                            fileNames = new String[getNumBands()];
                            for (int i = 0; i < fileNames.length; i++) {
                                fileNames[i] = baseName.replace(RapidEyeConstants.TOKEN_BAND_N, String.format(RapidEyeConstants.TOKEN_BAND_X, i + 1));
                            }
                        } else {
                            logger.warning("Band names not found in metadata. Will scan product folder.");
                        }
                    } else {
                        fileNames = new String[] { baseName };
                    }
                } else {
                    if (((currentElement = currentElement.getElement(TAG_MOSAIC_DECOMPOSITION)) != null) &&
                            ((currentElement = currentElement.getElement(TAG_MOSAIC_TILE)) != null)) {
                        String baseName = currentElement.getAttributeString(RapidEyeConstants.TAG_FILE_NAME);
                        fileNames = new String[] { baseName };
                    }
                }
            }
        }
        return fileNames;
    }

    public SpatialReferenceSystem getReferenceSystem() {
        SpatialReferenceSystem srs = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_SPATIAL_REFERENCE_SYSTEM)) != null)) {
                srs = new SpatialReferenceSystem();
                srs.epsgCode = currentElement.getAttributeString(RapidEyeConstants.TAG_EPSG_CODE, "N/A");
                srs.geodeticDatum = currentElement.getAttributeString(RapidEyeConstants.TAG_GEODETIC_DATUM, "N/A");
                srs.projectionCode = currentElement.getAttributeString(RapidEyeConstants.TAG_PROJECTION, "N/A");
                srs.projectionZone = currentElement.getAttributeString(RapidEyeConstants.TAG_PROJECTION_ZONE, "N/A");
            }
        }
        return srs;
    }

    public ProductData.UTC getProductStartTime() {
        ProductData.UTC startTime = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null)) {
                MetadataElement[] bandSpecificMetadataElements = currentElement.getElements();
                for (MetadataElement element : bandSpecificMetadataElements) {
                    ProductData.UTC currentTime;
                    if (RapidEyeConstants.TAG_BAND_SPECIFIC_METADATA.equals(element.getName())) {
                        String stringData = element.getAttributeString(RapidEyeConstants.TAG_START_DATE_TIME, null);
                        if (stringData != null) {
                            try {
                                if (stringData.endsWith("Z")) stringData = stringData.substring(0,stringData.length() - 1);
                                String microseconds = stringData.substring(stringData.indexOf(".") + 1);
                                Date date = new SimpleDateFormat(RapidEyeConstants.UTC_DATE_FORMAT).parse(stringData);
                                currentTime = ProductData.UTC.create(date, Long.parseLong(microseconds));
                                if (startTime == null) {
                                    startTime = currentTime;
                                } else if (startTime.getAsCalendar().after(currentTime.getAsCalendar())) {
                                    startTime = currentTime;
                                }
                            } catch (ParseException e) {
                                logger.warning(String.format("Product start time not in expected format. Found %s, expected %s",
                                        stringData,
                                        RapidEyeConstants.UTC_DATE_FORMAT));
                            }
                        }
                    }
                }
            }
        }
        return startTime;
    }

    public ProductData.UTC getProductEndTime() {
        ProductData.UTC endTime = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null)) {
                MetadataElement[] bandSpecificMetadataElements = currentElement.getElements();
                for (MetadataElement element : bandSpecificMetadataElements) {
                    ProductData.UTC currentTime;
                    if (RapidEyeConstants.TAG_BAND_SPECIFIC_METADATA.equals(element.getName())) {
                        String stringData = element.getAttributeString(RapidEyeConstants.TAG_END_DATE_TIME, null);
                        if (stringData != null) {
                            try {
                                if (stringData.endsWith("Z")) stringData = stringData.substring(0,stringData.length() - 1);
                                String microseconds = stringData.substring(stringData.indexOf(".") + 1);
                                Date date = new SimpleDateFormat(RapidEyeConstants.UTC_DATE_FORMAT).parse(stringData);
                                currentTime = ProductData.UTC.create(date, Long.parseLong(microseconds));
                                if (endTime == null) {
                                    endTime = currentTime;
                                } else if (endTime.getAsCalendar().before(currentTime.getAsCalendar())) {
                                    endTime = currentTime;
                                }
                            } catch (ParseException e) {
                                logger.warning(String.format("Product end time not in expected format. Found %s, expected %s",
                                                             stringData,
                                                             RapidEyeConstants.UTC_DATE_FORMAT));
                            }
                        }
                    }
                }
            }
        }
        return endTime;
    }

    public float getScaleFactor(int bandIndex) {
        if (scaleFactors == null) {
            scaleFactors = new float[getNumBands()];
            for (int i = 0; i < scaleFactors.length; i++) scaleFactors[i] = 1.0f;
            if (rootElement != null) {
                MetadataElement currentElement;
                if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                        ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null)) {
                    MetadataElement[] bandSpecificMetadataElements = currentElement.getElements();
                    for (MetadataElement element : bandSpecificMetadataElements) {
                        if (RapidEyeConstants.TAG_BAND_SPECIFIC_METADATA.equals(element.getName())) {
                            int idx = Integer.parseInt(element.getAttributeString(RapidEyeConstants.TAG_BAND_NUMBER));
                            String value = element.getAttributeString(RapidEyeConstants.TAG_RADIOMETRIC_SCALE_FACTOR, "");
                            try {
                                scaleFactors[idx - 1] = Float.parseFloat(value);
                            } catch (NumberFormatException e) {
                                logger.warning(String.format("Incorrect scale factor for band %d. Will use default (found %s, expected numeric)",
                                        idx,
                                        value));
                            }
                        }
                    }
                }
            }
        }
        return scaleFactors[bandIndex];
    }

    public int getPixelFormat() {
        RapidEyeConstants.PixelFormat dataType = RapidEyeConstants.PixelFormat.UNSIGNED_INTEGER;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_META_DATA)) != null) {
                String pixelFormat = currentElement.getAttributeString(RapidEyeConstants.TAG_PIXEL_FORMAT, "");
                try {
                    dataType = RapidEyeConstants.PixelFormat.valueOf(pixelFormat);
                } catch (Exception e) {
                    logger.warning(String.format("Invalid pixel format. Found %s, expected 16U or SI.", pixelFormat));
                }
            }
        }
        return dataType.getDataType();
    }

    public float[] getCornersLatitudes() {
        float[] lats = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_TARGET)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_FOOTPRINT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_GEOGRAPHIC_LOCATION)) != null)) {
                MetadataElement[] pointElements = currentElement.getElements();
                lats = new float[4];
                for (MetadataElement element : pointElements) {
                    try {
                        if (RapidEyeConstants.TAG_TOP_LEFT.equals(element.getName())) {
                            lats[0] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LATITUDE, ""));
                        } else if (RapidEyeConstants.TAG_TOP_RIGHT.equals(element.getName())) {
                            lats[1] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LATITUDE, ""));
                        } else if (RapidEyeConstants.TAG_BOTTOM_LEFT.equals(element.getName())) {
                            lats[2] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LATITUDE, ""));
                        } else if (RapidEyeConstants.TAG_BOTTOM_RIGHT.equals(element.getName())) {
                            lats[3] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LATITUDE, ""));
                        }
                    } catch (NumberFormatException e) {
                        logger.severe("One of the corner latitudes is not in the expected format. Product will not have associated geocoding.");
                    }
                }
            }
        }
        return lats;
    }

    public float[] getCornersLongitudes() {
        float[] longs = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_TARGET)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_FOOTPRINT)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_GEOGRAPHIC_LOCATION)) != null)) {
                MetadataElement[] pointElements = currentElement.getElements();
                longs = new float[4];
                for (MetadataElement element : pointElements) {
                    try {
                        if (RapidEyeConstants.TAG_TOP_LEFT.equals(element.getName())) {
                            longs[0] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LONGITUDE, ""));
                        } else if (RapidEyeConstants.TAG_TOP_RIGHT.equals(element.getName())) {
                            longs[1] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LONGITUDE, ""));
                        } else if (RapidEyeConstants.TAG_BOTTOM_LEFT.equals(element.getName())) {
                            longs[2] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LONGITUDE, ""));
                        } else if (RapidEyeConstants.TAG_BOTTOM_RIGHT.equals(element.getName())) {
                            longs[3] = Float.parseFloat(element.getAttributeString(RapidEyeConstants.TAG_LONGITUDE, ""));
                        }
                    } catch (NumberFormatException e) {
                        logger.severe("One of the corner longitudes is not in the expected format. Product will not have associated geocoding.");
                    }
                }
            }
        }
        return longs;
    }

    public RationalCoefficients getRationalPolinomialCoefficients() {
        RationalCoefficients coefficients = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RPC)) != null) {
                coefficients = new RationalCoefficients();
                String data, attrName;
                for (MetadataAttribute attribute : currentElement.getAttributes()) {
                    attrName = attribute.getName();
                    data = currentElement.getAttributeString(attrName, "");
                    if (RapidEyeConstants.TAG_LINEOFF.equals(attrName)) {
                        coefficients.lineOffset = asInt(data);
                    } else if (RapidEyeConstants.TAG_SAMPLEOFF.equals(attrName)) {
                        coefficients.sampleOffset = asInt(data);
                    } else if (RapidEyeConstants.TAG_LATOFF.equals(attrName)) {
                        coefficients.latOffset = asFloat(data);
                    } else if (RapidEyeConstants.TAG_LONGOFF.equals(attrName)) {
                        coefficients.lonOffset = asFloat(data);
                    } else if (RapidEyeConstants.TAG_HEIGHTOFF.equals(attrName)) {
                        coefficients.heightOffset = asInt(data);
                    } else if (RapidEyeConstants.TAG_LINESCALE.equals(attrName)) {
                        coefficients.lineScale = asInt(data);
                    } else if (RapidEyeConstants.TAG_SAMPLESCALE.equals(attrName)) {
                        coefficients.sampleScale = asInt(data);
                    } else if (RapidEyeConstants.TAG_LATSCALE.equals(attrName)) {
                        coefficients.latScale = asInt(data);
                    } else if (RapidEyeConstants.TAG_LONGSCALE.equals(attrName)) {
                        coefficients.lonScale = asFloat(data);
                    } else if (RapidEyeConstants.TAG_HEIGHTSCALE.equals(attrName)) {
                        coefficients.heightScale = asInt(data);
                    } else if (RapidEyeConstants.TAG_LINENUMCOEFF.equals(attrName)) {
                        coefficients.lineNumCoefficients = asFloatArray(data);
                    } else if (RapidEyeConstants.TAG_LINEDENCOEFF.equals(attrName)) {
                        coefficients.lineDenomCoefficients = asFloatArray(data);
                    } else if (RapidEyeConstants.TAG_SAMPLENUMCOEFF.equals(attrName)) {
                        coefficients.sampleNumCoefficients = asFloatArray(data);
                    } else if (RapidEyeConstants.TAG_SAMPLEDENCOEFF.equals(attrName)) {
                        coefficients.sampleDenomCoefficients = asFloatArray(data);
                    }
                }
            }
        }
        return coefficients;
    }

    private float[] asFloatArray(String value) {
        float[] array = null;
        if (value != null && !value.isEmpty()) {
            String[] values = value.split(" ");
            if (values.length > 1) {
                array = new float[values.length];
                for (int i = 0; i < values.length; i++) {
                    array[i] = Float.parseFloat(values[i]);
                }
            }
        }
        return array;
    }

    private float asFloat(String value) {
        float ret = Float.NaN;
        try {
            ret = Float.parseFloat(value);
        } catch (NumberFormatException e) {}
        return ret;
    }

    private int asInt(String value) {
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (NumberFormatException e) {}
        return ret;
    }

    public class SpatialReferenceSystem {
        public String getEpsgCode() {
            return epsgCode;
        }

        public String getGeodeticDatum() {
            return geodeticDatum;
        }

        public String getProjectionCode() {
            return projectionCode;
        }

        public String getProjectionZone() {
            return projectionZone;
        }

        String epsgCode;
        String geodeticDatum;
        String projectionCode;
        String projectionZone;
    }

    public class RationalCoefficients {
        float[] lineNumCoefficients;
        float[] lineDenomCoefficients;
        float[] sampleNumCoefficients;
        float[] sampleDenomCoefficients;
        int lineOffset;
        int sampleOffset;
        int heightOffset;
        float latOffset;
        float lonOffset;
        float latScale;
        float lonScale;
        int lineScale;
        int sampleScale;
        int heightScale;

        public float[] getLineNumeratorCoefficients() {
            return lineNumCoefficients;
        }

        public float[] getLineDenominatorCoefficients() {
            return lineDenomCoefficients;
        }

        public float[] getSampleNumeratorCoefficients() {
            return sampleNumCoefficients;
        }

        public float[] getSampleDenominatorCoefficients() {
            return sampleDenomCoefficients;
        }

        public int getLineOffset() {
            return lineOffset;
        }

        public int getSampleOffset() {
            return sampleOffset;
        }

        public int getHeightOffset() {
            return heightOffset;
        }

        public float getLatitudeOffset() {
            return latOffset;
        }

        public float getLongitudeOffset() {
            return lonOffset;
        }

        public float getLatitudeScale() {
            return latScale;
        }

        public float getLongitudeScale() {
            return lonScale;
        }

        public int getLineScale() {
            return lineScale;
        }

        public int getSampleScale() {
            return sampleScale;
        }

        public int getHeightScale() {
            return heightScale;
        }
    }
}
