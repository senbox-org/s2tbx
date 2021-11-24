package org.esa.s2tbx.dataio.s2.l1c.metadata;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.l1c.L1cPSD148Constants;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by obarrile on 30/09/2016.
 */

public class L1cDatastripMetadataPSD148 extends GenericXmlMetadata implements IL1cDatastripMetadata {

    private static class L1cDatastripMetadataPSD148Parser extends XmlMetadataParser<L1cDatastripMetadataPSD148> {

        public L1cDatastripMetadataPSD148Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cPSD148Constants.getDatastripSchemaLocations());
            setSchemaBasePath(L1cPSD148Constants.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cDatastripMetadataPSD148 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L1cDatastripMetadataPSD148 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L1cDatastripMetadataPSD148Parser parser = new L1cDatastripMetadataPSD148Parser(L1cDatastripMetadataPSD148.class);
                result = parser.parse(stream);
                result.setName("Level-1C_DataStrip_ID");
            }
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (IOException ignore) {
            }
        }
        return result;
    }

    public L1cDatastripMetadataPSD148(String name) {
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
