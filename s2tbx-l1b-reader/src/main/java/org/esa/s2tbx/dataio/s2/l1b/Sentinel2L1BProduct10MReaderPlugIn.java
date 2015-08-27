/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.util.SystemUtils;

import java.util.Locale;

/**
 * @author Nicolas Ducoin
 */
public class Sentinel2L1BProduct10MReaderPlugIn extends Sentinel2L1BProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader...");
        return new Sentinel2L1BProductReader(this, false, S2SpatialResolution.R10M);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{FORMAT_NAME+"-10M"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L1B 10M";
    }
}
