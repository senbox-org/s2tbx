package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
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
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class ColorFillerOpTest {
    private Path colorFillerTestsFolderPath;
    private Product segmentationProduct;
    @Before
    public void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();

        Class<?> sentinelReaderPlugInClass = Class.forName("org.esa.snap.core.dataio.dimap.DimapProductReaderPlugIn");
        ProductReaderPlugIn productReaderPlugIn = (ProductReaderPlugIn)sentinelReaderPlugInClass.newInstance();

        File currentProductFile = this.colorFillerTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        this.segmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path "+testDirectoryPathProperty+" is not valid.");
        }

        this.colorFillerTestsFolderPath = testFolderPath.resolve("_forest-cover-change");
        if (!Files.exists(colorFillerTestsFolderPath)) {
            fail("The Forest Cover Change test directory path "+ colorFillerTestsFolderPath.toString()+" is not valid.");
        }
    }
}
