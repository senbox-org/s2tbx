package org.esa.s2tbx.dataio.s2;

import org.esa.snap.framework.dataio.AbstractProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;

/**
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2ProductReader  extends AbstractProductReader {

    private S2Config config;

    private S2ReaderFactory readerFactory;

    public Sentinel2ProductReader(ProductReaderPlugIn readerPlugIn, S2ReaderFactory readerFactory) {
        super(readerPlugIn);

        this.readerFactory = readerFactory;
        this.config = readerFactory.getConfigInstance();
    }

    public S2Config getConfig() {
        return config;
    }
}
