package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.KEADriverProductReaderPlugIn;
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
public class KEADriverProductReaderTest extends AbstractTestDriverProductReader {

    public KEADriverProductReaderTest() {
    }

    @Test
    public void testKEAReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("KEA-driver.kea").toFile();

            KEADriverProductReaderPlugIn readerPlugin = new KEADriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(3072, finalProduct.getSceneRasterWidth());
            assertEquals(2048, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(21, band.getDataType());
            assertEquals(6291456, band.getNumDataElems());

            float bandValue = band.getSampleFloat(32, 11);
            assertEquals(2426.0f, bandValue, 0);

            bandValue = band.getSampleFloat(33, 32);
            assertEquals(2649.0f, bandValue, 0);

            bandValue = band.getSampleFloat(30, 30);
            assertEquals(2049.0f, bandValue, 0);

            bandValue = band.getSampleFloat(27, 29);
            assertEquals(2240.0f, bandValue, 0);

            bandValue = band.getSampleFloat(15, 33);
            assertEquals(2898.0f, bandValue, 0);
        }
    }
}
