package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.AbstractWriteMasksTilesComputing;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.ByteMatrix;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.nio.file.Path;
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
    private final Path currentMaskTilesFolder;
    private final Path previousMaskTilesFolder;

    public ColorFillerTilesComputing(IntMatrix segmentationMatrix, IntSet validRegions, Path currentMaskTilesFolder,
                                     Path previousMaskTilesFolder, int tileWidth, int tileHeight) {

        super(segmentationMatrix.getColumnCount(), segmentationMatrix.getRowCount(), tileWidth, tileHeight);

        this.segmentationMatrix = segmentationMatrix;
        this.validRegions = validRegions;
        this.currentMaskTilesFolder = currentMaskTilesFolder;
        this.previousMaskTilesFolder = previousMaskTilesFolder;

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

        ByteMatrix currentMaskTilePixels = null;
        if (this.currentMaskTilesFolder != null) {
            currentMaskTilePixels = AbstractWriteMasksTilesComputing.readMaskTile(this.currentMaskTilesFolder, tileLeftX, tileTopY, tileWidth, tileHeight);
        }
        ByteMatrix previousMaskTilePixels = null;
        if (this.previousMaskTilesFolder != null) {
            previousMaskTilePixels = AbstractWriteMasksTilesComputing.readMaskTile(this.previousMaskTilesFolder, tileLeftX, tileTopY, tileWidth, tileHeight);
        }

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationValue = this.segmentationMatrix.getValueAt(y, x);
                if (!this.validRegions.contains(segmentationValue)) {
                    segmentationValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                } else if (currentMaskTilePixels != null && currentMaskTilePixels.getValueAt(y-tileTopY, x-tileLeftX) != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    segmentationValue = ForestCoverChangeConstants.CURRENT_MASK_NO_DATA_VALUE;
                } else if (previousMaskTilePixels != null && previousMaskTilePixels.getValueAt(y-tileTopY, x-tileLeftX) != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    segmentationValue = ForestCoverChangeConstants.PREVIOUS_MASK_NO_DATA_VALUE;
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
