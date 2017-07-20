package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
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

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

@OperatorMetadata(
        alias = "ColorFillerOp",
        version="1.0",
        category = "",
        description = "Operaotr that fills the source product band's color with color from the Land Cover Product band",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class ColorFillerOp extends Operator {
    private static final Logger logger = Logger.getLogger(ColorFillerOp.class.getName());

    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product segmentationSourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter (itemAlias = "validRegions", description = "The valid regions with forest pixels")
    private IntSet validRegions;

    private Set<String> processedTiles;
    private ColorFillerTilesComputing colorFillerHelper;

    public ColorFillerOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateInputs();

        createTargetProduct();

        this.processedTiles = new HashSet<String>();
        this.colorFillerHelper = new ColorFillerTilesComputing(segmentationSourceProduct, validRegions, 0, 0);
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
                this.colorFillerHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, 0, 0);
            } catch (Exception ex) {
                throw new OperatorException(ex);
            }
        }
    }

    private void validateInputs() {
        if (this.segmentationSourceProduct.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.segmentationSourceProduct.getName());
            throw new OperatorException(message);
        }
        GeoCoding geo = this.segmentationSourceProduct.getSceneGeoCoding();
        if (geo == null) {
            String message = String.format("Source product '%s' must contain GeoCoding", this.segmentationSourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    private void createTargetProduct() {
        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.segmentationSourceProduct.getName() + "_fill", this.segmentationSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(this.segmentationSourceProduct, this.targetProduct);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    public ProductData getProductData() {
        return this.colorFillerHelper.getProductData();
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ColorFillerOp.class);
        }
    }
}
