package org.esa.s2tbx.dataio.ikonos;

import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;


public class IkonosProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_ikonos" + File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");

        IkonosProductReader reader = buildProductReader();

        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(200, product.getSceneRasterWidth());
        assertEquals(200, product.getSceneRasterHeight());
        assertEquals("Ikonos Product", product.getProductType());
        assertEquals("20-AUG-2008 09:26:00.000000", product.getStartTime().toString());
        assertEquals("20-AUG-2008 09:26:00.000000", product.getEndTime().toString());
        assertEquals("metadata", product.getMetadataRoot().getName());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(5, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(21, band.getDataType());
        assertEquals(2500, band.getNumDataElems());
        assertEquals("Red", band.getName());
        assertEquals(50, band.getRasterWidth());
        assertEquals(50, band.getRasterHeight());

        assertEquals(0.3423f, band.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.2793f, band.getSampleFloat(22, 20), 0.0f);
        assertEquals(0.26460f, band.getSampleFloat(21, 11), 0.0f);
        assertEquals(0.52080f, band.getSampleFloat(11, 29), 0.0f);
        assertEquals(0.3528f, band.getSampleFloat(23, 23), 0.0f);
        assertEquals(0.273f, band.getSampleFloat(23, 47), 0.0f);
        assertEquals(0.32865f, band.getSampleFloat(21, 20), 0.0f);
        assertEquals(0.3234f, band.getSampleFloat(13, 44), 0.0f);
        assertEquals(0.28035f, band.getSampleFloat(42, 49), 0.0f);
        assertEquals(0.3423f, band.getSampleFloat(5, 17), 0.0f);
        assertEquals(0.36645f, band.getSampleFloat(16, 13), 0.0f);
        assertEquals(0.35595f, band.getSampleFloat(41, 14), 0.0f);
        assertEquals(0.3801f, band.getSampleFloat(10, 10), 0.0f);
        assertEquals(0.3108f, band.getSampleFloat(32, 44), 0.0f);
        assertEquals(0.0f, band.getSampleFloat(50, 50), 0.0f);
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");

        IkonosProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "Pan", "Red", "Green" } );
        subsetDef.setRegion(new Rectangle(12, 15, 30, 25));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertEquals("IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001", product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(30, product.getSceneRasterWidth());
        assertEquals(25, product.getSceneRasterHeight());
        assertEquals("Ikonos Product", product.getProductType());
        assertEquals("20-AUG-2008 09:26:00.000000", product.getStartTime().toString());
        assertEquals("20-AUG-2008 09:26:00.000000", product.getEndTime().toString());
        assertEquals("metadata", product.getMetadataRoot().getName());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(21, band.getDataType());
        assertEquals(750, band.getNumDataElems());
        assertEquals("Pan", band.getName());
        assertEquals(30, band.getRasterWidth());
        assertEquals(25, band.getRasterHeight());

        assertEquals(0.524145f, band.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.449445f, band.getSampleFloat(22, 20), 0.0f);
        assertEquals(0.459405f, band.getSampleFloat(21, 11), 0.0f);
        assertEquals(0.412095f, band.getSampleFloat(11, 21), 0.0f);
        assertEquals(0.519165f, band.getSampleFloat(23, 23), 0.0f);
        assertEquals(0.526635f, band.getSampleFloat(20, 24), 0.0f);
        assertEquals(0.422055f, band.getSampleFloat(21, 20), 0.0f);
        assertEquals(0.45318f, band.getSampleFloat(13, 14), 0.0f);
        assertEquals(0.392175f, band.getSampleFloat(12, 19), 0.0f);
        assertEquals(0.53037f, band.getSampleFloat(5, 17), 0.0f);
        assertEquals(0.498f, band.getSampleFloat(16, 13), 0.0f);
        assertEquals(0.444465f, band.getSampleFloat(21, 14), 0.0f);
        assertEquals(0.489285f, band.getSampleFloat(20, 20), 0.0f);
        assertEquals(0.41832f, band.getSampleFloat(10, 23), 0.0f);
        assertEquals(0.0f, band.getSampleFloat(30, 25), 0.0f);
    }

    private static IkonosProductReader buildProductReader() {
        IkonosProductReaderPlugin plugin = new IkonosProductReaderPlugin();
        return new IkonosProductReader(plugin);
    }
}
