package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.IntervalPartition;
import org.esa.snap.core.util.math.LookupTable;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;

import static org.junit.Assert.*;

/**
 * @author Tonio Fincke
 */
public class S2LutTest {

    private S2Lut s2Lut;

    @Before
    public void setUp() throws IOException {
        URL lutResource = S2LutAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d");
        // test shall be performed in local environment only, i.e. if we have the LUT available as test resource
        Assume.assumeTrue(lutResource != null);

        assert lutResource != null;
        File lutFile = new File(lutResource.getPath());
        final FileImageInputStream imageInputStream = new FileImageInputStream(lutFile);
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

        final float[] wvp = S2LutConstants.dimValues[0];
        final float[] ad = S2LutConstants.dimValues[1];
        final float[] sza = S2LutConstants.dimValues[2];
        final float[] vza = S2LutConstants.dimValues[3];
        final float[] ra = S2LutConstants.dimValues[4];
        final float[] alt = S2LutConstants.dimValues[5];
        final float[] at = S2LutConstants.dimValues[6];
        final float[] wvl = S2LutConstants.dimValues[10];
        float[] params = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f};

        int lutSize = wvp.length * ad.length * sza.length * vza.length * ra.length * alt.length * at.length *
                wvl.length * params.length;

        float[] lutArray = new float[lutSize];

        imageInputStream.readFully(lutArray, 0, lutArray.length);

        imageInputStream.close();

        s2Lut = new S2Lut(lutArray, wvp, ad, sza, vza, ra, alt, at, wvl, params);

    }

    @Test
    public void testGetAotWvlAndACValues() throws Exception {
        FracIndex[] fracIndexes = FracIndex.createArray(6);
        IntervalPartition[] partitions = s2Lut.getDimensions();
        LookupTable.computeFracIndex(partitions[0], 750, fracIndexes[0]);
        LookupTable.computeFracIndex(partitions[2], 35, fracIndexes[1]);
        LookupTable.computeFracIndex(partitions[3], 35, fracIndexes[2]);
        LookupTable.computeFracIndex(partitions[4], 55, fracIndexes[3]);
        LookupTable.computeFracIndex(partitions[5], 1.5, fracIndexes[4]);
        LookupTable.computeFracIndex(partitions[6], 2.0, fracIndexes[5]);

        int[] wvlIndexes = new int[13];
        for (int i = 0; i < wvlIndexes.length; i++) {
            wvlIndexes[i] = i;
        }
        double[][][] aotWvlAndACValues = s2Lut.getAotWvlAndACValues(fracIndexes, wvlIndexes);

        FracIndex[] moreFracIndexes = FracIndex.createArray(8);
        moreFracIndexes[0] = fracIndexes[0];
        moreFracIndexes[2] = fracIndexes[1];
        moreFracIndexes[3] = fracIndexes[2];
        moreFracIndexes[4] = fracIndexes[3];
        moreFracIndexes[5] = fracIndexes[4];
        moreFracIndexes[6] = fracIndexes[5];

        float[] aodValues = S2LutConstants.dimValues[1];
        float[] wvlValues = S2LutConstants.dimValues[10];
        for (int i_aod = 0; i_aod < aodValues.length; i_aod++) {
            LookupTable.computeFracIndex(partitions[1], aodValues[i_aod], moreFracIndexes[1]);
            for (int i_wvl = 0; i_wvl < wvlValues.length; i_wvl++) {
                LookupTable.computeFracIndex(partitions[7], wvlValues[i_wvl], moreFracIndexes[7]);
                double[] values = s2Lut.getValues(moreFracIndexes);
                assertArrayEquals(values, aotWvlAndACValues[i_aod][i_wvl], 1e-8);
            }
        }

        int[] newWvlIndexes = new int[11];
        int offset = 0;
        for (int i = 0; i < wvlIndexes.length; i++) {
            wvlIndexes[i] = i + offset;
            if (i == 9) {
                offset += 2;
            }
        }

        double[][][] newAotWvlAndACValues = s2Lut.getAotWvlAndACValues(fracIndexes, newWvlIndexes);
        for (int i_aod = 0; i_aod < aodValues.length; i_aod++) {
            LookupTable.computeFracIndex(partitions[1], aodValues[i_aod], moreFracIndexes[1]);
            for (int i_wvl = 0; i_wvl < newWvlIndexes.length; i_wvl++) {
                LookupTable.computeFracIndex(partitions[7], wvlValues[newWvlIndexes[i_wvl]], moreFracIndexes[7]);
                double[] values = s2Lut.getValues(moreFracIndexes);
                assertArrayEquals(values, newAotWvlAndACValues[i_aod][i_wvl], 1e-8);
            }
        }
    }
}