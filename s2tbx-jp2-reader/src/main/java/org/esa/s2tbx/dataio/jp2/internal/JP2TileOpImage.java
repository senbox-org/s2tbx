package org.esa.s2tbx.dataio.jp2.internal;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A JAI operator for handling JP2 tiles.
 *
 * @author Cosmin Cara
 */
public class JP2TileOpImage extends SingleBandedOpImage {

    private final TileLayout tileLayout;
    private final File imageFile;
    private final File cacheDir;
    private final ImageReader imageReader;
    private final int tileIndex;
    private final int bandIndex;
    private final Logger logger;

    public JP2TileOpImage(File imageFile, int bandIdx, File cacheDir, int row, int col,
                          TileLayout tileLayout, MultiLevelModel imageModel, int dataType, int level) throws IOException {
        super(dataType, null, tileLayout.tileWidth, tileLayout.tileHeight,
                getTileDimAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, level),
                null, ResolutionLevel.create(imageModel, level));

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(tileLayout, "tileLayout");
        Assert.notNull(imageModel, "imageModel");

        this.logger = SystemUtils.LOG;
        this.imageFile = imageFile;
        this.cacheDir = cacheDir;
        this.tileLayout = tileLayout;
        this.tileIndex = col + row * tileLayout.numXTiles;
        this.bandIndex = bandIdx;
        imageReader = new ImageReader();
    }

    /**
     * Factory method for creating a TileOpImage instance.
     *
     * @param imageFile     The JP2 file
     * @param cacheDir      The directory where decompressed tiles will be extracted
     * @param bandIdx       The index of the band for which the operator is created
     * @param row           The row of the tile in the scene layout
     * @param col           The column of the tile in the scene layout
     * @param tileLayout    The scene layout
     * @param imageModel    The multi-level image model
     * @param dataType      The data type of the tile raster
     * @param level         The resolution at which the tile is created
     * @throws IOException
     */
    public static PlanarImage create(File imageFile, File cacheDir, int bandIdx,
                                     int row, int col, TileLayout tileLayout,
                                     MultiLevelModel imageModel, int dataType, int level) throws IOException {
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(tileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");
        if (imageFile != null) {
            return new JP2TileOpImage(imageFile, bandIdx, cacheDir, row, col, tileLayout, imageModel, dataType, level);
        } else {
            int targetWidth = tileLayout.tileWidth;
            int targetHeight = tileLayout.tileHeight;
            Dimension targetTileDim = getTileDimAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, level);
            SampleModel sampleModel = ImageUtils.createSingleBandedSampleModel(dataType, targetWidth, targetHeight);
            ImageLayout imageLayout = new ImageLayout(0, 0, targetWidth, targetHeight, 0, 0, targetTileDim.width, targetTileDim.height, sampleModel, null);
            return ConstantDescriptor.create((float) imageLayout.getWidth(null),
                    (float) imageLayout.getHeight(null),
                    new Short[]{0},
                    new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
        }
    }

    @Override
    protected synchronized void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        final DataBuffer dataBuffer = dest.getDataBuffer();
        int tileWidth = this.getTileWidth();
        int tileHeight = this.getTileHeight();
        final int fileTileX = destRect.x / tileLayout.tileWidth;
        final int fileTileY = destRect.y / tileLayout.tileHeight;
        int fileTileOriginX = destRect.x - fileTileX * tileLayout.tileWidth;
        int fileTileOriginY = destRect.y - fileTileY * tileLayout.tileHeight;
        try {
            imageReader.setInput(decompressTile(tileIndex, getLevel()));
            int fileTileWidth = imageReader.getImageWidth();
            int fileTileHeight = imageReader.getImageHeight();
            RenderedImage readTileImage = null;
            if (fileTileOriginX == 0 && tileLayout.tileWidth == tileWidth
                    && fileTileOriginY == 0 && tileLayout.tileHeight == tileHeight
                    && tileWidth * tileHeight == dataBuffer.getSize()) {
                readTileImage = imageReader.read();
            } else {
                final Rectangle fileTileRect = new Rectangle(0, 0, fileTileWidth, fileTileHeight);
                final Rectangle tileRect = new Rectangle(fileTileOriginX, fileTileOriginY, tileWidth, tileHeight);
                final Rectangle intersection = fileTileRect.intersection(tileRect);
                if (!intersection.isEmpty()) {
                    readTileImage = imageReader.read(intersection);
                }
            }
            if (readTileImage != null) {
                Raster readBandRaster = readTileImage.getData().createChild(0, 0, readTileImage.getWidth(), readTileImage.getHeight(), 0, 0, new int[] { bandIndex });
                dest.setDataElements(dest.getMinX(), dest.getMinY(), readBandRaster);
            }

        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private static int scaleValue(int source, int level) {
        int size = source >> level;
        int sizeTest = size << level;
        if (sizeTest < source) {
            size++;
        }
        return size;
    }

    protected static Dimension getTileDimAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = scaleValue(fullTileWidth, level);
        int height = scaleValue(fullTileHeight, level);
        return getTileDim(width, height);
    }

    static Dimension getTileDim(int width, int height) {
        return new Dimension(width < JAI.getDefaultTileSize().width ? width : JAI.getDefaultTileSize().width,
                height < JAI.getDefaultTileSize().height ? height : JAI.getDefaultTileSize().height);
    }


    protected File decompressTile(int tileIndex, int level) throws IOException {
        File tileFile = new File(cacheDir, imageFile.getName() + "_tile_" + String.valueOf(tileIndex) + "_" + String.valueOf(level) + ".tif");
        if (!tileFile.exists()) {
            final OpjExecutor decompress = new OpjExecutor(OpenJpegExecRetriever.getSafeDecompressorAndUpdatePermissions());
            final Map<String, String> params = new HashMap<String, String>() {{
                put("-i", imageFile.getAbsolutePath());
                put("-r", String.valueOf(level));
                put("-l", "20");
            }};
            params.put("-o", tileFile.getAbsolutePath());
            params.put("-t", String.valueOf(tileIndex));
            if (decompress.execute(params) != 0) {
                logger.severe(decompress.getLastError());
            } else {
                logger.fine("Decompressed tile #" + String.valueOf(tileIndex) + " @ resolution " + String.valueOf(level));
            }
        }
        return tileFile;
    }


    @Override
    public synchronized void dispose() {
        imageReader.close();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    private class ImageReader {
        TIFFImageReader imageReader;
        ImageInputStream inputStream;

        public ImageReader() throws IOException {
            Iterator<javax.imageio.ImageReader> imageReaders = ImageIO.getImageReadersByFormatName("tiff");
            while (imageReaders.hasNext()) {
                final javax.imageio.ImageReader reader = imageReaders.next();
                if (reader instanceof TIFFImageReader) {
                    imageReader = (TIFFImageReader) reader;
                    break;
                }
            }
            if (imageReader == null) {
                throw new IOException("Tiff imageReader not found");
            }
        }

        public void setInput(File input) throws IOException {
            inputStream = ImageIO.createImageInputStream(input);
            imageReader.setInput(inputStream);
        }

        public int getImageWidth() throws IOException {
            return inputStream != null ? imageReader.getWidth(0) : 0;
        }

        public int getImageHeight() throws IOException {
            return inputStream != null ? imageReader.getHeight(0) : 9;
        }

        public RenderedImage read() throws IOException {
            if (inputStream == null) {
                throw new IOException("No input stream");
            }
            return imageReader.readAsRenderedImage(0, null);
        }

        public RenderedImage read(Rectangle rectangle) throws IOException {
            if (inputStream == null) {
                throw new IOException("No input stream");
            }
            ImageReadParam params = imageReader.getDefaultReadParam();
            params.setSourceRegion(rectangle);
            return imageReader.read(0, params);
        }

        public void close() {
            try {
                if (inputStream != null)
                    inputStream.close();
                imageReader.dispose();
            } catch (IOException ignored) {
            }
        }
    }

}
