package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
@OperatorMetadata(
        alias = "TrimmingRegionComputingOp",
        version="1.0",
        category = "",
        description = "Creates a hash map containing the values from the source bands for a respective segmentation region",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class TrimmingRegionComputingOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(alias = "source", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    @SourceProducts(alias = "source composition product", description = "The source products to be used for trimming.")
    private Product sourceCompositionProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "bandsUsed", description = "the index from the sourceCompositionProduct to be used")
    int[] bandsUsed;

    private Map<Integer, List<PixelsSourceBands>> statistics;

    @Override
    public void initialize() throws OperatorException {
    }
    @Override
    public void computeTile(Band targetBand, Tile targetTile, ProgressMonitor pm) throws OperatorException {
        Rectangle region = targetTile.getRectangle();

    }

    /**
     *
     * @return returns the HashMap containing the pixels values from the 4 bands selected per region
     */
    public Map<Integer, List<PixelsSourceBands>> getPixelsStatistics(){
        return this.statistics;
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingRegionComputingOp.class);
        }
    }
}
