package org.esa.s2tbx.dataio.jp2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2ProductWriterTest {
    private final String FILE_NAME = "test_product.jp2";
    private JP2ProductWriter _productWriter;
    private Product _product;
    private static Path testsFolderPath;

    @BeforeClass
    public static void oneTimeSetUp() throws IOException {
        Path temp = Files.createTempDirectory("_temp");
        testsFolderPath = temp;
        if (!Files.exists(testsFolderPath)) {
            fail("The test directory path '"+temp+"' is not valid.");
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        if (!FileUtils.deleteTree(testsFolderPath.toFile())) {
            fail("Unable to delete test directory");
        }
    }

    @Before
    public void setUp() throws Exception {
        new File(testsFolderPath.toFile(), FILE_NAME).delete();

        _productWriter = new JP2ProductWriter(new JP2ProductWriterPlugIn());

        _product = new Product("temp", "type", 3, 3);
        _product.addBand("band_01", ProductData.TYPE_UINT16);
        fillBandWithData(_product.getBand("band_01"), 1);
    }

    @After
    public void tearDown() throws Exception {
        _productWriter.close();
        new File(testsFolderPath.toFile(), FILE_NAME).delete();
    }

    @Test
    public void testJP2ProductWriterCreation() {
        final JP2ProductWriter productWriter = new JP2ProductWriter(new JP2ProductWriterPlugIn());
        assertNotNull(productWriter.getWriterPlugIn());
    }

    @Test
    public void testThatStringIsAValidOutput() throws IOException {
        _productWriter.writeProductNodes(_product, new File(testsFolderPath.toFile(), FILE_NAME));
    }

    @Test
    public void testThatFileIsAValidOutput() throws IOException {
        _productWriter.writeProductNodes(_product, new File(testsFolderPath.toFile(), FILE_NAME));
    }

    @Test
    public void testWriteProductNodes_ChangeFileSize() throws IOException {
        File writtenFile = new File(testsFolderPath.toFile(), FILE_NAME);
        _productWriter.writeProductNodes(_product, writtenFile);
        assertTrue(writtenFile.length() == 0);
        writeBand(_product);
        _productWriter.close();
        assertTrue(writtenFile.length() > 0);
    }

    private void fillBandWithData(final Band band, final int start)throws IOException {
        final ProductData data = band.createCompatibleRasterData();
        for (int i = 0; i < band.getRasterWidth() * band.getRasterHeight(); i++) {
            data.setElemIntAt(i, start + i);
        }
        band.setData(data);
    }

    private void writeBand(final Product product) throws IOException {
        final int width = product.getSceneRasterWidth();
        final int height = product.getSceneRasterHeight();
        final Band[] bands = product.getBands();
        for (Band band : bands) {
            _productWriter.writeBandRasterData(band, 0, 0, width, height, band.getData(), ProgressMonitor.NULL);
        }
    }
}
