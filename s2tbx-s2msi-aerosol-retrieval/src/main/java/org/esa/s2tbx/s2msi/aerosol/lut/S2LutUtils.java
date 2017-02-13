package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.s2tbx.s2msi.aerosol.InputPixelData;
import org.esa.s2tbx.s2msi.aerosol.S2AerosolConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.IntervalPartition;
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

    private final static double OZONE_STANDARD = 0.33176;
    private final static double PRESSURE_STANDARD = 1013.25;
    private final static double[] OZONE_ABSORPTION_COEFFICENTS_PER_S2_BAND =
            new double[]{0.0024, 0.021, 0.103, 0.0525, 0.0205, 0.01, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

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
        final double altitude = ipd.elevation / 1000.0;
        final double aerosolType = 3.0; // get started with this

        final double wvl_0 = S2LutConstants.dimValues[10][0];
        final double rhoPathLutResultIndex = 1.0;

        final FracIndex[] fracIndices = FracIndex.createArray(9);
        final IntervalPartition[] partitions = s2Lut.getDimensions();
        final double[] coordinates = new double[]{wv, aod, sza, vza, raa, altitude, aerosolType, wvl_0, rhoPathLutResultIndex};

        for (int i = 0; i < 9; i++) {
            LookupTable.computeFracIndex(partitions[i], coordinates[i], fracIndices[i]);
        }

        double[] v = new double[1 << fracIndices.length];
        double lPath = s2Lut.getValue(fracIndices, v);
        // lPath = lPath * Math.PI / Math.cos(Math.toRadians(sza));      // TODO: GK to check if this factor is correct
        double lPath0 = lPath;
        int iAot = -1;
        while (iAot < aot.length - 1 && lPath < ipd.toaReflec[0]) {
            lPath0 = lPath;
            iAot++;
            fracIndices[1].i = iAot;
            fracIndices[1].f = 0.0;
            lPath = s2Lut.getValue(fracIndices, v);
            // lPath = lPath * Math.PI / Math.cos(Math.toRadians(sza));  // TODO: GK to check if this factor is correct (s.a.)
        }
        if (iAot == 0) {
            return 0.05;
        }
        if (lPath < ipd.toaReflec[0]) {
            return 1.2;
        }
        return aot[iAot - 1] + (aot[iAot] - aot[iAot - 1]) * (ipd.toaReflec[0] - lPath0) / (lPath - lPath0);
    }

    public static void getSdrAndDiffuseFrac(InputPixelData ipd, LookupTable s2Lut, double julianDay, double distanceCorrection, double tau) {
        // todo: ugly that void is returned here

        Guardian.assertNotNull("InputPixelData.diffuseFrac[][]", ipd.diffuseFrac);
        Guardian.assertNotNull("InputPixelData.surfReflec[][]", ipd.surfReflec);

        final double wv = ipd.wvCol;
        final double sza = ipd.geom.sza;
        final double vza = ipd.geom.vza;
        final double raa = ipd.geom.razi;
        final double ozone = ipd.ozone / 0.0214144;
        final double surfPressure = ipd.surfPressure / 100.;
        final double altitude = ipd.elevation / 1000.0;
        final double aerosolType = 3.0; // get started with this

        // from S2 LUT we get for wvl=1,..,13:
        // "path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
        // "view_trans_dir", "sun_trans_dir", "toa_irradiance"

        final double cosineOfSZA = Math.cos(Math.toRadians(sza));
        final double cosineOfVZA = Math.cos(Math.toRadians(vza));
//            # Ozone:
//            # eq. 6-1
//            m_corr_ozone = 0      // later
//            final double mCorrOzone = 0.0;
        final double airMassOzoneCorrection = ozone - OZONE_STANDARD;
        //            # eq. 6-9
        final double mCorrRay = (surfPressure - PRESSURE_STANDARD) / PRESSURE_STANDARD;
//            # Stratospheric aerosol:
//            # eq. 6-4
//            # if AOD < 0.03, we don't do anything -->
//            tau_strat_aero_v = 1.0
        final double tauStratAeroView = 1.0;
        final double tauStratAeroSun = 1.0;
//            # eq. 6-12
        final double cosScattAngle = cosineOfSZA * cosineOfVZA +
                Math.sin(Math.toRadians(sza)) * Math.sin(Math.toRadians(vza)) * Math.cos(Math.toRadians(raa));
        final double rayPhaseFunc = 0.75 * (1.0 - cosScattAngle * cosScattAngle);

        final FracIndex[] fracIndexes = FracIndex.createArray(8);
        final IntervalPartition[] partitions = s2Lut.getDimensions();
        final double[] coordinates = new double[]{wv, tau, sza, vza, raa, altitude, aerosolType};

        for (int i = 0; i < 7; i++) {
            LookupTable.computeFracIndex(partitions[i], coordinates[i], fracIndexes[i]);
        }

        for (int iWvl = 0; iWvl < ipd.nSpecWvl; iWvl++) {
            LookupTable.computeFracIndex(partitions[7], ipd.specWvl[iWvl], fracIndexes[7]);
            final double[] atmosphericParameters = s2Lut.getValues(fracIndexes);
            // "path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
            // "view_trans_dir", "sun_trans_dir", "toa_irradiance"
            final double lPath0 = atmosphericParameters[0];
            final double viewTransDiff = atmosphericParameters[1];
            final double spherAlb = atmosphericParameters[2];
            final double eg0 = atmosphericParameters[3];
            final double viewTransDir = atmosphericParameters[4];
            final double sunTransDir = atmosphericParameters[5];
            double toaIrradiance = atmosphericParameters[6];


            // lPath = lPath * Math.PI / Math.cos(Math.toRadians(sza));   // TODO: GK to check if this factor is correct (s.a.)
            final double tupTdown = viewTransDiff + viewTransDir; // GK

            final double reflToa = ipd.getToaReflec()[iWvl];

            final double lToa = convertReflToRad(reflToa, iWvl, sza, julianDay) / distanceCorrection;
            ;  // convert to radiance

            final double absorptionCoefficient = OZONE_ABSORPTION_COEFFICENTS_PER_S2_BAND[iWvl];

            // todo: implement here as in Python S2 AC, radToa is l_toa below:
//            e_s *= cos_sza
            toaIrradiance /= 10000;
            toaIrradiance *= cosineOfSZA;
//
//            # Ozone:
//            # eq. 6-2
            final double tauOzoneSun = Math.exp(-(absorptionCoefficient * airMassOzoneCorrection / cosineOfSZA));
//            # eq. 6-3
//            tau_ozone_v = 1.0
//            final double tauOzoneV = 1.0;
            final double tauOzoneView = Math.exp(-(absorptionCoefficient * airMassOzoneCorrection / cosineOfVZA));
////            # Stratospheric aerosol:

//            m_corr_ray = 0
//            final double mCorrRay = 0.0;
//            # eq. 6-10
//            k_ray = 0.008375 * np.power(wvl, -4.08)  # wvl for given band in microns
            final double kRay = 0.008375 * Math.pow(ipd.specWvl[iWvl], -4.08);
//            # k_ray = 0.008375 * wvl  # wvl for given band in microns
            final double tauRaySun = Math.exp(-(0.5 * kRay * mCorrRay / cosineOfSZA));
//            tau_ray_s = 1.0
//            final double tauRayS = 0.0;
//            # eq. 6-11
            final double tauRayView = Math.exp(-(0.5 * kRay * mCorrRay / cosineOfVZA));
//            tau_ray_v = 1.0
//            final double tauRayV = 1.0;

//
//            # eq. 6-13
//            l_path_toa_tosa = (e_s * k_ray * m_corr_ray * tau_ray_s * ray_phase_func) / (
//                    4.0 * np.pi * cos_sza * cos_vza)
            final double lPathToaTosa = (toaIrradiance * kRay * mCorrRay * tauRaySun * rayPhaseFunc) /
                    (4.0 * Math.PI * cosineOfSZA * cosineOfVZA);
//                    (4.0 * Math.PI * Math.cos(Math.toRadians(sza)) * Math.cos(Math.toRadians(vza)));
//
//            # eq. 6-14
            final double tosaIrradiance = toaIrradiance * tauRaySun * tauOzoneSun * tauStratAeroSun;

//            # eq. 6-15
//            l_toa_tosa = (l_toa - l_path_toa_tosa * tau_ozone_v * tau_strat_aero_v) / (
//                    tau_ray_v * tau_ozone_v * tau_strat_aero_v)
            final double lToaTosa = (lToa - lPathToaTosa * tauOzoneView * tauStratAeroView) / (tauRayView * tauOzoneView * tauStratAeroView);
//
//            # eq. 6-17
//            # first line
//            # just the terms of E_g
//
//            # second line
//            # --> b=0: shadow, 1: no shadow on pixel
//            b = 1  # for the moment
//            e_dir_star = b * e_s * sun_trans_dir * cos_beta    // sun_trans_dir from LUT, cos_beta = cos(sza)
//            final double eDirStar = toaIrradiance * sunTransDir * Math.cos(Math.toRadians(sza));
//
//            # third line
//            # e_diff = e_g_0 / (1.0 - rho_reference * sph_alb) - e_s * sun_trans_dir * cos_sza  # NOT in ATBD, given by GK
//            # RHO_REFERENCE = 0.15:
//            e_diff = e_g_0 / (
//                    1.0 - RHO_REFERENCE * sph_alb) - e_s * sun_trans_dir * cos_sza  # NOT in ATBD, given by GK
            final double eDiff = eg0 / (1.0 - S2AerosolConstants.RHO_REFERENCE * spherAlb) -
                    tosaIrradiance * sunTransDir * Math.cos(Math.toRadians(sza));

            // e_g = e_g_0 / (1.0 - RHO_REFERENCE * sph_alb)
            final double eg = eg0 / (1.0 - S2AerosolConstants.RHO_REFERENCE * spherAlb);

            ipd.surfReflec[0][iWvl] = Math.PI * (lToaTosa - lPath0) / (Math.PI * spherAlb * (lToaTosa - lPath0) + eg0 * tupTdown);
//            ipd.surfReflec[0][iWvl] = Math.PI * (radToa - lPath) / (Math.PI * spherAlb * (radToa - lPath) + eg0 * tupTdown);

//            ipd.diffuseFrac[0][iWvl] = 1.0 - lutValues[iWvl][3];
//            ipd.diffuseFrac[0][iWvl] = 1.0; // get started with this
            // MomoLut doc says: ratio diff / total downward radiation
            // now GK derived this term from formulas above as: e_diff/e_g
            ipd.diffuseFrac[0][iWvl] = eDiff / eg;
        }
    }

    public static double getDistanceCorr(int doy) {
        final double gamma = 2.0 * Math.PI * (doy - 1) / 365.0;
        return 1.000110 + 0.034221 * Math.cos(gamma) + 0.001280 * Math.sin(gamma) +
                0.000719 * Math.cos(2.0 * gamma) + 0.000077 * Math.sin(2.0 * gamma);
    }

    private static double convertReflToRad(double refl, int iWvl, double sza, double julianDay) {
        // basically follows S2 AC Python implementation:
        final double dt = 1.0 / Math.pow(1 - 0.01673 * Math.cos(0.0172 * (julianDay - 2)), 2);
        final double solarIrrad = S2IdepixConstants.S2_SOLAR_IRRADIANCES[iWvl];
        final double conversionFactor = solarIrrad * dt * Math.cos(sza * MathUtils.DTOR) / (10000 * Math.PI);

        return refl * conversionFactor;
    }
}
