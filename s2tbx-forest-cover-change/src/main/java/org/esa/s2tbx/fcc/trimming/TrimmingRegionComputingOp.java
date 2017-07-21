package org.esa.s2tbx.fcc.trimming;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

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
import org.esa.snap.utils.AbstractTilesComputingOp;

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
public class TrimmingRegionComputingOp extends AbstractTilesComputingOp {
    @SourceProduct(alias = "Source product", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    @SourceProduct(alias = "Segmentation source product", description = "The source products to be used for trimming.")
    private Product segmentationSourceProduct;

    @Parameter(label = "Source band indices", description = "The source band indices.")
    private int[] sourceBandIndices;

    private TrimmingRegionTilesComputing trimmingRegionComputingHelper;

    public TrimmingRegionComputingOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.segmentationSourceProduct.getName() + "_trim", this.segmentationSourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);

        this.trimmingRegionComputingHelper = new TrimmingRegionTilesComputing(this.segmentationSourceProduct, this.sourceProduct, this.sourceBandIndices, 0, 0);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();
        this.trimmingRegionComputingHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, tileRowIndex, tileColumnIndex);
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
