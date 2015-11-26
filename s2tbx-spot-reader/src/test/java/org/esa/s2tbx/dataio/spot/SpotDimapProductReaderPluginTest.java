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
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * @author Ramona Manda
 */
public class SpotDimapProductReaderPluginTest {
    @Test
    public void spotDimapReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(SpotConstants.DIMAP_FORMAT_NAMES[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(SpotDimapProductReaderPlugin.class, plugIn.getClass());
    }

    public void testGetDefaultFileExtension() {
        final SpotDimapProductReaderPlugin plugIn = new SpotDimapProductReaderPlugin();

        final String[] defaultFileExtensions = plugIn.getDefaultFileExtensions();
        assertEquals(".dim", defaultFileExtensions[0]);
        assertEquals(".DIM", defaultFileExtensions[1]);
        assertEquals(".zip", defaultFileExtensions[2]);
        assertEquals(".ZIP", defaultFileExtensions[3]);
    }
}
