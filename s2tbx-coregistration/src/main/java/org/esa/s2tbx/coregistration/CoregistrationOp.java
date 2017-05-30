package org.esa.s2tbx.coregistration;


import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.netbeans.api.progress.aggregate.ProgressMonitor;
import org.esa.snap.utils.StringHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Coregistration
 *
 * @author  Ramona Manda
 * @since   6.0.0
 */
@OperatorMetadata(
        alias = "CoregistrationOp",
        version="1.0",
        category = "Optical",
        description = "The 'Coregistration Processor' operator ...",
        authors = "RamonaM",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class CoregistrationOp extends Operator {
    @Parameter(label = "Solar irradiance (if neither Sentinel-2 nor SPOT)", description = "The solar irradiance.")
    private float solarIrradiance;

    @Parameter(label = "U (if not Sentinel-2)", description = "U")
    private float u;

    @Parameter(label = "Incidence angle (if neither Sentinel-2 nor SPOT)", description = "The incidence angle in degrees.")
    private float incidenceAngle;

    @SourceProduct(alias = "source", description = "The source product.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    @Parameter(label = "Copy masks", description = "Copy masks from the source product", defaultValue = "false")
    private boolean copyMasks;

    private double d2;
    private double scale;
    private Map<String, TiePointGrid> tiePointGrids;
    private Map<String, Float> solarIrradiances;

    public CoregistrationOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        Band sunZenithBand = this.sourceProduct.getBand("sun_zenith");
/*
        if (isSentinelProduct(this.sourceProduct)) {
            this.solarIrradiances = extractSolarIrradiancesFromSentinelProduct(this.sourceProduct, this.sourceBandNames);
            this.u = extractUFromSentinelProduct(this.sourceProduct);
        } else if (isSpotProduct(this.sourceProduct)) {
            this.solarIrradiances = extractSolarIrradianceFromSpotProduct(this.sourceProduct, this.sourceBandNames);
            this.incidenceAngle = extractIncidenceAngleFromSpotProduct(this.sourceProduct);
        }

        if (this.solarIrradiances == null && this.solarIrradiance == 0.0f) {
            throw new OperatorException("Please specify the solar irradiance.");
        }
        if (this.u == 0.0f) {
            throw new OperatorException("Please specify the U.");
        }
        */
        int sceneWidth = 0, sceneHeight = 0;
        Set<Integer> distictWidths = new HashSet<>();
        for (String bandName : this.sourceBandNames) {
            Band band = this.sourceProduct.getBand(bandName);
            if (sceneWidth < band.getRasterWidth()) {
                sceneWidth = band.getRasterWidth();
                sceneHeight = band.getRasterHeight();
            }
            distictWidths.add(band.getRasterHeight());
        }
        /*int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();*/

        targetProduct = new Product(sourceProduct.getName() + "_rad", sourceProduct.getProductType(), sceneWidth, sceneHeight);

        /*targetProduct.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());*/
        targetProduct.setNumResolutionsMax(distictWidths.size());

        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        if (this.copyMasks) {
            copyMasks(sourceProduct, targetProduct, sourceBandNames);
        }
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        Band[] sourceBands = new Band[this.sourceBandNames.length];
        this.tiePointGrids = new HashMap<>();
        for (int i = 0; i < this.sourceBandNames.length; i++) {
            Band sourceBand = sourceProduct.getBand(this.sourceBandNames[i]);
            sourceBands[i] = this.sourceProduct.getBand(this.sourceBandNames[i]);
            int sourceBandWidth = sourceBands[i].getRasterWidth();
            int sourceBandHeight = sourceBands[i].getRasterHeight();

            Band targetBand = new Band(this.sourceBandNames[i], ProductData.TYPE_FLOAT32, sourceBandWidth, sourceBandHeight);
            ProductUtils.copyRasterDataNodeProperties(sourceBand, targetBand);
            targetBand.setGeoCoding(sourceBand.getGeoCoding());
            this.targetProduct.addBand(targetBand);

            if (sunZenithBand == null) {
                if (this.incidenceAngle == 0.0f) {
                    throw new OperatorException("Please specify the incidence angle.");
                }
                float[] tiePoints = new float[] {this.incidenceAngle, this.incidenceAngle, this.incidenceAngle, this.incidenceAngle};
                this.tiePointGrids.put(sourceBand.getName(), new TiePointGrid("angles_" + sourceBand.getName(), 2, 2, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints));
            } else {
                int sunZenithBandWidth = sunZenithBand.getRasterWidth();
                int sunZenithBandHeight = sunZenithBand.getRasterHeight();
                float[] tiePoints = new float[sunZenithBandWidth * sunZenithBandHeight];
                int index = 0;
                for (int row = 0; row < sunZenithBandHeight; row++) {
                    for (int column = 0; column < sunZenithBandWidth; column++) {
                        tiePoints[index++] = sunZenithBand.getSampleFloat(column, row);
                    }
                }
                this.tiePointGrids.put(sourceBand.getName(), new TiePointGrid("angles_" + sourceBand.getName(), sunZenithBandWidth, sunZenithBandHeight, 0, 0, sourceBandWidth, sourceBandHeight, tiePoints));
            }
        }

        this.d2 = 1.0d / this.u;
        this.scale = 1.0d;
    }

    @Override
    public synchronized void computeTile(Band targetBand, Tile targetTile, com.bc.ceres.core.ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing Reflectance to Radiance", targetTile.getHeight());
        // https://github.com/umwilm/SEN2COR/blob/96a00464bef15404a224b2262accd0802a338ff9/sen2cor/L2A_Tables.py
        // The final formula is:
        // rad = rho * cos(radians(sza)) * Es * sc / (pi * d2)
        // where: d2 = 1.0 / U
        // scale: 1 / (0.001 * 1000) = 1 (default)
        Rectangle rectangle = targetTile.getRectangle();
        /*
        try {
            Tile sourceTile = getSourceTile(this.sourceProduct.getBand(targetBand.getName()), rectangle);
            TiePointGrid tiePointGrid = this.tiePointGrids.get(targetBand.getName());
            float slrIrr = this.solarIrradiances != null ?
                    this.solarIrradiances.get(targetBand.getName()) :
                    this.solarIrradiance;
            double factor = slrIrr * this.scale / (Math.PI * this.d2);
            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    //float sunZenitAngle = tiePointGrid.getSampleFloat(x, y);
                    double sunZenitRadians = Math.toRadians(tiePointGrid.getSampleFloat(x, y));
                    //float pixelValue = sourceTile.getSampleFloat(x, y);
                    float result = (float)(sourceTile.getSampleFloat(x, y) * Math.cos(sunZenitRadians) * factor);
                    targetTile.setSample(x, y, result);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
        */
    }

    private boolean isSentinelProduct(Product product) {
        return StringHelper.startsWithIgnoreCase(product.getProductType(), "S2_MSI_Level");
    }

    private boolean isSpotProduct(Product product) {
        return StringHelper.startsWithIgnoreCase(product.getProductType(), "SPOTSCENE");
    }

    private void copyMasks(Product sourceProduct, Product targetProduct, String...bandNames) {
        if (isSentinelProduct(sourceProduct)) {
            final ProductNodeGroup<Mask> sourceMaskGroup = sourceProduct.getMaskGroup();
            int nodeCount = sourceMaskGroup.getNodeCount();
            for (int i = 0; i < nodeCount; i++) {
                final Mask mask = sourceMaskGroup.get(i);
                String maskName = mask.getName();
                if (!targetProduct.getMaskGroup().contains(maskName)
                        && StringHelper.endsWithIgnoreCase(maskName, bandNames)) {
                    if (mask.getImageType().transferMask(mask, targetProduct) == null) {
                        Mask targetMask = new Mask(maskName, mask.getRasterWidth(), mask.getRasterHeight(), mask.getImageType());
                        ProductUtils.copyRasterDataNodeProperties(mask, targetMask);
                        targetMask.setSourceImage(mask.getSourceImage());
                        targetProduct.getMaskGroup().add(targetMask);
                    }
                }
            }
        } else {
            final ProductNodeGroup<Mask> sourceMaskGroup = sourceProduct.getMaskGroup();
            for (int i = 0; i < sourceMaskGroup.getNodeCount(); i++) {
                final Mask mask = sourceMaskGroup.get(i);
                if (!targetProduct.getMaskGroup().contains(mask.getName())) {
                    mask.getImageType().transferMask(mask, targetProduct);
                }
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(CoregistrationOp.class);
        }
    }
}
