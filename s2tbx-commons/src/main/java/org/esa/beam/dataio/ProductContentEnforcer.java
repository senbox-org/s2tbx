package org.esa.beam.dataio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kraftek on 9/16/2014.
 */
public class ProductContentEnforcer {

    private String[] minimalFilePatternList;
    private String[] notAcceptedFilePatternList;

    public static ProductContentEnforcer create(String[] minimalPatterns) {
        return new ProductContentEnforcer(minimalPatterns, null);
    }

    public static ProductContentEnforcer create(String[] minimalPatterns, String[] notAcceptedPatterns) {
        return new ProductContentEnforcer(minimalPatterns, notAcceptedPatterns);
    }

    private ProductContentEnforcer(String[] requiredPatterns, String[] notAcceptedPatterns) {
        this.minimalFilePatternList = requiredPatterns;
        this.notAcceptedFilePatternList = notAcceptedPatterns;
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
            if (notAcceptedFilePatternList != null && notAcceptedFilePatternList.length > 0) {
                for (String pattern : notAcceptedFilePatternList) {
                    for (String fileName : fileNames) {
                        if (fileName.toLowerCase().matches(pattern))
                            return false;
                    }
                }
            }
            for (String pattern : minimalFilePatternList) {
                boolean localMatch = false;
                for (String fileName : fileNames) {
                    localMatch = fileName.toLowerCase().matches(pattern);
                    if (localMatch)
                        break;
                }
                retFlag &= localMatch;
                if (!retFlag)
                    break;
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
