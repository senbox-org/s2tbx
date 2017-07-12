package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.JAI;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public abstract class AbstractRegionComputingOp extends Operator{

    private static final Logger logger = Logger.getLogger(AbstractRegionComputingOp.class.getName());

    @SourceProduct(alias = "Source", description = "The segmentation source product with segments that have more than 95% forest cover")
    protected Product segmentationSourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "sourceBandIndices", description = "The index from the source product to be used.")
    protected int[] sourceBandIndices;

    private Int2ObjectMap<AveragePixelsSourceBands> validRegionsMap;

    protected abstract float getFirstBandBandValue(int x, int y);

    protected abstract float getSecondBandBandValue(int x, int y);

    protected abstract float getThirdBandBandValue(int x, int y);

    protected abstract boolean isSegmentationPixelValid(int x, int y, int segmentationPixelValue);

    @Override
    public void initialize() throws OperatorException {
        validateParametersInput();
        this.validRegionsMap = new Int2ObjectLinkedOpenHashMap<>();
        createTargetProduct();
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute trimming statistics for tile region: bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);

        for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
            for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                if (isSegmentationPixelValid(x, y, segmentationPixelValue)) {
                    synchronized (this.validRegionsMap) {
                        AveragePixelsSourceBands value = this.validRegionsMap.get(segmentationPixelValue);
                        if (value == null) {
                            value = new AveragePixelsSourceBands();
                            this.validRegionsMap.put(segmentationPixelValue, value);
                        }
                        value.addPixelValuesBands(getFirstBandBandValue(x, y), getSecondBandBandValue(x, y), getThirdBandBandValue(x, y));
                    }
                }
            }
        }
    }

    private void createTargetProduct() {
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.segmentationSourceProduct.getName() + "_trim", this.segmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    private void validateParametersInput() {
        if (this.sourceBandIndices.length != 3) {
            throw new OperatorException("The number of bands must be equal to 3.");
        }
    }

    /**
     *
     * @return returns the HashMap containing the pixels values from the 4 bands selected per region
     */
    public Int2ObjectMap<AveragePixelsSourceBands> getValidRegionsMap() {
        return this.validRegionsMap;
    }


}
