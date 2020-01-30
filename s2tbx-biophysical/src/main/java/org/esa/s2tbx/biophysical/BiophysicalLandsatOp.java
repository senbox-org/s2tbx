/*
 * Copyright (C) 2012 CSSI (foss-contact@c-s.fr)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.biophysical;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.SampleCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VirtualBand;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.pointop.PixelOperator;
import org.esa.snap.core.gpf.pointop.ProductConfigurer;
import org.esa.snap.core.gpf.pointop.Sample;
import org.esa.snap.core.gpf.pointop.SourceSampleConfigurer;
import org.esa.snap.core.gpf.pointop.TargetSampleConfigurer;
import org.esa.snap.core.gpf.pointop.WritableSample;
import org.esa.snap.core.util.math.MathUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Julien Malik (CS SI)
 */
@OperatorMetadata(
        alias = "BiophysicalLandsat8Op",
        category = "Optical/Thematic Land Processing/Biophysical Processor (LAI, fAPAR...)",
        description = "The 'Biophysical Processor' operator retrieves LAI from atmospherically corrected Landsat8 products",
        authors = "CS",
        copyright = "CS SI (foss-contact@c-s.fr)")
public class BiophysicalLandsatOp extends PixelOperator {

    private Map<BiophysicalVariable, BiophysicalAlgo> algos = new HashMap<>();

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @Parameter(defaultValue = "true", label = "Compute LAI", description = "Compute LAI (Leaf Area Index)")
    private boolean computeLAI;

    @Parameter(defaultValue = "true", label = "Compute FAPAR", description = "Compute FAPAR (Fraction of Absorbed Photosynthetically Active Radiation)")
    private boolean computeFapar;

    @Parameter(defaultValue = "true", label = "Compute FVC", description = "Compute FVC (Fraction of Vegetation Cover)")
    private boolean computeFcover;




