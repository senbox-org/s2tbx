#include <stdio.h>
#include <stddef.h>
#include <jasper.h>
#include "jna_image_access.h"

int testFunction(char *message) {
    printf("in C libjna_jasper testFunction(message) - hello %s!", message);
    return 0;
}

