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
import org.esa.snap.core.util.ProductUtils;
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
        alias = "UnionMasksOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class UnionMasksOp extends AbstractTilesComputingOp {
    @SourceProduct(alias = "source", description = "The first source product")
    private Product currentSegmentationSourceProduct;
    @SourceProduct(alias = "source", description = "The second source product")
    private Product previousSegmentationSourceProduct;

    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet currentSegmentationTrimmingRegionKeys;
    @Parameter(itemAlias = "segmentationRegionKeys", description = "Specifies the segmentation keys after trimming.")
    private IntSet previousSegmentationTrimmingRegionKeys;

    private UnionMasksTilesComputing unionMasksHelper;
    private Set<String> processedTiles;

    public UnionMasksOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int sceneWidth = this.currentSegmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSegmentationSourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.currentSegmentationSourceProduct.getName() + "_union", this.currentSegmentationSourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);

        this.processedTiles = new HashSet<String>();
        this.unionMasksHelper = new UnionMasksTilesComputing(currentSegmentationSourceProduct, previousSegmentationSourceProduct,
                                                             this.currentSegmentationTrimmingRegionKeys, previousSegmentationTrimmingRegionKeys, 0, 0);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();

        String key = tileRegion.x+"|"+tileRegion.y+"|"+tileRegion.width+"|"+tileRegion.height;
        boolean canProcessTile = false;
        synchronized (this.processedTiles) {
            canProcessTile = this.processedTiles.add(key);
        }
        if (canProcessTile) {
            try {
                this.unionMasksHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, tileRowIndex, tileColumnIndex);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        }
    }

    public ProductData getProductData() {
        return this.unionMasksHelper.getProductData();
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(UnionMasksOp.class);
        }
    }
}
