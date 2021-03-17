package org.esa.s2tbx.dataio.s2.l2h.metadata;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.l2h.L2hPSD13Constants;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by obarrile on 04/10/2016.
 */

public class L2hDatastripMetadataPSD13 extends GenericXmlMetadata implements IL2hDatastripMetadata  {

    private static class L2hDatastripMetadataPSD13Parser extends XmlMetadataParser<L2hDatastripMetadataPSD13> {

        public L2hDatastripMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L2hPSD13Constants.getDatastripSchemaLocations());
            setSchemaBasePath(L2hPSD13Constants.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L2hDatastripMetadataPSD13 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L2hDatastripMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L2hDatastripMetadataPSD13Parser parser = new L2hDatastripMetadataPSD13Parser(L2hDatastripMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-2H_DataStrip_ID");
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    public L2hDatastripMetadataPSD13(String name) {
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
