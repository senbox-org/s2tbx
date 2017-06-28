package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
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
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter (itemAlias = "percentagePixels", description = "The percentage of valid forrest pixels in a region")
    private float percentagePixels;

    private IntSet validRegions;

    public ColorFillerOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        validateInputs();

        Map<String, Object> parameters = new HashMap<>();
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", this.sourceProduct);
        ObjectsSelectionOp objSelOp = (ObjectsSelectionOp) GPF.getDefaultInstance().createOperator("ObjectsSelectionOp", parameters, sourceProducts, null);
        objSelOp.getTargetProduct();

        OperatorExecutor executor = OperatorExecutor.create(objSelOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics = objSelOp.getStatistics();
        this.validRegions = computeValidStatisticRegions(statistics, this.percentagePixels);

        createTargetProduct();
    }

    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle tileRegion = targetTile.getRectangle();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute color filler for tile region: bounds [x=" + tileRegion.x+", y="+tileRegion.y+", width="+tileRegion.width+", height="+tileRegion.height+"]");
        }

        Band segmentationBand = sourceProduct.getBandAt(0);
        for (int y = tileRegion.y; y < tileRegion.y + tileRegion.height; y++) {
            for (int x = tileRegion.x; x < tileRegion.x + tileRegion.width; x++) {
                int sgmentationValue = segmentationBand.getSampleInt(x, y);
                if (!this.validRegions.contains(sgmentationValue)) {
                    sgmentationValue = ForestCoverChangeConstans.NO_DATA_VALUE;
                }
                targetTile.setSample(x, y, sgmentationValue);
            }
        }
    }

    private static IntSet computeValidStatisticRegions(Int2ObjectMap<ObjectsSelectionOp.PixelStatistic> statistics, float percentagePixels) {
        IntSet validReg = new IntOpenHashSet();
        ObjectIterator<Int2ObjectMap.Entry<ObjectsSelectionOp.PixelStatistic>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<ObjectsSelectionOp.PixelStatistic> entry = it.next();
            ObjectsSelectionOp.PixelStatistic value = entry.getValue();
            float percent = ((float)value.getPixelsInRange()/(float)value.getTotalNumberPixels()) * 100;
            if (percent >= percentagePixels) {
                validReg.add(entry.getIntKey());
            }
        }
        return validReg;
    }

    private void validateInputs() {
        if (this.sourceProduct.isMultiSize()) {
            String message = String.format("Source product '%s' contains rasters of different sizes and can not be processed.\n" +
                            "Please consider resampling it so that all rasters have the same size.",
                    this.sourceProduct.getName());
            throw new OperatorException(message);
        }
        GeoCoding geo = this.sourceProduct.getSceneGeoCoding();
        if (geo == null) {
            String message = String.format("Source product '%s' must contain GeoCoding", this.sourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    private void createTargetProduct() {
        int sceneWidth = this.sourceProduct.getSceneRasterWidth();
        int sceneHeight = this.sourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();

        this.targetProduct = new Product(this.sourceProduct.getName() + "_fill", this.sourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(this.sourceProduct, this.targetProduct);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ColorFillerOp.class);
        }
    }
}
