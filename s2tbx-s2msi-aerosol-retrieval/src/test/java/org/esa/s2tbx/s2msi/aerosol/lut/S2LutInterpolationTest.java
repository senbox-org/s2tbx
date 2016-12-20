package org.esa.s2tbx.s2msi.aerosol.lut;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.util.math.LookupTable;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author olafd
 */
public class S2LutInterpolationTest {

    private LookupTable snapS2Lut;

    @Before
    public void setUp() throws IOException {
        URL lutResource = S2LutAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d");
                // test shall be performed in local environment only, i.e. if we have the LUT available as test resource
        Assume.assumeTrue(lutResource != null);

        assert lutResource != null;
        final File lutFile = new File(lutResource.getPath());
        S2LutAccessor s2LutAccessor = new S2LutAccessor(lutFile);
        snapS2Lut = s2LutAccessor.readLut(ProgressMonitor.NULL);
    }

    @Test
    public void testS2LutDimensions() throws Exception {
//        "lutshape": [7, 17, 8, 7, 7, 6, 4, 1, 1, 1, 13, 7],

//        "water_vapour": [500, 1000, 1500, 2000, 3000, 4000, 5000],
//        "aerosol_depth": [0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2],
//        "sun_zenith_angle": [0, 10, 20, 30, 40, 50, 60, 70],
//        "view_zenith_angle": [0, 10, 20, 30, 40, 50, 60],
//        "relative_azimuth": [0, 30, 60, 90, 120, 150, 180],
//        "altitude": [0.0, 0.5, 1.0, 2.0, 3.0, 4.0],
//        "aerosol_type": ["___rural", "maritime", "___urban", "__desert"],
//        "model_type": ["MidLatitudeSummer"],
//        "ozone_content": [0.33176],
//        "co2_mixing_ratio": [380],
//        "wavelengths": [0.443, 0.49, 0.56, 0.665, 0.705, 0.74, 0.783, 0.842, 0.865, 0.945, 1.375, 1.61, 2.19],

//        "atmospheric_correction_parameters": ["path_radiance", "view_trans_diff", "spherical_albedo", "global_irradiance",
//                "view_trans_dir", "sun_trans_dir", "toa_irradiance"],

//        Assume.assumeTrue(lutResource != null);   // todo: check!!

        assertEquals(9, snapS2Lut.getDimensionCount());

        // "water_vapour": [500, 1000, 1500, 2000, 3000, 4000, 5000]
        assertEquals(7, snapS2Lut.getDimension(0).getSequence().length);
        assertEquals(500.0, snapS2Lut.getDimension(0).getMin(), 1.E-6);
        assertEquals(5000.0, snapS2Lut.getDimension(0).getMax(), 1.E-6);

        // "aerosol_depth": [0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2]
        assertEquals(17, snapS2Lut.getDimension(1).getSequence().length);
        assertEquals(0.05, snapS2Lut.getDimension(1).getMin(), 1.E-6);
        assertEquals(1.2, snapS2Lut.getDimension(1).getMax(), 1.E-6);

        // "sun_zenith_angle": [0, 10, 20, 30, 40, 50, 60, 70]
        assertEquals(8, snapS2Lut.getDimension(2).getSequence().length);
        assertEquals(0, snapS2Lut.getDimension(2).getMin(), 1.E-6);
        assertEquals(70, snapS2Lut.getDimension(2).getMax(), 1.E-6);

        // "view_zenith_angle": [0, 10, 20, 30, 40, 50, 60]
        assertEquals(7, snapS2Lut.getDimension(3).getSequence().length);
        assertEquals(0, snapS2Lut.getDimension(3).getMin(), 1.E-6);
        assertEquals(60, snapS2Lut.getDimension(3).getMax(), 1.E-6);

        // "relative_azimuth": [0, 30, 60, 90, 120, 150, 180]
        assertEquals(7, snapS2Lut.getDimension(4).getSequence().length);
        assertEquals(0, snapS2Lut.getDimension(4).getMin(), 1.E-6);
        assertEquals(180, snapS2Lut.getDimension(4).getMax(), 1.E-6);

        // "altitude": [0.0, 0.5, 1.0, 2.0, 3.0, 4.0]
        assertEquals(6, snapS2Lut.getDimension(5).getSequence().length);
        assertEquals(0.0, snapS2Lut.getDimension(5).getMin(), 1.E-6);
        assertEquals(4.0, snapS2Lut.getDimension(5).getMax(), 1.E-6);

        // "aerosol_type": ["___rural", "maritime", "___urban", "__desert"]
        assertEquals(4, snapS2Lut.getDimension(6).getSequence().length);
        assertEquals(0.0, snapS2Lut.getDimension(6).getMin(), 1.E-6);
        assertEquals(3.0, snapS2Lut.getDimension(6).getMax(), 1.E-6);

        // "wavelengths": [0.443, 0.49, 0.56, 0.665, 0.705, 0.74, 0.783, 0.842, 0.865, 0.945, 1.375, 1.61, 2.19]
        assertEquals(13, snapS2Lut.getDimension(7).getSequence().length);
        assertEquals(0.443, snapS2Lut.getDimension(7).getMin(), 1.E-6);
        assertEquals(2.19, snapS2Lut.getDimension(7).getMax(), 1.E-6);

        // "params": [0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0]
        assertEquals(7, snapS2Lut.getDimension(8).getSequence().length);
        assertEquals(1.0, snapS2Lut.getDimension(8).getMin(), 1.E-6);
        assertEquals(7.0, snapS2Lut.getDimension(8).getMax(), 1.E-6);
    }

