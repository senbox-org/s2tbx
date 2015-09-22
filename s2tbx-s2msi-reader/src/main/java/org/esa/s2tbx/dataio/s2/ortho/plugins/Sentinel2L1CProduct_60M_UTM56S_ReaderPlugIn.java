
package org.esa.s2tbx.dataio.s2.ortho.plugins;

import org.esa.s2tbx.dataio.s2.ortho.S2OrthoProduct60MReaderPlugIn;

/**
 * Reader plugin for S2 MSI L1C over WGS84 / UTM Zone 56 S
 */
public class Sentinel2L1CProduct_60M_UTM56S_ReaderPlugIn extends S2OrthoProduct60MReaderPlugIn {

    @Override
    public String getEPSG() {
        return "EPSG:32756";
    }

}
