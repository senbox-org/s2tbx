package org.esa.s2tbx.dataio.s2.l2a;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2ReaderFactory;

/**
 * @author Nicolas Ducoin
 */
public class S2L2AReaderFactory extends S2ReaderFactory {

    @Override
    public S2Config createS2Config() {
        return new S2L2AConfig();
    }
}
