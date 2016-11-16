package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.math.Array;
import org.esa.snap.utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obarrilero on 26/10/2016.
 *
 * Common utils to validate the product structure and to obtain some basic information
 * by using only the product structure and filenames. This class do not have into account any REGEX
 * in xml, granules, datastrip...
 * When possible, it is recommended to use the the namingConvention utils because they
 * use the different REGEXs to check the filenames and implement optimized methods depending on
 * the product format.
 */
public class S2ProductNamingUtils {

    // list of files to be ignored when searching xml files into a folder
    private static String[] EXCLUDED_XML = {"INSPIRE","L2A_Manifest", "manifest"};

    //Pattern used to find the tile identifier in a string
    private static String SIMPLIFIED_TILE_ID_REGEX = "(.*)(T[0-9]{2}[A-Z]{3})(.*)";

    /**
     * Checks whether the structure of folders is right:
     * checks if there is a not empty datastrip folder
     * checks if there is a not empty granule folder
     * checks that subfolders contain a xml file
     * @param xmlPath path to product metadata
     * @return
     */
    public static boolean checkStructureFromProductXml(Path xmlPath) {
        if(!Files.exists(xmlPath.resolveSibling("DATASTRIP"))) {
            return false;
        }

        if(!Files.exists(xmlPath.resolveSibling("GRANULE"))) {
            return false;
        }

        ArrayList<Path> datastripDirs = getDatastripsFromProductXml(xmlPath);
        if(datastripDirs.isEmpty()) {
            return false;
        }

        ArrayList<Path> tileDirs = getTilesFromProductXml(xmlPath);
        if(tileDirs.isEmpty()) {
            return false;
        }

        for(Path datastripDir : datastripDirs) {
            if(getXmlFromDir(datastripDir) == null) {
                return false;
            }
        }

        for(Path tileDir : tileDirs) {
            if(getXmlFromDir(tileDir) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the structure of a granule is right:
     * checks if it contains "IMG_DATA"
     * checks if it contains "QI_DATA"
     * @param xmlPath path to granule metadata
     * @return
     */
    public static boolean checkStructureFromGranuleXml(Path xmlPath) {
        if(!Files.exists(xmlPath.resolveSibling("IMG_DATA"))) {
            return false;
        }

        if(!Files.exists(xmlPath.resolveSibling("QI_DATA"))) {
            return false;
        }

        return true;
    }

    /**
     * Get the paths of the folders contained in the GRANULE folder
     * @param xmlPath path to product metadata
     * @return
     */
    public static ArrayList<Path> getTilesFromProductXml(Path xmlPath) {
        ArrayList<Path> tilePaths = new ArrayList<>();
        Path granuleFolder = xmlPath.resolveSibling("GRANULE");
        try {
            File[] granuleFiles = granuleFolder.toFile().listFiles();
            if(granuleFiles == null) {
                return tilePaths;
            }
            for(File granule : granuleFiles) {
                if (granule.isDirectory()){
                    tilePaths.add(granuleFolder.resolve(granule.getName()));
                }
            }
        } catch (Exception e) {
        }
        return tilePaths;
    }

    /**
     * Get the paths of the folders contained in the DATASTRIP folder
     * @param xmlPath path to product metadata
     * @return
     */
    public static ArrayList<Path> getDatastripsFromProductXml(Path xmlPath) {
        ArrayList<Path> datastripPaths = new ArrayList<>();
        Path datastripFolder = xmlPath.resolveSibling("DATASTRIP");
        try {
            File[] datastripFiles = datastripFolder.toFile().listFiles();
            for(File granule : datastripFiles) {
                if (granule.isDirectory()){
                    datastripPaths.add(datastripFolder.resolve(granule.getName()));
                }
            }
        } catch (Exception e) {
        }
        return datastripPaths;
    }


    /**
     * Get the path to the xml in dirPath. Only xml files different to EXCLUDED_XML are considered.
     * No REGEX is calculated.
     * @param dirPath path to folder
     * @return path to xml or null if two or more files different to EXCLUDED_XML are found
     */
    public static Path getXmlFromDir(Path dirPath) {
        //TODO try to replace by functions in NamingConventions and apply REGEX
        if(!Files.isDirectory(dirPath)) {
            return null;
        }
        String[] listXmlFiles = dirPath.toFile().list((f, s) -> s.endsWith(".xml"));
        String xmlFile = "";
        int availableXmlCount = 0;

        for(String xml : listXmlFiles) {
            boolean bExcluded = false;
            for(String excluded : EXCLUDED_XML) {
                if (xml.substring(0, xml.lastIndexOf(".xml")).equals(excluded)) {
                    bExcluded = true;
                    break;
                }
            }
            if(!bExcluded) {
                xmlFile = xml;
                availableXmlCount++;
            }
        }
        if(availableXmlCount != 1) {
            return null;
        }

        return dirPath.resolve(xmlFile);
    }


    /**
     * Search the pattern "T[0-9]{2}[A-Z]{3}" in string
     * @param string
     * @return a string with REGEX "T[0-9]{2}[A-Z]{3}", or null if not found
     */
    public static String getTileIdFromString(String string) {
        Pattern pattern = Pattern.compile(SIMPLIFIED_TILE_ID_REGEX);
        Matcher matcher = pattern.matcher(string);
        if(!matcher.matches()) {
            return null;
        }
        return matcher.group(2);

    }

    /**
     * Obtains the level from xmlPath
     * @param xmlPath
     * @param inputType
     * @return
     */
    public static S2Config.Sentinel2ProductLevel getLevel(Path xmlPath, S2Config.Sentinel2InputType inputType) {
        if(inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA)) {
            return getLevelFromGranuleXml(xmlPath);
        } else if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)) {
            return getLevelFromProductXml(xmlPath);
        }
        return S2Config.Sentinel2ProductLevel.UNKNOWN;
    }

