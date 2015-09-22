
package org.esa.s2tbx.dataio.s2.ortho.plugins;

import org.esa.s2tbx.dataio.s2.ortho.S2OrthoProduct20MReaderPlugIn;

/**
 * Reader plugin for S2 MSI L1C over WGS84 / UTM Zone 36 N
 */
public class Sentinel2L1CProduct_20M_UTM36N_ReaderPlugIn extends S2OrthoProduct20MReaderPlugIn {

    @Override
    public String getEPSG()
    {
        return "EPSG:32636";
    }

}
