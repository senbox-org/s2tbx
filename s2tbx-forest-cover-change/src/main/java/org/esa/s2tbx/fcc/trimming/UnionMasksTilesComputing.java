package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class UnionMasksTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(UnionMasksTilesComputing.class.getName());

    private final IntMatrix currentSegmentationSourceProduct;
    private final IntMatrix previousSegmentationSourceProduct;
    private final IntSet currentSegmentationTrimmingRegionKeys;
    private final IntSet previousSegmentationTrimmingRegionKeys;
    private final IntMatrix result;

    public UnionMasksTilesComputing(IntMatrix currentSegmentationSourceProduct, IntMatrix previousSegmentationSourceProduct,
                                    IntSet currentSegmentationTrimmingRegionKeys, IntSet previousSegmentationTrimmingRegionKeys, int tileWidth, int tileHeight) {

        super(currentSegmentationSourceProduct.getColumnCount(), currentSegmentationSourceProduct.getRowCount(), tileWidth, tileHeight);

        this.currentSegmentationSourceProduct = currentSegmentationSourceProduct;
        this.previousSegmentationSourceProduct = previousSegmentationSourceProduct;
        this.currentSegmentationTrimmingRegionKeys = currentSegmentationTrimmingRegionKeys;
        this.previousSegmentationTrimmingRegionKeys = previousSegmentationTrimmingRegionKeys;

        this.result = new IntMatrix(currentSegmentationSourceProduct.getRowCount(), currentSegmentationSourceProduct.getColumnCount());
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
            throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Union masks for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

//        Band currentSegmentationBand = this.currentSegmentationSourceProduct.getBandAt(0);
//        Band previousSegmentationBand = this.previousSegmentationSourceProduct.getBandAt(0);
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = this.previousSegmentationSourceProduct.getValueAt(y, x);
                        //previousSegmentationBand.getSampleInt(x, y);
                if (this.previousSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                    int currentSegmentationPixelValue = this.currentSegmentationSourceProduct.getValueAt(y, x);
                            //currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(currentSegmentationPixelValue)) {
                        segmentationPixelValue = 255;
                    } else {
                        segmentationPixelValue = 50;
                    }
                } else {
                    segmentationPixelValue = this.currentSegmentationSourceProduct.getValueAt(y, x);
                            //currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        segmentationPixelValue = 100;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                    }
                }
                synchronized (this.result) {
                    this.result.setValueAt(y, x, segmentationPixelValue);
                }
            }
        }
    }

    public IntMatrix runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.result;
    }
}
