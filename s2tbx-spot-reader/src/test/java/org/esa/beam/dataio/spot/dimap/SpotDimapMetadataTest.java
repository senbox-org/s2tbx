package org.esa.beam.dataio.spot.dimap;

import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.dataio.spot.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ramona Manda
 */
public class SpotDimapMetadataTest {
    private SpotDimapMetadata metadata;

    @Before
    public void setUp() throws Exception {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new SpotDimapMetadata.SpotDimapMetadataParser(SpotDimapMetadata.class));
        metadata = XmlMetadata.create(SpotDimapMetadata.class, TestUtil.getTestFile("metadata.dim"));
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("SPOTSCENE_1A", metadata.getFileName());
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("SCENE 4 060-283 07/05/04 10:18:44 2 I", metadata.getProductName());
    }

    @Test
    public void testGetProductDescription() throws Exception {
        assertEquals("SCENE HRVIR2 I", metadata.getProductDescription());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals("DIMAP", metadata.getFormatName());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals("SPOTSCENE_1A", metadata.getMetadataProfile());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(3000, metadata.getRasterWidth());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(3000, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterFileName() throws Exception {
        assertNotNull(metadata.getRasterFileNames());
        assertEquals("mediumimage.tif", metadata.getRasterFileNames()[0]);

    }

    @Test
    public void testGetBandNames() throws Exception {
        assertEquals(4, metadata.getBandNames().length);
        assertEquals("XS1", metadata.getBandNames()[0]);
        assertEquals("XS2", metadata.getBandNames()[1]);
        assertEquals("XS3", metadata.getBandNames()[2]);
        assertEquals("SWIR", metadata.getBandNames()[3]);
    }

    @Test
    public void testGetBandUnits() throws Exception {
        assertEquals(4, metadata.getBandUnits().length);
        assertEquals("equivalent radiance (W.m-2.Sr-1.um-1)", metadata.getBandUnits()[0]);
        assertEquals("equivalent radiance (W.m-2.Sr-1.um-1)", metadata.getBandUnits()[1]);
        assertEquals("equivalent radiance (W.m-2.Sr-1.um-1)", metadata.getBandUnits()[2]);
        assertEquals("equivalent radiance (W.m-2.Sr-1.um-1)", metadata.getBandUnits()[3]);
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(4, metadata.getNumBands());
    }

    @Test
    public void testGetNoDataValue() throws Exception {
        assertEquals(0, metadata.getNoDataValue());
    }

    @Test
    public void testGetNoDataColor() throws Exception {
        assertEquals(new Color(0, 0, 0), metadata.getNoDataColor());
    }

    @Test
    public void testGetSaturatedPixelValue() throws Exception {
        assertEquals(255, metadata.getSaturatedPixelValue());
    }

    @Test
    public void testGetSaturatedColor() throws Exception {
        assertEquals(new Color(255, 255, 255), metadata.getSaturatedColor());
    }

    @Test
    public void testGetWavelength() throws Exception {
        assertEquals(470, metadata.getWavelength(0), 0.001);
        assertEquals(590, metadata.getWavelength(1), 0.001);
        assertEquals(730, metadata.getWavelength(2), 0.001);
        assertEquals(1510, metadata.getWavelength(3), 0.001);
    }

    @Test
    public void testGetBandwidth() throws Exception {
        assertEquals(5, metadata.getBandwidth(0), 0.001);
        assertEquals(5, metadata.getBandwidth(1), 0.001);
        assertEquals(5, metadata.getBandwidth(2), 0.001);
        assertEquals(5, metadata.getBandwidth(3), 0.001);
    }

    @Test
    public void testGetCenterTime() throws Exception {
        Date expected = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).parse("2007-05-04 10:18:44.370");
        assertEquals(expected.getTime(), metadata.getCenterTime().getAsDate().getTime());
    }

    @Test
    public void testGetTiePoints() throws Exception {
        assertEquals(4, metadata.getTiePoints().length);
    }

    @Test
    public void testGetInsertPoint() throws Exception {
        SpotDimapMetadata.InsertionPoint point = metadata.getInsertPoint();
        assertEquals(null, point);
    }

    @Test
    public void testGetScalingFactor() throws Exception {
        metadata.getWavelength(0);
        assertEquals(1.862, metadata.getScalingFactor(0), 0.001);
        assertEquals(2.333, metadata.getScalingFactor(1), 0.001);
        assertEquals(2.882, metadata.getScalingFactor(2), 0.001);
        assertEquals(5.941, metadata.getScalingFactor(3), 0.001);
    }

    @Test
    public void testGetScalingOffset() throws Exception {
        metadata.getWavelength(0);
        assertEquals(470, metadata.getScalingOffset(0), 0.001);
        assertEquals(590, metadata.getScalingOffset(1), 0.001);
        assertEquals(730, metadata.getScalingOffset(2), 0.001);
        assertEquals(1510, metadata.getScalingOffset(3), 0.001);
    }

    @Test
    public void testGetGain() throws Exception {
        assertEquals(0, metadata.getGain(0), 0.001);
        assertEquals(0, metadata.getGain(1), 0.001);
        assertEquals(0, metadata.getGain(2), 0.001);
        assertEquals(0, metadata.getGain(3), 0.001);
    }

    @Test
    public void testGetCRSCode() throws Exception {
        assertEquals("epsg:4326", metadata.getCRSCode());
    }

    @Test
    public void testGetOrientation() throws Exception {
        assertEquals(9.359, metadata.getOrientation(), 0.001);

    }

    @Test
    public void testGetCornerCoordinates() throws Exception {
        assertEquals(5, metadata.getCornerCoordinates().length);
        assertEquals(new Point2D.Float(6.410438f, 33.727127f), metadata.getCornerCoordinates()[0]);
        assertEquals(new Point2D.Float(7.0806904f, 33.63457f), metadata.getCornerCoordinates()[1]);
        assertEquals(new Point2D.Float(6.937253f, 33.10657f), metadata.getCornerCoordinates()[2]);
        assertEquals(new Point2D.Float(6.271041f, 33.198708f), metadata.getCornerCoordinates()[3]);
        assertEquals(new Point2D.Float(6.677884f, 33.416885f), metadata.getCornerCoordinates()[4]);
    }

    @Test
    public void testGetStatistics() throws Exception {
        //add some other test with statistics??
        assertEquals(null, metadata.getStatistics(0));
    }

    @Test
    public void testGetPixelDataType() throws Exception {
        assertEquals(20, metadata.getPixelDataType());

    }

    @Test
    public void testSetFileName() throws Exception {
        metadata.setFileName("testname");
        assertEquals("testname", metadata.getFileName());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals(TestUtil.getTestFile("metadata.dim").getAbsolutePath(), metadata.getPath());
    }

    @Test
    public void testSetPath() throws Exception {
        metadata.setPath("testname");
        assertEquals("testname", metadata.getPath());
    }
}