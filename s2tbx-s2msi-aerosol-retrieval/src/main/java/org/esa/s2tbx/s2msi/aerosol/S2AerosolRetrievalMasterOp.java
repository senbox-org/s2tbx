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

import org.esa.s2tbx.s2msi.aerosol.util.AerosolUtils;
import org.esa.s2tbx.s2msi.idepix.util.AlgorithmSelector;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.s2tbx.s2msi.wv.S2WaterVapourRetrievalOp;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.util.Map;

/**
 * Main Operator for aerosol retrieval from S2 MSI following USwansea algorithm as used in GlobAlbedo project.
 */
@OperatorMetadata(alias = "AerosolRetrieval.S2.Master",
        category = "Optical",
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

    @Parameter(description = "A product to read climatology data from. " +
            "Only required if the aerosol type shall be retrieved. " +
            "If not provided, a default product will be written to the local aux data directory.", notNull = true)
    private String climatologiesFile;

    // todo: add labels, descriptions...
    @Parameter(defaultValue = "true")
    private boolean copyToaReflBands;

    @Parameter(description = "The reflectance bands which shall be considered for aot retrieval." +
            "Note that bands B3 and B11 cannot be left out.",
            defaultValue = "B1,B2,B3,B4,B5,B6,B7,B8,B8A,B11,B12")
    private String[] reflectanceBandNames;

    @Parameter(defaultValue = "true")
    private boolean copyGeometryBands;

    @Parameter(defaultValue = "true", description = "if true, aerosol retrieval is done")
    private boolean computeAerosol;

    @Parameter(defaultValue = "true", description = "if true, water vapour retrieval is done")
    private boolean computeWaterVapour;

    @Parameter(defaultValue = "true", description = "if true, aerosol type retrieval is done")
    private boolean computeAerosolType;

    @Parameter(defaultValue = "true")
    private boolean filling;

    @Parameter(defaultValue = "true")
    private boolean upscaling;

    @Parameter(description = "Full path to S2 Lookup Table.",    // todo: define how to finally specify
            label = "Path to S2 Lookup Table")
    private String pathToLut;

    @Parameter(description = "The downscaling factor for AOT retrieval grid. E.g. 20 for S2 60m --> 1.2km",
            label = "Downscaling factor for AOT retrieval grid",
            defaultValue = "20")
    private int scale;

    @Override
    public void initialize() throws OperatorException {
        final boolean inputProductIsValid = S2IdepixUtils.validateInputProduct(sourceProduct, AlgorithmSelector.MSI);
        if (!inputProductIsValid) {
            throw new OperatorException(S2IdepixConstants.INPUT_INCONSISTENCY_ERROR_MESSAGE);
        }

        S2AerosolMsiPreparationOp s2msiPrepOp = new S2AerosolMsiPreparationOp();
        s2msiPrepOp.setParameterDefaultValues();
        s2msiPrepOp.setSourceProduct(sourceProduct);
        final Product extendedSourceProduct = s2msiPrepOp.getTargetProduct();

        setTargetProduct(extendedSourceProduct);

        Product waterVapourProduct = extendedSourceProduct;
        if (!waterVapourProduct.containsRasterDataNode(InstrumentConsts.WATER_VAPOUR_NAME)) {
            if (computeWaterVapour) {
                final S2WaterVapourRetrievalOp waterVapourRetrievalOp = new S2WaterVapourRetrievalOp();
                waterVapourRetrievalOp.setParameterDefaultValues();
                waterVapourRetrievalOp.setSourceProduct(extendedSourceProduct);
                waterVapourProduct = waterVapourRetrievalOp.getTargetProduct();
            } else {
                String waterVapourExpression = "2500.0";
                Band waterVapourBand = waterVapourProduct.addBand(InstrumentConsts.WATER_VAPOUR_NAME, waterVapourExpression);
                waterVapourBand.setDescription("estimated water vapour");
                waterVapourBand.setNoDataValue(-99999.0);
                waterVapourBand.setNoDataValueUsed(true);
                targetProduct.addBand(waterVapourBand);
            }
        }

        Product aerosolTypeProduct = waterVapourProduct;
        if (!aerosolTypeProduct.containsRasterDataNode(InstrumentConsts.AEROSOL_TYPE_NAME)) {
            if (computeAerosolType) {
                final S2AerosolTypeOp s2AerosolTypeOp = new S2AerosolTypeOp();
                s2AerosolTypeOp.setParameterDefaultValues();
                s2AerosolTypeOp.setSourceProduct(waterVapourProduct);
                s2AerosolTypeOp.setParameter("climatologiesFile", climatologiesFile);
                aerosolTypeProduct = s2AerosolTypeOp.getTargetProduct();
            } else {
                String aerosolTypeExpression = "0.0";
                Band aerosolTypeBand = aerosolTypeProduct.addBand(InstrumentConsts.AEROSOL_TYPE_NAME, aerosolTypeExpression);
                aerosolTypeBand.setDescription("Aerosol Type");
                targetProduct.addBand(aerosolTypeBand);
            }
        }

        Product aotDownscaledProduct = aerosolTypeProduct;
        if (computeAerosol) {
            S2AerosolOp s2AerosolOp = new S2AerosolOp();
            s2AerosolOp.setParameterDefaultValues();
            s2AerosolOp.setSourceProduct(aerosolTypeProduct);
            s2AerosolOp.setParameter("pathToLut", pathToLut);
            s2AerosolOp.setParameter("scale", scale);
            s2AerosolOp.setParameter("reflectanceBandNames", reflectanceBandNames);
            aotDownscaledProduct = s2AerosolOp.getTargetProduct();
        }

        Product aotGapFilledProduct = aotDownscaledProduct;
        if (computeAerosol && filling) {
            S2AerosolGapFillingOp gapFillingOp = new S2AerosolGapFillingOp();
            gapFillingOp.setParameterDefaultValues();
            gapFillingOp.setSourceProduct("aotProduct", aotDownscaledProduct);
            aotGapFilledProduct = gapFillingOp.getTargetProduct();
        }

        targetProduct = aotGapFilledProduct;     // second test break: set upscaling to false
        if (computeAerosol && upscaling) {
            S2AerosolUpscaleOp upscaleOp = new S2AerosolUpscaleOp();
            upscaleOp.setParameterDefaultValues();
            upscaleOp.setSourceProduct("lowresProduct", aotGapFilledProduct);
            upscaleOp.setSourceProduct("hiresProduct", extendedSourceProduct);
            upscaleOp.setParameter("scale", scale);
            Product aotOrigResolutionProduct = upscaleOp.getTargetProduct();

            targetProduct = mergeToTargetProduct(aerosolTypeProduct, aotOrigResolutionProduct);
            ProductUtils.copyPreferredTileSize(extendedSourceProduct, targetProduct);
        }
        setTargetProduct(targetProduct);
    }

    private Product mergeToTargetProduct(Product reflProduct, Product aotOriginalResolutionProduct) {
        final String targetProductName = reflProduct.getName() + "_AOT";
        final String targetProductType = reflProduct.getProductType() + " S2 MSI AOT";
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

            if (copyBand) {
                ProductUtils.copyBand(sourceBandName, reflProduct, targetProduct, true);
            }
        }
        for (Band sourceBand : reflProduct.getBands()) {
            final String sourceBandName = sourceBand.getName();
            final boolean copyBand = (copyGeometryBands &&
                    !targetProduct.containsBand(sourceBandName) &&
                    AerosolUtils.isS2GeometryBand(sourceBand));

            if (copyBand) {
                ProductUtils.copyBand(sourceBandName, reflProduct, targetProduct, true);
            }
        }
        for (Band sourceBand : aotOriginalResolutionProduct.getBands()) {
            final String sourceBandName = sourceBand.getName();
            if (!sourceBand.isFlagBand() && !targetProduct.containsBand(sourceBandName)) {
                ProductUtils.copyBand(sourceBandName, aotOriginalResolutionProduct, targetProduct, true);
            }
        }
        for (Band sourceBand : reflProduct.getBands()) {
            final String sourceBandName = sourceBand.getName();
            if (!sourceBand.isFlagBand() && !targetProduct.containsBand(sourceBandName)) {
                ProductUtils.copyBand(sourceBandName, reflProduct, targetProduct, true);
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
