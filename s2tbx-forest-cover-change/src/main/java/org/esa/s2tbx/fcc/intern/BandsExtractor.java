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

    public static Product generateTargetProduct(Product sourceProduct, int[] indexes){
        Product targetProduct = new Product(sourceProduct.getName(),
                sourceProduct.getProductType(),
                sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        targetProduct.setStartTime(sourceProduct.getStartTime());
        targetProduct.setEndTime(sourceProduct.getEndTime());
        targetProduct.setNumResolutionsMax(sourceProduct.getNumResolutionsMax());
        ProductUtils.copyMetadata(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyVectorData(sourceProduct, targetProduct);
        for(int index:indexes){
            ProductUtils.copyBand(sourceProduct.getBandAt(index).getName(), sourceProduct,  targetProduct, true);
            ProductUtils.copyGeoCoding(sourceProduct.getBandAt(index),  targetProduct.getBand(sourceProduct.getBandAt(index).getName()));
        }
        targetProduct = resample(targetProduct, targetProduct.getSceneRasterWidth(),targetProduct.getSceneRasterHeight());
        targetProduct.setName(sourceProduct.getName());
        return targetProduct;
    }

    private static Product resample(Product source, int targetWidth, int targetHeight) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", targetWidth);
        parameters.put("targetHeight", targetHeight);
        return GPF.createProduct("Resample", parameters, source);
    }
}
