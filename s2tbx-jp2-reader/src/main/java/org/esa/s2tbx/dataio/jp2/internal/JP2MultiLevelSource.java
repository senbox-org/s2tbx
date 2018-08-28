/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.jp2.internal;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;

import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A single banded multi-level image source for JP2 files.
 *
 * @author Cosmin Cara
 */
public class JP2MultiLevelSource extends AbstractMultiLevelSource {

    private final TileLayout tileLayout;
    private final Path sourceFile;
    private final Path cacheFolder;
    private final int dataType;
    private final Logger logger;
    private final int bandIndex;
    private final TileImageDisposer tileManager;
    private boolean directMode;

    /**
     * Constructs an instance of a single band multi-level image source
     *
     * @param jp2File     The original (i.e. compressed) JP2 file
     * @param cacheFolder The cache (temporary) folder
     * @param bandIndex   The destination Product band for which the image source is created
     * @param imageWidth  The width of the scene image
     * @param imageHeight The height of the scene image
     * @param tileWidth   The width of a JP2 tile composing the scene image
     * @param tileHeight  The height of a JP2 tile composing the scene image
     * @param numTilesX   The number of JP2 tiles in a row
     * @param numTilesY   The number of JP2 tiles in a column
     * @param levels      The number of resolutions found in the JP2 file
     * @param dataType    The pixel data type
     * @param geoCoding   (optional) The geocoding found (if any) in the JP2 header
     */
    public JP2MultiLevelSource(Path jp2File, Path cacheFolder, int bandIndex, int numBands, int imageWidth, int imageHeight,
                               int tileWidth, int tileHeight, int numTilesX, int numTilesY, int levels, int dataType,
                               GeoCoding geoCoding) {
        super(new DefaultMultiLevelModel(levels,
                                         Product.findImageToModelTransform(geoCoding),
                                         imageWidth, imageHeight));
        sourceFile = jp2File;
        this.cacheFolder = cacheFolder;
        this.dataType = dataType;
        logger = Logger.getLogger(JP2MultiLevelSource.class.getName());
        tileLayout = new TileLayout(imageWidth, imageHeight, tileWidth, tileHeight, numTilesX, numTilesY, levels);
        tileLayout.numBands = numBands;
        this.bandIndex = bandIndex;
        this.tileManager = new TileImageDisposer();
    }

    public void enableDirectMode(boolean enable) {
        directMode = enable;
    }
    /**
     * Creates a planar image corresponding of a tile identified by row and column, at the specified resolution.
     *
     * @param row   The row of the tile (0-based)
     * @param col   The column of the tile (0-based)
     * @param level The resolution level (0 = highest)
     */
    protected PlanarImage createTileImage(int row, int col, int level) throws IOException {
        TileLayout currentLayout = tileLayout;
        // the edge tiles dimensions may be less than the dimensions from JP2 header
        if (row == tileLayout.numYTiles - 1 || col == tileLayout.numXTiles - 1) {
            currentLayout = new TileLayout(tileLayout.width, tileLayout.height,
                                           Math.min(tileLayout.width - col * tileLayout.tileWidth, tileLayout.tileWidth),
                                           Math.min(tileLayout.height - row * tileLayout.tileHeight, tileLayout.tileHeight),
                                           tileLayout.numXTiles, tileLayout.numYTiles, tileLayout.numResolutions);
            currentLayout.numBands = tileLayout.numBands;
        }
        return JP2TileOpImage.create(sourceFile, cacheFolder, bandIndex, row, col, currentLayout, getModel(), dataType, level, directMode);
    }

    @Override
    protected RenderedImage createImage(int level) {
        final List<RenderedImage> tileImages = Collections.synchronizedList(new ArrayList<>(tileLayout.numXTiles * tileLayout.numYTiles));
        TileLayout layout = tileLayout;
        double factorX = 1.0 / Math.pow(2, level);
        double factorY = 1.0 / Math.pow(2, level);
        for (int x = 0; x < tileLayout.numYTiles; x++) {
            for (int y = 0; y < tileLayout.numXTiles; y++) {
                PlanarImage opImage;
                try {
                    opImage = createTileImage(x, y, level);
                    if (opImage != null) {
                        tileManager.registerForDisposal(opImage);
                        opImage = TranslateDescriptor.create(opImage,
                                                             (float) (y * layout.tileWidth * factorX),
                                                             (float) (x * layout.tileHeight * factorY),
                                                             Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                             null);
                    }
                } catch (IOException ex) {
                    opImage = ConstantDescriptor.create((float) layout.tileWidth, (float) layout.tileHeight, new Number[]{0}, null);
                }
                tileImages.add(opImage);
            }
        }
        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic");
            return null;
        }

        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setTileWidth(JAI.getDefaultTileSize().width);
        imageLayout.setTileHeight(JAI.getDefaultTileSize().height);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);

        RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                      MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                      null, null, null, null,
                                                      new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

        int fittingRectWidth = scaleValue(tileLayout.width, level);
        int fittingRectHeight = scaleValue(tileLayout.height, level);

        Rectangle fitRect = new Rectangle(0, 0, fittingRectWidth, fittingRectHeight);
        final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect, Math.pow(2.0, level));

        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

        if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
            int rightPad = destBounds.width - mosaicOp.getWidth();
            int bottomPad = destBounds.height - mosaicOp.getHeight();

            mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
        }
        return mosaicOp;
    }

    @Override
    public synchronized void reset() {
        super.reset();
        tileManager.disposeAll();
        System.gc();
    }

    private int scaleValue(int source, int level) {
        int size = source >> level;
        int sizeTest = size << level;
        if (sizeTest < source) {
            size++;
        }
        return size;
    }
}