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

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.util.SystemUtils;
import org.esa.snap.util.io.SnapFileFilter;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2L1BProduct20MReaderPlugIn extends Sentinel2L1BProductReaderPlugin {


    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader...");
        return new Sentinel2L1BProductReader(this, false, 20, getReaderFactory());
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{getConfig().getFormatName()+"-20M"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L1B 20M";
    }
}
