package org.esa.s2tbx.fcc.trimming;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.AveragePixelsSourceBands;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
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
    @SourceProduct(alias = "Source product", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    @SourceProduct(alias = "Segmentation source product", description = "The source products to be used for trimming.")
    private Product segmentationSourceProduct;

    @Parameter(label = "Source band indices", description = "The source band indices.")
    private int[] sourceBandIndices;

    @TargetProduct
    private Product targetProduct;

    private TrimmingRegionComputingHelper trimmingRegionComputingHelper;
    private Set<String> processedTiles;

    public TrimmingRegionComputingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.segmentationSourceProduct.getName() + "_trim", this.segmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        this.processedTiles = new HashSet<String>();
        this.trimmingRegionComputingHelper = new TrimmingRegionComputingHelper(this.segmentationSourceProduct, this.sourceProduct, this.sourceBandIndices, 0, 0);
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();
        String key = tileRegion.x+"|"+tileRegion.y+"|"+tileRegion.width+"|"+tileRegion.height;
        boolean canProcessTile = false;
        synchronized (this.processedTiles) {
            canProcessTile = this.processedTiles.add(key);
        }
        if (canProcessTile) {
            try {
                this.trimmingRegionComputingHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, 0, 0);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        }
    }

    public final IntSet processResult(int threadCount, Executor threadPool) throws Exception {
        return this.trimmingRegionComputingHelper.processResult(threadCount, threadPool);
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingRegionComputingOp.class);
        }
    }
}
