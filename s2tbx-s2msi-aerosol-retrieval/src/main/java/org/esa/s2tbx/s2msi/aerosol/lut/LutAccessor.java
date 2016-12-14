package org.esa.s2tbx.s2msi.aerosol.lut;

import org.esa.snap.core.util.ArrayUtils;
import org.esa.snap.core.util.StringUtils;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * todo: add comment
 *
 * @author olafd
 */
public class LutAccessor {

    private File lutFile;
    private final Properties properties;
    private final static String lutshape_name = "lutshape";
    private final static String dimnames_name = "dimnames";
    private final String[] intPropertyNames = {lutshape_name};

    public LutAccessor(File inputFile) throws IOException {
        properties = new Properties();

        boolean lutExists = false;
        if (inputFile.exists()) {
            File lutDescription = null;
            if(inputFile.getPath().endsWith("memmap.d")) {
                final String lutDescriptionPath = inputFile.getAbsolutePath().replace("memmap.d", "dims.jsn");
                lutDescription = new File(lutDescriptionPath);
                this.lutFile = inputFile;
            } else if (inputFile.getPath().endsWith("dims.jsn")) {
                final String lutFilePath = inputFile.getAbsolutePath().replace("dims.jsn", "memmap.d");
                lutDescription = inputFile;
                this.lutFile = new File(lutFilePath);
            }
            if (lutDescription != null && lutDescription.exists()) {
                readPropertiesFromJSONFile(lutDescription);
                lutExists = true;
            }
        }
        if (!lutExists) {
            throw  new IOException("Could not read LUT description file");
        }
    }

