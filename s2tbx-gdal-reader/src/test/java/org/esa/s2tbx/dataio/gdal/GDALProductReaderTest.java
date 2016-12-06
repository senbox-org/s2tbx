package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.glevel.MultiLevelImage;
import junit.framework.TestCase;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.utils.NativeLibraryUtils;
import org.esa.snap.utils.TestUtil;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * The system properties to set:
 * gdal.bin.dir : the folder containing the GDAL binaries
 * gdal.jni.libs.dir : the folder containing the following libraries: gdaljni.dll, gdalconstjni.dll, ogrjni.dll, osrjni.dll
 * snap.reader.tests.data.dir : the folder containing the '_gdal' sub-folder
 *
 * @author Jean Coravu
 */
public class GDALProductReaderTest extends AbstractGDALPlugInTest {
    private final String productsFolder;
    private GDALProductReaderPlugin plugIn;

    public GDALProductReaderTest() {

        this.productsFolder = "_gdal" + File.separator;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        checkTestDirectoryExists();

        this.plugIn = new GDALProductReaderPlugin();
    }

    public void testReadProductNodes() throws IOException {
        File file = TestUtil.getTestFile(this.productsFolder + "S2A_4.jp2");

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
        assertEquals(18.0f, bandValue);

        bandValue = band.getSampleFloat(333, 320);
        assertEquals(12.0f, bandValue);

        bandValue = band.getSampleFloat(300, 300);
        assertEquals(10.0f, bandValue);

        bandValue = band.getSampleFloat(277, 298);
        assertEquals(9.0f, bandValue);

        bandValue = band.getSampleFloat(297, 338);
        assertEquals(7.0f, bandValue);
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        File testFolder = new File(testDirectoryPathProperty);
        if (!testFolder.isDirectory()) {
            fail("The test directory path '"+testDirectoryPathProperty+"' is not valid.");
        }
    }
}
