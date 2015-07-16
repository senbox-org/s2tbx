package org.esa.s2tbx.dataio.j2k.metadata;

import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.framework.datamodel.ProductData;

import java.awt.geom.Point2D;

/**
 * Metadata extracted from JP2 XML blocks.
 *
 * @author Cosmin Cara
 */
public class Jp2XmlMetadata extends XmlMetadata {

    private static class DeimosMetadataParser extends XmlMetadataParser<Jp2XmlMetadata> {

        public DeimosMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public Jp2XmlMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return "";
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    @Override
    public int getRasterWidth() {
        String dims = getAttributeValue("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/limits/gridenvelope/@high", null);
        if (dims != null) {
            return Integer.parseInt(dims.split(" ")[0]);
        }
        return 0;
    }

    @Override
    public int getRasterHeight() {
        String dims = getAttributeValue("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/limits/gridenvelope/@high", null);
        if (dims != null) {
            return Integer.parseInt(dims.split(" ")[1]);
        }
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        String fileName = getFileName();
        if (fileName != null) {
            return new String[] { fileName };
        } else {
            return new String[0];
        }
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return null;
    }

    public Point2D getOrigin() {
        Point2D origin = null;
        String coords = getAttributeValue("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/origin/point/@pos", null);
        if (coords != null) {
            origin = new Point2D.Double(Double.parseDouble(coords.split(" ")[0]), Double.parseDouble(coords.split(" ")[1]));
        }
        return origin;
    }

    public double getStepX() {
        String[] values = getAttributeValues("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/@offsetVector");
        if (values != null) {
            return Double.parseDouble(values[0].split(" ")[0]);
        }
        return 0;
    }

    public double getStepY() {
        String[] values = getAttributeValues("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/@offsetVector");
        if (values != null) {
            return Double.parseDouble(values[1].split(" ")[1]);
        }
        return 0;
    }

    public String getCrsGeocoding() {
        String crs = null;
        String srs = getAttributeValue("/featurecollection/featuremember/featurecollection/featuremember/rectifiedgridcoverage/rectifiedgriddomain/rectifiedgrid/origin/point/@srsname", null);
        if (srs != null && srs.contains("crs")) {
            crs = srs.substring(srs.indexOf("crs:") + 4);
        }
        return crs;
    }
}
