package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.IntSet;
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

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * @author Jean Coravu
 */
@OperatorMetadata(
        alias = "UnionMasksOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class UnionMasksOp extends Operator {
    @SourceProduct(alias = "source", description = "The first source product")
    private Product currentSegmentationSourceProduct;
    @SourceProduct(alias = "source", description = "The second source product")
    private Product previousSegmentationSourceProduct;

    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet currentSegmentationTrimmingRegionKeys;
    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet previousSegmentationTrimmingRegionKeys;

    @TargetProduct
    private Product targetProduct;

    public UnionMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.currentSegmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSegmentationSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.currentSegmentationSourceProduct.getName() + "_union", this.currentSegmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        this.targetProduct.setSceneGeoCoding(this.currentSegmentationSourceProduct.getSceneGeoCoding());
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();
        Band currentSegmentationBand = this.currentSegmentationSourceProduct.getBandAt(0);
        Band previousSegmentationBand = this.previousSegmentationSourceProduct.getBandAt(0);

        for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
            for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
                int segmentationPixelValue = previousSegmentationBand.getSampleInt(x, y);
                if (this.previousSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                    segmentationPixelValue = 1;
                }
                else  {
                    segmentationPixelValue = currentSegmentationBand.getSampleInt(x, y);
                    if (this.currentSegmentationTrimmingRegionKeys.contains(segmentationPixelValue)) {
                        segmentationPixelValue = 1;
                    } else {
                        segmentationPixelValue = ForestCoverChangeConstans.NO_DATA_VALUE;
                    }
                }
                targetTile.setSample(x, y, segmentationPixelValue);
            }
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(UnionMasksOp.class);
        }
    }
}
