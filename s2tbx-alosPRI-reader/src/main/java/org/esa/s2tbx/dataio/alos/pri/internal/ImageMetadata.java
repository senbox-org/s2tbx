package org.esa.s2tbx.dataio.alos.pri.internal;

import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

/**
 * Holder class for DIMAP metadata file.
 *
 * @author Denisa Stefanescu
 */
public class ImageMetadata extends XmlMetadata {

    private String imageRelativeFilePath;

    public static class ImageMetadataParser extends XmlMetadataParser<ImageMetadata> {

        public ImageMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected ProductData inferType(String elementName, String value) {
            return ProductData.createInstance(value);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public ImageMetadata(String name) {
        super(name);
    }

    public void setImageRelativeFilePath(String imageRelativeFilePath) {
        this.imageRelativeFilePath = imageRelativeFilePath;
    }

    public String getImageRelativeFilePath() {
        return imageRelativeFilePath;
    }

    @Override
    public int getNumBands() {
        if (numBands == 0) {
            try {
                numBands = Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_IMG_NUM_BANDS, "4"));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_IMG_NUM_BANDS);
            }
        }
        return numBands;
    }

    @Override
    public String getProductName() {
        String name = getAttributeValue(AlosPRIConstants.PATH_SOURCE_ID, AlosPRIConstants.VALUE_NOT_AVAILABLE);
        rootElement.setDescription(name);
        return name;
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(AlosPRIConstants.PATH_IMG_METADATA_FORMAT, AlosPRIConstants.DIMAP);
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            try {
                width = Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_IMG_NUM_COLS, AlosPRIConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_IMG_NUM_COLS);
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            try {
                height = Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_IMG_NUM_ROWS, AlosPRIConstants.STRING_ZERO));
            } catch (NumberFormatException e) {
                warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_IMG_NUM_ROWS);
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        String path = getAttributeValue(AlosPRIConstants.PATH_IMG_DATA_FILE_PATH, null);
        return (path != null ? new String[]{path.toLowerCase()} : null);
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosPRIConstants.PATH_TIME_FIRST_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosPRIConstants.ALOSPRI_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosPRIConstants.PATH_TIME_LAST_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosPRIConstants.ALOSPRI_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosPRIConstants.PATH_TIME_CENTER_LINE, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosPRIConstants.ALOSPRI_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public String getProductDescription() {
        return AlosPRIConstants.DESCRIPTION;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(AlosPRIConstants.PATH_IMG_METADATA_PROFILE, AlosPRIConstants.ALOSPRI);
    }

    public int getBandRows() {
        return Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_BAND_ROWS, AlosPRIConstants.STRING_ZERO));
    }

    public int getBandCols() {
        return Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_BAND_COLS, AlosPRIConstants.STRING_ZERO));
    }

    public String getBandUnit() {
        return getAttributeValue(AlosPRIConstants.PATH_BAND_UNIT, AlosPRIConstants.DEFAULT_UNIT);
    }

    public String getBandName() {
        return getAttributeValue(AlosPRIConstants.PATH_SOURCE_ID, AlosPRIConstants.DEFAULT_BAND_NAMES[0]);
    }

    public int getNoDataValue() {
        return Integer.parseInt(getAttributeSiblingValue(AlosPRIConstants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosPRIConstants.NODATA,
                                                         AlosPRIConstants.PATH_IMG_SPECIAL_VALUE_COUNT, AlosPRIConstants.STRING_ZERO));
    }

    public int getSaturatedValue() {
        return Integer.parseInt(getAttributeSiblingValue(AlosPRIConstants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosPRIConstants.SATURATED,
                                                         AlosPRIConstants.PATH_IMG_SPECIAL_VALUE_COUNT, AlosPRIConstants.STRING_4095));
    }

    public String getCrsCode() {
        return getAttributeValue(AlosPRIConstants.PATH_CRS_CODE, null);
    }

    public int getPixelDataType() {
        int retVal;
        int value = 8;
        try {
            value = Integer.parseInt(getAttributeValue(AlosPRIConstants.PATH_IMG_NBITS, "8"));
        } catch (NumberFormatException e) {
            warn(MISSING_ELEMENT_WARNING, AlosPRIConstants.PATH_IMG_NBITS);
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

    public String getBandDescription() {
        return getAttributeValue(AlosPRIConstants.PATH_BAND_DESCRIPTION, AlosPRIConstants.DEFAULT_BAND_DESCRIPTION);
    }

    public double getGain() {
        return Double.parseDouble(getAttributeValues(AlosPRIConstants.PATH_PHYSICAL_GAIN)[0]);
    }

    public boolean hasInsertPoint() {
        return !AlosPRIConstants.VALUE_NOT_AVAILABLE.equals(getAttributeValue(AlosPRIConstants.PATH_IMG_GEOPOSITION_INSERT_ULXMAP, AlosPRIConstants.VALUE_NOT_AVAILABLE));
    }

    public float getInsertPointX() {
        if(hasInsertPoint()) {
            return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_GEOPOSITION_INSERT_ULXMAP, String.valueOf(Float.NaN)));
        }else{
            return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LON, 0, String.valueOf(Float.NaN)));
        }
    }

    public float getInsertPointY() {
        if(hasInsertPoint()) {
            return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_GEOPOSITION_INSERT_ULYMAP, String.valueOf(Float.NaN)));
        }else{
            return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LAT, 0, String.valueOf(Float.NaN)));
        }
    }

    public float getPixelSizeX() {
        return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_GEOPOSITION_INSERT_XDIM, AlosPRIConstants.DEFAULT_PIXEL_SIZE));
    }

    public float getPixelSizeY() {
        return Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_GEOPOSITION_INSERT_YDIM, AlosPRIConstants.DEFAULT_PIXEL_SIZE));
    }

    public InsertionPoint getInsertPoint() {
        InsertionPoint point = new InsertionPoint();
        if (hasInsertPoint()) {
            point.x = getInsertPointX();
            point.y = getInsertPointY();
            point.stepX = getPixelSizeX();
            point.stepY = getPixelSizeY();
        } else {
            point.x = Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LON, 0, String.valueOf(Float.NaN)));
            point.y = Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LAT, 0, String.valueOf(Float.NaN)));
            point.stepX = Float.parseFloat(AlosPRIConstants.DEFAULT_PIXEL_SIZE);
            point.stepY = Float.parseFloat(AlosPRIConstants.DEFAULT_PIXEL_SIZE);
        }
        return point;
    }

    public float getInsertPointMaxRightX(){
        float pointX;
        if (hasInsertPoint()) {
            pointX = getInsertPointX()+getRasterWidth()*getPixelSizeX();
        } else {
            pointX = Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LON, 1, String.valueOf(Float.NaN)));
        }
        return pointX;
    }

    public float getInsertPointMaxLowerY(){
        float pointY;
        if (hasInsertPoint()) {
            pointY = getInsertPointY()-getRasterHeight()*getPixelSizeY();
        } else {
            pointY = Float.parseFloat(getAttributeValue(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LAT, 3, String.valueOf(Float.NaN)));
        }
        return pointY;
    }

    public float[][] getCornerLonsLats() {
        float[][] result = new float[2][4];
        String[] lons = getAttributeValues(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LON);
        String[] lats = getAttributeValues(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_LAT);
        String[] rows = getAttributeValues(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_ROW);
        String[] cols = getAttributeValues(AlosPRIConstants.PATH_IMG_EXTENT_VERTEX_COL);
        int maxRow = getBandRows();
        int maxCol = getBandCols();
        for (int i = 0; i < 4; i++) {
            int row = Integer.parseInt(rows[i]);
            int col = Integer.parseInt(cols[i]);
            float lon = Float.parseFloat(lons[i]);
            float lat = Float.parseFloat(lats[i]);
            if (row == 0 && col == 0) {
                result[0][0] = lon;
                result[1][0] = lat;
            } else if (row == 0 && col == maxCol - 1) {
                result[0][1] = lon;
                result[1][1] = lat;
            } else if (row == maxRow - 1 && col == 0) {
                result[0][2] = lon;
                result[1][2] = lat;
            } else {
                result[0][3] = lon;
                result[1][3] = lat;
            }
        }
        return result;
    }

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }
}
