Sentinel-2 Toolbox (S2TBX)
==========================

A toolbox for the MSI instruments on board of ESA's Sentinel-2 satellite.

[![Build Status](https://travis-ci.org/senbox-org/s2tbx.svg?branch=master)](https://travis-ci.org/senbox-org/s2tbx) [![Coverity Scan Status](https://scan.coverity.com/projects/7175/badge.svg)](https://scan.coverity.com/projects/senbox-org-s2tbx)

Building S2TBX from the source
------------------------------

Download and install the required build tools
	* Install J2SE 1.8 JDK and set JAVA_HOME accordingly. 
	* Install Maven and set MAVEN_HOME accordingly. 
	* Install git

Add $JAVA_HOME/bin, $MAVEN_HOME/bin to your PATH.

Clone the S2TBX source code and related repositories into a directory referred to a ${snap} from here on

    cd ${snap}
    git clone https://github.com/senbox-org/s2tbx.git
    git clone https://github.com/senbox-org/snap-desktop.git
    git clone https://github.com/senbox-org/snap-engine.git
    
Build SNAP-Engine:

    cd ${snap}/snap-engine
    mvn install

Build SNAP-Desktop:

    cd ${snap}/snap-desktop
    mvn install

Build Sentinel-2 Toolbox:

    cd ${snap}/s2tbx
    mvn install
   
If unit tests are failing, you can use the following to skip the tests
   
    mvn clean
    mvn install -Dmaven.test.skip=true
	
Setting up IntelliJ IDEA
------------------------

1. Create an empty project with the ${snap} directory as project directory

2. Import the pom.xml files of snap-engine, snap-desktop and s2tbx as modules. Ensure **not** to enable
the option *Create module groups for multi-module Maven projects*. Everything can be default values.

3. Set the used SDK for the main project. A JDK 1.8 or later is needed.

4. Use the following configuration to run SNAP in the IDE:

    **Main class:** org.esa.snap.nbexec.Launcher
    **VM parameters:** -Dsun.awt.nopixfmt=true -Dsun.java2d.noddraw=true -Dsun.java2d.dpiaware=false
    All VM parameters are optional
    **Program arguments:**
    --userdir
    "${snap}/s2tbx/target/userdir"
    --clusters
    "${snap}/s2tbx/s2tbx-kit/target/netbeans_clusters/s2tbx"
    --patches
    "${snap}/snap-engine/$/target/classes;${snap}/s2tbx/$/target/classes"
    **Working directory:** ${snap}/snap-desktop/snap-application/target/snap/
    **Use classpath of module:** snap-main

Enjoy!


