package org.esa.s2tbx.dataio.s2.l1c;

import java.util.Locale;

/**
 * Created by jmalik on 01/07/15.
 */
public class Sentinel2L1CProduct_20M_UTM31N_ReaderPlugIn extends Sentinel2L1CProduct20MReaderPlugIn {

    @Override
    public String getEPSG()
    {
        return "EPSG:32631";
    }

}
