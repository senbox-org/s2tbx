package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
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
import org.esa.snap.utils.AbstractTilesComputingOp;

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

public class ColorFillerOp extends AbstractTilesComputingOp {
    private static final Logger logger = Logger.getLogger(ColorFillerOp.class.getName());

    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product segmentationSourceProduct;

    @Parameter (itemAlias = "validRegions", description = "The valid regions with forest pixels")
    private IntSet validRegions;

    public ColorFillerOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateInputs();

        int sceneWidth = this.segmentationSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.segmentationSourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.segmentationSourceProduct.getName() + "_fill", this.segmentationSourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);
        ProductUtils.copyGeoCoding(this.segmentationSourceProduct, this.targetProduct);
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Color filler for tile region: row index: "+ tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);
        int tileBottomY = tileRegion.y + tileRegion.height;
        int tileRightX = tileRegion.x + tileRegion.width;
        for (int y = tileRegion.y; y < tileBottomY; y++) {
            for (int x = tileRegion.x; x < tileRightX; x++) {
                int segmentationValue = segmentationBand.getSampleInt(x, y);
                if (!this.validRegions.contains(segmentationValue)) {
                    segmentationValue = ForestCoverChangeConstants.NO_DATA_VALUE;
                }
                targetTile.setSample(x, y, segmentationValue);
            }
            checkForCancellation();
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

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ColorFillerOp.class);
        }
    }
}
