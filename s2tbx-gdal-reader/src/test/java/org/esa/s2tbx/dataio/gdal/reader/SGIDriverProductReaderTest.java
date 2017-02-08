package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.SGIDriverProductReaderPlugIn;
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
public class SGIDriverProductReaderTest extends AbstractTestDriverProductReader {

    public SGIDriverProductReaderTest() {
    }

    @Test
    public void testSGIReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("KRO-driver.kro").toFile();

            SGIDriverProductReaderPlugIn readerPlugin = new SGIDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(768, finalProduct.getSceneRasterWidth());
            assertEquals(512, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(2);
            assertEquals(20, band.getDataType());
            assertEquals(393216, band.getNumDataElems());

            float bandValue = band.getSampleFloat(620, 410);
            assertEquals(24.0f, bandValue, 0);

            bandValue = band.getSampleFloat(543, 444);
            assertEquals(57.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(168.0f, bandValue, 0);

            bandValue = band.getSampleFloat(270, 290);
            assertEquals(78.0f, bandValue, 0);

            bandValue = band.getSampleFloat(158, 335);
            assertEquals(87.0f, bandValue, 0);
        }
    }
}
