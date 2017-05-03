package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.GDALProductReader;
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
public class HFADriverProductReaderTest extends AbstractTestDriverProductReader {

    public HFADriverProductReaderTest() {
    }

    @Test
    public void testHFAReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("HFA-driver.img").toFile();

            HFADriverProductReaderPlugIn readerPlugin = new HFADriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(768, finalProduct.getSceneRasterWidth());
            assertEquals(512, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(1);
            assertEquals(20, band.getDataType());
            assertEquals(393216, band.getNumDataElems());

            float bandValue = band.getSampleFloat(620, 410);
            assertEquals(53.0f, bandValue, 0);

            bandValue = band.getSampleFloat(543, 444);
            assertEquals(109.0f, bandValue, 0);

            bandValue = band.getSampleFloat(321, 379);
            assertEquals(60.0f, bandValue, 0);

            bandValue = band.getSampleFloat(276, 299);
            assertEquals(170.0f, bandValue, 0);

            bandValue = band.getSampleFloat(158, 335);
            assertEquals(116.0f, bandValue, 0);
        }
    }
}
