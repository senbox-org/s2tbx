package org.esa.beam.dataio.s2.filepatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S2DatastripDirFilename {
    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_(S[0-9]{8}T[0-9]{6})(_N[0-9]{2}\\.[0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;

    public final String applicabilityStart;
    public final String processingBaseline;

    private S2DatastripDirFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String applicabilityStart, String processingBaseline) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.applicabilityStart = applicabilityStart;
        this.processingBaseline = processingBaseline;
    }

    public S2DatastripFilename getDatastripFilename(String fileCategory)
    {
        String defaultFileCategory = "MTD_";
        if(fileCategory != null)
        {
            defaultFileCategory = fileCategory;
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s_%s.xml", missionID, fileClass, defaultFileCategory, fileSemantic, siteCentre, creationDate, applicabilityStart);
        return S2DatastripFilename.create(tmp);
    }

    public static S2DatastripDirFilename create(String fileName, String fileCategory) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches())
        {
            if(fileCategory == null)
            {
                return new S2DatastripDirFilename(fileName,
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3),
                        matcher.group(4),
                        matcher.group(5),
                        matcher.group(6),
                        matcher.group(7),
                        matcher.group(8)
                );
            }
            return new S2DatastripDirFilename(fileName.replace(matcher.group(3), fileCategory),
                    matcher.group(1),
                    matcher.group(2),
                    fileCategory,
                    matcher.group(4),
                    matcher.group(5),
                    matcher.group(6),
                    matcher.group(7),
                    matcher.group(8)
            );

        } else {
            return null;
        }
    }
}
