package org.esa.beam.dataio.s2.jp2;

/**
 * Access an image's raster data.
 *
 * @author Norman Fomferra
 */
public interface ImageAccess {

    int testFunction(String message);
    String testFunction2();
    void testFunction3();

    /**
     * Opens an image.
     *
     * @param filePath The image file.
     * @return The image reference.
     */
    long openImage(String filePath);

    /**
     * Disposes the image an all associated resources.
     *
     * @param imageRef The image.
     */
    void disposeImage(long imageRef);

    /**
     * @param imageRef The image.
     * @return The number of resolution levels in the image.
     */
    int getNumResolutionLevels(long imageRef);

    /**
     * @param imageRef The image.
     * @return The number of components (bands, channels) in the image.
     */
    int getNumComponents(long imageRef);

    /**
     * @param imageRef       The image.
     * @param componentIndex The component index.
     * @return The sample data type according to {@link java.awt.image.DataBuffer}
     */
    int getSampleDataType(long imageRef, int componentIndex);

    /**
     * @param imageRef        The image.
     * @param resolutionIndex The resolution level.
     * @return The image width for the given resolution level.
     */
    int getImageWidth(long imageRef, int resolutionIndex);

    /**
     * @param imageRef        The image.
     * @param resolutionIndex The resolution level.
     * @return The image height for the given resolution level.
     */
    int getImageHeight(long imageRef, int resolutionIndex);

    /**
     * @param imageRef
     * @param resolutionIndex
     * @return the level for the given index. A level is a sub-image at a given resolution.
     */
    long getResolutionLevel(long imageRef, int resolutionIndex);

    /**
     * Reads rectangular region of raster data from the given image.
     * Note: Emphasis is on runtime performance!
     *
     * @param levelRef        The image level reference.
     * @param componentIndex  The component index.
     * @param resolutionLevel The resolution level.
     * @param x               The X-coordinate of the region rectangle in pixel units.
     * @param y               The Y-coordinate of the region rectangle in pixel units.
     * @param width           The width of the region rectangle in pixel units.
     * @param height          The height of the region rectangle in pixel units.
     * @param buffer          Client supplied one-dimensional array of primitive numbers
     *                        according to {@link #getSampleDataType(long, int)}.
     *                        The size of this array must be equal to {@code width * height} of
     *                        the region rectangle.
     *                        Example: if the sample data type is DataBuffer.TYPE_INT, then a buffer of
     *                        type {@code int[width * height]} is expected here.
     * @return an error code (TBD).
     */
    int readRasterDataB(long levelRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        byte[] buffer);

    int readRasterDataS(long levelRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        short[] buffer);

    int readRasterDataI(long levelRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        int[] buffer);

    int readRasterDataF(long levelRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        float[] buffer);

    int readRasterDataD(long levelRef,
                        int componentIndex,
                        int resolutionLevel,
                        int x,
                        int y,
                        int width,
                        int height,
                        double[] buffer);

}
