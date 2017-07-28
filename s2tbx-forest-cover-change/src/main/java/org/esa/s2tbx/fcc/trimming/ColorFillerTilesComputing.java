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
public class ColorFillerTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(ColorFillerTilesComputing.class.getName());

    private final IntMatrix segmentationMatrix;
    private final IntSet validRegions;
    private final IntMatrix result;

    public ColorFillerTilesComputing(IntMatrix segmentationMatrix, IntSet validRegions, int tileWidth, int tileHeight) {
        super(segmentationMatrix.getColumnCount(), segmentationMatrix.getRowCount(), tileWidth, tileHeight);

        this.segmentationMatrix = segmentationMatrix;
        this.validRegions = validRegions;

        int sceneWidth = this.segmentationMatrix.getColumnCount();
        int sceneHeight = this.segmentationMatrix.getRowCount();
        this.result = new IntMatrix(sceneWidth, sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Color filler for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationValue = this.segmentationMatrix.getValueAt(y, x);
                if (!this.validRegions.contains(segmentationValue)) {
                    segmentationValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                } else {
                    System.out.println("");
                }
                synchronized (this.result) {
                    this.result.setValueAt(y, x, segmentationValue);
                }
            }
        }
    }

    public IntMatrix runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.result;
    }
}
