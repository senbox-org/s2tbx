package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by obarrile on 30/09/2016.
 */

public class L1cDatastripMetadataPSD13 extends XmlMetadata implements IL1cDatastripMetadata {

    MetadataElement metadataElement;

    private static class L1cDatastripMetadataPSD13Parser extends XmlMetadataParser<L1cGranuleMetadataPSD13> {

        public L1cDatastripMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cMetadataPSD13Helper.getSchemaLocations());
        }


        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cDatastripMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L1cDatastripMetadataPSD13 result = new L1cDatastripMetadataPSD13("ProductMetadata");
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L1cDatastripMetadataPSD13Parser parser = new L1cDatastripMetadataPSD13Parser(L1cDatastripMetadataPSD13.class);
                result.metadataElement = parser.parse(path, null);
                String metadataProfile = result.getMetadataProfile();
                if (metadataProfile != null)
                    result.setName(metadataProfile);
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

    public L1cDatastripMetadataPSD13(String name) {
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
        return null;
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
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return null;
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
        return null;
    }
}
