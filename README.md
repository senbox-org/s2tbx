Sentinel-2 Toolbox (s2tbx)
==========================

A toolbox for the MSI instrument on board of ESA's Sentinel-2 satellite.

Use the following command to build BEAM modules:
	mvn compile package install -U -DskipTests -Duser.language=eng

To create a Install4j installer use the following command:
    cd s2tbx-installer
	mvn compile package assembly:assembly -U -DskipTests -Duser.language=eng

Previous command creates a directory `s2tbx-installer/target/s2tbx-bin` containing all the files required to run SNAP S-2

Then open the file `s2tbx-installer/s2tbx.install4j` with Install4j and start the build (maybe you will be required to download JRE's first).