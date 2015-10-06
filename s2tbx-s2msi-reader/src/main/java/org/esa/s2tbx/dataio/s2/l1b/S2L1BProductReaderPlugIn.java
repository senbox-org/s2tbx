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

import org.esa.s2tbx.dataio.s2.S2ProductReaderPlugIn;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.s2tbx.dataio.s2.l1b.filepaterns.S2L1BGranuleMetadataFilename;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.util.SystemUtils;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * @author Norman Fomferra
 */
public class S2L1BProductReaderPlugIn extends S2ProductReaderPlugIn {

    public final static String L1B_LEVEL = "L1B";

    public S2L1BProductReaderPlugIn() {
    }

    protected String getLevel() {
        return L1B_LEVEL;
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        SystemUtils.LOG.fine("Getting decoders...");

        DecodeQualification decodeQualification = DecodeQualification.UNABLE;

        if (input instanceof File) {
            File file = (File) input;

            if (file.isFile()) {
                String fileName = file.getName();

                // first check it is a Sentinel-2 product
                Matcher matcher = PATTERN.matcher(fileName);
                if (matcher.matches()) {

                    // test for granule filename first as it is more restrictive
                    if (S2L1BGranuleMetadataFilename.isGranuleFilename(fileName)) {
                        String levelFromName = matcher.group(4).substring(0, 3);
                        if (levelFromName.equals(L1B_LEVEL)) {
                            decodeQualification = DecodeQualification.INTENDED;
                        }
                    } else if (S2ProductFilename.isMetadataFilename(fileName)) {
                        String levelFromName = matcher.group(4).substring(3, 6);
                        if (levelFromName.equals("L1B")) {
                            decodeQualification = DecodeQualification.INTENDED;
                        }
                    }
                }
            }
        }

        return decodeQualification;
    }

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader L1B Multisize...");

        return new Sentinel2L1BProductReader(this, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_MULTI);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{String.format("%s-%s-MultiRes", L1B_LEVEL, FORMAT_NAME)};
    }


    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI %s - all resolutions", L1B_LEVEL);
    }
}
