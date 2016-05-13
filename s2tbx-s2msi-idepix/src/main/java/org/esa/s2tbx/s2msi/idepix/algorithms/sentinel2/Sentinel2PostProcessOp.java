package org.esa.s2tbx.s2msi.idepix.algorithms.sentinel2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.util.IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.RectangleExtender;

import java.awt.*;

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
public class Sentinel2PostProcessOp extends Operator {

    //    @Parameter(defaultValue = "true",
//            label = " Compute cloud shadow",
//            description = " Compute cloud shadow with latest 'fronts' algorithm")
    private boolean computeCloudShadow = false;   // never done currently

    //    @Parameter(defaultValue = "true",
//               label = " Refine pixel classification near coastlines",
//               description = "Refine pixel classification near coastlines. ")
    private boolean refineClassificationNearCoastlines = false; // todo check if needed


    @SourceProduct(alias = "l1c")
    private Product l1cProduct;

    @SourceProduct(alias = "s2Cloud")
    private Product s2CloudProduct;

    private Band origClassifFlagBand;
    private RectangleExtender rectCalculator;

    @Override
    public void initialize() throws OperatorException {

        Product postProcessedCloudProduct = createTargetProduct("postProcessedCloud", "postProcessedCloud");

        origClassifFlagBand = s2CloudProduct.getBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS);

        int extendedWidth = 3;
        int extendedHeight = 3; // todo: what do we need?

        rectCalculator = new RectangleExtender(new Rectangle(l1cProduct.getSceneRasterWidth(),
                                                             l1cProduct.getSceneRasterHeight()),
                                               extendedWidth, extendedHeight
        );

        ProductUtils.copyBand(IdepixUtils.IDEPIX_CLASSIF_FLAGS, s2CloudProduct, postProcessedCloudProduct, false);
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
                    boolean isInvalid = targetTile.getSampleBit(x, y, IdepixConstants.F_INVALID);
                    if (!isInvalid) {
                        combineFlags(x, y, classifFlagTile, targetTile);
                        setCloudShadow(x, y, classifFlagTile, targetTile);
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
            super(Sentinel2PostProcessOp.class);
        }
    }
}
