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

package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Ramona Manda
 */
public class SpotTake5ProductReaderPluginTest {

    private SpotTake5ProductReaderPlugin plugIn;
    private String productsFolder = "_spot" + File.separator;

    @Before
    public void setup() {
        plugIn = new SpotTake5ProductReaderPlugin();
    }

    @Test
    public void spotTake5ReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(SpotConstants.SPOT4_TAKE5_FORMAT_NAME[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(SpotTake5ProductReaderPlugin.class, plugIn.getClass());
    }

    @Test
    public void testDecodeQualificationForXML() throws IOException {
        assumeTrue(TestUtil.testdataAvailable());

        Date startDate = Calendar.getInstance().getTime();
        DecodeQualification decodeQualification = plugIn.getDecodeQualification(TestUtil.getTestFile(productsFolder + "dimap/test_ST4_MT.xml"));
        assertEquals(DecodeQualification.UNABLE, decodeQualification);
        decodeQualification = plugIn.getDecodeQualification(TestUtil.getTestFile(productsFolder + "SPOT4_HRVIR1_XS_88888888_N1A.tgz"));
        assertEquals(DecodeQualification.INTENDED, decodeQualification);
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The decoding time for the file is too big!", (endDate.getTime() - startDate.getTime()) / 1000 < 30);//30 sec
    }

    @Test
    public void testFileExtensions() {
        final String[] fileExtensions = plugIn.getDefaultFileExtensions();
        assertNotNull(fileExtensions);
        final List<String> extensionList = Arrays.asList(fileExtensions);
        assertEquals(4, extensionList.size());
        assertEquals(".xml", extensionList.get(0));
        assertEquals(".XML", extensionList.get(1));
        assertEquals(".tgz", extensionList.get(2));
        assertEquals(".TGZ", extensionList.get(3));
    }

    @Test
    public void testFormatNames() {
        final String[] formatNames = plugIn.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals("SPOT4Take5", formatNames[0]);
    }

    @Test
    public void testInputTypes() {
        final Class[] classes = plugIn.getInputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);
        final List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    @Test
    public void testProductFileFilter() {
        final SnapFileFilter snapFileFilter = plugIn.getProductFileFilter();
        assertNotNull(snapFileFilter);
        assertArrayEquals(plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(plugIn.getDescription(Locale.getDefault())));
    }

}
