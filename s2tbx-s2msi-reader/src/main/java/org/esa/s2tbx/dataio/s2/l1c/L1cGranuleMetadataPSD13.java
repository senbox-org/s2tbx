package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * Created by obarrile on 30/09/2016.
 */
public class L1cGranuleMetadataPSD13 extends XmlMetadata implements IL1cGranuleMetadata {


    MetadataElement metadataElement;

    private static class L1cGranuleMetadataPSD13Parser extends XmlMetadataParser<L1cGranuleMetadataPSD13> {

        public L1cGranuleMetadataPSD13Parser(Class metadataFileClass) {
            super(metadataFileClass);
            setSchemaLocations(L1cMetadataPSD13Helper.getSchemaLocations());
        }


        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static L1cGranuleMetadataPSD13 create(Path path) throws IOException {
        Assert.notNull(path);
        L1cGranuleMetadataPSD13 result = new L1cGranuleMetadataPSD13("ProductMetadata");
        InputStream stream = null;
        try {
            if (Files.exists(path)) {
                stream = Files.newInputStream(path, StandardOpenOption.READ);
                //noinspection unchecked
                L1cGranuleMetadataPSD13Parser parser = new L1cGranuleMetadataPSD13Parser(L1cGranuleMetadataPSD13.class);
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


    public L1cGranuleMetadataPSD13(String name) {
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
    public S2Metadata.ProductCharacteristics getTileProductOrganization() {
        return null;
    }

    @Override
    public Map<S2SpatialResolution, L1cMetadata.TileGeometry> getTileGeometries() {
        return null;
    }

    @Override
    public L1cMetadata.AnglesGrid getSunGrid() {
        return null;
    }

    @Override
    public L1cMetadata.AnglesGrid[] getAnglesGrid() {
        return new L1cMetadata.AnglesGrid[0];
    }

    @Override
    public S2Metadata.MaskFilename[] getMasks(File file) {
        return new S2Metadata.MaskFilename[0];
    }

    @Override
    public MetadataElement getMetadataElement() {
        return null;
    }
}
