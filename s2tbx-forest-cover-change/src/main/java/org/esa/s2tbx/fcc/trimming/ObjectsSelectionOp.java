package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
    private static final Logger logger = Logger.getLogger(ObjectsSelectionOp.class.getName());

    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product sourceProduct;

    @SourceProduct(alias = "Land cover product", description = "The land cover product.")
    private Product landCoverProduct;

    private Int2ObjectMap<PixelStatistic> statistics;

    public ObjectsSelectionOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();

        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        initTargetProduct(sceneWidth, sceneHeight, this.sourceProduct.getName() + "_CCI", this.sourceProduct.getProductType(), "band_1", ProductData.TYPE_INT32);

        this.statistics = new Int2ObjectLinkedOpenHashMap<PixelStatistic>();
    }

    @Override
    protected void processTile(Band targetBand, Tile targetTile, ProgressMonitor pm, int tileRowIndex, int tileColumnIndex) throws Exception {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Object selection for tile region: row index: "+ tileRowIndex+", column index: "+tileColumnIndex+", bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band segmentationBand = this.sourceProduct.getBandAt(0);
        Band landCoverBand = this.landCoverProduct.getBandAt(0);
        int tileBottomY = tileRegion.y + tileRegion.height;
        int tileRightX = tileRegion.x + tileRegion.width;
        for (int y = tileRegion.y; y < tileBottomY; y++) {
            for (int x = tileRegion.x; x < tileRightX; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                int landCoverPixelValue = landCoverBand.getSampleInt(x, y);
                synchronized (this.statistics) {
                    PixelStatistic pixel = this.statistics.get(segmentationPixelValue);
                    if (pixel == null) {
                        pixel = new PixelStatistic(0, 0);
                        this.statistics.put(segmentationPixelValue, pixel);
                    }
                    pixel.incrementTotalNumberPixels();
                    for (int index : ForestCoverChangeConstants.COVER_LABElS) {
                        if (index == landCoverPixelValue) {
                            pixel.incrementPixelsInRange();
                        }
                    }
                }
            }
            checkForCancellation();
        }
    }

    public Int2ObjectMap<PixelStatistic> getStatistics() {
        return this.statistics;
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
