package org.esa.s2tbx.fcc.common;

import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;
import org.esa.snap.utils.matrix.ByteMatrix;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractWriteMasksTilesComputing extends AbstractImageTilesParallelComputing {

    private static final Logger logger = Logger.getLogger(AbstractWriteMasksTilesComputing.class.getName());

    private final Path temporaryFolder;

    public AbstractWriteMasksTilesComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight, Path temporaryParentFolder) throws IOException {
        super(imageWidth, imageHeight, tileWidth, tileHeight);

        String temporaryFolderName = "product-masks" + Long.toString(System.currentTimeMillis());
        this.temporaryFolder = Files.createDirectories(temporaryParentFolder.resolve(temporaryFolderName));
    }

    protected abstract boolean isValidMaskPixel(int x, int y);

    @Override
    protected final void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
            throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Write mask values for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        File nodesFile = AbstractWriteMasksTilesComputing.computeMaskTilePath(this.temporaryFolder, tileLeftX, tileTopY, tileWidth, tileHeight).toFile();

        BufferedOutputStreamWrapper outputFileStream = null;
        try {
            outputFileStream = new BufferedOutputStreamWrapper(nodesFile);

            int tileBottomY = tileTopY + tileHeight;
            int tileRightX = tileLeftX + tileWidth;
            for (int y = tileTopY; y < tileBottomY; y++) {
                for (int x = tileLeftX; x < tileRightX; x++) {
                    outputFileStream.writeBoolean(isValidMaskPixel(x, y));
                }
            }
        } finally {
            if (outputFileStream != null) {
                try {
                    outputFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    public final Path runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.temporaryFolder;
    }

    public static ByteMatrix readMaskTile(Path parentFolderPath, int tileLeftX, int tileTopY, int tileWidth, int tileHeight)
            throws IOException {

        File nodesFile = AbstractWriteMasksTilesComputing.computeMaskTilePath(parentFolderPath, tileLeftX, tileTopY, tileWidth, tileHeight).toFile();

        BufferedInputStreamWrapper inputFileStream = null;
        try {
            inputFileStream = new BufferedInputStreamWrapper(nodesFile);

            ByteMatrix maskTilePixels = new ByteMatrix(tileHeight, tileWidth);

            int tileBottomY = tileTopY + tileHeight;
            int tileRightX = tileLeftX + tileWidth;
            for (int y = tileTopY; y < tileBottomY; y++) {
                for (int x = tileLeftX; x < tileRightX; x++) {
                    maskTilePixels.setValueAt(y-tileTopY, x-tileLeftX, inputFileStream.readBoolean() ? 1 : (byte)ForestCoverChangeConstants.NO_DATA_VALUE);
                }
            }

            return maskTilePixels;
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

    public static Path computeMaskTilePath(Path parentFolderPath, int tileLeftX, int tileTopY, int tileWidth, int tileHeight) {
        String tileFileName = "maskTile-"+ tileLeftX +"-"+tileTopY+"-"+tileWidth+"-"+tileHeight+".bin";
        return parentFolderPath.resolve(tileFileName);
    }
}