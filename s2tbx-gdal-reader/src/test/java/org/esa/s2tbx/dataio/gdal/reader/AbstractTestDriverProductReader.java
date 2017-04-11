package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GDALInstaller;
import org.esa.s2tbx.dataio.gdal.GDALUtils;
import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.activator.GDALDistributionInstaller;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * The system properties to set:
 * snap.reader.tests.data.dir : the test folder containing the '_gdal' sub-folder
 *
 * @author Jean Coravu
 */
public abstract class AbstractTestDriverProductReader {
    protected Path gdalTestsFolderPath;

    protected AbstractTestDriverProductReader() {
    }

    @Before
    public void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());

        if (!GdalInstallInfo.INSTANCE.isPresent()) {
            GDALDistributionInstaller.install();
            if (GdalInstallInfo.INSTANCE.isPresent()) {
                GDALUtils.initDrivers();
                checkTestDirectoryExists();
            }
        }
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }

        this.gdalTestsFolderPath = testFolderPath.resolve("_gdal");
        if (!Files.exists(gdalTestsFolderPath)) {
            fail("The GDAL test directory path '"+gdalTestsFolderPath.toString()+"' is not valid.");
        }
    }
}
