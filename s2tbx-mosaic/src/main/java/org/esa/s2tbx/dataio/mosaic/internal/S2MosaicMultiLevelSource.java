package org.esa.s2tbx.dataio.mosaic.internal;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.s2tbx.dataio.readers.TileLayout;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.VirtualBandOpImage;
import org.esa.snap.rcp.statistics.StatisticsUtils;

import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A single banded multi-level image source for Sentinel2 products
 *
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
public class S2MosaicMultiLevelSource extends AbstractMultiLevelSource {
    private final Band[]sourceBands;
    private final int imageWidth;
    private final int imageHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final int dataType;
    private final String mosaicType;
    private ImageLayout imageLayout;
    private final Logger logger;
    private final double originX;
    private final double originY;
    private final int productType;
    public S2MosaicMultiLevelSource(Band[] sourceBands, int imageWidth, int imageHeight,
                                    int tileWidth, int tileHeight, int levels, int dataType,
                                    AffineTransform transform, String mosaicType, double originX, double originY, int productType) {
        super(new DefaultMultiLevelModel(levels,
                transform,
                imageWidth, imageHeight));
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sourceBands = sourceBands;
        this.dataType = dataType;
        this.mosaicType = mosaicType;
        this.originX = originX;
        this.originY = originY;
        this.productType = productType;

        this.logger = Logger.getLogger(S2MosaicMultiLevelSource.class.getName());
    }

    /**
     * Creates a planar image corresponding of a tile identified by row and column, at the specified resolution.
     *
     * @param bandIndex   The index of the sourceBand (0-based)
     *
     */
    protected PlanarImage createTileImage(int bandIndex, int level) throws IOException {

        return (PlanarImage) sourceBands[bandIndex].getSourceImage().getImage(level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        double factorX = 1.0 / Math.pow(2, level);
        double factorY = 1.0 / Math.pow(2, level);
        final List<RenderedImage> tileImages = Collections.synchronizedList(new ArrayList<>(this.sourceBands.length));

        for (int index = 0; index < this.sourceBands.length; index++) {
            PlanarImage opImage = null;
            try {
                opImage = createTileImage(index, level);
                if (opImage != null) {
                    final double sourceBandOriginX  = sourceBands[index].getSourceImage().getModel().getImageToModelTransform(0).getTranslateX();
                    final double sourceBandOriginY =  sourceBands[index].getSourceImage().getModel().getImageToModelTransform(0).getTranslateY();
                    final double sourceBandStepSize = sourceBands[index].getSourceImage().getModel().getImageToModelTransform(0).getScaleX();
                    opImage = TranslateDescriptor.create(opImage,
                            (float)(Math.abs((sourceBandOriginX - this.originX)/sourceBandStepSize)*factorX),
                            (float)(Math.abs((sourceBandOriginY - this.originY)/sourceBandStepSize)*factorY),
                            Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                            null);
                }
            } catch (IOException e) {
                opImage = ConstantDescriptor.create((float) tileWidth, (float) tileHeight, new Number[]{0}, null);
            }
            tileImages.add(opImage);
        }
        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic");
            return null;
        }

        this.imageLayout = new ImageLayout();
        this.imageLayout.setMinX(0);
        this.imageLayout.setMinY(0);
        this.imageLayout.setTileWidth(JAI.getDefaultTileSize().width);
        this.imageLayout.setTileHeight(JAI.getDefaultTileSize().height);
        this.imageLayout.setTileGridXOffset(0);
        this.imageLayout.setTileGridYOffset(0);
        RenderedOp mosaicOp = null;
        if(this.productType==1)
        {
            switch(this.mosaicType) {
                case "MOSAIC_TYPE_BLEND":
                    mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                            MosaicDescriptor.MOSAIC_TYPE_BLEND,
                            null, null, new double[][]{{-1.0}}, null,
                            new RenderingHints(JAI.KEY_IMAGE_LAYOUT, this.imageLayout));
                    break;
                case "MOSAIC_TYPE_OVERLAY":
                    mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                            MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                            null, null, new double[][]{{-1.0}}, null,
                            new RenderingHints(JAI.KEY_IMAGE_LAYOUT, this.imageLayout));
                    break;
                default:
                    throw new IllegalArgumentException("Mosaic type not accepted");
            }
        }else {
            switch (this.mosaicType) {
                case "MOSAIC_TYPE_BLEND":
                    mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                            MosaicDescriptor.MOSAIC_TYPE_BLEND,
                            null, null, null, null,
                            new RenderingHints(JAI.KEY_IMAGE_LAYOUT, this.imageLayout));
                    break;
                case "MOSAIC_TYPE_OVERLAY":
                    mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                            MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                            null, null, null, null,
                            new RenderingHints(JAI.KEY_IMAGE_LAYOUT, this.imageLayout));
                    break;
                default:
                    throw new IllegalArgumentException("Mosaic type not accepted");
            }
        }
        final int fittingRectWidth = scaleValue(this.imageWidth, level);
        final int fittingRectHeight = scaleValue(this.imageHeight, level);

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
