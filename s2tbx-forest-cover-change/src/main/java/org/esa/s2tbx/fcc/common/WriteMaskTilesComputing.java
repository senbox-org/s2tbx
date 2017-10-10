package org.esa.s2tbx.fcc.common;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.tiles.AbstractTileSegmenter;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.BufferedOutputStreamWrapper;
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
public class WriteMaskTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(WriteMaskTilesComputing.class.getName());

     private final Path temporaryFolder;
    private final Mask maskToWrite;

    public WriteMaskTilesComputing(Mask maskToWrite, int tileWidth, int tileHeight, Path temporaryParentFolder) throws IOException {
        super(maskToWrite.getRasterWidth(), maskToWrite.getRasterHeight(), tileWidth, tileHeight);


        String temporaryFolderName = "product-masks" + Long.toString(System.currentTimeMillis());
        this.temporaryFolder = Files.createDirectories(temporaryParentFolder.resolve(temporaryFolderName));
        this.maskToWrite = maskToWrite;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Write band values for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        String tileFileName =  "maskTile-"+ tileLeftX +"-"+tileTopY+"-"+tileWidth+"-"+tileHeight+".bin";
        File nodesFile = this.temporaryFolder.resolve(tileFileName).toFile();

        BufferedOutputStreamWrapper outputFileStream = null;
        try {
            outputFileStream = new BufferedOutputStreamWrapper(nodesFile);

            int tileBottomY = tileTopY + tileHeight;
            int tileRightX = tileLeftX + tileWidth;
            for (int y = tileTopY; y < tileBottomY; y++) {
                for (int x = tileLeftX; x < tileRightX; x++) {
                        outputFileStream.writeFloat(this.maskToWrite.getSampleFloat(x, y));
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

    public Path runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.temporaryFolder;
    }
}