    /**
     * Obtains the level from xmlPath or from its parent
     * @param xmlPath
     * @return
     */
    private static S2Config.Sentinel2ProductLevel getLevelFromGranuleXml(Path xmlPath) {
        String filename = xmlPath.getFileName().toString();
        S2Config.Sentinel2ProductLevel level = getLevelFromString(filename);
        if(level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        Path parentPath = xmlPath.getParent();
        if(parentPath == null) {
            return level;
        }

        filename = parentPath.getFileName().toString();
        level = getLevelFromString(filename);
        return level;
    }

    /**
     * Obtains the level from xmlPath, from its parent, or from the tiles
     * @param xmlPath
     * @return
     */
    private static S2Config.Sentinel2ProductLevel getLevelFromProductXml(Path xmlPath) {
        String filename = xmlPath.getFileName().toString();
        S2Config.Sentinel2ProductLevel level = getLevelFromString(filename);
        if(level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        Path parentPath = xmlPath.getParent();
        if(parentPath == null) {
            return level;
        }

        filename = parentPath.getFileName().toString();
        level = getLevelFromString(filename);
        if(level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        for(Path tile : getTilesFromProductXml(xmlPath)) {
            Path xmlGranule = getXmlFromDir(tile);
            if(xmlGranule == null) {
                return level;
            }
            level = getLevelFromGranuleXml(xmlGranule);
            if(level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
                return level;
            }
        }

        return level;
    }

    /**
     * Searches L1C, L2A, L03 or L1C in string
     * @param string
     * @return the corresponding Sentinel2ProductLevel or UNKNOWN if it is not found
     */
    private static  S2Config.Sentinel2ProductLevel getLevelFromString(String string) {
        if(string.contains("L1C")) {
            return S2Config.Sentinel2ProductLevel.L1C;
        }
        if(string.contains("L2A")) {
            return S2Config.Sentinel2ProductLevel.L2A;
        }
        if(string.contains("L03")) {
            return S2Config.Sentinel2ProductLevel.L3;
        }
        if(string.contains("L1B")) {
            return S2Config.Sentinel2ProductLevel.L1B;
        }
        return S2Config.Sentinel2ProductLevel.UNKNOWN;
    }

    public static Set<String> getEpsgCodeList(Path xmlPath, S2Config.Sentinel2InputType inputType) {
        Set<String> epsgCodeList = new HashSet<>();

        if(inputType == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA) {
            String epsg = getEpsgCodeFromGranule(xmlPath);
            if(epsg != null) {
                epsgCodeList.add(epsg);
            }
        } else if(inputType == S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA) {
            for(Path tile : getTilesFromProductXml(xmlPath)) {
                Path xmlGranule = getXmlFromDir(tile);
                if(xmlGranule == null) {
                    continue;
                }
                String epsg = getEpsgCodeFromGranule(xmlGranule);
                if(epsg != null) {
                    epsgCodeList.add(epsg);
                }
            }
        }
        return epsgCodeList;
    }


    private static String getEpsgCodeFromGranule(Path xmlPath) {
        String epsgCode = null;

        String tileId = getTileIdFromString(xmlPath.getFileName().toString());
        if(tileId == null && xmlPath.getParent() != null) {
            tileId = getTileIdFromString(xmlPath.getParent().getFileName().toString());
        }

        if(tileId != null) {
            epsgCode = S2CRSHelper.tileIdentifierToEPSG(tileId);
        }
        return epsgCode;
    }

    /**
     * Search the tileId from granuleFolder (TXXABC) and return the granuleId in
     * availableGranuleIds which contains the same. If it is not found returns null
     * @param availableGranuleIds
     * @param granuleFolder
     * @return
     */
    public static String searchGranuleId(Collection<String> availableGranuleIds, String granuleFolder) {
        String tileId = getTileIdFromString(granuleFolder);
        if(tileId == null) {
            return null;
        }
        for(String tileName : availableGranuleIds) {
            String auxTileId = getTileIdFromString(tileName);
            if(auxTileId == null) {
                continue;
            }
            if(auxTileId.equals(tileId)) {
                return tileName;
            }
        }
        return null;
    }

    public static boolean hasValidStructure(S2Config.Sentinel2InputType inputType, Path xmlPath) {
        if(inputType == S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA) {
            return S2ProductNamingUtils.checkStructureFromProductXml(xmlPath);
        }
        if(inputType == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA) {
            return S2ProductNamingUtils.checkStructureFromGranuleXml(xmlPath);
        }
        return false;
    }
}
