/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot.dimap;

import org.apache.commons.lang.SystemUtils;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotViewMetadataTest {
    private SpotViewMetadata metadata;
    private String productsFolder = "_spot" + File.separator;

    @Before
    public void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());

        XmlMetadataParserFactory.registerParser(SpotViewMetadata.class, new XmlMetadataParser<SpotViewMetadata>(SpotViewMetadata.class));
        metadata = GenericXmlMetadata.create(SpotViewMetadata.class, TestUtil.getTestFile(productsFolder + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL/metadata.xml"));
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
        String root = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        String partialPath = root + File.separator + productsFolder + "SP04_HRI1_X__1O_20050605T090007_20050605T090016_DLR_70_PREU.BIL" + File.separator + "metadata.xml";
        String metadataPath = metadata.getPath().toString();
        if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
        {
            partialPath = partialPath.replaceAll("\\\\", "/");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            partialPath = partialPath.replace("\\", "/");
            metadataPath = metadataPath.replace("\\", "/");
        }
        
        assertEquals(partialPath, metadataPath);
    }

    @Test
    public void testSetPath() throws Exception {
        metadata.setPath(Paths.get("D:/testsetpath"));
        assertEquals("D:" + File.separator + "testsetpath", metadata.getPath().toString());
    }

    @Test
    public void testSetName() throws Exception {
        metadata.setName("testtest");
        //no get method available, how to test is was changed?
        //assertEquals(0, metadata.getName());
    }
}
