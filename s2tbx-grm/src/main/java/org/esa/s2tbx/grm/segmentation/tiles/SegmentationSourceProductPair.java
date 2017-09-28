package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.TileDataSource;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.matrix.FloatMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class SegmentationSourceProductPair {
    private static final Logger logger = Logger.getLogger(SegmentationSourceProductPair.class.getName());

    private final Path currentTemporaryFolder;
    private final Path previousTemporaryFolder;

    public SegmentationSourceProductPair(Path currentTemporaryFolder, Path previousTemporaryFolder) {
        this.currentTemporaryFolder = currentTemporaryFolder;
        this.previousTemporaryFolder = previousTemporaryFolder;
    }

    public TileDataSource[] buildSourceTiles(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex,
                                             int localColumnIndex, BoundingBox segmentationTileBounds)
                                             throws IOException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Read bands values for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        TileDataSource[] currentSourceTiles = buildSourceTiles(segmentationTileBounds, this.currentTemporaryFolder);
        TileDataSource[] previousSourceTiles = buildSourceTiles(segmentationTileBounds, this.previousTemporaryFolder);

        int count = currentSourceTiles.length + previousSourceTiles.length + currentSourceTiles.length;
        TileDataSource[] output = new TileDataSource[count];
        for (int i=0; i<currentSourceTiles.length; i++) {
            output[i] = currentSourceTiles[i];
            output[i + currentSourceTiles.length] = previousSourceTiles[i];
            output[i + (2*currentSourceTiles.length)] = new DifferenceTileDataSourceImpl(currentSourceTiles[i], previousSourceTiles[i]);
        }
        return output;
    }

    public static String buildSegmentationTileFileName(BoundingBox segmentationTileBounds) {
        return "segmentationTile-"+segmentationTileBounds.getLeftX()+"-"+segmentationTileBounds.getTopY()+"-"+segmentationTileBounds.getWidth()+"-"+segmentationTileBounds.getHeight()+".bin";
    }

    public static TileDataSource[] buildSourceTiles(BoundingBox segmentationTileBounds, Path temporaryFolder) throws IOException {
        String tileFileName = buildSegmentationTileFileName(segmentationTileBounds);
        File nodesFile = temporaryFolder.resolve(tileFileName).toFile();

        BufferedInputStreamWrapper inputFileStream = null;
        try {
            inputFileStream = new BufferedInputStreamWrapper(nodesFile);

            int bandCount = inputFileStream.readInt();

            int segmentationTileTopY = segmentationTileBounds.getTopY();
            int segmentationTileLeftX = segmentationTileBounds.getLeftX();

            TileDataSourceImplNew[] output = new TileDataSourceImplNew[bandCount];
            for (int i=0; i<bandCount; i++) {
                output[i] = new TileDataSourceImplNew(segmentationTileBounds.getHeight(), segmentationTileBounds.getWidth(), segmentationTileTopY, segmentationTileLeftX);
            }

            int segmentationTileBottomY = segmentationTileTopY + segmentationTileBounds.getHeight();
            int segmentationTileRightX = segmentationTileLeftX + segmentationTileBounds.getWidth();
            for (int y = segmentationTileTopY; y < segmentationTileBottomY; y++) {
                for (int x = segmentationTileLeftX; x < segmentationTileRightX; x++) {
                    for (int i=0; i<bandCount; i++) {
                        output[i].setValueAt(y, x, inputFileStream.readFloat());
                    }
                }
            }

            return output;
        } finally {
            if (inputFileStream != null) {
                try {
                    inputFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    private static class TileDataSourceImplNew extends FloatMatrix implements TileDataSource {

        private final int segmentationTileTopY;
        private final int segmentationTileLeftX;

        public TileDataSourceImplNew(int rowCount, int columnCount, int segmentationTileTopY, int segmentationTileLeftX) {
            super(rowCount, columnCount);

            this.segmentationTileTopY = segmentationTileTopY;
            this.segmentationTileLeftX = segmentationTileLeftX;
        }

        @Override
        public float getSampleFloat(int x, int y) {
            return getValueAt(y, x);
        }

        @Override
        public float getValueAt(int rowIndex, int columnIndex) {
            return super.getValueAt(rowIndex - this.segmentationTileTopY, columnIndex - this.segmentationTileLeftX);
        }

        @Override
        public void setValueAt(int rowIndex, int columnIndex, float value) {
            super.setValueAt(rowIndex - this.segmentationTileTopY, columnIndex - this.segmentationTileLeftX, value);
        }
    }

    private static class DifferenceTileDataSourceImpl implements TileDataSource {
        private final TileDataSource currentTileDataSource;
        private final TileDataSource previousTileDataSource;

        public DifferenceTileDataSourceImpl(TileDataSource currentTileDataSource, TileDataSource previousTileDataSource) {
            this.currentTileDataSource = currentTileDataSource;
            this.previousTileDataSource = previousTileDataSource;
        }

        @Override
        public float getSampleFloat(int x, int y) {
            return this.currentTileDataSource.getSampleFloat(x, y) - this.previousTileDataSource.getSampleFloat(x, y);
        }
    }
}
