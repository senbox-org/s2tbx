package org.esa.beam.dataio.s2.filepatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2GranuleDirFilename {

    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})(_A[0-9]{6})(_T[A-Z|0-9]{5})(_N[0-9]{2}\\.[0-9]{2})(\\.[A-Z|a-z|0-9]{3,4})?";
    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;
    public final String absoluteOrbit;
    public final String tileNumber;
    public final String processingBaseline;

    private S2GranuleDirFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String absoluteOrbit, String tileNumber, String processingBaseline) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.absoluteOrbit = absoluteOrbit;
        this.tileNumber = tileNumber;
        this.processingBaseline = processingBaseline;
    }

    public static boolean isGranuleFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public S2GranuleMetadataFilename getMetadataFilename()
    {
        String tmp = String.format("%s_%s_%s%s_%s_%s%s%s.xml", missionID, fileClass, "MTD_", fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber);
        return S2GranuleMetadataFilename.create(tmp);
    }

    public S2GranuleImageFilename getImageFilename(String bandId)
    {
        String newBandId = bandId;

        if(newBandId.length() == 2)
        {
            newBandId = new String(bandId.charAt(0) + "0" + bandId.charAt(1));
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s%s%s_%s.jp2", missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, absoluteOrbit, tileNumber, newBandId);
        return S2GranuleImageFilename.create(tmp);
    }

    public static S2GranuleDirFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2GranuleDirFilename(fileName,
                                     matcher.group(1),
                                     matcher.group(2),
                                     matcher.group(3),
                                     matcher.group(4),
                                     matcher.group(5),
                                     matcher.group(6),
                                     matcher.group(7),
                                     matcher.group(8),
                                     matcher.group(9)
                    );
        } else {
            return null;
        }
    }
}