    private void readPropertiesFromJSONFile(File jsonFile) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
        final String fileContent = bufferedReader.readLine();
        String bracketContent = fileContent.substring(1, fileContent.length() - 1);
        List<Integer> indexesOfColons = new ArrayList<Integer>();
        int currentColonindex = -1;
        while ((currentColonindex = bracketContent.indexOf(':', currentColonindex + 1)) >= 0) {
            indexesOfColons.add(currentColonindex);
        }
        final int numberOfProperties = indexesOfColons.size();
        int indexOfCurrentComma;
        int indexOfLastComma = -1;
        for (int i = 1; i < numberOfProperties; i++) {
            indexOfCurrentComma = bracketContent.substring(0, indexesOfColons.get(i)).lastIndexOf(',');
            if (indexOfCurrentComma != indexOfLastComma) {
                final String propertyName =
                        bracketContent.substring(indexOfLastComma + 1, indexesOfColons.get(i - 1)).replace("\"", "").trim();
                final String propertyValue =
                        bracketContent.substring(indexesOfColons.get(i - 1) + 1, indexOfCurrentComma).trim();
                putPropertyValue(propertyName, propertyValue);
                indexOfLastComma = indexOfCurrentComma;
            }
        }
        final String propertyName =
                bracketContent.substring(indexOfLastComma + 1, indexesOfColons.get(numberOfProperties - 1)).replace("\"", "").trim();
        final String propertyValue =
                bracketContent.substring(indexesOfColons.get(numberOfProperties - 1) + 1).trim();
        putPropertyValue(propertyName, propertyValue);
    }

    private void putPropertyValue(String propertyName, String propertyValue) {
        if (propertyValue.startsWith("[") && propertyValue.endsWith("]")) {
            final String[] propertyValues = propertyValue.substring(1, propertyValue.length() - 1).split(",");
            if (ArrayUtils.isMemberOf(propertyName, intPropertyNames)) {
                putPropertiesAsIntegerArray(propertyName, propertyValues);
            } else if(propertyValues.length > 0 && StringUtils.isNumeric(propertyValues[0], Double.class)) {
                putPropertiesAsDoubleArray(propertyName, propertyValues);
            } else {
                putPropertiesAsStringArray(propertyName, propertyValues);
            }
        } else {
            properties.put(propertyName, propertyValue.replace("\"", ""));
        }
    }

    private void putPropertiesAsIntegerArray(String propertyName, String[] propertyValues) {
        int[] propertyValuesAsInteger = new int[propertyValues.length];
        for (int j = 0; j < propertyValues.length; j++) {
            String stringValue = propertyValues[j].trim();
            propertyValuesAsInteger[j] = Integer.parseInt(stringValue);
        }
        properties.put(propertyName, propertyValuesAsInteger);
    }

    private void putPropertiesAsDoubleArray(String propertyName, String[] propertyValues) {
        double[] propertyValuesAsDouble = new double[propertyValues.length];
        for (int j = 0; j < propertyValues.length; j++) {
            String stringValue = propertyValues[j];
            propertyValuesAsDouble[j] = Double.parseDouble(stringValue);
        }
        properties.put(propertyName, propertyValuesAsDouble);
    }

    private void putPropertiesAsStringArray(String propertyName, String[] propertyValues) {
        List<String> newPropertyValues = new ArrayList<String>();
        for (int j = 0; j < propertyValues.length; j++) {
            String propertyValue = propertyValues[j];
            while(j < propertyValues.length - 1 &&
                    !(propertyValue.charAt(propertyValue.length() - 1) == '"') &&
                    !(propertyValues[j + 1].charAt(0) == '"')) {
                propertyValue = propertyValue + ", "  + propertyValues[j + 1];
                j++;
            }
            propertyValue = propertyValue.replace("\"", "").trim();
            newPropertyValues.add(propertyValue);
        }
        properties.put(propertyName, newPropertyValues.toArray(new String[newPropertyValues.size()]));
    }

    public int getNumberOfNonSpectralProperties() {
        final String[] dimnames = (String[]) properties.get(dimnames_name);
        return dimnames.length - 1;
    }

    public String[] getTargetNames() {
        final String[] dimNames = (String[]) properties.get(dimnames_name);
        String[] targetNames = new String[dimNames.length - 1];
        System.arraycopy(dimNames, 0, targetNames, 0, targetNames.length);
        return targetNames;
    }

    public boolean isDoubleDim(String dimName) {
        final Object property = properties.get(dimName);
        return (property instanceof double[]);
    }

    public double[] getDimValues(String dimName) {
        final Object property = properties.get(dimName);
        if (property instanceof double[]) {
            return (double[]) property;
        }
        throw new IllegalArgumentException("Cannot find values for dimension " + dimName);
    }

    public String[] getStringDimValues(String dimName) {
        final Object property = properties.get(dimName);
        if (property instanceof String[]) {
            return (String[]) property;
        }
        throw new IllegalArgumentException("Cannot find values for dimension " + dimName);
    }

    public int[] getLUTShapes() {
        return ((int[]) properties.get(lutshape_name));
    }

    public ImageInputStream openLUTStream() throws IOException {
        final FileImageInputStream imageInputStream = new FileImageInputStream(lutFile);
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        return imageInputStream;
    }

    public String getDimName(int index) {
        return ((String[]) properties.get(dimnames_name))[index];
    }

    public String getLUTPath() {
        return lutFile.getAbsolutePath();
    }

    public int getDimIndex(String dimName) {
        final String[] dimNames = (String[]) properties.get(dimnames_name);
        return ArrayUtils.getElementIndex(dimName, dimNames);
    }

    public String getOpticalModel() {
        final Object opticalModelPropery = properties.get("optical_model");
        if(opticalModelPropery != null) {
            return opticalModelPropery.toString();
        }
        return "probably_not_momo";
    }

    public String getReflectancesType() {
        return properties.get("reflectances_type").toString();
    }

    public String getUnit(String dim) {
        final String[] dimunits = (String[]) properties.get("dimunits");
        return dimunits[getDimIndex(dim)];
    }

    public String getDescription(String dim) {
        final String[] dimdescriptions = (String[]) properties.get("dimdescriptions");
        return dimdescriptions[getDimIndex(dim)];
    }

}
