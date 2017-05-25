package org.esa.s2tbx.fcc.intern;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "BandsCompositingOp",
        version="1.0",
        category = "",
        description = "Creates a new product containing the concatenation of the bands of the source products",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class BandsCompositingOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(alias = "source", description = "The source products to be used for concatenation.")
    private Product[] sourceProducts;

    @TargetProduct
    private Product targetProduct;

    @Override
    public void initialize() throws OperatorException {
        validateSourceProducts();
        this.targetProduct = generateBandsCompositing();
    }

    private void validateSourceProducts() {
        for(Product product: this.sourceProducts){
            if((product.getSceneRasterHeight()!=this.sourceProducts[0].getSceneRasterHeight())||
                    (product.getSceneRasterWidth()!=this.sourceProducts[0].getSceneRasterWidth())){
                throw new OperatorException("Source products must have the same raster size");
            }
        }
    }

    private Product generateBandsCompositing() {
        Product product = new Product("BandsCompositing", this.sourceProducts[0].getProductType(),
                this.sourceProducts[0].getSceneRasterWidth(), this.sourceProducts[0].getSceneRasterHeight());

        for(int index = 0; index < this.sourceProducts.length; index++) {
            copyBands(this.sourceProducts[index], product, index + 1 + "_");
        }
        return product;
    }

    private void copyBands(Product sourceProduct, Product targetProduct, String prefixTargetBandNames) {
        int bandCount = sourceProduct.getBandGroup().getNodeCount();
        for (int i=0; i<bandCount; i++) {
            Band sourceBand = sourceProduct.getBandAt(i);
            String sourceBandName = sourceBand.getName();
            String targetBandName = prefixTargetBandNames + sourceBandName;
            ProductUtils.copyBand(sourceBandName, sourceProduct, targetBandName, targetProduct, true);

            Band targetBand = targetProduct.getBand(sourceBandName);
            ProductUtils.copyGeoCoding(sourceBand, targetBand);
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(BandsCompositingOp.class);
        }
    }
}
