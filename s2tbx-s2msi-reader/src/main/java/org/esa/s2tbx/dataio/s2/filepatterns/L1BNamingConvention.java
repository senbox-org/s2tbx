package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2ProductNamingUtils;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.core.util.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 08/11/2016.
 */
public class L1BNamingConvention implements INamingConvention {
    //REGEX and getters
    public static String PRODUCT_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{3})L1B_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_R([0-9]{3}).*";
    public static String PRODUCT_COMPACT_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9|_]{3})L1B_([0-9]{8}T[0-9]{6})_N([0-9]{4})_R([0-9]{3})_.*";
    public static String PRODUCT_XML_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{3})L1B_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_R([0-9]{3}).*\\.xml";
    public static String GRANULE_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})L1B([A-Z|0-9|_]{3})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_.*";
    public static String GRANULE_XML_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})L1B([A-Z|0-9|_]{3})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_.*\\.xml";
    public static String DATASTRIP_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})L1B([A-Z|0-9|_]{3})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_S([0-9]{8}T[0-9]{6})_N([0-9]{2})\\.([0-9]{2})";
    public static String DATASTRIP_XML_REGEX = "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})_S([0-9]{8}T[0-9]{6})\\.xml";
    @Override
    public String[] getProductREGEXs() {
        String[] REGEXs = {PRODUCT_REGEX,PRODUCT_COMPACT_REGEX};
        return REGEXs;
    }

    @Override
    public String[] getProductXmlREGEXs() {
        String[] REGEXs = {PRODUCT_XML_REGEX};
        return REGEXs;
    }

    @Override
    public String[] getGranuleREGEXs() {
        String[] REGEXs = {GRANULE_REGEX};
        return REGEXs;
    }

    @Override
    public String[] getGranuleXmlREGEXs() {
        String[] REGEXs = {GRANULE_XML_REGEX};
        return REGEXs;
    }

    @Override
    public String[] getDatastripREGEXs() {
        String[] REGEXs = {DATASTRIP_REGEX};
        return REGEXs;
    }

    @Override
    public String[] getDatastripXmlREGEXs() {
        String[] REGEXs = {DATASTRIP_XML_REGEX};
        return REGEXs;
    }

    //Image templates
    public static String SPECTRAL_BAND_TEMPLATE_L1B = "IMG_DATA" + File.separator + "{{MISSION_ID}}_OPER_MSI_L1B_GR_{{SITECENTRE}}_{{CREATIONDATE}}_S{{STARTDATE}}_{{DETECTOR}}_{{BANDFILEID}}.jp2";


    private S2Config.Sentinel2InputType inputType = null;
    private S2Config.Sentinel2ProductLevel level = S2Config.Sentinel2ProductLevel.UNKNOWN;
    Path inputDirPath = null;
    Path inputXmlPath = null;
    Path inputProductXml = null; //store the product xml if exists when the input is a granule
    private S2SpatialResolution resolution = S2SpatialResolution.R10M;


    public L1BNamingConvention(Path input){
        String inputName = input.getFileName().toString();
        level = S2Config.Sentinel2ProductLevel.L1B;

        if(Files.isDirectory(input)) {
            inputDirPath = input;
            Pattern pattern = Pattern.compile(PRODUCT_REGEX);
            Pattern patternCompact = Pattern.compile(PRODUCT_COMPACT_REGEX);
            if (pattern.matcher(inputName).matches()||patternCompact.matcher(inputName).matches()) {
                inputXmlPath = getXmlProductFromDir(input);
                inputProductXml = inputXmlPath;
                inputType = S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA;
            }
            if(inputXmlPath == null) {
                pattern = Pattern.compile(GRANULE_REGEX);
                if (pattern.matcher(inputName).matches()) {
                    inputXmlPath = getXmlGranuleFromDir(input);
                    inputProductXml = S2NamingConventionUtils.getProductXmlFromGranuleXml(inputXmlPath,getProductXmlREGEXs());
                    inputType = S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA;
                }
            }
            if(inputXmlPath == null) {
                inputType = null;
                return;
            }
        } else {
            Pattern pattern = Pattern.compile(PRODUCT_XML_REGEX);
            if (pattern.matcher(inputName).matches()) {
                inputXmlPath = input;
                inputType = S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA;
                inputProductXml = inputXmlPath;
            }
            if(inputXmlPath == null) {
                pattern = Pattern.compile(GRANULE_XML_REGEX);
                if (pattern.matcher(inputName).matches()) {
                    inputXmlPath = input;
                    inputProductXml = S2NamingConventionUtils.getProductXmlFromGranuleXml(inputXmlPath,getProductXmlREGEXs());
                    inputType = S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA;
                }
            }
            if(inputXmlPath == null) {
                inputType = null;
                return;
            }
        }
    }

    @Override
    public boolean matches(String filename) {
        return S2NamingConventionUtils.matches(filename,this);
    }

    @Override
    public boolean hasValidStructure() {
        return S2ProductNamingUtils.hasValidStructure(inputType, getInputXml());
    }

    @Override
    public Path getXmlFromDir(Path path) {
        return S2NamingConventionUtils.getXmlFromDir(path, PRODUCT_XML_REGEX, GRANULE_XML_REGEX);
    }

    @Override
    public S2Config.Sentinel2InputType getInputType() {
        return inputType;
    }

    @Override
    public S2Config.Sentinel2ProductLevel getProductLevel() {
        return level;
    }

    @Override
    public Set<String> getEPSGList() {
        return null;
    }

    @Override
    public Path getInputXml() {
        return inputXmlPath;
    }

    @Override
    public Path getInputProductXml() {
        return inputProductXml;
    }

    @Override
    public S2SpatialResolution getResolution() {
        return resolution;
    }

    @Override
    public String getProductName() {
        if(getInputXml().getParent() != null) {
            return FileUtils.getFilenameWithoutExtension(getInputXml().getParent().getFileName().toString());
        }
        return FileUtils.getFilenameWithoutExtension(getInputXml().getFileName().toString());
    }

    @Override
    public boolean matchesProductMetadata(String filename) {
        return S2NamingConventionUtils.matches(filename, PRODUCT_XML_REGEX);
    }

    private Path getXmlProductFromDir(Path path) {
        return S2NamingConventionUtils.getFileFromDir(path, getProductXmlREGEXs());
    }

    @Override
    public ArrayList<Path> getDatastripXmlPaths() {
        return S2NamingConventionUtils.getDatastripXmlPaths(inputType, getInputXml(), getDatastripREGEXs(), getDatastripXmlREGEXs());
    }

    @Override
    public ArrayList<Path> getGranulesXmlPaths() {
        return S2NamingConventionUtils.getGranulesXmlPaths(inputType, getInputXml(), getGranuleREGEXs(), getGranuleXmlREGEXs());
    }

    @Override
    public Path findGranuleFolderFromTileId(String tileId) {
        Path path = null;
        if(getInputType()== S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA) {
            path = inputXmlPath.resolveSibling("GRANULE").resolve(tileId);

        } else {
            if(inputXmlPath.getParent() == null) {
                return null;
            }
            path = inputXmlPath.getParent().resolveSibling(tileId);
        }
        if(Files.exists(path) && Files.isDirectory(path) && S2NamingConventionUtils.matches(path.getFileName().toString(),getGranuleREGEXs())) {
            return path;
        }
        return null;
    }

    @Override
    public Path findXmlFromTileId(String tileID) {
        Path granuleFolderPath = findGranuleFolderFromTileId(tileID);
        if(granuleFolderPath == null) {
            return null;
        }
        Path path = S2NamingConventionUtils.getFileFromDir(granuleFolderPath,getGranuleXmlREGEXs());
        if(path != null && Files.exists(path)) {
            return path;
        }
        return null;
    }

    @Override
    public String findGranuleId(Collection<String> availableGranuleIds, String granuleFolder) {
        if(availableGranuleIds.contains(granuleFolder)) {
            return granuleFolder;
        }
        return null;
    }

    private Path getXmlGranuleFromDir(Path path) {
        return S2NamingConventionUtils.getFileFromDir(path, getGranuleXmlREGEXs());
    }


}
