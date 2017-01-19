package org.esa.s2tbx.dataio.gdal;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jean Coravu
 */
public abstract class AbstractGDALPlugInTest extends TestCase {

    protected AbstractGDALPlugInTest() {
    }

    @Override
    protected void setUp() throws Exception {
        checkGDALDistributionRootFolder();
    }

    private void checkGDALDistributionRootFolder() throws IOException {
        String gdalDistributionPropertyName = "gdal.distribution.root.dir";

        String gdalDistributionFolderPathProperty = System.getProperty(gdalDistributionPropertyName);
        assertNotNull("The system property '" + gdalDistributionPropertyName + "' representing the directory with the GDAL distribution is not set.", gdalDistributionFolderPathProperty);
        Path gdalDistributionRootFolderPath = Paths.get(gdalDistributionFolderPathProperty);
        if (!gdalDistributionRootFolderPath.toFile().isDirectory()) {
            fail("The directory path containing the distribution '"+gdalDistributionFolderPathProperty+"' is not valid.");
        }

        GDALInstaller installer = new GDALInstaller();
        installer.processInstalledDistribution(gdalDistributionRootFolderPath);
    }
}
