package org.esa.s2tbx.dataio.s2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jmalik on 01/07/15.
 */
public class S2CRSHelper {

    private final static String TILE_IDENTIFIER_REGEX = "T([0-9]{2})([A-Z]{3})";
    private final static Pattern TILE_IDENTIFIER_PATTERN = Pattern.compile(TILE_IDENTIFIER_REGEX);

    private final static String EPSG_REGEX = "EPSG:32([0-9]{1})([0-9]{2})";
    private final static Pattern EPSG_PATTERN = Pattern.compile(EPSG_REGEX);

    static public String tileIdentifierToEPSG(String tileIdentifier)
    {
        String epsgcode;
        final Matcher matcher = TILE_IDENTIFIER_PATTERN.matcher(tileIdentifier);
        if (matcher.matches()) {
            String zone = matcher.group(1);
            String mgrs = matcher.group(2);
            char gridZone = mgrs.charAt(0);
            char hemisphere = (gridZone <= 'M' ? 'S' : 'N');
            epsgcode = String.format("EPSG:32%s%s", hemisphere == 'N' ? '6' : '7', zone);
        }
        else {
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
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal UTM EPSG code identifier %s", epsg));
        }
        return displayName;
    }

    static public String epsgToShortDisplayName(String epsg) {
        String displayName;
        final Matcher matcher = EPSG_PATTERN.matcher(epsg);
        if (matcher.matches()) {
            char hemisphereIdentifier = matcher.group(1).charAt(0);
            String zone = matcher.group(2);
            char hemisphere = (('6' == hemisphereIdentifier) ? 'N' : 'S');
            displayName = String.format("UTM%s%s", zone, hemisphere);
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal UTM EPSG code identifier %s", epsg));
        }
        return displayName;
    }

}
