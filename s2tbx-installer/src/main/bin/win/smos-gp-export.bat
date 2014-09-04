@echo off

set S2TBX_HOME=${installer:sys.installationDir}

"%S2TBX_HOME%\jre\bin\java.exe" ^
    -Xmx1024M ^
    -Dceres.context=s2tbx ^
    "-Ds2tbx.mainClass=org.esa.beam.smos.visat.export.GridPointExporter" ^
    "-Ds2tbx.home=%S2TBX_HOME%" ^
    -jar "%S2TBX_HOME%\bin\snap-launcher.jar" %*

exit /B 0
