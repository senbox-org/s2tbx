#! /bin/sh

export S2TBX_HOME=${installer:sys.installationDir}

if [ ! -d "${S2TBX_HOME}" ]
then
    PRGDIR=`dirname $0`
    export S2TBX_HOME=`cd "${PRGDIR}/.." ; pwd`
fi

if [ -z "${S2TBX_HOME}" ]; then
    echo
    echo Error:
    echo S2TBX_HOME does not exists in your environment. Please
    echo set the S2TBX_HOME variable in your environment to the
    echo location of your SNAP installation.
    echo
    exit 2
fi

. "S2TBX_HOME/bin/detect_java.sh"

"${app_java_home}/bin/java" \
    -Xmx1024M \
    -Dceres.context=s2tbx \
    "-Ds2tbx.mainClass=org.esa.beam.smos.visat.export.GridPointExporter" \
    "-Ds2tbx.home=${S2TBX_HOME}" \
    -jar "${S2TBX_HOME}/bin/snap-launcher.jar" "$@"

exit 0