package org.esa.s2tbx.s2msi.idepix.operators;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.SourceProduct;

import javax.media.jai.BorderExtender;
import java.awt.Rectangle;
import java.util.Map;

/**
 * @author Tonio Fincke
 */
public class S2IdepixMountainShadowOp extends Operator {

    @SourceProduct
    private Product sourceProduct;

    private boolean slopeIsMissing;
    private boolean aspectIsMissing;
    private boolean orientationIsMissing;
    private int productWidth;
    private int productHeight;

    private Band elevationBand;

    @Override
    public void initialize() throws OperatorException {

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm)
            throws OperatorException {
//        super.computeTileStack(targetTiles, targetRectangle, pm);
//        sourceProduct.getBand(S2IdepixConstants.ELEVATION_BAND_NAME)
        if (slopeIsMissing || aspectIsMissing || orientationIsMissing) {
            int leftExtender = 1;
            int rightExtender = 1;
            int upperExtender = 1;
            int lowerExtender = 1;
            if (targetRectangle.x == 0) {
                leftExtender = 0;
            }
            if (targetRectangle.y == 0) {
                upperExtender = 0;
            }
//            if ()
//            final int sourceX = Math.max(0, targetRectangle.x);
//            final int sourceY = Math.max(0, targetRectangle.y);
//            int sourceWidth = targetRectangle.width + 1;
//            int sourceHeight = targetRectangle.height + 1;
            if (targetRectangle.x + targetRectangle.width == productWidth) {
                rightExtender = 0;
            }
            if (targetRectangle.y + targetRectangle.height == productHeight) {
                lowerExtender = 0;
            }
            Rectangle sourceRectangle = new Rectangle(targetRectangle.x - leftExtender,
                                                      targetRectangle.y - upperExtender,
                                                      targetRectangle.width + leftExtender + rightExtender,
                                                      targetRectangle.height + upperExtender + lowerExtender);
            final BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);
            final Tile elevationTile = getSourceTile(elevationBand, sourceRectangle, borderExtender);
//            elevationTile.getDataBufferFloat()
        }
    }



    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixMountainShadowOp.class);
        }
    }
}
