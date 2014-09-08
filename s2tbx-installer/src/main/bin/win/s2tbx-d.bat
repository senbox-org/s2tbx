@echo off

set S2TBX_HOME=${installer:sys.installationDir}

"%S2TBX_HOME%\jre\bin\java.exe" ^
    -Xmx${installer:maxHeapSize} ^
    -Dceres.context=s2tbx ^
    -Ds2tbx.debug=true ^
    "-Ds2tbx.home=%S2TBX_HOME%" ^
    -jar "%S2TBX_HOME%\bin\snap-launcher.jar" -d %*

exit /B %ERRORLEVEL%
