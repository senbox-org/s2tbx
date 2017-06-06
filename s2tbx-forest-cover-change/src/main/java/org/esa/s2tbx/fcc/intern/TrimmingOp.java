package org.esa.s2tbx.fcc.intern;

import java.util.Map;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "TrimmingOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class TrimmingOp extends Operator{

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(alias = "source", description = "The source products to be used for trimming.")
    private Product sourceProduct;

    @SourceProducts(alias = "source composition product ", description = "The source products to be used for trimming.")
    private Product sourceCompositionProduct;

    @TargetProduct
    private Product targetProduct;

    private Map<Integer, PixelsSourceBands> statstics;

    @Override
    public void initialize() throws OperatorException {

    }

    public Map<Integer, PixelsSourceBands> getStatistics(){
        return this.statstics;
    }
    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingOp.class);
        }
    }
}
