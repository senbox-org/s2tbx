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

package org.esa.s2tbx.dataio.s2.ortho;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Julien Malik
 */
public class S2CRSHelper {

    private final static String TILE_IDENTIFIER_REGEX = "T([0-9]{2})([A-Z]{3})";
    private final static Pattern TILE_IDENTIFIER_PATTERN = Pattern.compile(TILE_IDENTIFIER_REGEX);

    private final static String EPSG_REGEX = "EPSG:32([0-9]{1})([0-9]{2})";
    private final static Pattern EPSG_PATTERN = Pattern.compile(EPSG_REGEX);

    static public String tileIdentifierToEPSG(String tileIdentifier) {
        String epsgcode;
        final Matcher matcher = TILE_IDENTIFIER_PATTERN.matcher(tileIdentifier);
        if (matcher.matches()) {
            String zone = matcher.group(1);
            String mgrs = matcher.group(2);
            char gridZone = mgrs.charAt(0);
            char hemisphere = (gridZone <= 'M' ? 'S' : 'N');
            epsgcode = String.format("EPSG:32%s%s", hemisphere == 'N' ? '6' : '7', zone);
        } else {
            throw new IllegalArgumentException(String.format("Illegal tile identifier %s", tileIdentifier));
        }
        return epsgcode;
    }

    static public String epsgToDisplayName(String epsg) {
        String displayName;
        final Matcher matcher = EPSG_PATTERN.matcher(epsg);
        if (matcher.matches()) {
            char hemisphereIdentifier = matcher.group(1).charAt(0);
            String zone = matcher.group(2);
            char hemisphere = (('6' == hemisphereIdentifier) ? 'N' : 'S');
            displayName = String.format("WGS84 / UTM zone %s%s", zone, hemisphere);
        } else {
            throw new IllegalArgumentException(String.format("Illegal UTM EPSG code identifier %s", epsg));
        }
        return displayName;
    }

    static public String epsgToShortDisplayName(String epsg) {
        String displayName = null;
        if (epsg != null) {
            final Matcher matcher = EPSG_PATTERN.matcher(epsg);
            if (matcher.matches()) {
                char hemisphereIdentifier = matcher.group(1).charAt(0);
                String zone = matcher.group(2);
                char hemisphere = (('6' == hemisphereIdentifier) ? 'N' : 'S');
                displayName = String.format("UTM%s%s", zone, hemisphere);
            } else {
                throw new IllegalArgumentException(String.format("Illegal UTM EPSG code identifier %s", epsg));
            }
        }
        return displayName;
    }

}
