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
 * @author olafd, Tonio Fincke
 */
public class S2LutUtils {

    public static boolean isInsideLut(InputPixelData ipd, LookupTable s2Lut) {
        final boolean wvInside = ipd.wvCol >= s2Lut.getDimension(0).getMin() && ipd.wvCol <= s2Lut.getDimension(0).getMax();
        final boolean szaInside = ipd.geom.sza >= s2Lut.getDimension(2).getMin() && ipd.geom.sza <= s2Lut.getDimension(2).getMax();
        final boolean vzaInside = ipd.geom.vza >= s2Lut.getDimension(3).getMin() && ipd.geom.vza <= s2Lut.getDimension(3).getMax();
        final boolean raaInside = ipd.geom.razi >= s2Lut.getDimension(4).getMin() && ipd.geom.razi <= s2Lut.getDimension(4).getMax();

        return wvInside && szaInside && vzaInside && raaInside;
    }

    public static double getMaxAOT(InputPixelData ipd, double[] aot) {
        // todo: what to set here?? An initial value? AOD is what we want to retrieve?!?!??
        double lPath = getAtmosphericParameter(ipd, 4, 0, 0); // starting with aod=0.15
        double lPath0 = lPath;
        int iAot = -1;
        final double toaIrradiance = getAtmosphericParameter(ipd, 4, 0, 6);
        final double lToaTosa = getLToaTosa(toaIrradiance, ipd, 0);
        while (iAot < aot.length - 1 && lPath < lToaTosa) {
            lPath0 = lPath;
            iAot++;
            lPath = getAtmosphericParameter(ipd, iAot, 0, 0);
        }
        if (iAot == 0) {
            return 0.05;
        }
        if (lPath < lToaTosa) {
            return 1.2;
        }
        return aot[iAot - 1] + (aot[iAot] - aot[iAot - 1]) * (lToaTosa - lPath0) / (lPath - lPath0);
    }

    private static double getAtmosphericParameter(InputPixelData ipd, int aotIndex, int wavelengthIndex, int paramIndex) {
        return ipd.pixelLutSubset[aotIndex][wavelengthIndex][paramIndex];
    }

    private static double[][] getAtmosphericParameters(InputPixelData ipd, S2Lut s2Lut, double tau) {
        final FracIndex fracIndex = new FracIndex();
        LookupTable.computeFracIndex(s2Lut.getDimension(1), tau, fracIndex);
        int numWavelengths = ipd.nSpecWvl;
        int numParams = s2Lut.getDimension(s2Lut.getDimensionCount() - 1).getSequence().length;
        double[][] atmosphericParameters = new double[numWavelengths][numParams];
        for (int i_wvl = 0; i_wvl < numWavelengths; i_wvl++) {
            for (int i_param = 0; i_param < numParams; i_param++) {
                atmosphericParameters[i_wvl][i_param] =
                        ipd.pixelLutSubset[fracIndex.i][i_wvl][i_param] * fracIndex.f +
                                ipd.pixelLutSubset[fracIndex.i + 1][i_wvl][i_param] * (1. - fracIndex.f);
            }
        }
        return atmosphericParameters;
    }


    public static double[][][] getLutSubset(S2Lut s2Lut, int[] wvlIndexes, double wv, double sza, double vza,
                                            double raa, double altitude) {
        final double aerosolType = 3.0; // get started with this

        final double[] coordinates = new double[]{wv, sza, vza, raa, altitude, aerosolType};

        final FracIndex[] fracIndexes = FracIndex.createArray(coordinates.length);
        final IntervalPartition[] partitions = s2Lut.getDimensions();

        int index = 0;
        for (int i = 0; i < 6; i++) {
            if (index == 1) {
                index++;
            }
            LookupTable.computeFracIndex(partitions[index], coordinates[i], fracIndexes[i]);
            index++;
        }
        return s2Lut.getAotWvlAndACValues(fracIndexes, wvlIndexes);
    }

    public static double getRayPhaseFunc(double sza, double vza, double raa) {
//      # eq. 6-12
        final double radSza = Math.toRadians(sza);
        final double radVza = Math.toRadians(vza);
        final double cosScattAngle = Math.cos(radSza) * Math.cos(radVza) +
                Math.sin(radSza) * Math.sin(radVza) * Math.cos(Math.toRadians(raa));
        return 0.75 * (1.0 - cosScattAngle * cosScattAngle);
    }

