package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.glevel.MultiLevelImage;
import junit.framework.TestCase;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.TestUtil;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu
 */
public class GDALProductReaderTest extends TestCase {
    private final GDALProductReader reader;
    private final String productsFolder;

    public GDALProductReaderTest() {
        GDALProductReaderPlugin plugIn = new GDALProductReaderPlugin();
        this.reader = (GDALProductReader)plugIn.createReaderInstance();

        this.productsFolder = "_gdal" + File.separator;
    }

    public void testReadProductNodes() {
        checkTestDirectoryExists();

        File file = TestUtil.getTestFile(this.productsFolder + "S2A_4.jp2");
        try {
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
            assertEquals(18.0f, bandValue);

            bandValue = band.getSampleFloat(333, 320);
            assertEquals(12.0f, bandValue);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(10.0f, bandValue);

            bandValue = band.getSampleFloat(277, 298);
            assertEquals(9.0f, bandValue);

            bandValue = band.getSampleFloat(297, 338);
            assertEquals(7.0f, bandValue);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The test directory path is not set as system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "'.", testDirectoryPathProperty);
        File testFolder = new File(testDirectoryPathProperty);
        if (!testFolder.isDirectory()) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }
    }
}
