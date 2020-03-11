package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.lib.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.dataio.gdal.GDALLoader;
import org.esa.snap.utils.TestUtil;
import org.junit.Assume;
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
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());

        //TODO Jean do not use the GDAL distribution until fixing the bug to check if the installed version is valid
//        if (!GDALInstallInfo.INSTANCE.isPresent()) {
//            Path gdalDistributionRootFolderPath = GDALLoader.getInstance().initGDAL();
//            Assume.assumeNotNull(gdalDistributionRootFolderPath);
//        }
//        if (GDALInstallInfo.INSTANCE.isPresent()) {
//            checkTestDirectoryExists();
//        }
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }
        this.gdalTestsFolderPath = testFolderPath.resolve("_gdal");
        if (!Files.exists(this.gdalTestsFolderPath)) {
            fail("The GDAL test directory path '"+this.gdalTestsFolderPath.toString()+"' is not valid.");
        }
    }
}
