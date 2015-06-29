package org.esa.s2tbx.dataio.s2;

import org.esa.snap.framework.dataio.AbstractProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;

/**
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2ProductReader  extends AbstractProductReader {

    private S2Config config;


    public Sentinel2ProductReader(ProductReaderPlugIn readerPlugIn, S2Config config) {
        super(readerPlugIn);

        this.config = config;
    }

    public S2Config getConfig() {
        return config;
    }
}
