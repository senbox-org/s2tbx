package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class ProductDataTilesComputing extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(ProductDataTilesComputing.class.getName());

    private final ProductData productData;
    private final IntMatrix inputMatrix;

    public ProductDataTilesComputing(IntMatrix inputMatrix, int tileWidth, int tileHeight) {
        super(inputMatrix.getColumnCount(), inputMatrix.getRowCount(), tileWidth, tileHeight);

        this.inputMatrix = inputMatrix;

        int sceneWidth = this.inputMatrix.getColumnCount();
        int sceneHeight = this.inputMatrix.getRowCount();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        int sceneWidth = this.inputMatrix.getColumnCount();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int pixelValue = this.inputMatrix.getValueAt(y, x);

                synchronized (this.productData) {
                    this.productData.setElemIntAt(sceneWidth * y + x, pixelValue);
                }
            }
        }
    }

    public ProductData runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }
}
