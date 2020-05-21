package org.esa.s2tbx.mapper.common;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.binding.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SpectrumInputConverter implements Converter<SpectrumInput[]> {

    private static final String SPECTRUM_INPUTS_SEPARATOR = ":";
    private static final String SPECTRUM_INPUT_VARS_SEPARATOR = ";";

    /**
     * Gets the value type.
     *
     * @return The value type.
     */
    @Override
    public Class<? extends SpectrumInput[]> getValueType() {
        return SpectrumInput[].class;
    }

    /**
     * Converts a value from its plain text representation to a Java object instance
     * of the type returned by {@link #getValueType()}.
     *
     * @param text The textual representation of the value.
     * @return The converted value.
     * @throws com.bc.ceres.binding.ConversionException If the conversion fails.
     */
    @Override
    public SpectrumInput[] parse(String text) throws ConversionException {
        final List<SpectrumInput> spectrumList = new ArrayList<>();

        final String[] spectraElements = text.split(SPECTRUM_INPUTS_SEPARATOR);
        for (String spectrumElem : spectraElements) {

            String[] spectrumElemVars = spectrumElem.split(SPECTRUM_INPUT_VARS_SEPARATOR);
            if (spectrumElemVars.length != 4) {
                continue;
            }
            String nameStr = spectrumElemVars[0];
            String xPosStr = spectrumElemVars[1];
            String yPosStr = spectrumElemVars[2];
            String isShapeDefinedStr = spectrumElemVars[3];
            int[] xintArr = null;
            int[] yintArr = null;

            if (xPosStr != null) {
                xPosStr = xPosStr.replaceAll("\\[|]|\\s", "");
                String[] stringTokens = xPosStr.split(",");
                xintArr = Stream.of(stringTokens).mapToInt(Integer::parseInt).toArray();
            }
            if (yPosStr != null) {
                yPosStr = yPosStr.replaceAll("\\[|]|\\s", "");
                String[] stringTokens = yPosStr.split(",");
                yintArr = Stream.of(stringTokens).mapToInt(Integer::parseInt).toArray();
            }

            final SpectrumInput spectrum = new SpectrumInput(nameStr, xintArr, yintArr);
            if (isShapeDefinedStr != null) {
                spectrum.setIsShapeDefined(Boolean.parseBoolean(isShapeDefinedStr));
            }
            spectrumList.add(spectrum);
        }

        return spectrumList.toArray(new SpectrumInput[0]);
    }

    /**
     * Converts a value of the type returned by {@link #getValueType()} to its
     * plain text representation.
     *
     * @param value The value to be converted to text.
     * @return The textual representation of the value.
     */
    @Override
    public String format(SpectrumInput[] value) {
        String valueStr = "";
        if(value != null) {
            for (SpectrumInput spectrumInput : value) {
                String nameStr = spectrumInput.getName();
                String xPosStr = Arrays.toString(spectrumInput.getXPixelPolygonPositions());
                String yPosStr = Arrays.toString(spectrumInput.getYPixelPolygonPositions());
                String isShapeDefinedStr = Boolean.toString(spectrumInput.getIsShapeDefined());
                valueStr = (valueStr.isEmpty() ? valueStr : valueStr + SPECTRUM_INPUTS_SEPARATOR) + nameStr + SPECTRUM_INPUT_VARS_SEPARATOR + xPosStr + SPECTRUM_INPUT_VARS_SEPARATOR + yPosStr + SPECTRUM_INPUT_VARS_SEPARATOR + isShapeDefinedStr;
            }
        }
        return valueStr;
    }
}
