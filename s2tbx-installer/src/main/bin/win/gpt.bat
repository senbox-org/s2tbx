@echo off

set S2TBX_HOME=${installer:sys.installationDir}

"%S2TBX_HOME%\jre\bin\java.exe" ^
    -Xmx${installer:maxHeapSize} ^
    -Dceres.context=s2tbx ^
    "-Ds2tbx.mainClass=org.esa.beam.framework.gpf.main.GPT" ^
    "-Ds2tbx.home=%S2TBX_HOME%" ^
    "-Dncsa.hdf.hdflib.HDFLibrary.hdflib=%S2TBX_HOME%\modules\lib-hdf-${hdf.version}\lib\jhdf.dll" ^
    "-Dncsa.hdf.hdf5lib.H5.hdf5lib=%S2TBX_HOME%\modules\lib-hdf-${hdf.version}\lib\jhdf5.dll" ^
    -jar "%S2TBX_HOME%\bin\snap-launcher.jar" %*

exit /B %ERRORLEVEL%
