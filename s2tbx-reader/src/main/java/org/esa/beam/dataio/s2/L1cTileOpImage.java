package org.esa.beam.dataio.s2;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.apache.commons.lang.SystemUtils;
import org.esa.beam.jai.ResolutionLevel;
import org.esa.beam.jai.SingleBandedOpImage;
import org.esa.beam.util.ImageUtils;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.*;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.esa.beam.dataio.s2.S2Config.L1C_TILE_LAYOUTS;

// todo - better log problems during read process, see {@report "Problem detected..."} code marks

/**
 * @author Norman Fomferra
 */
class L1cTileOpImage extends SingleBandedOpImage {

    private static class Jp2File {
        File file;
        String header;
        ImageInputStream stream;
        long dataPos;
        int width;
        int height;
    }

    protected final Logger logger;
    private final File imageFile;
    private final File cacheDir;
    private final L1cTileLayout l1cTileLayout;
    private Map<File, Jp2File> openFiles;
    private Map<File, Object> locks;

    static PlanarImage create(File imageFile,
                              File cacheDir,
                              Point imagePos,
                              L1cTileLayout l1cTileLayout,
                              MultiLevelModel imageModel,
                              S2SpatialResolution spatialResolution,
                              int level) {

        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1cTileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");
        Assert.notNull(spatialResolution, "spatialResolution");

        if (imageFile != null) {
            PlanarImage opImage = new L1cTileOpImage(imageFile, cacheDir, imagePos, l1cTileLayout, imageModel, level);

            BeamLogManager.getSystemLogger().fine("The size before: " + opImage.getWidth() + ", " + opImage.getHeight());

            // todo skipping scale

            if (spatialResolution != S2SpatialResolution.R10M) {
                PlanarImage scaled = createScaledImage(opImage, spatialResolution, level);
                BeamLogManager.getSystemLogger().fine("The size after: " + scaled.getWidth() + ", " + scaled.getHeight());

                return scaled;
            }

            return opImage;
        } else {
            int targetWidth = getSizeAtResolutionLevel(L1C_TILE_LAYOUTS[0].width, level);
            int targetHeight = getSizeAtResolutionLevel(L1C_TILE_LAYOUTS[0].height, level);
            Dimension targetTileDim = getTileDimAtResolutionLevel(L1C_TILE_LAYOUTS[0].tileWidth, L1C_TILE_LAYOUTS[0].tileHeight, level);
            SampleModel sampleModel = ImageUtils.createSingleBandedSampleModel(S2Config.SAMPLE_DATA_BUFFER_TYPE, targetWidth, targetHeight);
            ImageLayout imageLayout = new ImageLayout(0, 0, targetWidth, targetHeight, 0, 0, targetTileDim.width, targetTileDim.height, sampleModel, null);
            return ConstantDescriptor.create((float) imageLayout.getWidth(null),
                                             (float) imageLayout.getHeight(null),
                                             new Short[]{S2Config.FILL_CODE_NO_FILE},
                                             new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
        }
    }

    static PlanarImage createScaledImage(PlanarImage sourceImage, S2SpatialResolution resolution, int level) {
        BeamLogManager.getSystemLogger().fine("Asking for scaled image: " + resolution.toString());

        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        int targetWidth = getSizeAtResolutionLevel(L1C_TILE_LAYOUTS[0].width, level);
        int targetHeight = getSizeAtResolutionLevel(L1C_TILE_LAYOUTS[0].height, level);
        float scaleX = resolution.resolution / (float) S2SpatialResolution.R10M.resolution;
        float scaleY = resolution.resolution / (float) S2SpatialResolution.R10M.resolution;
        final Dimension tileDim = getTileDim(targetWidth, targetHeight);
        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setTileWidth(tileDim.width);
        imageLayout.setTileHeight(tileDim.height);
        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_ZERO);
        RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                           borderExtender);
        renderingHints.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);
        RenderedOp scaledImage = ScaleDescriptor.create(sourceImage,
                                                        scaleX,
                                                        scaleY,
                                                        sourceImage.getMinX() - sourceImage.getMinX() * scaleX,
                                                        sourceImage.getMinY() - sourceImage.getMinY() * scaleY,
                                                        Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                        renderingHints);
        if (scaledImage.getWidth() == targetWidth && scaledImage.getHeight() == targetHeight) {
            return scaledImage;
        }
        else if (scaledImage.getWidth() >= targetWidth && scaledImage.getHeight() >= targetHeight) {
            return CropDescriptor.create(scaledImage,
                                         (float) sourceImage.getMinX(),
                                         (float) sourceImage.getMinY(),
                                         (float) targetWidth,
                                         (float) targetHeight,
                                         null);
        }
        else if (scaledImage.getWidth() <= targetWidth && scaledImage.getHeight() <= targetHeight) {
            int rightPad = targetWidth - scaledImage.getWidth();
            int bottomPad = targetHeight - scaledImage.getHeight();
            return BorderDescriptor.create(scaledImage, 0, rightPad, 0, bottomPad, borderExtender, null);
        } else {
            throw new IllegalStateException();
        }
    }

    L1cTileOpImage(File imageFile,
                   File cacheDir,
                   Point imagePos,
                   L1cTileLayout l1cTileLayout,
                   MultiLevelModel imageModel,
                   int level) {
        super(S2Config.SAMPLE_DATA_BUFFER_TYPE,
              imagePos,
              l1cTileLayout.width,
              l1cTileLayout.height,
              getTileDimAtResolutionLevel(l1cTileLayout.tileWidth, l1cTileLayout.tileHeight, level),
              null,
              ResolutionLevel.create(imageModel, level));

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1cTileLayout, "l1cTileLayout");
        Assert.notNull(imageModel, "imageModel");

        this.imageFile = imageFile;
        this.cacheDir = cacheDir;
        this.l1cTileLayout = l1cTileLayout;
        this.openFiles = new HashMap<File, Jp2File>();
        this.locks = new HashMap<File, Object>();
        this.logger = BeamLogManager.getSystemLogger();
    }

    @Override
    protected synchronized void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        final DataBufferUShort dataBuffer = (DataBufferUShort) dest.getDataBuffer();
        final short[] tileData = dataBuffer.getData();

        final int tileWidth = this.getTileWidth();
        final int tileHeight = this.getTileHeight();
        final int tileX = destRect.x / tileWidth;
        final int tileY = destRect.y / tileHeight;

        if (tileWidth * tileHeight != tileData.length) {
            throw new IllegalStateException(String.format("tileWidth (=%d) * tileHeight (=%d) != tileData.length (=%d)",
                                                          tileWidth, tileHeight, tileData.length));
        }

        final Dimension jp2TileDim = getDimAtResolutionLevel(l1cTileLayout.tileWidth, l1cTileLayout.tileHeight, getLevel());

        final int jp2TileWidth = jp2TileDim.width;
        final int jp2TileHeight = jp2TileDim.height;
        final int jp2TileX = destRect.x / jp2TileWidth;
        final int jp2TileY = destRect.y / jp2TileHeight;

        // Res - Img Size - Tile W
        //  0  -  10960   -  4096
        //  1  -   5480   -  2048
        //  2  -   2740   -  1024
        //  3  -   1370   -   512
        //  4  -    685   -   256
        //  5  -    343   -   128

        File outputFile = null;

        try {
            outputFile = new File(cacheDir,
                                  FileUtils.exchangeExtension(imageFile.getName(),
                                                              String.format("_R%d_TX%d_TY%d.pgx",
                                                                            getLevel(), jp2TileX, jp2TileY)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final File outputFile0 = getFirstComponentOutputFile(outputFile);


        // todo - outputFile0 may have already been created, although 'opj_decompress' has not finished execution.
        //        This may be the reason for party filled tiles, that sometimes occur
        if (!outputFile0.exists())
        {
            logger.fine(String.format("Jp2ExeImage.readTileData(): recomputing res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));

            try {
                decompressTile(outputFile, jp2TileX, jp2TileY);
            } catch (IOException e) {
                logger.severe("opj_decompress process failed: " + e.getMessage());
                if (outputFile0.exists() && !outputFile0.delete()) {
                    logger.severe("Failed to delete file: " + outputFile0.getAbsolutePath());
                }
            }
            if (!outputFile0.exists()) {
                logger.fine("No output file generated");
                Arrays.fill(tileData, S2Config.FILL_CODE_NO_FILE);
                return;
            }
        }

        try {
            logger.fine(String.format("Jp2ExeImage.readTileData(): reading res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));
            readTileData(outputFile0, tileX, tileY, tileWidth, tileHeight, jp2TileX, jp2TileY, jp2TileWidth, jp2TileHeight, tileData, destRect);
        } catch (IOException e) {
            logger.severe("Failed to read uncompressed tile data");
        }
    }

    private File getFirstComponentOutputFile(File outputFile) {
        return FileUtils.exchangeExtension(outputFile, "_0.pgx");
    }

    private void decompressTile(final File outputFile, int jp2TileX, int jp2TileY) throws IOException {
        final int tileIndex = l1cTileLayout.numXTiles * jp2TileY + jp2TileX;


        ProcessBuilder builder = null;
        if(SystemUtils.IS_OS_WINDOWS)
        {
            String inputFileName = Utils.GetIterativeShortPathName(imageFile.getPath());
            String outputFileName = outputFile.getPath();

            if(inputFileName.length() == 0)
            {
                inputFileName = imageFile.getPath();
            }

            builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                    "-i", inputFileName,
                    "-o", outputFileName,
                    "-r", getLevel() + "",
                    "-t", tileIndex + "");
        }
        else
        {
            builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                    "-i", imageFile.getPath(),
                    "-o", outputFile.getPath(),
                    "-r", getLevel() + "",
                    "-t", tileIndex + "");
        }

        logger.fine(builder.command().toString());
        final Process process = builder.directory(cacheDir).start();

        try {
            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.severe("Failed to uncompress tile: exitCode = " + exitCode);
            }
        } catch (InterruptedException e) {
            logger.severe("InterruptedException: " + e.getMessage());
        }
    }

    @Override
    public synchronized void dispose() {

        for (Map.Entry<File, Jp2File> entry : openFiles.entrySet()) {
            logger.fine("closing " + entry.getKey());
            try {
                final Jp2File jp2File = entry.getValue();
                if (jp2File.stream != null) {
                    jp2File.stream.close();
                    jp2File.stream = null;
                }
            } catch (IOException e) {
                logger.severe("Failed to close stream: " + e.getMessage());
            }
        }

        for (File file : openFiles.keySet()) {
            logger.fine("deleting " + file.getAbsolutePath());
            if (!file.delete()) {
                logger.severe("Failed to delete file: " + file.getAbsolutePath());
            }
        }

        openFiles.clear();

        if (!cacheDir.delete()) {
            logger.severe("Failed to delete cache dir: " + cacheDir.getAbsolutePath() );
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    private void readTileData(File outputFile,
                              int tileX, int tileY,
                              int tileWidth, int tileHeight,
                              int jp2TileX, int jp2TileY,
                              int jp2TileWidth, int jp2TileHeight,
                              short[] tileData,
                              Rectangle destRect) throws IOException {

        synchronized (this) {
            if (!locks.containsKey(outputFile)) {
                locks.put(outputFile, new Object());
            }
        }

        // todo - we still have a synchronisation problem here: often zero areas are generated in a tile.
        // This does not happen, if we synchronise entire computeRect() on the instance, but it is less efficient.
        final Object lock = locks.get(outputFile);
        synchronized (lock) {

            Jp2File jp2File = getOpenJ2pFile(outputFile);

            int jp2Width = jp2File.width;
            int jp2Height = jp2File.height;
            if (jp2Width > jp2TileWidth || jp2Height > jp2TileHeight) {
                throw new IllegalStateException(String.format("width (=%d) > tileWidth (=%d) || height (=%d) > tileHeight (=%d)",
                                                              jp2Width, jp2TileWidth, jp2Height, jp2TileHeight));
            }

            int jp2X = destRect.x - jp2TileX * jp2TileWidth;
            int jp2Y = destRect.y - jp2TileY * jp2TileHeight;
            if (jp2X < 0 || jp2Y < 0) {
                throw new IllegalStateException(String.format("jp2X (=%d) < 0 || jp2Y (=%d) < 0",
                                                              jp2X, jp2Y));
            }

            final ImageInputStream stream = jp2File.stream;

            if (jp2X == 0 && jp2Width == tileWidth
                    && jp2Y == 0 && jp2Height == tileHeight
                    && tileWidth * tileHeight == tileData.length) {
                stream.seek(jp2File.dataPos);
                stream.readFully(tileData, 0, tileData.length);
            } else {
                final Rectangle jp2FileRect = new Rectangle(0, 0, jp2Width, jp2Height);
                final Rectangle tileRect = new Rectangle(jp2X,
                                                         jp2Y,
                                                         tileWidth, tileHeight);
                final Rectangle intersection = jp2FileRect.intersection(tileRect);
                //System.out.printf("%s: tile=(%d,%d): jp2FileRect=%s, tileRect=%s, intersection=%s\n", jp2File.file, tileX, tileY, jp2FileRect, tileRect, intersection);
                if (!intersection.isEmpty()) {
                    long seekPos = jp2File.dataPos + S2Config.SAMPLE_BYTE_COUNT * (intersection.y * jp2Width + intersection.x);
                    int tilePos = 0;
                    for (int y = 0; y < intersection.height; y++) {
                        stream.seek(seekPos);
                        stream.readFully(tileData, tilePos, intersection.width);
                        seekPos += S2Config.SAMPLE_BYTE_COUNT * jp2Width;
                        tilePos += tileWidth;
                        for (int x = intersection.width; x < tileWidth; x++) {
                            tileData[y * tileWidth + x] = S2Config.FILL_CODE_OUT_OF_X_BOUNDS;
                        }
                    }
                    for (int y = intersection.height; y < tileWidth; y++) {
                        for (int x = 0; x < tileWidth; x++) {
                            tileData[y * tileWidth + x] = S2Config.FILL_CODE_OUT_OF_Y_BOUNDS;
                        }
                    }
                } else {
                    Arrays.fill(tileData, S2Config.FILL_CODE_NO_INTERSECTION);
                }
            }
        }
    }

    private Jp2File getOpenJ2pFile(File outputFile) throws IOException {
        Jp2File jp2File = openFiles.get(outputFile);
        if (jp2File == null) {
            jp2File = new Jp2File();
            jp2File.file = outputFile;
            jp2File.stream = new FileImageInputStream(outputFile);
            jp2File.header = jp2File.stream.readLine();
            jp2File.dataPos = jp2File.stream.getStreamPosition();

            final String[] tokens = jp2File.header.split(" ");
            if (tokens.length != 6) {
                throw new IOException("Unexpected PGX tile image format");
            }

            // String pg = tokens[0];   // PG
            // String ml = tokens[1];   // ML
            // String plus = tokens[2]; // +
            try {
                // int jp2File.nbits = Integer.parseInt(tokens[3]);
                jp2File.width = Integer.parseInt(tokens[4]);
                jp2File.height = Integer.parseInt(tokens[5]);
            } catch (NumberFormatException e) {
                throw new IOException("Unexpected PGX tile image format");
            }

            openFiles.put(outputFile, jp2File);
        }

        return jp2File;
    }

    static Dimension getTileDimAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = getSizeAtResolutionLevel(fullTileWidth, level);
        int height = getSizeAtResolutionLevel(fullTileHeight, level);
        return getTileDim(width, height);
    }

    static Dimension getDimAtResolutionLevel(int fullWidth, int fullHeight, int level) {
        int width = getSizeAtResolutionLevel(fullWidth, level);
        int height = getSizeAtResolutionLevel(fullHeight, level);
        return new Dimension(width, height);
    }

    /**
     * Computes a new size at a given resolution level in the style of JPEG2000.
     *
     * @param fullSize the full size
     * @param level    the resolution level
     * @return the reduced size at the given level
     */
    static int getSizeAtResolutionLevel(int fullSize, int level) {
        int size = fullSize >> level;
        int sizeTest = size << level;
        if (sizeTest < fullSize) {
            size++;
        }
        return size;
    }

    static Dimension getTileDim(int width, int height) {
        return new Dimension(width < S2Config.DEFAULT_JAI_TILE_SIZE ? width : S2Config.DEFAULT_JAI_TILE_SIZE,
                             height < S2Config.DEFAULT_JAI_TILE_SIZE ? height : S2Config.DEFAULT_JAI_TILE_SIZE);
    }
}
