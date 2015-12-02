/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.Map;

@OperatorMetadata(
        alias = "SaviOp",
        category = "Optical/Thematic Land Processing",
        description = "This retrieves the Soil-Adjusted Vegetation Index (SAVI).",
        authors = "Cosmin Cara",
        copyright = "Copyright (C) 2015 by CS ROMANIA")
public class SaviOp extends Operator {

    // constants
    public static final String SAVI_BAND_NAME = "savi";
    public static final String SAVI_FLAGS_BAND_NAME = "savi_flags";
    public static final String SAVI_ARITHMETIC_FLAG_NAME = "SAVI_ARITHMETIC";
    public static final String SAVI_LOW_FLAG_NAME = "SAVI_NEGATIVE";
    public static final String SAVI_HIGH_FLAG_NAME = "SAVI_SATURATION";
    public static final int SAVI_ARITHMETIC_FLAG_VALUE = 1;
    public static final int SAVI_LOW_FLAG_VALUE = 1 << 1;
    public static final int SAVI_HIGH_FLAG_VALUE = 1 << 2;

    @SourceProduct(alias = "source", description="The source product.")
    private Product sourceProduct;
    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Soil brigthness correction factor", defaultValue = "0.5F", description = "The amount or cover of green vegetation.")
    private float soilCorrectionFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the SAVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the SAVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;


    @Override
    public void initialize() throws OperatorException {
        loadSourceBands(sourceProduct);
        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();
        targetProduct = new Product("savi", sourceProduct.getProductType() + "_savi", sceneWidth, sceneHeight);

        Band saviOutputBand = new Band(SAVI_BAND_NAME, ProductData.TYPE_FLOAT32, sceneWidth,
                sceneHeight);
        targetProduct.addBand(saviOutputBand);

        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);

        FlagCoding saviFlagCoding = createSaviFlagCoding();
        targetProduct.getFlagCodingGroup().add(saviFlagCoding);

        Band saviFlagsOutputBand = new Band(SAVI_FLAGS_BAND_NAME, ProductData.TYPE_INT32,
                sceneWidth, sceneHeight);
        saviFlagsOutputBand.setDescription("savi specific flags");
        saviFlagsOutputBand.setSampleCoding(saviFlagCoding);
        targetProduct.addBand(saviFlagsOutputBand);

        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        targetProduct.addMask(SAVI_ARITHMETIC_FLAG_NAME, (SAVI_FLAGS_BAND_NAME + "." + SAVI_ARITHMETIC_FLAG_NAME),
                "An arithmetic exception occurred.",
                Color.red.brighter(), 0.7);
        targetProduct.addMask(SAVI_LOW_FLAG_NAME, (SAVI_FLAGS_BAND_NAME + "." + SAVI_LOW_FLAG_NAME),
                "savi value is too low.",
                Color.red, 0.7);
        targetProduct.addMask(SAVI_HIGH_FLAG_NAME, (SAVI_FLAGS_BAND_NAME + "." + SAVI_HIGH_FLAG_NAME),
                "savi value is too high.",
                Color.red.darker(), 0.7);
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing savi", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile savi = targetTiles.get(targetProduct.getBand(SAVI_BAND_NAME));
            Tile saviFlags = targetTiles.get(targetProduct.getBand(SAVI_FLAGS_BAND_NAME));

            float saviValue;
            int saviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    saviValue = (nir - red) / (nir + red + soilCorrectionFactor) * (1 + soilCorrectionFactor);
                    saviFlagsValue = 0;
                    if (Float.isNaN(saviValue) || Float.isInfinite(saviValue)) {
                        saviFlagsValue |= SAVI_ARITHMETIC_FLAG_VALUE;
                        saviValue = 0.0f;
                    }
                    if (saviValue < 0.0f) {
                        saviFlagsValue |= SAVI_LOW_FLAG_VALUE;
                    }
                    if (saviValue > 1.0f) {
                        saviFlagsValue |= SAVI_HIGH_FLAG_VALUE;
                    }
                    savi.setSample(x, y, saviValue);
                    saviFlags.setSample(x, y, saviFlagsValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    private void loadSourceBands(Product product) throws OperatorException {
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product);
            getLogger().info("Using band '" + redSourceBand + "' as red input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(800, 900, product);
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
    }

    // package local for testing reasons only
    static String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float bestBandLowerDelta = Float.MAX_VALUE;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float lowerDelta = bandWavelength - minWavelength;
                if (lowerDelta < bestBandLowerDelta && bandWavelength <= maxWavelength && bandWavelength >= minWavelength) {
                    bestBand = band.getName();
                    bestBandLowerDelta = lowerDelta;
                }
            }
        }
        return bestBand;
    }

    private static FlagCoding createSaviFlagCoding() {

        FlagCoding saviFlagCoding = new FlagCoding("savi_flags");
        saviFlagCoding.setDescription("SAVI Flag Coding");

        MetadataAttribute attribute;

        attribute = new MetadataAttribute(SAVI_ARITHMETIC_FLAG_NAME, ProductData.TYPE_INT32);
        attribute.getData().setElemInt(SAVI_ARITHMETIC_FLAG_VALUE);
        attribute.setDescription("SAVI value calculation failed due to an arithmetic exception");
        saviFlagCoding.addAttribute(attribute);

        attribute = new MetadataAttribute(SAVI_LOW_FLAG_NAME, ProductData.TYPE_INT32);
        attribute.getData().setElemInt(SAVI_LOW_FLAG_VALUE);
        attribute.setDescription("SAVI value is too low");
        saviFlagCoding.addAttribute(attribute);

        attribute = new MetadataAttribute(SAVI_HIGH_FLAG_NAME, ProductData.TYPE_INT32);
        attribute.getData().setElemInt(SAVI_HIGH_FLAG_VALUE);
        attribute.setDescription("SAVI value is too high");
        saviFlagCoding.addAttribute(attribute);

        return saviFlagCoding;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SaviOp.class);
        }

    }
}
