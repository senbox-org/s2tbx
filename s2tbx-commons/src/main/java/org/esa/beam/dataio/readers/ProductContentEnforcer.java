package org.esa.beam.dataio.readers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by kraftek on 9/16/2014.
 */
public class ProductContentEnforcer {

    //private String[] minimalFilePatternList;
    private Pattern[] minimalFilePatternList;
    //private String[] notAcceptedFilePatternList;
    private Pattern[] notAcceptedFilePatternList;

    public static ProductContentEnforcer create(String[] minimalPatterns) {
        return new ProductContentEnforcer(minimalPatterns, null);
    }

    public static ProductContentEnforcer create(String[] minimalPatterns, String[] notAcceptedPatterns) {
        return new ProductContentEnforcer(minimalPatterns, notAcceptedPatterns);
    }

    private ProductContentEnforcer(String[] requiredPatterns, String[] notAcceptedPatterns) {
        /*this.minimalFilePatternList = requiredPatterns;
        this.notAcceptedFilePatternList = notAcceptedPatterns;*/
        if (requiredPatterns != null) {
            minimalFilePatternList = new Pattern[requiredPatterns.length];
            for (int i = 0; i < requiredPatterns.length; i++) {
                minimalFilePatternList[i] = Pattern.compile(requiredPatterns[i], Pattern.CASE_INSENSITIVE);
            }
        }
        if (notAcceptedPatterns != null) {
            notAcceptedFilePatternList = new Pattern[notAcceptedPatterns.length];
            for (int i = 0; i < notAcceptedPatterns.length; i++) {
                notAcceptedFilePatternList[i] = Pattern.compile(notAcceptedPatterns[i], Pattern.CASE_INSENSITIVE);
            }
        }
    }

    public boolean isConsistent(File input) {
        boolean retFlag = true;
        if (input == null || input.isFile() ||
                minimalFilePatternList == null || minimalFilePatternList.length == 0) {
            retFlag = false;
        } else {
            List<String> fileNames = new ArrayList<String>();
            listFiles(input, fileNames);
            retFlag &= isConsistent((String[])fileNames.toArray());
        }
        return retFlag;
    }

    public boolean isConsistent(String[] fileNames) {
        boolean retFlag = true;
        if (minimalFilePatternList == null || minimalFilePatternList.length == 0) {
            retFlag = false;
        } else {
            if (notAcceptedFilePatternList != null) {
                for (Pattern pattern : notAcceptedFilePatternList) {
                    for (String fileName : fileNames) {
                        if (pattern.matcher(fileName.toLowerCase()).matches());
                            return false;
                    }
                }
            }
            if (minimalFilePatternList != null) {
                for (Pattern pattern : minimalFilePatternList) {
                    boolean localMatch = false;
                    for (String fileName : fileNames) {
                        localMatch = pattern.matcher(fileName.toLowerCase()).matches();
                        if (localMatch)
                            break;
                    }
                    retFlag &= localMatch;
                    if (!retFlag)
                        break;
                }
            }
        }
        return retFlag;
    }

    private void listFiles(File parent, List<String> outList) {
        if (parent.isFile())
            return;
        File[] files = parent.listFiles();
        for (File file : files) {
            if (file.isFile())
                outList.add(file.getName().toLowerCase());
            else {
                listFiles(file, outList);
            }
        }
    }

}
