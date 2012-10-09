package org.esa.beam.dataio.s2.jp2;

import java.io.File;
import java.io.IOException;

/**
 * Access an image's raster data.
 *
 * @author Norman Fomferra
 */
public interface ImageAccess {

    public static class ImageRef {
        private final Object handle;

        public ImageRef(Object handle) {
            this.handle = handle;
        }

        public Object getHandle() {
            return handle;
        }
    }

    /**
     * Opens an image.
     *
     * @param file The image file.
     * @return The image reference.
     */
    ImageRef openImage(File file) throws IOException;

    /**
     * Disposes the image an all associated resources.
     *
     * @param imageRef The image.
     */
    void disposeImage(ImageRef imageRef) throws IOException;

    /**
     * @param imageRef The image.
     * @return The number of resolution levels in the image.
     */
    int getNumResolutionLevels(ImageRef imageRef);

    /**
     * @param imageRef The image.
     * @return The number of components (bands, channels) in the image.
     */
    int getNumComponents(ImageRef imageRef);

    /**
     * @param imageRef       The image.
     * @param componentIndex The component index.
     * @return The sample data type according to {@link java.awt.image.DataBuffer}
     */
    int getSampleDataType(ImageRef imageRef, int componentIndex);

    /**
     * @param imageRef        The image.
     * @param resolutionLevel The resolution level.
     * @return The image width for the given resolution level.
     */
    int getImageWidth(ImageRef imageRef, int resolutionLevel);

    /**
     * @param imageRef        The image.
     * @param resolutionLevel The resolution level.
     * @return The image height for the given resolution level.
     */
    int getImageHeight(ImageRef imageRef, int resolutionLevel);

    /**
     * Reads rectangular region of raster data from the given image.
     * Note: Emphasis is on runtime performance!
     *
     * @param imageRef        The image.
     * @param componentIndex  The component index.
     * @param resolutionLevel The resolution level.
     * @param x               The X-coordinate of the region rectangle in pixel units.
     * @param y               The Y-coordinate of the region rectangle in pixel units.
     * @param width           The width of the region rectangle in pixel units.
     * @param height          The height of the region rectangle in pixel units.
     * @param buffer          Client supplied one-dimensional array of primitive numbers
     *                        according to {@link #getSampleDataType(ImageAccess.ImageRef, int)}.
     *                        The size of this array must be equal to {@code width * height} of
     *                        the region rectangle.
     *                        Example: if the sample data type is DataBuffer.TYPE_INT, then a buffer of
     *                        type {@code int[width * height]} is expected here.
     */
    void readRasterData(ImageRef imageRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        Object buffer) throws IOException;

}
