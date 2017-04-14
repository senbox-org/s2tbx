package org.esa.s2tbx.dataio.mosaic.internal;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;

import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.CompositeDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A single banded multi-level image source for Sentinel2 products
 *
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
public final class S2MosaicMultiLevelSource extends AbstractMultiLevelSource {
    private final Band[] sourceBands;
    private final GeoCoding targetGeoCoding;
    private final int imageWidth;
    private final int imageHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final String mosaicType;
    private final Logger logger;
    private final int productType;

    public S2MosaicMultiLevelSource(Band[] sourceBands, int imageWidth, int imageHeight,
                                    int tileWidth, int tileHeight, int levels,
                                    GeoCoding geoCoding, String mosaicType, int productType) {
        super(new DefaultMultiLevelModel(levels,
                                         Product.findImageToModelTransform(geoCoding),
                                         imageWidth, imageHeight));
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sourceBands = sourceBands;
        this.targetGeoCoding = geoCoding;
        this.mosaicType = mosaicType;
        this.productType = productType;

        this.logger = Logger.getLogger(S2MosaicMultiLevelSource.class.getName());
    }

    /**
     * Creates a planar image corresponding of source band, at the specified resolution.
     *
     * @param bandIndex   The index of the sourceBand (0-based)
     *
     */
    protected PlanarImage createTileImage(final int bandIndex, final int level) throws IOException {
        return (PlanarImage) this.sourceBands[bandIndex].getSourceImage().getImage(level);
    }

