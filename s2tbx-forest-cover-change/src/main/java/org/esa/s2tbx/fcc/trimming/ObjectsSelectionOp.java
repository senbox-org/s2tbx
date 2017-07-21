package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.utils.AbstractTilesComputingOp;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ObjectsSelectionOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")

public class ObjectsSelectionOp extends AbstractTilesComputingOp {
    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product sourceProduct;

    @SourceProduct(alias = "Land cover product", description = "The land cover product.")
    private Product landCoverProduct;

    private ObjectsSelectionTilesComputing objectsSelectionHelper;

    public ObjectsSelectionOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.sourceProduct.getName() + "_CCI", this.sourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);

        this.objectsSelectionHelper = new ObjectsSelectionTilesComputing(this.sourceProduct, this.landCoverProduct, 0, 0);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();
        this.objectsSelectionHelper.runTile(tileRegion.x, tileRegion.y, tileRegion.width, tileRegion.height, tileRowIndex, tileColumnIndex);
    }

    public Int2ObjectMap<PixelStatistic> getStatistics() {
        return this.objectsSelectionHelper.getStatistics();
    }

    private void validateSourceProduct() {
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if (geo == null) {
            throw new OperatorException("Source product must contain GeoCoding");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ObjectsSelectionOp.class);
        }
    }
}
