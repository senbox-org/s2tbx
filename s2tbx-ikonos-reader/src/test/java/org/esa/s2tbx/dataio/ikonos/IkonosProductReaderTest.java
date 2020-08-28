package org.esa.s2tbx.dataio.ikonos;

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

public class IkonosProductReaderTest {

    private static final String PRODUCTS_FOLDER = "_ikonos" + File.separator;

    @BeforeClass
    public static void setup() throws Exception {
        LogUtils4Tests.initLogger();
    }

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
        assertEquals(0.2604f, band.getSampleFloat(49, 49), 0.0f);
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");

        IkonosProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[] { "Pan", "Red", "Green" } );
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(12, 15, 30, 25), 0));
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

        Band bandRed = product.getBand("Red");
        assertNotNull(bandRed);
        assertEquals(21, bandRed.getDataType());
        assertEquals(48, bandRed.getNumDataElems());
        assertEquals(8, bandRed.getRasterWidth());
        assertEquals(6, bandRed.getRasterHeight());

        assertEquals(0.45885f, bandRed.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.37485f, bandRed.getSampleFloat(1, 2), 0.0f);
        assertEquals(0.27825f, bandRed.getSampleFloat(3, 1), 0.0f);
        assertEquals(0.32025f, bandRed.getSampleFloat(6, 4), 0.0f);

        Band bandPan = product.getBandAt(1);
        assertNotNull(bandPan);
        assertEquals(21, bandPan.getDataType());
        assertEquals(750, bandPan.getNumDataElems());
        assertEquals("Pan", bandPan.getName());
        assertEquals(30, bandPan.getRasterWidth());
        assertEquals(25, bandPan.getRasterHeight());

        assertEquals(0.524145f, bandPan.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.449445f, bandPan.getSampleFloat(22, 20), 0.0f);
        assertEquals(0.459405f, bandPan.getSampleFloat(21, 11), 0.0f);
        assertEquals(0.412095f, bandPan.getSampleFloat(11, 21), 0.0f);
        assertEquals(0.519165f, bandPan.getSampleFloat(23, 23), 0.0f);
        assertEquals(0.526635f, bandPan.getSampleFloat(20, 24), 0.0f);
        assertEquals(0.422055f, bandPan.getSampleFloat(21, 20), 0.0f);
        assertEquals(0.45318f, bandPan.getSampleFloat(13, 14), 0.0f);
        assertEquals(0.392175f, bandPan.getSampleFloat(12, 19), 0.0f);
        assertEquals(0.53037f, bandPan.getSampleFloat(5, 17), 0.0f);
        assertEquals(0.498f, bandPan.getSampleFloat(16, 13), 0.0f);
        assertEquals(0.444465f, bandPan.getSampleFloat(21, 14), 0.0f);
        assertEquals(0.489285f, bandPan.getSampleFloat(20, 20), 0.0f);
        assertEquals(0.41832f, bandPan.getSampleFloat(10, 23), 0.0f);
        assertEquals(0.43326f, bandPan.getSampleFloat(29, 24), 0.0f);
    }

    @Test
    public void testReadProductGeometrySubset() throws IOException, ConversionException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCTS_FOLDER + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP" + File.separator + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML");

        JtsGeometryConverter converter = new JtsGeometryConverter();
        Geometry geometry = converter.parse("POLYGON ((23.91600227355957 38.10725402832031, 23.91603660583496 38.10725021362305," +
                " 23.91607093811035 38.10725021362305, 23.916105270385742 38.10725021362305," +
                " 23.916139602661133 38.10725021362305, 23.916173934936523 38.10724639892578," +
                " 23.916208267211914 38.10724639892578, 23.916242599487305 38.10724639892578," +
                " 23.916276931762695 38.10724639892578, 23.916311264038086 38.10724639892578," +
                " 23.916345596313477 38.107242584228516, 23.916343688964844 38.107215881347656," +
                " 23.91634178161621 38.1071891784668, 23.91634178161621 38.10716247558594," +
                " 23.916339874267578 38.10713577270508, 23.916339874267578 38.10710906982422," +
                " 23.916337966918945 38.10708236694336, 23.916337966918945 38.1070556640625," +
                " 23.916336059570312 38.10702896118164, 23.916336059570312 38.107017517089844," +
                " 23.916301727294922 38.10702133178711, 23.91626739501953 38.10702133178711," +
                " 23.91623306274414 38.10702133178711, 23.91619873046875 38.10702133178711," +
                " 23.91616439819336 38.10702133178711, 23.91613006591797 38.107025146484375," +
                " 23.916095733642578 38.107025146484375, 23.916061401367188 38.107025146484375," +
                " 23.91602897644043 38.107025146484375, 23.91599464416504 38.10702896118164," +
                " 23.91599464416504 38.10703659057617, 23.91599464416504 38.10706329345703," +
                " 23.915996551513672 38.10708999633789, 23.915998458862305 38.10711669921875," +
                " 23.915998458862305 38.10714340209961, 23.916000366210938 38.10717010498047," +
                " 23.916000366210938 38.10719680786133, 23.91600227355957 38.10722732543945," +
                " 23.91600227355957 38.10725402832031))");

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"Pan", "Red", "Green"});
        subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
        subsetDef.setSubSampling(1, 1);

        IkonosProductReader reader = buildProductReader();
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

        Band bandRed = product.getBand("Red");
        assertNotNull(bandRed);
        assertEquals(21, bandRed.getDataType());
        assertEquals(48, bandRed.getNumDataElems());
        assertEquals(8, bandRed.getRasterWidth());
        assertEquals(6, bandRed.getRasterHeight());

        assertEquals(0.45885f, bandRed.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.37485f, bandRed.getSampleFloat(1, 2), 0.0f);
        assertEquals(0.27825f, bandRed.getSampleFloat(3, 1), 0.0f);
        assertEquals(0.32025f, bandRed.getSampleFloat(6, 4), 0.0f);

        Band bandPan = product.getBandAt(1);
        assertNotNull(bandPan);
        assertEquals(21, bandPan.getDataType());
        assertEquals(750, bandPan.getNumDataElems());
        assertEquals("Pan", bandPan.getName());
        assertEquals(30, bandPan.getRasterWidth());
        assertEquals(25, bandPan.getRasterHeight());

        assertEquals(0.524145f, bandPan.getSampleFloat(0, 0), 0.0f);
        assertEquals(0.449445f, bandPan.getSampleFloat(22, 20), 0.0f);
        assertEquals(0.459405f, bandPan.getSampleFloat(21, 11), 0.0f);
        assertEquals(0.412095f, bandPan.getSampleFloat(11, 21), 0.0f);
        assertEquals(0.519165f, bandPan.getSampleFloat(23, 23), 0.0f);
        assertEquals(0.526635f, bandPan.getSampleFloat(20, 24), 0.0f);
        assertEquals(0.422055f, bandPan.getSampleFloat(21, 20), 0.0f);
        assertEquals(0.45318f, bandPan.getSampleFloat(13, 14), 0.0f);
        assertEquals(0.392175f, bandPan.getSampleFloat(12, 19), 0.0f);
        assertEquals(0.53037f, bandPan.getSampleFloat(5, 17), 0.0f);
        assertEquals(0.498f, bandPan.getSampleFloat(16, 13), 0.0f);
        assertEquals(0.444465f, bandPan.getSampleFloat(21, 14), 0.0f);
        assertEquals(0.489285f, bandPan.getSampleFloat(20, 20), 0.0f);
        assertEquals(0.41832f, bandPan.getSampleFloat(10, 23), 0.0f);
        assertEquals(0.43326f, bandPan.getSampleFloat(29, 24), 0.0f);
    }

    private static IkonosProductReader buildProductReader() {
        IkonosProductReaderPlugin plugin = new IkonosProductReaderPlugin();
        return new IkonosProductReader(plugin);
    }
}
