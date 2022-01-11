/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.mosaic;


import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
public class S2tbxMosaicOpImageTest {

    private RenderedImage[] sourceImages1;
    private RenderedImage[] sourceImages2;
    private PlanarImage[] alphaImages;

    @Before
    public void setup() {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
        sourceImages1 = new RenderedImage[]{
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{2.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{3.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{5.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{7.0f}, null)
        };
        sourceImages2 = new RenderedImage[]{
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{7.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{5.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{3.0f}, null),
                ConstantDescriptor.create(10.0f, 10.0f, new Float[]{2.0f}, null)
        };

        alphaImages = new PlanarImage[]{
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{1.0f}, null)),
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{2.0f}, null)),
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{3.0f}, null)),
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{4.0f}, null))
        };


    }

    @Test
    public void testAveraging() {

        final RenderedOp mosaicImage = MosaicDescriptor.create(sourceImages1, MosaicDescriptor.MOSAIC_TYPE_BLEND,
                                                               alphaImages, null, null, null, null);
        final Raster data = mosaicImage.getData();
        float sample = data.getSampleFloat(0, 0, 0);
        assertEquals(5.1f, sample, 0.0f);
        sample = data.getSampleFloat(5, 5, 0);
        assertEquals(5.1f, sample, 0.0f);
    }

    @Test
    public void testMosaicUpdate() {
        final RenderedOp firstImage = MosaicDescriptor.create(Arrays.copyOf(sourceImages1, 3),
                                                              MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                              Arrays.copyOf(alphaImages, 3), null, null, null, null);
        final PlanarImage[] alphaUpdateImages = {
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{6.0f}, null)),
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{4.0f}, null))
        };
        final RenderedImage[] sourceUpdateImages = {firstImage, sourceImages1[3]};
        final RenderedOp updatedImage = MosaicDescriptor.create(sourceUpdateImages,
                                                                MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                                alphaUpdateImages, null, null, null, null);

        final Raster data = updatedImage.getData();
        float sample = data.getSampleFloat(0, 0, 0);
        assertEquals(2.0f, sample, 0.0f);
        sample = data.getSampleFloat(5, 5, 0);
        assertEquals(2.0f, sample, 0.0f);
    }
    @Test
    public void testMosaicUpdateWithNewSourceImages() {
        final RenderedOp firstImage = MosaicDescriptor.create(Arrays.copyOf(sourceImages2, 3),
                MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                Arrays.copyOf(alphaImages, 3), null, null, null, null);
        final PlanarImage[] alphaUpdateImages = {
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{6.0f}, null)),
                PlanarImage.wrapRenderedImage(ConstantDescriptor.create(10.0f, 10.0f, new Float[]{4.0f}, null))
        };
        final RenderedImage[] sourceUpdateImages = {firstImage, sourceImages2[3]};
        final RenderedOp updatedImage = MosaicDescriptor.create(sourceUpdateImages,
                MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                alphaUpdateImages, null, null, null, null);

        final Raster data = updatedImage.getData();
        float sample = data.getSampleFloat(0, 0, 0);
        assertEquals(7.0f, sample, 0.0f);
        sample = data.getSampleFloat(5, 5, 0);
        assertEquals(7.0f, sample, 0.0f);
    }
}
