/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.radiometry;

import org.esa.s2tbx.radiometry.annotations.BandParameter;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base class for radiometric indices.
 *
 * @author Dragos Mihailescu
 * @author Cosmin Cara
 *
 * @since 5.0.0
 */
public abstract class BaseIndexOp extends Operator {

    public static final String RESAMPLE_NONE = "None";
    public static final String RESAMPLE_LOWEST = "Lowest resolution";
    public static final String RESAMPLE_HIGHEST = "Highest resolution";

    protected static final String FLAGS_BAND_NAME = "flags";

    protected static final String ARITHMETIC_FLAG_NAME = "ARITHMETIC";
    protected static final String LOW_FLAG_NAME = "NEGATIVE";
    protected static final String HIGH_FLAG_NAME = "SATURATION";

    protected static final int ARITHMETIC_FLAG_VALUE = 1;
    protected static final int LOW_FLAG_VALUE = 1 << 1;
    protected static final int HIGH_FLAG_VALUE = 1 << 2;

    protected float lowValueThreshold;
    protected float highValueThreshold;

    @SourceProduct(alias = "source", description = "The source product.")
    protected Product sourceProduct;
    @TargetProduct
    protected Product targetProduct;
    @Parameter(label = "Resample Type",
            description = "If selected band s differ in size, the resample method used before computing the index",
            defaultValue = RESAMPLE_NONE, valueSet = { RESAMPLE_NONE, RESAMPLE_LOWEST, RESAMPLE_HIGHEST })
    protected String resampleType;
    @Parameter(alias = "upsampling",
            label = "Upsampling Method",
            description = "The method used for interpolation (upsampling to a finer resolution).",
            valueSet = {"Nearest", "Bilinear", "Bicubic"},
            defaultValue = "Nearest")
    protected String upsamplingMethod;
    @Parameter(alias = "downsampling",
            label = "Downsampling Method",
            description = "The method used for aggregation (downsampling to a coarser resolution).",
            valueSet = {"First", "Min", "Max", "Mean", "Median"},
            defaultValue = "First")
    private String downsamplingMethod;
    protected String[] sourceBandNames;
    private List<Field> bandFields;

    private FlagCoding flagCoding;
    private List<MaskDescriptor> maskDescriptors;

