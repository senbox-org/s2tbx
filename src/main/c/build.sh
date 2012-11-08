#!/bin/bash
#
# TODO: get a C guy to make this with cmake / make
# This build script might be removed later... but it doesn't really need to be replaced because this build is so simple.
# also, the -I library paths are hardcoded... the openjpeg one has a version number in the path

mode=
verbose=
debug=

# parse command line options
while [ "$#" != 0 ]; do
    arg="$1"
    if [ "$arg" = "-v" ]; then
        verbose="$arg"
    elif [ "$arg" = "-g" ]; then
        debug="$arg"
    elif [ "$arg" = "-ggdb" ]; then
        debug="$arg"
    elif [ -n "$mode" ]; then
        echo "ERROR: too many arguments: $arg"
        exit 1
    else
        mode="$arg"
    fi
    shift
done

#gcc () {
#echo gcc "$@"
#}

#OPENJPEG_INCLUDE=-I/usr/local/include/openjpeg-1.99
OPENJPEG_INCLUDE=-I/usr/include/openjpeg-2.0

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
# works
    gcc $verbose $debug "$OPENJPEG_INCLUDE" -L. -o pathtest pathtest.c -ljna_openjpeg -lopenjpeg
}

build() {
    #compile as a shared lib
    gcc $verbose $debug -c -fpic "$OPENJPEG_INCLUDE" jna_openjpeg.c -lopenjpeg
    gcc $verbose $debug -shared -o libjna_openjpeg.so jna_openjpeg.o -lopenjpeg

    gcc $verbose $debug -c -fpic -I/usr/local/include/jasper jna_jasper.c -ljasper
    gcc $verbose $debug -shared -o libjna_jasper.so jna_jasper.o -ljasper

    if ! ldd -r libjna_openjpeg.so >/dev/null 2>&1; then
        echo "OpenJPEG failed:"
        ldd -r libjna_openjpeg.so
    fi
    if ! ldd -r libjna_jasper.so >/dev/null 2>&1; then
        echo "JasPer failed:"
        ldd -r libjna_jasper.so
    fi
}

if [ "$mode" = "all" ]; then
    clean
    build
    buildTest
elif [ "$mode" = "clean" ]; then
    clean
elif [ "$mode" = "build" ]; then
    build
elif [ "$mode" = "buildtest" ]; then
    buildTest
else
    clean
    build
    buildTest
fi

