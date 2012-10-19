#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <openjpeg.h>
#include "jna_image_access.h"


/** Test functions... they will be removed later */
int testFunction(char *message) {
    printf("in C - testing slashes:   /path/to/file/ \n", message);
    if( strcmp(message,"test1/test2") == 0 ) {
        printf("string matched test\n");
    }
    return 0;
}

char* testFunction2() {
    return "/this/is/a/test/path";
}

void testFunction3() {
    char* nullpointer = 0;
    nullpointer[5]='a';
}
/** end of Test functions... they will be removed later */


/**
 * Opens an image.
 *
 * this mostly maps to:
 *    OPJ_API opj_bool OPJ_CALLCONV opj_decode(   opj_codec_t *p_decompressor,
 *                                                   opj_stream_t *p_stream,
 *                                                   opj_image_t *p_image);
 *
 * @param file The image file.
 * @return The image reference.
 */
Image openImage(const char* file_path) {
    printf("todo: not fully implemented (always returns 0)\n");

    FILE * p_file = fopen(file_path, "r");
    opj_bool p_is_read_stream = 1; // typedef int opj_bool

    opj_stream_t* p_stream = opj_stream_create_default_file_stream( p_file, p_is_read_stream );

    opj_codec_t *p_decompressor = opj_create_decompress(CODEC_J2K);

    opj_image_t *p_image = malloc( sizeof(opj_image_t) );
    opj_bool status = opj_decode( p_decompressor, p_stream, p_image );

    if( status ) {
        return (Image)p_image;
    }else{
        return (Image)0;
    }
}

/**
 * Disposes the image an all associated resources.
 *
 * This maps to openjpeg's
 *     OPJ_API void OPJ_CALLCONV opj_image_destroy(opj_image_t *image);
 *
 * @param imageRef The image.
 */
void disposeImage(Image imageRef) {
    if( imageRef == 0 ) {
        return;
    }
    opj_image_t *image = (opj_image_t*) imageRef;
    opj_image_destroy(image);
}

/**
 * @param imageRef The image.
 * @return The number of resolution levels in the image.
 */
int getNumResolutionLevels(Image imageRef) {
    printf("not implemented\n");
}


/**
 * @param imageRef The image.
 * @return The number of components (bands, channels) in the image.
 */
long getNumComponents(Image imageRef) {
    if( imageRef == 0 ) {
        return;
    }
    opj_image_t *p_image = (opj_image_t*)imageRef;

    return (long)p_image->numcomps;
}


/**
 * @param imageRef       The image.
 * @param componentIndex The component index.
 * @return The sample data type according to {@link java.awt.image.DataBuffer}
 */
int getSampleDataType(Image imageRef, int componentIndex) {
    printf("not implemented\n");
}

/**
 * @param imageRef        The image.
 * @param resolutionIndex The resolution level.
 * @return The image width for the given resolution level.
 */
long getImageWidth(Image imageRef, int resolutionIndex) {
    if( imageRef == 0 ) {
        return -1;
    }
    opj_image_t *p_image = (opj_image_t*)imageRef;
    // TODO: truncating UINT_32

    printf("C - image width = %ld\n", p_image->x1);
    return (long)p_image->x1;
}

/**
 * @param imageRef        The image.
 * @param resolutionIndex The resolution level.
 * @return The image height for the given resolution level.
 */
long getImageHeight(Image imageRef, int resolutionIndex) {
    if( imageRef == 0 ) {
        return -1;
    }
    opj_image_t *p_image = (opj_image_t*)imageRef;
    // TODO: truncating UINT_32
    return (long)p_image->y1;
}

/**
 * @param imageRef The image.
 * @return The number of resolution levels in the image.
 */
Level getResolutionLevel(Image imageRef, int resolutionIndex) {
    printf("not implemented\n");
}

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
                    char buffer[]) {
    printf("not implemented\n");
}

int readRasterDataS(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    short buffer[]) {
    printf("not implemented\n");
}

int readRasterDataI(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    int buffer[]) {
    printf("not implemented\n");
}

int readRasterDataF(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    float buffer[]) {
    printf("not implemented\n");
}

int readRasterDataD(Level levelRef,
                    int componentIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    double buffer[]) {
    printf("not implemented\n");
}
