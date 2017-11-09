package org.esa.s2tbx.fcc.common;

import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jean Coravu
 */
public class WriteCombinedMasksTilesComputing extends AbstractWriteMasksTilesComputing {

    private final String[] maskNamesToCombine;
    private final Product product;

    public WriteCombinedMasksTilesComputing(Product product, String[] maskNamesToCombine, int tileWidth, int tileHeight, Path temporaryParentFolder) throws IOException {
        super(product.getSceneRasterWidth(), product.getSceneRasterHeight(), tileWidth, tileHeight, temporaryParentFolder);

        this.product = product;
        this.maskNamesToCombine = maskNamesToCombine;
    }

    @Override
    protected boolean isValidMaskPixel(int x, int y) {
        boolean isValidMaskPixel = false;
        for (int k=0; k<this.maskNamesToCombine.length && !isValidMaskPixel; k++) {
            Mask sourceMask = this.product.getMaskGroup().get(this.maskNamesToCombine[k]);
            if (sourceMask.getSampleInt(x, y) != ForestCoverChangeConstants.NO_DATA_VALUE) {
                isValidMaskPixel = true;
            }
        }

        return isValidMaskPixel;
    }
}