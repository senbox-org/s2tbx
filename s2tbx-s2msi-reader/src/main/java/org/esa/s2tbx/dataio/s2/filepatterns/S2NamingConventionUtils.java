package org.esa.s2tbx.dataio.s2.filepatterns;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.engine_utilities.util.PathUtils;
import org.esa.s2tbx.dataio.s2.S2Config;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
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

    public static VirtualPath getXmlFromDir(VirtualPath path, String PRODUCT_XML_REGEX, String GRANULE_XML_REGEX) {
        if (!path.existsAndHasChildren()) {
            return null;
        }
        Pattern productPattern = Pattern.compile(PRODUCT_XML_REGEX);
        Pattern granulePattern = Pattern.compile(GRANULE_XML_REGEX);


        String[] listXmlFiles;
        try {
            listXmlFiles = path.listEndingBy(".xml");
        } catch (IOException e) {
            return null;
        }

        if (listXmlFiles == null) {
            return null;
        }
        String xmlFile = "";
        int availableXmlCount = 0;

        for (String xml : listXmlFiles) {
            if (productPattern.matcher(xml).matches() || granulePattern.matcher(xml).matches()) {
                xmlFile = xml;
                availableXmlCount++;
            }
        }

        if (availableXmlCount != 1) {
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
    public static VirtualPath getFileFromDir(VirtualPath path, String[] REGEXs) {
        if (path == null || !path.existsAndHasChildren()) {
            return null;
        }
        Pattern[] patterns = new Pattern[REGEXs.length];
        for (int i = 0; i < REGEXs.length; i++) {
            patterns[i] = Pattern.compile(REGEXs[i]);
        }

        String[] listXmlFiles;
        try {
            listXmlFiles = path.listEndingBy(".xml");
        } catch (IOException e) {
            return null;
        }
        if (listXmlFiles == null) {
            return null;
        }

        ArrayList<String> xmlCandidates = new ArrayList<>(5);
        for (String xml : listXmlFiles) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(xml).matches()) {
                    xmlCandidates.add(xml);
                    break; //do not check the rest of the patterns
                }
            }
        }

        if (xmlCandidates.size() == 1) {
            return path.resolve(xmlCandidates.get(0));
        } else if (xmlCandidates.size() == 0) {
            return null;
        } else {
            //Try to filter the result, by removing the reports
            //TODO Make a list of string to filter
            Iterator<String> i = xmlCandidates.iterator();
            while (i.hasNext()) {
                String xmlString = i.next();
                if (xmlString.contains("report")) {
                    i.remove();
                }
            }
            //If only one remaining, return it
            if (xmlCandidates.size() == 1) {
                return path.resolve(xmlCandidates.get(0));
            }
            return null;
        }
    }

    public static ArrayList<VirtualPath> getAllFilesFromDir(VirtualPath path, String[] REGEXs) {
        ArrayList<VirtualPath> paths = new ArrayList<>();
        if (path == null || !path.existsAndHasChildren()) {
            return paths;
        }
        if (REGEXs == null) {
            String[] listFiles;
            try {
                listFiles = path.list();
            } catch (IOException e) {
                return paths;
            }
            if (listFiles == null) {
                return paths;
            }
            for (String file : listFiles) {
                paths.add(path.resolve(file));
            }
            return paths;
        }

        Pattern[] patterns = new Pattern[REGEXs.length];
        for (int i = 0; i < REGEXs.length; i++) {
            patterns[i] = Pattern.compile(REGEXs[i]);
        }

        String[] listFiles;
        try {
            listFiles = path.list();
            
        } catch (IOException e) {
            return paths;
        }
        if (listFiles == null) {
            return paths;
        }
        for (String file : listFiles) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(file).matches()) {
                    paths.add(path.resolve(file));
                }
            }
        }

        return paths;
    }

    public static ArrayList<VirtualPath> getDatastripXmlPaths(S2Config.Sentinel2InputType inputType, VirtualPath inputXml, String[] datastripREGEXs, String[] datastripXmlREGEXs) {
        ArrayList<VirtualPath> paths = new ArrayList<>();
        if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)) {
            VirtualPath datastripPath = inputXml.resolveSibling("DATASTRIP");
            if (datastripPath == null) {
                return paths;
            }
            if (!datastripPath.existsAndHasChildren()) {
                return paths;
            }
            VirtualPath[] datastripFiles;
            try {
                datastripFiles = datastripPath.listPaths();
            } catch (IOException e) {
                return paths;
            }
            if (datastripFiles == null) {
                return paths;
            }
            for (VirtualPath datastrip : datastripFiles) {
                if (datastrip.existsAndHasChildren() && S2NamingConventionUtils.matches(datastrip.getFileName().toString(), datastripREGEXs)) {
                    VirtualPath xml = S2NamingConventionUtils.getFileFromDir(datastrip, datastripXmlREGEXs);
                    if (xml != null) {
                        paths.add(xml);
                    }
                }
            }
        } else if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA)) {
            VirtualPath parentPath = inputXml.getParent();
            if (parentPath == null) {
                return paths;
            }
            VirtualPath parentPath2 = parentPath.getParent();
            if (parentPath2 == null) {
                return paths;
            }
            return getDatastripXmlPaths(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA, parentPath2, datastripREGEXs, datastripXmlREGEXs);
        }
        return paths;
    }

    public static ArrayList<VirtualPath> getGranulesXmlPaths(S2Config.Sentinel2InputType inputType, VirtualPath inputXml, String[] granuleREGEXs, String[] granuleXmlREGEXs) {
        ArrayList<VirtualPath> paths = new ArrayList<>();
        if (inputType.equals(S2Config.Sentinel2InputType.INPUT_TYPE_PRODUCT_METADATA)) {
            VirtualPath granulePath = inputXml.resolveSibling("GRANULE");
            if (granulePath == null) {
                return paths;
            }
            if (!granulePath.existsAndHasChildren()) {
                return paths;
            }
            VirtualPath[] granules;
            try {
                granules = granulePath.listPaths();
            } catch (IOException e) {
                return paths;
            }
            if (granules == null) {
                return paths;
            }
            for (VirtualPath granule : granules) {
                if (granule.existsAndHasChildren() && S2NamingConventionUtils.matches(granule.getFileName().toString(), granuleREGEXs)) {
                    VirtualPath xml = S2NamingConventionUtils.getFileFromDir(granule, granuleXmlREGEXs);
                    if (xml != null) {
                        paths.add(xml);
                    }
                }
            }
        }
        return paths;
    }

    public static VirtualPath getProductXmlFromGranuleXml(VirtualPath granuleXmlPath, String[] REGEXs) {
        VirtualPath productXml = null;
        try {
            Objects.requireNonNull(granuleXmlPath.getParent());
            Objects.requireNonNull(granuleXmlPath.getParent().getParent());
            Objects.requireNonNull(granuleXmlPath.getParent().getParent().getParent());

            VirtualPath up2levels = granuleXmlPath.getParent().getParent().getParent();

            if (up2levels == null) {
                return productXml;
            }
            return getFileFromDir(up2levels, REGEXs);
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public static VirtualPath transformToSentinel2VirtualPath(Path path) throws IOException {
        boolean copyFilesFromDirectoryOnLocalDisk = (path.getFileSystem() != FileSystems.getDefault());
        VirtualDirEx virtualDirEx = VirtualDirEx.build(path, copyFilesFromDirectoryOnLocalDisk, true);
        if (virtualDirEx == null) {
            throw new IllegalArgumentException("Unable to read the path '"+path.toString()+"'.");
        } else {
            Path relativePath;
            if (virtualDirEx.isArchive()) {
                // the path represents an archive
                String folderName = PathUtils.getFileNameWithoutExtension(path);
                if (!folderName.endsWith(".SAFE")) {
                    folderName = folderName + ".SAFE";
                }
                if (!virtualDirEx.exists(folderName)) {
                    folderName = ".";
                }
                relativePath = path.resolve(folderName).getFileName();
            } else {
                // the path represents a directory
                if(Files.isDirectory(path)) {
                    relativePath = Paths.get(".");
                } else {
                    relativePath = path.getFileName();
                }

                //if defaultFileSytem, try to use relativePaths longer in order to be able to get the parents for looking for product metadata
                if(!copyFilesFromDirectoryOnLocalDisk) {
                    if(path.getParent() != null && path.getParent().getParent() != null && path.getParent().getParent().getParent() != null) {
                        virtualDirEx = VirtualDirEx.build(path.getParent().getParent().getParent(), copyFilesFromDirectoryOnLocalDisk, true);
                        relativePath = Paths.get(path.getParent().getParent().getFileName().toString(),path.getParent().getFileName().toString(),path.getFileName().toString());
                    } else if (path.getParent() != null && path.getParent().getParent() != null ) {
                        virtualDirEx = VirtualDirEx.build(path.getParent().getParent(), copyFilesFromDirectoryOnLocalDisk, true);
                        relativePath = Paths.get(path.getParent().getFileName().toString(),path.getFileName().toString());
                    }
                }
            }
            return new VirtualPath(relativePath.toString(), virtualDirEx);
        }
    }
}
