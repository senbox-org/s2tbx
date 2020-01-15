/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.rapideye.metadata;

import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

/**
 * Specialized <code>XmlMetadata</code> for RapidEye.
 *
 * @author  Cosmin Cara
 * @see XmlMetadata
 */
public class RapidEyeMetadata extends XmlMetadata {

    private float[] scaleFactors;

    public RapidEyeMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() { return name; }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
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
        return numBands;
    }

    @Override
    public String getProductName() {
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
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION)) != null)) {
            formatName = currentElement.getAttributeString(RapidEyeConstants.TAG_PRODUCT_FORMAT);
        }
        return formatName;
    }

    @Override
    public String getMetadataProfile() {
        String profile = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_META_DATA_PROPERTY)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_META_DATA)) != null)) {
            profile = currentElement.getAttributeString(RapidEyeConstants.TAG_PRODUCT_TYPE);
        }
        return profile;
    }

    @Override
    public String getProductDescription() {
        StringBuilder description = new StringBuilder();
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_USING)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_EQUIPMENT)) != null)) {
            MetadataElement childElement;
            if (((childElement = currentElement.getElement(RapidEyeConstants.TAG_PLATFORM_OUTER)) != null) &&
                    ((childElement = childElement.getElement(RapidEyeConstants.TAG_PLATFORM_INNER)) != null)) {
                description.append("Platform: ").append(childElement.getAttributeString(RapidEyeConstants.TAG_SERIAL_IDENTIFIER, RapidEyeConstants.NOT_AVAILABLE)).append("; ")
                           .append("Orbit type: ").append(childElement.getAttributeString(RapidEyeConstants.TAG_ORBIT_TYPE, RapidEyeConstants.NOT_AVAILABLE)).append("; ");
            }
            if (((childElement = currentElement.getElement(RapidEyeConstants.TAG_INSTRUMENT_OUTER)) != null) &&
                    ((childElement = childElement.getElement(RapidEyeConstants.TAG_INSTRUMENT_INNER)) != null)) {
                description.append("Instrument: ").append(childElement.getAttributeString(RapidEyeConstants.TAG_SHORT_NAME, RapidEyeConstants.NOT_AVAILABLE)).append("; ");
            }
            if (((childElement = currentElement.getElement(RapidEyeConstants.TAG_SENSOR_OUTER)) != null) &&
                    ((childElement = childElement.getElement(RapidEyeConstants.TAG_SENSOR_INNER)) != null)) {
                description.append("Sensor: ").append(childElement.getAttributeString(RapidEyeConstants.TAG_SENSOR_TYPE, RapidEyeConstants.NOT_AVAILABLE)).append(";");
            }
        }
        return description.toString();
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
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
        if (height == 0) {
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

    public String getBrowseFileName() {
        String fileName = null;
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
        return fileName;
    }

    public String getMaskFileName() {
        String fileName = null;
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
        return fileName;
    }

    @Override
    public String[] getRasterFileNames() {
        String[] fileNames = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT)) != null)) {
            MetadataElement element = currentElement.getElement(RapidEyeConstants.TAG_PRODUCT_INFORMATION);
            // other than L3b products
            if (element != null) {
                String baseName = element.getAttributeString(RapidEyeConstants.TAG_FILE_NAME);
                if (isL1Product()) {
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
                if (((currentElement = currentElement.getElement(RapidEyeConstants.TAG_MOSAIC_DECOMPOSITION)) != null) &&
                        ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_MOSAIC_TILE)) != null)) {
                    String baseName = currentElement.getAttributeString(RapidEyeConstants.TAG_FILE_NAME);
                    fileNames = new String[] { baseName };
                }
            }
        }
        return fileNames;
    }

    public SpatialReferenceSystem getReferenceSystem() {
        SpatialReferenceSystem srs = null;
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
        return srs;
    }

    public ProductData.UTC getProductStartTime() {
        ProductData.UTC bandsStartTime = null;
        ProductData.UTC productStartTime = null;
        MetadataElement currentElement;
        if (isL1Product()) {
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null)) {
                MetadataElement[] bandSpecificMetadataElements = currentElement.getElements();
                for (MetadataElement element : bandSpecificMetadataElements) {
                    if (RapidEyeConstants.TAG_BAND_SPECIFIC_METADATA.equals(element.getName())) {
                        ProductData.UTC currentTime = DateHelper.parseDate(element.getAttributeString(RapidEyeConstants.TAG_START_DATE_TIME, null),
                                                                           RapidEyeConstants.UTC_DATE_FORMAT);
                        if (bandsStartTime == null) {
                            bandsStartTime = currentTime;
                        } else if (currentTime != null && bandsStartTime.getAsCalendar().after(currentTime.getAsCalendar())) {
                            bandsStartTime = currentTime;
                        }
                    }
                }
            }
        }
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_VALID_TIME)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_TIME_PERIOD)) != null)) {
            productStartTime = DateHelper.parseDate(currentElement.getAttributeString(RapidEyeConstants.TAG_BEGIN_POSITION, null),
                                                    RapidEyeConstants.UTC_DATE_FORMAT);
        }
        if (productStartTime != null && bandsStartTime != null && !productStartTime.equalElems(bandsStartTime)) {
            logger.warning(String.format("Product start time [%s] is different from bands start time [%s]. Bands start time will be used.",
                                         productStartTime.toString(), bandsStartTime.toString()));
            productStartTime = bandsStartTime;
        }
        return productStartTime;
    }

    public ProductData.UTC getProductEndTime() {
        ProductData.UTC bandsEndTime = null;
        ProductData.UTC productEndTime = null;
        MetadataElement currentElement;
        if (isL1Product()) {
            if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_RESULT_OF)) != null) &&
                    ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_RESULT)) != null)) {
                MetadataElement[] bandSpecificMetadataElements = currentElement.getElements();
                for (MetadataElement element : bandSpecificMetadataElements) {
                    if (RapidEyeConstants.TAG_BAND_SPECIFIC_METADATA.equals(element.getName())) {
                        ProductData.UTC currentTime = DateHelper.parseDate(element.getAttributeString(RapidEyeConstants.TAG_END_DATE_TIME, null),
                                                                           RapidEyeConstants.UTC_DATE_FORMAT);
                        if (bandsEndTime == null) {
                            bandsEndTime = currentTime;
                        } else if (currentTime != null && bandsEndTime.getAsCalendar().before(currentTime.getAsCalendar())) {
                            bandsEndTime = currentTime;
                        }
                    }
                }
            }
        }
        if (((currentElement = rootElement.getElement(RapidEyeConstants.TAG_VALID_TIME)) != null) &&
                ((currentElement = currentElement.getElement(RapidEyeConstants.TAG_TIME_PERIOD)) != null)) {
            productEndTime = DateHelper.parseDate(currentElement.getAttributeString(RapidEyeConstants.TAG_END_POSITION, null),
                                                  RapidEyeConstants.UTC_DATE_FORMAT);
        }
        if (productEndTime != null && bandsEndTime != null && !productEndTime.equalElems(bandsEndTime)) {
            logger.warning(String.format("Product end time [%s] is different from bands end time [%s]. Bands end time will be used.",
                                         productEndTime.toString(), bandsEndTime.toString()));
            productEndTime = bandsEndTime;
        }
        return productEndTime;
    }

    public ProductData.UTC getCenterTime() {
        ProductData.UTC centerTime = null;
        ProductData.UTC startTime = getProductStartTime();
        if (startTime != null) {
            ProductData.UTC endTime = getProductEndTime();
            if (endTime != null) {
                centerTime = DateHelper.average(startTime, endTime);
            }
        }
        return centerTime;
    }

    public float getScaleFactor(int bandIndex) {
        if (scaleFactors == null) {
            scaleFactors = new float[getNumBands()];
            for (int i = 0; i < scaleFactors.length; i++) scaleFactors[i] = 1.0f;
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
        return scaleFactors[bandIndex];
    }

    public int getPixelFormat() {
        RapidEyeConstants.PixelFormat dataType = RapidEyeConstants.PixelFormat.UNSIGNED_INTEGER;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(RapidEyeConstants.TAG_EARTH_OBSERVATION_META_DATA)) != null) {
            String pixelFormat = currentElement.getAttributeString(RapidEyeConstants.TAG_PIXEL_FORMAT, "");
            try {
                dataType = RapidEyeConstants.PixelFormat.valueOf(pixelFormat);
            } catch (Exception e) {
                logger.warning(String.format("Invalid pixel format. Found %s, expected 16U or SI.", pixelFormat));
            }
        }
        return dataType.getDataType();
    }

    public float[] getCornersLatitudes() {
        float[] lats = null;
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
        return lats;
    }

    public float[] getCornersLongitudes() {
        float[] longs = null;
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
        return longs;
    }

    public RationalCoefficients getRationalPolinomialCoefficients() {
        RationalCoefficients coefficients = null;
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
                    coefficients.lineNumCoefficients = asFloatArray(data, " ");
                } else if (RapidEyeConstants.TAG_LINEDENCOEFF.equals(attrName)) {
                    coefficients.lineDenomCoefficients = asFloatArray(data, " ");
                } else if (RapidEyeConstants.TAG_SAMPLENUMCOEFF.equals(attrName)) {
                    coefficients.sampleNumCoefficients = asFloatArray(data, " ");
                } else if (RapidEyeConstants.TAG_SAMPLEDENCOEFF.equals(attrName)) {
                    coefficients.sampleDenomCoefficients = asFloatArray(data, " ");
                }
            }
        }
        return coefficients;
    }

    private boolean isL1Product() {
        String metadataProfile = getMetadataProfile();
        return (metadataProfile != null && metadataProfile.startsWith(RapidEyeConstants.PROFILE_L1));
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
