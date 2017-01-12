package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.s2tbx.dataio.readers.TileLayout;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;

import javax.media.jai.*;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *  A single banded multi-level image source for products imported with the GDAL library.
 *
 * @author Jean Coravu
 */
class GDALMultiLevelSource extends AbstractMultiLevelSource {
    private static final Logger logger = Logger.getLogger(GDALMultiLevelSource.class.getName());

    private final TileLayout tileLayout;
    private final Path sourceFile;
    private final int dataBufferType;
    private final int bandIndex;

    GDALMultiLevelSource(Path sourceFile, int bandIndex, int numBands, int imageWidth, int imageHeight,
                                int tileWidth, int tileHeight, int levels, int dataBufferType, GeoCoding geoCoding) {

        super(new DefaultMultiLevelModel(levels, Product.findImageToModelTransform(geoCoding), imageWidth, imageHeight));

        this.sourceFile = sourceFile;
        this.dataBufferType = dataBufferType;
        this.bandIndex = bandIndex;

        int numTilesX = imageWidth / tileWidth;
        if (imageWidth % tileWidth != 0) {
            numTilesX++;
        }
        int numTilesY = imageHeight / tileHeight;
        if (imageHeight % tileHeight != 0) {
            numTilesY++;
        }
        this.tileLayout = new TileLayout(imageWidth, imageHeight, tileWidth, tileHeight, numTilesX, numTilesY, levels);
        this.tileLayout.numBands = numBands;
    }

    @Override
    protected RenderedImage createImage(int level) {
        int tileCount = this.tileLayout.numXTiles * this.tileLayout.numYTiles;
        List<RenderedImage> tileImages = Collections.synchronizedList(new ArrayList<>(tileCount));
        double factorX = 1.0 / Math.pow(2, level);
        double factorY = 1.0 / Math.pow(2, level);
        for (int x = 0; x < tileLayout.numYTiles; x++) {
            for (int y = 0; y < tileLayout.numXTiles; y++) {
                PlanarImage opImage;
                opImage = createTileImage(x, y, level);
                if (opImage != null) {
                    float xTrans = (float) (y * this.tileLayout.tileWidth * factorX);
                    float yTrans = (float) (x * this.tileLayout.tileHeight * factorY);
                    opImage = TranslateDescriptor.create(opImage, xTrans, yTrans, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                } else {
                    opImage = ConstantDescriptor.create((float) tileLayout.tileWidth, (float) tileLayout.tileHeight, new Number[]{0}, null);
                }
                tileImages.add(opImage);
            }
        }
        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic.");
            return null;
        }

        Dimension defaultTileSize = JAI.getDefaultTileSize();
        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setTileWidth(defaultTileSize.width);
        imageLayout.setTileHeight(defaultTileSize.height);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);

        RenderedImage[] sources = tileImages.toArray(new RenderedImage[tileImages.size()]);
        RenderedOp mosaicOp = MosaicDescriptor.create(sources, MosaicDescriptor.MOSAIC_TYPE_OVERLAY, null, null, null, null,
                                                      new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

        int fittingRectWidth = GDALTileOpImage.scaleValue(this.tileLayout.width, level);
        int fittingRectHeight = GDALTileOpImage.scaleValue(this.tileLayout.height, level);

        Rectangle fitRect = new Rectangle(0, 0, fittingRectWidth, fittingRectHeight);
        Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect, Math.pow(2.0, level));

        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

        if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
            int rightPad = destBounds.width - mosaicOp.getWidth();
            int bottomPad = destBounds.height - mosaicOp.getHeight();

            mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
        }
        return mosaicOp;
    }

    private PlanarImage createTileImage(int row, int col, int level) {
        return GDALTileOpImage.create(this.sourceFile, this.bandIndex, row, col, tileLayout, getModel(), this.dataBufferType, level);
    }
}
