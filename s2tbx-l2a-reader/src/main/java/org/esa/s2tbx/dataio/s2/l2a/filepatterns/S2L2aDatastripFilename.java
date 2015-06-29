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

package org.esa.s2tbx.dataio.s2.l2a.filepatterns;

import org.esa.s2tbx.dataio.s2.filepatterns.S2DatastripFilename;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S2L2aDatastripFilename extends S2DatastripFilename {
    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_(S[0-9]{8}T[0-9]{6})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    private S2L2aDatastripFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String applicabilityStart) {
        super(name, missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, applicabilityStart);
    }

    public static S2DatastripFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2L2aDatastripFilename(fileName,
                                              matcher.group(1),
                                              matcher.group(2),
                                              matcher.group(3),
                                              matcher.group(4),
                                              matcher.group(5),
                                              matcher.group(6),
                                              matcher.group(7)
            );
        } else {
            return null;
        }
    }
}
