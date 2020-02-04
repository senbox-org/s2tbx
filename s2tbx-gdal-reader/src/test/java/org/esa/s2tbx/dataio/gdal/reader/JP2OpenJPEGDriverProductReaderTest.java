package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.lib.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.gdal.reader.plugins.JP2OpenJPEGDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.reader.GDALProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ProductUtils;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
    public void testReadProduct() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File productFile = this.gdalTestsFolderPath.resolve("JP2OpenJPEG-driver.jp2").toFile();

            GDALProductReader reader = buildProductReader();
            Product product = reader.readProductNodes(productFile, null);
            assertNotNull(product.getFileLocation());
            assertNotNull(product.getName());
            assertNotNull(product.getPreferredTileSize());
            assertNotNull(product.getProductReader());
            assertEquals(product.getProductReader(), reader);
            assertEquals("GDAL", product.getProductType());
            assertEquals(343, product.getSceneRasterWidth());
            assertEquals(343, product.getSceneRasterHeight());

            GeoCoding geoCoding = product.getSceneGeoCoding();
            assertNotNull(geoCoding);
            CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
            assertNotNull(coordinateReferenceSystem);
            assertNotNull(coordinateReferenceSystem.getName());
            assertEquals("WGS_1984", coordinateReferenceSystem.getName().getCode());

            assertEquals(3, product.getMaskGroup().getNodeCount());

            assertEquals(3, product.getBands().length);

            Band band = product.getBandAt(0);
            assertEquals(20, band.getDataType());
            assertEquals(117649, band.getNumDataElems());
            assertEquals("Red", band.getName());
            assertEquals(343, band.getRasterWidth());
            assertEquals(343, band.getRasterHeight());

            assertEquals(0, band.getSampleInt(0, 0));
            assertEquals(9, band.getSampleInt(310, 210));
            assertEquals(12, band.getSampleInt(333, 320));
            assertEquals(0, band.getSampleInt(234, 165));
            assertEquals(18, band.getSampleInt(320, 110));
            assertEquals(10, band.getSampleInt(300, 300));
            assertEquals(9, band.getSampleInt(277, 298));
            assertEquals(7, band.getSampleInt(297, 338));
            assertEquals(8, band.getSampleInt(256, 178));
            assertEquals(13, band.getSampleInt(342, 342));
        }
    }

    @Test
    public void testReadProductSubset() throws IOException {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            File productFile = this.gdalTestsFolderPath.resolve("JP2OpenJPEG-driver.jp2").toFile();

            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[] { "Red", "Green" } );
            subsetDef.setRegion(new Rectangle(123, 100, 210, 200));
            subsetDef.setSubSampling(1, 1);

            GDALProductReader reader = buildProductReader();
            Product product = reader.readProductNodes(productFile, subsetDef);
            assertNotNull(product.getFileLocation());
            assertNotNull(product.getName());
            assertNotNull(product.getPreferredTileSize());
            assertNotNull(product.getProductReader());
            assertEquals(product.getProductReader(), reader);
            assertEquals("GDAL", product.getProductType());
            assertEquals(210, product.getSceneRasterWidth());
            assertEquals(200, product.getSceneRasterHeight());

            GeoCoding geoCoding = product.getSceneGeoCoding();
            assertNotNull(geoCoding);
            CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
            assertNotNull(coordinateReferenceSystem);
            assertNotNull(coordinateReferenceSystem.getName());
            assertEquals("WGS_1984", coordinateReferenceSystem.getName().getCode());

            assertEquals(0, product.getMaskGroup().getNodeCount());

            assertEquals(2, product.getBands().length);

            Band band = product.getBandAt(1);
            assertEquals(20, band.getDataType());
            assertEquals(42000, band.getNumDataElems());
            assertEquals("Green", band.getName());
            assertEquals(210, band.getRasterWidth());
            assertEquals(200, band.getRasterHeight());

            assertEquals(0, band.getSampleInt(0, 0));
            assertEquals(0, band.getSampleInt(110, 110));
            assertEquals(0, band.getSampleInt(200, 200));
            assertEquals(15, band.getSampleInt(198, 165));
            assertEquals(11, band.getSampleInt(120, 198));
            assertEquals(0, band.getSampleInt(50, 50));
            assertEquals(0, band.getSampleInt(100, 100));
            assertEquals(12, band.getSampleInt(200, 169));
            assertEquals(11, band.getSampleInt(156, 187));
            assertEquals(11, band.getSampleInt(209, 199));
        }
    }

    private static GDALProductReader buildProductReader() {
        JP2OpenJPEGDriverProductReaderPlugIn readerPlugin = new JP2OpenJPEGDriverProductReaderPlugIn();
        return (GDALProductReader)readerPlugin.createReaderInstance();
    }
}
