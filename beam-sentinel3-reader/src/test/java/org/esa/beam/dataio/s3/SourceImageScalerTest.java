package org.esa.beam.dataio.s3;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Test;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import static org.junit.Assert.assertEquals;

public class SourceImageScalerTest {

    private static final int MAX_USHORT = (2 << 15) - 1;

    @Test
    public void testScaleSourceImage() {
        Band targetBand = new Band("targetBand", ProductData.TYPE_INT32, 200, 200);
        int levelCount = 5;
        MultiLevelImage sourceImage = createSourceImage(levelCount, 100, 100);
        float[] scalings = new float[]{((float) targetBand.getRasterWidth()) / sourceImage.getWidth(),
                ((float) targetBand.getRasterHeight()) / sourceImage.getHeight()};
        float[] translationsBeforeScaling = new float[]{0f, 0f};
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                                 BorderExtender.createInstance(
                                                                         BorderExtender.BORDER_COPY));
        MultiLevelImage scaledImage = SourceImageScaler.scaleMultiLevelImage(targetBand.getSourceImage(),
                                                                             sourceImage, scalings,
                                                                             translationsBeforeScaling,
                                                                             new float[]{0f,0f}, renderingHints,
                                                                             Double.NaN);
        final Rectangle targetBounds = targetBand.getSourceImage().getBounds();

        assertEquals(targetBand.getRasterWidth(), scaledImage.getWidth());
        assertEquals(targetBand.getRasterHeight(), scaledImage.getHeight());
        assertEquals(targetBand.getSourceImage().getModel().getLevelCount(), scaledImage.getModel().getLevelCount());
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(0, 0, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(targetBounds.width - 1, 0, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(0, targetBounds.height - 1, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(targetBounds.width - 1, targetBounds.height - 1, 0));
    }

    @Test
    public void testScaleMultiLevelImageWithDifferentLevelCounts() {
        Band targetBand = new Band("targetBand", ProductData.TYPE_INT32, 200, 200);
        int masterLevelCount = 5;
        int sourceLevelCount = 3;
        MultiLevelImage masterImage = createNewMultiLevelMasterImage(masterLevelCount, 200, 200);
        targetBand.setSourceImage(masterImage);
        MultiLevelImage sourceImage = createSourceImage(sourceLevelCount, 100, 100);
        float[] scalings = new float[]{((float) targetBand.getRasterWidth()) / sourceImage.getWidth(),
                ((float) targetBand.getRasterHeight()) / sourceImage.getHeight()};
        float[] translationsBeforeScaling = new float[]{0f, 0f};
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                                 BorderExtender.createInstance(
                                                                         BorderExtender.BORDER_COPY));
        MultiLevelImage scaledImage = SourceImageScaler.scaleMultiLevelImage(masterImage, sourceImage, scalings,
                                                                             translationsBeforeScaling,
                                                                             new float[]{0f, 0f}, renderingHints,
                                                                             Double.NaN);
        final Rectangle targetBounds = targetBand.getSourceImage().getBounds();

        assertEquals(targetBand.getRasterWidth(), scaledImage.getWidth());
        assertEquals(targetBand.getRasterHeight(), scaledImage.getHeight());
        assertEquals(masterLevelCount, scaledImage.getModel().getLevelCount());
        for (int i = 0; i < masterLevelCount; i++) {
            final RenderedImage masterImageAtLevel = ((MultiLevelImage) targetBand.getSourceImage()).getImage(i);
            final RenderedImage scaledImageAtLevel = scaledImage.getImage(i);
            assertEquals(masterImageAtLevel.getWidth(), scaledImageAtLevel.getWidth());
            assertEquals(masterImageAtLevel.getHeight(), scaledImageAtLevel.getHeight());
        }
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(0, 0, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(targetBounds.width - 1, 0, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(0, targetBounds.height - 1, 0));
        assertEquals(MAX_USHORT, scaledImage.getData().getSample(targetBounds.width - 1, targetBounds.height - 1, 0));
    }

    private static MultiLevelImage createSourceImage(int levelCount, int srcW, int srcH) {
        BufferedImage sourceImage = new BufferedImage(srcW, srcH, BufferedImage.TYPE_USHORT_GRAY);
        for (int y = 0; y < srcH; y++) {
            for (int x = 0; x < srcW; x++) {
                sourceImage.getRaster().setSample(x, y, 0, (int) (MAX_USHORT * Math.random()));
            }
        }
        sourceImage.getRaster().setSample(0, 0, 0, MAX_USHORT);
        sourceImage.getRaster().setSample(srcW - 1, 0, 0, MAX_USHORT);
        sourceImage.getRaster().setSample(0, srcH - 1, 0, MAX_USHORT);
        sourceImage.getRaster().setSample(srcW - 1, srcH - 1, 0, MAX_USHORT);
        final DefaultMultiLevelSource multiLevelSource = new DefaultMultiLevelSource(sourceImage, levelCount);
        return new DefaultMultiLevelImage(multiLevelSource);
    }

    private static MultiLevelImage createNewMultiLevelMasterImage(int levelCount, int srcW, int srcH) {
        BufferedImage sourceImage = new BufferedImage(srcW, srcH, BufferedImage.TYPE_USHORT_GRAY);
        final DifferentlyScalingMultiLevelModel multiLevelModel = new DifferentlyScalingMultiLevelModel(levelCount, sourceImage);
        final DefaultMultiLevelSource source = new DefaultMultiLevelSource(sourceImage, multiLevelModel);
        return new DefaultMultiLevelImage(source);
    }

    private static class DifferentlyScalingMultiLevelModel extends DefaultMultiLevelModel {

        public DifferentlyScalingMultiLevelModel(int levelCount, BufferedImage sourceImage) {
            super(levelCount, new AffineTransform(), sourceImage.getWidth(), sourceImage.getHeight());
        }

        @Override
        public double getScale(int level) {
            checkLevel(level);
            return Math.pow(3, level);
        }

    }

}