    protected BaseIndexOp() {
        maskDescriptors = new ArrayList<>();
        bandFields = Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Parameter.class) != null
                        && f.getAnnotation(BandParameter.class) != null)
                .collect(Collectors.toList());
        lowValueThreshold = 0.0f;
        highValueThreshold = 1.0f;
    }

    public abstract String getBandName();

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceProduct == null) {
            throw new OperatorException("Source product not set");
        }
        loadSourceBands(sourceProduct);
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Source bands not set");
        }
        int sceneWidth = 0, sceneHeight = 0;
        //GeoCoding targetGeocoding = null;

        boolean resampleNeeded = !RESAMPLE_NONE.equals(this.resampleType);
        if (resampleNeeded) {
            for (String bandName : this.sourceBandNames) {
                Band band = this.sourceProduct.getBand(bandName);
                int bandRasterWidth = band.getRasterWidth();
                if (RESAMPLE_HIGHEST.equals(this.resampleType)) {
                    if (sceneWidth < bandRasterWidth) {
                        sceneWidth = bandRasterWidth;
                        sceneHeight = band.getRasterHeight();
                    }
                } else {
                    if (sceneWidth == 0 || sceneWidth >= bandRasterWidth) {
                        sceneWidth = bandRasterWidth;
                        sceneHeight = band.getRasterHeight();
                    }
                }
            }
            this.sourceProduct = resample(this.sourceProduct, sceneWidth, sceneHeight);
            //targetGeocoding = this.sourceProduct.getSceneGeoCoding();
        } else {
            // Issue only when used from Graph Builder: Resample not asked for: must check bands resolution in case of multisize product
            if (this.sourceProduct.isMultiSize()) {
                Band band0 = this.sourceProduct.getBand(this.sourceBandNames[0]);
                Dimension dim0 = band0.getRasterSize();
                boolean forceResampling = false;
                for (String bandName : this.sourceBandNames) {
                    if (!this.sourceProduct.getBand(bandName).getRasterSize().equals(dim0)){
                        //throw new OperatorException("Source bands with different resolutions. Define a resampling method before using the operator.");
                        //instead of launching an error, set resampling to lowest
                        forceResampling = true;
                        break;
                    }
                }

                if(forceResampling) {
                    for (String bandName : this.sourceBandNames) {
                        this.resampleType = RESAMPLE_LOWEST;
                        // TODO How to trigger UI update in order for this to be also visible to the user ?

                        Band band = this.sourceProduct.getBand(bandName);
                        int bandRasterWidth = band.getRasterWidth();
                        if (sceneWidth == 0 || sceneWidth >= bandRasterWidth) {
                            sceneWidth = bandRasterWidth;
                            sceneHeight = band.getRasterHeight();
                        }
                    }
                    this.sourceProduct = resample(this.sourceProduct, sceneWidth, sceneHeight);
                    //targetGeocoding = this.sourceProduct.getSceneGeoCoding();
                } else {
                    // All the bands have the same resolution.
                    // For the target product use the common resolution as it can be different from the sourceProduct.
                    sceneWidth = band0.getRasterWidth();
                    sceneHeight = band0.getRasterHeight();
                    //targetGeocoding = band0.getGeoCoding();
                }
            } else {
                sceneWidth = sourceProduct.getSceneRasterWidth();
                sceneHeight = sourceProduct.getSceneRasterHeight();
                //targetGeocoding = sourceProduct.getSceneGeoCoding();
            }
        }

        initDefaultMasks();

        String name = getBandName();

        targetProduct = new Product(name, sourceProduct.getProductType() + "_" + name, sceneWidth, sceneHeight);

        // SIITBX-290: Radiometric Index Operators do not handle GeoCoding correctly, ProductUtils should be used
        /*if (targetGeocoding instanceof TiePointGeoCoding) {
            for (TiePointGrid tiePointGrid : sourceProduct.getTiePointGrids()) {
                targetProduct.addTiePointGrid(tiePointGrid);
            }
        }
        targetProduct.setSceneGeoCoding(targetGeocoding);*/
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);

        Band outputBand = new Band(name, ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        targetProduct.addBand(outputBand);

        Band flagsOutputBand = new Band(FLAGS_BAND_NAME, ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        flagsOutputBand.setDescription(name + " specific flags");

        FlagCoding flagCoding = initFlagCoding();
        flagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(flagsOutputBand);

        for (MaskDescriptor maskDescriptor : getMaskDescriptors()) {
            targetProduct.addMask(maskDescriptor.name, maskDescriptor.expression, maskDescriptor.description, maskDescriptor.color, maskDescriptor.transparency);
        }

    }

    @Override
    public Product getSourceProduct() {
        return this.sourceProduct;
    }

    protected float computeFlag(int x, int y, float computedValue, Tile flagTile) {
        int flag = 0;
        if (Float.isNaN(computedValue) || Float.isInfinite(computedValue)) {
            flag = ARITHMETIC_FLAG_VALUE;
            computedValue = 0.0f;
        } else if (computedValue < this.lowValueThreshold) {
            flag |= LOW_FLAG_VALUE;
        } else if (computedValue > this.highValueThreshold) {
            flag |= HIGH_FLAG_VALUE;
        }
        flagTile.setSample(x, y, flag);
        return computedValue;
    }

    protected void loadSourceBands(Product product) throws OperatorException {
        final List<String> bands = new ArrayList<>();
        bandFields.forEach(bandField -> {
            Parameter paramAnnotation = bandField.getAnnotation(Parameter.class);
            BandParameter bandAnnotation = bandField.getAnnotation(BandParameter.class);
            try {
                bandField.setAccessible(true);
                Object value = bandField.get(this);
                if (value == null) {
                    String bandName = findBand((int) bandAnnotation.minWavelength(),
                            (int) bandAnnotation.maxWavelength(),
                            product);
                    if (bandName != null) {
                        bandField.set(this, bandName);
                        bands.add(bandName);
                        getLogger().fine(String.format("Using band '%s' as %s",
                                bandName, paramAnnotation.label()));
                    } else {
                        throw new OperatorException(
                                String.format("Unable to find band that could be used as %s. Please specify band.",
                                        paramAnnotation.label()));
                    }
                } else {
                    bands.add(value.toString());
                }
            } catch (IllegalAccessException e) {
                getLogger().severe(e.getMessage());
            }

        });
        this.sourceBandNames = bands.toArray(new String[bands.size()]);
    }

    private void addMaskDescriptor(String name, String expression, String description, Color color, double transparency) {
        maskDescriptors.add(new MaskDescriptor(name, expression, description, color, transparency));
    }

    private List<MaskDescriptor> getMaskDescriptors() { return maskDescriptors; }

    private void addFlagDescriptor(String name, int value, String description) {
        MetadataAttribute attribute = new MetadataAttribute(name, ProductData.TYPE_INT32);
        attribute.getData().setElemInt(value);
        attribute.setDescription(description);
        flagCoding.addAttribute(attribute);
    }

    private Product resample(Product source, int targetWidth, int targetHeight) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("referenceBandName", null);
        parameters.put("targetWidth", targetWidth);
        parameters.put("targetHeight", targetHeight);
        parameters.put("targetResolution", null);
        if (RESAMPLE_LOWEST.equals(this.resampleType)) {
            parameters.put("downsampling", this.downsamplingMethod != null ? this.downsamplingMethod : "First");
        } else if (RESAMPLE_HIGHEST.equals(this.resampleType)) {
            parameters.put("upsampling", this.upsamplingMethod != null ? this.upsamplingMethod : "Nearest");
        }
        return GPF.createProduct("Resample", parameters, source);
    }

    private void initDefaultMasks() {
        addMaskDescriptor(ARITHMETIC_FLAG_NAME, FLAGS_BAND_NAME + "." + ARITHMETIC_FLAG_NAME,
                "An arithmetic exception occurred.", Color.red.brighter(), 0.7);
        addMaskDescriptor(LOW_FLAG_NAME, FLAGS_BAND_NAME + "." + LOW_FLAG_NAME,
                "Index value is too low.", Color.red, 0.7);
        addMaskDescriptor(HIGH_FLAG_NAME, FLAGS_BAND_NAME + "." + HIGH_FLAG_NAME,
                "Index value is too high.", Color.red.darker(), 0.7);
    }

    private FlagCoding initFlagCoding() {
        flagCoding = new FlagCoding(FLAGS_BAND_NAME);
        flagCoding.setDescription("Index Flag Coding");
        addFlagDescriptor(ARITHMETIC_FLAG_NAME, ARITHMETIC_FLAG_VALUE, "Value calculation failed due to an arithmetic exception");
        addFlagDescriptor(LOW_FLAG_NAME, LOW_FLAG_VALUE, "Index value is too low");
        addFlagDescriptor(HIGH_FLAG_NAME, HIGH_FLAG_VALUE, "Index value is too high");
        return flagCoding;
    }

    public static String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float minDelta = Float.MAX_VALUE;
        float mean = (minWavelength + maxWavelength) / 2;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float delta = Math.abs(bandWavelength - mean);
                if (delta < minDelta) {
                    bestBand = band.getName();
                    minDelta = delta;
                }
            }
        }
        return bestBand;
    }

    private class MaskDescriptor {

        String name;
        String expression;
        String description;
        Color color;
        double transparency;

        MaskDescriptor(String name, String expression, String description, Color color, double transparency) {
            this.name = name;
            this.expression = expression;
            this.description = description;
            this.color = color;
            this.transparency = transparency;
        }
    }
}