    @Override
    protected RenderedImage createImage(int level) {

        double scaleFactor = 1.0 / Math.pow(2, level);
        final List<RenderedImage> tileImages = Collections.synchronizedList(new ArrayList<>(this.sourceBands.length));

        for (int index = 0; index < this.sourceBands.length; index++) {
            PlanarImage opImage;
            try {
                opImage = createTileImage(index, level);
                if (opImage != null) {
                    //compute the origin of the source bands so that it can be determined where
                    // to place them in relation to the target product
                    final PixelPos sourceBandOriginPos = getSourceOriginPixelPosition(sourceBands[index]);
                    float offsetX = (float) (sourceBandOriginPos.getX() * scaleFactor);
                    float offsetY = (float) (sourceBandOriginPos.getY() * scaleFactor);
                    opImage = TranslateDescriptor.create(opImage, offsetX, offsetY,
                                                         Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                                         null);
                }
            } catch (IOException e) {
                opImage = ConstantDescriptor.create((float) this.tileWidth, (float) this.tileHeight, new Number[]{0}, null);
            }
            tileImages.add(opImage);
        }
        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic");
            return null;
        }

        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setWidth(imageWidth);
        imageLayout.setHeight(imageHeight);
        imageLayout.setTileWidth(JAI.getDefaultTileSize().width);
        imageLayout.setTileHeight(JAI.getDefaultTileSize().height);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);
        RenderedOp mosaicOp;

        final int sourceCount = tileImages.size();
        double[][] thresholds = new double[sourceCount][1];
        for (int i = 0; i < sourceCount; i++) {
            thresholds[i][0] = 0.0;
        }
        switch(this.mosaicType) {
            case "MOSAIC_TYPE_BLEND":
                List<RenderedImage> newImages = new ArrayList<>();
                for (int i = 0; i < sourceCount; i++) {
                    RenderedImage source = tileImages.get(i);
                    Rectangle sourceRect = new Rectangle(source.getMinX(), source.getMinY(),
                                                         source.getWidth(), source.getHeight());
                    for (int j = i + 1; j < sourceCount; j++) {
                        RenderedImage nextSource = tileImages.get(j);
                        Rectangle nextRect = new Rectangle(nextSource.getMinX(), nextSource.getMinY(),
                                                           nextSource.getWidth(), nextSource.getHeight());
                        Rectangle intersection = sourceRect.intersection(nextRect);
                        if (intersection.width > 0 && intersection.height > 0) {
                            Collections.addAll(newImages, cut(source, intersection));
                            newImages.add(compose(nextSource, crop(source, intersection)));
                        }
                    }
                    if (!newImages.contains(source)) {
                        newImages.add(source);
                    }
                }
                mosaicOp = MosaicDescriptor.create(newImages.toArray(new RenderedImage[newImages.size()]),
                                                   MosaicDescriptor.MOSAIC_TYPE_BLEND,
                                                   null,
                                                   null,
                                                   (this.productType == 1) ? thresholds : null,
                                                   new double[] { Double.NaN },
                                                   new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
                break;
            case "MOSAIC_TYPE_OVERLAY":
                mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[sourceCount]),
                                                   MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                   null,
                                                   null,
                                                   (this.productType == 1) ? thresholds : null,
                                                   new double[] {0.0},
                                                   new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
                break;
            default:
                throw new IllegalArgumentException("Mosaic type not accepted");
        }

        final int fittingRectWidth = scaleValue(this.imageWidth, level);
        final int fittingRectHeight = scaleValue(this.imageHeight, level);
        Rectangle fitRect = new Rectangle(0, 0, fittingRectWidth, fittingRectHeight);

        final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect,scaleFactor);
        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

        if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
            int topPad = mosaicOp.getMinY();
            int lepftPad = mosaicOp.getMinX();
            int rightPad = destBounds.width - mosaicOp.getWidth();
            int bottomPad = destBounds.height - mosaicOp.getHeight();
            mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
            logger.info( "mosaicOp.width " + mosaicOp.getWidth() + " mosaicOp.height" + mosaicOp.getHeight());
        }

        return mosaicOp;

    }

    @Override
    public synchronized void reset() {
        super.reset();
    }

    private RenderedImage makeAlpha(RenderedImage source, float alphaValue) {
        /*ImageLayout imageLayout = new ImageLayout(source);
        imageLayout.unsetValid(ImageLayout.COLOR_MODEL_MASK).unsetValid(ImageLayout.SAMPLE_MODEL_MASK);*/
        ImageLayout layout = new ImageLayout(source.getMinX(), source.getMinY(),
                                             source.getWidth(), source.getHeight(),
                                             source.getTileGridXOffset(), source.getTileGridYOffset(),
                                             source.getTileWidth(), source.getTileHeight(),
                                             source.getSampleModel(), null);
        final int dataType = source.getSampleModel().getDataType();
        Number[] alphaValues;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                alphaValues = new Byte[] { (byte) (255f * alphaValue) };
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                alphaValues = new Short[] { (short) (32767f * alphaValue) };
                break;
            case DataBuffer.TYPE_INT:
                alphaValues = new Integer[] { (int) ((float) Integer.MAX_VALUE * alphaValue) };
                break;
            case DataBuffer.TYPE_DOUBLE:
                alphaValues = new Double[] { (double) alphaValue };
                break;
            case DataBuffer.TYPE_FLOAT:
            default:
                alphaValues = new Float[] { alphaValue };
                break;
        }
        return ConstantDescriptor.create((float) source.getWidth(), (float) source.getHeight(),
                                         alphaValues,
                                         new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout)).getRendering();
    }

    private RenderedImage compose(RenderedImage source1, RenderedImage source2) {
        float alpha1 = 0.5f;
        float alpha2 = 1.0f;
        final RenderedImage alphaImage1 = makeAlpha(source1, alpha1);
        final RenderedImage alphaImage2 = makeAlpha(source2, alpha2);
        return CompositeDescriptor.create(source1, source2,
                                          alphaImage1,
                                          alphaImage2,
                                          Boolean.FALSE,
                                          CompositeDescriptor.NO_DESTINATION_ALPHA,
                                          null);
    }

    private RenderedImage crop(RenderedImage source, Rectangle cropArea) {
        return CropDescriptor.create(source, (float) cropArea.getX(), (float) cropArea.getY(),
                                     (float) cropArea.getWidth(), (float) cropArea.getHeight(), null);
    }

    private RenderedImage[] cut(RenderedImage source, Rectangle cropArea) {
        List<Rectangle> smallerCrops = new ArrayList<>();
        Rectangle sourceArea = new Rectangle(source.getMinX(), source.getMinY(),
                                             source.getWidth(), source.getHeight());
        int topHeight = cropArea.y - sourceArea.y;
        if (topHeight > 0) {
            smallerCrops.add(new Rectangle(sourceArea.x, sourceArea.y, sourceArea.width, topHeight));
        }
        //compute the bottom rectangle
        int bottomY = cropArea.y + cropArea.height;
        int bottomHeight = sourceArea.height - (bottomY - sourceArea.y);
        if (bottomHeight > 0 && bottomY < sourceArea.y + sourceArea.height ) {
            smallerCrops.add(new Rectangle(sourceArea.x, bottomY, sourceArea.width, bottomHeight));
        }
        int rectAYH = sourceArea.y + sourceArea.height;
        int y1 = cropArea.y > sourceArea.y ? cropArea.y : sourceArea.y;
        int y2 = bottomY < rectAYH ? bottomY : rectAYH;
        int leftHeight = y2 - y1;
        //compute the left rectangle
        int leftWidth = cropArea.x - sourceArea.x;
        if (leftWidth > 0 && leftHeight > 0 ) {
            smallerCrops.add(new Rectangle(sourceArea.x, y1, leftWidth, leftHeight));
        }
        //compute the right rectangle
        int rbX = cropArea.x + cropArea.width;
        int rightWIdth = sourceArea.width - (rbX - sourceArea.x);
        if (rightWIdth > 0) {
            smallerCrops.add(new Rectangle(rbX, y1, rightWIdth, leftHeight));
        }
        List<RenderedImage> images = smallerCrops.stream().map(r -> crop(source, r)).collect(Collectors.toList());
        return images.toArray(new RenderedImage[images.size()]);
    }

    private PixelPos getSourceOriginPixelPosition(Band sourceBand) {
        final GeoCoding sourceBandGeoCoding = sourceBand.getGeoCoding();
        GeoPos sourceBandGeoPos = sourceBandGeoCoding.getGeoPos(new PixelPos(0, 0), null);
        return this.targetGeoCoding.getPixelPos(sourceBandGeoPos, null);
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
