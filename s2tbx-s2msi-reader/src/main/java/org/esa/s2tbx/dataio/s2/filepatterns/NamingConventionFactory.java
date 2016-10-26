package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFE;
import org.esa.s2tbx.dataio.s2.l1c.L1cNamingConventionSAFECompactSingle;
import org.esa.s2tbx.dataio.s2.ortho.S2ProductCRSCacheEntry;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 19/10/2016.
 */
public class NamingConventionFactory {

    private HashMap<Path, Path> cacheXML = new HashMap<>();
    public static INamingConvention createNamingConvention(Path path) {

        INamingConvention namingConvention = null;
        String filename = path.getFileName().toString();

            if(L1cNamingConventionSAFE.productMatches(filename) || L1cNamingConventionSAFE.granuleMatches(filename)) {
                namingConvention = new L1cNamingConventionSAFE();
            } else if (L1cNamingConventionSAFECompactSingle.productMatches(filename) || L1cNamingConventionSAFECompactSingle.granuleMatches(filename)) {
                namingConvention = new L1cNamingConventionSAFECompactSingle();
            }
            //TODO

        return namingConvention;
    }

    public static ArrayList<S2FileNamingTemplate> getXmlProductsOrthoTemplate (){
        ArrayList<S2FileNamingTemplate> templates = new ArrayList<>();
        //templates.add(L1cNamingConventionSAFE.getStaticProductXmlTemplate());
        //templates.add(L1cNamingConventionSAFECompactSingle.getStaticProductXmlTemplate());
        return templates;
    }

    public static ArrayList<Pattern> getPatterns (S2Config.Sentinel2ProductLevel level) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        switch (level) {
            case L1B:
                break;

            case L1C:
                patterns.addAll(L1cNamingConventionSAFE.getInputPatterns());
                patterns.addAll(L1cNamingConventionSAFECompactSingle.getInputPatterns());
                break;

            case L2A:

                break;
            case L3:

                break;

            default:

                break;
        }
        return patterns;

    }




    public static boolean isValidXmlProduct(Path path) {
        if (L1cNamingConventionSAFE.productMatches(path.getFileName().toString())) {
            return true;
        }
        if (L1cNamingConventionSAFECompactSingle.productMatches(path.getFileName().toString())) {
            return true;
        }
        return false;
    }

    public static boolean isValidXmlGranule(Path path) {
        if (L1cNamingConventionSAFE.granuleMatches(path.getFileName().toString())) {
            return true;
        }
        if (L1cNamingConventionSAFECompactSingle.granuleMatches(path.getFileName().toString())) {
            return true;
        }
        return false;
    }


    public static Path getXmlFromProductDir(Path productDir) {
        if (!Files.isDirectory(productDir)) {
            return null;
        }
        String fileName = "";

        if (L1cNamingConventionSAFE.productDirMatches(productDir.getFileName().toString())) {
            String[] listXmlFiles = productDir.toFile().list((f, s) -> s.endsWith(".xml"));
            int countValidXml = 0;
            for (int i = 0; i < listXmlFiles.length; i++) {
                if (L1cNamingConventionSAFE.productMatches(listXmlFiles[i])) {
                    countValidXml++;
                    fileName = listXmlFiles[i];
                }
            }
            // If there are more than one valid file, it is considered an invalid input
            if (countValidXml != 1) {
                return null;
            }
            return productDir.resolve(fileName);
        }

        if (L1cNamingConventionSAFECompactSingle.productDirMatches(productDir.getFileName().toString())) {
            String[] listXmlFiles = productDir.toFile().list((f, s) -> s.endsWith(".xml"));
            int countValidXml = 0;
            for (int i = 0; i < listXmlFiles.length; i++) {
                if (L1cNamingConventionSAFECompactSingle.productMatches(listXmlFiles[i])) {
                    countValidXml++;
                    fileName = listXmlFiles[i];
                }
            }
            // If there are more than one valid file, it is considered an invalid input
            if (countValidXml != 1) {
                return null;
            }
            return productDir.resolve(fileName);
        }

        return null;
    }

    public static Path getXmlFromGranuleDir(Path granuleDir) {
        if (!Files.isDirectory(granuleDir)) {
            return null;
        }
        String fileName = "";

        if (L1cNamingConventionSAFE.granuleDirMatches(granuleDir.getFileName().toString())) {
            String[] listXmlFiles = granuleDir.toFile().list((f, s) -> s.endsWith(".xml"));
            int countValidXml = 0;
            for (int i = 0; i < listXmlFiles.length; i++) {
                if (L1cNamingConventionSAFE.granuleMatches(listXmlFiles[i])) {
                    countValidXml++;
                    fileName = listXmlFiles[i];
                }
            }
            // If there are more than one valid file, it is considered an invalid input
            if (countValidXml != 1) {
                return null;
            }
            return granuleDir.resolve(fileName);
        }

        if (L1cNamingConventionSAFECompactSingle.granuleDirMatches(granuleDir.getFileName().toString())) {
            String[] listXmlFiles = granuleDir.toFile().list((f, s) -> s.endsWith(".xml"));
            int countValidXml = 0;
            for (int i = 0; i < listXmlFiles.length; i++) {
                if (L1cNamingConventionSAFECompactSingle.granuleMatches(listXmlFiles[i])) {
                    countValidXml++;
                    fileName = listXmlFiles[i];
                }
            }
            // If there are more than one valid file, it is considered an invalid input
            if (countValidXml != 1) {
                return null;
            }
            return granuleDir.resolve(fileName);
        }

        return null;
    }


    //TODO posibles NamingConventions en una lista e ir recorriendola?
    public static S2ProductCRSCacheEntry createCacheEntry(Path xmlPath) {
        INamingConvention namingConvention;
        S2ProductCRSCacheEntry cacheEntry = null;

        namingConvention = new L1cNamingConventionSAFE();
        cacheEntry = namingConvention.createCacheEntry(xmlPath);
        if(cacheEntry != null) {
            return cacheEntry;
        }

        namingConvention = new L1cNamingConventionSAFECompactSingle();
        cacheEntry = namingConvention.createCacheEntry(xmlPath);
        if(cacheEntry != null) {
            return cacheEntry;
        }

        return cacheEntry;
    }
}
