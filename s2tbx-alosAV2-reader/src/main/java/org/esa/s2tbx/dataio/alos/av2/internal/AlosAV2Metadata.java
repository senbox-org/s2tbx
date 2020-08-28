package org.esa.s2tbx.dataio.alos.av2.internal;

import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder class for DIMAP metadata file.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2Metadata extends XmlMetadata {

    private HashMap<String, Float> bandGains;
    private HashMap<String, Float> bandBiases;

    public AlosAV2Metadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getProductName() {
        String name = getAttributeValue(AlosAV2Constants.PATH_SOURCE_ID, AlosAV2Constants.VALUE_NOT_AVAILABLE);
        rootElement.setDescription(name);
        return name;
    }

    @Override
    public String getProductDescription() {
        return AlosAV2Constants.DESCRIPTION;
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(AlosAV2Constants.PATH_IMG_METADATA_FORMAT, AlosAV2Constants.DIMAP);
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(AlosAV2Constants.PATH_IMG_METADATA_PROFILE, AlosAV2Constants.ALOSAV2);
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            try {
                width = Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_COLS, AlosAV2Constants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_NUM_COLS);
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            try {
                height = Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_ROWS, AlosAV2Constants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_NUM_ROWS);
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        String path = getAttributeValue(AlosAV2Constants.PATH_IMG_DATA_FILE_PATH, null);
        return (path != null ? new String[]{path.toLowerCase()} : null);
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_FIRST_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_LAST_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    /**
     * Returns the names of the bands found in the metadata file.
     * If the expected metadata nodes are not present, then the default band names
     * are returned (i.e. band_n).
     *
     * @return an array of band names
     */
    public String[] getBandNames() {
        int nBands = getNumBands();
        String[] names = new String[nBands];
        for (int i = 0; i < nBands; i++) {
            names[i] = getAttributeValue(AlosAV2Constants.PATH_BAND_DESCRIPTION, i, AlosAV2Constants.DEFAULT_BAND_NAMES[i]);
            if (names[i].contains(",") || names[i].contains("(")) {
                String betweenBrackets = null;
                if (names[i].contains("(")) {
                    betweenBrackets = names[i].substring(names[i].indexOf("(")+1, names[i].indexOf(")")+1);
                    if (betweenBrackets.contains(",")) {
                        betweenBrackets = betweenBrackets.substring(0, betweenBrackets.indexOf(","));
                    }
                }
                if (betweenBrackets != null) {
                    names[i] = betweenBrackets;
                }
            }
            if(names[i].contains(" ")){
                names[i] = names[i].replace(" ","_");
            }
        }
        return names;
    }

    public Map<String, String> getBandUnits() {
        final int nBands = getNumBands();
        final Map<String,String> units = new HashMap<>();
        for (int i = 0; i < nBands; i++) {
            units.put(getBandNames()[i],getAttributeValue(AlosAV2Constants.PATH_BAND_UNIT, i, AlosAV2Constants.DEFAULT_UNIT));
        }
        return units;
    }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            try {
                numBands = Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_BANDS, "4"));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_NUM_BANDS);
            }
        }
        return numBands;
    }

    public int getNoDataValue() {
        int noData = Integer.MIN_VALUE;
        try {
            noData = Integer.parseInt(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.NODATA, AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MIN_VALUE)));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.NODATA);
        }
        return noData;
    }

    public Color getNoDataColor() {
        Color color;
        try {
            final int red = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.NODATA, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, AlosAV2Constants.STRING_ZERO)));
            final int green = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.NODATA, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, AlosAV2Constants.STRING_ZERO)));
            final int blue = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.NODATA, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, AlosAV2Constants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.BLACK;
        }
        return color;
    }

    public int getSaturatedPixelValue() {
        int saturatedValue = Integer.MAX_VALUE;
        try {
           saturatedValue = Integer.parseInt(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.SATURATED, AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MAX_VALUE)));
        } catch (NumberFormatException nfe) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.SATURATED);
        }
        return saturatedValue;
    }

    public Color getSaturatedColor() {
        Color color;
        try {
            final int red = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.SATURATED, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, AlosAV2Constants.STRING_ZERO)));
            final int green = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.SATURATED, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, AlosAV2Constants.STRING_ZERO)));
            final int blue = (int) (AlosAV2Constants.MAX_LEVEL * Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.SATURATED, AlosAV2Constants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, AlosAV2Constants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.WHITE;
        }
        return color;
    }

    public ProductData.UTC getCenterTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_CENTER_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }


    public float getGain(final String bandName) {
        if (bandGains == null) {
            extractGainsAndBiases();
        }
        return bandGains.get(bandName);
    }

    public float getBias(final String bandName) {
        if (bandBiases == null) {
            extractGainsAndBiases();
        }
        return bandBiases.get(bandName);
    }

    private void extractGainsAndBiases() {
        if (bandGains == null || bandBiases == null) {
            final int nBands = getNumBands();
            bandGains = new HashMap<>();
            // we extract both, because they are used in conjunction
            bandBiases = new HashMap<>();
            try {
                for (int i = 0; i < nBands; i++) {
                    bandGains.put(getBandNames()[i],Float.parseFloat(getAttributeValue(AlosAV2Constants.PATH_PHYSICAL_GAIN, i, AlosAV2Constants.STRING_ZERO)));
                    bandBiases.put(getBandNames()[i],Float.parseFloat(getAttributeValue(AlosAV2Constants.PATH_PHYSICAL_BIAS, i, AlosAV2Constants.STRING_ZERO)));
                }
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_SPECTRAL_BAND_INFO);
            }
        }
    }

    public String getProcessingLevel() {
        String value = null;
        try {
            value = getAttributeValue(AlosAV2Constants.PATH_IMG_PROCESSING_LEVEL, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_PROCESSING_LEVEL);
        }
        if (value == null)
            value = AlosAV2Constants.PROCESSING_1B;
        return value;
    }

    public InsertionPoint[] getGeopositionPoints() {
        InsertionPoint[] points = null;
        try {
            String[] dataX = getAttributeValues(AlosAV2Constants.PATH_TIE_POINT_DATA_X);
            if (dataX != null) {
                String[] dataY = getAttributeValues(AlosAV2Constants.PATH_TIE_POINT_DATA_Y);
                String[] crsX = getAttributeValues(AlosAV2Constants.PATH_TIE_POINT_CRS_X);
                String[] crsY = getAttributeValues(AlosAV2Constants.PATH_TIE_POINT_CRS_Y);
                points = new InsertionPoint[dataX.length];
                for (int i = 0; i < points.length; i++) {
                    points[i] = new InsertionPoint();
                    points[i].x = Float.parseFloat(crsX[i]);
                    points[i].y = Float.parseFloat(crsY[i]);
                    points[i].stepX = Float.parseFloat(dataX[i]);
                    points[i].stepY = Float.parseFloat(dataY[i]);
                }
            }
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_PROCESSING_LEVEL);
        }
        return points;
    }

    public int getPixelDataType() {
        int retVal;
        int value = 8;
        try {
            value = Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NBITS, "8"));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, AlosAV2Constants.PATH_IMG_NBITS);
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

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }
}
