package org.esa.s2tbx.dataio.ikonos.internal;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.readers.TileLayout;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.LevelImageSupport;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.esa.snap.core.util.ImageUtils;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 28/10/2019.
 */
class IkonosTileOpImage extends SingleBandedOpImage {

    private static final Logger logger = Logger.getLogger(IkonosTileOpImage.class.getName());

    private final int productDataType;
    private final GeoTiffImageReader geoTiffImageReader;
    private final LevelImageSupport levelImageSupport;
    private final Point tileOffset;
    private final Point productSubsetOffset;

    private IkonosTileOpImage(GeoTiffImageReader geoTiffImageReader, Point productSubsetOffset, Point tileOffset,
                              TileLayout tileLayout, MultiLevelModel imageMultiLevelModel, int dataType, int level) {

        this(geoTiffImageReader, productSubsetOffset, tileOffset, tileLayout, dataType, getTileDimensionAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, level),
                ResolutionLevel.create(imageMultiLevelModel, level));
    }

    private IkonosTileOpImage(GeoTiffImageReader geoTiffImageReader, Point productSubsetOffset, Point tileOffset,
                              TileLayout tileLayout, int dataType, Dimension tileSize, ResolutionLevel resolutionLevel) {

        super(dataType, null, tileLayout.tileWidth, tileLayout.tileHeight, tileSize, null, resolutionLevel);

        this.geoTiffImageReader = geoTiffImageReader;
        this.productSubsetOffset = productSubsetOffset;
        this.tileOffset = tileOffset;

        this.levelImageSupport = new LevelImageSupport(tileLayout.width, tileLayout.height, resolutionLevel);
        this.productDataType = ImageManager.getProductDataType(dataType);;
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster destinationRaster, Rectangle destinationRectangle) {
        try {
            int sourceImageWidth = getSourceImageWidth();
            int sourceImageHeight = getSourceImageHeight();
            Rectangle sourceImageBounds = new Rectangle(this.productSubsetOffset.x, this.productSubsetOffset.y, sourceImageWidth, sourceImageHeight);

            int destinationSourceWidth = this.levelImageSupport.getSourceWidth(destinationRectangle.width);
            int destinationSourceHeight = this.levelImageSupport.getSourceHeight(destinationRectangle.height);
            int destinationSourceX = this.levelImageSupport.getSourceX(this.tileOffset.x + destinationRectangle.x);
            int destinationSourceY = this.levelImageSupport.getSourceY(this.tileOffset.y + destinationRectangle.y);
            if (destinationSourceX + destinationSourceWidth > sourceImageWidth) {
                destinationSourceWidth = sourceImageWidth - destinationSourceX;
            }
            if (destinationSourceY + destinationSourceHeight > sourceImageHeight) {
                destinationSourceHeight = sourceImageHeight - destinationSourceY;
            }
            destinationSourceX += this.productSubsetOffset.x;
            destinationSourceY += this.productSubsetOffset.y;

            Rectangle tileBoundsInSourceImage = new Rectangle(destinationSourceX, destinationSourceY, destinationSourceWidth, destinationSourceHeight);
            Rectangle tileBoundsIntersection = sourceImageBounds.intersection(tileBoundsInSourceImage);
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
                    int currentSrcYOffset = this.productSubsetOffset.y + this.levelImageSupport.getSourceY(this.tileOffset.y + destinationRectangle.y + y);
                    int currentDestYOffset = y * destinationRectangle.width;
                    for (int x = 0; x < destinationRectangle.width; x++) {
                        int currentSrcXOffset = this.productSubsetOffset.x + this.levelImageSupport.getSourceX(this.tileOffset.x + destinationRectangle.x + x);
                        double value = getSourceValue(tileBoundsIntersection, tileData, currentSrcXOffset, currentSrcYOffset);
                        destData.setElemDoubleAt(currentDestYOffset + x, value);
                    }
                }
                if (!directMode) {
                    destinationRaster.setDataElements(destinationRectangle.x, destinationRectangle.y, destinationRectangle.width, destinationRectangle.height, destData.getElems());
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private int getSourceImageWidth() {
        return this.levelImageSupport.getSourceWidth();
    }

    private int getSourceImageHeight() {
        return this.levelImageSupport.getSourceHeight();
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

        Raster rasterData = this.geoTiffImageReader.readRect(sourceOffsetX, sourceOffsetY, sourceStepX, sourceStepY, destOffsetX, destOffsetY, destWidth, destHeight);

        ProductData tileData = ProductData.createInstance(this.productDataType, destWidth * destHeight);

        int bandIndex = 0;
        DataBuffer dataBuffer = rasterData.getDataBuffer();
        SampleModel sampleModel = rasterData.getSampleModel();
        int rasterDataBufferType = dataBuffer.getDataType();

        boolean isInteger = (rasterDataBufferType == DataBuffer.TYPE_SHORT || rasterDataBufferType == DataBuffer.TYPE_USHORT || rasterDataBufferType == DataBuffer.TYPE_INT);
        boolean isIntegerTarget = (tileData.getElems() instanceof int[]);
        if (isInteger && isIntegerTarget) {
            sampleModel.getSamples(0, 0, rasterData.getWidth(), rasterData.getHeight(), bandIndex, (int[]) tileData.getElems(), dataBuffer);
        } else if (rasterDataBufferType == DataBuffer.TYPE_FLOAT && tileData.getElems() instanceof float[]) {
            sampleModel.getSamples(0, 0, rasterData.getWidth(), rasterData.getHeight(), bandIndex, (float[]) tileData.getElems(), dataBuffer);
        } else {
            double[] doubleArray = new double[destWidth * destHeight];
            sampleModel.getSamples(0, 0, rasterData.getWidth(), rasterData.getHeight(), bandIndex, doubleArray, dataBuffer);
            if (tileData.getElems() instanceof double[]) {
                System.arraycopy(doubleArray, 0, tileData.getElems(), 0, doubleArray.length);
            } else {
                for (int i=0; i< doubleArray.length; i++) {
                    tileData.setElemDoubleAt(i, doubleArray[i]);
                }
            }
        }
        return tileData;
    }

    static PlanarImage create(GeoTiffImageReader geoTiffImageReader, int tileRowIndex, int tileColumnIndex, Point productSubsetOffset, TileLayout tileLayout, MultiLevelModel imageModel, int dataType, int level) {
        Assert.notNull(tileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");

        TileLayout currentLayout = tileLayout;
        if (tileColumnIndex == tileLayout.numXTiles - 1 || tileRowIndex == tileLayout.numYTiles - 1) {
            int tileWidth = Math.min(tileLayout.width - tileColumnIndex * tileLayout.tileWidth, tileLayout.tileWidth);
            int tileHeight = Math.min(tileLayout.height - tileRowIndex * tileLayout.tileHeight, tileLayout.tileHeight);
            currentLayout = new TileLayout(tileLayout.width, tileLayout.height, tileWidth, tileHeight, tileLayout.numXTiles, tileLayout.numYTiles, tileLayout.numResolutions);
            currentLayout.numBands = tileLayout.numBands;
        }
        int offsetX = tileColumnIndex * computeValueAtResolutionLevel(tileLayout.tileWidth, level);
        int offsetY = tileRowIndex * computeValueAtResolutionLevel(tileLayout.tileHeight, level);
        Point tileOffset = new Point(offsetX, offsetY);
        return new IkonosTileOpImage(geoTiffImageReader, productSubsetOffset, tileOffset, currentLayout, imageModel, dataType, level);
    }

    static int computeValueAtResolutionLevel(int source, int level) {
        int size = source >> level;
        int sizeTest = size << level;
        if (sizeTest < source) {
            size++;
        }
        return size;
    }

    private static Dimension getTileDimensionAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = computeValueAtResolutionLevel(fullTileWidth, level);
        int height = computeValueAtResolutionLevel(fullTileHeight, level);
        return getTileDimension(width, height);
    }

    private static Dimension getTileDimension(int width, int height) {
        Dimension defaultTileSize = JAI.getDefaultTileSize();
        return new Dimension(Math.min(width, defaultTileSize.width), Math.min(height, defaultTileSize.height));
    }
}
