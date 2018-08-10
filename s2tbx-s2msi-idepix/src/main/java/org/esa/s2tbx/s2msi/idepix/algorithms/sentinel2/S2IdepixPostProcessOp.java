package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.operators.cloudshadow.S2IdepixCloudShadowOp;
import org.esa.s2tbx.s2msi.idepix.operators.cloudshadow.S2IdepixPreCloudShadowOp;
import org.esa.s2tbx.s2msi.idepix.operators.mountainshadow.S2IdepixMountainShadowOp;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 * Operator used to consolidate cloud flag for Sentinel-2:
 * - coastline refinement? (tbd)
 * - cloud shadow (tbd)
 *
 * @author olafd
 */
@OperatorMetadata(alias = "Idepix.Sentinel2.Postprocess",
        version = "2.2",
        internal = true,
        authors = "Olaf Danne",
        copyright = "(c) 2016 by Brockmann Consult",
        description = "Refines the Sentinel-2 MSI pixel classification.")
public class S2IdepixPostProcessOp extends Operator {

    @SourceProduct(alias = "l1c")
    private Product l1cProduct;

    @SourceProduct(alias = "s2Classif")
    private Product s2ClassifProduct;

    @SourceProduct(alias = "s2CloudBuffer", optional = true)
    private Product s2CloudBufferProduct;      // has only classifFlagBand with buffer added

    @Parameter(defaultValue = "true", label = "Compute mountain shadow", description = "Whether to compute mountain shadow")
    private boolean computeMountainShadow;

