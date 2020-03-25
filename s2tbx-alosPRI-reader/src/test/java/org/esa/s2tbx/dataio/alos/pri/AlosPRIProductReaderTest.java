package org.esa.s2tbx.dataio.alos.pri;

import com.bc.ceres.binding.ConversionException;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.subset.GeometrySubsetRegion;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.converters.JtsGeometryConverter;
import org.esa.snap.utils.TestUtil;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class AlosPRIProductReaderTest {

    private static final String PRODUCT_FOLDER = "_alos" + File.separator;

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

        Band band = product.getBand("ALPSMB038921910");
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(419548800, band.getNumDataElems());
        //assertEquals("ALPSMB038921910", band.getName());
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
    public void testReadProductGeometrySubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML");
        try {
            AlosPRIProductReader reader = buildProductReader();
            JtsGeometryConverter converter = new JtsGeometryConverter();
            Geometry geometry = converter.parse("POLYGON ((-93.02961730957031 81.30195617675781, -92.81564331054688 81.30191802978516," +
                                                        " -92.60167694091797 81.30175018310547, -92.38772583007812 81.30146789550781," +
                                                        " -92.17378234863281 81.30107116699219, -91.95986938476562 81.30055236816406," +
                                                        " -91.74598693847656 81.29991149902344, -91.53213500976562 81.29914855957031," +
                                                        " -91.31832122802734 81.29827117919922, -91.25322723388672 81.29798126220703," +
                                                        " -91.2596435546875 81.26563262939453, -91.2660140991211 81.23328399658203," +
                                                        " -91.27234649658203 81.20093536376953, -91.27862548828125 81.16858673095703," +
                                                        " -91.28485870361328 81.13623809814453, -91.29104614257812 81.10388946533203," +
                                                        " -91.29718780517578 81.071533203125, -91.30328369140625 81.0391845703125," +
                                                        " -91.30329895019531 81.03912353515625, -91.36653137207031 81.0394058227539," +
                                                        " -91.57421875 81.04025268554688, -91.78194427490234 81.0409927368164," +
                                                        " -91.98970031738281 81.04161834716797, -92.19747924804688 81.04212188720703," +
                                                        " -92.40528106689453 81.04251098632812, -92.61310577392578 81.04278564453125," +
                                                        " -92.82093048095703 81.0429458618164, -93.02876281738281 81.04298400878906," +
                                                        " -93.02876281738281 81.04305267333984, -93.02886962890625 81.0754165649414," +
                                                        " -93.02897644042969 81.10778045654297, -93.0290756225586 81.14014434814453," +
                                                        " -93.02918243408203 81.1725082397461, -93.02928924560547 81.20487213134766," +
                                                        " -93.0293960571289 81.23723602294922, -93.02950286865234 81.26959991455078," +
                                                        " -93.02961730957031 81.30195617675781))");
            ProductSubsetDef subsetDef = new ProductSubsetDef();
            subsetDef.setNodeNames(new String[]{"ALPSMB038921910", "ALPSMF038921800", "no data", "saturated"});
            subsetDef.setSubsetRegion(new GeometrySubsetRegion(geometry, 0));
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

            Band band_B = product.getBand("ALPSMB038921910");
            assertNotNull(band_B);
            assertEquals(20, band_B.getDataType());
            assertEquals(128112000, band_B.getNumDataElems());
            //assertEquals("ALPSMB038921910", band_B.getName());
            assertEquals(12000, band_B.getRasterWidth());
            assertEquals(10676, band_B.getRasterHeight());

            assertEquals(0, band_B.getSampleInt(0, 0));
            assertEquals(0, band_B.getSampleInt(6124, 8532));
            assertEquals(1, band_B.getSampleInt(7601, 2010));
            assertEquals(0, band_B.getSampleInt(3024, 5126));
            assertEquals(0, band_B.getSampleInt(9123, 9010));
            assertEquals(0, band_B.getSampleInt(4010, 7021));

            Band band_F = product.getBand("ALPSMF038921800");
            assertNotNull(band_F);
            assertEquals(20, band_F.getDataType());
            assertEquals(95183284, band_F.getNumDataElems());
            //assertEquals("ALPSMF038921800", band_F.getName());
            assertEquals(8231, band_F.getRasterWidth());
            assertEquals(11564, band_F.getRasterHeight());

            assertEquals(0, band_F.getSampleInt(0, 0));
            assertEquals(0, band_F.getSampleInt(6124, 8532));
            assertEquals(0, band_F.getSampleInt(7601, 2010));
            assertEquals(0, band_F.getSampleInt(3024, 5126));
            assertEquals(0, band_F.getSampleInt(8123, 9010));
            assertEquals(0, band_F.getSampleInt(4010, 7021));
            assertEquals(0, band_F.getSampleInt(8100, 8331));
            assertEquals(0, band_F.getSampleInt(6452, 8742));
        } catch (ConversionException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testReadProductPixelSubset() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        File productFile = TestUtil.getTestFile(PRODUCT_FOLDER + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP" + File.separator + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML");

        AlosPRIProductReader reader = buildProductReader();

        ProductSubsetDef subsetDef = new ProductSubsetDef();
        subsetDef.setNodeNames(new String[]{"ALPSMB038921910", "ALPSMF038921800", "no data", "saturated"});
        subsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(12354, 9874, 12000, 11563), 0));
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

        Band band_B = product.getBand("ALPSMB038921910");
        assertNotNull(band_B);
        assertEquals(20, band_B.getDataType());
        assertEquals(128112000, band_B.getNumDataElems());
        //assertEquals("ALPSMB038921910", band_B.getName());
        assertEquals(12000, band_B.getRasterWidth());
        assertEquals(10676, band_B.getRasterHeight());

        assertEquals(0, band_B.getSampleInt(0, 0));
        assertEquals(0, band_B.getSampleInt(6124, 8532));
        assertEquals(1, band_B.getSampleInt(7601, 2010));
        assertEquals(0, band_B.getSampleInt(3024, 5126));
        assertEquals(0, band_B.getSampleInt(9123, 9010));
        assertEquals(0, band_B.getSampleInt(4010, 7021));

        Band band_F = product.getBand("ALPSMF038921800");
        assertNotNull(band_F);
        assertEquals(20, band_F.getDataType());
        assertEquals(95183284, band_F.getNumDataElems());
        //assertEquals("ALPSMF038921800", band_F.getName());
        assertEquals(8231, band_F.getRasterWidth());
        assertEquals(11564, band_F.getRasterHeight());

        assertEquals(0, band_F.getSampleInt(0, 0));
        assertEquals(0, band_F.getSampleInt(6124, 8532));
        assertEquals(0, band_F.getSampleInt(7601, 2010));
        assertEquals(0, band_F.getSampleInt(3024, 5126));
        assertEquals(0, band_F.getSampleInt(8123, 9010));
        assertEquals(0, band_F.getSampleInt(4010, 7021));
        assertEquals(0, band_F.getSampleInt(8100, 8331));
        assertEquals(0, band_F.getSampleInt(6452, 8742));
    }

    private static AlosPRIProductReader buildProductReader() {
        AlosPRIProductReaderPlugin readerPlugin = new AlosPRIProductReaderPlugin();
        return new AlosPRIProductReader(readerPlugin);
    }
}
