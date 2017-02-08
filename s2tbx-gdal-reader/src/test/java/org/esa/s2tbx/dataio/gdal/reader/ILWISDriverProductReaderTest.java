package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.ILWISDriverProductReaderPlugIn;
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
public class ILWISDriverProductReaderTest extends AbstractTestDriverProductReader {

    public ILWISDriverProductReaderTest() {
    }

    @Test
    public void testILWISReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("ILWIS-driver.mpr").toFile();

            ILWISDriverProductReaderPlugIn readerPlugin = new ILWISDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(768, finalProduct.getSceneRasterWidth());
            assertEquals(512, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(12, band.getDataType());
            assertEquals(393216, band.getNumDataElems());

            float bandValue = band.getSampleFloat(234, 321);
            assertEquals(61.0f, bandValue, 0);

            bandValue = band.getSampleFloat(543, 444);
            assertEquals(109.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 432);
            assertEquals(106.0f, bandValue, 0);

            bandValue = band.getSampleFloat(470, 291);
            assertEquals(114.0f, bandValue, 0);

            bandValue = band.getSampleFloat(458, 500);
            assertEquals(101.0f, bandValue, 0);
        }
    }
}
