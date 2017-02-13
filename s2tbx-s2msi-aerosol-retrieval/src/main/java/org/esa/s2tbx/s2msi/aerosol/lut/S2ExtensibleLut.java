package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.snap.core.util.math.Array;
import org.esa.snap.core.util.math.FracIndex;
import org.esa.snap.core.util.math.LookupTable;

import java.text.MessageFormat;

/**
 * @author Tonio Fincke
 */
public class S2ExtensibleLut extends LookupTable {

    public S2ExtensibleLut(float[] values, float[]... dimensions) {
        super(values, dimensions);
    }

    public final double[][][] getAotWvlAndACValues(final FracIndex[] fracIndexes) {
        final int[] strides = getStrides();
        final int[] offsets = getOffsets();
        final Array values = getValues();

        if (fracIndexes.length != strides.length - 3) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "number of fracIndexes.length = {0} does not correspond to the expected length {1}",
                    fracIndexes.length, strides.length - 3));
        }

        final int numberofWavelengths = fracIndexes.length - 2;
        final int numberOfAtmosphericParameters = fracIndexes.length - 1;
        final int resultLength = numberofWavelengths * numberOfAtmosphericParameters;
        double[][][] v = new double[1 << fracIndexes.length][strides[numberofWavelengths]][strides[numberOfAtmosphericParameters]];

        int origin = 0;
        int index = 0;
        for (int i = 0; i < fracIndexes.length; ++i, ++index) {
            if (i == 1) {
                index++;
            }
            origin += fracIndexes[i].i * strides[index];
        }
        for (int i = 0; i < v.length; ++i) {
//            values.copyTo(origin + offsets[i], v[i], 0, resultLength);
//            for (int j = 0; j < numberofWavelengths; ++j) {
//            }
        }

        return null;
    }

}
