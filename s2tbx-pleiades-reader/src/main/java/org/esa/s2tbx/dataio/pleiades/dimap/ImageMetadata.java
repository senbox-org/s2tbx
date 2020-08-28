package org.esa.s2tbx.dataio.pleiades.dimap;

import com.bc.ceres.core.Assert;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.Stx;
import org.esa.snap.core.datamodel.StxFactory;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Metadata class for Pleiades raster metadata.
 *
 * @author Cosmin Cara
 */
public class ImageMetadata extends XmlMetadata {

    private Map<String, Integer> bandIndices;

    static class ImageMetadataParser extends XmlMetadataParser<ImageMetadata> {

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

    public class BandInfo {
        String id;
        int index;
        String unit;
        String description;
        Double gain;
        Double bias;
        Float centralWavelength;
        Float bandwidth;

        public String getId() {
            return id;
        }

        public String getUnit() {
            return unit;
        }

        public String getDescription() {
            return description;
        }

        public Double getGain() {
            return gain;
        }

        public Double getBias() {
            return bias;
        }

        public Float getCentralWavelength() { return centralWavelength; }

        public Float getBandwidth() { return bandwidth; }

        public int getIndex() { return index; }

        public void setIndex(int index) { this.index = index; }
    }

    public static ImageMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        ImageMetadata result = null;
        try (InputStream inputStream = Files.newInputStream(path)) {
            ImageMetadataParser parser = new ImageMetadataParser(ImageMetadata.class);
            Path parentFolderPath = path.getParent();
            result = parser.parse(inputStream);
            result.setPath(parentFolderPath);
            result.setFileName(path.getFileName().toString());
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public ImageMetadata(String name) {
        super(name);
        bandIndices = new HashMap<>();
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public int getNumBands() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NBANDS, Constants.STRING_ZERO));
    }

    @Override
    public String getProductName() {
        return getAttributeValue(Constants.PATH_IMG_DATASET_NAME, Constants.PRODUCT);
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(Constants.PATH_IMG_METADATA_FORMAT, Constants.PRODUCT);
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(Constants.PATH_IMG_METADATA_PROFILE, Constants.PRODUCT);
    }

