/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.idepix.util.AlgorithmSelector;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Operator for aerosol retrieval from S2 MSI following USwansea algorithm as used in GlobAlbedo project.
 */
@OperatorMetadata(alias = "AerosolRetrieval.S2.Master",
        description = "Aerosol retrieval from S2 MSI following USwansea algorithm as used in GlobAlbedo project.",
        authors = "Olaf Danne, Marco Zuehlke, Grit Kirches, Andreas Heckel",
        version = "1.0",
        copyright = "(C) 2010, 2016 by University Swansea and Brockmann Consult")
public class S2AerosolRetrievalMasterOp extends Operator {

    public static final Product EMPTY_PRODUCT = new Product("empty", "empty", 0, 0);

    @SourceProduct
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(defaultValue = "true")
    private boolean copyToaReflBands;
    @Parameter(defaultValue = "false")
    private boolean filling;
    @Parameter(defaultValue = "false")
    private boolean upscaling;
    @Parameter(defaultValue = "1")
    private int soilSpecId;
    @Parameter(defaultValue = "5")
    private int vegSpecId;
    @Parameter(defaultValue = "9")
    private int scale;
    @Parameter(defaultValue = "0.3")
    private float ndviThr;

    @Override
    public void initialize() throws OperatorException {
        final boolean inputProductIsValid = S2IdepixUtils.validateInputProduct(sourceProduct, AlgorithmSelector.MSI);
        if (!inputProductIsValid) {
            throw new OperatorException(S2IdepixConstants.INPUT_INCONSISTENCY_ERROR_MESSAGE);
        }

        Dimension targetDim = ImageManager.getPreferredTileSize(sourceProduct);         // original grid
        Dimension aotDim = new Dimension(targetDim.width / 9, targetDim.height / 9);    // AOT grid (downscaled)
        RenderingHints rhTarget = new RenderingHints(GPF.KEY_TILE_SIZE, targetDim);
        RenderingHints rhAot = new RenderingHints(GPF.KEY_TILE_SIZE, aotDim);

        S2AerosolMsiPreparationOp s2msiPrepOp = new S2AerosolMsiPreparationOp();
        s2msiPrepOp.setParameterDefaultValues();
        s2msiPrepOp.setSourceProduct(sourceProduct);
        final Product extendedSourceProduct = s2msiPrepOp.getTargetProduct();

        setTargetProduct(extendedSourceProduct);    // first test break

//        Map<String, Object> aotParams = new HashMap<>(4);
//        aotParams.put("soilSpecId", soilSpecId);
//        aotParams.put("vegSpecId", vegSpecId);
//        aotParams.put("scale", scale);
//        aotParams.put("ndviThreshold", ndviThr);
//
//        Product aotDownscaledProduct =
//                GPF.createProduct(OperatorSpi.getOperatorAlias(S2AerosolOp.class), aotParams, extendedSourceProduct, rhAot);
//
//        Product aotGapFilledProduct = aotDownscaledProduct;
//        if (filling) {
//            Map<String, Product> gapFillSourceProducts = new HashMap<>(2);
//            gapFillSourceProducts.put("aotProduct", aotDownscaledProduct);
//            aotGapFilledProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(S2AerosolGapFillingOp.class),
//                                                    GPF.NO_PARAMS, gapFillSourceProducts);
//        }
//
//        targetProduct = aotGapFilledProduct;     // second test break: set upscaling to false
//        if (upscaling) {
//            Map<String, Product> upsclProducts = new HashMap<>(2);
//            upsclProducts.put("lowresProduct", aotGapFilledProduct);
//            upsclProducts.put("hiresProduct", extendedSourceProduct);
//            Map<String, Object> sclParams = new HashMap<>(1);
//            sclParams.put("scale", scale);
//            Product aotOrigResolutionProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(S2AerosolUpscaleOp.class),
//                                                                 sclParams, upsclProducts, rhTarget);
//
//            targetProduct = mergeToTargetProduct(extendedSourceProduct, aotOrigResolutionProduct);
//            ProductUtils.copyPreferredTileSize(extendedSourceProduct, targetProduct);
//        }
//        setTargetProduct(targetProduct);
    }

    private Product mergeToTargetProduct(Product reflProduct, Product aotOriginalResolutionProduct) {
        final String targetProductName = reflProduct.getName() + "_AOT";
        final String targetProductType = reflProduct.getProductType() + " GlobAlbedo AOT";
        final int rasterWidth = reflProduct.getSceneRasterWidth();
        final int rasterHeight = reflProduct.getSceneRasterHeight();

        Product targetProduct = new Product(targetProductName, targetProductType, rasterWidth, rasterHeight);
        targetProduct.setStartTime(reflProduct.getStartTime());
        targetProduct.setEndTime(reflProduct.getEndTime());
        ProductUtils.copyMetadata(aotOriginalResolutionProduct, targetProduct);
        ProductUtils.copyTiePointGrids(reflProduct, targetProduct);
        ProductUtils.copyGeoCoding(reflProduct, targetProduct);
        ProductUtils.copyFlagBands(reflProduct, targetProduct, true);
        ProductUtils.copyFlagBands(aotOriginalResolutionProduct, targetProduct, true);

        for (Band sourceBand : reflProduct.getBands()) {
            final String sourceBandName = sourceBand.getName();
            final boolean copyBand = (copyToaReflBands &&
                    !targetProduct.containsBand(sourceBandName) &&
                    sourceBand.getSpectralWavelength() > 0);

            if (copyBand && !targetProduct.containsBand(sourceBandName)) {
                ProductUtils.copyBand(sourceBandName, reflProduct, targetProduct, true);
            }
        }
        for (Band sourceBand : aotOriginalResolutionProduct.getBands()) {
            final String sourceBandName = sourceBand.getName();
            if (!sourceBand.isFlagBand() && !targetProduct.containsBand(sourceBandName)) {
                ProductUtils.copyBand(sourceBandName, aotOriginalResolutionProduct, targetProduct, true);
            }
        }
        return targetProduct;
    }

    /**
     * The SPI is used to register this operator in the graph processing framework
     * via the SPI configuration file
     * {@code META-INF/services/org.esa.beam.framework.gpf.OperatorSpi}.
     * This class may also serve as a factory for new operator instances.
     *
     * @see OperatorSpi#createOperator()
     * @see OperatorSpi#createOperator(Map, Map)
     */
    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2AerosolRetrievalMasterOp.class);
        }
    }
}
