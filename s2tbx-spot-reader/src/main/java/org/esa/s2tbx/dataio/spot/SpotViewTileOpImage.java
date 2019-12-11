package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.IOException;
/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewTileOpImage extends AbstractSubsetTileOpImage {

    private final SpotViewImageReader spotViewImageReader;
    private final int productDataType;

    public SpotViewTileOpImage(SpotViewImageReader spotViewImageReader, MultiLevelModel imageMultiLevelModel, int dataBufferType, int bandIndex,
                               Rectangle imageBounds, Dimension tileSize, Point tileOffset, int level) {

        super(imageMultiLevelModel, dataBufferType, bandIndex, imageBounds, tileSize, tileOffset, level);

        this.spotViewImageReader = spotViewImageReader;
        this.productDataType = ImageManager.getProductDataType(dataBufferType);
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster destinationRaster, Rectangle destinationRectangle) {
        try {
            Rectangle tileBoundsIntersection = computeIntersection(destinationRectangle);
            if (!tileBoundsIntersection.isEmpty()) {
                ProductData tileData = readRasterData(tileBoundsIntersection.x, tileBoundsIntersection.y, tileBoundsIntersection.width, tileBoundsIntersection.height);

                ProductData destData;
                boolean directMode = (destinationRaster.getDataBuffer().getSize() == destinationRectangle.width * destinationRectangle.height);
                if (directMode) {
                    destData = ProductData.createInstance(this.productDataType, ImageUtils.getPrimitiveArray(destinationRaster.getDataBuffer()));
                } else {
                    destData = ProductData.createInstance(this.productDataType, destinationRectangle.width * destinationRectangle.height);
                }
                for (int y = 0; y < destinationRectangle.height; y++) {
                    int currentSrcYOffset = this.imageOffset.y + this.levelImageSupport.getSourceY(this.levelTileOffset.y + destinationRectangle.y + y);
                    int currentDestYOffset = y * destinationRectangle.width;
                    for (int x = 0; x < destinationRectangle.width; x++) {
                        int currentSrcXOffset = this.imageOffset.x + this.levelImageSupport.getSourceX(this.levelTileOffset.x + destinationRectangle.x + x);
                        double value = getSourceValue(tileBoundsIntersection, tileData, currentSrcXOffset, currentSrcYOffset);
                        destData.setElemDoubleAt(currentDestYOffset + x, value);
                    }
                }
                if (!directMode) {
                    destinationRaster.setDataElements(destinationRectangle.x, destinationRectangle.y, destinationRectangle.width, destinationRectangle.height, destData.getElems());
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read the data for level " + getLevel()+" and rectangle " + destinationRectangle + ".", ex);
        }
    }

    private static double getSourceValue(Rectangle tileRect, ProductData tileData, int sourceX, int sourceY) {
        int currentX = sourceX - tileRect.x;
        int currentY = sourceY - tileRect.y;
        return tileData.getElemDoubleAt(currentY * tileRect.width + currentX);
    }

    private ProductData readRasterData(int destOffsetX, int destOffsetY, int destWidth, int destHeight) throws IOException {
        int sourceStepX = 1;
        int sourceStepY = 1;
        int sourceOffsetX = sourceStepX * destOffsetX;
        int sourceOffsetY = sourceStepY * destOffsetY;
        int sourceWidth = sourceStepX * (destWidth - 1) + 1;
        int sourceHeight = sourceStepY * (destHeight - 1) + 1;
        ProductData tileData = ProductData.createInstance(this.productDataType, destWidth * destHeight);
        synchronized (this.spotViewImageReader) {
            this.spotViewImageReader.readBandRasterData(this.bandIndex, 4, sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destOffsetX, destOffsetY, destWidth, destHeight, tileData);
        }
        return tileData;
    }
}
