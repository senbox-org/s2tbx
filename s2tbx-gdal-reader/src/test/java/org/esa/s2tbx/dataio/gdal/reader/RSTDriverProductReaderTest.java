package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.lib.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.gdal.reader.plugins.RSTDriverProductReaderPlugIn;
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
import static org.junit.Assert.assertNull;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductReaderTest extends AbstractTestDriverProductReader {

    public RSTDriverProductReaderTest() {
    }

    @Test
    public void testRSTReadProductNodes() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("RST-driver.rst").toFile();

            RSTDriverProductReaderPlugIn readerPlugin = new RSTDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, null);
            assertNotNull(finalProduct.getSceneGeoCoding());
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(20, finalProduct.getSceneRasterWidth());
            assertEquals(30, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(600, band.getNumDataElems());

            float bandValue = band.getSampleFloat(10, 20);
            assertEquals(155.0f, bandValue, 0);

            bandValue = band.getSampleFloat(12, 26);
            assertEquals(21.0f, bandValue, 0);

            bandValue = band.getSampleFloat(16, 16);
            assertEquals(81.0f, bandValue, 0);

            bandValue = band.getSampleFloat(7, 19);
            assertEquals(132.0f, bandValue, 0);

            bandValue = band.getSampleFloat(5, 13);
            assertEquals(10.0f, bandValue, 0);
        }
    }

    @Test
    public void testRSTReadProductSubset() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("RST-driver.rst").toFile();

            Rectangle subsetRegion = new Rectangle(5, 10, 15, 10);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "band_1"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            RSTDriverProductReaderPlugIn readerPlugin = new RSTDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(15.00f, productOrigin.lat,2);
            assertEquals(12.50f, productOrigin.lon,2);

            assertNotNull(finalProduct.getMaskGroup());
            assertEquals(0,finalProduct.getMaskGroup().getNodeNames().length);
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(15, finalProduct.getSceneRasterWidth());
            assertEquals(10, finalProduct.getSceneRasterHeight());

            Band band = finalProduct.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(150, band.getNumDataElems());

            float bandValue = band.getSampleFloat(2, 0);
            assertEquals(208.0f, bandValue, 0);

            bandValue = band.getSampleFloat(6, 4);
            assertEquals(36.0f, bandValue, 0);

            bandValue = band.getSampleFloat(8, 2);
            assertEquals(254.0f, bandValue, 0);

            bandValue = band.getSampleFloat(6, 7);
            assertEquals(96.0f, bandValue, 0);

            bandValue = band.getSampleFloat(9, 7);
            assertEquals(99.0f, bandValue, 0);

            bandValue = band.getSampleFloat(11, 5);
            assertEquals(61.0f, bandValue, 0);

            bandValue = band.getSampleFloat(10, 7);
            assertEquals(100.0f, bandValue, 0);
        }
    }
}
