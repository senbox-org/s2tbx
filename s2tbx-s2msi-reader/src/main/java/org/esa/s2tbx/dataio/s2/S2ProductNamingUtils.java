package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.dataio.s2.ortho.S2CRSHelper;
import org.esa.snap.lib.openjpeg.dataio.Utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static String[] EXCLUDED_XML = {"INSPIRE", "L2A_Manifest", "manifest"};

    //Pattern used to find the tile identifier in a string
    private static String SIMPLIFIED_TILE_ID_REGEX = "(.*)(T[0-9]{2}[A-Z]{3})(.*)";

    protected static final Logger logger = Logger.getLogger(S2ProductNamingUtils.class.getName());
    /**
     * Checks whether the structure of folders is right:
     * checks if there is a not empty datastrip folder
     * checks if there is a not empty granule folder
     * checks that subfolders contain a xml file
     *
     * @param xmlPath path to product metadata
     * @return
     */
    public static boolean checkStructureFromProductXml(VirtualPath xmlPath) throws IOException {
        if (!xmlPath.resolveSibling("DATASTRIP").exists()) {
            return false;
        }

        if (!xmlPath.resolveSibling("GRANULE").exists()) {
            return false;
        }
        ArrayList<VirtualPath> datastripPaths = getDatastripsFromProductXml(xmlPath);
        if (datastripPaths.isEmpty()) {
            logger.warning("The datastrip folder is empty (ignore this warning for the L2H and L2F level products).");
        }

        ArrayList<VirtualPath> tileDirs = getTilesFromProductXml(xmlPath);
        if (tileDirs.isEmpty()) {
            return false;
        }

        for (VirtualPath datastripPath : datastripPaths) {
            if (getXmlFromDir(datastripPath) == null) {
                logger.warning("The datastrip xml files don't exist (ignore this warning for the L2H and L2F level products).");
            }
        }

        for (VirtualPath tileDir : tileDirs) {
            if (getXmlFromDir(tileDir) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the structure of a granule is right:
     * checks if it contains "IMG_DATA"
     * checks if it contains "QI_DATA"
     *
     * @param xmlPath path to granule metadata
     * @return
     */
    public static boolean checkStructureFromGranuleXml(VirtualPath xmlPath) {
        return checkStructureFromGranuleXml(xmlPath,true);
    }

    /**
     * Checks whether the structure of a granule is right:
     * checks if it contains "IMG_DATA"
     * checks if it contains "QI_DATA"
     *
     * @param xmlPath path to granule metadata
     * @param requiredAuxData to disable the AuxData checking folder (L1B product, there are no AuxData in the Granule folder)
     * @return
     */
    public static boolean checkStructureFromGranuleXml(VirtualPath xmlPath, boolean hasAuxDataFolder) {
        if (!xmlPath.resolveSibling("IMG_DATA").exists()) {
            return false;
        }

        if (!xmlPath.resolveSibling("QI_DATA").exists()) {
            return false;
        }

        if(hasAuxDataFolder)
            if (!xmlPath.resolveSibling("AUX_DATA").exists()) {
                return false;
            }

        return true;
    }

    /**
     * Get the paths of the folders contained in the GRANULE folder
     *
     * @param xmlPath path to product metadata
     * @return
     */
    public static ArrayList<VirtualPath> getTilesFromProductXml(VirtualPath xmlPath) {
        ArrayList<VirtualPath> tilePaths = new ArrayList<>();
        VirtualPath granuleFolder = xmlPath.resolveSibling("GRANULE");
        try {
            VirtualPath[] granulePaths = granuleFolder.listPaths();
            if (granulePaths == null) {
                return tilePaths;
            }
            for (VirtualPath granule : granulePaths) {
                if (granule.existsAndHasChildren()) {
                    tilePaths.add(granuleFolder.resolve(granule.getFileName().toString()));
                }
            }
        } catch (Exception e) {
        }
        return tilePaths;
    }

    /**
     * Get the paths of the folders contained in the DATASTRIP folder
     *
     * @param xmlPath path to product metadata
     * @return
     */
    public static ArrayList<VirtualPath> getDatastripsFromProductXml(VirtualPath xmlPath) throws IOException {
        ArrayList<VirtualPath> datastripPaths = new ArrayList<>();
        VirtualPath datastripFolder = xmlPath.resolveSibling("DATASTRIP");
        VirtualPath[] datastripFiles = datastripFolder.listPaths();
        if(datastripFiles!=null){
            for (VirtualPath datastrip : datastripFiles) {
                if (datastrip.existsAndHasChildren()) {
                    datastripPaths.add(datastripFolder.resolve(datastrip.getFileName().toString()));
                }
            }
        }
        return datastripPaths;
    }

    /**
     * Get the path to the xml in dirPath. Only xml files different to EXCLUDED_XML are considered.
     * No REGEX is calculated.
     *
     * @param dirPath path to folder
     * @return path to xml or null if two or more files different to EXCLUDED_XML are found
     */
    public static VirtualPath getXmlFromDir(VirtualPath dirPath) {
        //TODO try to replace by functions in NamingConventions and apply REGEX
        if (!dirPath.existsAndHasChildren()) {
            return null;
        }
        String[] listXmlFiles;
        try {
            listXmlFiles = dirPath.listEndingBy(".xml");
        } catch (IOException e) {
            return null;
        }
        String xmlFile = "";
        int availableXmlCount = 0;

        for (String xml : listXmlFiles) {
            boolean bExcluded = false;
            for (String excluded : EXCLUDED_XML) {
                if (xml.substring(0, xml.lastIndexOf(".xml")).equals(excluded)) {
                    bExcluded = true;
                    break;
                }
            }
            if (!bExcluded) {
                xmlFile = xml;
                availableXmlCount++;
            }
        }
        if (availableXmlCount != 1) {
            return null;
        }

        return dirPath.resolve(xmlFile);
    }

    /**
     * Search the pattern "T[0-9]{2}[A-Z]{3}" in string
     *
     * @param string
     * @return a string with REGEX "T[0-9]{2}[A-Z]{3}", or null if not found
     */
    public static String getTileIdFromString(String string) {
        Pattern pattern = Pattern.compile(SIMPLIFIED_TILE_ID_REGEX);
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(2);

    }

    /**
     * Obtains the level from xmlPath
     *
     * @param xmlPath
     * @param inputType
     * @return
     */
    public static S2Config.Sentinel2ProductLevel getLevel(VirtualPath xmlPath, S2Config.Sentinel2InputType inputType) {
        if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA)) {
            return getLevelFromGranuleXml(xmlPath);
        } else if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)) {
            return getLevelFromProductXml(xmlPath);
        }
        return S2Config.Sentinel2ProductLevel.UNKNOWN;
    }

    /**
     * Obtains the level from xmlPath or from its parent
     *
     * @param xmlPath
     * @return
     */
    private static S2Config.Sentinel2ProductLevel getLevelFromGranuleXml(VirtualPath xmlPath) {
        String filename = xmlPath.getFileName().toString();
        S2Config.Sentinel2ProductLevel level = getLevelFromString(filename);
        if (level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        VirtualPath parentPath = xmlPath.getParent();
        if (parentPath == null) {
            return level;
        }

        filename = parentPath.getFileName().toString();
        level = getLevelFromString(filename);
        return level;
    }

    /**
     * Obtains the level from xmlPath, from its parent, or from the tiles
     *
     * @param xmlPath
     * @return
     */
    private static S2Config.Sentinel2ProductLevel getLevelFromProductXml(VirtualPath xmlPath) {
        String filename = xmlPath.getFileName().toString();
        S2Config.Sentinel2ProductLevel level = getLevelFromString(filename);
        if (level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        VirtualPath parentPath = xmlPath.getParent();
        if (parentPath == null) {
            return level;
        }

        filename = parentPath.getFileName().toString();
        level = getLevelFromString(filename);
        if (level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
            return level;
        }

        for (VirtualPath tile : getTilesFromProductXml(xmlPath)) {
            VirtualPath xmlGranule = getXmlFromDir(tile);
            if (xmlGranule == null) {
                return level;
            }
            level = getLevelFromGranuleXml(xmlGranule);
            if (level != S2Config.Sentinel2ProductLevel.UNKNOWN) {
                return level;
            }
        }

        return level;
    }

    /**
     * Searches L1C, L2A, L03 or L1C in string
     *
     * @param string
     * @return the corresponding Sentinel2ProductLevel or UNKNOWN if it is not found
     */
    private static S2Config.Sentinel2ProductLevel getLevelFromString(String string) {
        if (string.contains("L1C")) {
            return S2Config.Sentinel2ProductLevel.L1C;
        }
        if (string.contains("L2A")) {
            return S2Config.Sentinel2ProductLevel.L2A;
        }
        if (string.contains("L2H")) {
            return S2Config.Sentinel2ProductLevel.L2H;
        }
        if (string.contains("L2F")) {
            return S2Config.Sentinel2ProductLevel.L2F;
        }
        if (string.contains("L03")) {
            return S2Config.Sentinel2ProductLevel.L3;
        }
        if (string.contains("L1B")) {
            return S2Config.Sentinel2ProductLevel.L1B;
        }
        return S2Config.Sentinel2ProductLevel.UNKNOWN;
    }

    public static Set<String> getEpsgCodeList(VirtualPath xmlPath, S2Config.Sentinel2InputType inputType) {
        Set<String> epsgCodeList = new HashSet<>();
        if (inputType == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA) {
            String epsg = getEpsgCodeFromGranule(xmlPath);
            if (epsg != null) {
                epsgCodeList.add(epsg);
            }
        } else if (inputType == S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA) {
            for (VirtualPath tile : getTilesFromProductXml(xmlPath)) {
                VirtualPath xmlGranule = getXmlFromDir(tile);
                if (xmlGranule == null) {
                    continue;
                }
                String epsg = getEpsgCodeFromGranule(xmlGranule);
                if (epsg != null) {
                    epsgCodeList.add(epsg);
                }
            }
        }
        return epsgCodeList;
    }


    protected static String getEpsgCodeFromGranule(VirtualPath xmlPath) {
        String epsgCode = null;

        String tileId = getTileIdFromString(xmlPath.getFileName().toString());
        if (tileId == null && xmlPath.getParent() != null) {
            tileId = getTileIdFromString(xmlPath.getParent().getFileName().toString());
        }

        if (tileId != null) {
            epsgCode = S2CRSHelper.tileIdentifierToEPSG(tileId);
        }
        return epsgCode;
    }

    /**
     * Search the tileId from granuleFolder (TXXABC) and return the granuleId in
     * availableGranuleIds which contains the same. If it is not found returns null
     *
     * @param availableGranuleIds
     * @param granuleFolder
     * @return
     */
    public static String searchGranuleId(Collection<String> availableGranuleIds, String granuleFolder) {
        String tileId = getTileIdFromString(granuleFolder);
        if (tileId == null) {
            return null;
        }
        for (String tileName : availableGranuleIds) {
            String auxTileId = getTileIdFromString(tileName);
            if (auxTileId == null) {
                continue;
            }
            if (auxTileId.equals(tileId)) {
                return tileName;
            }
        }
        return null;
    }

    public static boolean hasValidStructure(S2Config.Sentinel2InputType inputType, VirtualPath xmlPath) throws IOException {
       return hasValidStructure(inputType, xmlPath, true);
    }

    public static boolean hasValidStructure(S2Config.Sentinel2InputType inputType, VirtualPath xmlPath, boolean hasAuxDataFolder) throws IOException {
        if (inputType == S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA) {
            return S2ProductNamingUtils.checkStructureFromProductXml(xmlPath);
        }
        if (inputType == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA) {
            return S2ProductNamingUtils.checkStructureFromGranuleXml(xmlPath, hasAuxDataFolder);
        }
        return false;
    }

    public static Path processInputPath(Path inputPath) {
        if (inputPath.getFileSystem() == FileSystems.getDefault()) {
            // the local file system
            if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
                String longInput = Utils.GetLongPathNameW(inputPath.toString());
                if (longInput.length() > 0) {
                    return Paths.get(longInput);
                }
            }
        }
        return inputPath;
    }
}
