package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.s2.S2Config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by obarrile on 07/11/2016.
 */
public class S2NamingConventionUtils {

    /**
     * Checks if a string match with any of the REGEX of namingConvention.
     * Try productREGEX, productXmlREGEX, GranuleREGEX and granuleXmlREGEX
     * @param filename
     * @param namingConvention
     * @return
     */
    public static boolean matches(String filename, INamingConvention namingConvention) {

        for(String REGEX : namingConvention.getProductREGEXs()) {
            if (matches(filename,REGEX)) {
                return true;
            }
        }
        for(String REGEX : namingConvention.getProductXmlREGEXs()) {
            if (matches(filename,REGEX)) {
                return true;
            }
        }

        for(String REGEX : namingConvention.getGranuleREGEXs()) {
            if (matches(filename,REGEX)) {
                return true;
            }
        }

        for(String REGEX : namingConvention.getGranuleXmlREGEXs()) {
            if (matches(filename,REGEX)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matches(String filename, String REGEX) {
        Pattern pattern = Pattern.compile(REGEX);
        if (pattern.matcher(filename).matches()) {
            return true;
        }
        return false;
    }

    public static boolean matches(String filename, String[] REGEXs) {
        for(String REGEX : REGEXs) {
            Pattern pattern = Pattern.compile(REGEX);
            if (pattern.matcher(filename).matches()) {
                return true;
            }
        }
        return false;
    }

    public static Path getXmlFromDir(Path path, String PRODUCT_XML_REGEX, String GRANULE_XML_REGEX) {
        if(!Files.isDirectory(path)) {
            return null;
        }
        Pattern productPattern = Pattern.compile(PRODUCT_XML_REGEX);
        Pattern granulePattern = Pattern.compile(GRANULE_XML_REGEX);


        String[] listXmlFiles = path.toFile().list((f, s) -> s.endsWith(".xml"));
        if(listXmlFiles == null) {
            return null;
        }
        String xmlFile = "";
        int availableXmlCount = 0;

        for(String xml : listXmlFiles) {
            if (productPattern.matcher(xml).matches() || granulePattern.matcher(xml).matches()) {
                xmlFile = xml;
                availableXmlCount++;
            }
        }

        if(availableXmlCount != 1) {
            return null;
        }

        return path.resolve(xmlFile);
    }

    /**
     * Searches the file from path that matches some REGEX.
     * If more than one file is found, returns null
     * @param path
     * @param REGEXs
     * @return
     */
    public static Path getFileFromDir(Path path, String[] REGEXs) {
        if(path == null || !Files.isDirectory(path)) {
            return null;
        }
        Pattern[] patterns = new Pattern[REGEXs.length];
        for(int i = 0 ; i < REGEXs.length ; i++) {
            patterns[i] = Pattern.compile(REGEXs[i]);
        }

        String[] listXmlFiles = path.toFile().list((f, s) -> s.endsWith(".xml"));
        if(listXmlFiles == null) {
            return null;
        }

        String xmlFile = "";
        int availableXmlCount = 0;

        for(String xml : listXmlFiles) {
            for(Pattern pattern : patterns) {
                if (pattern.matcher(xml).matches() ) {
                    xmlFile = xml;
                    availableXmlCount++;
                    break;
                }
            }
        }

        if(availableXmlCount != 1) {
            return null;
        }

        return path.resolve(xmlFile);
    }

    public static ArrayList<Path> getAllFilesFromDir(Path path, String[] REGEXs) {
        ArrayList<Path> paths = new ArrayList<>();
        if(path == null || !Files.isDirectory(path)) {
            return paths;
        }
        if(REGEXs == null) {
            String[] listFiles = path.toFile().list();
            if(listFiles == null) {
                return paths;
            }
            for(String file : listFiles) {
                paths.add(path.resolve(file));
            }
            return paths;
        }

        Pattern[] patterns = new Pattern[REGEXs.length];
        for(int i = 0 ; i < REGEXs.length ; i++) {
            patterns[i] = Pattern.compile(REGEXs[i]);
        }

        String[] listFiles = path.toFile().list();
        if(listFiles == null) {
            return paths;
        }
        for(String file : listFiles) {
            for(Pattern pattern : patterns) {
                if (pattern.matcher(file).matches() ) {
                    paths.add(path.resolve(file));
                }
            }
        }

        return paths;
    }

    public static ArrayList<Path> getDatastripXmlPaths(S2Config.Sentinel2InputType inputType, Path inputXml, String[] datastripREGEXs, String[] datastripXmlREGEXs) {
        ArrayList<Path> paths = new ArrayList<>();
        if(inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)){
            Path datastripPath = inputXml.resolveSibling("DATASTRIP");
            if(datastripPath == null) {
                return paths;
            }
            if(!Files.isDirectory(datastripPath)) {
                return paths;
            }
            File[] datastripFiles = datastripPath.toFile().listFiles();
            if(datastripFiles == null) {
                return paths;
            }
            for(File datastrip : datastripFiles) {
                if(datastrip.isDirectory() && S2NamingConventionUtils.matches(datastrip.getName(),datastripREGEXs)){
                    Path xml = S2NamingConventionUtils.getFileFromDir(datastrip.toPath(), datastripXmlREGEXs);
                    if(xml != null) {
                        paths.add(xml);
                    }
                }
            }
        } else if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA)){
            Path parentPath = inputXml.getParent();
            if(parentPath == null) {
                return paths;
            }
            Path parentPath2 = parentPath.getParent();
            if(parentPath2 == null) {
                return paths;
            }
            return getDatastripXmlPaths(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA,parentPath2,datastripREGEXs,datastripXmlREGEXs);
        }
        return paths;
    }

    public static ArrayList<Path> getGranulesXmlPaths(S2Config.Sentinel2InputType inputType, Path inputXml, String[] granuleREGEXs, String[] granuleXmlREGEXs) {
        ArrayList<Path> paths = new ArrayList<>();
        if(inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)){
            Path granulePath = inputXml.resolveSibling("GRANULE");
            if(granulePath == null) {
                return paths;
            }
            if(!Files.isDirectory(granulePath)) {
                return paths;
            }
            File[] granules = granulePath.toFile().listFiles();
            if(granules == null) {
                return paths;
            }
            for(File granule : granules) {
                if(granule.isDirectory() && S2NamingConventionUtils.matches(granule.getName(),granuleREGEXs)){
                    Path xml = S2NamingConventionUtils.getFileFromDir(granule.toPath(), granuleXmlREGEXs);
                    if(xml != null) {
                        paths.add(xml);
                    }
                }
            }
        }
        return paths;
    }

    public static Path getProductXmlFromGranuleXml(Path granuleXmlPath, String[] REGEXs) {
        Path productXml = null;
        try {
            Objects.requireNonNull(granuleXmlPath.getParent());
            Objects.requireNonNull(granuleXmlPath.getParent().getParent());
            Objects.requireNonNull(granuleXmlPath.getParent().getParent().getParent());

            Path up2levels = granuleXmlPath.getParent().getParent().getParent();

            if (up2levels == null) {
                return productXml;
            }
            return getFileFromDir(up2levels, REGEXs);
        } catch (NullPointerException npe) {
            return null;
        }
    }

}