    /**
     * Configures all source samples that this operator requires for the computation of target samples.
     * Source sample are defined by using the provided {@link SourceSampleConfigurer}.
     * <p/>
     * <p/> The method is called by {@link #initialize()}.
     *
     * @param sampleConfigurer The configurer that defines the layout of a pixel.
     * @throws OperatorException If the source samples cannot be configured.
     */
    @Override
    protected void configureSourceSamples(SourceSampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(L2BInput.GREEN.getIndex(), findBandLikeS2(sourceProduct, Landsat8OLIBandConstant.GREEN));
        sampleConfigurer.defineSample(L2BInput.RED.getIndex(), findBandLikeS2(sourceProduct, Landsat8OLIBandConstant.RED));
        sampleConfigurer.defineSample(L2BInput.NIR.getIndex(), findBandLikeS2(sourceProduct, Landsat8OLIBandConstant.NIR));
        sampleConfigurer.defineSample(L2BInput.SWIR1.getIndex(), findBandLikeS2(sourceProduct, Landsat8OLIBandConstant.SWIR1));
        sampleConfigurer.defineSample(L2BInput.SWIR2.getIndex(), findBandLikeS2(sourceProduct, Landsat8OLIBandConstant.SWIR2));
        sampleConfigurer.defineSample(L2BInput.VIEW_ZENITH.getIndex(), L2BInput.VIEW_ZENITH.getBandName());
        sampleConfigurer.defineSample(L2BInput.SUN_ZENITH.getIndex(), L2BInput.SUN_ZENITH.getBandName());
        sampleConfigurer.defineSample(L2BInput.SUN_AZIMUTH.getIndex(), L2BInput.SUN_AZIMUTH.getBandName());
        sampleConfigurer.defineSample(L2BInput.VIEW_AZIMUTH.getIndex(), L2BInput.VIEW_AZIMUTH.getBandName());
    }

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        loadAuxData();
    }

    private void loadAuxData() throws OperatorException {

        try {
            for (BiophysicalVariable biophysicalVariable : BiophysicalVariable.values()) {
                if (BiophysicalModel.LANDSAT8.computesVariable(biophysicalVariable) && isComputed(biophysicalVariable)) {
                    algos.put(biophysicalVariable, new BiophysicalAlgo(BiophysicalAuxdata.makeBiophysicalAuxdata(biophysicalVariable, BiophysicalModel.LANDSAT8)));
                }
            }
        } catch(IOException e) {
            throw new OperatorException(e.getMessage());
        }
    }

    /**
     * Configures all target samples computed by this operator.
     * Target samples are defined by using the provided {@link TargetSampleConfigurer}.
     * <p/>
     * <p/> The method is called by {@link #initialize()}.
     *
     * @param sampleConfigurer The configurer that defines the layout of a pixel.
     * @throws OperatorException If the target samples cannot be configured.
     */
    @Override
    protected void configureTargetSamples(TargetSampleConfigurer sampleConfigurer) throws OperatorException {

        int sampleIndex = 0;
        for (BiophysicalVariable biophysicalVariable : BiophysicalVariable.values()) {
            if (BiophysicalModel.LANDSAT8.computesVariable(biophysicalVariable) && isComputed(biophysicalVariable)) {
                sampleConfigurer.defineSample(sampleIndex, biophysicalVariable.getSampleName());
                sampleIndex++;
                sampleConfigurer.defineSample(sampleIndex, biophysicalVariable.getSampleName() + "_flags");
                sampleIndex++;
            }
        }
    }

    /**
     * Configures the target product via the given {@link ProductConfigurer}. Called by {@link #initialize()}.
     * <p/>
     * Client implementations of this method usually add product components to the given target product, such as
     * {@link Band bands} to be computed by this operator,
     * {@link VirtualBand virtual bands},
     * {@link Mask masks}
     * or {@link SampleCoding sample codings}.
     * <p/>
     * The default implementation retrieves the (first) source product and copies to the target product
     * <ul>
     * <li>the start and stop time by calling {@link ProductConfigurer#copyTimeCoding()},</li>
     * <li>all tie-point grids by calling {@link ProductConfigurer#copyTiePointGrids(String...)},</li>
     * <li>the geo-coding by calling {@link ProductConfigurer#copyGeoCoding()}.</li>
     * </ul>
     * <p/>
     * Clients that require a similar behaviour in their operator shall first call the {@code super} method
     * in their implementation.
     *
     * @param productConfigurer The target product configurer.
     * @throws OperatorException If the target product cannot be configured.
     * @see Product#addBand(Band)
     * @see Product#addBand(String, String)
     * @see Product#addTiePointGrid(TiePointGrid)
     * @see Product#getMaskGroup()
     */
    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        productConfigurer.copyMetadata();
        productConfigurer.copyMasks();

        Product tp = productConfigurer.getTargetProduct();
        // todo setDescription

        for (BiophysicalVariable biophysicalVariable : BiophysicalVariable.values()) {
            if (BiophysicalModel.LANDSAT8.computesVariable(biophysicalVariable) && isComputed(biophysicalVariable)) {
                // Add biophysical variable band
                final Band biophysicalVariableBand = tp.addBand(biophysicalVariable.getBandName(), ProductData.TYPE_FLOAT32);
                biophysicalVariableBand.setDescription(biophysicalVariable.getDescription());
                biophysicalVariableBand.setUnit(biophysicalVariable.getUnit());
                // todo better setDescription
                // todo setValidPixelExpression
                // todo setNoDataValueUsed (see flhmci)
                // todo setNoDataValue (see flhmci)


                // Add corresponding flag band
                String flagBandName = String.format("%s_flags", biophysicalVariable.getBandName());
                final Band biophysicalVariableFlagBand = tp.addBand(flagBandName, ProductData.TYPE_UINT8);
                final FlagCoding biophysicalVariableFlagCoding = new FlagCoding(flagBandName);
                for (BiophysicalFlag flagDef : BiophysicalFlag.values()) {
                    biophysicalVariableFlagCoding.addFlag(flagDef.getName(), flagDef.getFlagValue(), flagDef.getDescription());
                }
                tp.getFlagCodingGroup().add(biophysicalVariableFlagCoding);
                biophysicalVariableFlagBand.setSampleCoding(biophysicalVariableFlagCoding);

                // Add a mask for each flag
                for (BiophysicalFlag flagDef : BiophysicalFlag.values()) {
                    String maskName = String.format("%s_%s", biophysicalVariable.getBandName(), flagDef.getName().toLowerCase());
                    tp.addMask(maskName,
                               String.format("%s.%s", flagBandName, flagDef.getName()),
                               flagDef.getDescription(),
                               flagDef.getColor(), flagDef.getTransparency());
                }
            }
        }
    }

    /**
     * Computes the target samples from the given source samples.
     * <p/>
     * The number of source/target samples is the maximum defined sample index plus one. Source/target samples are defined
     * by using the respective sample configurer in the
     * {@link #configureSourceSamples(SourceSampleConfigurer) configureSourceSamples} and
     * {@link #configureTargetSamples(TargetSampleConfigurer) configureTargetSamples} methods.
     * Attempts to read from source samples or write to target samples at undefined sample indices will
     * cause undefined behaviour.
     *
     * @param x             The current pixel's X coordinate.
     * @param y             The current pixel's Y coordinate.
     * @param sourceSamples The source samples (= source pixel).
     * @param targetSamples The target samples (= target pixel).
     */
    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {

        double[] input = new double[8];
        for (int i = 0; i < 8; ++i) {
            input[i] = sourceSamples[i].getDouble();
        }

        input[L2BInput.GREEN.getIndex()] = sourceSamples[L2BInput.GREEN.getIndex()].getDouble();
        input[L2BInput.RED.getIndex()] = sourceSamples[L2BInput.RED.getIndex()].getDouble();
        input[L2BInput.NIR.getIndex()] = sourceSamples[L2BInput.NIR.getIndex()].getDouble();
        input[L2BInput.SWIR1.getIndex()] = sourceSamples[L2BInput.SWIR1.getIndex()].getDouble();
        input[L2BInput.SWIR2.getIndex()] = sourceSamples[L2BInput.SWIR2.getIndex()].getDouble();

        // cos(View_Zenith)
        input[5] = Math.cos(MathUtils.DTOR * sourceSamples[L2BInput.VIEW_ZENITH.getIndex()].getDouble());
        // cos(Sun_Zenith)
        input[6] = Math.cos(MathUtils.DTOR * sourceSamples[L2BInput.SUN_ZENITH.getIndex()].getDouble());
        // cos(Relative_Azimuth)
        input[7] = Math.cos(MathUtils.DTOR * (sourceSamples[L2BInput.SUN_AZIMUTH.getIndex()].getDouble() - sourceSamples[L2BInput.VIEW_AZIMUTH.getIndex()].getDouble()));

        int targetIndex = 0;
        for (BiophysicalVariable biophysicalVariable : BiophysicalVariable.values()) {
            if (BiophysicalModel.LANDSAT8.computesVariable(biophysicalVariable) && isComputed(biophysicalVariable)) {
                BiophysicalAlgo algo = algos.get(biophysicalVariable);
                BiophysicalAlgo.Result result = algo.process(input);
                targetSamples[targetIndex].set(result.getOutputValue());
                targetIndex++;

                targetSamples[targetIndex].set(BiophysicalFlag.INPUT_OUT_OF_RANGE.getBitIndex(), result.isInputOutOfRange());
                targetSamples[targetIndex].set(BiophysicalFlag.OUTPUT_THRESHOLDED_TO_MIN_OUTPUT.getBitIndex(), result.isOutputThresholdedToMinOutput());
                targetSamples[targetIndex].set(BiophysicalFlag.OUTPUT_THRESHOLDED_TO_MAX_OUTPUT.getBitIndex(), result.isOutputThresholdedToMaxOutput());
                targetSamples[targetIndex].set(BiophysicalFlag.OUTPUT_TOO_LOW.getBitIndex(), result.isOutputTooLow());
                targetSamples[targetIndex].set(BiophysicalFlag.OUTPUT_TOO_HIGH.getBitIndex(), result.isOutputTooHigh());
                targetIndex++;
            }
        }
    }

    private boolean isComputed(BiophysicalVariable biophysicalVariable) {
        switch (biophysicalVariable) {
            case LAI:
                return computeLAI;
            case FAPAR:
                return computeFapar;
            case FCOVER:
                return computeFcover;
            default:
                // this is a programming error
                throw new AssertionError(String.format("Wrong biophysical variable value %s", biophysicalVariable));
        }
    }

    static String findBandLikeS2(Product product, Landsat8OLIBandConstant oliBand) {
        return findWaveBand(product, true, oliBand.getWavelength(), oliBand.getBandwidth(), oliBand.getPhysicalName());
    }

    // package local for testing reasons only
    static String findWaveBand(Product product, boolean fail, double centralWavelength, double maxDeltaWavelength, String... bandNames) {
        Band[] bands = product.getBands();
        String bestBand = null;
        double minDelta = Double.MAX_VALUE;
        for (Band band : bands) {
            double bandWavelength = band.getSpectralWavelength();
            if (bandWavelength > 0.0) {
                double delta = Math.abs(bandWavelength - centralWavelength);
                if (delta < minDelta && delta <= maxDeltaWavelength) {
                    bestBand = band.getName();
                    minDelta = delta;
                }
            }
        }
        if (bestBand != null) {
            return bestBand;
        }
        for (String bandName : bandNames) {
            Band band = product.getBand(bandName);
            if (band != null) {
                return band.getName();
            }
        }
        if (fail) {
            throw new OperatorException("Missing band at " + centralWavelength + " nm");
        }
        return null;
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(BiophysicalLandsatOp.class);
        }
    }

    public enum L2BInput {
        GREEN(0, "green"),
        RED(1, "red"),
        NIR(2, "near_infrared"),
        SWIR1(3, "B6"),
        SWIR2(4, "B7"),
        VIEW_ZENITH(5, "view_zenith_mean"),
        SUN_ZENITH(6, "sun_zenith"),
        SUN_AZIMUTH(7, "sun_azimuth"),
        VIEW_AZIMUTH(8, "view_azimuth_mean");

        private final int index;
        private final String bandName;

        L2BInput(int index, String bandName) {
            this.index = index;
            this.bandName = bandName;
        }

        public int getIndex() {
            return this.index;
        }

        public String getBandName() {
            return this.bandName;
        }
    }

    public enum Landsat8OLIBandConstant {

        COASTAL_AEROSOL("coastal_aerosol",0,440,20),
        BLUE("blue",1,480,60),
        GREEN("green",2,560,60),
        RED("red",3,655,30),
        NIR("near_infrared",4,865,30),
        SWIR1("swir_1",5,1610,80),
        SWIR2("swir_2",6,2200,180),
        PANCHROMATIC("panchromatic",7,590,180),
        CIRRUS("cirrus",8,1370,20),
        TIR1("thermal_infrared_(tirs)_1",9,10895,590),
        TIR2("thermal_infrared_(tirs)_2",10,12005,1010);

        private String physicalName;
        private int bandIndex;
        private double wavelength;
        private double bandWidth;

        Landsat8OLIBandConstant(String physicalName,
                        int bandIndex,
                        double wavelength,
                        double bandWidth ) {
            this.physicalName = physicalName;
            this.bandIndex = bandIndex;
            this.wavelength = wavelength;
            this.bandWidth = bandWidth;
        }

        public String getPhysicalName() {
            return physicalName;
        }

        public int getBandIndex() {
            return bandIndex;
        }

        public double getWavelength() {
            return wavelength;
        }

        public double getBandwidth() {
            return bandWidth;
        }
    }

}
