package org.esa.s2tbx.mapper;

import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assume.assumeTrue;

/**
 * @author Razvan Dumitrascu
 */

public class AbstractOpTest {
    protected Path SpectralAngleMapperTestsFolderPath;

    protected AbstractOpTest() {
    }

    @Before
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();
    }

    protected static ProductReaderPlugIn buildDimapProductReaderPlugIn() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> sentinelReaderPlugInClass = Class.forName("org.esa.snap.core.dataio.dimap.DimapProductReaderPlugIn");
        return (ProductReaderPlugIn)sentinelReaderPlugInClass.newInstance();
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path "+testDirectoryPathProperty+" is not valid.");
        }

        this.SpectralAngleMapperTestsFolderPath = testFolderPath.resolve("_spectral_angle_mapper");
        if (!Files.exists(SpectralAngleMapperTestsFolderPath)) {
            fail("The Spectral Angle Mapper test directory path "+ SpectralAngleMapperTestsFolderPath.toString()+" is not valid.");
        }
    }
}
