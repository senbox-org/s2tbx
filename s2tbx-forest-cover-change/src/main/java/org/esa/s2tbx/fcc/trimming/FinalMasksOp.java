package org.esa.s2tbx.fcc.trimming;

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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jean Coravu
 */
@OperatorMetadata(
        alias = "FinalMasksOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class FinalMasksOp extends AbstractTilesComputingOp {
    @SourceProduct(alias = "differenceSegmentationProduct", description = "The source product")
    private Product differenceSegmentationProduct;

    @SourceProduct(alias = "unionMaskProduct", description = "The source product")
    private Product unionMaskProduct;

    @Parameter(itemAlias = "differenceTrimmingSet", description = "")
    private IntSet differenceTrimmingSet;

    private FinalMasksTilesComputing finalMasksHelper;

    public FinalMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.differenceSegmentationProduct.getSceneRasterWidth();
        int sceneHeight = this.differenceSegmentationProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.differenceSegmentationProduct.getName() + "_final", this.differenceSegmentationProduct.getProductType(), "band_1", ProductData.TYPE_INT32);

        this.finalMasksHelper = new FinalMasksTilesComputing(differenceSegmentationProduct, unionMaskProduct, differenceTrimmingSet, 0, 0);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();
        this.finalMasksHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, tileRowIndex, tileColumnIndex);
    }

    public ProductData getProductData() {
        return this.finalMasksHelper.getProductData();
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(FinalMasksOp.class);
        }
    }
}
