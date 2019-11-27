package org.esa.s2tbx.dataio.ikonos.internal;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.s2tbx.dataio.readers.TileLayout;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.image.TileImageDisposer;

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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class IkonosMultiLevelSource extends AbstractMultiLevelSource {

    private static final Logger logger = Logger.getLogger(IkonosMultiLevelSource.class.getName());

    private final TileLayout tileLayout;
    private final TileImageDisposer tileManager;
    private int dataType;
    private final GeoTiffImageReader geoTiffImageReader;
    private final Point productSubsetOffset;

    public IkonosMultiLevelSource(GeoTiffImageReader geoTiffImageReader, int dataType, Point productSubsetOffset, int imageWidth, int imageHeight, Dimension tileSize, GeoCoding geoCoding) {
        super(new DefaultMultiLevelModel(Product.findImageToModelTransform(geoCoding), imageWidth, imageHeight));

        this.geoTiffImageReader = geoTiffImageReader;
        this.productSubsetOffset = productSubsetOffset;

        this.dataType = dataType;

        int numTilesX = imageWidth / tileSize.width;
        if (imageWidth % tileSize.width != 0) {
            numTilesX++;
        }
        int numTilesY = imageHeight / tileSize.height;
        if (imageHeight % tileSize.height != 0) {
            numTilesY++;
        }

        int levelCount = getModel().getLevelCount();
        this.tileLayout = new TileLayout(imageWidth, imageHeight, tileSize.width, tileSize.height, numTilesX, numTilesY, levelCount);
        this.tileLayout.numBands = 1;

        this.tileManager = new TileImageDisposer();
    }

    @Override
    protected RenderedImage createImage(int level) {
        int tileCount = this.tileLayout.numXTiles * this.tileLayout.numYTiles;
        List<RenderedImage> tileImages = Collections.synchronizedList(new ArrayList<>(tileCount));
        double factor = 1.0 / Math.pow(2, level);
        for (int tileRowIndex = 0; tileRowIndex < tileLayout.numYTiles; tileRowIndex++) {
            for (int tileColumnIndex = 0; tileColumnIndex < tileLayout.numXTiles; tileColumnIndex++) {
                PlanarImage opImage = createTileImage(tileRowIndex, tileColumnIndex, level);
                if (opImage == null) {
                    opImage = ConstantDescriptor.create((float) tileLayout.tileWidth, (float) tileLayout.tileHeight, new Number[]{0}, null);
                } else {
                    this.tileManager.registerForDisposal(opImage);

                    float xTrans = (float) (tileColumnIndex * this.tileLayout.tileWidth * factor);
                    float yTrans = (float) (tileRowIndex * this.tileLayout.tileHeight * factor);
                    opImage = TranslateDescriptor.create(opImage, xTrans, yTrans, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
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
        RenderedOp mosaicOp = MosaicDescriptor.create(sources, MosaicDescriptor.MOSAIC_TYPE_OVERLAY, null, null, null, null, new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

        int fittingRectWidth = IkonosTileOpImage.computeValueAtResolutionLevel(this.tileLayout.width, level);
        int fittingRectHeight = IkonosTileOpImage.computeValueAtResolutionLevel(this.tileLayout.height, level);

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

    @Override
    public synchronized void reset() {
        super.reset();

        this.tileManager.disposeAll();
        System.gc();
    }

    private PlanarImage createTileImage(int tileRowIndex, int tileColumnIndex, int level) {
        return IkonosTileOpImage.create(this.geoTiffImageReader, tileRowIndex, tileColumnIndex, this.productSubsetOffset, this.tileLayout, getModel(), this.dataType, level);
    }
}
