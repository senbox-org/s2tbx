package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class ObjectsSelectionTilesComputing extends AbstractImageTilesParallelComputing {

    private static final Logger logger = Logger.getLogger(ObjectsSelectionTilesComputing.class.getName());

    private final IntMatrix segmentationMatrix;
    private final Product landCoverProduct;
    private final IntSet landCoverValidPixels;
    private final Int2ObjectMap<PixelStatistic> statistics;

    public ObjectsSelectionTilesComputing(IntMatrix segmentationMatrix, Product landCoverProduct, IntSet landCoverValidPixels, int tileWidth, int tileHeight) {
        super(segmentationMatrix.getColumnCount(), segmentationMatrix.getRowCount(), tileWidth, tileHeight);

        this.segmentationMatrix = segmentationMatrix;
        this.landCoverProduct = landCoverProduct;
        this.landCoverValidPixels = landCoverValidPixels;

        this.statistics = new Int2ObjectLinkedOpenHashMap<PixelStatistic>();
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
            throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Object selection for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band landCoverBand = this.landCoverProduct.getBandAt(0);

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = this.segmentationMatrix.getValueAt(y, x);
                int landCoverPixelValue = landCoverBand.getSampleInt(x, y);
                synchronized (this.statistics) {
                    PixelStatistic pixel = this.statistics.get(segmentationPixelValue);
                    if (pixel == null) {
                        pixel = new PixelStatistic(0, 0);
                        this.statistics.put(segmentationPixelValue, pixel);
                    }
                    pixel.incrementTotalNumberPixels();
                    if (this.landCoverValidPixels.contains(landCoverPixelValue)) {
                        pixel.incrementPixelsInRange();
                    }
                }
            }
        }
    }

    public Int2ObjectMap<PixelStatistic> runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.statistics;
    }
}
