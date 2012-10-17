#!/bin/bash
#
# TODO: get a C guy to make this with cmake / make
# This build script might be removed later... but it doesn't really need to be replaced because this build is so simple.
# also, the -I library paths are hardcoded... the openjpeg one has a version number in the path

mode="$1"

#quiet remove
rmq() {
    file="$1"
    if [ -f "$file" ]; then
        rm "$file"
    fi
}

if [ "$mode" = "clean" ]; then
    rmq jna_openjpeg.o
    rmq libjna_openjpeg.so
    rmq jna_jasper.o
    rmq libjna_jasper.so
elif [ "$mode" = "buildtest" ]; then
    gcc -I/usr/local/include/openjpeg-1.99 -L. -lopenjpeg -ljna_openjpeg -o pathtest pathtest.c
else
    #compile as a shared lib

    gcc -c -fpic -I/usr/local/include/openjpeg-1.99 jna_openjpeg.c
    gcc -shared -o libjna_openjpeg.so jna_openjpeg.o

    gcc -c -fpic -I/usr/local/include/jasper jna_jasper.c
    gcc -shared -o libjna_jasper.so jna_jasper.o
fi

