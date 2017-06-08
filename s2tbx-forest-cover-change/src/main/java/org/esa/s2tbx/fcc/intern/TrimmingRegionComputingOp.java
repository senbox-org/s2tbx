package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.JAI;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "TrimmingRegionComputingOp",
        version="1.0",
        category = "",
        description = "Creates a hash map containing the values from the source bands for a respective segmentation region",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class TrimmingRegionComputingOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(alias = "source", description = "The segmentation source product with segments that have more than 95% forest cover")
    private Product segmentationSourceProduct;

    @SourceProducts(alias = "sourceCompositionProduct", description = "The source products to be used for trimming.")
    private Product sourceCompositionProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "bandsUsed", description = "the index from the sourceCompositionProduct to be used")
    int[] bandsUsed;

    private Map<Integer, List<PixelSourceBands>> statistics;

    @Override
    public void initialize() throws OperatorException {
        validateSourceProducts();
        validateParametersInput();
        this.statistics = new HashMap<>();
        createTargetProduct();
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());
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
        if(this.bandsUsed.length!=3){
            throw new OperatorException("the number of bands must be equal to 4");
        }
    }

    private void validateSourceProducts() {
        if(this.sourceCompositionProduct.isMultiSize()){
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.sourceCompositionProduct.getName());
            throw new OperatorException(message);
        }
        if(this.sourceCompositionProduct.getSceneRasterHeight() != this.segmentationSourceProduct.getSceneRasterHeight()||
                this.sourceCompositionProduct.getSceneRasterWidth() != this.segmentationSourceProduct.getSceneRasterWidth()){
            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
                            "Please consider resampling it so that the 2 products have the same size.",
                    this.segmentationSourceProduct.getName(),this.sourceCompositionProduct.getName() );
            throw new OperatorException(message);
        }

    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle region = targetTile.getRectangle();
        Band firstBand  =  this.sourceCompositionProduct.getBandAt(bandsUsed[0]);
        Band secondBand =  this.sourceCompositionProduct.getBandAt(bandsUsed[1]);
        Band thirdBand  =  this.sourceCompositionProduct.getBandAt(bandsUsed[2]);
        Band fourthBand =  this.sourceCompositionProduct.getBandAt(bandsUsed[1]);

        for (int y = region.y; y < region.y + region.height; y++) {
            for (int x = region.x; x < region.x + region.width; x++) {
                int sourceProductPixelValue = this.segmentationSourceProduct.getBandAt(0).getSampleInt(x,y);
                if(sourceProductPixelValue != ForestCoverChangeConstans.NO_DATA_VALUE){
                    List<PixelSourceBands> value =  this.statistics.get(sourceProductPixelValue);
                    PixelSourceBands pixels = new PixelSourceBands(firstBand.getSampleFloat(x,y),
                                                                   secondBand.getSampleFloat(x,y),
                                                                   thirdBand.getSampleFloat(x,y),
                                                                   fourthBand.getSampleFloat(x,y));
                    if(value == null){
                        value = new ArrayList<>();
                        statistics.put(sourceProductPixelValue, value);
                    }
                    value.add(pixels);
                }
            }
        }
    }

    /**
     *
     * @return returns the HashMap containing the pixels values from the 4 bands selected per region
     */
    public Map<Integer, List<PixelSourceBands>> getPixelsStatistics(){
        return this.statistics;
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingRegionComputingOp.class);
        }
    }
}
