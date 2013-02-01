package org.esa.beam.dataio.s3.synergy;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import static org.junit.Assert.*;

import org.junit.*;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ConstantDescriptor;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

public class CameraImageSplicerTest {

    @Test
    public void testCreateMosaicImage() throws Exception {
        final int WIDTH = 10;
        final RenderedImage image1 = createRenderedImage(new Short[]{1}, WIDTH);
        final RenderedImage image2 = createRenderedImage(new Short[]{2}, WIDTH);
        final RenderedImage image3 = createRenderedImage(new Short[]{3}, WIDTH);
        final RenderedImage image4 = createRenderedImage(new Short[]{4}, WIDTH);
        final RenderedImage image5 = createRenderedImage(new Short[]{5}, WIDTH);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        assertEquals(50, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();
        for (int x = 0; x < mosaicImage.getWidth(); x++) {
            assertEquals(x / 10 + 1, data.getSample(x, 0, 0));
        }
    }

    @Test
    public void testCreateMosaicImageNegativeValues() throws Exception {
        final int WIDTH = 10;
        final RenderedImage image1 = createRenderedImage(new Short[]{-1}, WIDTH);
        final RenderedImage image2 = createRenderedImage(new Short[]{-2}, WIDTH);
        final RenderedImage image3 = createRenderedImage(new Short[]{-3}, WIDTH);
        final RenderedImage image4 = createRenderedImage(new Short[]{-4}, WIDTH);
        final RenderedImage image5 = createRenderedImage(new Short[]{-5}, WIDTH);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        assertEquals(50, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();
        for (int x = 0; x < mosaicImage.getWidth(); x++) {
            assertEquals("" + x, (x / WIDTH + 1) * -1, data.getSample(x, 0, 0));
        }
    }

    @Test
    public void testCreateMosaicImageDifferentSourceWidths() throws Exception {
        final RenderedImage image1 = createRenderedImage(new Float[]{1f}, 7);
        final RenderedImage image2 = createRenderedImage(new Float[]{2f}, 12);
        final RenderedImage image3 = createRenderedImage(new Float[]{3f}, 11);
        final RenderedImage image4 = createRenderedImage(new Float[]{4f}, 13);
        final RenderedImage image5 = createRenderedImage(new Float[]{5f}, 17);
        final RenderedImage[] sourceImages = {image1, image2, image3, image4, image5};

        final RenderedImage mosaicImage = CameraImageSplicer.create(sourceImages);

        assertNotNull(mosaicImage);
        assertEquals(60, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();
        int[] expectedLine = new int[]{
                    1, 1, 1, 1, 1, 1, 1,
                    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                    3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                    5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5
        };
        final int[] currentLine = data.getSamples(0, 0, 60, 1, 0, new int[60]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Byte() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Byte[]{1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Byte[]{2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Byte[]{3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Byte[]{4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Byte[]{5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = i + 1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Short() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Short[]{1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Short[]{2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Short[]{3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Short[]{4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Short[]{5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = i + 1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Integer() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Integer[]{1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Integer[]{2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Integer[]{3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Integer[]{4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Integer[]{5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = i + 1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Float() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Float[]{1f}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Float[]{2f}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Float[]{3f}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Float[]{4f}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Float[]{5f}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = i + 1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Double() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Double[]{1d}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Double[]{2d}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Double[]{3d}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Double[]{4d}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Double[]{5d}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = i + 1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Negative_Byte() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Byte[]{-1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Byte[]{-2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Byte[]{-3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Byte[]{-4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Byte[]{-5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = 255 -i;  // 255 int means -1 in Byte
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Negative_Short() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Short[]{-1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Short[]{-2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Short[]{-3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Short[]{-4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Short[]{-5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = (i + 1) * -1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Negative_Integer() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Integer[]{-1}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Integer[]{-2}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Integer[]{-3}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Integer[]{-4}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Integer[]{-5}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = (i + 1) * -1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Negative_Float() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Float[]{-1f}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Float[]{-2f}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Float[]{-3f}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Float[]{-4f}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Float[]{-5f}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = (i + 1) * -1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    @Test
    public void testCreateMosaicImageRealSourceWidths_Negative_Double() throws Exception {
        final int[] widths = {740, 709, 713, 707, 711};
        final RenderedImage image1 = createRenderedImage(new Double[]{-1d}, widths[0]);
        final RenderedImage image2 = createRenderedImage(new Double[]{-2d}, widths[1]);
        final RenderedImage image3 = createRenderedImage(new Double[]{-3d}, widths[2]);
        final RenderedImage image4 = createRenderedImage(new Double[]{-4d}, widths[3]);
        final RenderedImage image5 = createRenderedImage(new Double[]{-5d}, widths[4]);

        final RenderedImage mosaicImage = CameraImageSplicer.create(image1, image2, image3, image4, image5);

        assertNotNull(mosaicImage);
        int mosaicWidth = 0;
        for (int width : widths) {
            mosaicWidth += width;
        }

        assertEquals(mosaicWidth, mosaicImage.getWidth());
        assertEquals(100, mosaicImage.getHeight());

        final Raster data = mosaicImage.getData();

        int[] expectedLine = new int[mosaicWidth];
        int idx = 0;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            for (int j = 0; j < width; j++) {
                expectedLine[idx++] = (i + 1) * -1;
            }
        }

        final int[] currentLine = data.getSamples(0, 0, mosaicWidth, 1, 0, new int[mosaicWidth]);
        assertArrayEquals(expectedLine, currentLine);
    }

    private RenderedOp createRenderedImage(Number[] sampleValue, float width) {
        return ConstantDescriptor.create(width, 100.0f, sampleValue, null);
    }
}
