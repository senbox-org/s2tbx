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

import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.util.SystemUtils;

import java.util.Locale;

/**
 * @author Nicolas Ducoin
 */
public class S2L1BProduct10MReaderPlugIn extends S2L1BProductReaderPlugIn {

    public S2L1BProduct10MReaderPlugIn() {
    }

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader L1B 10M");

        return new Sentinel2L1BProductReader(this, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_10M);
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_10M);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{String.format("%s-10M", getFormatName())};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI %s - 10m bands", getLevel());
    }
}