    @Test
    public void testS2LutInterpolation() throws Exception {

        // test cases are taken from S2A-AC Python processor  (test_create_lut_with_all_constants)
        // test input arrays are set in InputArrays

        //        exp_res_1 = [4.36316803e-03, 1.63388416e-01, 1.46495119e-01, 1.44627824e-01,
        //                7.04174936e-01, 7.52480745e-01, 1.93931946e+03]
        //        exp_res_2 = [2.89879995e-03, 1.25021160e-01, 1.04601875e-01, 1.50571063e-01,
        //                7.70004094e-01, 8.09069276e-01, 1.96882263e+03]
        //        exp_res_3 = [1.60053559e-03, 9.09210593e-02, 7.14782253e-02, 1.40640020e-01,
        //                7.97799408e-01, 8.32686245e-01, 1.84810095e+03]
        //        exp_res_4 = [7.34889647e-04, 6.36115447e-02, 4.37759310e-02, 1.22092247e-01,
        //                8.75487089e-01, 8.97929430e-01, 1.53450806e+03]
        //        exp_res_5 = [5.73001278e-04, 5.64536750e-02, 3.71807367e-02, 1.16180725e-01,
        //                8.93960476e-01, 9.13471520e-01, 1.44546106e+03]
        //        exp_res_6 = [4.35618480e-04, 5.05545102e-02, 3.24458741e-02, 1.06038056e-01,
        //                9.11960781e-01, 9.28287983e-01, 1.30628271e+03]
        //        exp_res_7 = [3.24377994e-04, 4.47021052e-02, 2.80021261e-02, 9.64753479e-02,
        //                9.27032173e-01, 9.40617859e-01, 1.17952368e+03]
        //        exp_res_8 = [2.36884211e-04, 3.81955057e-02, 2.34984271e-02, 8.64133686e-02,
        //                9.22770858e-01, 9.36986208e-01, 1.06169763e+03]
        //        exp_res_9 = [1.96404668e-04, 3.57813984e-02, 2.14877799e-02, 8.30364376e-02,
        //                9.45528269e-01, 9.55744326e-01, 1.00748700e+03]
        //        exp_res_10 = [1.02020094e-04, 2.07145475e-02, 1.52445761e-02, 5.73066957e-02,
        //                6.69852078e-01, 7.03298569e-01, 8.12655029e+02]
        //        exp_res_11 = [1.00000000e-15, 6.02438604e-06, 3.17718135e-03, 4.50836448e-03,
        //                3.90184708e-02,  5.76096289e-02, 3.61265411e+02]
        //        exp_res_12 = [7.05832463e-06, 7.90359545e-03, 4.24025534e-03, 1.97301302e-02,
        //                9.62910652e-01, 9.70321953e-01, 2.40558807e+02]
        //        exp_res_13 = [9.62014838e-07, 3.59692983e-03, 8.22568836e-04, 6.75748195e-03,
        //                9.58798945e-01, 9.66247261e-01, 8.29926300e+01]

        double[] result_wvl_1 = new double[7];
        double[] expected_wvl_1 =
                new double[]{4.36317e-03, 1.63388e-01, 1.46495e-01, 1.44627e-01, 7.04175e-01, 7.5248e-01, 1939.31};
        double[] delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_1.length; i++) {
            result_wvl_1[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_1[i]);
            assertEquals(expected_wvl_1[i], result_wvl_1[i], delta[i]);
        }

        double[] result_wvl_2 = new double[7];
        double[] expected_wvl_2 =
                new double[]{2.89879e-03, 1.25021e-01, 1.04601e-01, 1.50571e-01, 7.70004e-01, 8.09069e-01, 1.96882e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_2.length; i++) {
            result_wvl_2[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_2[i]);
            assertEquals(expected_wvl_2[i], result_wvl_2[i], delta[i]);
        }

