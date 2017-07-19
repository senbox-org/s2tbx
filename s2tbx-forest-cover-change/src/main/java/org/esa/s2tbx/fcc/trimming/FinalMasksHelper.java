package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.AbstractImageTilesParallelComputing;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class FinalMasksHelper extends AbstractImageTilesParallelComputing {
    private static final Logger logger = Logger.getLogger(FinalMasksHelper.class.getName());

    private final ProductData productData;
    private final Product differenceSegmentationProduct;
    private final Product unionMaskProduct;
    private final IntSet differenceTrimmingSet;

    public FinalMasksHelper(Product differenceSegmentationProduct, Product unionMaskProduct, IntSet differenceTrimmingSet, int tileWidth, int tileHeight) {
        super(differenceSegmentationProduct.getSceneRasterWidth(), differenceSegmentationProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.differenceSegmentationProduct = differenceSegmentationProduct;
        this.unionMaskProduct = unionMaskProduct;
        this.differenceTrimmingSet = differenceTrimmingSet;

        int sceneWidth = this.differenceSegmentationProduct.getSceneRasterWidth();
        int sceneHeight = this.differenceSegmentationProduct.getSceneRasterHeight();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Final masks for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band differenceSegmentationBand = this.differenceSegmentationProduct.getBandAt(0);
        Band unionMaskBand = this.unionMaskProduct.getBandAt(0);
        int sceneWidth = this.differenceSegmentationProduct.getSceneRasterWidth();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = differenceSegmentationBand.getSampleInt(x, y);
                if (this.differenceTrimmingSet.contains(segmentationPixelValue)) {
                    int unionPixelValue = unionMaskBand.getSampleInt(x, y);
                    if (unionPixelValue == ForestCoverChangeConstants.NO_DATA_VALUE) {
                        segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                    } else {
                        segmentationPixelValue = 1;
                    }
                } else {
                    segmentationPixelValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                }
                synchronized (this.productData) {
                    this.productData.setElemIntAt(sceneWidth * y + x, segmentationPixelValue);
                }
            }
        }
    }

    public ProductData runTilesInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        return this.productData;
    }

    public ProductData getProductData() {
        return productData;
    }
}
