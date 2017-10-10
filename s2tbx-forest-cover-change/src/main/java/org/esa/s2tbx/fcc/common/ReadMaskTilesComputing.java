package org.esa.s2tbx.fcc.common;

import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;

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
public class ReadMaskTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(ReadMaskTilesComputing.class.getName());

    private final Path temporaryFolder;
    private final ProductData productData;
    private final int imageWidth;

    public ReadMaskTilesComputing(int imageWidth, int imageHeight, int tileWidth, int tileHeight, Path temporaryFolder) throws IOException {
        super(imageWidth, imageHeight, tileWidth, tileHeight);

        this.temporaryFolder = temporaryFolder;
        this.imageWidth = imageWidth;
        this.productData = ProductData.createInstance(ProductData.TYPE_FLOAT32, imageWidth * imageHeight);
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

        BufferedInputStreamWrapper inputFileStream = null;
        try {
            inputFileStream = new BufferedInputStreamWrapper(nodesFile);

            int tileBottomY = tileTopY + tileHeight;
            int tileRightX = tileLeftX + tileWidth;
            for (int y = tileTopY; y < tileBottomY; y++) {
                for (int x = tileLeftX; x < tileRightX; x++) {
                    float pixelValue = inputFileStream.readFloat();
                    synchronized (this.productData) {
                        this.productData.setElemFloatAt(this.imageWidth * y + x, pixelValue);
                    }
                }
            }
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

    public ProductData runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }
}
