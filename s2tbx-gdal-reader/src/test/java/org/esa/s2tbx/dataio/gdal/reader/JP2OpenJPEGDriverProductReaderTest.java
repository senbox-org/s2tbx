package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.lib.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.gdal.reader.plugins.JP2OpenJPEGDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.reader.GDALProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.junit.Test;

import java.awt.*;
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
        if (GDALInstallInfo.INSTANCE.isPresent()) {
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
    }

    @Test
    public void testJP2ReadProductSubset() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("JP2OpenJPEG-driver.jp2").toFile();

            Rectangle subsetRegion = new Rectangle(200, 100, 100, 200);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "band_2","band_3"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            JP2OpenJPEGDriverProductReaderPlugIn readerPlugin = new JP2OpenJPEGDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(44.81f, productOrigin.lat,2);
            assertEquals(6.34f, productOrigin.lon,2);

            assertNotNull(finalProduct.getMaskGroup());
            assertEquals(0,finalProduct.getMaskGroup().getNodeNames().length);
            assertEquals(2, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(100, finalProduct.getSceneRasterWidth());
            assertEquals(200, finalProduct.getSceneRasterHeight());

            Band band_2 = finalProduct.getBand("band_2");
            assertEquals(20, band_2.getDataType());
            assertEquals(20000, band_2.getNumDataElems());

            float bandValue = band_2.getSampleFloat(44, 47);
            assertEquals(0.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(76, 16);
            assertEquals(23.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(67, 84);
            assertEquals(9.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(91, 164);
            assertEquals(11.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(63, 198);
            assertEquals(12.0f, bandValue, 0);

            Band band_3 = finalProduct.getBand("band_2");
            assertEquals(20, band_3.getDataType());
            assertEquals(20000, band_3.getNumDataElems());

            bandValue = band_2.getSampleFloat(44, 47);
            assertEquals(0.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(76, 16);
            assertEquals(21.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(67, 84);
            assertEquals(9.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(91, 164);
            assertEquals(10.0f, bandValue, 0);

            bandValue = band_2.getSampleFloat(63, 198);
            assertEquals(13.0f, bandValue, 0);
        }
    }
}
