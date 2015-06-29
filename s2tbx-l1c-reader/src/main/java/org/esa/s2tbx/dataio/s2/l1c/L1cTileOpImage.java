/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l1c;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import jp2.AEmptyListener;
import jp2.CodeStreamUtils;
import jp2.TileLayout;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2TileOpImage;
import org.esa.snap.util.ImageUtils;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.FileUtils;
import org.geotools.geometry.Envelope2D;
import org.openjpeg.CommandOutput;
import org.openjpeg.JpegUtils;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// todo - better log problems during read process, see {@report "Problem detected..."} code marks

/**
 * @author Norman Fomferra
 */
class L1cTileOpImage extends S2TileOpImage  {

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
    private final TileLayout l1cTileLayout;
    private Map<File, Jp2File> openFiles;
    private Map<File, Object> locks;

    static PlanarImage create(File imageFile,
                              File cacheDir,
                              Point imagePos,
                              TileLayout l1cTileLayout,
                              TileLayout[] tileLayouts,
                              MultiLevelModel imageModel,
                              S2SpatialResolution spatialResolution,
                              int level) {

        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1cTileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");
        Assert.notNull(spatialResolution, "spatialResolution");

        if (imageFile != null) {
            SystemUtils.LOG.fine("Image layout: " + l1cTileLayout);

            return new L1cTileOpImage(imageFile, cacheDir, imagePos, l1cTileLayout, imageModel, level);
        } else {
            SystemUtils.LOG.fine("Using empty image !");

            int targetWidth = getSizeAtResolutionLevel(tileLayouts[0].width, level);
            int targetHeight = getSizeAtResolutionLevel(tileLayouts[0].height, level);
            Dimension targetTileDim = getTileDimAtResolutionLevel(tileLayouts[0].tileWidth, tileLayouts[0].tileHeight, level);
            SampleModel sampleModel = ImageUtils.createSingleBandedSampleModel(S2Config.SAMPLE_DATA_BUFFER_TYPE, targetWidth, targetHeight);
            ImageLayout imageLayout = new ImageLayout(0, 0, targetWidth, targetHeight, 0, 0, targetTileDim.width, targetTileDim.height, sampleModel, null);
            return ConstantDescriptor.create((float) imageLayout.getWidth(null),
                                             (float) imageLayout.getHeight(null),
                                             new Short[]{S2Config.FILL_CODE_NO_FILE},
                                             new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
        }
    }

    static PlanarImage createGenericScaledImage(PlanarImage sourceImage, Envelope2D sceneEnvelope, S2SpatialResolution resolution, int level, boolean forceResize) {
        SystemUtils.LOG.fine("Asking for scaled mosaic image: " + resolution.toString());
        SystemUtils.LOG.warning("SourceImage:" + sourceImage.getWidth() + ", " + sourceImage.getHeight());

        SystemUtils.LOG.severe("ForceResize:" + forceResize);

        int targetResolution = resolution.resolution;

        if(forceResize)
        {
            targetResolution = S2SpatialResolution.R10M.resolution;
        }

        int targetWidth = L1cTileOpImage.getSizeAtResolutionLevel((int) (sceneEnvelope.getWidth() / (targetResolution)), level);
        int targetHeight = L1cTileOpImage.getSizeAtResolutionLevel((int) (sceneEnvelope.getHeight() / (targetResolution)), level);


        float scaleX = targetWidth / ((float) sourceImage.getWidth());
        float scaleY = targetHeight / ((float) sourceImage.getHeight());

        if(!forceResize)
        {
            scaleX = (float) 1.0;
            scaleY = (float) 1.0;
        }

        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_ZERO);
        RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                           borderExtender);

        RenderedOp scaledImage = ScaleDescriptor.create(sourceImage,
                                                        scaleX,
                                                        scaleY,
                                                        sourceImage.getMinX() - sourceImage.getMinX() * scaleX,
                                                        sourceImage.getMinY() - sourceImage.getMinY() * scaleY,
                                                        Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                        renderingHints);

        SystemUtils.LOG.fine(String.format("After scaling: (%d, %d)", scaledImage.getWidth(), scaledImage.getHeight()));

