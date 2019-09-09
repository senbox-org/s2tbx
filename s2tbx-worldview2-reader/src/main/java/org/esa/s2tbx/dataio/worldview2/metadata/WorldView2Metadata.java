package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 * @see XmlMetadata
 */
public class WorldView2Metadata extends XmlMetadata {

    private static class WorldView2MetadataParser extends XmlMetadataParser<WorldView2Metadata> {

        public WorldView2MetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public WorldView2Metadata(String name) {
        super(name);
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
        return  WorldView2Constants.PRODUCT_GENERIC_NAME;
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
        try{
            value = getAttributeValue(WorldView2Constants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2Constants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2Constants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(WorldView2Constants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2Constants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2Constants.WORLDVIEW2_UTC_DATE_FORMAT);
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

    public static WorldView2Metadata create(FilePathInputStream filePathInputStream) throws IOException {
        WorldView2Metadata result;
        try {
            WorldView2MetadataParser parser = new WorldView2MetadataParser(WorldView2Metadata.class);
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
