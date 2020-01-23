package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.lib.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.gdal.reader.plugins.GTXDriverProductReaderPlugIn;
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
public class GTXDriverProductReaderTest extends AbstractTestDriverProductReader {

    public GTXDriverProductReaderTest() {
    }

    @Test
    public void testGTXReadProductNodes() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
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

    @Test
    public void testGTXReadProductSubset() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File file = this.gdalTestsFolderPath.resolve("GTX-driver.gtx").toFile();

            Rectangle subsetRegion = new Rectangle(5, 10, 10, 20);
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "band_1"} );
            subsetDef.setRegion(subsetRegion);
            subsetDef.setSubSampling(1, 1);

            GTXDriverProductReaderPlugIn readerPlugin = new GTXDriverProductReaderPlugIn();
            GDALProductReader reader = (GDALProductReader)readerPlugin.createReaderInstance();
            Product finalProduct = reader.readProductNodes(file, subsetDef);

            assertNotNull(finalProduct.getSceneGeoCoding());
            GeoPos productOrigin = ProductUtils.getCenterGeoPos(finalProduct);
            assertEquals(0.09f, productOrigin.lat,2);
            assertEquals(0.10f, productOrigin.lon,2);

            assertNotNull(finalProduct.getMaskGroup());
            assertEquals(0,finalProduct.getMaskGroup().getNodeNames().length);
            assertEquals(1, finalProduct.getBands().length);
            assertEquals("GDAL", finalProduct.getProductType());
            assertEquals(10, finalProduct.getSceneRasterWidth());
            assertEquals(20, finalProduct.getSceneRasterHeight());
            assertEquals(0,finalProduct.getTiePointGridGroup().getNodeNames().length);

            Band band = finalProduct.getBandAt(0);
            assertEquals(30, band.getDataType());
            assertEquals(200, band.getNumDataElems());

            float bandValue = band.getSampleFloat(2, 2);
            assertEquals(248.0f, bandValue, 0);

            bandValue = band.getSampleFloat(6, 8);
            assertEquals(372.0f, bandValue, 0);

            bandValue = band.getSampleFloat(3, 14);
            assertEquals(489, bandValue, 0);

            bandValue = band.getSampleFloat(7, 15);
            assertEquals(513.0f, bandValue, 0);

            bandValue = band.getSampleFloat(9, 19);
            assertEquals(595.0f, bandValue, 0);
        }
    }
}
