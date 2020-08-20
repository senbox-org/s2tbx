package org.esa.s2tbx.dataio.rapideye;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.image.AbstractSubsetTileOpImage;
import org.esa.snap.core.image.ImageReadBoundsSupport;

import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.IOException;

/**
 * Created by jcoravu on 23/12/2019.
 */
public class RapidEyeL1TileOpImage extends AbstractSubsetTileOpImage {

    private final NITFReaderWrapper nitfReader;

    public RapidEyeL1TileOpImage(NITFReaderWrapper nitfReader, int dataBufferType, int tileWidth, int tileHeight, int tileOffsetFromReadBoundsX,
                                 int tileOffsetFromReadBoundsY, ImageReadBoundsSupport imageBoundsSupport, Dimension defaultJAIReadTileSize) {

        super(dataBufferType, tileWidth, tileHeight, tileOffsetFromReadBoundsX, tileOffsetFromReadBoundsY, imageBoundsSupport, defaultJAIReadTileSize);

        this.nitfReader = nitfReader;
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
        synchronized (this.nitfReader) {
            this.nitfReader.readBandData(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, tileData, ProgressMonitor.NULL);
        }
        return tileData;
    }
}
