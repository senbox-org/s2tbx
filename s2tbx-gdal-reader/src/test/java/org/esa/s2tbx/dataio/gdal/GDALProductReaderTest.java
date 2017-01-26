package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.TestUtil;
import org.gdal.gdal.gdal;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * The system properties to set:
 * snap.reader.tests.data.dir : the test folder containing the '_gdal' sub-folder
 *
 * @author Jean Coravu
 */
public class GDALProductReaderTest {
    private GDALProductReaderPlugin plugIn;
    private Path gdalTestsFolderPath;

    @Before
    public void setUp() throws Exception {
        checkTestDirectoryExists();

        GDALInstaller installer = new GDALInstaller();
        installer.install();

        if (GdalInstallInfo.INSTANCE.isPresent()) {
            gdal.AllRegister(); // GDAL init drivers
            this.plugIn = new GDALProductReaderPlugin();
        }
    }

    @Test
    public void testJP2ReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("S2A_4.jp2").toFile();

            GDALProductReader reader = (GDALProductReader)this.plugIn.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNotNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(343, finalProduct.getSceneRasterWidth());
            assertEquals(343, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(117649, band.getNumDataElems());

            float bandValue = band.getSampleFloat(320, 110);
            assertEquals(18.0f, bandValue, 0);

            bandValue = band.getSampleFloat(333, 320);
            assertEquals(12.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(10.0f, bandValue, 0);

            bandValue = band.getSampleFloat(277, 298);
            assertEquals(9.0f, bandValue, 0);

            bandValue = band.getSampleFloat(297, 338);
            assertEquals(7.0f, bandValue, 0);
        }
    }

    @Test
    public void testNITFReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("U_1005A.NTF").toFile();

            GDALProductReader reader = (GDALProductReader)this.plugIn.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(64, finalProduct.getSceneRasterWidth());
            assertEquals(64, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(4096, band.getNumDataElems());

            float bandValue = band.getSampleFloat(32, 11);
            assertEquals(108.0f, bandValue, 0);

            bandValue = band.getSampleFloat(33, 32);
            assertEquals(217.0f, bandValue, 0);

            bandValue = band.getSampleFloat(30, 30);
            assertEquals(211.0f, bandValue, 0);

            bandValue = band.getSampleFloat(27, 29);
            assertEquals(224.0f, bandValue, 0);

            bandValue = band.getSampleFloat(15, 33);
            assertEquals(133.0f, bandValue, 0);
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
