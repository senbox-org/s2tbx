package org.esa.beam.dataio.s2.jp2;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author Norman Fomferra
 */
public class BufferedImageAccess implements ImageAccess {
    @Override
    public ImageRef openImage(File file) throws IOException {
        final BufferedImage image = ImageIO.read(file);
        return new ImageRef(image);
    }

    @Override
    public void disposeImage(ImageRef imageRef) throws IOException {
    }

    @Override
    public int getNumResolutionLevels(ImageRef imageRef) {
        return 1;
    }

    @Override
    public int getNumComponents(ImageRef imageRef) {
        return getImage(imageRef).getSampleModel().getNumBands();
    }

    @Override
    public int getSampleDataType(ImageRef imageRef, int componentIndex) {
        return getImage(imageRef).getSampleModel().getDataType();
    }

    @Override
    public int getImageWidth(ImageRef imageRef, int resolutionLevel) {
        return getImage(imageRef).getWidth();
    }

    @Override
    public int getImageHeight(ImageRef imageRef, int resolutionLevel) {
        return getImage(imageRef).getHeight();
    }

    @Override
    public void readRasterData(ImageRef imageRef,
                               int componentIndex,
                               int resolutionLevel,
                               int x, int y,
                               int width, int height,
                               Object buffer) throws IOException {
        final Raster data = getImage(imageRef).getData(new Rectangle(x, y, width, height));
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Array.set(buffer, j * width + i,
                          data.getSample(x + i, y + j, componentIndex));
            }
        }
    }

    private BufferedImage getImage(ImageRef imageRef) {
        return ((BufferedImage) imageRef.getHandle());
    }

}
