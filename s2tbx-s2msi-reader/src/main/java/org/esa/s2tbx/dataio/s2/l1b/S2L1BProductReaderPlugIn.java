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
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

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

        if (!(input instanceof File)) {
            return DecodeQualification.UNABLE;
        }

        File file = (File) input;
        if(!isValidExtension(file)) {
            return DecodeQualification.UNABLE;
        }


        //TODO Jean uncoment the lines when implementing for remote files
//        INamingConvention namingConvention = null;
//        try {
//            namingConvention = NamingConventionFactory.createL1BNamingConvention(S2NamingConventionUtils.transformToSentinel2VirtualPath(file.toPath()));
//        } catch (IOException e) {
//            return DecodeQualification.UNABLE;
//        }
//        if(namingConvention != null && namingConvention.getProductLevel().equals(S2Config.Sentinel2ProductLevel.L1B)) {
//            return DecodeQualification.INTENDED;
//        }
        return DecodeQualification.UNABLE;

    }

    @Override
    public ProductReader createReaderInstance() {
        SystemUtils.LOG.info("Building product reader L1B Multisize...");

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