        if (scaledImage.getWidth() == targetWidth && scaledImage.getHeight() == targetHeight) {
            return scaledImage;
        } else if (scaledImage.getWidth() >= targetWidth || scaledImage.getHeight() >= targetHeight) {
            SystemUtils.LOG.fine(String.format("Cropping: (%d, %d), (%d, %d)", scaledImage.getWidth(), targetWidth, scaledImage.getHeight(), targetHeight));

            return CropDescriptor.create(scaledImage,
                                         (float) sourceImage.getMinX(),
                                         (float) sourceImage.getMinY(),
                                         (float) targetWidth,
                                         (float) targetHeight,
                                         null);
        } else if (scaledImage.getWidth() <= targetWidth && scaledImage.getHeight() <= targetHeight) {
            int rightPad = targetWidth - scaledImage.getWidth();
            int bottomPad = targetHeight - scaledImage.getHeight();
            SystemUtils.LOG.fine(String.format("Border: (%d, %d), (%d, %d)", scaledImage.getWidth(), targetWidth, scaledImage.getHeight(), targetHeight));

            return BorderDescriptor.create(scaledImage, 0, rightPad, 0, bottomPad, borderExtender, null);
        } else {
            throw new IllegalStateException();
        }
    }

    L1cTileOpImage(File imageFile,
                   File cacheDir,
                   Point imagePos,
                   TileLayout l1cTileLayout,
                   MultiLevelModel imageModel,
                   int level) {
        super(imagePos,
              l1cTileLayout,
              imageModel,
              level);

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1cTileLayout, "l1cTileLayout");
        Assert.notNull(imageModel, "imageModel");

        this.imageFile = imageFile;
        this.cacheDir = cacheDir;
        this.l1cTileLayout = l1cTileLayout;
        this.openFiles = new HashMap<>();
        this.locks = new HashMap<>();
        this.logger = SystemUtils.LOG;
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
            logger.severe(Utils.getStackTrace(e));
        }

        TileLayout myLayout = null;
        try {
            myLayout = CodeStreamUtils.getTileLayout(S2Config.OPJ_INFO_EXE, imageFile.toURI(), new AEmptyListener(), S2Config.NODUMP);
        } catch (Exception iae) {
            Arrays.fill(tileData, S2Config.FILL_CODE_MOSAIC_BG);
            return;
        }

        final File outputFile0 = getFirstComponentOutputFile(outputFile);
        // todo - outputFile0 may have already been created, although 'opj_decompress' has not finished execution.
        //        This may be the reason for party filled tiles, that sometimes occur
        if (!outputFile0.exists()) {
            logger.finest(String.format("Jp2ExeImage.readTileData(): recomputing res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));
            try {
                decompressTile(outputFile, jp2TileX, jp2TileY);
            } catch (IOException e) {
                logger.severe("opj_decompress process failed! :" + Utils.getStackTrace(e));
                if (outputFile0.exists() && !outputFile0.delete()) {
                    logger.severe("Failed to delete file: " + outputFile0.getAbsolutePath());
                }
            }
            if (!outputFile0.exists()) {
                Arrays.fill(tileData, S2Config.FILL_CODE_NO_FILE);
                return;
            }
        }

        try {
            logger.finest(String.format("Jp2ExeImage.readTileData(): reading res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));
            readTileData(outputFile0, tileX, tileY, tileWidth, tileHeight, jp2TileX, jp2TileY, jp2TileWidth, jp2TileHeight, tileData, destRect);
        } catch (IOException e) {
            logger.severe("Failed to read uncompressed file data: " + Utils.getStackTrace(e));
        }
    }

    private File getFirstComponentOutputFile(File outputFile) {
        return FileUtils.exchangeExtension(outputFile, "_0.pgx");
    }

    private void decompressTile(final File outputFile, int jp2TileX, int jp2TileY) throws IOException {
        final int tileIndex = l1cTileLayout.numXTiles * jp2TileY + jp2TileX;

        ProcessBuilder builder;
        if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
            String inputFileName = Utils.GetIterativeShortPathName(imageFile.getPath());
            String outputFileName = outputFile.getPath();

            if (inputFileName.length() == 0) {
                inputFileName = imageFile.getPath();
            }

            builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                                         "-i", inputFileName,
                                         "-o", outputFileName,
                                         "-r", getLevel() + "",
                                         "-t", tileIndex + "");
        } else {
            logger.fine("Writing to " + outputFile.getPath());

            builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                                         "-i", imageFile.getPath(),
                                         "-o", outputFile.getPath(),
                                         "-r", getLevel() + "",
                                         "-t", tileIndex + "");
        }

        builder = builder.directory(cacheDir);

        try {
            CommandOutput result = JpegUtils.runProcess(builder);

            final int exitCode = result.getErrorCode();
            if (exitCode != 0) {
                logger.severe(String.format("Failed to uncompress tile: %s, exitCode = %d, command = [%s], command stdoutput = [%s], command stderr = [%s]", imageFile.getPath(), exitCode, builder.command().toString(), result.getTextOutput(), result.getErrorOutput()));
            }
        } catch (InterruptedException e) {
            logger.severe("Process was interrupted, InterruptedException: " + e.getMessage());
        }
    }

    @Override
    public synchronized void dispose() {

        for (Map.Entry<File, Jp2File> entry : openFiles.entrySet()) {
            logger.finest("closing " + entry.getKey());
            try {
                final Jp2File jp2File = entry.getValue();
                if (jp2File.stream != null) {
                    jp2File.stream.close();
                    jp2File.stream = null;
                }
            } catch (IOException e) {
                logger.severe("Failed to close stream: " + Utils.getStackTrace(e));
            }
        }

        for (File file : openFiles.keySet()) {
            logger.fine("Deleting " + file);
            if (!file.delete()) {
                logger.severe("Failed to delete file! :" + file.getAbsolutePath());
            }
        }

        openFiles.clear();

        if (!cacheDir.delete()) {
            logger.severe("Failed to delete cache dir! :" + cacheDir.getAbsolutePath());
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
                logger.fine(String.format("%s: tile=(%d,%d): jp2FileRect=%s, tileRect=%s, intersection=%s\n", jp2File.file, tileX, tileY, jp2FileRect, tileRect, intersection));
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
