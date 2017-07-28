package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
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
public class ColorFillerTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(ColorFillerTilesComputing.class.getName());

    private final Product segmentationSourceProduct;
    private final IntSet validRegions;
    private final IntMatrix result;

    public ColorFillerTilesComputing(Product segmentationSourceProduct, IntSet validRegions, int tileWidth, int tileHeight) {
        super(segmentationSourceProduct.getSceneRasterWidth(), segmentationSourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.segmentationSourceProduct = segmentationSourceProduct;
        this.validRegions = validRegions;

        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        this.result = new IntMatrix(sceneWidth, sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
            throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Color filler for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationValue = segmentationBand.getSampleInt(x, y);
                if (!this.validRegions.contains(segmentationValue)) {
                    segmentationValue = ForestCoverChangeConstants.NO_DATA_VALUE;
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
