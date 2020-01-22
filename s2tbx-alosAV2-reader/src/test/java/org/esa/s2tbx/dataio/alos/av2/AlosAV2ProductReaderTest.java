package org.esa.s2tbx.dataio.alos.av2;

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

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class AlosAV2ProductReaderTest {

    private static final String PRODUCT_FOLDER = "_alos"+ File.separator;

    @Test
    public void testReadProduct() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA");

        AlosAV2ProductReader reader = buildProductReader();

        Product product = reader.readProductNodes(file, null);
        assertNotNull(product);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("AlosAV2Dimap", product.getProductType());
        assertEquals(200, product.getSceneRasterWidth());
        assertEquals(200, product.getSceneRasterHeight());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(2, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(4, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(40000, band.getNumDataElems());
        assertEquals("blue", band.getName());
        assertEquals(200, band.getRasterWidth());
        assertEquals(200, band.getRasterHeight());

        assertEquals(37, band.getSampleInt(0, 0));
        assertEquals(38, band.getSampleInt(10, 10));
        assertEquals(90, band.getSampleInt(100, 100));
        assertEquals(75, band.getSampleInt(143, 43));
        assertEquals(62, band.getSampleInt(12, 120));
        assertEquals(46, band.getSampleInt(87, 145));
        assertEquals(58, band.getSampleInt(134, 134));
        assertEquals(0, band.getSampleInt(200, 200));

        band = product.getBandAt(3);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(40000, band.getNumDataElems());
        assertEquals("near_infrared", band.getName());
        assertEquals(200, band.getRasterWidth());
        assertEquals(200, band.getRasterHeight());

        assertEquals(10, band.getSampleInt(0, 0));
        assertEquals(11, band.getSampleInt(10, 10));
        assertEquals(53, band.getSampleInt(100, 100));
        assertEquals(20, band.getSampleInt(143, 43));
        assertEquals(40, band.getSampleInt(12, 120));
        assertEquals(21, band.getSampleInt(87, 145));
        assertEquals(29, band.getSampleInt(134, 134));
        assertEquals(0, band.getSampleInt(200, 200));
    }

    @Test
    public void testReadProductSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA");

        AlosAV2ProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "blue", "near_infrared"} );
        subsetDef.setRegion(new Rectangle(20, 35, 155, 165));
        subsetDef.setSubSampling(1, 1);

        Product product = reader.readProductNodes(file, subsetDef);
        assertNotNull(product);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("AlosAV2Dimap", product.getProductType());
        assertEquals(155, product.getSceneRasterWidth());
        assertEquals(165, product.getSceneRasterHeight());

        GeoCoding geoCoding = product.getSceneGeoCoding();
        assertNotNull(geoCoding);
        CoordinateReferenceSystem coordinateReferenceSystem = geoCoding.getGeoCRS();
        assertNotNull(coordinateReferenceSystem);
        assertNotNull(coordinateReferenceSystem.getName());
        assertEquals("World Geodetic System 1984", coordinateReferenceSystem.getName().getCode());

        assertEquals(0, product.getMaskGroup().getNodeCount());

        assertEquals(0, product.getTiePointGrids().length);

        assertEquals(2, product.getBands().length);

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(25575, band.getNumDataElems());
        assertEquals("blue", band.getName());
        assertEquals(155, band.getRasterWidth());
        assertEquals(165, band.getRasterHeight());

        assertEquals(67, band.getSampleInt(0, 0));
        assertEquals(45, band.getSampleInt(10, 10));
        assertEquals(46, band.getSampleInt(100, 100));
        assertEquals(54, band.getSampleInt(143, 43));
        assertEquals(67, band.getSampleInt(12, 120));
        assertEquals(44, band.getSampleInt(87, 145));
        assertEquals(46, band.getSampleInt(134, 134));
        assertEquals(0, band.getSampleInt(155, 165));

        band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(25575, band.getNumDataElems());
        assertEquals("near_infrared", band.getName());
        assertEquals(155, band.getRasterWidth());
        assertEquals(165, band.getRasterHeight());

        assertEquals(36, band.getSampleInt(0, 0));
        assertEquals(25, band.getSampleInt(10, 10));
        assertEquals(20, band.getSampleInt(100, 100));
        assertEquals(31, band.getSampleInt(143, 43));
        assertEquals(38, band.getSampleInt(12, 120));
        assertEquals(20, band.getSampleInt(87, 145));
        assertEquals(25, band.getSampleInt(134, 134));
        assertEquals(0, band.getSampleInt(155, 165));
    }

    private static AlosAV2ProductReader buildProductReader() {
        AlosAV2ProductReaderPlugin plugin = new AlosAV2ProductReaderPlugin();
        return  new AlosAV2ProductReader(plugin);
    }
}
