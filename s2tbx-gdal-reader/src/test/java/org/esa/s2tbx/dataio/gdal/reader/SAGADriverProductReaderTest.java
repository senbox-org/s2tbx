package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.KEADriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.reader.plugins.SAGADriverProductReaderPlugIn;
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
public class SAGADriverProductReaderTest extends AbstractDriverProductReaderTest {

    public SAGADriverProductReaderTest() {
    }

    @Test
    public void testSAGAReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("SAGA-driver.sdat").toFile();

            SAGADriverProductReaderPlugIn readerPlugin = new SAGADriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(1024, finalProduct.getSceneRasterWidth());
            assertEquals(1024, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(1048576, band.getNumDataElems());

            float bandValue = band.getSampleFloat(32, 11);
            assertEquals(121.0f, bandValue, 0);

            bandValue = band.getSampleFloat(33, 32);
            assertEquals(118.0f, bandValue, 0);

            bandValue = band.getSampleFloat(30, 30);
            assertEquals(123.0f, bandValue, 0);

            bandValue = band.getSampleFloat(27, 29);
            assertEquals(141.0f, bandValue, 0);

            bandValue = band.getSampleFloat(15, 33);
            assertEquals(147.0f, bandValue, 0);
        }
    }
}
