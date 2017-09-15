package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class FinalMasksTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(FinalMasksTilesComputing.class.getName());

    private final ProductData productData;
    private final IntMatrix differenceSegmentationMatrix;
    private final IntMatrix unionMaskMatrix;
    private final IntSet differenceTrimmingSet;

    public FinalMasksTilesComputing(IntMatrix differenceSegmentationMatrix, IntMatrix unionMaskMatrix, IntSet differenceTrimmingSet, int tileWidth, int tileHeight) {
        super(differenceSegmentationMatrix.getColumnCount(), differenceSegmentationMatrix.getRowCount(), tileWidth, tileHeight);

        this.differenceSegmentationMatrix = differenceSegmentationMatrix;
        this.unionMaskMatrix = unionMaskMatrix;
        this.differenceTrimmingSet = differenceTrimmingSet;

        int sceneWidth = this.differenceSegmentationMatrix.getColumnCount();
        int sceneHeight = this.differenceSegmentationMatrix.getRowCount();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Final masks for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        int sceneWidth = this.differenceSegmentationMatrix.getColumnCount();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                // get the pixel value from the difference segmentation
                int segmentationPixelValue = this.differenceSegmentationMatrix.getValueAt(y, x);

                // check if the pixel value from the difference segmentation exists among the trimming region keys of the difference segmentation
                if (this.differenceTrimmingSet.contains(segmentationPixelValue)) {
                    // the pixel value from the difference segmentation exists among the trimming region keys of the difference segmentation
                    segmentationPixelValue = this.unionMaskMatrix.getValueAt(y, x);
                } else {
                    segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                }
                
                synchronized (this.productData) {
                    this.productData.setElemIntAt(sceneWidth * y + x, segmentationPixelValue);
                }
            }
        }
    }

    public ProductData runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }
}
