package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstans;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class ObjectsSelectionHelper extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(ObjectsSelectionHelper.class.getName());

    private final Product sourceProduct;
    private final Product landCoverProduct;
    private final Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics;

    public ObjectsSelectionHelper(Product sourceProduct, Product landCoverProduct, int tileWidth, int tileHeight) {
        super(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.sourceProduct = sourceProduct;
        this.statistics = new Int2ObjectLinkedOpenHashMap<ObjectsSelectionOp.PixelStatistic>();

        this.landCoverProduct = landCoverProduct;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Object selection for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band segmentationBand = this.sourceProduct.getBandAt(0);
        Band landCoverBand = this.landCoverProduct.getBandAt(0);

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                int landCoverPixelValue = landCoverBand.getSampleInt(x, y);
                synchronized (this.statistics) {
                    ObjectsSelectionOp.PixelStatistic pixel = this.statistics.get(segmentationPixelValue);
                    if (pixel == null) {
                        pixel = new ObjectsSelectionOp.PixelStatistic(0, 0);
                        this.statistics.put(segmentationPixelValue, pixel);
                    }
                    pixel.incrementTotalNumberPixels();
                    for (int index : ForestCoverChangeConstans.COVER_LABElS) {
                        if (index == landCoverPixelValue) {
                            pixel.incrementPixelsInRange();
                        }
                    }
                }
            }
        }
    }

    public Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.statistics;
    }
}
