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

package org.esa.s2tbx.dataio.s2.l1b;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;

/**
 * @author  opicas-p
 */
public class MetadataReaderTest {

    public IL1bProductMetadata getUserProduct() throws Exception
    {
        IL1bProductMetadata productMetadata = L1bMetadataFactory.createL1bProductMetadata(buildPathResource("S2A_OPER_MTD_SAFL1B_PDMC_20140926T120000_R069_V20130707T171925_20130707T172037.xml"));

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
        IL1bProductMetadata product = getUserProduct();

        assertNotNull(product);
    }

    @Test
    public void test2() throws Exception
    {
        IL1bGranuleMetadata granuleMetadata = null;

        granuleMetadata = L1bMetadataFactory.createL1bGranuleMetadata(buildPathResource("S2A_OPER_MTD_L1B_GR_MPS__20140926T120000_S20130707T171927_D06.xml"));
        assertNotNull(granuleMetadata);
    }

    @Test
    public void test3() throws Exception
    {
        IL1bDatastripMetadata datastripMetadata = null;

        datastripMetadata = L1bMetadataFactory.createL1bDatastripMetadata(buildPathResource("S2A_OPER_MTD_L1B_DS_MPS__20140926T120000_S20130707T171925.xml"));
        assertNotNull(datastripMetadata);
    }
}
