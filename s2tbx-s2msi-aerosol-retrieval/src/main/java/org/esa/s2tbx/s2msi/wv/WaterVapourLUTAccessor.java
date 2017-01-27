package org.esa.s2tbx.s2msi.wv;

import java.net.URL;

/**
 * @author Tonio Fincke
 */
class WaterVapourLUTAccessor {

    WaterVapourLUTAccessor() {
        init();
    }

    void init() {
        final URL pathToWVLut = WaterVapourLUTAccessor.class.getResource("water_vapour_lut_CIBR_9_8A.txt");
        final URL pathToWVDimsFile = WaterVapourLUTAccessor.class.getResource("wv_lut_v0.6.dims.jsn");



    }

}
