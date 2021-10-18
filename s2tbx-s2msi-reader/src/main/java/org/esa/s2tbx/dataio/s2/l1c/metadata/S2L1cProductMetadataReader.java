package org.esa.s2tbx.dataio.s2.l1c.metadata;

import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.ortho.metadata.AbstractS2OrthoMetadataReader;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
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
                //bandNames = new String[]{"B02", "B03", "B04", "B08"};
                bandNames = new String[4];
                bandNames[0] = S2BandConstants.B2.getFilenameBandId();
                bandNames[1] = S2BandConstants.B3.getFilenameBandId();
                bandNames[2] = S2BandConstants.B4.getFilenameBandId();
                bandNames[3] = S2BandConstants.B8.getFilenameBandId();
                break;
            case R20M:
                //bandNames = new String[]{"B05", "B06", "B07", "B8A", "B11", "B12"};
                bandNames = new String[6];
                bandNames[0] = S2BandConstants.B5.getFilenameBandId();
                bandNames[1] = S2BandConstants.B6.getFilenameBandId();
                bandNames[2] = S2BandConstants.B7.getFilenameBandId();
                bandNames[3] = S2BandConstants.B8A.getFilenameBandId();
                bandNames[4] = S2BandConstants.B11.getFilenameBandId();
                bandNames[5] = S2BandConstants.B12.getFilenameBandId();
                break;
            case R60M:
                //bandNames = new String[]{"B01", "B09", "B10"};
                bandNames = new String[3];
                bandNames[0] = S2BandConstants.B1.getFilenameBandId();
                bandNames[1] = S2BandConstants.B9.getFilenameBandId();
                bandNames[2] = S2BandConstants.B10.getFilenameBandId();
                break;
            case R15M:
                // SystemUtils.LOG.warning("None resolution " + resolution+" for this product.");
                bandNames = null;
                break;
            case R30M:
                // SystemUtils.LOG.warning("None resolution " + resolution+" for this product.");
                bandNames = null;
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
