package org.esa.s2tbx.dataio.alos.av2;

import com.bc.ceres.binding.ConversionException;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.runtime.LogUtils4Tests;
import org.esa.snap.utils.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public class AlosAV2ProductReaderTest {

    private static final String PRODUCT_FOLDER = "_alos"+ File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

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
        assertEquals(45, band.getSampleInt(199, 199));

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
        assertEquals(22, band.getSampleInt(199, 199));
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "blue", "near_infrared"} );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(20, 35, 155, 165), 0));
        subsetDef.setSubSampling(1, 1);

        AlosAV2ProductReader reader = buildProductReader();
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
        assertEquals(45, band.getSampleInt(154, 164));

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
        assertEquals(20, band.getSampleInt(154, 164));
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        File file = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP" + File.separator + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410" + File.separator + "AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((-33.38557815551758 82.1087646484375, -33.37318420410156 82.10877990722656," +
                " -33.36078643798828 82.1087875366211, -33.348392486572266 82.10879516601562," +
                " -33.33599853515625 82.10881042480469, -33.32360076904297 82.10881805419922," +
                " -33.31120681762695 82.10882568359375, -33.29880905151367 82.10883331298828," +
                " -33.286415100097656 82.10884857177734, -33.28445816040039 82.10884857177734," +
                " -33.28439712524414 82.10714721679688, -33.28433609008789 82.1054458618164," +
                " -33.28427505493164 82.1037368774414, -33.28421401977539 82.10203552246094," +
                " -33.28415298461914 82.10033416748047, -33.28409194946289 82.0986328125," +
                " -33.28403091430664 82.09693145751953, -33.28396987915039 82.09523010253906," +
                " -33.28392791748047 82.09406280517578, -33.28588104248047 82.09406280517578," +
                " -33.298255920410156 82.09405517578125, -33.31062698364258 82.09404754638672," +
                " -33.323001861572266 82.09403991699219, -33.33537292480469 82.09402465820312," +
                " -33.34774398803711 82.0940170288086, -33.3601188659668 82.09400939941406," +
                " -33.37248992919922 82.093994140625, -33.384864807128906 82.09398651123047," +
                " -33.384918212890625 82.09514617919922, -33.38500213623047 82.09685516357422," +
                " -33.38508605957031 82.09855651855469, -33.38516616821289 82.10025787353516," +
                " -33.385250091552734 82.10195922851562, -33.38533020019531 82.1036605834961," +
                " -33.385414123535156 82.10536193847656, -33.385498046875 82.10706329345703," +
                " -33.38557815551758 82.1087646484375))");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"blue", "near_infrared"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        AlosAV2ProductReader reader = buildProductReader();
        Product product = reader.readProductNodes(file, subsetDef);

        assertNotNull(product);
        assertNotNull(product.getFileLocation());
        assertNotNull(product.getName());
        assertNotNull(product.getPreferredTileSize());
        assertNotNull(product.getProductReader());
        assertEquals(product.getProductReader(), reader);
        assertEquals("AlosAV2Dimap", product.getProductType());
        assertEquals(157, product.getSceneRasterWidth());
        assertEquals(166, product.getSceneRasterHeight());

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
        assertEquals(26062, band.getNumDataElems());
        assertEquals("blue", band.getName());
        assertEquals(157, band.getRasterWidth());
        assertEquals(166, band.getRasterHeight());

        assertEquals(57.6815f, band.getSampleInt(0, 0), 4);
        assertEquals(44.7455f, band.getSampleInt(10, 10), 4);
        assertEquals(46.5095f, band.getSampleInt(100, 100), 4);
        assertEquals(53.5655f, band.getSampleInt(143, 43), 4);
        assertEquals(66.5015f, band.getSampleInt(12, 120), 4);
        assertEquals(44.7455f, band.getSampleInt(87, 145), 4);
        assertEquals(47.0975f, band.getSampleInt(134, 134), 4);
        assertEquals(45.9215f, band.getSampleInt(155, 165), 4);

        band = product.getBandAt(1);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(26062, band.getNumDataElems());
        assertEquals("near_infrared", band.getName());
        assertEquals(157, band.getRasterWidth());
        assertEquals(166, band.getRasterHeight());

        assertEquals(35.1275f, band.getSampleInt(0, 0), 4);
        assertEquals(25.1075f, band.getSampleInt(10, 10), 4);
        assertEquals(20.0975f, band.getSampleInt(100, 100), 4);
        assertEquals(30.1175f, band.getSampleInt(143, 43), 4);
        assertEquals(38.4675f, band.getSampleInt(12, 120), 4);
        assertEquals(10.0775f, band.getSampleInt(68, 126), 4);
        assertEquals(25.9425f, band.getSampleInt(134, 134), 4);
        assertEquals(20.9325f, band.getSampleInt(155, 165), 4);
    }

    private static AlosAV2ProductReader buildProductReader() {
        AlosAV2ProductReaderPlugin plugin = new AlosAV2ProductReaderPlugin();
        return  new AlosAV2ProductReader(plugin);
    }
}
