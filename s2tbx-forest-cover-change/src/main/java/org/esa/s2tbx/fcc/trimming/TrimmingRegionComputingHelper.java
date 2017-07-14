package org.esa.s2tbx.fcc.trimming;

import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class TrimmingRegionComputingHelper extends AbstractRegionParallelComputing {
    private static final Logger logger = Logger.getLogger(TrimmingRegionComputingHelper.class.getName());

    private final Product segmentationSourceProduct;
    private final Product sourceProduct;
    private final int[] sourceBandIndices;

    TrimmingRegionComputingHelper(Product segmentationSourceProduct, Product sourceProduct, int[] sourceBandIndices, int tileWidth, int tileHeight) {
        super(segmentationSourceProduct.getSceneRasterWidth(), segmentationSourceProduct.getSceneRasterHeight(), tileWidth, tileHeight);

        this.segmentationSourceProduct = segmentationSourceProduct;
        this.sourceProduct = sourceProduct;
        this.sourceBandIndices = sourceBandIndices;
    }

    @Override
    protected void runTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Trimming statistics for tile region: row index: "+ localRowIndex+", column index: "+localColumnIndex+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        Band firstBand = this.sourceProduct.getBandAt(this.sourceBandIndices[0]);
        Band secondBand = this.sourceProduct.getBandAt(this.sourceBandIndices[1]);
        Band thirdBand = this.sourceProduct.getBandAt(this.sourceBandIndices[2]);

        Band segmentationBand = this.segmentationSourceProduct.getBandAt(0);

        int tileBottomY = tileTopY + tileHeight;
        int tileRightX = tileLeftX + tileWidth;
        for (int y = tileTopY; y < tileBottomY; y++) {
            for (int x = tileLeftX; x < tileRightX; x++) {
                int segmentationPixelValue = segmentationBand.getSampleInt(x, y);
                if (segmentationPixelValue != ForestCoverChangeConstants.NO_DATA_VALUE) {
                    float a = firstBand.getSampleFloat(x, y);
                    float b = secondBand.getSampleFloat(x, y);
                    float c = thirdBand.getSampleFloat(x, y);

                    addPixelValuesBands(segmentationPixelValue, a, b, c);
                }
            }
        }
    }
}
