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

package org.esa.s2tbx.dataio.s2.l2f;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata.IL2fGranuleMetadata;
import org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata.IL2fProductMetadata;
import org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata.L2fMetadataFactory;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;

/**
 * @author Florian Douziech
 */
public class MetadataReaderTest {

    public IL2fProductMetadata getUserProductS2() throws Exception
    {
        final Path path = buildPathResource("metadata/S2A_MSIL2F_20200101T105441_N9999_R051_T31UFS_20200101T112309.xml");
        IL2fProductMetadata productMetadata = L2fMetadataFactory.createL2fProductMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));

        return productMetadata;
    }

    public IL2fProductMetadata getUserProductLS8() throws Exception
    {
        final Path path = buildPathResource("metadata/LS8_OLIL2F_20200121T103424_N9999_R198_T31UFS_20200128T084427.xml");
        IL2fProductMetadata productMetadata = L2fMetadataFactory.createL2fProductMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));

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
        IL2fProductMetadata productMetadata = getUserProductS2();
        assertNotNull(productMetadata);
    }

    @Test
    public void test2() throws Exception
    {
        IL2fProductMetadata productMetadata = getUserProductLS8();
        assertNotNull(productMetadata);
    }

    @Test
    public void test3() throws Exception
    {
        final Path path = buildPathResource("granule/S2A_MSIL2F_20200101T105441_N9999_R051_T31UFS_20200101T112309.xml");
        IL2fGranuleMetadata granuleMetadata = L2fMetadataFactory.createL2fGranuleMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));
        assertNotNull(granuleMetadata);
    }

    @Test
    public void test4() throws Exception
    {
        final Path path = buildPathResource("granule/LS8_OLIL2F_20200121T103424_N9999_R198_T31UFS_20200128T084427.xml");
        IL2fGranuleMetadata granuleMetadata = L2fMetadataFactory.createL2fGranuleMetadata(new VirtualPath(path.toString(), VirtualDirEx.build(path.getParent())));
        assertNotNull(granuleMetadata);
    }

}
