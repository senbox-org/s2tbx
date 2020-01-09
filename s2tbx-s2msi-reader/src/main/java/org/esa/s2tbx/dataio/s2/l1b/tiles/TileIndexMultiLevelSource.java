package org.esa.s2tbx.dataio.s2.l1b.tiles;

import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.l1b.L1bSceneDescription;
import org.esa.s2tbx.dataio.s2.l1b.Sentinel2L1BProductReader;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleDirFilename;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.util.SystemUtils;

import javax.media.jai.*;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by jcoravu on 9/1/2020.
 */
public class TileIndexMultiLevelSource extends AbstractL1bSceneMultiLevelSource {

    private final Logger logger = Logger.getLogger(TileIndexMultiLevelSource.class.getName());

    private final S2Config config;
    private final Sentinel2L1BProductReader.L1BBandInfo tileBandInfo;
    private final S2SpatialResolution productResolution;

    public TileIndexMultiLevelSource(L1bSceneDescription sceneDescription, Sentinel2L1BProductReader.L1BBandInfo tileBandInfo, AffineTransform imageToModelTransform,
                                     S2Config config, S2SpatialResolution productResolution) {

        super(sceneDescription, imageToModelTransform, tileBandInfo.getImageLayout().numResolutions);

        this.tileBandInfo = tileBandInfo;
        this.config = config;
        this.productResolution = productResolution;
    }

    private PlanarImage createConstantTileImage(String tileId, int level) {
        S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) tileBandInfo.getBandInformation();
        IndexCoding indexCoding = indexBandInformation.getIndexCoding();
        Integer indexValue = indexCoding.getIndexValue(S2L1BGranuleDirFilename.create(tileId).getTileID());
        short indexValueShort = indexValue.shortValue();

        Rectangle tileRectangleL0 = new Rectangle();
        tileRectangleL0.height = tileBandInfo.getImageLayout().height;
        tileRectangleL0.width = tileBandInfo.getImageLayout().width;

        sceneDescription.getTileRectangle(sceneDescription.getTileIndex(tileId));

        Rectangle tileRectangle = DefaultMultiLevelSource.getLevelImageBounds(tileRectangleL0, getModel().getScale(level));
        PlanarImage planarImage = ConstantDescriptor.create((float) tileRectangle.width, (float) tileRectangle.height, new Short[]{indexValueShort}, null);

        return planarImage;
    }

    @Override
    protected RenderedImage createImage(int level) {
        ArrayList<RenderedImage> tileImages = new ArrayList<>();

        java.util.List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(tileBandInfo.getDetectorId())).collect(Collectors.toList());

        TileLayout thisBandTileLayout = this.tileBandInfo.getImageLayout();
        TileLayout productTileLayout = this.config.getTileLayout(this.productResolution);
        float layoutRatioX = (float) productTileLayout.width / thisBandTileLayout.width;
        float layoutRatioY = (float) productTileLayout.height / thisBandTileLayout.height;

        for (String tileId : tiles) {
            int tileIndex = sceneDescription.getTileIndex(tileId);
            Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

            PlanarImage opImage = createConstantTileImage(tileId, level);
            double factorX = 1.0 / (Math.pow(2, level) * layoutRatioX);
            double factorY = 1.0 / (Math.pow(2, level) * layoutRatioY);

            float translateOffsetX = (float) Math.floor((tileRectangle.x * factorX));
            float translateOffsetY = (float) Math.floor((tileRectangle.y * factorY));
            opImage = TranslateDescriptor.create(opImage, translateOffsetX, translateOffsetY, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

            logger.log(Level.parse(S2Config.LOG_SCENE), String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));

            logger.log(Level.parse(S2Config.LOG_SCENE), String.format("opImage added for level %d at (%d,%d) with size (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY(), opImage.getWidth(), opImage.getHeight()));
            tileImages.add(opImage);
        }

        if (tileImages.isEmpty()) {
            logger.warning("No tile images for mosaic");
            return null;
        }

        ImageLayout imageLayout = new ImageLayout();
        imageLayout.setMinX(0);
        imageLayout.setMinY(0);
        imageLayout.setTileWidth(S2Config.DEFAULT_JAI_TILE_SIZE);
        imageLayout.setTileHeight(S2Config.DEFAULT_JAI_TILE_SIZE);
        imageLayout.setTileGridXOffset(0);
        imageLayout.setTileGridYOffset(0);

        RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                null, null, new double[][]{{1.0}}, new double[]{S2Config.FILL_CODE_MOSAIC_BG},
                new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));


        int fitRectWidht = (int) (sceneDescription.getSceneEnvelope().getWidth() / (layoutRatioX * this.productResolution.resolution));
        int fitRectHeight = (int) (sceneDescription.getSceneEnvelope().getHeight() / (layoutRatioY * this.productResolution.resolution));

        Rectangle fitRect = new Rectangle(0, 0, fitRectWidht, fitRectHeight);
        final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitRect, Math.pow(2.0, level));

        BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

        if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
            int rightPad = destBounds.width - mosaicOp.getWidth();
            int bottomPad = destBounds.height - mosaicOp.getHeight();
            SystemUtils.LOG.log(Level.parse(S2Config.LOG_SCENE), String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

            mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
        }

        logger.log(Level.parse(S2Config.LOG_SCENE), String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

        return mosaicOp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
