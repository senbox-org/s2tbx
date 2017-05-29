package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.media.jai.JAI;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */

@OperatorMetadata(
        alias = "ColorFillerOp",
        version="1.0",
        category = "",
        description = "Operaotr that fills the source product band's color with color from the Land COver Product band",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class ColorFillerOp extends Operator {
    @SuppressWarnings({"PackageVisibleField"})
    @SourceProduct(alias = "source", description = "The source product to be modified.")
    private Product sourceProduct;


    @TargetProduct
    private Product targetProduct;

    @Parameter (itemAlias = "percentagePixels", description = "The percentage of valid forrest pixels in a region")
    private float percentagePixels;

    private Product CCILandCoverProduct;
    private Map<Integer, ObjectsSelectionOp.PixelStatistic> statistics;
    private Set validRegions;
    @Override
    public void initialize() throws OperatorException {
        validateInputs();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", this.sourceProduct);
        ObjectsSelectionOp objSelOp = (ObjectsSelectionOp) GPF.getDefaultInstance().createOperator("ObjectsSelectionOp", parameters, sourceProducts, null);
        Product targetProductObjectsSelectionOp = objSelOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(objSelOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        this.statistics = objSelOp.getStatistics();
        this.CCILandCoverProduct = objSelOp.getLandCoverProduct();
        this.validRegions = generateValidStatisticRegions();
        createTargetProduct();
    }

    private Set generateValidStatisticRegions() {
        Set<Integer> validReg = new HashSet<>();
        for (Map.Entry<Integer, ObjectsSelectionOp.PixelStatistic> pair : this.statistics.entrySet()) {
            ObjectsSelectionOp.PixelStatistic value = pair.getValue();
            float percet = ((float)value.getPixelsInRange()/(float)value.getTotalNumberPixels())*100;
            if(percet>=this.percentagePixels){
                validReg.add(pair.getKey());
            }
        }
        return validReg;
    }

    private void validateInputs() {

        if(this.sourceProduct.isMultiSize()){
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.sourceProduct.getName());
            throw new OperatorException(message);
        }
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if(geo == null){
            String message = String.format("Source product '%s' must contain GeoCoding", this.sourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    private void createTargetProduct() {
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_fill", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        this.targetProduct.setSceneGeoCoding(this.sourceProduct.getSceneGeoCoding());
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
        final ProductNodeGroup<IndexCoding> indexCodingGroup = this.CCILandCoverProduct.getIndexCodingGroup();
        for (int i = 0; i < indexCodingGroup.getNodeCount(); i++) {
            IndexCoding sourceIndexCoding = indexCodingGroup.get(i);
            ProductUtils.copyIndexCoding(sourceIndexCoding, this.targetProduct);
        }
        final IndexCoding sourceIndexCoding = this.CCILandCoverProduct.getBandAt(0).getIndexCoding();
        if (sourceIndexCoding != null) {
            final String indexCodingName = sourceIndexCoding.getName();
            final IndexCoding destIndexCoding = this.targetProduct.getIndexCodingGroup().get(indexCodingName);
            this.targetProduct.getBandAt(0).setSampleCoding(destIndexCoding);
            this.targetProduct.getBandAt(0).setImageInfo(this.CCILandCoverProduct.getBandAt(0).getImageInfo());
        }
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle region  = targetTile.getRectangle();
        Band segmentationBand = sourceProduct.getBandAt(0);
        Band landCoverBand = this.CCILandCoverProduct.getBandAt(0);
        for (int y = region.y; y < region.y + region.height; y++) {
            for (int x = region.x; x < region.x + region.width; x++) {

                if(this.validRegions.contains(segmentationBand.getSampleInt(x,y))){
                    int value = landCoverBand.getSampleInt(x,y);
                    targetTile.setSample(x, y, value);
                } else {
                    targetTile.setSample(x, y, 0);
                }
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ColorFillerOp.class);
        }
    }
}
