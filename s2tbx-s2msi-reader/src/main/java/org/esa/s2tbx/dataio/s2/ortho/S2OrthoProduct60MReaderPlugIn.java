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

package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.s2tbx.dataio.s2.l2a.Sentinel2L2AProductReader;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.util.SystemUtils;

import java.util.Locale;

import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.epsgToDisplayName;
import static org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper.epsgToShortDisplayName;


/**
 * @author Nicolas Ducoin
 */
public abstract class S2OrthoProduct60MReaderPlugIn extends S2OrthoProductReaderPlugIn {

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader 60M");

        if (getLevel() != null && getLevel().equals("L2A")) {
            return new Sentinel2L2AProductReader(this, S2SpatialResolution.R60M, getEPSG());
        } else {
            return new Sentinel2L1CProductReader(this, S2SpatialResolution.R60M, getEPSG());
        }
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{String.format("%s-60M-%s", getFormatName(), epsgToShortDisplayName(getEPSG()))};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI %s - 6Om bands - %s", getLevel(), epsgToDisplayName(getEPSG()));
    }
}
