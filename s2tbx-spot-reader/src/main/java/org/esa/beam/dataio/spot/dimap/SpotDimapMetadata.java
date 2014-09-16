package org.esa.beam.dataio.spot.dimap;

import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.dataio.spot.internal.DimapSchemaHelper;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.geotools.coverage.grid.io.imageio.geotiff.TiePoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Holder for DIMAP metadata file (either Scene or View).
 *
 * @author Cosmin Cara
 */
public class SpotDimapMetadata extends XmlMetadata {
    private float[] wavelengths;
    private float[] bandwidths;
    private double[] scalingFactors;
    private double[] scalingOffsets;
    private float[] bandGains;
    private float[] bandBiases;
    private List<HashMap<String, Double>> bandStatistics;

    public static class SpotDimapMetadataParser extends XmlMetadataParser<SpotDimapMetadata> {

        public SpotDimapMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(DimapSchemaHelper.getSchemaLocations());
        }

        @Override
        protected ProductData inferType(String elementName, String value) {
            return DimapSchemaHelper.createProductData(elementName, value);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return true;
        }
    }

    public SpotDimapMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getProductName() {
        if (rootElement == null) {
            return null;
        }
        String name = null;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_DATASET_SOURCES)) != null &&
                (currentElement = currentElement.getElement(SpotConstants.TAG_SOURCE_INFORMATION)) != null) {
            name = currentElement.getAttributeString(SpotConstants.TAG_SOURCE_ID);
            rootElement.setDescription(name);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_DATASET_NAME));
        }
        return name;
    }

    public String getProductDescription() {
        if (rootElement == null) {
            return null;
        }
        String descr = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_DATASET_SOURCES)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_SOURCE_INFORMATION)) != null)) {
            descr = currentElement.getAttributeString(SpotConstants.TAG_SOURCE_DESCRIPTION);
            rootElement.setDescription(descr);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SOURCE_DESCRIPTION));
        }
        return descr;
    }

    @Override
    public String getFormatName() {
        String format = "NOT DIMAP";
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_METADATA_ID)) != null) {
            format = currentElement.getAttributeString(SpotConstants.TAG_METADATA_FORMAT);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_METADATA_FORMAT));
        }
        return format;
    }

    @Override
    public String getMetadataProfile() {
        String profile = "SPOTScene";
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_METADATA_ID)) != null) {
            profile = currentElement.getAttributeString(SpotConstants.TAG_METADATA_PROFILE);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_METADATA_PROFILE));
        }
        return profile;
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            MetadataElement rasterDimensions = rootElement.getElement(SpotConstants.TAG_RASTER_DIMENSIONS);
            try {
                width = Integer.parseInt(rasterDimensions.getAttributeString(SpotConstants.TAG_NCOLS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_NCOLS));
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            MetadataElement rasterDimensions = rootElement.getElement(SpotConstants.TAG_RASTER_DIMENSIONS);
            try {
                height = Integer.parseInt(rasterDimensions.getAttributeString(SpotConstants.TAG_NROWS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_NROWS));
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        if (rootElement == null) {
            return null;
        }
        String path = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_DATA_ACCESS)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_DATA_FILE)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_DATA_FILE_PATH)) != null)) {
            path = currentElement.getAttributeString(SpotConstants.ATTR_HREF);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_DATA_FILE_PATH));
        }
        return (path != null ? new String[] { path.toLowerCase() } : null);
    }

    /**
     * Returns the names of the bands found in the metadata file.
     * If the expected metadata nodes are not present, then the default band names
     * are returned (i.e. band_n).
     *
     * @return an array of band names
     */
    public String[] getBandNames() {
        if (rootElement == null) {
            return null;
        }
        String[] names = new String[getNumBands()];
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_INTERPRETATION)) != null) {
            MetadataElement[] bandInfo = currentElement.getElements();
            for (int i = 0; i < bandInfo.length; i++) {
                names[i] = bandInfo[i].getAttributeString(SpotConstants.TAG_BAND_DESCRIPTION, SpotConstants.DEFAULT_BAND_NAME_PREFIX + i);
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = SpotConstants.DEFAULT_BAND_NAME_PREFIX + i;
            }
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_BAND_DESCRIPTION));
        }
        return names;
    }

    public String[] getBandUnits() {
        if (rootElement == null) {
            return null;
        }
        String[] units = new String[getNumBands()];
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_INTERPRETATION)) != null) {
            MetadataElement[] bandInfo = currentElement.getElements();
            for (int i = 0; i < bandInfo.length; i++) {
                units[i] = bandInfo[i].getAttributeString(SpotConstants.TAG_PHYSICAL_UNIT, SpotConstants.DEFAULT_SPOT_UNIT);
            }
        } else {
            for (int i = 0; i < units.length; i++) {
                units[i] = SpotConstants.DEFAULT_SPOT_UNIT;
            }
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_PHYSICAL_UNIT));
        }
        return units;
    }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            MetadataElement rasterDimensions = rootElement.getElement(SpotConstants.TAG_RASTER_DIMENSIONS);
            try {
                numBands = Integer.parseInt(rasterDimensions.getAttributeString(SpotConstants.TAG_NBANDS));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_NBANDS));
            }
        }
        return numBands;
    }

    public int getNoDataValue() {
        int noData = -1;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_DISPLAY)) != null) {
                MetadataElement[] specialValues = currentElement.getElements();
                for (MetadataElement specialValue : specialValues) {
                    if (specialValue.containsAttribute(SpotConstants.TAG_SPECIAL_VALUE_TEXT) &&
                            SpotConstants.NODATA_VALUE.equals(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_TEXT))) {
                        noData = Integer.parseInt(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_INDEX));
                    }
                }
            } else {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.NODATA_VALUE));
            }
        }
        return noData;
    }

    public Color getNoDataColor() {
        Color color = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_DISPLAY)) != null) {
                MetadataElement[] specialValues = currentElement.getElements();
                for (MetadataElement specialValue : specialValues) {
                    if (specialValue.containsAttribute(SpotConstants.TAG_SPECIAL_VALUE_TEXT) &&
                            SpotConstants.NODATA_VALUE.equals(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_TEXT))) {
                        if ((currentElement = specialValue.getElement(SpotConstants.TAG_SPECIAL_VALUE_COLOR)) != null) {
                            int red = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_RED_LEVEL)));
                            int green = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_GREEN_LEVEL)));
                            int blue = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_BLUE_LEVEL)));
                            color = new Color(red, green, blue);
                        }
                    }
                }
            } else {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.NODATA_VALUE));
            }
        }
        return color;
    }

    public int getSaturatedPixelValue() {
        int saturatedValue = -1;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_DISPLAY)) != null) {
                MetadataElement[] specialValues = currentElement.getElements();
                for (MetadataElement specialValue : specialValues) {
                    if (specialValue.containsAttribute(SpotConstants.TAG_SPECIAL_VALUE_TEXT) &&
                            SpotConstants.SATURATED_VALUE.equals(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_TEXT))) {
                        saturatedValue = Integer.parseInt(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_INDEX));
                    }
                }
            } else {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.SATURATED_VALUE));
            }
        }
        return saturatedValue;
    }

    public Color getSaturatedColor() {
        Color color = null;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_DISPLAY)) != null) {
                MetadataElement[] specialValues = currentElement.getElements();
                for (MetadataElement specialValue : specialValues) {
                    if (specialValue.containsAttribute(SpotConstants.TAG_SPECIAL_VALUE_TEXT) &&
                            SpotConstants.SATURATED_VALUE.equals(specialValue.getAttributeString(SpotConstants.TAG_SPECIAL_VALUE_TEXT))) {
                        if ((currentElement = specialValue.getElement(SpotConstants.TAG_SPECIAL_VALUE_COLOR)) != null) {
                            int red = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_RED_LEVEL)));
                            int green = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_GREEN_LEVEL)));
                            int blue = (int) (255.0 * Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_BLUE_LEVEL)));
                            color = new Color(red, green, blue);
                        }
                    }
                }
            } else {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.SATURATED_VALUE));
            }
        }
        return color;
    }

    public float getWavelength(int bandIndex) {
        if (wavelengths == null) {
            extractWavelengthAndBandwidths();
        }
        return wavelengths[bandIndex];
    }

    public float getBandwidth(int bandIndex) {
        if (bandwidths == null) {
            extractWavelengthAndBandwidths();
        }
        return bandwidths[bandIndex];
    }

    public ProductData.UTC getCenterTime() {
        ProductData.UTC centerTime = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_DATA_STRIP)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_SENSOR_CONFIGURATION)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_TIME_STAMP)) != null)) {
            String stringData = currentElement.getAttributeString(SpotConstants.TAG_SCENE_CENTER_TIME);
            if (stringData != null) {
                String milliseconds = stringData.substring(stringData.indexOf(".") + 1);
                stringData = stringData.substring(0, stringData.indexOf(".")) + ".000000";
                try {
                    Date date = new SimpleDateFormat(SpotConstants.UTC_DATE_FORMAT).parse(stringData);
                    centerTime = ProductData.UTC.create(date, Long.parseLong(milliseconds));
                } catch (ParseException pEx) {
                    logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SCENE_CENTER_TIME));
                }
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SCENE_CENTER_TIME));
        }
        return centerTime;
    }

    public TiePoint[] getTiePoints() {
        TiePoint[] tiePoints = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_GEOPOSITION)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_GEOPOSITION_POINTS)) != null)) {
            MetadataElement[] tiePointNodes = currentElement.getElements();
            if (tiePointNodes != null && tiePointNodes.length > 0) {
                try {
                    tiePoints = new TiePoint[tiePointNodes.length];
                    int idx = 0;
                    for (MetadataElement tiePointNode : tiePointNodes) {
                        float i = Float.parseFloat(tiePointNode.getAttributeString(SpotConstants.TAG_TIE_POINT_DATA_X));
                        float j = Float.parseFloat(tiePointNode.getAttributeString(SpotConstants.TAG_TIE_POINT_DATA_Y));
                        float x = Float.parseFloat(tiePointNode.getAttributeString(SpotConstants.TAG_TIE_POINT_CRS_X));
                        float y = Float.parseFloat(tiePointNode.getAttributeString(SpotConstants.TAG_TIE_POINT_CRS_Y));
                        float z = Float.parseFloat(tiePointNode.getAttributeString(SpotConstants.TAG_TIE_POINT_CRS_Z));
                        tiePoints[idx++] = new TiePoint(i, j, 0.0, x, y, z);
                    }
                } catch (NumberFormatException e) {
                    logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_GEOPOSITION_POINTS));
                    tiePoints = null;
                }
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_GEOPOSITION_POINTS));
        }
        return tiePoints;
    }

    public InsertionPoint getInsertPoint() {
        InsertionPoint point = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_GEOPOSITION)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_GEOPOSITION_INSERT)) != null)) {
            try {
                point = new InsertionPoint();
                point.x = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_ULXMAP, "0"));
                point.y = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_ULYMAP, "0"));
                point.stepX = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_XDIM, "0"));
                point.stepY = Float.parseFloat(currentElement.getAttributeString(SpotConstants.TAG_YDIM, "0"));
            } catch (NumberFormatException e) {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_GEOPOSITION_INSERT));
                point = null;
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_GEOPOSITION_INSERT));
        }
        return point;
    }

    public double getScalingFactor(int bandIndex) {
        if (scalingFactors == null) {
            computeScaling();
            //extractGainsAndBiases();
        }
        return scalingFactors[bandIndex];
        //return bandGains[bandIndex];
    }

    public double getScalingOffset(int bandIndex) {
        if (scalingFactors == null) {
            computeScaling();
            //extractGainsAndBiases();
        }
        return scalingOffsets[bandIndex];
        //return bandBiases[bandIndex];
    }

    public float getGain(int bandIndex) {
        if (bandGains == null) {
            extractGainsAndBiases();
        }
        return bandGains[bandIndex];
    }

    public String getCRSCode() {
        if (rootElement == null) {
            return null;
        }
        String name = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_COORDINATE_REFERENCE_SYSTEM)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_HORIZONTAL_CS)) != null)) {
            name = currentElement.getAttributeString(SpotConstants.TAG_HORIZONTAL_CS_CODE);
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_HORIZONTAL_CS_CODE));
        }
        return name;
    }

    public double getOrientation() {
        double orientation = 0.0;
        if (rootElement != null) {
            MetadataElement currentElement;
            if ((currentElement = rootElement.getElement(SpotConstants.TAG_DATASET_FRAME)) != null) {
                try {
                    orientation = Double.parseDouble(currentElement.getAttributeString(SpotConstants.TAG_SCENE_ORIENTATION));
                } catch (NumberFormatException e) {
                    logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SCENE_ORIENTATION));
                }
            } else {
                logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SCENE_ORIENTATION));
            }
        }
        return orientation;
    }

    public Point2D.Float[] getCornerCoordinates() {
        if (rootElement == null) {
            return null;
        }
        Point2D.Float[] corners = new Point2D.Float[5];
        MetadataElement currentElement = rootElement.getElement(SpotConstants.TAG_DATASET_FRAME);
        if (currentElement != null) {
            MetadataElement[] vertices = currentElement.getElements();
            float x, y;
            int idx = 0;
            for (MetadataElement vertex : vertices) {
                if (SpotConstants.TAG_VERTEX.equals(vertex.getName()) || SpotConstants.TAG_SCENE_CENTER.equals(vertex.getName())) {
                    x = Float.parseFloat(vertex.getAttributeString(SpotConstants.TAG_FRAME_LON));
                    y = Float.parseFloat(vertex.getAttributeString(SpotConstants.TAG_FRAME_LAT));
                    corners[idx++] = new Point2D.Float(x, y);
                }
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_DATASET_FRAME));
        }
        return corners;
    }

    public HashMap<String, Double> getStatistics(int bandIndex) {
        if (bandStatistics == null) {
            bandStatistics = new ArrayList<HashMap<String, Double>>(getNumBands());
            if (rootElement != null) {
                MetadataElement currentElement;
                if ((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_DISPLAY)) != null) {
                    MetadataElement[] elements = currentElement.getElements();
                    // There are 2 values that will not be used because there is no such support in BEAM StxFactory: LIN_MIN and LIN_MAX
                    int bIdx;
                    for (MetadataElement element : elements) {
                        if (SpotConstants.TAG_BAND_STATISTICS.equals(element.getName())) {
                            final double min = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_MIN, "0"));
                            final double max = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_MAX, "0"));
                            final double mean = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_MEAN, "0"));
                            final double stdv = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_STDV, "0"));
                            final double linMin = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_LIN_MIN, "0"));
                            final double linMax = Double.parseDouble(element.getAttributeString(SpotConstants.TAG_STX_LIN_MAX, "0"));
                            bIdx = Integer.parseInt(element.getAttributeString(SpotConstants.TAG_BAND_INDEX, "0"));
                            HashMap<String, Double> hashMap = new HashMap<String, Double>() {{
                                                                    put(SpotConstants.TAG_STX_MIN, min);
                                                                    put(SpotConstants.TAG_STX_MAX, max);
                                                                    put(SpotConstants.TAG_STX_MEAN, mean);
                                                                    put(SpotConstants.TAG_STX_STDV, stdv);
                                                                    put(SpotConstants.TAG_STX_LIN_MIN, linMin);
                                                                    put(SpotConstants.TAG_STX_LIN_MAX, linMax);
                                                                }};
                            if (bIdx > 0) {
                                bandStatistics.add(bIdx - 1, hashMap);
                            } else {
                                bandStatistics.add(hashMap);
                            }
                        }
                    }
                }
            }
        }
        return bandStatistics.size() > 0 ? bandStatistics.get(bandIndex) : null;
    }

    private void extractWavelengthAndBandwidths() {
        MetadataElement currentElement;
        bandwidths = new float[getNumBands()];
        // we extract both, because they are used in conjunction
        wavelengths = new float[getNumBands()];
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_DATA_STRIP)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_SENSOR_CALIBRATION)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_SPECTRAL_SENSITIVITIES)) != null)) {
            MetadataElement[] bandSpectralSensitivities = currentElement.getElements();
            for (int i = 0; i < bandSpectralSensitivities.length; i++) {
                wavelengths[i] = Float.parseFloat(bandSpectralSensitivities[i].getAttributeString(SpotConstants.TAG_FIRST_WAVELENGTH_VALUE)) * SpotConstants.UNIT_MULTIPLIER;
                bandwidths[i] = Float.parseFloat(bandSpectralSensitivities[i].getAttributeString(SpotConstants.TAG_WAVELENGTH_STEP)) * SpotConstants.UNIT_MULTIPLIER;
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SPECTRAL_SENSITIVITIES));
        }
    }

    private void extractGainsAndBiases() {
        MetadataElement currentElement;
        bandGains = new float[getNumBands()];
        // we extract both, because they are used in conjunction
        bandBiases = new float[getNumBands()];
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_IMAGE_INTERPRETATION)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_SPECTRAL_BAND_INFO)) != null)) {
            MetadataElement[] bandInfo = currentElement.getElements();
            for (int i = 0; i < bandInfo.length; i++) {
                bandBiases[i] = Float.parseFloat(bandInfo[i].getAttributeString(SpotConstants.TAG_PHYSICAL_BIAS)) * SpotConstants.UNIT_MULTIPLIER;
                bandGains[i] = Float.parseFloat(bandInfo[i].getAttributeString(SpotConstants.TAG_PHYSICAL_GAIN)) * SpotConstants.UNIT_MULTIPLIER;
            }
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_SPECTRAL_BAND_INFO));
        }
    }

    private void computeScaling() {
        MetadataElement currentElement;
        scalingFactors = new double[getNumBands()];
        scalingOffsets = new double[getNumBands()];
        double minPixel, maxPixel;
        if (((currentElement = rootElement.getElement(SpotConstants.TAG_DATA_PROCESSING)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_PROCESSING_OPTIONS)) != null) &&
                ((currentElement = currentElement.getElement(SpotConstants.TAG_DYNAMIC_STRETCH)) != null)) {
            MetadataElement[] thresholds = currentElement.getElements();
            for (int i = 0; i < thresholds.length; i++) {
                minPixel = Double.parseDouble(thresholds[i].getAttributeString(SpotConstants.TAG_LOW_THRESHOLD));
                maxPixel = Double.parseDouble(thresholds[i].getAttributeString(SpotConstants.TAG_HIGH_THRESHOLD));
                scalingFactors[i] = (wavelengths[i] + bandwidths[i]) / (maxPixel - minPixel);
                scalingOffsets[i] = wavelengths[i] - scalingFactors[i] * minPixel;
            }
        }
    }

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }

    public int getPixelDataType() {
        int value = 0, retVal;
        MetadataElement currentElement;
        if ((currentElement = rootElement.getElement(SpotConstants.TAG_RASTER_ENCODING)) != null) {
            value = Integer.parseInt(currentElement.getAttributeString(SpotConstants.TAG_NBITS, "0"));
        } else {
            logger.warning(String.format(MISSING_ELEMENT_WARNING, SpotConstants.TAG_NBITS));
        }
        switch (value) {
            case 8:
                retVal = ProductData.TYPE_UINT8;
                break;
            case 16:
                retVal = ProductData.TYPE_INT16;
                break;
            case 32:
                retVal = ProductData.TYPE_FLOAT32;
                break;
            default:
                retVal = ProductData.TYPE_UINT8;
                break;

        }
        return retVal;
    }

}
