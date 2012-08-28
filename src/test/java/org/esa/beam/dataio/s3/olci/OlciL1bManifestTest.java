/*
 * Copyright (c) 2012. Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package org.esa.beam.dataio.s3.olci;

import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

public class OlciL1bManifestTest {

    private OlciL1bManifest manifestTest;

    @Before
    public void before() throws ParserConfigurationException, IOException, SAXException {
        InputStream stream = getClass().getResourceAsStream("L1b_TEST_manifest.xml");
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            manifestTest = new OlciL1bManifest(doc);
        } finally {
            stream.close();
        }
    }

    @Test
    public void testGetProductName() {
        assertEquals("S3_OL_1_ERR_20130621T100921_20130621T101413_00291_000001_001_EST_TEST_00",
                     manifestTest.getProductName());
    }

    @Test
    public void testGetProductType() {
        assertEquals("  OL_1_ERR", manifestTest.getProductType());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Sentinel 3 Level 1B Product", manifestTest.getDescription());
    }

    @Test
    public void testGetLineCount() {
        assertEquals(1664, manifestTest.getLineCount());
    }

    @Test
    public void testGetColumnCount() {
        assertEquals(1217, manifestTest.getColumnCount());
    }

    @Test
    public void testGetStartTime() throws ParseException {
        ProductData.UTC expected = ProductData.UTC.parse("2013-06-21T10:09:20.659100", "yyyy-MM-dd'T'HH:mm:ss");
        assertTrue(expected.equalElems(manifestTest.getStartTime()));
    }

    @Test
    public void testGetStopTime() throws ParseException {
        ProductData.UTC expected = ProductData.UTC.parse("2013-06-21T10:14:12.597100", "yyyy-MM-dd'T'HH:mm:ss");
        assertTrue(expected.equalElems(manifestTest.getStopTime()));
    }

    @Test
    public void testGetMeasurementDataSetPointers() {
        List<DataSetPointer> dataSetPointers = manifestTest.getDataSetPointers(DataSetPointer.Type.M);
        assertEquals(22, dataSetPointers.size());
    }

    @Test
    public void testGetAnnotationDataSetPointers() {
        List<DataSetPointer> dataSetPointers = manifestTest.getDataSetPointers(DataSetPointer.Type.A);
        assertEquals(5, dataSetPointers.size());
    }

    @Test
    public void testGetFixedHeader() {
        MetadataElement fixedHeader = manifestTest.getFixedHeader();
        assertNotNull(fixedHeader);
        assertEquals("Fixed_Header", fixedHeader.getName());
        assertEquals(7, fixedHeader.getNumAttributes());
        assertEquals("TEST", fixedHeader.getAttributeString("File_Class"));
        assertEquals(2, fixedHeader.getNumElements());
        MetadataElement validityPeriodElement = fixedHeader.getElement("Validity_Period");
        assertNotNull(validityPeriodElement);
        assertEquals(2, validityPeriodElement.getNumAttributes());
    }
}
