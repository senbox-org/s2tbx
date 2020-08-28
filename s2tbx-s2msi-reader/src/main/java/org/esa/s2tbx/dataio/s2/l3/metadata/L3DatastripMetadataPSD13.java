package org.esa.s2tbx.dataio.s2.l3.metadata;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.l3.L3PSD13Constants;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by obarrile on 07/10/2016.
 */
public class L3DatastripMetadataPSD13 extends GenericXmlMetadata implements IL3DatastripMetadata  {

    private static class L3DatastripMetadataPSD13Parser extends XmlMetadataParser<L3DatastripMetadataPSD13> {

        public L3DatastripMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L3PSD13Constants.getDatastripSchemaLocations());
            setSchemaBasePath(L3PSD13Constants.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L3DatastripMetadataPSD13 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L3DatastripMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L3DatastripMetadataPSD13Parser parser = new L3DatastripMetadataPSD13Parser(L3DatastripMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-3_DataStrip_ID");
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    public L3DatastripMetadataPSD13(String name) {
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
