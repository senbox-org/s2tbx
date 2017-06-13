package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
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
    @SourceProduct(alias = "source", description = "The segmentation source product with segments that have more than 95% forest cover")
    private Product segmentationSourceProduct;

    @SourceProduct(alias = "sourceCompositionProduct", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "sourceBandIndices", description = "The index from the source product to be used.")
    private int[] sourceBandIndices;

    private Int2ObjectMap<List<PixelSourceBands>> statistics;

    public TrimmingRegionComputingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateSourceProducts();
        validateParametersInput();
        this.statistics = new Int2ObjectLinkedOpenHashMap<List<PixelSourceBands>>();
        createTargetProduct();
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();
        Band firstBand = this.sourceProduct.getBandAt(sourceBandIndices[0]);
        Band secondBand = this.sourceProduct.getBandAt(sourceBandIndices[1]);
        Band thirdBand = this.sourceProduct.getBandAt(sourceBandIndices[2]);
        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);

        for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
            for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                if (segmentationPixelValue != ForestCoverChangeConstans.NO_DATA_VALUE) {
                    synchronized (this.statistics) {
                        List<PixelSourceBands> value = this.statistics.get(segmentationPixelValue);
                        if (value == null) {
                            value = new ArrayList<>();
                            this.statistics.put(segmentationPixelValue, value);
                        }
                        PixelSourceBands pixels = new PixelSourceBands(firstBand.getSampleFloat(x, y), secondBand.getSampleFloat(x, y),
                                                                       thirdBand.getSampleFloat(x, y), secondBand.getSampleFloat(x, y));
                        value.add(pixels);
                    }
                }
            }
        }
    }

    /**
     *
     * @return returns the HashMap containing the pixels values from the 4 bands selected per region
     */
    public Int2ObjectMap<List<PixelSourceBands>> getPixelsStatistics() {
        return this.statistics;
    }

    private void createTargetProduct() {
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.segmentationSourceProduct.getName() + "_trim", this.segmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
        this.targetProduct.setPreferredTileSize(JAI.getDefaultTileSize());
    }

    private void validateParametersInput() {
        if (this.sourceBandIndices.length != 3) {
            throw new OperatorException("The number of bands must be equal to 3.");
        }
    }

    private void validateSourceProducts() {
        if (this.sourceProduct.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.sourceProduct.getName());
            throw new OperatorException(message);
        }
        if ((this.sourceProduct.getSceneRasterHeight() != this.segmentationSourceProduct.getSceneRasterHeight()) ||
                (this.sourceProduct.getSceneRasterWidth() != this.segmentationSourceProduct.getSceneRasterWidth())) {
            String message = String.format("Source product '%s' must have the same scene raster size as the source Composition Product '%s'.\n" +
                            "Please consider resampling it so that the 2 products have the same size.",
                    this.segmentationSourceProduct.getName(), this.sourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingRegionComputingOp.class);
        }
    }
}
