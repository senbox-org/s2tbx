package org.esa.s2tbx.dataio.s2.l2a.metadata;

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
public class L2aDatastripMetadataGenericPSD extends GenericXmlMetadata implements IL2aDatastripMetadata  {

    private static class L2aDatastripMetadataGenericPSDParser extends XmlMetadataParser<L2aDatastripMetadataGenericPSD> {

        public L2aDatastripMetadataGenericPSDParser(Class metadataFileClass, IL2aMetadataPathsProvider metadataPathProvider) {
            super(metadataFileClass);
            setSchemaLocations(metadataPathProvider.getDatastripSchemaLocations());
            setSchemaBasePath(metadataPathProvider.getDatastripSchemaBasePath());
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L2aDatastripMetadataGenericPSD create(VirtualPath path, IL2aMetadataPathsProvider metadataPathProvider) throws IOException, ParserConfigurationException, SAXException {
        Assert.notNull(path);
        L2aDatastripMetadataGenericPSD result = null;
        InputStream stream = null;
        try {
            if (path.exists()) {
                stream = path.getInputStream();
                L2aDatastripMetadataGenericPSDParser parser = new L2aDatastripMetadataGenericPSDParser(L2aDatastripMetadataGenericPSD.class, metadataPathProvider);
                result = parser.parse(stream);
                result.setName("Level-2A_DataStrip_ID");
                result.setMetadataPathsProvider(metadataPathProvider);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    private IL2aMetadataPathsProvider metadataPathProvider = null;

    private void setMetadataPathsProvider(IL2aMetadataPathsProvider metadataPathProvider) {
        this.metadataPathProvider = metadataPathProvider;
    }
    public L2aDatastripMetadataGenericPSD(String name) {
        super(name);
    }
    public L2aDatastripMetadataGenericPSD(String name, IL2aMetadataPathsProvider metadataPathProvider) {
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
