package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.image.ImageReadBoundsSupport;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.IOException;
/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewTileOpImage extends AbstractSubsetTileOpImage {

    private final SpotViewImageReader spotViewImageReader;
    private final int bandIndex;
    private final int bandCount;

    public SpotViewTileOpImage(SpotViewImageReader spotViewImageReader, int dataBufferType, int bandIndex, int bandCount,
                               int tileWidth, int tileHeight, int tileOffsetFromReadBoundsX, int tileOffsetFromReadBoundsY, ImageReadBoundsSupport imageBoundsSupport) {

        super(dataBufferType, tileWidth, tileHeight, tileOffsetFromReadBoundsX, tileOffsetFromReadBoundsY, imageBoundsSupport);

        this.spotViewImageReader = spotViewImageReader;
        this.bandIndex = bandIndex;
        this.bandCount = bandCount;
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster levelDestinationRaster, Rectangle levelDestinationRectangle) {
        Rectangle normalBoundsIntersection = computeIntersectionOnNormalBounds(levelDestinationRectangle);
        if (!normalBoundsIntersection.isEmpty()) {
            ProductData normalBoundsIntersectionData;
            try {
                normalBoundsIntersectionData = readRasterData(normalBoundsIntersection);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to read the data for level " + getLevel() + " and rectangle " + levelDestinationRectangle + ".", ex);
            }
            writeDataOnLevelRaster(normalBoundsIntersection, normalBoundsIntersectionData, levelDestinationRaster, levelDestinationRectangle);
        }
    }

    private ProductData readRasterData(Rectangle normalBounds) throws IOException {
        int sourceStepX = 1;
        int sourceStepY = 1;
        int sourceOffsetX = sourceStepX * normalBounds.x;
        int sourceOffsetY = sourceStepY * normalBounds.y;
        int sourceWidth = sourceStepX * (normalBounds.width - 1) + 1;
        int sourceHeight = sourceStepY * (normalBounds.height - 1) + 1;
        ProductData tileData = ProductData.createInstance(getProductDataType(), normalBounds.width * normalBounds.height);
        synchronized (this.spotViewImageReader) {
            this.spotViewImageReader.readBandRasterData(this.bandIndex, this.bandCount, sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY,
                    normalBounds.x, normalBounds.y, normalBounds.width, normalBounds.height, tileData);
        }
        return tileData;
    }
}
