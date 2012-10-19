#!/bin/bash
#
# TODO: get a C guy to make this with cmake / make
# This build script might be removed later... but it doesn't really need to be replaced because this build is so simple.
# also, the -I library paths are hardcoded... the openjpeg one has a version number in the path

mode=
verbose=

# parse command line options
while [ "$#" != 0 ]; do
    arg="$1"
    if [ "$arg" = "-v" ]; then
        verbose="$arg"
    elif [ -n "$mode" ]; then
        echo "ERROR: too many arguments: $arg"
        exit 1
    else
        mode="$arg"
    fi
    shift
done


#quiet remove
rmq() {
    file="$1"
    if [ -f "$file" ]; then
        rm "$file"
    fi
}

clean() {
    rmq jna_openjpeg.o
    rmq libjna_openjpeg.so
    rmq jna_jasper.o
    rmq libjna_jasper.so
    rmq pathtest
}

buildTest() {
#    gcc $verbose -I/usr/local/include/openjpeg-1.99 -L. -o pathtest pathtest.c -ljna_openjpeg -lopenjpeg
    gcc $verbose -I/usr/local/include/openjpeg-1.99 -L. -o pathtest pathtest.c -ljna_openjpeg
}

build() {
    #compile as a shared lib
    gcc $verbose -c -fpic -I/usr/local/include/openjpeg-1.99 jna_openjpeg.c -lopenjpeg
    gcc $verbose -shared -o libjna_openjpeg.so jna_openjpeg.o -lopenjpeg

    gcc $verbose -c -fpic -I/usr/local/include/jasper jna_jasper.c -ljasper
    gcc $verbose -shared -o libjna_jasper.so jna_jasper.o -ljasper

    echo "Checking to see that the libraries have no unresolved dependencies:"
    echo "OpenJPEG:"
    ldd -r libjna_openjpeg.so
    echo $?
    echo "JasPer:"
    ldd -r libjna_jasper.so
    echo $?
}

if [ "$mode" = "all" ]; then
    clean
    build
    buildTest
elif [ "$mode" = "clean" ]; then
    clean
elif [ "$mode" = "buildtest" ]; then
    buildTest
else
    build
fi

