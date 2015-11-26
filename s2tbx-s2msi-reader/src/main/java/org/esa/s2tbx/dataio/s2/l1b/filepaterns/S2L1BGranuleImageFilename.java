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

package org.esa.s2tbx.dataio.s2.l1b.filepaterns;

import org.esa.s2tbx.dataio.s2.filepatterns.S2GranuleImageFilename;
import org.esa.snap.core.util.SystemUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2L1BGranuleImageFilename extends S2GranuleImageFilename {

    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_S([0-9]{8}T[0-9]{6})(_D(0[1-9]|1[0-2]))(_B[A-B|0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    private S2L1BGranuleImageFilename(String name,
                                      String missionID,
                                      String fileClass,
                                      String fileCategory,
                                      String fileSemantic,
                                      String siteCentre,
                                      String creationDate,
                                      String instanceID,
                                      String absoluteOrbit,
                                      String tileNumber,
                                      String bandIndex) {
        super(name,
                missionID,
                fileClass,
                fileCategory,
                fileSemantic,
                siteCentre,
                creationDate,
                instanceID,
                absoluteOrbit,
                tileNumber,
                bandIndex);
    }

    public static boolean isImageFilename(String name) {
        return PATTERN.matcher(name).matches();
    }


    public static S2GranuleImageFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2L1BGranuleImageFilename(fileName,
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    matcher.group(5),
                    matcher.group(6),
                    matcher.group(7),
                    matcher.group(8),
                    matcher.group(9),
                    matcher.group(10));
        } else {
            SystemUtils.LOG.warning(String.format("%s S2GranuleImageFilename didn't match regexp %s", fileName, PATTERN.toString()));
            return null;
        }
    }
}
