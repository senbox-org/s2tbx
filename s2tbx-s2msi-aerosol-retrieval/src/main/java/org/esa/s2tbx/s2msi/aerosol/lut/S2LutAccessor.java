package org.esa.s2tbx.s2msi.aerosol.lut;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.lut.LutUtils;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * todo: add comment
 *
 * @author olafd
 */
public class S2LutAccessor {

    private File lutFile;

    private int[][] minMaxIndices;

    private Properties properties;
    private final static String lutshape_name = "lutshape";
    private final static String dimnames_name = "dimnames";
    private final String[] intPropertyNames = {lutshape_name};

    private static final String[] modelTypes = {"MidLatitudeSummer"};
    private static final String[] aerosolTypes = {"___rural", "maritime", "___urban", "__desert"};

    public static final Map<String, String[]> STRING_PROPERTY_MAP;

    static {
        STRING_PROPERTY_MAP = new HashMap<>();
        STRING_PROPERTY_MAP.put("model_type", modelTypes);
        STRING_PROPERTY_MAP.put("aerosol_type", aerosolTypes);
    }


    public S2LutAccessor(File inputFile) throws IOException {

        boolean lutExists = false;
        properties = null;
        if (inputFile.exists()) {
            File lutDescriptionFile = null;
            if (inputFile.getPath().endsWith("memmap.d")) {
                final String lutDescriptionPath = inputFile.getAbsolutePath().replace("memmap.d", "dims.jsn");
                lutDescriptionFile = new File(lutDescriptionPath);
                this.lutFile = inputFile;
            } else if (inputFile.getPath().endsWith("dims.jsn")) {
                final String lutFilePath = inputFile.getAbsolutePath().replace("dims.jsn", "memmap.d");
                lutDescriptionFile = inputFile;
                this.lutFile = new File(lutFilePath);
            }
            if (lutDescriptionFile != null && lutDescriptionFile.exists()) {
                properties = LutUtils.readPropertiesFromJsonFile(lutDescriptionFile, intPropertyNames);
                lutExists = true;
            }
        }
        if (!lutExists) {
            throw new IOException("Could not read LUT description file");
        }

        try {
            validate();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        initIndices();
    }

    public S2Lut readLut(ProgressMonitor progressMonitor) throws IOException {
        final int lutLength = getLutLength();
        if (progressMonitor != null) {
            progressMonitor.beginTask("Reading Look-Up-Table", lutLength);
        }

        ImageInputStream iis = openLUTStream();

        // See sentinel-2a_lut_smsi_v0.6.dims.jsn. We have:
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

        final float[] wvp = getDimValues(S2LutConstants.dimNames[0]);
        final float[] ad = getDimValues(S2LutConstants.dimNames[1]);
        final float[] sza = getDimValues(S2LutConstants.dimNames[2]);
        final float[] vza = getDimValues(S2LutConstants.dimNames[3]);
        final float[] ra = getDimValues(S2LutConstants.dimNames[4]);
        final float[] alt = getDimValues(S2LutConstants.dimNames[5]);
        final float[] at = getDimValues(S2LutConstants.dimNames[6]);
        final float[] wvl = getDimValues(S2LutConstants.dimNames[10]);

        float[] params = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f};
        final int nParams = params.length;

        float[] lutArray = new float[nParams * getLutLength()];

//        final long t1 = System.currentTimeMillis();
        iis.readFully(lutArray, 0, lutArray.length);
//        final long t2 = System.currentTimeMillis();
//        final long dt = t2 - t1;
//        System.out.println("t2-t1 = " + dt);

        iis.close();

        return new S2Lut(lutArray, wvp, ad, sza, vza, ra, alt, at, wvl, params);
    }

    int getNumberOfNonSpectralProperties() {
        final String[] dimnames = (String[]) properties.get(dimnames_name);
        return dimnames.length - 1;
    }

    String[] getTargetNames() {
        final String[] dimNames = (String[]) properties.get(dimnames_name);
        String[] targetNames = new String[dimNames.length - 1];
        System.arraycopy(dimNames, 0, targetNames, 0, targetNames.length);
        return targetNames;
    }

    float[] getDimValues(String dimName) {
        final Object property = properties.get(dimName);
        if (property instanceof float[]) {
            return (float[]) property;
        }
        if (property instanceof String[]) {
            float[] indices = new float[STRING_PROPERTY_MAP.get(dimName).length];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = (float) i;
            }
            return indices;
        }
        throw new IllegalArgumentException("Cannot find values for dimension " + dimName);
    }

    int[] getLUTShapes() {
        return ((int[]) properties.get(lutshape_name));
    }

    private void validate() throws ValidationException {
        final int[] lutShapes = getLUTShapes();
        final String[] targetNames = getTargetNames();
        if (lutShapes.length - 1 != targetNames.length) {
            throw new ValidationException("Look-Up-Table invalid: Parameter " + lutshape_name + " does not match " +
                                                  "parameter " + dimnames_name);
        }
    }

    private int getLutLength() {
        int lutLength = 1;
        for (int[] minMaxIndex : minMaxIndices) {
            lutLength *= (minMaxIndex[1] - minMaxIndex[0] + 1);
        }
        return lutLength;
    }

    private void initIndices() {
        minMaxIndices = new int[getNumberOfNonSpectralProperties()][2];
        for (int i = 0; i < minMaxIndices.length; i++) {
            minMaxIndices[i][0] = 0;
            minMaxIndices[i][1] = S2LutConstants.dimValues[i].length - 1;
        }
    }

    private ImageInputStream openLUTStream() throws IOException {
        final FileImageInputStream imageInputStream = new FileImageInputStream(lutFile);
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        return imageInputStream;
    }


}
