
typedef unsigned long long Image;
typedef unsigned long long Level;



int testFunction(char *message);
char* testFunction2();
extern void testFunction3();

/**
 * Opens an image.
 *
 * @param file The image file.
 * @return The image reference.
 */
Image openImage(const char* file_path);

/**
 * Disposes the image an all associated resources.
 *
 * @param imageRef The image.
 */
void disposeImage(Image imageRef);

/**
 * @param imageRef The image.
 * @return The number of resolution levels in the image.
 */
int getNumResolutionLevels(Image imageRef);


/**
 * @param imageRef The image.
 * @return The number of components (bands, channels) in the image.
 */
long getNumComponents(Image imageRef);

/**
 * @param imageRef       The image.
 * @param componentIndex The component index.
 * @return The sample data type according to {@link java.awt.image.DataBuffer}
 */
int getSampleDataType(Image imageRef, int componentIndex);

/**
 * @param imageRef        The image.
 * @param resolutionIndex The resolution level.
 * @return The image width for the given resolution level.
 */
long getImageWidth(Image imageRef, int resolutionIndex);

/**
 * @param imageRef        The image.
 * @param resolutionIndex The resolution level.
 * @return The image height for the given resolution level.
 */
long getImageHeight(Image imageRef, int resolutionIndex);

/**
 * @param imageRef The image.
 * @return The number of resolution levels in the image.
 */
Level getResolutionLevel(Image imageRef, int resolutionIndex);

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
 *                        according to {@link #getSampleDataType(long, int)}.
 *                        The size of this array must be equal to {@code width * height} of
 *                        the region rectangle.
 *                        Example: if the sample data type is DataBuffer.TYPE_INT, then a buffer of
 *                        type {@code int[width * height]} is expected here.
 * @return an error code (TBD).
 */
int readRasterDataB(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    char buffer[]);
int readRasterDataS(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    short buffer[]);
int readRasterDataI(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    int buffer[]);
int readRasterDataF(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    float buffer[]);
int readRasterDataD(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    double buffer[]);
