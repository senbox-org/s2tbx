package org.esa.s2tbx.dataio.gdal.reader;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.gdal.drivers.Band;
import org.esa.s2tbx.dataio.gdal.drivers.Dataset;
import org.esa.s2tbx.dataio.gdal.drivers.GDAL;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConst;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConstConstants;
import org.esa.s2tbx.dataio.readers.TileLayout;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A JAI operator for handling the tiles of the products imported with the GDAL library.
 *
 * @author Jean Coravu
 */
class GDALTileOpImage extends SingleBandedOpImage {
    private static final Logger logger = Logger.getLogger(GDALTileOpImage.class.getName());

    private final TileLayout tileLayout;
    private final Path imageFile;
    private final int bandIndex;
    private final int dataBufferType;
    private final int sourceX;
    private final int sourceY;

    private ImageReader imageReader;

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
    }

    /**
     * Factory method for creating a TileOpImage instance.
     *
     * @param imageFile      The file path
     * @param bandIndex      The index of the band for which the operator is created
     * @param row            The row of the tile in the scene layout
     * @param col            The column of the tile in the scene layout
     * @param tileLayout     The scene layout
     * @param imageModel     The multi-level image model
     * @param dataBufferType The data type of the tile raster
     * @param level          The resolution at which the tile is created
     * @return The TileOpImage instance
     */
    static PlanarImage create(Path imageFile, int bandIndex, int row, int col, TileLayout tileLayout, MultiLevelModel imageModel, int dataBufferType, int level) {
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
            int sourceX = col * scaleValue(tileLayout.tileWidth, level);
            int sourceY = row * scaleValue(tileLayout.tileHeight, level);
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

    static int scaleValue(int source, int level) {
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

    @Override
    protected synchronized void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        int fileTileX = destRect.x / this.tileLayout.tileWidth;
        int fileTileY = destRect.y / this.tileLayout.tileHeight;
        int fileTileOriginX = destRect.x - (fileTileX * this.tileLayout.tileWidth);
        int fileTileOriginY = destRect.y - (fileTileY * this.tileLayout.tileHeight);
        if (this.imageReader == null) {
            this.imageReader = new ImageReader(this.imageFile, this.bandIndex, this.sourceX, this.sourceY, this.dataBufferType, getLevel());
        }
        int fileTileWidth = this.imageReader.getBandWidth();
        int fileTileHeight = this.imageReader.getBandHeight();
        Rectangle fileTileRect = new Rectangle(0, 0, fileTileWidth, fileTileHeight);

        int tileWidth = getTileWidth();
        int tileHeight = getTileHeight();
        Rectangle tileRect = new Rectangle(fileTileOriginX, fileTileOriginY, tileWidth, tileHeight);

        Rectangle intersection = fileTileRect.intersection(tileRect);
        if (!intersection.isEmpty()) {
            try {
                int imageWidth = intersection.width;
                int imageHeight = intersection.height;
                int xOffset = this.imageReader.getOffsetX() + intersection.x;
                int yOffset = this.imageReader.getOffsetY() + intersection.y;
                int nXOffset = xOffset + imageWidth;
                int nYOffset = yOffset + imageHeight;
                int xBlock = xOffset / this.imageReader.getBandBlockWidth();
                int yBlock = yOffset / this.imageReader.getBandBlockHeight();
                int nXBlock = nXOffset / this.imageReader.getBandBlockWidth() + (nXOffset % this.imageReader.getBandBlockWidth() > 0 ? 1 : 0);
                int nYBlock = nYOffset / this.imageReader.getBandBlockHeight() + (nYOffset % this.imageReader.getBandBlockHeight() > 0 ? 1 : 0);

                long startTime = System.currentTimeMillis();

                int x = dest.getMinX();
                Raster readBandRaster = null;

                for (int iXBlock = xBlock; iXBlock < nXBlock; iXBlock++) {
                    int y = dest.getMinY();
                    for (int iYBlock = yBlock; iYBlock < nYBlock; iYBlock++) {
                        RenderedImage readTileImage = this.imageReader.read(iXBlock, iYBlock);
                        if (readTileImage != null) {
                            int[] bandList = new int[]{0}; // the band index is zero
                            Raster imageRaster = readTileImage.getData();
                            int rasterWidth = Math.min(imageWidth, readTileImage.getTileWidth());
                            int rasterHeight = Math.min(imageHeight, readTileImage.getTileHeight());
                            int rasterParentX = 0;
                            int rasterParentY = 0;

                            if (dest.getMinX() + rasterWidth <= imageRaster.getWidth()) {
                                rasterParentX = dest.getMinX();
                            }
                            if (dest.getMinY() + rasterHeight <= imageRaster.getHeight()) {
                                rasterParentY = dest.getMinY();
                            }
                            readBandRaster = imageRaster.createChild(rasterParentX, rasterParentY, rasterWidth, rasterHeight, 0, 0, bandList);
                            dest.setDataElements(x, y, readBandRaster);
                        }
                        if (readBandRaster != null) {
                            y += readBandRaster.getHeight();
                        }
                    }
                    if (readBandRaster != null) {
                        x += readBandRaster.getWidth();
                    }
                }

                long endTime = System.currentTimeMillis();
                String msg = String.format("readBlockDirect (took %s)", (new SimpleDateFormat("mm:ss:SSS")).format(new Date(endTime - startTime)));
                SystemUtils.LOG.info(msg);

            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public synchronized void dispose() {
        super.dispose();

        if (this.imageReader != null) {
            this.imageReader.close();
            this.imageReader = null;
        }
    }

    private static class ImageReader {
        private final Band band;
        private final int dataBufferType;
        private final int level;
        private final int offsetX;
        private final int offsetY;
        private Dataset gdalDataset;

        private ImageReader(Path inputFile, int bandIndex, int offsetX, int offsetY, int dataBufferType, int level) {
            this.dataBufferType = dataBufferType;
            this.level = level;
            this.offsetX = offsetX;
            this.offsetY = offsetY;

            this.gdalDataset = GDAL.open(inputFile.toString(), GDALConst.gaReadonly());
            // bands are not 0-base indexed, so we must add 1
            if (this.gdalDataset == null) {
                throw new IllegalStateException("No data set received from GDAL.");
            }
            Band rasterBand = this.gdalDataset.getRasterBand(bandIndex + 1);
            if (level > 0 && rasterBand.getOverviewCount() > 0) {
                this.band = rasterBand.getOverview(this.level - 1);
            } else {
                this.band = rasterBand;
            }
        }

        int getBandWidth() {
            return this.band.getXSize();
        }

        int getBandHeight() {
            return this.band.getYSize();
        }

        int getOffsetX() {
            return this.offsetX;
        }

        int getOffsetY() {
            return this.offsetY;
        }

        int getBandBlockWidth() {
            return this.band.getBlockXSize();
        }

        int getBandBlockHeight() {
            return this.band.getBlockYSize();
        }

        void close() {
            this.gdalDataset.delete();
        }

        RenderedImage read(int iXBlock, int iYBlock) throws IOException {
            int imageWidth = getBandBlockWidth();
            int imageHeight = getBandBlockHeight();
            int pixels = imageWidth * imageHeight;
            int gdalBufferDataType = this.band.getDataType();
            int bufferSize = pixels * GDAL.getDataTypeSize(gdalBufferDataType);
            ByteBuffer data = ByteBuffer.allocateDirect(bufferSize);
            data.order(ByteOrder.nativeOrder());

            int returnVal = this.band.readBlockDirect(iXBlock, iYBlock, data);

            if (returnVal == GDALConstConstants.ceNone()) {
                DataBuffer imageDataBuffer;
                if (this.dataBufferType == DataBuffer.TYPE_BYTE) {
                    byte[] bytes = new byte[pixels];
                    data.get(bytes);
                    imageDataBuffer = new DataBufferByte(bytes, pixels);
                } else if (this.dataBufferType == DataBuffer.TYPE_SHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferShort(shorts, shorts.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_USHORT) {
                    short[] shorts = new short[pixels];
                    data.asShortBuffer().get(shorts);
                    imageDataBuffer = new DataBufferUShort(shorts, shorts.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_INT) {
                    int[] ints = new int[pixels];
                    data.asIntBuffer().get(ints);
                    imageDataBuffer = new DataBufferInt(ints, ints.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_FLOAT) {
                    float[] floats = new float[pixels];
                    data.asFloatBuffer().get(floats);
                    imageDataBuffer = new DataBufferFloat(floats, floats.length);
                } else if (this.dataBufferType == DataBuffer.TYPE_DOUBLE) {
                    double[] doubles = new double[pixels];
                    data.asDoubleBuffer().get(doubles);
                    imageDataBuffer = new DataBufferDouble(doubles, doubles.length);
                } else {
                    throw new IllegalArgumentException("Unknown data buffer type " + this.dataBufferType + ".");
                }
                int[] index = new int[]{0};
                SampleModel sampleModel = new ComponentSampleModel(imageDataBuffer.getDataType(), imageWidth, imageHeight, 1, imageWidth, index);
                WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, imageDataBuffer, null);
                BufferedImage image;
                if (this.band.getRasterColorInterpretation().equals(GDALConstConstants.gciPaletteindex())) {
                    ColorModel cm = this.band.getRasterColorTable().getIndexColorModel(GDAL.getDataTypeSize(gdalBufferDataType));
                    image = new BufferedImage(cm, writableRaster, false, null);
                } else if (imageDataBuffer instanceof DataBufferByte || imageDataBuffer instanceof DataBufferUShort) {
                    int imageType = (imageDataBuffer instanceof DataBufferByte) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_USHORT_GRAY;
                    image = new BufferedImage(imageWidth, imageHeight, imageType);
                    image.setData(writableRaster);
                } else {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, imageDataBuffer.getDataType());
                    image = new BufferedImage(cm, writableRaster, true, null);
                }
                return image;
            } else {
                throw new IOException("Failed to read the product band data.");
            }
        }
    }
}
