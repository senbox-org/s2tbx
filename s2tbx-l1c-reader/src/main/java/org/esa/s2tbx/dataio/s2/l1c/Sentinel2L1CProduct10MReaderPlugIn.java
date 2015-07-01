/*
 *
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

package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.s2tbx.dataio.s2.S2CRSHelper;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.util.SystemUtils;

import java.util.Locale;

/**
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2L1CProduct10MReaderPlugIn extends Sentinel2L1CProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader 10M");

        return new Sentinel2L1CProductReader(this, false, 10, false, getEPSG());
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L1CConfig.getInstance().getFormatName()+"-10M"};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI L1C - 1Om bands - %s", S2CRSHelper.epsgToDisplayName(getEPSG()));
    }
}
