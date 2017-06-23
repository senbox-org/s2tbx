package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ObjectsSelectionOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class ObjectsSelectionOp extends Operator {
    private static final Logger logger = Logger.getLogger(ObjectsSelectionOp.class.getName());

    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    private Product landCoverProduct;
    private Int2ObjectMap<PixelStatistic> statistics;

    public ObjectsSelectionOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();
        this.landCoverProduct = addLandCoverBand();
        this.statistics = new Int2ObjectLinkedOpenHashMap<PixelStatistic>();

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_CCI", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute object selection for tile region: bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band segmentationBand = this.sourceProduct.getBandAt(0);
        Band landCoverBand = this.landCoverProduct.getBandAt(0);
        for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
            for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                int landCoverPixelValue = landCoverBand.getSampleInt(x, y);
                synchronized (this.statistics) {
                    PixelStatistic pixel = this.statistics.get(segmentationPixelValue);
                    if (pixel == null) {
                        pixel = new PixelStatistic(0, 0);
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

    public Int2ObjectMap<PixelStatistic> getStatistics() {
        return this.statistics;
    }

    private void validateSourceProduct() {
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if (geo == null) {
            throw new OperatorException("Source product must contain GeoCoding");
        }
    }

    private Product createLandCoverProduct() {
        Product landCoverProduct = new Product(this.sourceProduct.getName(), this.sourceProduct.getProductType(),
                this.sourceProduct.getSceneRasterWidth(), this.sourceProduct.getSceneRasterHeight());
        landCoverProduct.setStartTime(this.sourceProduct.getStartTime());
        landCoverProduct.setEndTime(this.sourceProduct.getEndTime());
        landCoverProduct.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(this.sourceProduct,  landCoverProduct);
        ProductUtils.copyGeoCoding(this.sourceProduct,  landCoverProduct);
        ProductUtils.copyTiePointGrids(this.sourceProduct,  landCoverProduct);
        ProductUtils.copyVectorData(this.sourceProduct,  landCoverProduct);
        return landCoverProduct;
    }

    private Product addLandCoverBand() {
        Product landCover = createLandCoverProduct();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("landCoverNames", ForestCoverChangeConstans.LAND_COVER_NAME);
        return GPF.createProduct("AddLandCover", parameters, landCover);
    }

    public static final class PixelStatistic {
        int totalNumberPixels;
        int pixelsInRage;

        public PixelStatistic(int totalNumberPixels, int pixelsInRage) {
            this.totalNumberPixels = totalNumberPixels;
            this.pixelsInRage = pixelsInRage;
        }

        public int getTotalNumberPixels() {
            return totalNumberPixels;
        }

        public void incrementTotalNumberPixels(){
            this.totalNumberPixels++;
        }

        public int getPixelsInRange() {
            return pixelsInRage;
        }

        public void incrementPixelsInRange(){
            this.pixelsInRage++;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ObjectsSelectionOp.class);
        }
    }
}
