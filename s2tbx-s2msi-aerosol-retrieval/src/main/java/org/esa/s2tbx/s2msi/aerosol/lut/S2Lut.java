package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.snap.core.util.math.Array;
import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.LookupTable;

import java.text.MessageFormat;

/**
 * @author Tonio Fincke
 */
public class S2Lut extends LookupTable {

    public S2Lut(float[] values, float[]... dimensions) {
        super(values, dimensions);
    }

    public final double[][][] getAotWvlAndACValues(final FracIndex[] fracIndexes, int[] wvlIndexes) {
        final int[] strides = getStrides();
        final int[] offsets = getOffsets();
        final Array values = getValues();

        if (fracIndexes.length != strides.length - 3) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "number of fracIndexes.length = {0} does not correspond to the expected length {1}",
                    fracIndexes.length, strides.length - 3));
        }

        final int numberOfAODs = 17;
        final int numberOfWavelengths = wvlIndexes.length;
        final int numberOfAtmosphericParameters = strides[strides.length - 2];
        double[][][][] v = new double[1 << fracIndexes.length][numberOfAODs][numberOfWavelengths]
                [numberOfAtmosphericParameters];

        int origin = 0;
        int index = 0;
        for (int i = 0; i < fracIndexes.length; ++i, ++index) {
            if (i == 1) {
                index++;
            }
            origin += fracIndexes[i].i * strides[index];
        }
        index = 0;
        for (int i = 0; i < v.length; ++i) {
            while ((index / 2) % 2 == 1) {
                index++;
            }
            int origin_i = origin + offsets[index];
            for (int i_aod = 0; i_aod < numberOfAODs; ++i_aod) {
                int aod_origin = origin_i + i_aod * strides[1];
                for (int i_wvl = 0; i_wvl < numberOfWavelengths; ++i_wvl) {
                    int wvl_origin = aod_origin + wvlIndexes[i_wvl] * numberOfAtmosphericParameters;
                    values.copyTo(wvl_origin, v[i][i_aod][i_wvl], 0, numberOfAtmosphericParameters);
                }
            }
            ++index;
        }
        for (int i = fracIndexes.length; i-- > 0; ) {
            final int m = 1 << i;
            final double f = fracIndexes[i].f;
            for (int j = 0; j < m; ++j) {
                for (int i_aod = 0; i_aod < numberOfAODs; ++i_aod) {
                    for (int i_wvl = 0; i_wvl < numberOfWavelengths; ++i_wvl) {
                        for (int i_acp = 0; i_acp < numberOfAtmosphericParameters; ++i_acp) {
                            v[j][i_aod][i_wvl][i_acp] +=
                                    f * (v[m + j][i_aod][i_wvl][i_acp] - v[j][i_aod][i_wvl][i_acp]);
                        }
                    }
                }
            }
        }
        return v[0];
    }

}
