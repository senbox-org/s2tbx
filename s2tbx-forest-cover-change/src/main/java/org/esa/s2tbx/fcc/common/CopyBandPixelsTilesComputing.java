package org.esa.s2tbx.fcc.common;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class CopyBandPixelsTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(CopyBandPixelsTilesComputing.class.getName());

    private final Band bandToCopy;
    private final ProductData productData;

    public CopyBandPixelsTilesComputing(Band bandToCopy, int tileWidth, int tileHeight) {
        super(bandToCopy.getRasterWidth(), bandToCopy.getRasterHeight(), tileWidth, tileHeight);

        this.bandToCopy = bandToCopy;

        this.productData = ProductData.createInstance(ProductData.TYPE_UINT8, bandToCopy.getRasterWidth() * bandToCopy.getRasterHeight());
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Write band values for tile region: row index: " + localRowIndex + ", column index: " + localColumnIndex + ", bounds [x=" + tileLeftX + ", y=" + tileTopY + ", width=" + tileWidth + ", height=" + tileHeight + "]");
        }

        int imageWidth = this.bandToCopy.getRasterWidth();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int pixelValue = this.bandToCopy.getSampleInt(x, y);
                synchronized (this.productData) {
                    this.productData.setElemIntAt(imageWidth * y + x, pixelValue);
                }
            }
        }
    }

    public ProductData runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }
}
