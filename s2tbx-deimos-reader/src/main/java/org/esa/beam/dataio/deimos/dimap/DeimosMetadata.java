package org.esa.beam.dataio.deimos.dimap;

import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.framework.datamodel.ProductData;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kraftek on 9/22/2014.
 */
public class DeimosMetadata extends XmlMetadata {

    private float[] bandGains;
    private float[] bandBiases;

    public static class DeimosMetadataParser extends XmlMetadataParser<DeimosMetadata> {

        public DeimosMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public DeimosMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getProductName() {
        String name = getAttributeValue(DeimosConstants.PATH_SOURCE_ID, DeimosConstants.VALUE_NOT_AVAILABLE);
        rootElement.setDescription(name);
        return name;
    }

    public String getProductDescription() {
        String descr = getAttributeValue(DeimosConstants.PATH_SOURCE_DESCRIPTION, DeimosConstants.VALUE_NOT_AVAILABLE);
        if (DeimosConstants.VALUE_NOT_AVAILABLE.equals(descr)) {
            descr = getAttributeValue(DeimosConstants.PATH_SOURCE_ID, DeimosConstants.VALUE_NOT_AVAILABLE);
        }
        rootElement.setDescription(descr);
        return descr;
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(DeimosConstants.PATH_METADATA_FORMAT, DeimosConstants.DIMAP);
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(DeimosConstants.PATH_METADATA_PROFILE, DeimosConstants.DEIMOS);
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            try {
                width = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NCOLS, DeimosConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NCOLS);
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            try {
                height = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NROWS, DeimosConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NROWS);
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        String path = getAttributeValue(DeimosConstants.PATH_DATA_FILE_PATH, null);
        return (path != null ? new String[] { path.toLowerCase() } : null);
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
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
            names[i] = getAttributeValue(DeimosConstants.PATH_BAND_DESCRIPTION, i, DeimosConstants.DEFAULT_BAND_NAME_PREFIX + i);
        }
        return names;
    }

    public String[] getBandUnits() {
        int nBands = getNumBands();
        String[] units = new String[nBands];
        for (int i = 0; i < nBands; i++) {
            units[i] = getAttributeValue(DeimosConstants.PATH_PHYSICAL_UNIT, i, DeimosConstants.DEFAULT_UNIT);
        }
        return units;
    }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            try {
                numBands = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NBANDS, "3"));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NBANDS);
            }
        }
        return numBands;
    }

    public int getNoDataValue() {
        int noData = Integer.MIN_VALUE;
        try {
            noData = Integer.parseInt(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MIN_VALUE)));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.NODATA_VALUE);
        }
        return noData;
    }

    public Color getNoDataColor() {
        Color color = null;
        try {
            int red = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, DeimosConstants.STRING_ZERO)));
            int green = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, DeimosConstants.STRING_ZERO)));
            int blue = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.NODATA_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, DeimosConstants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.BLACK;
        }
        return color;
    }

    public int getSaturatedPixelValue() {
        int saturatedValue = Integer.MAX_VALUE;
        try {
            saturatedValue = Integer.parseInt(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_INDEX, Integer.toString(Integer.MAX_VALUE)));
        } catch (NumberFormatException nfe) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.SATURATED_VALUE);
        }
        return saturatedValue;
    }

    public Color getSaturatedColor() {
        Color color = null;
        try {
            int red = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_RED_LEVEL, DeimosConstants.STRING_ZERO)));
            int green = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_GREEN_LEVEL, DeimosConstants.STRING_ZERO)));
            int blue = (int) (255.0 * Double.parseDouble(getAttributeSiblingValue(DeimosConstants.PATH_SPECIAL_VALUE_TEXT, DeimosConstants.SATURATED_VALUE, DeimosConstants.PATH_SPECIAL_VALUE_COLOR_BLUE_LEVEL, DeimosConstants.STRING_ZERO)));
            color = new Color(red, green, blue);
        } catch (NumberFormatException e) {
            color = Color.WHITE;
        }
        return color;
    }

    public ProductData.UTC getCenterTime() {
        ProductData.UTC centerTime = null;
        String stringDate = getAttributeValue(DeimosConstants.PATH_SCENE_CENTER_DATE, null);
        if (stringDate != null) {
            String stringTime = getAttributeValue(DeimosConstants.PATH_SCENE_CENTER_TIME, null);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stringDate + " " + stringTime);
                centerTime = ProductData.UTC.create(date, 0);
            } catch (ParseException pEx) {
                warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_SCENE_CENTER_DATE);
            }
        }
        return centerTime;
    }

    public InsertionPoint getInsertPoint() {
        InsertionPoint point = null;
        try {
            point = new InsertionPoint();
            point.x = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_ULXMAP, DeimosConstants.STRING_ZERO));
            point.y = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_ULYMAP, DeimosConstants.STRING_ZERO));
            point.stepX = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_XDIM, DeimosConstants.STRING_ZERO));
            point.stepY = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_YDIM, DeimosConstants.STRING_ZERO));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_GEOPOSITION_INSERT);
            point = null;
        }
        return point;
    }

    public float getGain(int bandIndex) {
        if (bandGains == null) {
            extractGainsAndBiases();
        }
        return bandGains[bandIndex];
    }

    private void extractGainsAndBiases() {
        int nBands = getNumBands();
        bandGains = new float[nBands];
        // we extract both, because they are used in conjunction
        bandBiases = new float[nBands];
        try {
            for (int i = 0; i < nBands; i++) {
                bandBiases[i] = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_PHYSICAL_BIAS, i, DeimosConstants.STRING_ZERO)) * DeimosConstants.UNIT_MULTIPLIER;
                bandGains[i] = Float.parseFloat(getAttributeValue(DeimosConstants.PATH_PHYSICAL_GAIN, i, DeimosConstants.STRING_ZERO)) * DeimosConstants.UNIT_MULTIPLIER;
            }
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_SPECTRAL_BAND_INFO);
        }
    }

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }

    public int getPixelDataType() {
        int retVal, value = 8;
        try {
            value = Integer.parseInt(getAttributeValue(DeimosConstants.PATH_NBITS, "8"));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, DeimosConstants.PATH_NBITS);
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