        double[] result_wvl_3 = new double[7];
        double[] expected_wvl_3 =
                new double[]{1.60053e-03, 9.09210e-02, 7.14782e-02, 1.40640e-01, 7.97799e-01, 8.32686e-01, 1.84810e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_3.length; i++) {
            result_wvl_3[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_3[i]);
            assertEquals(expected_wvl_3[i], result_wvl_3[i], delta[i]);
        }

        double[] result_wvl_4 = new double[7];
        double[] expected_wvl_4 =
                new double[]{7.34889e-04, 6.36115e-02, 4.37759e-02, 1.22092e-01, 8.75487e-01, 8.97929e-01, 1.53450e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_4.length; i++) {
            result_wvl_4[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_4[i]);
            assertEquals(expected_wvl_4[i], result_wvl_4[i], delta[i]);
        }

        double[] result_wvl_5 = new double[7];
        double[] expected_wvl_5 =
                new double[]{5.73001e-04, 5.64530e-02, 3.71807e-02, 1.16180e-01, 8.93960e-01, 9.13471e-01, 1.44546e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_5.length; i++) {
            result_wvl_5[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_5[i]);
            assertEquals(expected_wvl_5[i], result_wvl_5[i], delta[i]);
        }

        double[] result_wvl_6 = new double[7];
        double[] expected_wvl_6 =
                new double[]{4.35618e-04, 5.05545e-02, 3.24458e-02, 1.06038e-01, 9.11960e-01, 9.28288e-01, 1.30628e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_6.length; i++) {
            result_wvl_6[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_6[i]);
            assertEquals(expected_wvl_6[i], result_wvl_6[i], delta[i]);
        }

        double[] result_wvl_7 = new double[7];
        double[] expected_wvl_7 =
                new double[]{3.24377e-04, 4.47021e-02, 2.80021e-02, 9.64753e-02, 9.27032e-01, 9.40617e-01, 1.17952e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_7.length; i++) {
            result_wvl_7[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_7[i]);
            assertEquals(expected_wvl_7[i], result_wvl_7[i], delta[i]);
        }

        double[] result_wvl_8 = new double[7];
        double[] expected_wvl_8 =
                new double[]{2.36884e-04, 3.81955e-02, 2.34984e-02, 8.64133e-02, 9.22770e-01, 9.36986e-01, 1.06169e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_8.length; i++) {
            result_wvl_8[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_8[i]);
            assertEquals(expected_wvl_8[i], result_wvl_8[i], delta[i]);
        }

        double[] result_wvl_9 = new double[7];
        double[] expected_wvl_9 =
                new double[]{1.96404e-04, 3.57813e-02, 2.14877e-02, 8.30364e-02, 9.45528e-01, 9.55744e-01, 1.00748e+03};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_9.length; i++) {
            result_wvl_9[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_9[i]);
            assertEquals(expected_wvl_9[i], result_wvl_9[i], delta[i]);
        }

        double[] result_wvl_10 = new double[7];
        double[] expected_wvl_10 =
                new double[]{1.02020e-04, 2.07145e-02, 1.52445e-02, 5.73066e-02, 6.69852e-01, 7.03298e-01, 8.12655e+02};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_10.length; i++) {
            result_wvl_10[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_10[i]);
            assertEquals(expected_wvl_10[i], result_wvl_10[i], delta[i]);
        }

        double[] result_wvl_11 = new double[7];
        double[] expected_wvl_11 =
                new double[]{1.0e-15, 6.02438e-06, 3.17718e-03, 4.50836e-03, 3.90184e-02, 5.76096e-02, 3.61265e+02};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_11.length; i++) {
            result_wvl_11[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_11[i]);
            assertEquals(expected_wvl_11[i], result_wvl_11[i], delta[i]);
        }

        double[] result_wvl_12 = new double[7];
        double[] expected_wvl_12 =
                new double[]{7.05832e-06, 7.90359e-03, 4.24025e-03, 1.97301e-02, 9.62910e-01, 9.70321e-01, 2.40558e+02};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_12.length; i++) {
            result_wvl_12[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_12[i]);
            assertEquals(expected_wvl_12[i], result_wvl_12[i], delta[i]);
        }

        double[] result_wvl_13 = new double[7];
        double[] expected_wvl_13 =
                new double[]{9.62014e-07, 3.59692e-03, 8.22568e-04, 6.75748e-03, 9.58798e-01, 9.66247e-01, 8.29926e+01};
        delta = new double[]{1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-6, 1.E-2};
        for (int i = 0; i < S2LutInterpolationTestInputArrays.lutGridCoordWvl_13.length; i++) {
            result_wvl_13[i] = snapS2Lut.getValue(S2LutInterpolationTestInputArrays.lutGridCoordWvl_13[i]);
            assertEquals(expected_wvl_13[i], result_wvl_13[i], delta[i]);
        }
    }

}
