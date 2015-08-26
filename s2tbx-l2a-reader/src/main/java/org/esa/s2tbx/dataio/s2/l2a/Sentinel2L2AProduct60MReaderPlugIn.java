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

package org.esa.s2tbx.dataio.s2.l2a;

import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.util.SystemUtils;

import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2L2AProduct60MReaderPlugIn extends Sentinel2L2AProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader...");

        return new Sentinel2L2AProductReader(this, false, getReaderResolution());
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{FORMAT_NAME+"-60M"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L2A 60M";
    }

    @Override
    protected int getReaderResolution() {
        return S2SpatialResolution.R60M.resolution;
    }
}