    public static void getSdrAndDiffuseFrac(InputPixelData ipd, S2Lut s2Lut, double tau) {
        // todo: ugly that void is returned here

        Guardian.assertNotNull("InputPixelData.diffuseFrac[][]", ipd.diffuseFrac);
        Guardian.assertNotNull("InputPixelData.surfReflec[][]", ipd.surfReflec);

        final double sza = ipd.geom.sza;
        final double cosineOfSZA = Math.cos(Math.toRadians(sza));
//      # eq. 6-4
        final double tauStratAeroSun = 1.0;

        double[][] atmosphericParameters = getAtmosphericParameters(ipd, s2Lut, tau);

        for (int iWvl = 0; iWvl < ipd.nSpecWvl; iWvl++) {
            // "path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
            // "view_trans_dir", "sun_trans_dir", "toa_irradiance"
            final double lPath0 = atmosphericParameters[iWvl][0];
            final double viewTransDiff = atmosphericParameters[iWvl][1];
            final double spherAlb = atmosphericParameters[iWvl][2];
            final double eg0 = atmosphericParameters[iWvl][3];
            final double viewTransDir = atmosphericParameters[iWvl][4];
            final double sunTransDir = atmosphericParameters[iWvl][5];
            double toaIrradiance = atmosphericParameters[iWvl][6];
            toaIrradiance /= 10000;
            toaIrradiance *= cosineOfSZA;

            final double tupTdown = viewTransDiff + viewTransDir; // GK

            final double tauRaySun = ipd.tauRaySun[iWvl];
            final double tauOzoneSun = ipd.tauOzoneSun[iWvl];
//           # eq. 6-14
            final double tosaIrradiance = toaIrradiance * tauRaySun * tauOzoneSun * tauStratAeroSun;

            final double lToaTosa = getLToaTosa(toaIrradiance, ipd, iWvl);

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
            final double eDiff = eg0 / (1.0 - S2AerosolConstants.RHO_REFERENCE * spherAlb) -
                    tosaIrradiance * sunTransDir * Math.cos(Math.toRadians(sza));

            final double eg = eg0 / (1.0 - S2AerosolConstants.RHO_REFERENCE * spherAlb);

            ipd.surfReflec[0][iWvl] = Math.PI * (lToaTosa - lPath0) /
                    (Math.PI * spherAlb * (lToaTosa - lPath0) + eg0 * tupTdown);

            // MomoLut doc says: ratio diff / total downward radiation
            // now GK derived this term from formulas above as: e_diff/e_g
            ipd.diffuseFrac[0][iWvl] = eDiff / eg;
        }
    }

    private static double getLToaTosa(double toaIrradiance, InputPixelData ipd, int iWvl) {
        final double toaIrradianceToPathToaTosa = ipd.toaIrradianceToPathToToaTosa[iWvl];
        final double lToa = ipd.lToa[iWvl];
        final double tauOzoneStratAeroView = ipd.tauOzoneStratAeroView[iWvl];
        final double tauRayOzoneStratAeroView = ipd.tauRayOzoneStratAeroView[iWvl];
//            # eq. 6-13
        final double lPathToaTosa = toaIrradiance * toaIrradianceToPathToaTosa;
//            # eq. 6-15
        final double lToaTosa = (lToa - lPathToaTosa * tauOzoneStratAeroView) / tauRayOzoneStratAeroView;
        return lToaTosa;
    }

    public static double getDistanceCorr(int doy) {
        final double gamma = 2.0 * Math.PI * (doy - 1) / 365.0;
        return 1.000110 + 0.034221 * Math.cos(gamma) + 0.001280 * Math.sin(gamma) +
                0.000719 * Math.cos(2.0 * gamma) + 0.000077 * Math.sin(2.0 * gamma);
    }

    public static double convertReflToRad(double refl, int iWvl, double sza, double julianDay) {
        // basically follows S2 AC Python implementation:
        final double dt = 1.0 / Math.pow(1 - 0.01673 * Math.cos(0.0172 * (julianDay - 2)), 2);
        final double solarIrrad = S2IdepixConstants.S2_SOLAR_IRRADIANCES[iWvl];
        final double conversionFactor = solarIrrad * dt * Math.cos(sza * MathUtils.DTOR) / (10000 * Math.PI);

        return refl * conversionFactor;
    }
}
