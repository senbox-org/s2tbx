package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.activator.GDALInstallInfo;
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
public class PNMDriverProductReaderTest extends AbstractTestDriverProductReader {

    public PNMDriverProductReaderTest() {
    }

    @Test
    public void testPNMReadProductNodes() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("PNM-driver.pnm").toFile();

            PNMDriverProductReaderPlugIn readerPlugin = new PNMDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(768, finalProduct.getSceneRasterWidth());
            assertEquals(512, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(393216, band.getNumDataElems());

            float bandValue = band.getSampleFloat(620, 410);
            assertEquals(49, bandValue, 0);

            bandValue = band.getSampleFloat(543, 444);
            assertEquals(90.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(200.0f, bandValue, 0);

            bandValue = band.getSampleFloat(270, 290);
            assertEquals(122.0f, bandValue, 0);

            bandValue = band.getSampleFloat(158, 335);
            assertEquals(125.0f, bandValue, 0);
        }
    }
}
