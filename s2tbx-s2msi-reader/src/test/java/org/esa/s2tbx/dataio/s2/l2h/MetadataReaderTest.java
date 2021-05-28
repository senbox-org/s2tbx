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

package org.esa.s2tbx.dataio.s2.l2h;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l2hf.l2h.metadata.IL2hGranuleMetadata;
import org.esa.s2tbx.dataio.s2.l2hf.l2h.metadata.IL2hProductMetadata;
import org.esa.s2tbx.dataio.s2.l2hf.l2h.metadata.L2hMetadataFactory;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;

/**
 * @author Florian Douziech
 */
public class MetadataReaderTest {


    public IL2hProductMetadata getUserProductS2() throws Exception
    {
        final Path path = buildPathResource("metadata/S2A_MSIL2H_20200210T105201_N9999_R051_T31UFS_20200210T113338.xml");
        IL2hProductMetadata productMetadata = L2hMetadataFactory.createL2hProductMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));

        return productMetadata;
    }

    public IL2hProductMetadata getUserProductLS8() throws Exception
    {
        final Path path = buildPathResource("metadata/LS8_OLIL2H_20200121T103424_N9999_R198_T31UFS_20200128T084427.xml");
        
        IL2hProductMetadata productMetadata = L2hMetadataFactory.createL2hProductMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));

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
        IL2hProductMetadata productMetadata = getUserProductS2();
        assertNotNull(productMetadata);
    }

    @Test
    public void test2() throws Exception
    {
        IL2hProductMetadata productMetadata = getUserProductLS8();
        assertNotNull(productMetadata);
    }

    @Test
    public void test3() throws Exception
    {
        final Path path = buildPathResource("granule/S2A_MSIL2H_20200210T105201_N9999_R051_T31UFS_20200210T113338.xml");
        IL2hGranuleMetadata granuleMetadata = L2hMetadataFactory.createL2hGranuleMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));
        assertNotNull(granuleMetadata);
    }

    @Test
    public void test4() throws Exception
    {
        final Path path = buildPathResource("granule/LS8_OLIL2H_20200121T103424_N9999_R198_T31UFS_20200128T084427.xml");
        IL2hGranuleMetadata granuleMetadata = L2hMetadataFactory.createL2hGranuleMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));
        assertNotNull(granuleMetadata);
    }

}
