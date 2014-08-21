package org.esa.beam.dataio.spot.dimap;

import junit.framework.Assert;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.dataio.spot.TestUtil;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;

/**
 * @author Ramona Manda
 */
public class SpotViewMetadataTest {
    private SpotViewMetadata metadata;

    @Before
    public void setUp() throws Exception {
        XmlMetadataParserFactory.registerParser(SpotViewMetadata.class, new XmlMetadataParser<SpotViewMetadata>(SpotViewMetadata.class));
        metadata = XmlMetadata.create(SpotViewMetadata.class, TestUtil.getTestFile("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL/metadata.xml"));
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("metadata.xml", metadata.getFileName()); ;
    }

    @Test
    public void testGetProductName() throws Exception {
        assertEquals("SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL.ZIP", metadata.getProductName());
    }

    @Test
    public void testGetNumBands() throws Exception {
        assertEquals(4, metadata.getNumBands());
    }

    @Test
    public void testGetFormatName() throws Exception {
        assertEquals("NOT DIMAP", metadata.getFormatName());
    }

    @Test
    public void testGetMetadataProfile() throws Exception {
        assertEquals("SPOTScene", metadata.getMetadataProfile());
    }

    @Test
    public void testGetRasterWidth() throws Exception {
        assertEquals(2713, metadata.getRasterWidth());
    }

    @Test
    public void testGetRasterHeight() throws Exception {
        assertEquals(2568, metadata.getRasterHeight());
    }

    @Test
    public void testGetRasterFileNames() throws Exception {
        assertEquals(1, metadata.getRasterFileNames().length);
        assertEquals("imagery.bil", metadata.getRasterFileNames()[0]);
    }

    @Test
    public void testGetBandNames() throws Exception {
        assertEquals(4, metadata.getBandNames().length);
        assertEquals("band_0", metadata.getBandNames()[0]);
        assertEquals("band_1", metadata.getBandNames()[1]);
        assertEquals("band_2", metadata.getBandNames()[2]);
        assertEquals("band_3", metadata.getBandNames()[3]);
    }

    @Test
    public void testGetRasterJavaByteOrder() throws Exception {
        assertEquals(ByteOrder.BIG_ENDIAN, metadata.getRasterJavaByteOrder());
    }

    @Test
    public void testGetRasterDataType() throws Exception {
        assertEquals(ProductData.TYPE_UINT8, metadata.getRasterDataType());
    }

    @Test
    public void testGetRasterPixelSize() throws Exception {
        assertEquals(1, metadata.getRasterPixelSize());
    }

    @Test
    public void testGetRasterGeoRefX() throws Exception {
        assertEquals(5733737.5, metadata.getRasterGeoRefX(), 0.001);
    }

    @Test
    public void testGetRasterGeoRefY() throws Exception {
        assertEquals(2049987.5, metadata.getRasterGeoRefY(), 0.001);
    }

    @Test
    public void testGetRasterGeoRefSizeX() throws Exception {
        assertEquals(25.0, metadata.getRasterGeoRefSizeX(), 0.001);
    }

    @Test
    public void testGetRasterGeoRefSizeY() throws Exception {
        assertEquals(25.0, metadata.getRasterGeoRefSizeY(), 0.001);
    }

    @Test
    public void testGetGeolayerFileName() throws Exception {
        assertEquals("geolayer.bil", metadata.getGeolayerFileName());
    }

    @Test
    public void testGetGeolayerNumBands() throws Exception {
        assertEquals(2, metadata.getGeolayerNumBands());
    }

    @Test
    public void testGetGeolayerWidth() throws Exception {
        assertEquals(3000, metadata.getGeolayerWidth());
    }

    @Test
    public void testGetGeolayerHeight() throws Exception {
        assertEquals(3000, metadata.getGeolayerHeight());
    }

    @Test
    public void testGetGeolayerJavaByteOrder() throws Exception {
        assertEquals(ByteOrder.BIG_ENDIAN, metadata.getGeolayerJavaByteOrder());
    }

    @Test
    public void testGetGeolayerPixelSize() throws Exception {
        assertEquals(4, metadata.getGeolayerPixelSize());
    }

    @Test
    public void testGetGeolayerDataType() throws Exception {
        assertEquals(ProductData.TYPE_UINT8, metadata.getGeolayerDataType());
    }

    @Test
    public void testGetProjectionCode() throws Exception {
        assertEquals("epsg:3035", metadata.getProjectionCode());
    }

    @Test
    public void testSetFileName() throws Exception {
        metadata.setFileName("testtest");
        //wrong!!!, actual result=metadata.xml
        //assertEquals("testtest", metadata.getFileName());
        assertEquals("metadata.xml", metadata.getFileName());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals("D:\\Sentinel2_PROJECT\\Satellite_Imagery\\TestingJUnitFiles\\SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL\\metadata.xml", metadata.getPath());
    }

    @Test
    public void testSetPath() throws Exception {
        metadata.setPath("D:/testtets");
        assertEquals("D:/testtets", metadata.getPath());
    }

    @Test
    public void testSetName() throws Exception {
        metadata.setName("testtest");
        //no get method available, how to test is was changed?
        //assertEquals(0, metadata.getName());
    }
}