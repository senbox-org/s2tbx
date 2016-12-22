/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;

import java.util.HashMap;
import java.util.Map;

/**
 * Instrument specific constants
 *
 * @author olafd
 */
class InstrumentConsts {

    public static final String MSI_INSTRUMENT_NAME = "MSI";

    public static final String[] REFLEC_NAMES = S2IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES;

    public static final String[] GEOM_NAMES = {
            S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[0],
            S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[2],
            S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[1],
            S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[3]
    };

    public static final String IDEPIX_FLAG_BAND_NAME = "pixel_classif_flags";

    public static final String VALID_RETRIEVAL_EXPRESSION =
                        IDEPIX_FLAG_BAND_NAME + ".F_LAND "
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLEAR_SNOW "
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLOUD "   // ???
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLOUD_BUFFER "   // ???
            + " && (" + S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[0] + "<70)";

    public static final String VALID_AOT_OUT_EXPRESSION =
                    IDEPIX_FLAG_BAND_NAME + ".F_LAND "
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLEAR_SNOW "
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLOUD "   // ???
            + " && !" + IDEPIX_FLAG_BAND_NAME + ".F_CLOUD_BUFFER "   // ???
            + " && (" + S2IdepixConstants.S2_MSI_ANNOTATION_BAND_NAMES[0] + "<70)";

    public static final String SURFACE_PRESSURE_NAME = "surfPressEstimate";
    public static final String ELEVATION_NAME = "elevation";

    // todo: normalize these elsewhere, see old code
    public static final double[] FIT_WEIGHTS = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};

    public static final int N_LUT_BANDS = 13;


}
