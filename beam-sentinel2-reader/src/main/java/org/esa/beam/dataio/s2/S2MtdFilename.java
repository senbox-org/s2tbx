package org.esa.beam.dataio.s2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2MtdFilename {
    final static String IMG_FORMAT = "IMG_GPP%s_%s_%s_%s_%02d_000000_%s.jp2";
    final static String REGEX = "MTD_GPP([A-Z0-9]{3})_(\\d{3})_(\\d{14})_(\\d{14})_(\\d{4}).xml";
    final static Pattern PATTERN = Pattern.compile(REGEX);
    public final String name;
    public final String procLevel;
    public final String orbitNo;
    public final String start;
    public final String stop;

    public final String sceneId;

    private S2MtdFilename(String name, String procLevel, String orbitNo, String start, String stop, String sceneId) {
        this.name = name;
        this.procLevel = procLevel;
        this.orbitNo = orbitNo;
        this.start = start;
        this.stop = stop;
        this.sceneId = sceneId;
    }

    static boolean isMetadataFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public String getImgFilename(int bandId, String tileId) {
        return String.format(IMG_FORMAT, procLevel, orbitNo, start, stop, bandId, tileId);
    }

    public static S2MtdFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        final boolean matches = matcher.matches();
        if (matches) {
            return new S2MtdFilename(fileName,
                                      matcher.group(1),
                                      matcher.group(2),
                                      matcher.group(3),
                                      matcher.group(4),
                                      matcher.group(5));
        } else {
            return null;
        }
    }
}
