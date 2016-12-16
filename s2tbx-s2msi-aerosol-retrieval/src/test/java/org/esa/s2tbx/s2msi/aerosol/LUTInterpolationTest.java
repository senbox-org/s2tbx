package org.esa.s2tbx.s2msi.aerosol;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.aerosol.lut.LUTAccessor;
import org.esa.s2tbx.s2msi.aerosol.lut.LUTConstants;
import org.esa.s2tbx.s2msi.aerosol.lut.LUTReader;
import org.esa.s2tbx.s2msi.aerosol.lut.Luts;
import org.esa.snap.core.util.math.LookupTable;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 16.12.2016
 * Time: 14:09
 *
 * @author olafd
 */
public class LUTInterpolationTest {

    private URL lutResource;
    private String lutPath;
    private LUTAccessor lutAccessor;
    private String[] dimNames;
    private double[][] dimValues;


    @Before
    public void setUp() throws IOException {
        lutResource = LUTAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d");
        // test shall be performed in local environment only, i.e. if we have the LUT available as test resource
        Assume.assumeTrue(lutResource != null);

        dimNames = LUTConstants.dimNames;
        dimValues = LUTConstants.dimValues;
        File lutFile = new File(lutResource.getPath());
        lutPath = lutFile.getAbsolutePath();
        lutAccessor = new LUTAccessor(lutFile);
    }


    @Test
    public void testCreateLutWithAllConstants() throws Exception {
        // // TODO: 16.12.2016

        LUTReader lutReader = new LUTReader(lutAccessor);
        final float[][] lut = lutReader.readLut(ProgressMonitor.NULL);
        // todo: we need 1D LUT
//        new LookupTable(lut, wvl, aot, hsf, azi, sza, vza, parameters));


//        final double[][] dimensions = LUTConstants.dimValues;
//        final double[] values = new double[]{0, 1, 2, 3};
//
//        final LookupTable snapS2Lut = new LookupTable(values, dimensions);
//        assertEquals(2, snapS2Lut.getDimensionCount());
//
//        assertEquals(0.0, snapS2Lut.getDimension(0).getMin(), 0.0);
//        assertEquals(1.0, snapS2Lut.getDimension(0).getMax(), 0.0);
//        assertEquals(0.0, snapS2Lut.getDimension(1).getMin(), 0.0);
//        assertEquals(1.0, snapS2Lut.getDimension(1).getMax(), 0.0);
//
//        assertEquals(0.0, snapS2Lut.getValue(0.0, 0.0), 0.0);
//        assertEquals(1.0, snapS2Lut.getValue(0.0, 1.0), 0.0);
//        assertEquals(2.0, snapS2Lut.getValue(1.0, 0.0), 0.0);
//        assertEquals(3.0, snapS2Lut.getValue(1.0, 1.0), 0.0);
//
//        assertEquals(0.5, snapS2Lut.getValue(0.0, 0.5), 0.0);
//        assertEquals(1.5, snapS2Lut.getValue(0.5, 0.5), 0.0);
//        assertEquals(2.5, snapS2Lut.getValue(1.0, 0.5), 0.0);



        // Python (from S2A-AC):
//        constants =  {'water_vapour':500, 'aerosol_depth':0.05, 'sun_zenith_angle': 33.0, 'view_zenith_angle': 46.0,
//                'relative_azimuth':84.0, 'altitude':2.7, 'aerosol_type':0}
//        # testlut = lut11d('luts/sentinel-2a_lut_smsi_v0.6', constants=constants, verbose=True)
//        testlut = lut11d('luts/sentinel-2a_lut_smsi_v0.6', verbose=True)
//        # order of input variables: "water_vapour", "aerosol_depth", "sun_zenith_angle", "view_zenith_angle",
//        # "relative_azimuth", "altitude", "aerosol_type", "model_type", "ozone_content", "co2_mixing_ratio"
//        # on sampling points: water_vapour, aerosol_depth, aerosol_type (of course)
//        # between sampling points: sun_zenith_angle, view_zenith_angle, relative_azimuth, altitude
//        # inp = [[500, 0.05, 33.0, 46.0, 84.0, 2.7, 0, 0, 0.33176, 380]]
//        # inp = [[500], [0.05], [33.0], [46.0], [84.0], [2.7], [0], [0], [0.33176], [380]]
//        inp = np.empty([10, 1000])
//        inp[0] = 500
//        inp[1] = 0.05
//        inp[2] = 33.0
//        inp[3] = 46.0
//        inp[4] = 84.0
//        inp[5] = 2.7
//        inp[6] = 0
//        inp[7] = 0
//        inp[8] = 0.33176
//        inp[9] = 380
//        # inp = [[500, 500], [0.05, 0.05], [33.0, 33.0], [46.0, 46.0], [84.0, 84.0], [2.7, 2.7], [0, 0], [0, 0], [0.33176, 0.33176], [380, 380]]
//        number_of_test_runs = 1000
//        start = time.clock()
//        # for i in range(number_of_test_runs):
//        result = testlut.interp_over_bands(inp)
//        stop = time.clock()
//        print 'time for interpolation with all constants: ', (stop - start) / number_of_test_runs
//        self.assertEqual(result.size, 91000)
//        self.assertEqual(result[0].size, 91)
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
//        self.assertEqual(result[0][0].size, 7)
//        nt.assert_array_almost_equal(exp_res_1, result[0][0], 3)
//        self.assertEqual(result[0][1].size, 7)
//        nt.assert_array_almost_equal(exp_res_2, result[0][1], 3)
//        self.assertEqual(result[0][2].size, 7)
//        nt.assert_array_almost_equal(exp_res_3, result[0][2], 3)
//        self.assertEqual(result[0][3].size, 7)
//        nt.assert_array_almost_equal(exp_res_4, result[0][3], 3)
//        self.assertEqual(result[0][4].size, 7)
//        nt.assert_array_almost_equal(exp_res_5, result[0][4], 3)
//        self.assertEqual(result[0][5].size, 7)
//        nt.assert_array_almost_equal(exp_res_6, result[0][5], 3)
//        self.assertEqual(result[0][6].size, 7)
//        nt.assert_array_almost_equal(exp_res_7, result[0][6], 3)
//        self.assertEqual(result[0][7].size, 7)
//        nt.assert_array_almost_equal(exp_res_8, result[0][7], 3)
//        self.assertEqual(result[0][8].size, 7)
//        nt.assert_array_almost_equal(exp_res_9, result[0][8], 3)
//        self.assertEqual(result[0][9].size, 7)
//        nt.assert_array_almost_equal(exp_res_10, result[0][9], 3)
//        self.assertEqual(result[0][10].size, 7)
//        nt.assert_array_almost_equal(exp_res_11, result[0][10], 3)
//        self.assertEqual(result[0][11].size, 7)
//        nt.assert_array_almost_equal(exp_res_12, result[0][11], 3)
//        self.assertEqual(result[0][12].size, 7)
//        nt.assert_array_almost_equal(exp_res_13, result[0][12], 3)
    }

    @Test
    public void testCreateLutWithSomeConstants() throws Exception {
        // // TODO: 16.12.2016
    }

    @Test
    public void testLutInterpolationWithConstantsReference() throws Exception {
        // // TODO: 16.12.2016
    }

    @Test
    public void testLutAccessAllBandsAtOnce() throws Exception {
        // // TODO: 16.12.2016
    }
}
