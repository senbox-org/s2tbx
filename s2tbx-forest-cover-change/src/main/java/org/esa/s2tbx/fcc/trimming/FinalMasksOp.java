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
public class FinalMasksOp extends Operator {
    @SourceProduct(alias = "differenceSegmentationProduct", description = "The source product")
    private Product differenceSegmentationProduct;

    @SourceProduct(alias = "unionMaskProduct", description = "The source product")
    private Product unionMaskProduct;

    @Parameter(itemAlias = "differenceTrimmingSet", description = "")
    private IntSet differenceTrimmingSet;

    @TargetProduct
    private Product targetProduct;

    private FinalMasksHelper finalMasksHelper;
    private Set<String> processedTiles;

    public FinalMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.differenceSegmentationProduct.getSceneRasterWidth();
        int sceneHeight = this.differenceSegmentationProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.differenceSegmentationProduct.getName() + "_union", this.differenceSegmentationProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        this.processedTiles = new HashSet<String>();
        this.finalMasksHelper = new FinalMasksHelper(differenceSegmentationProduct, unionMaskProduct, differenceTrimmingSet, 0, 0);
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
                this.finalMasksHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, 0, 0);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        }
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
