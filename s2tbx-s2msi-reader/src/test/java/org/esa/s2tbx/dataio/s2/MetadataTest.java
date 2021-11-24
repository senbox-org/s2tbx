/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2;



import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.l1c.metadata.IL1cGranuleMetadata;
import org.esa.s2tbx.dataio.s2.l1c.metadata.L1cMetadataFactory;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.junit.Test;


import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.esa.s2tbx.dataio.s2.l1c.metadata.IL1cProductMetadata;
import org.xml.sax.SAXException;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class MetadataTest {


    /**
     * Test that if we have the (old) psd 12 root xml file, we can still parse it after update
     */
    @Test
    public void testUpdatePSD12RootXML() {
        URL url = getClass().getResource("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        Path psd12RootXmlFileName = null;
        try {
            File file = new File(url.toURI());
            psd12RootXmlFileName = file.toPath();
            IL1cProductMetadata productMetadata = L1cMetadataFactory.createL1cProductMetadata(new VirtualPath(psd12RootXmlFileName.toString(), VirtualDirEx.build(file.toPath().getParent())));
            assertNotNull(productMetadata.getMetadataElement());
        } catch (IOException e) {
            org.junit.Assert.fail(e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            org.junit.Assert.fail("Parser Configuration Exception in XML: " + psd12RootXmlFileName.getFileName().toString());
            e.printStackTrace();
        } catch (SAXException e) {
            org.junit.Assert.fail("SAX Exception in XML: " + psd12RootXmlFileName.getFileName().toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            org.junit.Assert.fail(e.getMessage());
            e.printStackTrace();
        }
    }

    public IL1cProductMetadata getUserProduct() throws Exception
    {
        URL url = getClass().getResource("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        Path psd12RootXmlFileName = null;

        File file = new File(url.toURI());
        psd12RootXmlFileName = file.toPath();
        IL1cProductMetadata productMetadata = L1cMetadataFactory.createL1cProductMetadata(new VirtualPath(psd12RootXmlFileName.toString(), VirtualDirEx.build(file.toPath().getParent())));

        return productMetadata;
    }

    public IL1cGranuleMetadata getTileProduct() throws Exception
    {
        URL url = getClass().getResource("l1c/metadata/S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLF.xml");
        Path tilePath = null;

        File file = new File(url.toURI());
        tilePath = file.toPath();
        IL1cGranuleMetadata granuleMetadata = L1cMetadataFactory.createL1cGranuleMetadata(new VirtualPath(tilePath.toString(), VirtualDirEx.build(file.toPath().getParent())), null);

        return granuleMetadata;
    }

    @Test
    public void test2() throws Exception
    {
        URL url = getClass().getResource("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        Path psd12RootXmlFileName = null;
        try {
            File file = new File(url.toURI());
            psd12RootXmlFileName = file.toPath();
            IL1cProductMetadata productMetadata = L1cMetadataFactory.createL1cProductMetadata(new VirtualPath(psd12RootXmlFileName.toString(), VirtualDirEx.build(file.toPath().getParent())));
            assertNotNull(productMetadata.getMetadataElement());
            String[] tiles = productMetadata.getTiles().toArray(new String[productMetadata.getTiles().size()]);
            assertEquals("S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLD_N01.01", tiles[0]);
            S2OrthoGranuleDirFilename gdir = S2OrthoGranuleDirFilename.create(tiles[0]);
            assertEquals("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml", gdir.getMetadataFilename().name);
        } catch (IOException e) {
            org.junit.Assert.fail(e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            org.junit.Assert.fail("Parser Configuration Exception in XML: " + psd12RootXmlFileName.getFileName().toString());
            e.printStackTrace();
        } catch (SAXException e) {
            org.junit.Assert.fail("SAX Exception in XML: " + psd12RootXmlFileName.getFileName().toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            org.junit.Assert.fail(e.getMessage());
            e.printStackTrace();
        }

    }

    @Test
    public void test3() throws Exception
    {
        URL url = getClass().getResource("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        Path psd12RootXmlFileName = null;

        File file = new File(url.toURI());
        psd12RootXmlFileName = file.toPath();
        IL1cProductMetadata productMetadata = L1cMetadataFactory.createL1cProductMetadata(new VirtualPath(psd12RootXmlFileName.toString(), VirtualDirEx.build(file.toPath().getParent())));

        assertNotNull(productMetadata);

        Collection<String> tiles = productMetadata.getTiles();

        for (String granuleName: tiles)
        {
            System.err.println(granuleName);
        }

    }


    @Test
    public void testTileProductsMetadataExistence() throws Exception
    {
        IL1cProductMetadata productMetadata = getUserProduct();

        Collection<String> tiles = productMetadata.getTiles();

        URL aUrl = getClass().getResource(
          "l1c/data/S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");

        if(aUrl != null)
        {
            File baseDir = new File(aUrl.toURI());
            if(baseDir.exists())
            {
                assertTrue(baseDir.exists());
                assertTrue(baseDir.isDirectory());

                for (String granuleName: tiles)
                {
                    File nestedMetadata = new File(baseDir, "GRANULE\\" + granuleName);
                    System.err.println(nestedMetadata.getAbsolutePath());
                    assertTrue(nestedMetadata.exists());
                    assertTrue(nestedMetadata.isDirectory());

                    S2OrthoGranuleDirFilename aGranuleDir = S2OrthoGranuleDirFilename.create(granuleName);
                    String theName = aGranuleDir.getMetadataFilename().name;

                    File nestedGranuleMetadata = new File(baseDir, "GRANULE\\" + granuleName + "\\" + theName);
                    assertTrue(nestedGranuleMetadata.exists());
                    assertTrue(nestedGranuleMetadata.isFile());
                }
            }
        }
    }


    @Test
    public void testPopulateTileInfo() throws Exception
    {
        IL1cGranuleMetadata granuleMetadata = getTileProduct();

        assertNotNull(granuleMetadata);

        granuleMetadata.getTileGeometries();
    }


    @Test
    public void testPopulateOtherTileInfo() throws Exception
    {
        IL1cGranuleMetadata granule = getTileProduct();

        assertNotNull(granule);

        granule.getSunGrid();
        granule.getViewingAnglesGrid();
    }

}
