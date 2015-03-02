package org.esa.beam.dataio.s2.filepatterns;

import org.esa.beam.util.logging.BeamLogManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Norman Fomferra
 */
public class S2L1bGranuleDirFilename {

    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_(S[0-9]{8}T[0-9]{6})(_D(0[1-9]|1[0-2]))(_N[0-9]{2}\\.[0-9]{2})";
    ;

    final static Pattern PATTERN = Pattern.compile(REGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;
    public final String startDate;
    public final String detectorId;
    public final String processingBaseline;

    private S2L1bGranuleDirFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String startDate, String detectorId, String processingBaseline) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.detectorId = detectorId;
        this.processingBaseline = processingBaseline;
    }

    public static boolean isGranuleFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public S2L1bGranuleMetadataFilename getMetadataFilename() {
        String tmp = String.format("%s_%s_%s%s_%s_%s_%s%s.xml", missionID, fileClass, "MTD_", fileSemantic, siteCentre, creationDate, startDate, detectorId);
        return S2L1bGranuleMetadataFilename.create(tmp);
    }

    public S2L1bGranuleImageFilename getImageFilename(String bandId) {
        String newBandId = bandId;

        if (newBandId.length() == 2) {
            newBandId = new String(bandId.charAt(0) + "0" + bandId.charAt(1));
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s_%s%s_%s.jp2", missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, startDate, detectorId, newBandId);
        return S2L1bGranuleImageFilename.create(tmp);
    }

    public S2L1bGranuleImageFilename getImageFilenameByDetector(String detectorId, String bandId) {
        String newDetectorId = detectorId;

        if (newDetectorId.length() == 2) {
            newDetectorId = new String(detectorId.charAt(0) + "0" + detectorId.charAt(1));
        }

        String newBandId = bandId;

        if (newBandId.length() == 2) {
            newBandId = new String(bandId.charAt(0) + "0" + bandId.charAt(1));
        }

        String tmp = String.format("%s_%s_%s%s_%s_%s_%s%s_%s.jp2", missionID, fileClass, fileCategory, fileSemantic, siteCentre, creationDate, startDate, newDetectorId, newBandId);
        return S2L1bGranuleImageFilename.create(tmp);
    }

    public static S2L1bGranuleDirFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2L1bGranuleDirFilename(fileName,
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
            BeamLogManager.getSystemLogger().warning(String.format("%s GranuleDir didn't match regexp %s", fileName, PATTERN.toString()));
            return null;
        }
    }

    public String getDetectorId() {
        return this.detectorId.substring(1);
    }
}