    @Override
    public int getRasterWidth() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NCOLS, Constants.STRING_ZERO));
    }

    @Override
    public int getRasterHeight() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NROWS, Constants.STRING_ZERO));
    }

    @Override
    public String[] getRasterFileNames() {
        return getAttributeValues(Constants.PATH_IMG_DATA_FILE_PATH);
    }

    public Map<String, int[]> getRasterTileInfo() {
        Map<String, int[]> tileInfo = new HashMap<>();
        String[] names = getRasterFileNames();
        String[] rows = getAttributeValues(Constants.PATH_IMG_DATA_FILE_ROW);
        String[] cols = getAttributeValues(Constants.PATH_IMG_DATA_FILE_COL);
        for (int i = 0; i < names.length; i++) {
            tileInfo.put(names[i], new int[] { Integer.parseInt(rows[i]) - 1, Integer.parseInt(cols[i]) - 1 });
        }
        return tileInfo;
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = getAttributeSiblingValue(Constants.PATH_IMG_LOCATION_TYPE, "BottomCenter", Constants.PATH_IMG_TIME, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Constants.UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = getAttributeSiblingValue(Constants.PATH_IMG_LOCATION_TYPE, "TopCenter", Constants.PATH_IMG_TIME, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Constants.UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        ProductData.UTC date = null;
        String value = getAttributeSiblingValue(Constants.PATH_IMG_LOCATION_TYPE, "Center", Constants.PATH_IMG_TIME, null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Constants.UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public String getProductDescription() {
        return this.name;
    }

    public int getTileCount() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NTILES, Constants.STRING_ONE));
    }

    public int getTileRowsCount() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NTILES_COUNT_ROWS, Constants.STRING_ONE));
    }

    public int getTileColsCount() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NTILES_COUNT_COLS, Constants.STRING_ONE));
    }

    public int getTileWidth() {
        String nCols = getAttributeValue(Constants.PATH_IMG_NTILES_SIZE_NCOLS, Constants.STRING_ZERO);
        if (Constants.STRING_ZERO.equals(nCols)) {
            return getRasterWidth();
        } else {
            return Integer.parseInt(nCols);
        }
    }

    public int getTileHeight() {
        String nRows = getAttributeValue(Constants.PATH_IMG_NTILES_SIZE_NROWS, Constants.STRING_ZERO);
        if (Constants.STRING_ZERO.equals(nRows)) {
            return getRasterHeight();
        } else {
            return Integer.parseInt(nRows);
        }
    }

    public int getTileOverlapX() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_OVERLAP_COL, Constants.STRING_ZERO));
    }

    public int getGetTileOverlapY() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_OVERLAP_ROW, Constants.STRING_ZERO));
    }

    public int getNoDataValue() {
        return Integer.parseInt(getAttributeSiblingValue(Constants.PATH_IMG_SPECIAL_VALUE_TEXT, Constants.NODATA,
                                Constants.PATH_IMG_SPECIAL_VALUE_COUNT, Constants.STRING_ZERO));
    }

    public int getSaturatedValue() {
        return Integer.parseInt(getAttributeSiblingValue(Constants.PATH_IMG_SPECIAL_VALUE_TEXT, Constants.SATURATED,
                Constants.PATH_IMG_SPECIAL_VALUE_COUNT, Constants.STRING_4095));
    }

    public BandInfo[] getBandsInformation() {
        BandInfo[] bandInfos = new BandInfo[getNumBands()];
        String[] ids = getAttributeValues(Constants.PATH_IMG_BAND_ID);
        if (ids == null) {
            for (int i = 0; i < bandInfos.length; i++) {
                bandInfos[i] = new BandInfo();
                bandInfos[i].id = "band_1" + String.valueOf(i);
                bandInfos[i].index = i;
                bandIndices.put( bandInfos[i].id, i);
                bandInfos[i].gain = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID,  bandInfos[i].id, Constants.PATH_IMG_BAND_GAIN, Constants.STRING_ONE));
                bandInfos[i].bias = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID,  bandInfos[i].id, Constants.PATH_IMG_BAND_BIAS, Constants.STRING_ZERO));
                bandInfos[i].unit = getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID,  bandInfos[i].id, Constants.PATH_IMG_BAND_MEASURE, Constants.VALUE_NOT_AVAILABLE);
                float scale = bandInfos[i].unit.contains("micrometer") ? 1000f : 1f;
                float min = Float.parseFloat(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID,  bandInfos[i].id, Constants.PATH_IMG_BAND_MIN, Constants.STRING_ZERO));
                float max = Float.parseFloat(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID,  bandInfos[i].id, Constants.PATH_IMG_BAND_MAX, Constants.STRING_ZERO));
                bandInfos[i].centralWavelength = (min + max) * scale / 2;
                bandInfos[i].bandwidth = (max - min) * scale;
            }
        } else {
            //Arrays.sort(ids);
            for (int i = 0; i < bandInfos.length; i++) {
                bandInfos[i] = new BandInfo();
                bandInfos[i].id = ids[i];
                bandInfos[i].index = i;
                bandIndices.put(ids[i], i);
                bandInfos[i].gain = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, ids[i], Constants.PATH_IMG_BAND_GAIN, Constants.STRING_ONE));
                bandInfos[i].bias = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, ids[i], Constants.PATH_IMG_BAND_BIAS, Constants.STRING_ZERO));
                bandInfos[i].unit = getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, ids[i], Constants.PATH_IMG_BAND_MEASURE, Constants.VALUE_NOT_AVAILABLE);
                float scale = bandInfos[i].unit.contains("micrometer") ? 1000f : 1f;
                float min = Float.parseFloat(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, ids[i], Constants.PATH_IMG_BAND_MIN, Constants.STRING_ZERO));
                float max = Float.parseFloat(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, ids[i], Constants.PATH_IMG_BAND_MAX, Constants.STRING_ZERO));
                bandInfos[i].centralWavelength = (min + max) * scale / 2;
                bandInfos[i].bandwidth = (max - min) * scale;
            }
            Arrays.sort(bandInfos, (o1, o2) -> Double.compare(o1.centralWavelength, o2.centralWavelength));
        }
        return bandInfos;
    }

    public double[][] getScalingAndOffsets() {
        int numBands = getNumBands();
        double[][] result = new double[numBands][2];
        String[] strScalingFactors = getAttributeValues(Constants.PATH_IMG_ADJUSTMENT_SLOPE);
        String[] strScalingOffsets = getAttributeValues(Constants.PATH_IMG_ADJUSTMENT_BIAS);
        if (strScalingFactors != null && strScalingOffsets != null) {
            for (String bandId : bandIndices.keySet()) {
                int i = bandIndices.get(bandId);
                result[i][0] = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_ADJUSTMENT_BAND_ID, bandId, Constants.PATH_IMG_ADJUSTMENT_SLOPE, Constants.STRING_ONE));
                result[i][1] = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_ADJUSTMENT_BAND_ID, bandId, Constants.PATH_IMG_ADJUSTMENT_BIAS, Constants.STRING_ZERO));
            }
        } else {
            for (String bandId : bandIndices.keySet()) {
                int i = bandIndices.get(bandId);
                result[i][0] = 1;
            }
        }
        return result;
    }

    public int getPixelNBits() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NBITS, Constants.DEFAULT_PIXEL_SIZE));
    }

    public int getPixelDataType() {
        int retVal;
        int value = Integer.parseInt(getAttributeValue(Constants.PATH_IMG_NBITS, Constants.DEFAULT_PIXEL_SIZE));
        boolean isUnsigned = Constants.DEFAULT_SIGN.equals(getAttributeValue(Constants.PATH_IMG_SIGN, Constants.DEFAULT_SIGN));
        boolean isInteger = Constants.INTEGER_TYPE.equals(getAttributeValue(Constants.PATH_IMG_DATA_TYPE, Constants.INTEGER_TYPE));
        switch (value) {
            case 8:
                retVal = isUnsigned ? ProductData.TYPE_UINT8 : ProductData.TYPE_INT8;
                break;
            case 12:
            case 16:
                retVal = isUnsigned ? ProductData.TYPE_UINT16 : ProductData.TYPE_INT16;
                break;
            case 32:
                retVal = isInteger ? (isUnsigned ? ProductData.TYPE_UINT32 : ProductData.TYPE_INT32) : ProductData.TYPE_FLOAT32;
                break;
            default:
                retVal = ProductData.TYPE_UINT8;
                break;

        }
        return retVal;
    }

    public Stx[] getBandsStatistics() {
        int numBands = getNumBands();
        Stx[] statistics = new Stx[numBands];
        for (String bandId : bandIndices.keySet()) {
            int i = bandIndices.get(bandId);
            Double gain = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, bandId, Constants.PATH_IMG_BAND_GAIN, Constants.STRING_ONE));
            Double bias = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_BAND_ID, bandId, Constants.PATH_IMG_BAND_BIAS, Constants.STRING_ZERO));
            Double min = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_HISTOGRAM_BAND, bandId, Constants.PATH_IMG_HISTOGRAM_MIN, String.valueOf(Double.NaN)));
            min = min / gain + bias;
            Double max = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_HISTOGRAM_BAND, bandId, Constants.PATH_IMG_HISTOGRAM_MAX, String.valueOf(Double.NaN)));
            max = max / gain + bias;
            Double mean = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_HISTOGRAM_BAND, bandId, Constants.PATH_IMG_HISTOGRAM_MEAN, String.valueOf(Double.NaN)));
            mean = mean / gain + bias;
            Double stDev = Double.parseDouble(getAttributeSiblingValue(Constants.PATH_IMG_HISTOGRAM_BAND, bandId, Constants.PATH_IMG_HISTOGRAM_STDEV, String.valueOf(Double.NaN)));
            stDev = stDev / gain + bias;
            String valuesList = getAttributeSiblingValue(Constants.PATH_IMG_HISTOGRAM_BAND, bandId, Constants.PATH_IMG_HISTOGRAM_VALUES, "");
            if (!(min.isNaN() || max.isNaN() || mean.isNaN() || stDev.isNaN())) {
                try {
                    String[] values = valuesList.trim().split(" ");
                    int[] bins = new int[values.length];
                    for (int x = 0; x < values.length; x++) {
                        bins[x] = Integer.parseInt(values[x]);
                    }
                    statistics[i] = new StxFactory()
                            .withMinimum(min)
                            .withMaximum(max)
                            .withMean(mean)
                            .withStandardDeviation(stDev)
                            .withHistogramBins(bins)
                            .create();
                } catch (Exception ex) {
                    logger.warning("Could not read metadata band statistics for band " + bandId);
                }
            }
        }
        return statistics;
    }

    public Float[] getSolarIrradiances() {
        int numBands = getNumBands();
        Float[] irradiances = new Float[numBands];
        for (String bandId : bandIndices.keySet()) {
            int i = bandIndices.get(bandId);
            irradiances[i] = Float.parseFloat(getAttributeSiblingValue(Constants.PATH_IMG_BAND_IRRADIANCE_BAND_ID, bandId, Constants.PATH_IMG_BAND_IRRADIANCE_VALUE, String.valueOf(Float.NaN)));
        }
        return irradiances;
    }

    public boolean hasInsertPoint() {
        return !Constants.VALUE_NOT_AVAILABLE.equals(getAttributeValue(Constants.PATH_IMG_GEOPOSITION_INSERT_ULXMAP, Constants.VALUE_NOT_AVAILABLE));
    }

    public InsertionPoint getInsertPoint() {
        InsertionPoint point = new InsertionPoint();
        if (hasInsertPoint()) {
            point.x = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_GEOPOSITION_INSERT_ULXMAP, String.valueOf(Float.NaN)));
            point.y = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_GEOPOSITION_INSERT_ULYMAP, String.valueOf(Float.NaN)));
            point.stepX = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_GEOPOSITION_INSERT_XDIM, String.valueOf(Float.NaN)));
            point.stepY = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_GEOPOSITION_INSERT_YDIM, String.valueOf(Float.NaN)));
        } else {
            point.x = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_EXTENT_VERTEX_LON, 0, String.valueOf(Float.NaN)));
            point.y = Float.parseFloat(getAttributeValue(Constants.PATH_IMG_EXTENT_VERTEX_LAT, 0, String.valueOf(Float.NaN)));
            point.stepX = Float.parseFloat(String.valueOf(getPixelSize()));
            point.stepY = Float.parseFloat(String.valueOf(getPixelSize()));
        }
        return point;
    }

    public String getCRSCode() {
        String code = null;
        String value = getAttributeValue(Constants.PATH_IMG_PROJECTED_CRS_CODE, null);
        if (value != null) {
            code = value.replace("urn:ogc:def:crs:", "").replace("::", ":");
        } else {
            value = getAttributeValue(Constants.PATH_IMG_GEODETIC_CRS_CODE, null);
            if (value != null) {
                code = value.replace("urn:ogc:def:crs:", "").replace("::", ":");
            }
        }
        return code;
    }

    public String getSpectralProcessing() {
        return getAttributeValue(Constants.PATH_IMG_SPECTRAL_PROCESSING, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getProcessingLevel() {
        return getAttributeValue(Constants.PATH_IMG_PROCESSING_LEVEL, Constants.VALUE_NOT_AVAILABLE);
    }

    public boolean isGeocoded() {
        return !Constants.PROCESSING_SENSOR.equals(getProcessingLevel());
    }

    public double getPixelSize() {
        String processing = getSpectralProcessing();
        return "PMS".equals(processing) || "PAN".equals(processing) ? Constants.P_RESOLUTION : Constants.MS_RESOLUTION;
    }

    public float[][] getCornerLonsLats() {
        float[][] result = new float[2][4];
        String[] lons = getAttributeValues(Constants.PATH_IMG_EXTENT_VERTEX_LON);
        String[] lats = getAttributeValues(Constants.PATH_IMG_EXTENT_VERTEX_LAT);
        String[] rows = getAttributeValues(Constants.PATH_IMG_EXTENT_VERTEX_ROW);
        String[] cols = getAttributeValues(Constants.PATH_IMG_EXTENT_VERTEX_COL);
        int maxRow = getRasterHeight();
        int maxCol = getRasterWidth();
        for (int i = 0; i < 4; i++) {
            int row = Integer.parseInt(rows[i]);
            int col = Integer.parseInt(cols[i]);
            float lon = Float.parseFloat(lons[i]);
            float lat = Float.parseFloat(lats[i]);
            if (row == 1 && col == 1) {
                result[0][0] = lon;
                result[1][0] = lat;
            } else if (row == 1 && col == maxCol) {
                result[0][1] = lon;
                result[1][1] = lat;
            } else if (row == maxRow && col == 1) {
                result[0][2] = lon;
                result[1][2] = lat;
            } else {
                result[0][3] = lon;
                result[1][3] = lat;
            }
        }
        return result;
    }

    public List<MaskInfo> getMasks() {
        List<MaskInfo> masks = new ArrayList<>();
        String[] maskNames = getAttributeValues(Constants.PATH_GML_COMPONENT_TITLE);
        String[] maskDescs = getAttributeValues(Constants.PATH_GML_MEASURE_DESC);
        String[] paths = getAttributeValues(Constants.PATH_GML_COMPONENT_PATH);
        if (maskNames != null && paths != null && maskNames.length == paths.length) {
            for (int i = 0; i < maskNames.length; i++) {
                MaskInfo mask = new MaskInfo();
                mask.name = maskNames[i];
                mask.description = (maskDescs != null && maskDescs.length == maskNames.length) ? maskDescs[i] : maskNames[i];
                mask.path = this.path.resolve(paths[i]);
                masks.add(mask);
            }
        }

        return masks;
    }

    public class InsertionPoint {
        public float x;
        public float y;
        public float stepX;
        public float stepY;
    }

    public class MaskInfo {
        public String name;
        public String description;
        public Path path;
    }
}
