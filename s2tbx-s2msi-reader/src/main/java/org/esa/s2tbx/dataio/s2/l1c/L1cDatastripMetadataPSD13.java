package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.metadata.GenericXmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by obarrile on 30/09/2016.
 */

public class L1cDatastripMetadataPSD13 extends GenericXmlMetadata implements IL1cDatastripMetadata {

    private static class L1cDatastripMetadataPSD13Parser extends XmlMetadataParser<L1cDatastripMetadataPSD13> {

        public L1cDatastripMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cPSD13Constants.getDatastripSchemaLocations());
            setSchemaBasePath(L1cPSD13Constants.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cDatastripMetadataPSD13 create(Path path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L1cDatastripMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                L1cDatastripMetadataPSD13Parser parser = new L1cDatastripMetadataPSD13Parser(L1cDatastripMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-1C_DataStrip_ID");
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    public L1cDatastripMetadataPSD13(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    @Override
    public MetadataElement getMetadataElement() {
        return rootElement;
    }
}
