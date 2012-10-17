#include <stdio.h>
#include <stddef.h>
#include <openjpeg.h>
#include "jna_image_access.h"


/** Test functions... they will be removed later */
int testFunction(char *message) {
    printf("in C libjna_openjpeg testFunction(message) - hello %s C/test/path/blah/\n", message);
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
 * this maps to openjpeg's:
 *    ???
 *
 * @param file The image file.
 * @return The image reference.
 */
Image openImage(const char* file_path) {
    printf("todo: not implemented");

    FILE * p_file = fopen(file_path, "r");
    opj_bool p_is_read_stream = 1; // typedef int opj_bool

    opj_stream_t* stream_t = opj_stream_create_default_file_stream( p_file, p_is_read_stream );

//Test return! value is junk
    return (Image)10;
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
    opj_image_t *image = (opj_image_t*) imageRef;
    opj_image_destroy(image);
}





