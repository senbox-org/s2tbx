package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2ReaderFactory;

/**
 * @author nducoin
 */
public class S2L1CReaderFactory extends S2ReaderFactory {

    @Override
    protected S2Config createS2Config() {
        return new S2L1CConfig();
    }
}
