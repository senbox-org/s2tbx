package org.esa.s2tbx.dataio.s2.l1c.metadata;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l1c.L1cMetadata;
import org.esa.s2tbx.dataio.s2.ortho.AbstractS2OrthoMetadataReader;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoMetadata;
import org.esa.snap.core.util.SystemUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class S2L1cProductMetadataReader extends AbstractS2OrthoMetadataReader {

    public S2L1cProductMetadataReader(VirtualPath virtualPath, String epsgCode) throws IOException {
        super(virtualPath, epsgCode);
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
                SystemUtils.LOG.warning("Invalid resolution: " + resolution);
                bandNames = null;
                break;
        }

        return bandNames;
    }

    protected S2OrthoMetadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsgCode, boolean isGranule) throws IOException {
        try {
            return L1cMetadata.parseHeader(path, granuleName, config, epsgCode, isGranule, this.namingConvention);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to parse metadata in " + path.getFileName().toString(), e);
        }
    }

}
