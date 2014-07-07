package org.esa.beam.dataio.s2.filepatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S2DatastripFilename {
    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_(S[0-9]{8}T[0-9]{6})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;

    public final String applicabilityStart;

    private S2DatastripFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String applicabilityStart) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.applicabilityStart = applicabilityStart;
    }

    public static S2DatastripFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2DatastripFilename(fileName,
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
