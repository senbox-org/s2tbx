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

package org.esa.s2tbx.dataio.s2.l1b;

import com.bc.ceres.core.Assert;
import com.bc.ceres.glevel.MultiLevelModel;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.jp2.AEmptyListener;
import org.esa.s2tbx.dataio.jp2.CodeStreamUtils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2TileOpImage;
import org.esa.snap.util.ImageUtils;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.FileUtils;
import org.geotools.geometry.Envelope2D;

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
import java.awt.*;
import java.awt.image.DataBufferUShort;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Norman Fomferra
 */
class L1bTileOpImage extends S2TileOpImage {

    protected final Logger logger;

    static PlanarImage create(File imageFile,
                              File cacheDir,
                              Point imagePos,
                              TileLayout l1bTileLayout,
                              TileLayout[] tileLayouts,
                              MultiLevelModel imageModel,
                              S2SpatialResolution spatialResolution,
                              int level) {

        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1bTileLayout, "imageLayout");
        Assert.notNull(imageModel, "imageModel");
        Assert.notNull(spatialResolution, "spatialResolution");

        if (imageFile != null) {
            SystemUtils.LOG.fine("Image layout: " + l1bTileLayout);

            return new L1bTileOpImage(imageFile, cacheDir, imagePos, l1bTileLayout, imageModel, level);
        } else {
            SystemUtils.LOG.warning("Using empty image !");

            int targetWidth = S2TileOpImage.getSizeAtResolutionLevel(tileLayouts[0].width, level);
            int targetHeight = S2TileOpImage.getSizeAtResolutionLevel(tileLayouts[0].height, level);
            Dimension targetTileDim = S2TileOpImage.getTileDimAtResolutionLevel(tileLayouts[0].tileWidth, tileLayouts[0].tileHeight, level);
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
        SystemUtils.LOG.fine("SourceImage:" + sourceImage.getWidth() + ", " + sourceImage.getHeight());
        SystemUtils.LOG.fine("TargetImage:" + sceneEnvelope.getWidth() + ", " + sceneEnvelope.getHeight());

        int targetWidth = L1bTileOpImage.getSizeAtResolutionLevel((int) (sceneEnvelope.getWidth() / (resolution.resolution)), level);
        int targetHeight = L1bTileOpImage.getSizeAtResolutionLevel((int) (sceneEnvelope.getHeight() / (resolution.resolution)), level);


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

    L1bTileOpImage(File imageFile,
                   File cacheDir,
                   Point imagePos,
                   TileLayout l1bTileLayout,
                   MultiLevelModel imageModel,
                   int level) {
        super(imageFile,
              cacheDir,
              imagePos,
              l1bTileLayout,
              imageModel,
              level);

        Assert.notNull(imageFile, "imageFile");
        Assert.notNull(cacheDir, "cacheDir");
        Assert.notNull(l1bTileLayout, "l1bTileLayout");
        Assert.notNull(imageModel, "imageModel");

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

        TileLayout myLayout;

        int realInternalJpegIndex;
        try {
            myLayout = CodeStreamUtils.getTileLayout(S2Config.OPJ_INFO_EXE, imageFile.toURI(), new AEmptyListener(), S2Config.NODUMP);
            realInternalJpegIndex = myLayout.numXTiles * myLayout.numYTiles;
        } catch (Exception iae) {
            Arrays.fill(tileData, S2Config.FILL_CODE_MOSAIC_BG);
            return;
        }

        Collections.addAll(S2L1bConfig.REAL_TILE_LAYOUT, tileLayout);

        if (!S2L1bConfig.REAL_TILE_LAYOUT.contains(myLayout)) {
            logger.severe(String.format("Unexpected signature of %s : %s", imageFile.getName(), myLayout.toString()));
            S2L1bConfig.REAL_TILE_LAYOUT.add(myLayout);
        }

        final Dimension jp2TileDim = S2TileOpImage.getDimAtResolutionLevel(tileLayout.tileWidth, tileLayout.tileHeight, getLevel());

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

        final File outputFile0 = getFirstComponentOutputFile(outputFile);
        // todo - outputFile0 may have already been created, although 'opj_decompress' has not finished execution.
        //        This may be the reason for party filled tiles, that sometimes occur
        if (!outputFile0.exists()) {
            int tileIndex = tileLayout.numXTiles * jp2TileY + jp2TileX;
            if (tileIndex >= realInternalJpegIndex) {
                Arrays.fill(tileData, S2Config.FILL_CODE_MOSAIC_BG);
                return;
            }

            logger.log(Level.parse(S2Config.LOG_JPEG), String.format("Jp2ExeImage.readTileData(): recomputing res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));
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
            logger.finer(String.format("Jp2ExeImage.readTileData(): reading res=%d, tile=(%d,%d)\n", getLevel(), jp2TileX, jp2TileY));
            readTileData(outputFile0, tileX, tileY, tileWidth, tileHeight, jp2TileX, jp2TileY, jp2TileWidth, jp2TileHeight, tileData, destRect);
        } catch (IOException e) {
            logger.severe("Failed to read uncompressed file data: " + Utils.getStackTrace(e));
        }
    }

    private File getFirstComponentOutputFile(File outputFile) {
        return FileUtils.exchangeExtension(outputFile, "_0.pgx");
    }
}
