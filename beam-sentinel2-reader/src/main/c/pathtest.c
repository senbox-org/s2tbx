/*
 * this is a simple test to track down issues with linking shared objects without a JNA layer in between confusing things
 *
 */

#include <stdio.h>
#include <stddef.h>
#include <openjpeg.h>
#include "jna_image_access.h"

int main(int argc, char**argv) {
//    const char* file_path = 0;
//    openImage(file_path);

    char* message = testFunction2();
    printf("%s\n", message);

//    testFunction3();

    Image image = openImage("/opt/imageaccess/Cevennes1.j2k");

    printf("image pointer is %ld\n", (long)image);

    return 0;
}

