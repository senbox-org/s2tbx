package org.esa.s2tbx.dataio.kompsat2.metadata;

import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

/**
 * Specialized <code>XmlMetadata</code> for Kompsat2.
 *
 * @author Razvan Dumitrascu
 * @see XmlMetadata
 */

public class Kompsat2Metadata extends XmlMetadata {

    private Kompsat2Component component;

    public Kompsat2Metadata(String name) {
        super(name);
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        String value = null;
        try {
            value = getAttributeValue(Kompsat2Constants.PATH_ID, Kompsat2Constants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return Kompsat2Constants.PRODUCT_GENERIC_NAME;
    }

    @Override
    public int getRasterWidth() {
        return 0;
    }

    @Override
    public int getRasterHeight() {
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[0];
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(Kompsat2Constants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Kompsat2Constants.KOMPSAT2_UTC_DATE_FORMAT);
        }
        return date;
    }

    public String getProductType() {

        String productType = null;
        try {
            productType = getAttributeValue(Kompsat2Constants.PATH_PRODUCT_TYPE, Kompsat2Constants.KOMPSAT2_PRODUCT);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_PRODUCT_TYPE);
        }
        return productType;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(Kompsat2Constants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, Kompsat2Constants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, Kompsat2Constants.KOMPSAT2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    public Kompsat2Component getMetadataComponent() {
        return this.component;
    }

    public void setMetadataComponent(Kompsat2Component component) {
        this.component = component;
    }

    /**
     * Parse tie point grid elements and rearrange them so that the order matches the one used in the
     * tie point grid constructor
     *
     * @param tiePointGridPointsString the tie point grid from metadata file to be parsed
     */
    public static float[][] parseTiePointGridAttribute(String tiePointGridPointsString) {
        String[] values;
        values = tiePointGridPointsString.split(" ");
        float[][] tiePoint = new float[2][values.length / 2 - 1];
        for (int x = 0; x < 8; x += 2) {
            tiePoint[0][x / 2] = Float.parseFloat(values[x]);
            tiePoint[1][x / 2] = Float.parseFloat(values[x + 1]);
        }
        float[] interchangeLat = new float[4];
        float[] interchangeLon = new float[4];
        interchangeLat[0] = tiePoint[0][0];
        interchangeLon[0] = tiePoint[1][0];
        interchangeLat[1] = tiePoint[0][3];
        interchangeLon[1] = tiePoint[1][3];
        interchangeLat[2] = tiePoint[0][1];
        interchangeLon[2] = tiePoint[1][1];
        interchangeLat[3] = tiePoint[0][2];
        interchangeLon[3] = tiePoint[1][2];
        tiePoint[0] = interchangeLat;
        tiePoint[1] = interchangeLon;
        return tiePoint;
    }
}
