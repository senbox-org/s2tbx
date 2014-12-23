Sentinel-2 Toolbox (s2tbx)
==========================

A toolbox for the MSI instrument on board of ESA's Sentinel-2 satellite.

## Build s2tbx from scratch: ##

Clone the source code and related repositories into a folder (i.e `~/SNAP/`)

    cd ~/SNAP
	git clone https://github.com/senbox-org/snap.git
	git clone https://github.com/senbox-org/beam.git
	git clone https://github.com/senbox-org/ceres.git
	git clone https://github.com/senbox-org/s2tbx.git
	
Use the following command to build s2tbx modules:

    cd ~/SNAP/s2tbx
	mvn compile package install -U -DskipTests -Duser.language=eng

To create a Install4j installer use the following commands:

    cd ~/SNAP/snap
    mvn compile -DskipTests=true
    cd ~/SNAP/s2tbx
    mvn compile package install -U -DskipTests -Duser.language=eng
    cd ~/SNAP/s2tbx/s2tbx-installer
	mvn compile package assembly:assembly -U -DskipTests -Duser.language=eng

Previous command creates a directory `~/SNAP/s2tbx/s2tbx-installer/target/s2tbx-bin` containing all the files required to run SNAP S-2

Then open the file `~/SNAP/s2tbx/s2tbx-installer/s2tbx.install4j` with Install4j and start the build (maybe you will be required to download JRE's first).

## Debugging s2tbx: ##

Open the ~/SNAP/s2tbx/pom.xml file from within IntelliJ IDEA to import.
Use the following configuration to run DAT:

    * Main class: com.bc.ceres.launcher.Launcher
    * VM parameters: -Xmx4G -Dceres.context=snap
    * Program parameters: none
    * Working directory: ~/SNAP/output
    * Use classpath of module: snap-bootstrap

Enjoy!