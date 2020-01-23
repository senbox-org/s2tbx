package org.esa.s2tbx.dataio.alos.pri;

import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class AlosPRIProductReaderTest {

    private static final String PRODUCT_FOLDER = "_alos"+ File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML");

        AlosPRIProductReader reader = buildProductReader();

        Product product = reader.readProductNodes(productFile, null);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(25629, product.getSceneRasterWidth());
        assertEquals(22640, product.getSceneRasterHeight());
        assertEquals("AlosPRIDimap", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(2, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(3, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(419548800, band.getNumDataElems());
        assertEquals("ALPSMB038921910", band.getName());
        assertEquals(20416, band.getRasterWidth());
        assertEquals(20550, band.getRasterHeight());

        assertEquals(0, band.getSampleInt(0, 0));
        assertEquals(1, band.getSampleInt(6124, 10532));
        assertEquals(1, band.getSampleInt(7601, 12010));
        assertEquals(0, band.getSampleInt(3024, 5126));
        assertEquals(1, band.getSampleInt(16000, 11010));
        assertEquals(1, band.getSampleInt(4010, 16021));
        assertEquals(1, band.getSampleInt(9700, 10331));
        assertEquals(1, band.getSampleInt(16452, 8742));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML");

        AlosPRIProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "ALPSMB038921910", "ALPSMF038921800", "no data", "saturated"} );
        subsetDef.setRegion(new Rectangle(12354, 9874, 12000, 11563));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(productFile, subsetDef);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals(12000, product.getSceneRasterWidth());
        assertEquals(11563, product.getSceneRasterHeight());
        assertEquals("AlosPRIDimap", product.getProductType());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(2, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(2, product.getBands().length);

        Band band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(100784566, band.getNumDataElems());
        assertEquals("ALPSMF038921800", band.getName());
        assertEquals(9638, band.getRasterWidth());
        assertEquals(10457, band.getRasterHeight());

        assertEquals(0, band.getSampleInt(0, 0));
        assertEquals(0, band.getSampleInt(6124, 8532));
        assertEquals(0, band.getSampleInt(7601, 2010));
        assertEquals(0, band.getSampleInt(3024, 5126));
        assertEquals(0, band.getSampleInt(9123, 9010));
        assertEquals(0, band.getSampleInt(4010, 7021));
        assertEquals(0, band.getSampleInt(9100, 8331));
        assertEquals(0, band.getSampleInt(6452, 8742));
    }

    private static AlosPRIProductReader buildProductReader() {
        AlosPRIProductReaderPlugin readerPlugin = new AlosPRIProductReaderPlugin();
        return new AlosPRIProductReader(readerPlugin);
    }
}
