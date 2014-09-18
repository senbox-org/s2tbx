package org.esa.beam.dataio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kraftek on 9/16/2014.
 */
public class ProductContentEnforcer {

    private String[] minimalFilePatternList;

    public static ProductContentEnforcer create(String[] minimalPatterns) {
        return new ProductContentEnforcer(minimalPatterns);
    }

    private ProductContentEnforcer(String[] patterns) {
        this.minimalFilePatternList = patterns;
    }

    public boolean isConsistent(File input) {
        boolean retFlag = true;
        if (input == null || input.isFile() ||
                minimalFilePatternList == null || minimalFilePatternList.length == 0) {
            retFlag = false;
        } else {
            List<String> fileNames = new ArrayList<String>();
            listFiles(input, fileNames);
            for (String pattern : minimalFilePatternList) {
                boolean localMatch = false;
                for (String fileName : fileNames) {
                    localMatch = fileName.toLowerCase().matches(pattern);
                    if (localMatch)
                        break;
                }
                retFlag &= localMatch;
            }
        }
        return retFlag;
    }

    public boolean isConsistent(String[] fileNames) {
        boolean retFlag = true;
        if (minimalFilePatternList == null || minimalFilePatternList.length == 0) {
            retFlag = false;
        } else {
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
        for(File file : files) {
            if(file.isFile())
                outList.add(file.getName().toLowerCase());
            else {
                listFiles(file, outList);
            }
        }
    }

}
