package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.s2tbx.s2msi.aerosol.InputPixelData;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.math.LookupTable;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 21.12.2016
 * Time: 16:50
 *
 * @author olafd
 */
public class S2LutUtils {

    public static boolean isInsideLut(InputPixelData ipd, LookupTable s2Lut) {
        // TODO: 21.12.2016
//        return (ipd.geom.vza >= lutLimits.get(DimSelector.VZA).min)
//                && (ipd.geom.vza <= lutLimits.get(DimSelector.VZA).max)
//                && (ipd.geom.sza >= lutLimits.get(DimSelector.SZA).min)
//                && (ipd.geom.sza <= lutLimits.get(DimSelector.SZA).max)
//                && (ipd.geom.razi >= lutLimits.get(DimSelector.AZI).min)
//                && (ipd.geom.razi <= lutLimits.get(DimSelector.AZI).max);
        return true;
    }

    public static synchronized double getMaxAOT(InputPixelData ipd, LookupTable s2Lut, double[] aot) {
//        final float geomAMF = (float) ((1 / Math.cos(Math.toRadians(sza))
//                + 1 / Math.cos(Math.toRadians(ipd.geom.vza))));
//        final double[] gasT = getGasTransmission(geomAMF, (float) ipd.wvCol, (float) (ipd.o3du / 1000));
        final double[] gasT = new double[]{1.0}; // todo: define how to derive; why do we need a full array here?
        final double toa = ipd.toaReflec[0] / gasT[0];

        // input required:
        //        "water_vapour": [500,.., 5000],
        //        "aerosol_depth": [0.05,.., 1.2],
        //        "sun_zenith_angle": [0,.., 70],
        //        "view_zenith_angle": [0,.., 60],
        //        "relative_azimuth": [0,.., 180],
        //        "altitude": [0.0,.., 4.0],
        //        "aerosol_type": ["___rural", "maritime", "___urban", "__desert"],
        //        "model_type": ["MidLatitudeSummer"],
        //        "ozone_content": [0.33176],
        //        "co2_mixing_ratio": [380],
        //        "wavelengths": [0.443,.., 2.19],

        // LUT result vector:
        // ["path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
        //  "view_trans_dir", "sun_trans_dir", "toa_irradiance"]
        // --> result indices 1.0,..,7.0

        final double wv = 1000.0;  // todo: where to get from?
        final double aod = 0.15;   // todo: what to set here?? An initial value? AOD is what we want to retrieve?!?!??
        final double sza = ipd.geom.sza;
        final double vza = ipd.geom.vza;
        final double raa = ipd.geom.razi;
        final double altitude = 1.0; // todo: get from Idepix product
        final double at = 0.0; // get started with this

        final double wvl_0 = S2LutConstants.dimValues[10][0];
        final double rhoPathLutResultIndex = 1.0;
        double rhoPath1 = s2Lut.getValue(wv, aod, sza, vza, raa, altitude, at, wvl_0, rhoPathLutResultIndex);
        rhoPath1 = rhoPath1 * Math.PI / Math.cos(Math.toRadians(sza));
        double rhoPath0 = rhoPath1;
        int iAot = 0;
        while (iAot < aot.length - 1 && rhoPath1 < toa) {
            rhoPath0 = rhoPath1;
            iAot++;
            double wvl = S2LutConstants.dimValues[10][iAot];
            rhoPath1 = s2Lut.getValue(wv, aod, sza, vza, raa, altitude, at, wvl, rhoPathLutResultIndex);
            rhoPath1 = rhoPath1 * Math.PI / Math.cos(Math.toRadians(sza));
        }
        if (iAot == 0) {
            return 0.05;
        }
        if (rhoPath1 < toa) {
            return 1.2;
        }
        return aot[iAot - 1] + (aot[iAot] - aot[iAot - 1]) * (toa - rhoPath0) / (rhoPath1 - rhoPath0);
    }

    public static synchronized void getSdrAndDiffuseFrac(InputPixelData ipd, LookupTable s2Lut, double tau) {
        // todo: ugly that void is returned here

        Guardian.assertNotNull("InputPixelData.diffuseFrac[][]", ipd.diffuseFrac);
        Guardian.assertNotNull("InputPixelData.surfReflec[][]", ipd.surfReflec);
        final double[] toaR = ipd.toaReflec;
//        final float geomAMF = (float) ((1 / Math.cos(Math.toRadians(ipd.geom.sza))
//                + 1 / Math.cos(Math.toRadians(ipd.geom.vza))));
//            final double[] gasT = getGasTransmission(geomAMF, (float) ipd.wvCol, (float) (ipd.o3du / 1000));
        final int nWvl = S2LutConstants.dimValues[10].length;
        final double[] gasT = new double[nWvl];
        for (int i = 0; i < nWvl; i++) {
            gasT[i] = 1.0; // todo: how to derive?
        }

        final double wv = 1000.0;  // todo: where to get from?
        final double sza = ipd.geom.sza;
        final double vza = ipd.geom.vza;
        final double raa = ipd.geom.razi;
        final double altitude = 1.0; // todo: get from Idepix product
        final double at = 0.0; // get started with this

        // from S2 LUT we get for wvl=1,..,13:
        // "path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
        // "view_trans_dir", "sun_trans_dir", "toa_irradiance"

        final double rhoPathLutResultIndex = 1.0;
        final double viewTransDiffResultIndex = 2.0;
        final double sphericalAlbedoResultIndex = 3.0;
        final double viewTransDirResultIndex = 5.0;

        for (int iWvl = 0; iWvl < ipd.nSpecWvl; iWvl++) {
            double rhoPath = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, rhoPathLutResultIndex);
            rhoPath = rhoPath * Math.PI / Math.cos(Math.toRadians(sza));
            double viewTransDiff = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, viewTransDiffResultIndex);
            double viewTransDir = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, viewTransDirResultIndex);
            final double tupTdown = viewTransDiff + viewTransDir; // GK
            final double spherAlb = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, sphericalAlbedoResultIndex);
//            double rhoPath = lutValues[iWvl][0] * Math.PI / Math.cos(Math.toRadians(ipd.geom.sza)); // path_radiance --> OK
//            double tupTdown = lutValues[iWvl][1] / Math.cos(Math.toRadians(ipd.geom.sza));          // tupTdown = view_trans_diff + view_trans_dir
//            double spherAlb = lutValues[iWvl][2];                                               // spherical_albedo --> OK
            //double tgO3 = Math.exp(ipd.o3du * o3corr[i] * geomAMF/2); // my o3 correction scheme uses AMF=SC/VC not AMF=SC
            double toaCorr = toaR[iWvl] / gasT[iWvl];       // todo: see above, not available from S2 LUT. Discuss with GK what to do here.
            double a = (toaCorr - rhoPath) / tupTdown;
            ipd.surfReflec[0][iWvl] = a / (1 + spherAlb * a);
//            ipd.diffuseFrac[0][iWvl] = 1.0 - lutValues[iWvl][3];
            ipd.diffuseFrac[0][iWvl] = 1.0; // get started with this todo: ask GK what lutValues[iWvl][3] is
        }
    }

}
