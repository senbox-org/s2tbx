/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.openjpeg.CommandOutput;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.FileUtils;

import javax.imageio.stream.FileImageInputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.*;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.logging.Logger;


/**
 * @author Norman Fomferra
 * @author Nicolas Ducoin
 */
public class S2TileOpImage extends SingleBandedOpImage {

    protected TileLayout tileLayout;
    protected File imageFile;
    protected File cacheDir;

    public S2TileOpImage(
            File imageFile,
            File cacheDir,
            Point imagePos,
            TileLayout tileLayout,
            MultiLevelModel imageModel,
            int level) {
        super(S2Config.SAMPLE_DATA_BUFFER_TYPE,
                imagePos,
                tileLayout.width,
                tileLayout.height,
                getTileDimAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, level),
                null,
                ResolutionLevel.create(imageModel, level));

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(tileLayout, "tileLayout");
        Assert.notNull(imageModel, "imageModel");

        this.logger = SystemUtils.LOG;

        this.imageFile = imageFile;
        this.cacheDir = cacheDir;
        this.tileLayout = tileLayout;
    }


    protected final Logger logger;

    public static PlanarImage create(File imageFile,
                                     File cacheDir,
                                     Point imagePos,
                                     TileLayout tileLayout,
                                     S2Config config,
                                     MultiLevelModel imageModel,
                                     S2SpatialResolution productResolution,
                                     int level) {

        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(tileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");

        if (imageFile != null) {
            SystemUtils.LOG.fine("Image layout: " + tileLayout);

            return new S2TileOpImage(imageFile, cacheDir, imagePos, tileLayout, imageModel, level);
        } else {
            SystemUtils.LOG.fine("Using empty image !");


            TileLayout tileLaoutForProductResolution = config.getTileLayout(productResolution);
            int targetWidth = getSizeAtResolutionLevel(tileLaoutForProductResolution.width, level);
            int targetHeight = getSizeAtResolutionLevel(tileLaoutForProductResolution.height, level);
            Dimension targetTileDim = getTileDimAtResolutionLevel(tileLaoutForProductResolution.tileWidth, tileLaoutForProductResolution.tileHeight, level);
            SampleModel sampleModel = ImageUtils.createSingleBandedSampleModel(S2Config.SAMPLE_DATA_BUFFER_TYPE, targetWidth, targetHeight);
            ImageLayout imageLayout = new ImageLayout(0, 0, targetWidth, targetHeight, 0, 0, targetTileDim.width, targetTileDim.height, sampleModel, null);
            return ConstantDescriptor.create((float) imageLayout.getWidth(null),
                    (float) imageLayout.getHeight(null),
                    new Short[]{S2Config.FILL_CODE_NO_FILE},
                    new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
        }
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

        final Dimension jp2TileDim = getDimAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, getLevel());

        final int jp2TileWidth = jp2TileDim.width;
        final int jp2TileHeight = jp2TileDim.height;
        final int jp2TileX = destRect.x / jp2TileWidth;
        final int jp2TileY = destRect.y / jp2TileHeight;

        File outputFile = null;
        try {
            outputFile = new File(cacheDir,
                    FileUtils.exchangeExtension(imageFile.getName(),
                            String.format("_R%d_TX%d_TY%d.pgx",
                                    getLevel(), jp2TileX, jp2TileY)));
        } catch (Exception e) {
            logger.severe(Utils.getStackTrace(e));
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

    protected static Dimension getTileDimAtResolutionLevel(int fullTileWidth, int fullTileHeight, int level) {
        int width = getSizeAtResolutionLevel(fullTileWidth, level);
        int height = getSizeAtResolutionLevel(fullTileHeight, level);
        return getTileDim(width, height);
    }


    protected static Dimension getDimAtResolutionLevel(int fullWidth, int fullHeight, int level) {
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
    protected static int getSizeAtResolutionLevel(int fullSize, int level) {
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


    protected void decompressTile(final File outputFile, int jp2TileX, int jp2TileY) throws IOException {
        final int tileIndex = tileLayout.numXTiles * jp2TileY + jp2TileX;

        ProcessBuilder builder;

        if (S2Config.OPJ_DECOMPRESSOR_EXE != null) {
            if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
                String inputFileName = Utils.GetIterativeShortPathNameW(imageFile.getPath());
                String outputFileName = outputFile.getPath();

                String outputNameShort = Utils.GetIterativeShortPathNameW(outputFile.getParent()) + File.separator + outputFile.getName();

                if (inputFileName.length() == 0) {
                    inputFileName = imageFile.getPath();
                }

                Guardian.assertTrue("Image file exists", new File(inputFileName).exists());

                builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                        "-i", inputFileName,
                        "-o", outputNameShort,
                        "-r", getLevel() + "",
                        "-t", tileIndex + "",
                        "-threads", "ALL_CPUS");
            } else {
                SystemUtils.LOG.fine("Writing to " + outputFile.getPath());

                Guardian.assertTrue("Image file exists", imageFile.exists());

                builder = new ProcessBuilder(S2Config.OPJ_DECOMPRESSOR_EXE,
                        "-i", imageFile.getPath(),
                        "-o", outputFile.getPath(),
                        "-r", getLevel() + "",
                        "-t", tileIndex + "",
                        "-threads", "ALL_CPUS" );
            }
        } else {
            throw new UnexpectedException("OpenJpeg decompressor is not set");
        }

        builder = builder.directory(cacheDir);

        try {
            builder.redirectErrorStream(true);
            CommandOutput result = OpenJpegUtils.runProcess(builder);

            final int exitCode = result.getErrorCode();
            if (exitCode != 0) {
                SystemUtils.LOG.severe(String.format("Failed to uncompress tile: %s, exitCode = %d, command = [%s], command stdoutput = [%s], command stderr = [%s]", imageFile.getPath(), exitCode, builder.command().toString(), result.getTextOutput(), result.getErrorOutput()));
            }
        } catch (InterruptedException e) {
            SystemUtils.LOG.severe("Process was interrupted, InterruptedException: " + e.getMessage());
        }
    }


    @Override
    public synchronized void dispose() {
//        if (!cacheDir.delete()) {
//            SystemUtils.LOG.severe("Failed to delete cache dir! :" + cacheDir.getAbsolutePath());
//        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    protected void readTileData(File outputFile,
                                int tileX, int tileY,
                                int tileWidth, int tileHeight,
                                int jp2TileX, int jp2TileY,
                                int jp2TileWidth, int jp2TileHeight,
                                short[] tileData,
                                Rectangle destRect) throws IOException {

        // todo - we still have a synchronisation problem here: often zero areas are generated in a tile.
        // This does not happen, if we synchronise entire computeRect() on the instance, but it is less efficient.
        try (FileImageInputStream fis = new FileImageInputStream(outputFile)) {
            int jp2Width, jp2Height;
            final String[] tokens = fis.readLine().split(" ");
            long dataPos = fis.getStreamPosition();
            if (tokens.length != 6) {
                throw new IOException("Unexpected PGX tile image format");
            }

            // String pg = tokens[0];   // PG
            // String ml = tokens[1];   // ML
            // String plus = tokens[2]; // +
            try {
                // int jp2File.nbits = Integer.parseInt(tokens[3]);
                jp2Width = Integer.parseInt(tokens[4]);
                jp2Height = Integer.parseInt(tokens[5]);
            } catch (NumberFormatException e) {
                throw new IOException("Unexpected PGX tile image format");
            }
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

            if (jp2X == 0 && jp2Width == tileWidth
                    && jp2Y == 0 && jp2Height == tileHeight
                    && tileWidth * tileHeight == tileData.length) {
                fis.seek(dataPos);
                fis.readFully(tileData, 0, tileData.length);
            } else {
                final Rectangle jp2FileRect = new Rectangle(0, 0, jp2Width, jp2Height);
                final Rectangle tileRect = new Rectangle(jp2X,
                        jp2Y,
                        tileWidth, tileHeight);
                final Rectangle intersection = jp2FileRect.intersection(tileRect);
                if (!intersection.isEmpty()) {
                    SystemUtils.LOG.fine(String.format("%s: tile=(%d,%d): jp2FileRect=%s, tileRect=%s, intersection=%s\n", outputFile, tileX, tileY, jp2FileRect, tileRect, intersection));
                    long seekPos = dataPos + S2Config.SAMPLE_BYTE_COUNT * (intersection.y * jp2Width + intersection.x);
                    int tilePos = 0;
                    for (int y = 0; y < intersection.height; y++) {
                        fis.seek(seekPos);
                        fis.readFully(tileData, tilePos, intersection.width);
                        seekPos += S2Config.SAMPLE_BYTE_COUNT * jp2Width;
                        tilePos += tileWidth;
                        for (int x = intersection.width; x < tileWidth; x++) {
                            tileData[y * tileWidth + x] = S2Config.FILL_CODE_OUT_OF_X_BOUNDS;
                        }
                    }

                    for (int y = intersection.height; y < tileHeight; y++) {
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

}
