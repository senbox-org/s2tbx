package org.esa.beam.dataio.s3.synergy;

import org.esa.beam.dataio.netcdf.util.DataTypeUtils;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.jai.ResolutionLevel;
import org.esa.beam.jai.SingleBandedOpImage;
import org.esa.beam.util.ImageUtils;
import ucar.nc2.Variable;

import javax.media.jai.PlanarImage;
import java.awt.Rectangle;
import java.awt.image.WritableRaster;
import java.io.IOException;

@Deprecated
public class SynTiePointImage extends SingleBandedOpImage {

    private final Variable variable;
    private final PixelPos[] pixelPositions;

    protected SynTiePointImage(Variable variable, int dataBufferType, Product masterProduct, ResolutionLevel level,
                               GeoCoding geoCoding, float[] latValues, float[] lonValues) {
        super(dataBufferType, masterProduct.getSceneRasterWidth(),
              masterProduct.getSceneRasterHeight(), masterProduct.getPreferredTileSize(), null, level);
        this.variable = variable;
        pixelPositions = new PixelPos[latValues.length];
        for (int i = 0; i < latValues.length; i++) {
            pixelPositions[i] = geoCoding.getPixelPos(new GeoPos(latValues[i], lonValues[i]), null);
        }
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        ProductData productData;
        boolean directMode = dest.getDataBuffer().getSize() == destRect.width * destRect.height;
        if (directMode) {
            productData = ProductData.createInstance(DataTypeUtils.getRasterDataType(variable),
                                                     ImageUtils.getPrimitiveArray(dest.getDataBuffer()));
        } else {
            productData = ProductData.createInstance(DataTypeUtils.getRasterDataType(variable),
                                                     destRect.width * destRect.height);
        }
        try {
            computeProductData(productData, destRect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!directMode) {
            dest.setDataElements(destRect.x, destRect.y, destRect.width, destRect.height, productData.getElems());
        }
    }

    private void computeProductData(ProductData productData, Rectangle destRect) throws IOException {
        final Object elems = productData.getElems();
        if (elems instanceof double[]) {
            double[] productDataElems = (double[]) elems;
            for (PixelPos pixelPosition : pixelPositions) {
                if (destRect.contains(pixelPosition)) {
                    final int productDataIndex = (int) ((pixelPosition.getY() - destRect.getY()) * destRect.getWidth() +
                            (pixelPosition.getX() - destRect.getX()));
                    productDataElems[productDataIndex] = 2;
                }
            }
        } else {
            float[] productDataElems = (float[]) elems;
            for (PixelPos pixelPosition : pixelPositions) {
                if (destRect.contains(pixelPosition)) {
                    final int productDataIndex = (int) ((pixelPosition.getY() - destRect.getY()) * destRect.getWidth() +
                            (pixelPosition.getX() - destRect.getX()));
                    productDataElems[productDataIndex] = 2;
                }
            }
        }
//        for (int y = (int) destRect.getY(); y < destRect.getHeight(); y++) {
//            for (int x = (int) destRect.getX(); x < destRect.getWidth(); x++) {
//                PixelPos pixelPos = new PixelPos(x, y);
//                geoCoding.getGeoPos(pixelPos, null);
//            }
//        }
    }

//    @Override
//    protected void computeProductData(ProductData productData, Rectangle destRect) throws IOException {
//        if (getLevel() == 0) {
//            getTiePointGrid().readPixels(destRect.x, destRect.y,
//                                         destRect.width, destRect.height,
//                                         (float[]) productData.getElems(),
//                                         ProgressMonitor.NULL);
//        } else {
//            final int sourceWidth = getSourceWidth(destRect.width);
//            ProductData lineData = ProductData.createInstance(getTiePointGrid().getDataType(), sourceWidth);
//            int[] sourceCoords = getSourceCoords(sourceWidth, destRect.width);
//            for (int y = 0; y < destRect.height; y++) {
//                getTiePointGrid().readPixels(getSourceX(destRect.x),
//                                             getSourceY(destRect.y + y),
//                                             sourceWidth, 1,
//                                             (float[]) lineData.getElems(),
//                                             ProgressMonitor.NULL);
//                copyLine(y, destRect.width, lineData, productData, sourceCoords);
//            }
//        }
//    }

}