    @Parameter(defaultValue = "true", label = "Compute cloud shadow", description = "Compute cloud shadow")
    private boolean computeCloudShadow;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CW_THRESH",
            description = " Threshold CW_THRESH")
    private double cwThresh;

    @Parameter(defaultValue = "-0.11",
            label = " Threshold GCL_THRESH",
            description = " Threshold GCL_THRESH")
    private double gclThresh;

    @Parameter(defaultValue = "0.01",
            label = " Threshold CL_THRESH",
            description = " Threshold CL_THRESH")
    private double clThresh;

    @Parameter(description = "The digital elevation model.", defaultValue = "SRTM 3Sec", label = "Digital Elevation Model")
    private String demName = "SRTM 3Sec";

    private Band s2ClassifFlagBand;
    private Band cloudBufferFlagBand;
    private Band mountainShadowFlagBand;
    private Band cloudShadowFlagBand;

    @Override
    public void initialize() throws OperatorException {

        Product postProcessedCloudProduct = createTargetProduct(s2ClassifProduct.getName(),
                                                                s2ClassifProduct.getProductType());

        s2ClassifFlagBand = s2ClassifProduct.getBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS);
        if (s2CloudBufferProduct != null) {
            cloudBufferFlagBand = s2CloudBufferProduct.getBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS);
        }

        if (computeMountainShadow) {
            final Product mountainShadowProduct = GPF.createProduct(
                    OperatorSpi.getOperatorAlias(S2IdepixMountainShadowOp.class), GPF.NO_PARAMS, s2ClassifProduct);
            mountainShadowFlagBand = mountainShadowProduct.getBand(
                    S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME);
        }

        cloudShadowFlagBand = null;
        if (computeCloudShadow) {
            HashMap<String, Product> input = new HashMap<>();
            input.put("l1cProduct", l1cProduct);
            input.put("s2ClassifProduct", s2ClassifProduct);
            Map<String, Object> params = new HashMap<>();
            params.put("computeMountainShadow", computeMountainShadow);
            params.put("mode", mode);
            params.put("cwThresh", cwThresh);
            params.put("gclThresh", gclThresh);
            params.put("clThresh", clThresh);
            params.put("demName", demName);
            final Product cloudShadowProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(S2IdepixCloudShadowOp.class),
                    params, input);
            cloudShadowFlagBand = cloudShadowProduct.getBand(S2IdepixCloudShadowOp.BAND_NAME_CLOUD_SHADOW);
        }

        ProductUtils.copyBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS, s2ClassifProduct, postProcessedCloudProduct, false);
        setTargetProduct(postProcessedCloudProduct);
    }

    private Product createTargetProduct(String name, String type) {
        final int sceneWidth = s2ClassifProduct.getSceneRasterWidth();
        final int sceneHeight = s2ClassifProduct.getSceneRasterHeight();

        Product targetProduct = new Product(name, type, sceneWidth, sceneHeight);
        ProductUtils.copyGeoCoding(s2ClassifProduct, targetProduct);
        targetProduct.setStartTime(s2ClassifProduct.getStartTime());
        targetProduct.setEndTime(s2ClassifProduct.getEndTime());

        return targetProduct;
    }

    @Override
    public void computeTile(Band targetBand, final Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();

        final Tile classifFlagTile = getSourceTile(s2ClassifFlagBand, targetRectangle);
        Tile cloudBufferFlagTile = null;
        if (s2CloudBufferProduct != null) {
            cloudBufferFlagTile = getSourceTile(cloudBufferFlagBand, targetRectangle);
        }

        for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
            checkForCancellation();
            for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {

                if (targetRectangle.contains(x, y)) {
                    boolean isInvalid = targetTile.getSampleBit(x, y, S2IdepixConstants.IDEPIX_INVALID);
                    if (!isInvalid) {
                        combineFlags(x, y, classifFlagTile, targetTile);
                        if (s2CloudBufferProduct != null) {
                            combineFlags(x, y, cloudBufferFlagTile, targetTile);
                        }
                    }
                }
            }
        }
        if (computeMountainShadow) {
            final Tile mountainShadowFlagTile = getSourceTile(mountainShadowFlagBand, targetRectangle);
            for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
                checkForCancellation();
                for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                    final boolean mountainShadow = mountainShadowFlagTile.getSampleInt(x, y) > 0;
                    targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_MOUNTAIN_SHADOW, mountainShadow);
                }
            }
        }
        if (computeCloudShadow) {
            final Tile flagTile = getSourceTile(cloudShadowFlagBand, targetRectangle);
            int clusteredCloudShadowFlag = (int) Math.pow(2, S2IdepixPreCloudShadowOp.F_CLOUD_SHADOW); //clustering algorithm
            int mountainShadowFlag = (int) Math.pow(2, S2IdepixPreCloudShadowOp.F_MOUNTAIN_SHADOW);
            int cloudBufferFlag = (int) Math.pow(2, S2IdepixPreCloudShadowOp.F_CLOUD_BUFFER);
            int potentialShadowFlag = (int) Math.pow(2, S2IdepixPreCloudShadowOp.F_POTENTIAL_CLOUD_SHADOW);
            int recommendedCloudShadow = (int) Math.pow(2, S2IdepixPreCloudShadowOp.F_RECOMMENDED_CLOUD_SHADOW);
            for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
                checkForCancellation();
                for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                    final int flagValue = flagTile.getSampleInt(x, y);
                    if ((flagValue & recommendedCloudShadow) == recommendedCloudShadow ) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLOUD_SHADOW, true);
                    }
                    if ((flagValue & cloudBufferFlag) == cloudBufferFlag) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLOUD_BUFFER, true);
                    }
                    if ((flagValue & potentialShadowFlag) == potentialShadowFlag) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_POTENTIAL_SHADOW, true);
                    }
                    if ((flagValue & clusteredCloudShadowFlag) == clusteredCloudShadowFlag) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLUSTERED_CLOUD_SHADOW, true);
                    }

                    if (computeMountainShadow && (flagValue & mountainShadowFlag) == mountainShadowFlag) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_MOUNTAIN_SHADOW, true);
                    }
                }
            }
        }
    }

    private void combineFlags(int x, int y, Tile sourceFlagTile, Tile targetTile) {
        int sourceFlags = sourceFlagTile.getSampleInt(x, y);
        int computedFlags = targetTile.getSampleInt(x, y);
        targetTile.setSample(x, y, sourceFlags | computedFlags);
    }

    /**
     * The Service Provider Interface (SPI) for the operator.
     * It provides operator meta-data and is a factory for new operator instances.
     */
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixPostProcessOp.class);
        }
    }
}
