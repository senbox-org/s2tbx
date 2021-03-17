package org.esa.s2tbx.dataio.s2.l2h.metadata;

import com.bc.ceres.core.Assert;
import org.apache.commons.io.IOUtils;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.MetadataElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by obarrile on 06/02/2018.
 */
public class L2hDatastripMetadataGenericPSD extends GenericXmlMetadata implements IL2hDatastripMetadata  {

    private static class L2hDatastripMetadataGenericPSDParser extends XmlMetadataParser<L2hDatastripMetadataGenericPSD> {

        public L2hDatastripMetadataGenericPSDParser(Class metadataFileClass, IL2hMetadataPathsProvider metadataPathProvider) {
            super(metadataFileClass);
            setSchemaLocations(metadataPathProvider.getDatastripSchemaLocations());
            setSchemaBasePath(metadataPathProvider.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L2hDatastripMetadataGenericPSD create(VirtualPath path, IL2hMetadataPathsProvider metadataPathProvider) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L2hDatastripMetadataGenericPSD result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L2hDatastripMetadataGenericPSDParser parser = new L2hDatastripMetadataGenericPSDParser(L2hDatastripMetadataGenericPSD.class, metadataPathProvider);
                result = parser.parse(stream);
                result.setName("Level-2H_DataStrip_ID");
                result.setMetadataPathsProvider(metadataPathProvider);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    private IL2hMetadataPathsProvider metadataPathProvider = null;

    private void setMetadataPathsProvider(IL2hMetadataPathsProvider metadataPathProvider) {
        this.metadataPathProvider = metadataPathProvider;
    }
    public L2hDatastripMetadataGenericPSD(String name) {
        super(name);
    }
    public L2hDatastripMetadataGenericPSD(String name, IL2hMetadataPathsProvider metadataPathProvider) {
        super(name);
        setMetadataPathsProvider(metadataPathProvider);
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
