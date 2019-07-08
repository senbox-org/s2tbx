package org.esa.s2tbx.dataio.worldview2esa.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

public class WorldView2ESAMetadata extends XmlMetadata {

    private static class WorldView2ESAMetadataParser extends XmlMetadataParser<WorldView2ESAMetadata> {

        public WorldView2ESAMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public WorldView2ESAMetadata(String name) {
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
            value = getAttributeValue(WorldView2ESAConstants.PATH_ID, WorldView2ESAConstants.PRODUCT_GENERIC_NAME);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_ID);
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return WorldView2ESAConstants.PRODUCT_GENERIC_NAME;
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
            value = getAttributeValue(WorldView2ESAConstants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2ESAConstants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(WorldView2ESAConstants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2ESAConstants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2ESAConstants.WORLDVIEW2_UTC_DATE_FORMAT);
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

    public static WorldView2ESAMetadata create(FilePathInputStream filePathInputStream) throws IOException {
        WorldView2ESAMetadata result;
        try {
            WorldView2ESAMetadataParser parser = new WorldView2ESAMetadataParser(WorldView2ESAMetadata.class);
            result = parser.parse(filePathInputStream);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e);
        }
        Path path = filePathInputStream.getPath();
        result.setPath(path);
        result.setFileName(path.getFileName().toString());
        return result;
    }
}
