package org.esa.s2tbx.fcc.intern;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
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
        alias = "BandsExtractorOp",
        version="1.0",
        category = "",
        description = "Creates a new product out of the source product containing only the indexes bands given",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class BandsExtractorOp extends Operator {
    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        this.targetProduct = generateBandsExtractor();
    }

    private Product generateBandsExtractor() {
        Product product = new Product(this.sourceProduct.getName(), this.sourceProduct.getProductType(),
                                      this.sourceProduct.getSceneRasterWidth(), this.sourceProduct.getSceneRasterHeight());
        product.setStartTime(this.sourceProduct.getStartTime());
        product.setEndTime(this.sourceProduct.getEndTime());
        product.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(this.sourceProduct, product);
        ProductUtils.copyGeoCoding(this.sourceProduct, product);
        ProductUtils.copyTiePointGrids(this.sourceProduct, product);
        ProductUtils.copyVectorData(this.sourceProduct, product);

        for (int i=0; i<this.sourceBandNames.length; i++) {
            Band sourceBand = this.sourceProduct.getBand(this.sourceBandNames[i]);
            String sourceBandName = sourceBand.getName();
            String targetBandName = sourceBandName;
            ProductUtils.copyBand(sourceBandName, this.sourceProduct, targetBandName, product, true);

            Band targetBand = product.getBand(targetBandName);
            ProductUtils.copyGeoCoding(sourceBand, targetBand);
        }

        return product;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(BandsExtractorOp.class);
        }
    }
}
