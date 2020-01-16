package org.esa.s2tbx.dataio.s2.l3;

import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l3.metadata.S2L3ProductMetadataReader;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;

import java.io.IOException;

/**
 * Created by obarrile on 15/06/2016.
 */
public class Sentinel2L3ProductReader  extends Sentinel2OrthoProductReader {

    static final String L3_CACHE_DIR = "l3-reader";

    public Sentinel2L3ProductReader(ProductReaderPlugIn readerPlugIn, String epsgCode) {
        super(readerPlugIn, epsgCode);
    }

    @Override
    protected S2L3ProductMetadataReader buildMetadataReader(VirtualPath virtualPath) throws IOException {
        return new S2L3ProductMetadataReader(virtualPath, this.epsgCode);
    }

    @Override
    protected String getReaderCacheDir() {
        return L3_CACHE_DIR;
    }

    @Override
    protected int getMaskLevel() {
        return MaskInfo.L3;
    }
}
