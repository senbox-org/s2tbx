package org.esa.s2tbx.fcc.intern;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.utils.AbstractImageTilesHelper;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class ColorFillerHelper extends AbstractImageTilesHelper {
    private static final Logger logger = Logger.getLogger(ColorFillerHelper.class.getName());

    private final Product sourceProduct;
    private final IntSet validRegions;
    private final ProductData productData;

    public ColorFillerHelper(Product sourceProduct, IntSet validRegions, int tileWidth, int tileHeight) {
        super(sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.sourceProduct = sourceProduct;
        this.validRegions = validRegions;

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        this.productData = ProductData.createInstance(ProductData.TYPE_INT32, sceneWidth * sceneHeight);
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex)
                           throws IOException, IllegalAccessException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Color filler for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band segmentationBand = this.sourceProduct.getBandAt(0);
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationValue = segmentationBand.getSampleInt(x, y);
                if (!this.validRegions.contains(segmentationValue)) {
                    segmentationValue = ForestCoverChangeConstans.NO_DATA_VALUE;
                }
                synchronized (this.productData) {
                    this.productData.setElemIntAt(sceneWidth * y + x, segmentationValue);
                }
            }
        }
    }

    public Product computeRegionsInParallel(int threadCount, Executor threadPool) throws Exception {
        super.executeInParallel(threadCount, threadPool);

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        Product targetProduct = new Product(this.sourceProduct.getName() + "_fill", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(this.sourceProduct, targetProduct);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        targetBand.setData(this.productData);

        targetProduct.addBand(targetBand);

        return targetProduct;
    }
}
