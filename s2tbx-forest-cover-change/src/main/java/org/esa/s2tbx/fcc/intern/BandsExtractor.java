package org.esa.s2tbx.fcc.intern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class BandsExtractor {
    private Product sourceProduct;
    private Product targetProduct;
    public BandsExtractor(Product sourceProduct){
        this.sourceProduct = sourceProduct;
        generateTargetProduct();
    }
    private void generateTargetProduct(){
        this.targetProduct = new Product(this.sourceProduct.getName(),
                this.sourceProduct.getProductType(),
                this.sourceProduct.getSceneRasterWidth(),
                this.sourceProduct.getSceneRasterHeight());
        this.targetProduct.setStartTime(this.sourceProduct.getStartTime());
        this.targetProduct.setEndTime(this.sourceProduct.getEndTime());
        this.targetProduct.setNumResolutionsMax(this.sourceProduct.getNumResolutionsMax());
        ProductUtils.copyMetadata(this.sourceProduct, this.targetProduct);
        ProductUtils.copyGeoCoding(this.sourceProduct, this.targetProduct);
        ProductUtils.copyTiePointGrids(this.sourceProduct, this.targetProduct);
        ProductUtils.copyVectorData(this.sourceProduct, this.targetProduct);
        int[] indexes = new int[]{2,3,7,11};
        for(int index:indexes){
            ProductUtils.copyBand(this.sourceProduct.getBandAt(index).getName(), this.sourceProduct,  this.targetProduct, true);
            ProductUtils.copyGeoCoding(this.sourceProduct.getBandAt(index),  this.targetProduct.getBand(this.sourceProduct.getBandAt(index).getName()));
        }
        this.targetProduct = resample(this.targetProduct, this.targetProduct.getSceneRasterWidth(), this.targetProduct.getSceneRasterHeight());
        this.targetProduct.setName(this.sourceProduct.getName());
    }

    private Product resample(Product source, int targetWidth, int targetHeight) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", targetWidth);
        parameters.put("targetHeight", targetHeight);
        return GPF.createProduct("Resample", parameters, source);
    }

    public Product getTargetProduct(){

        return this.targetProduct;
    }
}
