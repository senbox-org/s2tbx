package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu
 */
public abstract class AbstractOpTest {
    protected Path forestCoverChangeTestsFolderPath;

    protected AbstractOpTest() {
    }

    @Before
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();
    }

    protected static final ProductReaderPlugIn buildDimapProductReaderPlugIn() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
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

        this.forestCoverChangeTestsFolderPath = testFolderPath.resolve("_forest-cover-change");
        if (!Files.exists(forestCoverChangeTestsFolderPath)) {
            fail("The Forest Cover Change test directory path "+ forestCoverChangeTestsFolderPath.toString()+" is not valid.");
        }
    }
}
