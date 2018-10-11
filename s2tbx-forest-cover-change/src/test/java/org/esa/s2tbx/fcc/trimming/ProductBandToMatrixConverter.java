package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;
import org.esa.snap.utils.matrix.IntMatrix;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
class ProductBandToMatrixConverter extends AbstractImageTilesParallelComputing {
    private final Product productToConvert;
    private final IntMatrix result;

    ProductBandToMatrixConverter(Product productToConvert, int tileWidth, int tileHeight) {
        super(productToConvert.getSceneRasterWidth(), productToConvert.getSceneRasterHeight(), tileWidth, tileHeight);

        this.productToConvert = productToConvert;

        this.result = new IntMatrix(this.productToConvert.getSceneRasterWidth(), this.productToConvert.getSceneRasterHeight());
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
            throws IOException, IllegalAccessException, InterruptedException {

        Band segmentationBand = this.productToConvert.getBandAt(0);
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationValue = segmentationBand.getSampleInt(x, y);
                synchronized (this.result) {
                    this.result.setValueAt(y, x, segmentationValue);
                }
            }
        }
    }

    IntMatrix runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.result;
    }
}
