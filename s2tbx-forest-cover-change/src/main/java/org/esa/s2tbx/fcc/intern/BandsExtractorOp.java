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
    @SuppressWarnings({"PackageVisibleField"})
    @SourceProduct(alias = "source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "indexes", description = "Specifies the bands in the target product to be maintained.")
    int[] indexes;

    @Override
    public void initialize() throws OperatorException {

        validateInputIndexes();
        this.targetProduct = generateBandsExtractor();
    }

    private void validateInputIndexes() {
        if(this.indexes.length==0){
            throw new OperatorException("Invalid number of indexes given");
        }

        for(int i: this.indexes){
            if(i>this.sourceProduct.getNumBands() || i<0 ){
                throw new OperatorException("Band index can not be computed" + i);
            }
        }
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

        for (int index:this.indexes) {
            Band sourceBand = this.sourceProduct.getBandAt(index);
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
