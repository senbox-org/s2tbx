package org.esa.beam.dataio.s3;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;

import javax.media.jai.Interpolation;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;

public class SourceImageScaler {

    public static MultiLevelImage scaleMultiLevelImage(Rectangle targetBounds, MultiLevelImage sourceImage,
                                                       float[] transformations, RenderingHints renderingHints) {
        final float scaleX = (float) targetBounds.getWidth() / sourceImage.getWidth();
        final float scaleY = (float) targetBounds.getHeight() / sourceImage.getHeight();
        if (scaleX != 1.0 || scaleY != 1.0) {
            float[] scaleFactors = new float[]{scaleX, scaleY};
            final ScaledMultiLevelSource multiLevelSource = new ScaledMultiLevelSource(sourceImage, scaleFactors,
                                                                                       transformations, renderingHints);
            return new DefaultMultiLevelImage(multiLevelSource);
        }
        return sourceImage;
    }

    private static class ScaledMultiLevelSource extends AbstractMultiLevelSource {

        private final MultiLevelImage sourceImage;
        private final float[] scaleFactors;
        private final float[] transformations;
        private final RenderingHints renderingHints;

        private ScaledMultiLevelSource(MultiLevelImage sourceImage, float scaleFactors[], float[] transformations,
                                       RenderingHints renderingHints) {
            // Todo replace with product.getNumResolutionsMax() from masterProduct
            super(DefaultMultiLevelSource.createDefaultMultiLevelModel(sourceImage, sourceImage.getModel().getLevelCount()));
            this.sourceImage = sourceImage;
            this.scaleFactors = scaleFactors;
            this.transformations = transformations;
            this.renderingHints = renderingHints;
        }

        @Override
        protected RenderedImage createImage(int level) {
            final RenderedImage image = sourceImage.getImage(level);
            return ScaleDescriptor.create(image, scaleFactors[0], scaleFactors[1],
                                          transformations[0], transformations[1],
                                          Interpolation.getInstance(Interpolation.INTERP_NEAREST),
                                          renderingHints);
        }
    }

}
