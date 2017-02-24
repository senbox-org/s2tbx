package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.operators.S2IdepixCloudBuffer;
import org.esa.s2tbx.s2msi.idepix.operators.cloudshadow.S2IdepixCloudShadowOp;
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
import org.esa.snap.core.util.RectangleExtender;

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

    @SourceProduct(alias = "s2Cloud")
    private Product s2CloudProduct;

    @Parameter(defaultValue = "true", label = " Compute a cloud buffer")
    private boolean computeCloudBuffer;

    @Parameter(defaultValue = "true", label = " Compute a cloud buffer also for cloud ambiguous pixels")
    private boolean computeCloudBufferForCloudAmbiguous;

    @Parameter(defaultValue = "false", label = " Compute cloud shadow", description = " Compute cloud shadow")
    private boolean computeCloudShadow;

    @Parameter(defaultValue = "2", label = "Width of cloud buffer (# of pixels)")
    private int cloudBufferWidth;

    private Band origClassifFlagBand;
    private Band cloudShadowFlagBand;

    private RectangleExtender rectCalculator;
    private int oceanCloudShadowIndexValue;
    private int landCloudShadowIndexValue;

    @Override
    public void initialize() throws OperatorException {

        Product postProcessedCloudProduct = createTargetProduct("postProcessedCloud", "postProcessedCloud");

        origClassifFlagBand = s2CloudProduct.getBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS);

        cloudShadowFlagBand = null;
        if (computeCloudShadow) {
            HashMap<String, Product> input = new HashMap<>();
            input.put("sourceProduct", s2CloudProduct);
            final Product cloudShadowProduct = GPF.createProduct(OperatorSpi.getOperatorAlias(S2IdepixCloudShadowOp.class),
                                                                 GPF.NO_PARAMS, input);
            cloudShadowFlagBand = cloudShadowProduct.getBand(S2IdepixCloudShadowOp.BAND_NAME_CLOUD_SHADOW);
            final IndexCoding cloudShadowFlagBandIndexCoding = cloudShadowFlagBand.getIndexCoding();
            oceanCloudShadowIndexValue = cloudShadowFlagBandIndexCoding.getIndexValue("ocean_cloud_shadow");
            landCloudShadowIndexValue = cloudShadowFlagBandIndexCoding.getIndexValue("land_cloud_shadow");
        }

        int extendedWidth = computeCloudBuffer ? Math.max(3, cloudBufferWidth) : 3;
        int extendedHeight = extendedWidth;

        rectCalculator = new RectangleExtender(new Rectangle(l1cProduct.getSceneRasterWidth(),
                                                             l1cProduct.getSceneRasterHeight()),
                                               extendedWidth, extendedHeight
        );

        ProductUtils.copyBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS, s2CloudProduct, postProcessedCloudProduct, false);
        setTargetProduct(postProcessedCloudProduct);
    }

    private Product createTargetProduct(String name, String type) {
        final int sceneWidth = s2CloudProduct.getSceneRasterWidth();
        final int sceneHeight = s2CloudProduct.getSceneRasterHeight();

        Product targetProduct = new Product(name, type, sceneWidth, sceneHeight);
        ProductUtils.copyGeoCoding(s2CloudProduct, targetProduct);
        targetProduct.setStartTime(s2CloudProduct.getStartTime());
        targetProduct.setEndTime(s2CloudProduct.getEndTime());

        return targetProduct;
    }

    @Override
    public void computeTile(Band targetBand, final Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle targetRectangle = targetTile.getRectangle();
        final Rectangle srcRectangle = rectCalculator.extend(targetRectangle);

        final Tile classifFlagTile = getSourceTile(origClassifFlagBand, srcRectangle);

        for (int y = srcRectangle.y; y < srcRectangle.y + srcRectangle.height; y++) {
            checkForCancellation();
            for (int x = srcRectangle.x; x < srcRectangle.x + srcRectangle.width; x++) {

                if (targetRectangle.contains(x, y)) {
                    boolean isInvalid = targetTile.getSampleBit(x, y, S2IdepixConstants.F_INVALID);
                    if (!isInvalid) {
                        combineFlags(x, y, classifFlagTile, targetTile);
                        setCloudShadow(x, y, classifFlagTile, targetTile);
                    }
                }
            }
        }

        if (computeCloudBuffer) {
            for (int y = srcRectangle.y; y < srcRectangle.y + srcRectangle.height; y++) {
                checkForCancellation();
                for (int x = srcRectangle.x; x < srcRectangle.x + srcRectangle.width; x++) {

                    if (targetRectangle.contains(x, y)) {
                        S2IdepixUtils.combineFlags(x, y, classifFlagTile, targetTile);
                    }
                    final boolean isCloudSure = classifFlagTile.getSampleBit(x, y, S2IdepixConstants.F_CLOUD_SURE);
                    final boolean isCloudAmbiguous = classifFlagTile.getSampleBit(x, y, S2IdepixConstants.F_CLOUD_AMBIGUOUS);
                    final boolean doSimpleCloudBuffer =
                            computeCloudBufferForCloudAmbiguous ? isCloudSure || isCloudAmbiguous : isCloudSure;
                    if (doSimpleCloudBuffer) {
                        S2IdepixCloudBuffer.computeSimpleCloudBuffer(x, y,
                                                                     targetTile,
                                                                     srcRectangle,
                                                                     cloudBufferWidth,
                                                                     S2IdepixConstants.F_CLOUD_BUFFER);
                    }
                }
            }
            for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
                checkForCancellation();
                for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                    S2IdepixUtils.consolidateCloudAndBuffer(targetTile, x, y);
                }
            }
        }

        if (computeCloudShadow) {
            final Tile cloudShadowFlagTile = getSourceTile(cloudShadowFlagBand, srcRectangle);
            for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
                checkForCancellation();
                for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                    final int cloudShadowFlagValue = cloudShadowFlagTile.getSampleInt(x, y);

                    if (cloudShadowFlagValue == oceanCloudShadowIndexValue ||
                            cloudShadowFlagValue == landCloudShadowIndexValue) {
                        targetTile.setSample(x, y, S2IdepixConstants.F_CLOUD_SHADOW, true);
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


    private void setCloudShadow(int x, int y, Tile classifFlagTile, Tile targetTile) {
        // todo when we have algo
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
