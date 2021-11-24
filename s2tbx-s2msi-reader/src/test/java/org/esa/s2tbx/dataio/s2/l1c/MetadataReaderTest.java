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

package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l1c.metadata.IL1cDatastripMetadata;
import org.esa.s2tbx.dataio.s2.l1c.metadata.IL1cGranuleMetadata;
import org.esa.s2tbx.dataio.s2.l1c.metadata.IL1cProductMetadata;
import org.esa.s2tbx.dataio.s2.l1c.metadata.L1cMetadataFactory;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author opicas-p
 */
public class MetadataReaderTest {


    public IL1cProductMetadata getUserProduct() throws Exception
    {
        final Path path = buildPathResource("metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        IL1cProductMetadata productMetadata = L1cMetadataFactory.createL1cProductMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));

        return productMetadata;
    }

    public Path buildPathResource(String resource) throws Exception {
        URL url = getClass().getResource(resource);
        Path xmlPath = null;

        File file = new File(url.toURI());
        xmlPath = file.toPath();

        return xmlPath;
    }

    @Test
    public void test1() throws Exception
    {
        IL1cProductMetadata productMetadata = getUserProduct();
        assertNotNull(productMetadata);
        //assertTrue(productMetadata.getProductOrganization().getMetaDataLevel().equals("Expertise"));
        //assertTrue(productMetadata.getProductOrganization().getSpacecraft().equals("Sentinel-2A"));
        //assertEquals(productMetadata.getProductOrganization().getQuantificationValue(),1462, 1E-6);
    }

    @Test
    public void test2() throws Exception
    {
        final Path path = buildPathResource("metadata/S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLF.xml");
        IL1cGranuleMetadata granuleMetadata = L1cMetadataFactory.createL1cGranuleMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())), null);
        assertNotNull(granuleMetadata);
    }

    @Test
    public void test3() throws Exception
    {
        final Path path = buildPathResource("metadata/S2A_OPER_MTD_L1C_DS_CGS1_20130621T120000_S20091211T165928.xml");
        IL1cDatastripMetadata datastripMetadata = L1cMetadataFactory.createL1cDatastripMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));
        assertNotNull(datastripMetadata);
    }
}
