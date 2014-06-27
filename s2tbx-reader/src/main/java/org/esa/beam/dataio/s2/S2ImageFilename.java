package org.esa.beam.dataio.s2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2ImageFilename {

    final static String REGEX = "IMG_GPP([A-Z0-9]{3})_(\\d{3})_(\\d{14})_(\\d{14})_(\\d{2})_000000_(\\d{2}[A-Z]{3}).jp2";
    final static Pattern PATTERN = Pattern.compile(REGEX);
    final static String BAND_REGEX_FORMAT = "IMG_GPP%s_%s_%s_%s_(\\d{2})_000000_%s.jp2";

    public final String name;
    public final String procLevel;
    public final String orbitNo;
    public final String start;
    public final String stop;
    public final String band;
    public final String tileId;

    private final Pattern bandPattern;

    private S2ImageFilename(String name, String procLevel, String orbitNo, String start, String stop, String band, String tileId) {
        this.name = name;
        this.procLevel = procLevel;
        this.orbitNo = orbitNo;
        this.start = start;
        this.stop = stop;
        this.band = band;
        this.tileId = tileId;

        String regex = String.format(BAND_REGEX_FORMAT, procLevel, orbitNo, start, stop, tileId);
        this.bandPattern = Pattern.compile(regex);
    }

    static boolean isImageFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public int getBand() {
        try {
            return Integer.parseInt(band);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public int getBand(String filename) {
        Matcher matcher = bandPattern.matcher(filename);
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static S2ImageFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2ImageFilename(fileName,
                                     matcher.group(1),
                                     matcher.group(2),
                                     matcher.group(3),
                                     matcher.group(4),
                                     matcher.group(5),
                                     matcher.group(6));
        } else {
            return null;
        }
    }
}
