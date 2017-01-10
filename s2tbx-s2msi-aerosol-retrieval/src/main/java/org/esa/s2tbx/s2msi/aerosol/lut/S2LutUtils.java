package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.s2tbx.s2msi.aerosol.InputPixelData;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.math.LookupTable;
import org.esa.snap.core.util.math.MathUtils;

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
        final boolean wvInside = ipd.wvCol >= s2Lut.getDimension(0).getMin() && ipd.wvCol <= s2Lut.getDimension(0).getMax();
        final boolean szaInside = ipd.geom.sza >= s2Lut.getDimension(2).getMin() && ipd.geom.sza <= s2Lut.getDimension(2).getMax();
        final boolean vzaInside = ipd.geom.vza >= s2Lut.getDimension(3).getMin() && ipd.geom.vza <= s2Lut.getDimension(3).getMax();
        final boolean raaInside = ipd.geom.razi >= s2Lut.getDimension(4).getMin() && ipd.geom.razi <= s2Lut.getDimension(4).getMax();

        return wvInside && szaInside && vzaInside && raaInside;
    }

//    public static synchronized double getMaxAOT(InputPixelData ipd, LookupTable s2Lut, double[] aot) {
    public static double getMaxAOT(InputPixelData ipd, LookupTable s2Lut, double[] aot) {

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

        final double wv = ipd.wvCol;
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
        while (iAot < aot.length - 1 && rhoPath1 < ipd.toaReflec[0]) {
            rhoPath0 = rhoPath1;
            iAot++;
            final double aotLut = s2Lut.getDimension(1).getSequence()[iAot];
            rhoPath1 = s2Lut.getValue(wv, aotLut, sza, vza, raa, altitude, at, wvl_0, rhoPathLutResultIndex);
            rhoPath1 = rhoPath1 * Math.PI / Math.cos(Math.toRadians(sza));
        }
        if (iAot == 0) {
            return 0.05;
        }
        if (rhoPath1 < ipd.toaReflec[0]) {
            return 1.2;
        }
        return aot[iAot - 1] + (aot[iAot] - aot[iAot - 1]) * (ipd.toaReflec[0] - rhoPath0) / (rhoPath1 - rhoPath0);
    }

//    public static synchronized void getSdrAndDiffuseFrac(InputPixelData ipd, LookupTable s2Lut, double julianDay, double tau) {
    public static void getSdrAndDiffuseFrac(InputPixelData ipd, LookupTable s2Lut, double julianDay, double tau) {
        // todo: ugly that void is returned here

        Guardian.assertNotNull("InputPixelData.diffuseFrac[][]", ipd.diffuseFrac);
        Guardian.assertNotNull("InputPixelData.surfReflec[][]", ipd.surfReflec);

        final double wv = ipd.wvCol;
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
        final double globalIrradianceResultIndex = 4.0;
        final double viewTransDirResultIndex = 5.0;

        for (int iWvl = 0; iWvl < ipd.nSpecWvl; iWvl++) {
            double lPath = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, rhoPathLutResultIndex);
            lPath = lPath * Math.PI / Math.cos(Math.toRadians(sza));
            double viewTransDiff = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, viewTransDiffResultIndex);
            double viewTransDir = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, viewTransDirResultIndex);
            final double tupTdown = viewTransDiff + viewTransDir; // GK
            final double spherAlb = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, sphericalAlbedoResultIndex);
            final double eg0 = s2Lut.getValue(wv, tau, sza, vza, raa, altitude, at, iWvl, globalIrradianceResultIndex);

            final double lToa = ipd.getToaReflec()[iWvl];
            final double radToa = convertReflToRad(lToa, iWvl, sza, julianDay);  // convert to radiance
            ipd.surfReflec[0][iWvl] = Math.PI * (radToa - lPath) / (Math.PI * spherAlb * (radToa - lPath) + eg0 * tupTdown);

//            ipd.diffuseFrac[0][iWvl] = 1.0 - lutValues[iWvl][3];
            ipd.diffuseFrac[0][iWvl] = 1.0; // get started with this todo: clarify with GK what lutValues[iWvl][3] is
            // MomoLut doc says: ratio diff / total downward radiation
            // todo: GK to derive this term
        }
    }

    private static double convertReflToRad(double refl, int iWvl, double sza, double julianDay) {
        // basically follows S2 AC Python implementation:
        final double dt = 1.0 / Math.pow(1 - 0.01673 * Math.cos(0.0172 * (julianDay - 2)), 2);
        final double solarIrrad = S2IdepixConstants.S2_SOLAR_IRRADIANCES[iWvl];
        final double conversionFactor = solarIrrad * dt * Math.cos(sza* MathUtils.DTOR) / (10000 * Math.PI);

        return refl * conversionFactor;
    }
}
