package org.esa.s2tbx.dataio.alosAV2.metadata;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.alosAV2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BandMetadata extends XmlMetadata {

    private String imageFileName;
    private Map<String, Integer> bandIndices;

    private static class ImageMetadataParser extends XmlMetadataParser<BandMetadata> {

        ImageMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
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

    public static BandMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        BandMetadata result = null;
        try (InputStream inputStream = Files.newInputStream(path)) {
            ImageMetadataParser parser = new ImageMetadataParser(BandMetadata.class);
            result = parser.parse(inputStream);
            result.setPath(path.getParent().toAbsolutePath().toString());
            result.setFileName(path.getFileName().toString());
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    public BandMetadata(String name) {
        super(name);
        bandIndices = new HashMap<>();
    }


    public String getCRSCode() {
        return getAttributeValue(AlosAV2Constants.PATH_CRS_NAME, null);
    }

    @Override
    public int getNumBands() {
        return Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_BANDS, AlosAV2Constants.STRING_ZERO));
    }

    @Override
    public String getProductName() {
        return getAttributeValue(AlosAV2Constants.PATH_IMG_DATASET_NAME, AlosAV2Constants.PRODUCT_GENERIC_NAME);
    }

    @Override
    public String getFormatName() {
        return getAttributeValue(AlosAV2Constants.PATH_IMG_METADATA_FORMAT, AlosAV2Constants.PRODUCT_GENERIC_NAME);
    }

    @Override
    public int getRasterWidth() {
        return Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_COLS, AlosAV2Constants.STRING_ZERO));
    }

    @Override
    public int getRasterHeight() {
        return Integer.parseInt(getAttributeValue(AlosAV2Constants.PATH_IMG_NUM_ROWS,AlosAV2Constants.STRING_ZERO));
    }

    @Override
    public String[] getRasterFileNames() {
        return getAttributeValues(AlosAV2Constants.PATH_IMG_DATA_FILE_PATH);
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_FIRST_LINE,null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_LAST_LINE,null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        ProductData.UTC date = null;
        String value = getAttributeValue(AlosAV2Constants.PATH_TIME_CENTER_LINE,null);
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, AlosAV2Constants.ALOSAV2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public String getProductDescription() {
        return AlosAV2Constants.DESCRIPTION;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(AlosAV2Constants.PATH_IMG_METADATA_PROFILE, AlosAV2Constants.PRODUCT_GENERIC_NAME);
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public int getNoDataValue() {
        return Integer.parseInt(getAttributeSiblingValue(AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_TEXT, AlosAV2Constants.NODATA,
                                                         AlosAV2Constants.PATH_IMG_SPECIAL_VALUE_INDEX, AlosAV2Constants.STRING_ZERO));
    }

    public BandInfo[] getBandsInformation() {
        BandInfo[] bandInfos = new BandInfo[getNumBands()];
        String[] ids = getAttributeValues(AlosAV2Constants.PATH_BAND_INDEX);
        if (ids == null) {
            for (int i = 0; i < bandInfos.length; i++) {
                bandInfos[i] = new BandInfo();
                bandInfos[i].id = "band_1" + String.valueOf(i);
                bandInfos[i].index = i;
            }
        } else {
            //Arrays.sort(ids);
            for (int i = 0; i < bandInfos.length; i++) {
                bandInfos[i] = new BandInfo();
                bandInfos[i].id = ids[i];
                bandInfos[i].index = i;
                bandIndices.put(ids[i], i);
                bandInfos[i].gain = Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_BAND_INDEX, ids[i], AlosAV2Constants.PATH_PHYSICAL_GAIN, AlosAV2Constants.STRING_ONE));
                bandInfos[i].bias = Double.parseDouble(getAttributeSiblingValue(AlosAV2Constants.PATH_BAND_INDEX, ids[i], AlosAV2Constants.PATH_PHYSICAL_BIAS, AlosAV2Constants.STRING_ZERO));
                bandInfos[i].unit = getAttributeSiblingValue(AlosAV2Constants.PATH_BAND_INDEX, ids[i], AlosAV2Constants.PATH_UNIT, AlosAV2Constants.VALUE_NOT_AVAILABLE);
                float scale = bandInfos[i].unit.contains("micrometer")||bandInfos[i].unit.contains("uM")  ? 1000f : 1f;
                float min = Float.parseFloat(getAttributeSiblingValue(AlosAV2Constants.PATH_BAND_INDEX, ids[i], AlosAV2Constants.PATH_IMG_BAND_MIN, AlosAV2Constants.STRING_ZERO));
                float max = Float.parseFloat(getAttributeSiblingValue(AlosAV2Constants.PATH_BAND_INDEX, ids[i], AlosAV2Constants.PATH_IMG_BAND_MAX, AlosAV2Constants.STRING_ZERO));
                bandInfos[i].centralWavelength = (min + max) * scale / 2;
                bandInfos[i].bandwidth = (max - min) * scale;
            }
            Arrays.sort(bandInfos, (o1, o2) -> Double.compare(o1.centralWavelength, o2.centralWavelength));
        }
        return bandInfos;
    }
}
