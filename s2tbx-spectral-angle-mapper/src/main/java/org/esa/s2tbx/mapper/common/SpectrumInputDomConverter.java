package org.esa.s2tbx.mapper.common;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.binding.dom.DomConverter;
import com.bc.ceres.binding.dom.DomElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Dom converter for spectra elements
 * @author Oana Hogoiu
 * @since 4/19/2019
 */
public class SpectrumInputDomConverter implements DomConverter {

    @Override
    public Class<?> getValueType() {
        return SpectrumInput[].class;
    }

    @Override
    public SpectrumInput[] convertDomToValue(DomElement parentElement, Object value) throws ConversionException, ValidationException {

        final List<SpectrumInput> spectrumList = new ArrayList<>();

        final DomElement[] spectraElements = parentElement.getChildren();
        for(DomElement spectrumElem : spectraElements) {

            String nameStr = null;
            String xPosStr = null;
            String yPosStr = null;
            String isShapeDefinedStr = null;
            int [] xintArr = null;
            int [] yintArr = null;

            DomElement name = spectrumElem.getChild("name");
            if (name != null) {
                nameStr = name.getValue();
            }

            DomElement xPositions = spectrumElem.getChild("xPixelPolygonPositions");
            if (xPositions != null) {
                xPosStr = xPositions.getValue();
            }

            DomElement yPositions = spectrumElem.getChild("yPixelPolygonPositions");
            if (yPositions != null) {
                yPosStr = yPositions.getValue();
            }

            DomElement isShapeDefined = spectrumElem.getChild("isShapeDefined");
            if (isShapeDefined != null) {
                isShapeDefinedStr = isShapeDefined.getValue();
            }

            if (xPosStr != null)
            {
                xPosStr = xPosStr.replaceAll("\\[|]|\\s", "");
                String [] stringTokens = xPosStr.split(",");
                xintArr = Stream.of(stringTokens).mapToInt(Integer::parseInt).toArray();
            }
            if (yPosStr != null)
            {
                yPosStr = yPosStr.replaceAll("\\[|]|\\s", "");
                String [] stringTokens = yPosStr.split(",");
                yintArr = Stream.of(stringTokens).mapToInt(Integer::parseInt).toArray();
            }

            final SpectrumInput spectrum = new SpectrumInput(nameStr, xintArr, yintArr);
            if (isShapeDefinedStr != null)
            {
                spectrum.setIsShapeDefined(Boolean.parseBoolean(isShapeDefinedStr));
            }
            spectrumList.add(spectrum);
        }

        return spectrumList.toArray(new SpectrumInput[0]);
    }


    @Override
    public void convertValueToDom(Object value, DomElement parentElement) throws ConversionException {
        final SpectrumInput[] spectrumInputs = (SpectrumInput[])value;

        if(spectrumInputs != null) {
            for (SpectrumInput spectrumInput : spectrumInputs) {
                DomElement spectrum = parentElement.createChild("spectrum");

                final DomElement name = spectrum.createChild("name");
                name.setValue(spectrumInput.getName());

                DomElement xPixelPolygonPositions = spectrum.createChild("xPixelPolygonPositions");
                xPixelPolygonPositions.setValue(Arrays.toString(spectrumInput.getXPixelPolygonPositions()));

                DomElement yPixelPolygonPositions = spectrum.createChild("yPixelPolygonPositions");
                yPixelPolygonPositions.setValue(Arrays.toString(spectrumInput.getYPixelPolygonPositions()));

                DomElement isShapeDefined = spectrum.createChild("isShapeDefined");
                isShapeDefined.setValue(Boolean.toString(spectrumInput.getIsShapeDefined()));
            }
        }
    }
}
