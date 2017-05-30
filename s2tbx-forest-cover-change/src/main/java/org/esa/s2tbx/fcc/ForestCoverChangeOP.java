package org.esa.s2tbx.fcc;

import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ForestCoverChangeOp",
        version="1.0",
        category = "Raster",
        description = "Generates Forest Cover Change product from L2a Sentinel 2 products ",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class ForestCoverChangeOp extends Operator{

    @SuppressWarnings({"PackageVisibleField"})

    @SourceProduct(alias = "Source Product TM", description = "The source product to be modified.")
    private Product sourceProductTM;
    @SourceProduct(alias = "Source Product ETM", description = "The source product to be modified.")
    private Product sourceProductETM;

    @TargetProduct
    private Product targetProduct;

    @Parameter(defaultValue = "95.0", itemAlias = "percentage", description = "Specifies the percentage of forest cover per segment")
    private float percentage;

    @Override
    public void initialize() throws OperatorException {

    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
