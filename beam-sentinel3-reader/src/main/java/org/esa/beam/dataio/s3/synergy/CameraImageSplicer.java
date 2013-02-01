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

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddCollectionDescriptor;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.image.RenderedImage;
import java.util.Arrays;

class CameraImageSplicer {

    public static RenderedImage create(RenderedImage... sourceImages) {
        int fullWidth = 0;
        for (RenderedImage sourceImage : sourceImages) {
            fullWidth += sourceImage.getWidth();
        }

        int leftBorder = 0;
        RenderedImage[] translatedBorderedImages = new RenderedImage[sourceImages.length];
        for (int i = 0; i < sourceImages.length; i++) {
            if (i > 0) {
                leftBorder += sourceImages[i - 1].getWidth();
            }
            RenderedImage sourceImage = sourceImages[i];
            int imageWidth = sourceImage.getWidth();
            int rightBorder = fullWidth - leftBorder - imageWidth;
            RenderedOp translatedImage = TranslateDescriptor.create(sourceImage, (float) leftBorder, 0f, null, null);
            RenderedOp borderedImage = BorderDescriptor.create(translatedImage, leftBorder, rightBorder, 0, 0, null, null);
            translatedBorderedImages[i] = borderedImage;
        }
        return AddCollectionDescriptor.create(Arrays.asList(translatedBorderedImages), null);
    }

    public static void printOutForDebug(RenderedImage[] sourceImages) {
        for (int i = 0; i < sourceImages.length; i++) {
            RenderedImage image = sourceImages[i];
            System.out.println(
                        "sourceImages[" + i + "]=" + image.getSampleModel() +
                        ",minX=" + image.getMinX() +
                        ",minY=" + image.getMinY() +
                        ",width=" + image.getWidth() +
                        ",height=" + image.getHeight());

//            Raster data = image.getData();
//            for (int x = 0; x < image.getWidth(); x++) {
//                int sample = data.getSample(image.getMinX() + x, 0, 0);
//                System.out.println("sourceImages[" + i + "](" + x + ",0) = " + sample);
//            }
        }
    }
}
