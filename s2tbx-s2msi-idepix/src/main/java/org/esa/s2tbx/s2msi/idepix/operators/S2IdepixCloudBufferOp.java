package org.esa.s2tbx.s2msi.idepix.operators;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixUtils;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.RectangleExtender;

import java.awt.*;

/**
 * Adds a cloud buffer to cloudy pixels.
 *
 * @author olafd
 */
@OperatorMetadata(alias = "Idepix.S2Cloudbuffer",
        version = "2.2",
        internal = true,
        authors = "Olaf Danne",
        copyright = "(c) 2016 by Brockmann Consult",
        description = "Adds a cloud buffer to cloudy pixels.")
public class S2IdepixCloudBufferOp extends Operator {

    @Parameter(defaultValue = "2", label = "Width of cloud buffer (# of pixels)")
    private int cloudBufferWidth;

    @SourceProduct(alias = "classifiedProduct")
    private Product classifiedProduct;

    private Band origClassifFlagBand;

    private RectangleExtender rectCalculator;


    @Override
    public void initialize() throws OperatorException {

        Product cloudBufferProduct = createTargetProduct(classifiedProduct,
                                                         classifiedProduct.getName(),
                                                         classifiedProduct.getProductType());

        rectCalculator = new RectangleExtender(new Rectangle(classifiedProduct.getSceneRasterWidth(),
                                                             classifiedProduct.getSceneRasterHeight()),
                                               cloudBufferWidth, cloudBufferWidth);

        origClassifFlagBand = classifiedProduct.getBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS);
        ProductUtils.copyBand(S2IdepixUtils.IDEPIX_CLASSIF_FLAGS, classifiedProduct, cloudBufferProduct, false);
        setTargetProduct(cloudBufferProduct);
    }

    private Product createTargetProduct(Product sourceProduct, String name, String type) {
        final int sceneWidth = sourceProduct.getSceneRasterWidth();
        final int sceneHeight = sourceProduct.getSceneRasterHeight();

        Product targetProduct = new Product(name, type, sceneWidth, sceneHeight);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());

        return targetProduct;
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {

        Rectangle targetRectangle = targetTile.getRectangle();
        final Rectangle srcRectangle = rectCalculator.extend(targetRectangle);

        final Tile sourceFlagTile = getSourceTile(origClassifFlagBand, srcRectangle);

        for (int y = srcRectangle.y; y < srcRectangle.y + srcRectangle.height; y++) {
            checkForCancellation();
            for (int x = srcRectangle.x; x < srcRectangle.x + srcRectangle.width; x++) {

                if (targetRectangle.contains(x, y)) {
                    S2IdepixUtils.combineFlags(x, y, sourceFlagTile, targetTile);
                }
                boolean isCloud = sourceFlagTile.getSampleBit(x, y, S2IdepixConstants.IDEPIX_CLOUD_SURE) ||
                        sourceFlagTile.getSampleBit(x, y, S2IdepixConstants.IDEPIX_CLOUD_AMBIGUOUS);
                if (isCloud) {
                    S2IdepixCloudBuffer.computeSimpleCloudBuffer(x, y,
                                                                 targetTile,
                                                                 srcRectangle,
                                                                 cloudBufferWidth,
                                                                 S2IdepixConstants.IDEPIX_CLOUD_BUFFER);
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

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixCloudBufferOp.class);
        }
    }
}
