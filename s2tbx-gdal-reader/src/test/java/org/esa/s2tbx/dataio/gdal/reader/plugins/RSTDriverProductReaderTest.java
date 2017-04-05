package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.GDALProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductReaderTest extends AbstractTestDriverProductReader {

    public RSTDriverProductReaderTest() {
    }

    @Test
    public void testRSTReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("RST-driver.rst").toFile();

            RSTDriverProductReaderPlugIn readerPlugin = new RSTDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNotNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(20, finalProduct.getSceneRasterWidth());
            assertEquals(30, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(600, band.getNumDataElems());

            float bandValue = band.getSampleFloat(10, 20);
            assertEquals(155.0f, bandValue, 0);

            bandValue = band.getSampleFloat(12, 26);
            assertEquals(21.0f, bandValue, 0);

            bandValue = band.getSampleFloat(16, 16);
            assertEquals(81.0f, bandValue, 0);

            bandValue = band.getSampleFloat(7, 19);
            assertEquals(132.0f, bandValue, 0);

            bandValue = band.getSampleFloat(5, 13);
            assertEquals(10.0f, bandValue, 0);
        }
    }
}
