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

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2ProductReaderPlugIn;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Norman Fomferra
 */
public class S2L1BProductReaderPlugIn extends S2ProductReaderPlugIn {

    private static final Logger logger = Logger.getLogger(S2L1BProductReaderPlugIn.class.getName());

    public final static String L1B_LEVEL = "L1B";

    public S2L1BProductReaderPlugIn() {
    }

    protected String getLevel() {
        return L1B_LEVEL;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_MULTI);
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        logger.fine("Getting decoders...");

        if (!(input instanceof File)) {
            return DecodeQualification.UNABLE;
        }

        File file = (File) input;
        if (!isValidExtension(file)) {
            return DecodeQualification.UNABLE;
        }

        INamingConvention namingConvention;
        try {
            VirtualPath virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(file.toPath());
            namingConvention = NamingConventionFactory.createL1BNamingConvention(virtualPath);
        } catch (IOException e) {
            return DecodeQualification.UNABLE;
        }
        if (namingConvention != null && namingConvention.getProductLevel().equals(S2Config.Sentinel2ProductLevel.L1B)) {
            return DecodeQualification.INTENDED;
        }
        return DecodeQualification.UNABLE;
    }

    @Override
    public ProductReader createReaderInstance() {
        logger.info("Building product reader L1B Multisize...");

        return new Sentinel2L1BProductReader(this, Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_MULTI);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{String.format("%s-%s-MultiRes", FORMAT_NAME, L1B_LEVEL)};
    }

    @Override
    public String getDescription(Locale locale) {
        return String.format("Sentinel-2 MSI %s - all resolutions", L1B_LEVEL);
    }
}
