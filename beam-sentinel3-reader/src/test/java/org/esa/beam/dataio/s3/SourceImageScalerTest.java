package org.esa.beam.dataio.s3;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Test;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class SourceImageScalerTest {

    private static final int MAX_USHORT = (2 << 15) - 1;

    @Test
    public void testScaleSourceImage() {
        Band targetBand = new Band("targetBand", ProductData.TYPE_INT32, 200, 200);
        int levelCount = 5;
        MultiLevelImage sourceImage = createSourceImage(levelCount, 100, 100);
        float[] scalings = new float[]{((float)targetBand.getRasterWidth())/sourceImage.getWidth(),
            ((float)targetBand.getRasterHeight())/sourceImage.getHeight()};
        float[] transformations = new float[]{0f, 0f};
        final RenderingHints renderingHints = new RenderingHints(JAI.KEY_BORDER_EXTENDER,
                                                                 BorderExtender.createInstance(
                                                                         BorderExtender.BORDER_COPY));
        MultiLevelImage scaledImage = SourceImageScaler.scaleMultiLevelImage(sourceImage, scalings,
                                                                             transformations, renderingHints,
                                                                             levelCount);
        final Rectangle targetBounds = targetBand.getSourceImage().getBounds();

        assertEquals(targetBand.getRasterWidth(), scaledImage.getWidth());
        assertEquals(targetBand.getRasterHeight(), scaledImage.getHeight());
        assertEquals(levelCount, scaledImage.getModel().getLevelCount());
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
        MultiLevelImage image = new DefaultMultiLevelImage(multiLevelSource);
        return image;
    }

}