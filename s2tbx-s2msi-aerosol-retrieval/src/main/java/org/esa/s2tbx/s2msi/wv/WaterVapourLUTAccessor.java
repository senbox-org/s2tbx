package org.esa.s2tbx.s2msi.wv;

import org.esa.s2tbx.s2msi.lut.LutUtils;
import org.esa.snap.core.util.math.LookupTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author Tonio Fincke
 */
class WaterVapourLUTAccessor {

    private final static String LUTSHAPE_NAME = "lutshape";
    private final static String[] INT_PROPERTY_NAMES = {LUTSHAPE_NAME};
    private static final String dims_file_name = "wv_lut_v0.6.dims.jsn";
    private static final String LUT_FILE_NAME = "water_vapour_lut_CIBR_9_8A.txt";
    private static final String SURFACE_REFLECTANCE_NAME = "surface_reflectance";
    private static final String SUN_ZENITH_NAME = "sun_zenith";
    private static final String VIEW_ZENITH_NAME = "view_zenith";
    private static final String AZIMUTH_NAME = "azimuth";
    private static final String ELEVATION_NAME = "ground_altitude";
    private static final String B_NAME = "b";

    static LookupTable readLut() throws IOException {
        final InputStream dimsStream = WaterVapourLUTAccessor.class.getResourceAsStream(dims_file_name);
        Properties properties = LutUtils.readPropertiesFromJsonFile(dimsStream, INT_PROPERTY_NAMES);

        final InputStream lutStream = WaterVapourLUTAccessor.class.getResourceAsStream(LUT_FILE_NAME);

        final int[] lutShape = (int[]) properties.get(LUTSHAPE_NAME);
        final double[] surfaceReflectanceValues = convert((float[]) properties.get(SURFACE_REFLECTANCE_NAME));
        final double[] sunZenithValues = convert((float[]) properties.get(SUN_ZENITH_NAME));
        final double[] viewZenithValues = convert((float[]) properties.get(VIEW_ZENITH_NAME));
        final double[] azimuthValues = convert((float[]) properties.get(AZIMUTH_NAME));
        final double[] elevationValues = convert((float[]) properties.get(ELEVATION_NAME));
        final String[] bValues = (String[]) properties.get(B_NAME);
        final double[] bDoubleValues = new double[bValues.length];
        for (int i = 0; i < bValues.length; i++) {
            bDoubleValues[i] = i * 1f;
        }

        int numberOfLutValues = 1;
        for (int lutDimension : lutShape) {
            numberOfLutValues *= lutDimension;
        }

        double[] lookupValues = new double[numberOfLutValues];
        int count = 0;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lutStream));
        String line = bufferedReader.readLine();
        while (line != null) {
            if (!line.startsWith("0")) {
                line = bufferedReader.readLine();
                continue;
            }
            final String[] values = line.split(";");
            lookupValues[count++] = Double.parseDouble(values[5]);
            lookupValues[count++] = Double.parseDouble(values[6]);
            line = bufferedReader.readLine();
        }
        return new LookupTable(lookupValues, surfaceReflectanceValues, sunZenithValues, viewZenithValues,
                               azimuthValues, elevationValues, bDoubleValues);
    }

    private static double[] convert(float[] oldArray) {
        double[] newArray = new double[oldArray.length];
        for (int i = 0; i < oldArray.length; i++) {
            newArray[i] = oldArray[i];
        }
        return newArray;
    }

}
