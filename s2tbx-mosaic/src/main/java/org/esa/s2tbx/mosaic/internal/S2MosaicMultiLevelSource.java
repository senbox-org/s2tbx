package org.esa.s2tbx.mosaic.internal;

import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.geotools.geometry.DirectPosition2D;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.CropDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A single banded multi-level image source for Sentinel2 products
 *
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public final class S2MosaicMultiLevelSource extends AbstractMultiLevelSource {
    private final Band[] sourceBands;
    private final Map<Integer, List<PlanarImage>> tileImages;
    private final int imageWidth;
    private final int imageHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final String mosaicType;
    private final Logger logger;
    private final double originX;
    private final double originY;

    public S2MosaicMultiLevelSource(Band[] sourceBands, double originX, double originY, int imageWidth, int imageHeight,
                                    int tileWidth, int tileHeight, int levels,
                                    GeoCoding geoCoding, String mosaicType) {
        super(new DefaultMultiLevelModel(levels,
                Product.findImageToModelTransform(geoCoding),
                imageWidth, imageHeight));
        this.originX = originX;
        this.originY = originY;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sourceBands = sourceBands;
        this.mosaicType = mosaicType;
        this.tileImages = new HashMap<>();
        for (int i = 0; i < levels; i++) {
            this.tileImages.put(i, Collections.synchronizedList(new ArrayList<>()));
        }
        this.logger = Logger.getLogger(S2MosaicMultiLevelSource.class.getName());
    }

    /**
     * Creates a planar image corresponding of source band, at the specified resolution.
     *
     * @param bandIndex   The index of the sourceBand (0-based)
     *
     */
    private PlanarImage createTileImage(final int bandIndex, final int level) throws IOException {
        return (PlanarImage) this.sourceBands[bandIndex].getSourceImage().getImage(level);
    }

    @Override
    protected RenderedImage createImage(int level) {
        double scaleFactor = 1.0 / Math.pow(2, level);
        for (int index = 0; index < this.sourceBands.length; index++) {
            PlanarImage opImage;
            try {
                opImage = createTileImage(index, level);
                if (opImage != null) {
                    if (!this.tileImages.get(level).contains(opImage)) {
                        //compute the origin of the source bands so that it can be determined where
                        // to place them in relation to the target product
                        final Point2D bandOrigin = getSourceOriginPixelPosition(sourceBands[index]);
                        float offsetX = (float) ((bandOrigin.getX() - this.originX) /
                                sourceBands[index].getImageToModelTransform().getScaleX() * scaleFactor);
                        float offsetY = (float) ((bandOrigin.getY() - this.originY) /
                                sourceBands[index].getImageToModelTransform().getScaleY() * scaleFactor);
                        opImage = TranslateDescriptor.create(opImage, offsetX, offsetY,
                                Interpolation.getInstance(Interpolation.INTERP_BILINEAR),
                                null);
                    }
                }
            } catch (IOException e) {
                opImage = ConstantDescriptor.create((float) this.tileWidth, (float) this.tileHeight, new Number[]{0}, null);
            }
            //tileImages.add(opImage);
            this.tileImages.get(level).add(opImage);
        }
        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic");
            return null;
        }

        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setWidth((int)(imageWidth*scaleFactor));
        imageLayout.setHeight((int)(imageHeight*scaleFactor));
        imageLayout.setTileWidth(JAI.getDefaultTileSize().width);
        imageLayout.setTileHeight(JAI.getDefaultTileSize().height);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);
        RenderedOp mosaicOp;

        final int sourceCount = tileImages.get(level).size();
        double[][] thresholds = new double[sourceCount][1];
        for (int i = 0; i < sourceCount; i++) {
            thresholds[i][0] = Float.MIN_VALUE;
        }
        List<PlanarImage> images = tileImages.get(level);
        switch(this.mosaicType) {
            case "MOSAIC_TYPE_BLEND":
                List<RenderedImage> newTiles = new ArrayList<>();
                List<Rectangle> rectangles =  computeRectangles(images);
                HashMap<Rectangle, List<PlanarImage>> map = new HashMap<>();
                //compose a Hash Map of rectangles and their respective source images that overlap the regions
                for(Rectangle rectangle : rectangles) {
                    List<PlanarImage> imagesInRectangle = new ArrayList<>();
                    for(PlanarImage pImage: images){
                        if(((int)rectangle.getMinX() >= pImage.getMinX()) &&
                           ((int)rectangle.getMinX() < pImage.getMinX() + pImage.getWidth()) &&
                           ((int)rectangle.getMinY() >= pImage.getMinY()) &&
                           ((int)rectangle.getMinY() < pImage.getMinY() + pImage.getHeight())){
                            //for every rectangle there must be at least one source image  or fragment of a source image to overlap
                                imagesInRectangle.add(pImage);
                        }
                    }
                    map.put(rectangle,imagesInRectangle);
                }
                int maximumNumberOfOverlaps = 1;
                for (Map.Entry<Rectangle, List<PlanarImage>> pair : map.entrySet()) {
                    if (pair.getValue().size() > maximumNumberOfOverlaps) {
                        maximumNumberOfOverlaps = pair.getValue().size();
                    }
                }
                int numberOfOverlaps = 1;
                while(numberOfOverlaps<=maximumNumberOfOverlaps) {
                    for (Map.Entry<Rectangle, List<PlanarImage>> pair : map.entrySet()) {
                        List<PlanarImage> list = pair.getValue();
                        Rectangle reg = pair.getKey();
                        if ((list.size() == numberOfOverlaps) && (numberOfOverlaps == 1)) {
                            Collections.addAll(newTiles, crop(list.get(0), reg));
                        } else if ((list.size() > 1) && (list.size() == numberOfOverlaps)) {
                            RenderedImage rendered = compose(crop(list.get(0), reg), crop(list.get(1), reg));
                            if (list.size() > 2) {
                                int index = 2;
                                while (index < list.size()) {
                                    rendered = compose(crop(rendered, reg), crop(list.get(index), reg));
                                    index++;
                                }
                            }
                            newTiles.add(rendered);
                        }
                    }
                    numberOfOverlaps++;
                }
                mosaicOp = MosaicDescriptor.create(newTiles.toArray(new RenderedImage[newTiles.size()]),
                        MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                        null,
                        null,
                        thresholds,
                        new double[] { Float.NaN },
                        new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
                break;
            case "MOSAIC_TYPE_OVERLAY":
                mosaicOp = MosaicDescriptor.create(images.toArray(new RenderedImage[sourceCount]),
                        MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                        null,
                        null,
                        thresholds,
                        new double[] { Float.NaN },
                        new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
                break;
            default:
                throw new IllegalArgumentException("Mosaic type not accepted");
        }

        final int fittingRectWidth = scaleValue(this.imageWidth, level);
        final int fittingRectHeight = scaleValue(this.imageHeight, level);
        Rectangle fitRect = new Rectangle(0, 0, fittingRectWidth, fittingRectHeight);

        final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect,Math.pow(2, level));
        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_ZERO);

        if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
            int rightPad = destBounds.width - mosaicOp.getWidth();
            int bottomPad = destBounds.height - mosaicOp.getHeight();
            mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
        }

        return mosaicOp;

    }

    private List<Rectangle> computeRectangles(List<PlanarImage> images) {
        List<Rectangle> reg = new ArrayList<>();
        Set<Integer> xSet= new TreeSet<>();
        Set<Integer> ySet = new TreeSet<>();
        for(PlanarImage image: images){
            xSet.add(image.getMinX());
            xSet.add(image.getMinX() + image.getWidth());
            ySet.add(image.getMinY());
            ySet.add(image.getMinY() + image.getHeight());
        }
        Integer[] xArray = xSet.toArray(new Integer[xSet.size()]);
        Integer[] yArray = ySet.toArray(new Integer[ySet.size()]);
        for(int x = 0; x < xArray.length - 1; x++){
            for(int y = 0; y < yArray.length - 1; y++ ){
                reg.add(new Rectangle(xArray[x],yArray[y], xArray[x+1]-xArray[x], yArray[y+1]-yArray[y]));
            }
        }
        return reg;
    }

    @Override
    public synchronized void reset() {
        super.reset();
    }

    private RenderedImage makeAlpha(RenderedImage source, float alphaValue) {
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
        return ConstantDescriptor.create((float) source.getWidth(),
                (float) source.getHeight(),
                alphaValues,
                new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout)).getRendering();
    }

    private RenderedImage compose(RenderedImage source1, RenderedImage source2) {
        float alpha1 = 1.0f;
        float alpha2 = 1.0f;
        ImageLayout layout = new ImageLayout(source1);
        final RenderedImage alphaImage1 = makeAlpha(source1, alpha1);
        final RenderedImage alphaImage2 = makeAlpha(source2, alpha2);
        return new CompositeNoDestAlphaOpImage(source2,
                source1,
                null,
                layout,
                alphaImage2,
                alphaImage1,
                false);
    }

    private RenderedImage crop(RenderedImage source, Rectangle cropArea) {
        ImageLayout layout = new ImageLayout();
        layout.setMinX(cropArea.x);
        layout.setMinY(cropArea.y);
        layout.setWidth(cropArea.width);
        layout.setHeight(cropArea.height);
        return CropDescriptor.create(source, (float) cropArea.getX(), (float) cropArea.getY(),
                (float) cropArea.getWidth(), (float) cropArea.getHeight(),
                new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout)).getRendering();
    }

    private Point2D getSourceOriginPixelPosition(Band sourceBand) {
        final AffineTransform transform = sourceBand.getImageToModelTransform();
        return transform.transform(new DirectPosition2D(0, 0), null);
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
