package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.grm.RegionMergingInputParameters;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.utils.AbstractParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class MovingWindowTileParallelComputing extends AbstractParallelComputing {
    private static final Logger logger = Logger.getLogger(MovingWindowTileParallelComputing.class.getName());

    private final IntMatrix colorFillerMatrix;
    private final int segmentationTileMargin;
    private final Path temporarySourceSegmentationTilesFolder;
    private final int[] sourceBandIndices;
    private final MajorityVotingValidSegments majorityVotingValidSegments;
    private final Dimension movingStepSize;
    private final Dimension movingWindowSize;
    private final Dimension tileSize;
    private final double degreesOfFreedom;

    private int currentTopY;
    private int currentLeftX;

    public MovingWindowTileParallelComputing(IntMatrix colorFillerMatrix, Dimension movingWindowSize, Dimension movingStepSize, Dimension tileSize,
                                             Path temporarySourceSegmentationTilesFolder, int[] sourceBandIndices, double degreesOfFreedom) {

        this.colorFillerMatrix = colorFillerMatrix;
        this.movingWindowSize = movingWindowSize;
        this.movingStepSize = movingStepSize;
        this.tileSize = tileSize;

        this.temporarySourceSegmentationTilesFolder = temporarySourceSegmentationTilesFolder;
        this.sourceBandIndices = sourceBandIndices;

        this.degreesOfFreedom = degreesOfFreedom;

        this.majorityVotingValidSegments = new MajorityVotingValidSegments();
        this.segmentationTileMargin = AbstractTileSegmenter.computeTileMargin(this.tileSize.width, this.tileSize.height);

        this.currentTopY = 0;
        this.currentLeftX = 0;
    }

    @Override
    protected void execute() throws Exception {
        int imageWidth = this.colorFillerMatrix.getColumnCount();
        int imageHeight = this.colorFillerMatrix.getRowCount();
        boolean canContinue = false;
        do {
            int movingLocalTileTopY = -1;
            int movingLocalTileLeftX = -1;
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentTopY < imageHeight) {
                    if (this.currentLeftX < imageWidth) {
                        movingLocalTileLeftX = this.currentLeftX;
                        movingLocalTileTopY = this.currentTopY;
                    } else {
                        // new row
                        this.currentLeftX = 0; // reset the column index
                        movingLocalTileLeftX = this.currentLeftX;

                        this.currentTopY += this.movingStepSize.height; // increment the row index
                        if (this.currentTopY < imageHeight) {
                            movingLocalTileTopY = this.currentTopY;
                        }
                    }
                    this.currentLeftX += this.movingStepSize.width; // increment the column index
                }
            }
            canContinue = false;
            if (movingLocalTileTopY >= 0 && movingLocalTileTopY < imageHeight && movingLocalTileLeftX >= 0 && movingLocalTileLeftX < imageWidth) {
                canContinue = true;
                int movingLocalTileBottomY = movingLocalTileTopY + this.movingWindowSize.height;
                if (movingLocalTileBottomY > imageHeight) {
                    movingLocalTileBottomY = imageHeight;
                }

                int movingLocalTileRightX = movingLocalTileLeftX + this.movingWindowSize.width;
                if (movingLocalTileRightX > imageWidth) {
                    movingLocalTileRightX = imageWidth;
                }

                runMovingTile(movingLocalTileTopY, movingLocalTileLeftX, movingLocalTileBottomY, movingLocalTileRightX);
            }
        } while (canContinue);
    }

    public final IntSet runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.majorityVotingValidSegments.computeValidSegmentIds();
    }

    private void runMovingTile(int movingLocalTileTopY, int movingLocalTileLeftX, int movingLocalTileBottomY, int movingLocalTileRightX) throws Exception {
        if (logger.isLoggable(Level.FINE)) {
            int localTileWidth = movingLocalTileRightX - movingLocalTileLeftX;
            int localTileHeight = movingLocalTileBottomY - movingLocalTileTopY;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Moving window for tile region: bounds [x=" + movingLocalTileLeftX+", y="+movingLocalTileTopY+", width="+localTileWidth+", height="+localTileHeight+"]");
        }

        MovingWindow movingWindow = new MovingWindow(this.colorFillerMatrix);
        IntSet movingWindowValidSegmentIds = movingWindow.runTile(movingLocalTileLeftX, movingLocalTileTopY, movingLocalTileRightX, movingLocalTileBottomY);

        int imageWidth = this.colorFillerMatrix.getColumnCount();
        int imageHeight = this.colorFillerMatrix.getRowCount();
        TrimmingValidSegments trimmingValidSegments = new TrimmingValidSegments(this.degreesOfFreedom);

        Map<String, TileDataSource[]> inputProductBandsMap = new HashMap<String, TileDataSource[]>();
        for (int y = movingLocalTileTopY; y < movingLocalTileBottomY; y++) {
            for (int x = movingLocalTileLeftX; x < movingLocalTileRightX; x++) {
                int segmentationPixelValue = this.colorFillerMatrix.getValueAt(y, x);
                if (segmentationPixelValue != ForestCoverChangeConstants.NO_DATA_VALUE && movingWindowValidSegmentIds.contains(segmentationPixelValue)) {
                    int localTileTopY = computeLocalTileTopY(y);
                    int localTileLeftX = computeLocalTileLeftX(x);
                    int localTileWidth = computeLocalTileWidth(localTileLeftX);
                    int localTileHeight = computeLocalTileHeight(localTileTopY);
                    String mapKey = localTileTopY+"|"+localTileLeftX+"|"+localTileWidth+"|"+localTileHeight;
                    TileDataSource[] inputDataSource = inputProductBandsMap.get(mapKey);
                    if (inputDataSource == null) {
                        ProcessingTile segmentationProcessingTile = AbstractTileSegmenter.buildTile(localTileLeftX, localTileTopY, localTileWidth, localTileHeight, this.segmentationTileMargin, imageWidth, imageHeight);
                        BoundingBox segmentationTileBounds = segmentationProcessingTile.getRegion();
                        inputDataSource = SegmentationSourceProductPair.buildSourceTiles(segmentationTileBounds, this.temporarySourceSegmentationTilesFolder);
                        inputProductBandsMap.put(mapKey, inputDataSource);
                    }

                    TileDataSource firstBand = inputDataSource[this.sourceBandIndices[0]];
                    TileDataSource secondBand = inputDataSource[this.sourceBandIndices[1]];
                    TileDataSource thirdBand = inputDataSource[this.sourceBandIndices[2]];

                    float valueB4Band = firstBand.getSampleFloat(x, y);
                    float valueB8Band = secondBand.getSampleFloat(x, y);
                    float valueB11Band = thirdBand.getSampleFloat(x, y);

                    trimmingValidSegments.addPixelValuesBands(segmentationPixelValue, valueB4Band, valueB8Band, valueB11Band);
                }
            }
        }

        IntSet validSegmentIdsAfterTrimming = trimmingValidSegments.processResult(0, null);

        synchronized (this.majorityVotingValidSegments) {
            this.majorityVotingValidSegments.processMovingWindowValidSegments(movingWindowValidSegmentIds, validSegmentIdsAfterTrimming);
        }

        // reset the references
        WeakReference<MovingWindow> referenceMovingWindow = new WeakReference<MovingWindow>(movingWindow);
        referenceMovingWindow.clear();
        WeakReference<TrimmingValidSegments> referenceTrimmingValidSegments = new WeakReference<TrimmingValidSegments>(trimmingValidSegments);
        referenceTrimmingValidSegments.clear();
        WeakReference<Map<String, TileDataSource[]>> referenceInputProductBandsMap = new WeakReference<Map<String, TileDataSource[]>>(inputProductBandsMap);
        referenceInputProductBandsMap.clear();
        WeakReference<IntSet> referenceMovingWindowValidSegmentIds = new WeakReference<IntSet>(movingWindowValidSegmentIds);
        referenceMovingWindowValidSegmentIds.clear();
        WeakReference<IntSet> referenceValidSegmentIdsAfterTrimming = new WeakReference<IntSet>(validSegmentIdsAfterTrimming);
        referenceValidSegmentIdsAfterTrimming.clear();

        if (logger.isLoggable(Level.FINE)) {
            int localTileWidth = movingLocalTileRightX - movingLocalTileLeftX;
            int localTileHeight = movingLocalTileBottomY - movingLocalTileTopY;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Moving window for tile region: valid segment count: " + movingWindowValidSegmentIds.size()+", bounds [x=" + movingLocalTileLeftX+", y="+movingLocalTileTopY+", width="+localTileWidth+", height="+localTileHeight+"]");
        }
    }

    private int computeLocalTileTopY(int movingLocalTileTopY) {
        return (movingLocalTileTopY / this.tileSize.height) * this.tileSize.height;
    }

    private int computeLocalTileLeftX(int movingLocalTileLeftX) {
        return (movingLocalTileLeftX / this.tileSize.width) * this.tileSize.width;
    }
    private int computeLocalTileWidth(int localTileLeftX) {
        int imageWidth = this.colorFillerMatrix.getColumnCount();
        int localTileWidth = this.tileSize.width;
        if ((localTileLeftX + localTileWidth) > imageWidth) {
            localTileWidth = imageWidth - localTileLeftX;
        }
        return localTileWidth;
    }

    private int computeLocalTileHeight(int localTileTopY) {
        int imageHeight = this.colorFillerMatrix.getRowCount();
        int localTileHeight = this.tileSize.height;
        if ((localTileTopY + localTileHeight) > imageHeight) {
            localTileHeight = imageHeight - localTileTopY;
        }
        return localTileHeight;
    }
}
