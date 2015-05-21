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

import org.esa.s2tbx.dataio.s2.l2a.filepatterns.S2L2aProductFilename;
import org.esa.snap.framework.dataio.DecodeQualification;
import org.esa.snap.framework.dataio.ProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.util.io.SnapFileFilter;
import org.esa.snap.util.logging.BeamLogManager;

import java.io.File;
import java.util.Locale;

/**
 * @author Norman Fomferra
 */
public class Sentinel2L2AProductReaderPlugIn implements ProductReaderPlugIn {

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        BeamLogManager.getSystemLogger().fine("Getting decoders...");

        File file = new File(input.toString());
        DecodeQualification deco = S2L2aProductFilename.isProductFilename(file.getName()) ? DecodeQualification.SUITABLE : DecodeQualification.UNABLE;
        if (deco.equals(DecodeQualification.SUITABLE)) {
            if (S2L2aProductFilename.create(file.getName()).fileSemantic.contains("L2A")) {
                deco = DecodeQualification.INTENDED;
            }
            else
            {
                deco = DecodeQualification.UNABLE;
            }
        }

        return deco;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        BeamLogManager.getSystemLogger().info("Building product reader...");

        return new Sentinel2L2AProductReader(this, false);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{S2L2AConfig.FORMAT_NAME};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{S2L2AConfig.MTD_EXT};
    }

    @Override
    public String getDescription(Locale locale) {
        return "Sentinel-2 MSI L2A";
    }

    @Override
    public SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(S2L2AConfig.FORMAT_NAME,
                                  getDefaultFileExtensions(),
                                  "Sentinel-2 MSI L2A product or tile");
    }
}
