
package org.esa.s2tbx.dataio.s2.ortho.plugins;

import org.esa.s2tbx.dataio.s2.ortho.S2OrthoProduct10MReaderPlugIn;

/**
 * Reader plugin for S2 MSI L1C over WGS84 / UTM Zone 46 S
 */
public class Sentinel2L1CProduct_10M_UTM46S_ReaderPlugIn extends S2OrthoProduct10MReaderPlugIn {

    @Override
    public String getEPSG()
    {
        return "EPSG:32746";
    }

}
