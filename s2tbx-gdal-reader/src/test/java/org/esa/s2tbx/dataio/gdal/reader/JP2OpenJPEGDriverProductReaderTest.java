package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.plugins.JP2OpenJPEGDriverProductReaderPlugIn;
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
public class JP2OpenJPEGDriverProductReaderTest extends AbstractTestDriverProductReader {

    public JP2OpenJPEGDriverProductReaderTest() {
    }

    @Test
    public void testJP2ReadProductNodes() throws IOException {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("JP2OpenJPEG-driver.jp2").toFile();

            JP2OpenJPEGDriverProductReaderPlugIn readerPlugin = new JP2OpenJPEGDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNotNull(finalProduct.getSceneGeoCoding());
            assertEquals(3, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(343, finalProduct.getSceneRasterWidth());
            assertEquals(343, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(117649, band.getNumDataElems());

            float bandValue = band.getSampleFloat(320, 110);
            assertEquals(18.0f, bandValue, 0);

            bandValue = band.getSampleFloat(333, 320);
            assertEquals(12.0f, bandValue, 0);

            bandValue = band.getSampleFloat(300, 300);
            assertEquals(10.0f, bandValue, 0);

            bandValue = band.getSampleFloat(277, 298);
            assertEquals(9.0f, bandValue, 0);

            bandValue = band.getSampleFloat(297, 338);
            assertEquals(7.0f, bandValue, 0);
        }
    }}
