package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.utils.AbstractTilesComputingOp;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
@OperatorMetadata(
        alias = "FinalMasksOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class FinalMasksOp extends AbstractTilesComputingOp {
    private static final Logger logger = Logger.getLogger(FinalMasksOp.class.getName());

    @SourceProduct(alias = "differenceSegmentationProduct", description = "The source product")
    private Product differenceSegmentationProduct;

    @SourceProduct(alias = "unionMaskProduct", description = "The source product")
    private Product unionMaskProduct;

    @SourceProduct(alias = "outputTargetProduct", description = "The source product")
    private Product outputTargetProduct;

    @Parameter(itemAlias = "differenceTrimmingSet", description = "")
    private IntSet differenceTrimmingSet;

    public FinalMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        this.targetProduct = this.outputTargetProduct;
        initTiles();
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Final masks for tile region: row index: "+ tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band differenceSegmentationBand = this.differenceSegmentationProduct.getBandAt(0);
        Band unionMaskBand = this.unionMaskProduct.getBandAt(0);
        int tileBottomY = tileRegion.y + tileRegion.height;
        int tileRightX = tileRegion.x + tileRegion.width;
        for (int y = tileRegion.y; y < tileBottomY; y++) {
            for (int x = tileRegion.x; x < tileRightX; x++) {
                int segmentationPixelValue = differenceSegmentationBand.getSampleInt(x, y);
                if (this.differenceTrimmingSet.contains(segmentationPixelValue)) {
                    int unionPixelValue = unionMaskBand.getSampleInt(x, y);
                    if (unionPixelValue == ForestCoverChangeConstants.NO_DATA_VALUE) {
                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                    } else {
                        segmentationPixelValue = 1;
                    }
                } else {
                    segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                }
                targetTile.setSample(x, y, segmentationPixelValue);
            }
            checkForCancellation();
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(FinalMasksOp.class);
        }
    }
}
