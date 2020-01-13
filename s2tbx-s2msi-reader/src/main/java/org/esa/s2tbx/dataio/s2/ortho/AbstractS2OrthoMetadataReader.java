package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.metadata.AbstractS2ProductMetadataReader;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Objects;

public abstract class AbstractS2OrthoMetadataReader extends AbstractS2ProductMetadataReader {

    private final String epsgCode;

    protected AbstractS2OrthoMetadataReader(VirtualPath virtualPath, String epsgCode) throws IOException {
        super(virtualPath);

        this.epsgCode = epsgCode;
    }

    protected abstract S2Metadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsgCode, boolean isAGranule) throws IOException;

    @Override
    public S2Metadata readMetadataHeader(VirtualPath inputVirtualPath, S2Config config) throws IOException, ParserConfigurationException, SAXException {
        String granuleDirName = null;
        VirtualPath rootMetadataPath;

        // we need to recover parent metadata file if we have a granule
        boolean foundProductMetadata = true;
        if (isGranule()) {
            try {
                VirtualPath parentPath = inputVirtualPath.getParent();
                Objects.requireNonNull(parentPath);
                granuleDirName = parentPath.getFileName().toString();
            } catch (NullPointerException npe) {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", inputVirtualPath.getFileName().toString()));
            }

            rootMetadataPath = this.namingConvention.getInputProductXml();

            if (rootMetadataPath == null) {
                foundProductMetadata = false;
                rootMetadataPath = inputVirtualPath;
            }
        } else {
            rootMetadataPath = inputVirtualPath;
        }

        return parseHeader(rootMetadataPath, granuleDirName, config, this.epsgCode, !foundProductMetadata);
    }
}
