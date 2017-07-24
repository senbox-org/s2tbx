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
import org.esa.snap.core.util.ProductUtils;
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
        alias = "UnionMasksOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class UnionMasksOp extends AbstractTilesComputingOp {
    private static final Logger logger = Logger.getLogger(UnionMasksOp.class.getName());

    @SourceProduct(alias = "source", description = "The first source product")
    private Product currentSegmentationSourceProduct;
    @SourceProduct(alias = "source", description = "The second source product")
    private Product previousSegmentationSourceProduct;

    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet currentSegmentationTrimmingRegionKeys;
    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet previousSegmentationTrimmingRegionKeys;

    public UnionMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.currentSegmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSegmentationSourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.currentSegmentationSourceProduct.getName() + "_union", this.currentSegmentationSourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Union masks for tile region: row index: "+ tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band currentSegmentationBand = this.currentSegmentationSourceProduct.getBandAt(0);
        Band previousSegmentationBand = this.previousSegmentationSourceProduct.getBandAt(0);
        int tileBottomY = tileRegion.y + tileRegion.height;
        int tileRightX = tileRegion.x + tileRegion.width;
        for (int y = tileRegion.y; y < tileBottomY; y++) {
            for (int x = tileRegion.x; x < tileRightX; x++) {
                int segmentationPixelValue = previousSegmentationBand.getSampleInt(x, y);
                if (this.previousSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                    int currentSegmentationPixelValue = currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(currentSegmentationPixelValue)) {
                        segmentationPixelValue = 1;//255;
                    } else {
                        segmentationPixelValue = 1;//50;
                    }
                } else {
                    segmentationPixelValue = currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        segmentationPixelValue = 1;//100;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                    }
                }
                targetTile.setSample(x, y, segmentationPixelValue);
            }
            checkForCancellation();
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(UnionMasksOp.class);
        }
    }
}
