package org.esa.beam.dataio.s2.filepatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by opicas-p on 23/06/2014.
 */

public class S2ProductFilename {
    final static String REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3,4})?";
    final static String OPTIONALREGEX = "_S[0-9]{8}T[0-9]{6}|_O[0-9]{6}T[0-9]{6}|_V[0-9]{8}[T]?[0-9]{6}_[0-9]{8}[T]?[0-9]{6}|_D[0-9]{2}|_A[0-9]{6}|_R[0-9]{3}|_T[A-Z|0-9]{5}|_N[0-9]{2}\\.[0-9]{2}|_B[A-B|0-9]{2}|_W[F|P]|_L[N|D]";

    final static Pattern PATTERN = Pattern.compile(REGEX);
    final static Pattern OPTIONALPATTERN = Pattern.compile(OPTIONALREGEX);

    public final String name;
    public final String missionID;
    public final String fileClass;
    public final String fileCategory;
    public final String fileSemantic;
    public final String siteCentre;
    public final String creationDate;
    public final String instanceID;
    public String processingBaseline;
    public String tileNumber;
    public String absoluteOrbitNumber;
    public String detectorID;
    public String orbitalPeriod;
    public String relativeOrbitNumber;
    public String applicabilityStart;
    public String applicabilityPeriod;

    private S2ProductFilename(String name, String missionID, String fileClass, String fileCategory, String fileSemantic, String siteCentre, String creationDate, String instanceID) {
        this.name = name;
        this.missionID = missionID;
        this.fileClass = fileClass;
        this.fileCategory = fileCategory;
        this.fileSemantic = fileSemantic;
        this.siteCentre = siteCentre;
        this.creationDate = creationDate;
        this.instanceID = instanceID;

        Matcher m2 = OPTIONALPATTERN.matcher(instanceID);
        while(m2.find())
        {
            String captured = m2.group();
            if(captured.startsWith("_S"))
            {
                this.applicabilityStart = captured.substring(2);
            }
            else if (captured.startsWith("_R"))
            {
                this.relativeOrbitNumber = captured.substring(2);
            }
            else if (captured.startsWith("_V"))
            {
                this.applicabilityPeriod = captured.substring(2);
            }
            else if (captured.startsWith("_O"))
            {
                this.orbitalPeriod = captured.substring(2);
            }
            else if (captured.startsWith("_D"))
            {
                this.detectorID = captured.substring(2);
            }
            else if (captured.startsWith("_A"))
            {
                this.absoluteOrbitNumber = captured.substring(2);
            }
            else if (captured.startsWith("_T"))
            {
                this.tileNumber = captured.substring(2);
            }
            else if (captured.startsWith("_N"))
            {
                this.processingBaseline = captured.substring(2);
            }
        }
    }

    static public boolean isProductFilename(String name) {
        return PATTERN.matcher(name).matches();
    }

    public static boolean isMetadataFilename(String name)
    {
        boolean isProduct = isProductFilename(name);
        if(!isProduct)
        {
            return isProduct;
        }

        return name.toLowerCase().endsWith(".xml");
    }

    public static S2ProductFilename create(String fileName) {
        final Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.matches()) {
            return new S2ProductFilename(fileName,
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    matcher.group(5),
                    matcher.group(6),
                    matcher.group(7));
        } else {
            return null;
        }
    }
}
