package org.esa.s2tbx.dataio.s2.l1b.metadata;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.metadata.AbstractS2MetadataReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class L1bProductMetadataReader extends AbstractS2MetadataReader {

    public L1bProductMetadataReader(VirtualPath virtualPath) throws IOException {
        super(NamingConventionFactory.createL1BNamingConvention(virtualPath));
    }

    @Override
    protected String[] getBandNames(S2SpatialResolution resolution) {
        String[] bandNames;
        switch (resolution) {
            case R10M:
                bandNames = new String[]{"B02", "B03", "B04", "B08"};
                break;
            case R20M:
                bandNames = new String[]{"B05", "B06", "B07", "B8A", "B11", "B12"};
                break;
            case R60M:
                bandNames = new String[]{"B01", "B09", "B10"};
                break;
            default:
                bandNames = null;
                break;
        }
        return bandNames;
    }

    @Override
    public L1bMetadata readMetadataHeader(VirtualPath metadataPath, S2Config config) throws IOException, ParserConfigurationException, SAXException {
        VirtualPath productMetadataPath = null;
        String granuleFolderName = null;
        // we need to recover parent metadata file if we have a granule
        boolean foundProductMetadata = true;
        if (isGranule()) {
            VirtualPath parentMetadataPath = metadataPath.getParent();
            if (parentMetadataPath == null) {
                throw new NullPointerException("The parent metadata path is null.");
            }
            granuleFolderName = parentMetadataPath.getFileName().toString();
            VirtualPath rootMetadataPath = namingConvention.getInputProductXml();
            if (rootMetadataPath != null) {
                productMetadataPath = rootMetadataPath;
            }
            if (productMetadataPath == null) {
                foundProductMetadata = false;
                productMetadataPath = metadataPath;
            }
        } else {
            productMetadataPath = metadataPath;
        }
        if (!productMetadataPath.exists()) {
            throw new FileNotFoundException("File not found: " + productMetadataPath.getFullPathString());
        }

        return new L1bMetadata(productMetadataPath, granuleFolderName, config, foundProductMetadata, namingConvention);
    }
}
