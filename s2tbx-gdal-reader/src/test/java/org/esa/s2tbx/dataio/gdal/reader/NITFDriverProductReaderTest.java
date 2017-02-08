package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.JP2OpenJPEGDriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.reader.plugins.NITFDriverProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductReaderTest extends AbstractDriverProductReaderTest {

    public NITFDriverProductReaderTest() {
    }

    @Test
    public void testNITFReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("NITF-driver.ntf").toFile();

            NITFDriverProductReaderPlugIn readerPlugin = new NITFDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(64, finalProduct.getSceneRasterWidth());
            assertEquals(64, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(4096, band.getNumDataElems());

            float bandValue = band.getSampleFloat(32, 11);
            assertEquals(108.0f, bandValue, 0);

            bandValue = band.getSampleFloat(33, 32);
            assertEquals(217.0f, bandValue, 0);

            bandValue = band.getSampleFloat(30, 30);
            assertEquals(211.0f, bandValue, 0);

            bandValue = band.getSampleFloat(27, 29);
            assertEquals(224.0f, bandValue, 0);

            bandValue = band.getSampleFloat(15, 33);
            assertEquals(133.0f, bandValue, 0);
        }
    }
}
