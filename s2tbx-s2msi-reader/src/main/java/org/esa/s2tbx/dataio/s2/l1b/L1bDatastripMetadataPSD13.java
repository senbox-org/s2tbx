package org.esa.s2tbx.dataio.s2.l1b;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.GenericXmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2MetadataType;
import org.esa.snap.core.datamodel.MetadataElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by obarrile on 07/10/2016.
 */

public class L1bDatastripMetadataPSD13 extends GenericXmlMetadata implements IL1bDatastripMetadata {
    private static class L1bDatastripMetadataPSD13Parser extends XmlMetadataParser<L1bDatastripMetadataPSD13> {

        public L1bDatastripMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            String[] locations = {S2MetadataType.L1B_DATASTRIP_SCHEMA_FILE_PATH};
            setSchemaLocations(locations);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1bDatastripMetadataPSD13 create(Path path) throws IOException {


        Assert.notNull(path);
        L1bDatastripMetadataPSD13 result = null;
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L1bDatastripMetadataPSD13Parser parser = new L1bDatastripMetadataPSD13Parser(L1bDatastripMetadataPSD13.class);
                result = parser.parse(stream);
                result.setName("Level-1B_DataStrip_ID");
            }
        } catch (Exception e) {
            //Logger.getLogger(GenericXmlMetadata.class.getName()).severe(e.getMessage());
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (IOException e) {
                // swallowed exception
            }
        }
        return result;
    }

    public L1bDatastripMetadataPSD13(String name) {
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
