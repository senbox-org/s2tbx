package org.esa.s2tbx.dataio.s2.l1b.tiles;

/**
 * Created by jcoravu on 8/1/2020.
 */

import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2TileOpImage;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l1b.L1bSceneDescription;
import org.esa.s2tbx.dataio.s2.l1b.Sentinel2L1BProductReader;
import org.esa.snap.core.util.SystemUtils;

import javax.media.jai.*;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
 */
public class BandL1bSceneMultiLevelSource extends AbstractL1bSceneMultiLevelSource {

    private static final Logger logger = Logger.getLogger(BandL1bSceneMultiLevelSource.class.getName());

    private final Sentinel2L1BProductReader.L1BBandInfo tileBandInfo;
    private final Path cacheDir;
    private final S2Config config;
    private final S2SpatialResolution productResolution;

    public BandL1bSceneMultiLevelSource(S2Config config, Path cacheDir, S2SpatialResolution productResolution, L1bSceneDescription sceneDescription,
                                        Sentinel2L1BProductReader.L1BBandInfo tileBandInfo, AffineTransform imageToModelTransform) {

        super(sceneDescription, imageToModelTransform, tileBandInfo.getImageLayout().numResolutions);

        this.tileBandInfo = tileBandInfo;
        this.config = config;
        this.cacheDir = cacheDir;
        this.productResolution = productResolution;
    }

    private PlanarImage createL1bTileImage(String tileId, int level) {
        VirtualPath imagePath = this.tileBandInfo.getTileIdToPathMap().get(tileId);

        PlanarImage planarImage = null;
        try {
            planarImage = S2TileOpImage.create(imagePath.getLocalFile(), cacheDir, null, this.tileBandInfo.getImageLayout(), config, getModel(), this.productResolution, level);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create the image.", e);
        }

        logger.fine(String.format("Planar image model: %s", getModel().toString()));

        logger.fine(String.format("Planar image created: %s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                tileBandInfo.getBandInformation().getPhysicalBand(), tileId,
                planarImage.getMinX(), planarImage.getMinY(),
                planarImage.getWidth(), planarImage.getHeight()));

        return planarImage;
    }

    @Override
    protected RenderedImage createImage(int level) {
        List<RenderedImage> tileImages = new ArrayList<>();

        List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(tileBandInfo.getDetectorId())).collect(Collectors.toList());

        TileLayout thisBandTileLayout = this.tileBandInfo.getImageLayout();
        TileLayout productTileLayout = this.config.getTileLayout(this.productResolution);
        float layoutRatioX = (float) productTileLayout.width / thisBandTileLayout.width;
        float layoutRatioY = (float) productTileLayout.height / thisBandTileLayout.height;

        for (String tileId : tiles) {
            int tileIndex = sceneDescription.getTileIndex(tileId);
            Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

            PlanarImage opImage = createL1bTileImage(tileId, level);

            double factorX = 1.0 / (Math.pow(2, level) * layoutRatioX);
            double factorY = 1.0 / (Math.pow(2, level) * layoutRatioY);

            float translateX = (float) Math.floor((tileRectangle.x * factorX));
            float translateY = (float) Math.floor((tileRectangle.y * factorY));
            opImage = TranslateDescriptor.create(opImage, translateX, translateY, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

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
