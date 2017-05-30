package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.media.jai.JAI;
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

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProduct(alias = "source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    private Product landCoverProduct;
    private HashMap<Integer,PixelStatistic> statistic;

    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();
        this.landCoverProduct = addLandCoverBand();
        this.statistic = new HashMap<>();
        createTargetProduct();
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());
    }

    private void createTargetProduct() {
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
        Rectangle region  = targetTile.getRectangle();
        Band segmentationBand = sourceProduct.getBandAt(0);
        for (int y = region.y; y < region.y + region.height; y++) {
            for (int x = region.x; x < region.x + region.width; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x,y);
                PixelStatistic pixel = this.statistic.get(segmentationPixelValue);
                if (pixel==null) {
                    pixel = new PixelStatistic(0,0);
                    this.statistic.put(segmentationPixelValue, pixel);
                }
                pixel.incrementTotalNumberPixels();
                for(int index : ForestCoverChangeConstans.COVER_LABElS)
                if (index ==landCoverProduct.getBandAt(0).getSampleInt(x,y)) {
                    pixel.incrementPixelsInRange();
                }
            }
        }
    }

    public Map<Integer,PixelStatistic> getStatistics(){
        return this.statistic;
    }

    public Product getLandCoverProduct(){ return this.landCoverProduct; }

    private void validateSourceProduct() {
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if(geo == null){
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
    public static final class PixelStatistic{
        int totalNumberPixels;
        int pixelsInRage;

        public PixelStatistic() {
        }

        public PixelStatistic(int totalNumberPixels, int pixelsInRage) {
            this.totalNumberPixels = totalNumberPixels;
            this.pixelsInRage = pixelsInRage;
        }

        public int getTotalNumberPixels() {
            return totalNumberPixels;
        }

        public void setTotalNumberPixels(int  totalNumberPixels) {
            this.totalNumberPixels = totalNumberPixels;
        }

        public void incrementTotalNumberPixels(){
            this.totalNumberPixels++;
        }

        public int getPixelsInRange() {
            return pixelsInRage;
        }

        public void setPixelsInRange(int pixelsInRage) {
            this.pixelsInRage = pixelsInRage;
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
