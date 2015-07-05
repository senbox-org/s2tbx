Sentinel-2 Toolbox (s2tbx)
==========================

A toolbox for the MSI instrument on board of ESA's Sentinel-2 satellite.

## Build s2tbx from scratch: ##

Clone the source code and related repositories into a folder (i.e `~/SNAP/`)

    cd ~/SNAP
	git clone https://github.com/senbox-org/snap-desktop.git
	git clone https://github.com/senbox-org/s2tbx.git
	
Use the following command to build s2tbx modules using checked out snap-desktop:

    cd ~/SNAP/s2tbx
	mvn clean package -DskipTests=true -P withSnapDesktopSources
	
	cd ~/SNAP/snap-desktop
	mvn clean install -DskipTests=true
	cd ~/SNAP/snap-desktop/snap-application
	mvn nbm:cluster-app

Use the following command to build s2tbx modules using already installed snap-desktop:

    cd ~/SNAP/s2tbx
	mvn clean package -DskipTests=true

## Debugging s2tbx: ##

Open the ~/SNAP/s2tbx/pom.xml file from within IntelliJ IDEA to import.
Use the following configuration to run DAT:

    * Main class: org.esa.snap.nbexec.Launcher
    * VM parameters: -Dsun.java2d.noddraw=true -Dsun.awt.nopixfmt=true -Dsun.java2d.dpiaware=false -Dorg.netbeans.level=INFO -Xmx4G
    * Program parameters: --clusters "~/SNAP/s2tbx/target/nbm/netbeans/extra"
    * Working directory: ~/SNAP/snap-desktop/snap-application/target/snap
    * Use classpath of module: snap-main

Enjoy!
