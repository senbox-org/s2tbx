package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.snap.core.datamodel.Product;

/**
 * Created by jcoravu on 9/19/17.
 */
public class SegmentationSourceProductPair {
    private final Product sourceProduct;
    private final String[] sourceBandNames;

    public SegmentationSourceProductPair(Product sourceProduct, String[] sourceBandNames) {
        this.sourceProduct = sourceProduct;
        this.sourceBandNames = sourceBandNames;
    }

    public Product getSourceProduct() {
        return sourceProduct;
    }

    public String[] getSourceBandNames() {
        return sourceBandNames;
    }
}
