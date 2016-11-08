package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.glevel.MultiLevelImage;
import junit.framework.TestCase;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.TestUtil;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu
 */
public class GDALProductReaderTest extends TestCase {
    private final GDALProductReader reader;
    private final String productsFolder;

    public GDALProductReaderTest() {
        assumeTrue(TestUtil.testdataAvailable());

        GDALProductReaderPlugin plugIn = new GDALProductReaderPlugin();
        this.reader = (GDALProductReader)plugIn.createReaderInstance();

        this.productsFolder = "_gdal" + File.separator;
    }

    public void testReadProductNodes() {
        File file = TestUtil.getTestFile(this.productsFolder + "3_8bit_components_srgb.jp2");
        try {
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(768, finalProduct.getSceneRasterWidth());
            assertEquals(512, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(393216, band.getNumDataElems());

            MultiLevelImage multiLevelImage = band.getSourceImage();
            RenderedImage image = multiLevelImage.getImage(0);
            assertEquals(768, image.getWidth());
            assertEquals(512, image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }
}
