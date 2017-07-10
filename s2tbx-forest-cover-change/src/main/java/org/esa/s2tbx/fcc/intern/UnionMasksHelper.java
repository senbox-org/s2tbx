package org.esa.s2tbx.fcc.intern;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesHelper;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class UnionMasksHelper extends AbstractImageTilesHelper {
    private final ProductData productData;
    private final Product currentSegmentationSourceProduct;
    private final Product previousSegmentationSourceProduct;
    private final IntSet currentSegmentationTrimmingRegionKeys;
    private final IntSet previousSegmentationTrimmingRegionKeys;

    public UnionMasksHelper(Product currentSegmentationSourceProduct, Product previousSegmentationSourceProduct,
                            IntSet currentSegmentationTrimmingRegionKeys, IntSet previousSegmentationTrimmingRegionKeys, int tileWidth, int tileHeight) {

        super(currentSegmentationSourceProduct.getSceneRasterWidth(), currentSegmentationSourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.currentSegmentationSourceProduct = currentSegmentationSourceProduct;
        this.previousSegmentationSourceProduct = previousSegmentationSourceProduct;
        this.currentSegmentationTrimmingRegionKeys = currentSegmentationTrimmingRegionKeys;
        this.previousSegmentationTrimmingRegionKeys = previousSegmentationTrimmingRegionKeys;

        int sceneWidth = this.currentSegmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSegmentationSourceProduct.getSceneRasterHeight();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        Band currentSegmentationBand = this.currentSegmentationSourceProduct.getBandAt(0);
        Band previousSegmentationBand = this.previousSegmentationSourceProduct.getBandAt(0);
        int sceneWidth = this.currentSegmentationSourceProduct.getSceneRasterWidth();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = previousSegmentationBand.getSampleInt(x, y);
                if (this.previousSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                    int currentSegmentationPixelValue = currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(currentSegmentationPixelValue)) {
                        segmentationPixelValue = 255;
                    } else {
                        segmentationPixelValue = 50;
                    }
                } else {
                    segmentationPixelValue = currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        segmentationPixelValue = 100;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstans.NO_DATA_VALUE;
                    }
                }
                synchronized (this.productData) {
                    this.productData.setElemIntAt(sceneWidth * y + x, segmentationPixelValue);
                }
            }
        }
    }

    public ProductData computeRegionsInParallel(int threadCount, Executor threadPool) throws IllegalAccessException, IOException, InterruptedException {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }
}
