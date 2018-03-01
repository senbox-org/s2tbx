package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.operators.cloudshadow.S2IdepixCloudShadowOp;
import org.esa.s2tbx.s2msi.idepix.operators.mountainshadow.S2IdepixMountainShadowOp;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.*;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.HashMap;

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

    @Parameter(defaultValue = "true", label = "Compute mountain shadow", description = "Compute mountain shadow")
    private boolean computeMountainShadow;
    
    @Parameter(defaultValue = "true", label = "Compute cloud shadow", description = "Compute cloud shadow")
    private boolean computeCloudShadow;

//    @Parameter(defaultValue = "2", label = "Width of cloud buffer (# of pixels)")
//    private int cloudBufferWidth;

    private Band s2ClassifFlagBand;
    private Band cloudBufferFlagBand;
    private Band mountainShadowFlagBand;
    private Band cloudShadowFlagBand;

    private int oceanCloudShadowIndexValue;
    private int landCloudShadowIndexValue;

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
                    OperatorSpi.getOperatorAlias(S2IdepixMountainShadowOp.class), GPF.NO_PARAMS, l1cProduct);
            mountainShadowFlagBand = mountainShadowProduct.getBand(
                    S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME);
        }

        cloudShadowFlagBand = null;
        if (computeCloudShadow) {
            HashMap<String, Product> input = new HashMap<>();
            input.put("s2ClassifProduct", s2ClassifProduct);
            input.put("s2CloudBufferProduct", s2CloudBufferProduct);
            final Product cloudShadowProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(S2IdepixCloudShadowOp.class),
                                                                 GPF.NO_PARAMS, input);
            cloudShadowFlagBand = cloudShadowProduct.getBand(S2IdepixCloudShadowOp.BAND_NAME_CLOUD_SHADOW);
            final IndexCoding cloudShadowFlagBandIndexCoding = cloudShadowFlagBand.getIndexCoding();
            oceanCloudShadowIndexValue = cloudShadowFlagBandIndexCoding.getIndexValue("ocean_cloud_shadow");
            landCloudShadowIndexValue = cloudShadowFlagBandIndexCoding.getIndexValue("land_cloud_shadow");
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
//                    targetTile.setSample(x, y, S2IdepixConstants.);
//                    final int cloudShadowFlagValue = mountainShadowFlagTile.getSampleInt(x, y);
//
//                    if (cloudShadowFlagValue == oceanCloudShadowIndexValue ||
//                            cloudShadowFlagValue == landCloudShadowIndexValue) {
//                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLOUD_SHADOW, true);
//                    }
                }
            }
        }
        if (computeCloudShadow) {
            final Tile cloudShadowFlagTile = getSourceTile(cloudShadowFlagBand, targetRectangle);
            for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
                checkForCancellation();
                for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                    final int cloudShadowFlagValue = cloudShadowFlagTile.getSampleInt(x, y);

                    if (cloudShadowFlagValue == oceanCloudShadowIndexValue ||
                            cloudShadowFlagValue == landCloudShadowIndexValue) {
                        targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLOUD_SHADOW, true);
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
