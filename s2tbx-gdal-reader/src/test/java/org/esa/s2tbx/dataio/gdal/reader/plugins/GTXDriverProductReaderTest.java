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
public class GTXDriverProductReaderTest extends AbstractTestDriverProductReader {

    public GTXDriverProductReaderTest() {
    }

    @Test
    public void testGTXReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("GTX-driver.gtx").toFile();

            GTXDriverProductReaderPlugIn readerPlugin = new GTXDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNotNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(20, finalProduct.getSceneRasterWidth());
            assertEquals(30, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(30, band.getDataType());
            assertEquals(600, band.getNumDataElems());

            float bandValue = band.getSampleFloat(10, 15);
            assertEquals(311.0f, bandValue, 0);

            bandValue = band.getSampleFloat(13, 18);
            assertEquals(374.0f, bandValue, 0);

            bandValue = band.getSampleFloat(11, 19);
            assertEquals(392.0f, bandValue, 0);

            bandValue = band.getSampleFloat(12, 28);
            assertEquals(573.0f, bandValue, 0);

            bandValue = band.getSampleFloat(15, 18);
            assertEquals(376.0f, bandValue, 0);
        }
    }
}
