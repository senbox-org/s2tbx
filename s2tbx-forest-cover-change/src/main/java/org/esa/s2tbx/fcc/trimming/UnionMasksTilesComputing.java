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
public class UnionMasksTilesComputing extends AbstractImageTilesParallelComputing {

    private static final Logger logger = Logger.getLogger(UnionMasksTilesComputing.class.getName());

    private final IntMatrix colorFillerMatrix;
    private final IntSet currentSegmentationTrimmingRegionKeys;
    private final IntSet previousSegmentationTrimmingRegionKeys;
    private final ProductData productData;

    public UnionMasksTilesComputing(IntMatrix colorFillerMatrix, IntSet currentSegmentationTrimmingRegionKeys,
                                    IntSet previousSegmentationTrimmingRegionKeys, int tileWidth, int tileHeight) {

        super(colorFillerMatrix.getColumnCount(), colorFillerMatrix.getRowCount(), tileWidth, tileHeight);

        this.colorFillerMatrix = colorFillerMatrix;
        this.currentSegmentationTrimmingRegionKeys = currentSegmentationTrimmingRegionKeys;
        this.previousSegmentationTrimmingRegionKeys = previousSegmentationTrimmingRegionKeys;

        int sceneWidth = this.colorFillerMatrix.getColumnCount();
        int sceneHeight = this.colorFillerMatrix.getRowCount();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Union masks for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }
        int sceneWidth = this.colorFillerMatrix.getColumnCount();

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                // get the pixel value from the segmentation
                int segmentationPixelValue = this.colorFillerMatrix.getValueAt(y, x);

                if (segmentationPixelValue == ForestCoverChangeConstants.PREVIOUS_MASK_NO_DATA_VALUE) {
                    segmentationPixelValue = ForestCoverChangeConstants.PREVIOUS_MASK_VALUE;
                } else if (segmentationPixelValue == ForestCoverChangeConstants.CURRENT_MASK_NO_DATA_VALUE) {
                    segmentationPixelValue = ForestCoverChangeConstants.CURRENT_MASK_VALUE;
                } else if (this.previousSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                    // the pixel value from the previous segmentation exists among the trimming region keys of the previous segmentation

                    // check if the pixel value from the current segmentation exists among the trimming region keys of the current segmentation
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        // the pixel value from the current segmentation exists among the trimming region keys of the current segmentation
                        segmentationPixelValue = ForestCoverChangeConstants.COMMON_VALUE;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstants.PREVIOUS_VALUE;
                    }
                } else {
                    // the pixel value from the previous segmentation does not exist among the trimming region keys of the previous segmentation

                    // check if the pixel value from the current segmentation exists among the trimming region keys of the current segmentation
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        // the pixel value from the current segmentation exists among the trimming region keys of the current segmentation
                        segmentationPixelValue = ForestCoverChangeConstants.CURRENT_VALUE;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                    }
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
