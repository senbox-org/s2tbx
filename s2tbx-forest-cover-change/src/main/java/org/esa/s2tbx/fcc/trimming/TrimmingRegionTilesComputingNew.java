package org.esa.s2tbx.fcc.trimming;

import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.matrix.FloatMatrix;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class TrimmingRegionTilesComputingNew extends AbstractRegionParallelComputing {
    private static final Logger logger = Logger.getLogger(TrimmingRegionTilesComputingNew.class.getName());

    private final IntMatrix colorFillerMatrix;
    private final int[] sourceBandIndices;
    private final Path temporarySourceSegmentationTilesFolder;
    private final int segmentationTileMargin;

    public TrimmingRegionTilesComputingNew(IntMatrix colorFillerMatrix, Path temporarySourceSegmentationTilesFolder, int[] sourceBandIndices, int tileWidth, int tileHeight) {
        super(colorFillerMatrix.getColumnCount(), colorFillerMatrix.getRowCount(), tileWidth, tileHeight);

        this.colorFillerMatrix = colorFillerMatrix;
        this.temporarySourceSegmentationTilesFolder = temporarySourceSegmentationTilesFolder;
        this.sourceBandIndices = sourceBandIndices;

        this.segmentationTileMargin = AbstractTileSegmenter.computeTileMargin(tileWidth, tileHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Trimming statistics for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        int imageWidth = getImageWidth();
        int imageHeight = getImageHeight();
        ProcessingTile segmentationProcessingTile = AbstractTileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight, this.segmentationTileMargin, imageWidth, imageHeight);
        BoundingBox segmentationTileBounds = segmentationProcessingTile.getRegion();
        TileDataSource[] output = SegmentationSourceProductPair.buildSourceTiles(segmentationTileBounds, this.temporarySourceSegmentationTilesFolder);

        TileDataSource firstBand = output[this.sourceBandIndices[0]];
        TileDataSource secondBand = output[this.sourceBandIndices[1]];
        TileDataSource thirdBand = output[this.sourceBandIndices[2]];

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = this.colorFillerMatrix.getValueAt(y, x);
                if (segmentationPixelValue != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    float a = firstBand.getSampleFloat(x, y);
                    float b = secondBand.getSampleFloat(x, y);
                    float c = thirdBand.getSampleFloat(x, y);

                    addPixelValuesBands(segmentationPixelValue, a, b, c);
                }
            }
        }
    }
}
