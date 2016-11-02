package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.esa.snap.core.util.ImageUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class GDALTileOpImage extends SingleBandedOpImage {
    private static final Logger logger = Logger.getLogger(GDALTileOpImage.class.getName());;

    private final Map<Path, Rectangle> tileDimensions;
    private final TileLayout tileLayout;
    private final Path imageFile;
    private final int bandIndex;
    private final int dataBufferType;
    private final int sourceX;
    private final int sourceY;

    private GDALTileOpImage(Path imageFile, int bandIndex, int sourceX, int sourceY, TileLayout tileLayout, MultiLevelModel imageModel, int dataBufferType, int level) {
        super(dataBufferType, null, tileLayout.tileWidth, tileLayout.tileHeight,
                getTileDimensionAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, level),
                null, ResolutionLevel.create(imageModel, level));

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(tileLayout, "tileLayout");
        Assert.notNull(imageModel, "imageModel");

        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.imageFile = imageFile;
        this.tileLayout = tileLayout;
        this.bandIndex = bandIndex;
        this.dataBufferType = dataBufferType;
        this.tileDimensions = new HashMap<Path, Rectangle>();
    }

    @Override
    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        ImageReader imageReader = new ImageReader(this.imageFile, this.bandIndex, this.dataBufferType);
        int fileTileX = destRect.x / this.tileLayout.tileWidth;
        int fileTileY = destRect.y / this.tileLayout.tileHeight;
        int fileTileOriginX = this.sourceX + destRect.x - (fileTileX * this.tileLayout.tileWidth);
        int fileTileOriginY = this.sourceY + destRect.y - (fileTileY * this.tileLayout.tileHeight);

        Rectangle fileTileRect = this.tileDimensions.get(this.imageFile);
        if (fileTileRect == null) {
            fileTileRect = new Rectangle(0, 0, imageReader.getImageWidth(), imageReader.getImageHeight());
            this.tileDimensions.put(this.imageFile, fileTileRect);
        }
        int tileWidth = getTileWidth();
        int tileHeight = getTileHeight();
        Rectangle tileRect = new Rectangle(fileTileOriginX, fileTileOriginY, tileWidth, tileHeight);

        Rectangle intersection = fileTileRect.intersection(tileRect);
        if (!intersection.isEmpty()) {
            try {
                RenderedImage readTileImage = imageReader.read(intersection);
                if (readTileImage != null) {
                    int bandList[] = new int[] { 0 }; // the band index is zero
                    Raster readBandRaster = readTileImage.getData().createChild(0, 0, readTileImage.getWidth(), readTileImage.getHeight(), 0, 0, bandList);
                    dest.setDataElements(dest.getMinX(), dest.getMinY(), readBandRaster);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static PlanarImage create(Path imageFile, int bandIndex, int row, int col, TileLayout tileLayout, MultiLevelModel imageModel, int dataBufferType, int level) {
        Assert.notNull(tileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");

        TileLayout currentLayout = tileLayout;
        // the edge tiles dimensions may be less than the dimensions from header
        if (row == tileLayout.numYTiles - 1 || col == tileLayout.numXTiles - 1) {
            int tileWidth = Math.min(tileLayout.width - col * tileLayout.tileWidth, tileLayout.tileWidth);
            int tileHeight = Math.min(tileLayout.height - row * tileLayout.tileHeight, tileLayout.tileHeight);
            currentLayout = new TileLayout(tileLayout.width, tileLayout.height, tileWidth, tileHeight, tileLayout.numXTiles, tileLayout.numYTiles, tileLayout.numResolutions);
            currentLayout.numBands = tileLayout.numBands;
        }

        if (imageFile != null) {
            int sourceX = col * tileLayout.tileWidth;
            int sourceY = row * tileLayout.tileWidth;
            return new GDALTileOpImage(imageFile, bandIndex, sourceX, sourceY, currentLayout, imageModel, dataBufferType, level);
        }

        int targetWidth = currentLayout.tileWidth;
        int targetHeight = currentLayout.tileHeight;
        Dimension targetTileDim = getTileDimensionAtResolutionLevel(targetWidth, targetHeight, level);
        SampleModel sampleModel = ImageUtils.createSingleBandedSampleModel(dataBufferType, targetWidth, targetHeight);
        ImageLayout imageLayout = new ImageLayout(0, 0, targetWidth, targetHeight, 0, 0, targetTileDim.width, targetTileDim.height, sampleModel, null);

        float width = (float) imageLayout.getWidth(null);
        float height = (float) imageLayout.getHeight(null);
        Number[] bandValues = new Short[]{0};
        return ConstantDescriptor.create(width, height, bandValues, new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
    }

    public static int scaleValue(int source, int level) {
        int size = source >> level;
        int sizeTest = size << level;
        if (sizeTest < source) {
            size++;
        }
        return size;
    }

    private static Dimension getTileDimensionAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = scaleValue(fullTileWidth, level);
        int height = scaleValue(fullTileHeight, level);
        return getTileDimension(width, height);
    }

    private static Dimension getTileDimension(int width, int height) {
        Dimension defaultTileSize = JAI.getDefaultTileSize();
        return new Dimension(Math.min(width, defaultTileSize.width), Math.min(height, defaultTileSize.height));
    }

    private static class ImageReader {
        private final org.gdal.gdal.Band band;
        private final int dataBufferType;

        private ImageReader(Path inputFile, int bandIndex, int dataBufferType) {
            this.dataBufferType = dataBufferType;
            Dataset poDataset = gdal.Open(inputFile.toString(), gdalconst.GA_ReadOnly);
            // bands are not 0-base indexed, so we must add 1
            this.band = poDataset.GetRasterBand(bandIndex + 1);
        }

        int getImageWidth() {
            return this.band.getXSize();
        }

        int getImageHeight() {
            return this.band.getYSize();
        }

        RenderedImage read(Rectangle rectangle) throws IOException {
            int imageWidth = rectangle.width;
            int imageHeight = rectangle.height;
            int pixels = imageWidth * imageHeight;
            int gdalDataType = this.band.getDataType();
            int bufferSize = pixels * gdal.GetDataTypeSize(gdalDataType) / 8;
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            int returnVal = this.band.ReadRaster_Direct(rectangle.x, rectangle.y, rectangle.width, rectangle.height, imageWidth, imageHeight, gdalDataType, data);
            if (returnVal == gdalconstConstants.CE_None) {
                // the band index is zero
                int[] bankIndices = new int[] { 0 };
                int[] bandOffsets = new int[] { 0 };

                DataBuffer imgBuffer = null;
                SampleModel sampleModel = null;
                int imageType = 0;
                if (this.dataBufferType == DataBuffer.TYPE_BYTE) {
                    byte[] bytes = new byte[pixels];
                    data.get(bytes);
                    imgBuffer = new DataBufferByte(bytes, pixels);
                    sampleModel = new BandedSampleModel(this.dataBufferType, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                    imageType = (this.band.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) ? BufferedImage.TYPE_BYTE_INDEXED : BufferedImage.TYPE_BYTE_GRAY;
                } else if (this.dataBufferType == DataBuffer.TYPE_SHORT || this.dataBufferType == DataBuffer.TYPE_USHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imgBuffer = new DataBufferShort(shorts, pixels);
                    sampleModel = new BandedSampleModel(this.dataBufferType, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                    imageType = BufferedImage.TYPE_USHORT_GRAY;
                } else if (this.dataBufferType == DataBuffer.TYPE_INT) {
                    int[] ints = new int[pixels];
                    data.asIntBuffer().get(ints);
                    imgBuffer = new DataBufferInt(ints, pixels);
                    sampleModel = new BandedSampleModel(this.dataBufferType, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                    imageType = BufferedImage.TYPE_BYTE_INDEXED;
                } else if (this.dataBufferType == DataBuffer.TYPE_FLOAT) {
                    float[] floats = new float[pixels];
                    data.asFloatBuffer().get(floats);
                    imgBuffer = new DataBufferFloat(floats, pixels);
                    sampleModel = new BandedSampleModel(this.dataBufferType, imageWidth, imageHeight, imageWidth, bankIndices, bandOffsets);
                    imageType = BufferedImage.TYPE_BYTE_INDEXED;
                } else {
                    throw new IllegalArgumentException("Unknown data buffer type " + this.dataBufferType + ".");
                }
                WritableRaster raster = Raster.createWritableRaster(sampleModel, imgBuffer, null);
                BufferedImage image = null;
                if (this.band.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex) {
                    ColorModel cm = this.band.GetRasterColorTable().getIndexColorModel(gdal.GetDataTypeSize(gdalDataType));
                    image = new BufferedImage(cm, raster, false, null);
                } else {
                    image = new BufferedImage(imageWidth, imageHeight, imageType);
                    image.setData(raster);
                }
                return image;
            } else {
                throw new IOException("Failed to read the product band data.");
            }
        }
    }
}
