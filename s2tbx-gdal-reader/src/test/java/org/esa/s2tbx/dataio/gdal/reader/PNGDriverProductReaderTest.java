package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.PNGDriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.reader.plugins.PNMDriverProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Jean Coravu
 */
public class PNGDriverProductReaderTest extends AbstractDriverProductReaderTest {

    public PNGDriverProductReaderTest() {
    }

    @Test
    public void testPNGReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("PNG-driver.png").toFile();

            PNGDriverProductReaderPlugIn readerPlugin = new PNGDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(4, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(1920, finalProduct.getSceneRasterWidth());
            assertEquals(1200, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(2);
            assertEquals(20, band.getDataType());
            assertEquals(2304000, band.getNumDataElems());

            float bandValue = band.getSampleFloat(620, 410);
            assertEquals(219.0f, bandValue, 0);

            bandValue = band.getSampleFloat(543, 444);
            assertEquals(213.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(79.0f, bandValue, 0);

            bandValue = band.getSampleFloat(270, 290);
            assertEquals(74.0f, bandValue, 0);

            bandValue = band.getSampleFloat(158, 335);
            assertEquals(75.0f, bandValue, 0);
        }
    }
}
