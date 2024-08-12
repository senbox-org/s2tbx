package org.esa.s2tbx.dataio.s2.l1c.metadata;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l1c.L1cPSD150Constants;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by diana on 26/07/2024.
 */

public class L1cDatastripMetadataPSD150 extends GenericXmlMetadata implements IL1cDatastripMetadata {

    private static class L1cDatastripMetadataPSD150Parser extends XmlMetadataParser<L1cDatastripMetadataPSD150> {

        public L1cDatastripMetadataPSD150Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cPSD150Constants.getDatastripSchemaLocations());
            setSchemaBasePath(L1cPSD150Constants.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cDatastripMetadataPSD150 create(VirtualPath path) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L1cDatastripMetadataPSD150 result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L1cDatastripMetadataPSD150Parser parser = new L1cDatastripMetadataPSD150Parser(L1cDatastripMetadataPSD150.class);
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

    public L1cDatastripMetadataPSD150(String name) {
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
