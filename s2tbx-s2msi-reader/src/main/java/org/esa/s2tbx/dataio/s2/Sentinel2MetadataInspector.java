package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.commons.FilePath;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.l3.L3Metadata;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout;
import org.esa.s2tbx.dataio.s2.ortho.filepatterns.S2OrthoGranuleDirFilename;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.util.SystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sentinel2MetadataInspector implements MetadataInspector {
    protected Logger logger = Logger.getLogger(getClass().getName());

    private S2Config.Sentinel2ProductLevel productLevel;
    private String productResolution;
    public static final String VIEW_ZENITH_PREFIX = "view_zenith";
    public static final String VIEW_AZIMUTH_PREFIX = "view_azimuth";
    public static final String SUN_ZENITH_PREFIX = "sun_zenith";
    public static final String SUN_AZIMUTH_PREFIX = "sun_azimuth";

    public Metadata getMetadata(Path productPath) throws IOException {
        Path inputPath = S2ProductNamingUtils.processInputPath(productPath);
        VirtualPath virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
        INamingConvention namingConvention = NamingConventionFactory.createNamingConvention(virtualPath);
        VirtualPath inputVirtualPath = namingConvention.getInputXml();
        if (productLevel == S2Config.Sentinel2ProductLevel.L2A) {
            return getL2AMetadata(inputVirtualPath, namingConvention);
        } else if (productLevel == S2Config.Sentinel2ProductLevel.L1C) {
            return getL1CMetadata(inputVirtualPath, namingConvention);
        } else if (productLevel == S2Config.Sentinel2ProductLevel.L3) {
           return getL3Metadata(inputVirtualPath, namingConvention);
        } else if(productLevel == S2Config.Sentinel2ProductLevel.L1B){
            return getL1BMetadata(inputVirtualPath, namingConvention);
        } else {
            throw new IOException("Invalid level " + productLevel + ".");
        }
    }

    public Metadata getL1BMetadata(VirtualPath inputVirtualPath, INamingConvention namingConvention) throws IOException{
        return null;
    }

    public Metadata getL1CMetadata(VirtualPath inputVirtualPath, INamingConvention namingConvention) throws IOException{
        return null;
    }

    public Metadata getL2AMetadata(VirtualPath inputVirtualPath, INamingConvention namingConvention) throws IOException{
        return null;
    }

    public Metadata getL3Metadata(VirtualPath inputVirtualPath, INamingConvention namingConvention) throws IOException{
        Metadata metadata = new Metadata();
//        IL3ProductMetadata metadataProduct = null;
//        try {
//            metadataProduct = L3MetadataFactory.createL3ProductMetadata(virtualPath);
//        } catch (ParserConfigurationException | SAXException e) {
//            e.printStackTrace();
//            logger.log(Level.SEVERE, "Failed to read the metadata file! ", e);
//        }
//        if(metadataProduct == null) {
//            throw new IOException(String.format("Unable to read metadata from %s",virtualPath.getFileName().toString()));
//        }
        boolean isGranule = (namingConvention.getInputType() == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA);
        String granuleDirName = null;
        VirtualPath rootMetadataPath;
        boolean foundProductMetadata = true;
        if (isGranule) {
            try {
                VirtualPath parentPath = inputVirtualPath.getParent();
                Objects.requireNonNull(parentPath);
                granuleDirName = parentPath.getFileName().toString();
            } catch (NullPointerException npe) {
                throw new IOException(String.format("Unable to retrieve the product associated to granule metadata file [%s]", inputVirtualPath.getFileName().toString()));
            }

            rootMetadataPath = namingConvention.getInputProductXml();
            if (rootMetadataPath == null) {
                foundProductMetadata = false;
                rootMetadataPath = inputVirtualPath;
            }
        } else {
            rootMetadataPath = inputVirtualPath;
        }
        String epsg = S2ProductNamingUtils.getEpsgCodeFromGranule(rootMetadataPath);
        S2Config config = new S2Config();
        for (S2SpatialResolution layoutResolution : S2SpatialResolution.values()) {
            TileLayout tileLayout;
            if (isGranule) {
                tileLayout = retrieveTileLayoutFromGranuleMetadataFile(inputVirtualPath, layoutResolution);
            } else {
                tileLayout = retrieveTileLayoutFromProduct(inputVirtualPath, layoutResolution);
            }
            config.updateTileLayout(layoutResolution, tileLayout);
        }
        try {
            S2Metadata metadataHeader = L3Metadata.parseHeader(rootMetadataPath, granuleDirName, config, epsg, namingConvention.getResolution(), isGranule, namingConvention);
            S2OrthoSceneLayout sceneDescription = S2OrthoSceneLayout.create(metadataHeader);
            metadata.setProductWidth(sceneDescription.getSceneDimension(namingConvention.getResolution()).width);
            metadata.setProductHeight(sceneDescription.getSceneDimension(namingConvention.getResolution()).height);

            Arrays.stream(metadataHeader.getProductCharacteristics().getBandInformations()).forEach(bandInfo -> metadata.getBandList().add(bandInfo.getPhysicalBand()));
            addStaticAngleBands(metadata);

            List<S2Metadata.Tile> tileList = metadataHeader.getTileList();
            // Verify access to granule image files, and store absolute location
            // Add the masks in the mask list
            HashMap<String, VirtualPath> tilePathMap = new HashMap<>();
            VirtualPath productPath = null;
            if(rootMetadataPath.exists()){
                productPath = rootMetadataPath.getParent();
            }
            String separator = productPath.getSeparator();
            for (S2BandInformation bandInformation : metadataHeader.getProductCharacteristics().getBandInformations()) {
                for (S2Metadata.Tile tile : tileList) {
                    S2OrthoGranuleDirFilename gf = S2OrthoGranuleDirFilename.create(tile.getId());
                    if (gf != null) {
                        addAnglesBands(metadataHeader.getTile(tile.getId()), metadata);
                        String imageFileTemplate = bandInformation.getImageFileTemplate()
                                .replace("{{TILENUMBER}}", gf.getTileID())
                                .replace("{{MISSION_ID}}", gf.missionID)
                                .replace("{{SITECENTRE}}", gf.siteCentre)
                                .replace("{{CREATIONDATE}}", gf.creationDate)
                                .replace("{{ABSOLUTEORBIT}}", gf.absoluteOrbit)
                                .replace("{{DATATAKE_START}}", metadataHeader.getProductCharacteristics().getDatatakeSensingStartTime())
                                .replace("{{RESOLUTION}}", String.format("%d", bandInformation.getResolution().resolution));
                        String imageFileName;
                        if (foundProductMetadata) {
                            VirtualPath vp = metadataHeader.resolveResource(tile.getId());
                            String fileName = vp.getFileName().toString();
                            imageFileName = String.format("GRANULE%s%s%s%s", separator, fileName, separator, imageFileTemplate);
                        } else {
                            imageFileName = imageFileTemplate;
                        }
                        boolean bFound = false;
                        VirtualPath path = productPath.resolve(imageFileName);
                        if (path.exists()) {
                            tilePathMap.put(tile.getId(), path);
                            bFound = true;
                        } else {
                            VirtualPath parentPath = path.getParent();
                            if (parentPath != null && parentPath.exists()) { //Search a sibling containing the physicalBand name
                                S2BandConstants bandConstant = S2BandConstants.getBandFromPhysicalName(bandInformation.getPhysicalBand());
                                if (bandConstant != null) {
                                    VirtualPath[] otherPaths = parentPath.listPaths(bandConstant.getFilenameBandId());
                                    if (otherPaths != null && otherPaths.length == 1) {
                                        tilePathMap.put(tile.getId(), otherPaths[0]);
                                        bFound = true;
                                    } else if (otherPaths != null && otherPaths.length > 1) {
                                        VirtualPath pathWithResolution = filterVirtualPathsByResolution(otherPaths, bandInformation.getResolution().resolution);
                                        if (pathWithResolution != null) {
                                            tilePathMap.put(tile.getId(), pathWithResolution);
                                            bFound = true;
                                        }
                                    }
                                } else { //try specific bands
                                    S2SpecificBandConstants specificBandConstant = S2SpecificBandConstants.getBandFromPhysicalName(bandInformation.getPhysicalBand());
                                    if (specificBandConstant != null) {
                                        VirtualPath[] otherPaths = parentPath.listPaths(specificBandConstant.getFilenameBandId());
                                        if (otherPaths != null && otherPaths.length == 1) {
                                            tilePathMap.put(tile.getId(), otherPaths[0]);
                                            bFound = true;
                                        } else if (otherPaths != null && otherPaths.length > 1) {
                                            VirtualPath pathWithResolution = filterVirtualPathsByResolution(otherPaths, bandInformation.getResolution().resolution);
                                            if (pathWithResolution != null) {
                                                tilePathMap.put(tile.getId(), pathWithResolution);
                                                bFound = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!bFound) {
                            logger.warning(String.format("Warning: missing file %s\n", path.getFullPathString()));
                        }
                    }
                }
                if (!tilePathMap.isEmpty()) {
                    S2SpatialResolution spatialResolution = bandInformation.getResolution();
                    Sentinel2ProductReader.BandInfo bandInfo = null;
                    if (config.getTileLayout(spatialResolution.resolution) == null) {
                       logger.log(Level.SEVERE,"No resolution level");
                    }else{
                       bandInfo =  new Sentinel2ProductReader.BandInfo(tilePathMap, bandInformation, config.getTileLayout(spatialResolution.resolution));
                    }
                    if (bandInfo != null) {
                        if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                            S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                            IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                            Arrays.stream(indexCoding.getIndexNames()).forEach(index -> metadata.getMaskList().add(indexBandInformation.getPrefix() + index.toLowerCase()));
                        }
                    }
                } else {
                    logger.warning(String.format("Warning: no image files found for band %s\n", bandInformation.getPhysicalBand()));
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE,"Failed to parse metadata in " + rootMetadataPath.getFileName().toString());
            e.printStackTrace();
        }
        if(!metadata.getMaskList().isEmpty()){
            metadata.setHasMasks(true);
        }
        return metadata;
    }

    public void setProductLevel(S2Config.Sentinel2ProductLevel productLevel) {
        this.productLevel = productLevel;
    }

    public void setProductResolution(String productResolution) {
        this.productResolution = productResolution;
    }

    private VirtualPath filterVirtualPathsByResolution(VirtualPath[] paths, int resolution) {
        boolean found = false;
        VirtualPath resultPath = null;
        if (paths == null) {
            return null;
        }
        for (VirtualPath path : paths) {
            if (path.getFileName().toString().contains(String.format("%dm", resolution))) {
                if (found) {
                    return null;
                }
                found = true;
                resultPath = path;
            }
        }
        return resultPath;
    }
    public TileLayout retrieveTileLayoutFromProduct(VirtualPath productMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        if (productMetadataFilePath.exists() && productMetadataFilePath.getFileName().toString().endsWith(".xml")) {
            VirtualPath granulesFolder = productMetadataFilePath.resolveSibling("GRANULE");
            try {
                VirtualPath[] granulesFolderList = granulesFolder.listPaths();
                if (granulesFolderList != null && granulesFolderList.length > 0) {
                    for (VirtualPath granulePath : granulesFolderList) {
                        tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granulePath, resolution);
                        if (tileLayoutForResolution != null) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not retrieve tile layout for product " + productMetadataFilePath.getFullPathString() + " error returned: " + e.getMessage(), e);
            }
        }
        return tileLayoutForResolution;
    }

    private TileLayout retrieveTileLayoutFromGranuleDirectory(VirtualPath granuleMetadataPath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        VirtualPath pathToImages = granuleMetadataPath.resolve("IMG_DATA");
        try {
            List<VirtualPath> imageDirectories = getImageDirectories(pathToImages, resolution);
            for (VirtualPath imageFilePath : imageDirectories) {
                try {
                    if (OpenJpegUtils.canReadJP2FileHeaderWithOpenJPEG()) {
                        Path jp2FilePath = imageFilePath.getLocalFile();
                        tileLayoutForResolution = OpenJpegUtils.getTileLayoutWithOpenJPEG(S2Config.OPJ_INFO_EXE, jp2FilePath);
                    } else {
                        try (FilePath filePath = imageFilePath.getFilePath()) {
                            boolean canSetFilePosition = !imageFilePath.getVirtualDir().isArchive();
                            tileLayoutForResolution = OpenJpegUtils.getTileLayoutWithInputStream(filePath.getPath(), 5 * 1024, canSetFilePosition);
                        }
                    }
                    if (tileLayoutForResolution != null) {
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    // if we have an exception, we try with the next file (if any) // and log a warning
                    logger.log(Level.WARNING, "Could not retrieve tile layout for file " + imageFilePath.toString() + " error returned: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not retrieve tile layout for granule " + granuleMetadataPath.toString() + " error returned: " + e.getMessage(), e);
        }

        return tileLayoutForResolution;
    }

    protected List<VirtualPath> getImageDirectories(VirtualPath pathToImages, S2SpatialResolution spatialResolution) throws IOException {
        ArrayList<VirtualPath> imageDirectories = new ArrayList<>();
        String resolutionFolder = "R" + Integer.toString(spatialResolution.resolution) + "m";
        VirtualPath pathToImagesOfResolution = pathToImages.resolve(resolutionFolder);
        VirtualPath[] imagePaths = pathToImagesOfResolution.listPaths();
        if(imagePaths == null || imagePaths.length == 0) {
            return imageDirectories;
        }

        for (VirtualPath imagePath : imagePaths) {
            if (imagePath.getFileName().toString().endsWith("_" + spatialResolution.resolution + "m.jp2")) {
                imageDirectories.add(imagePath);
            }
        }

        return imageDirectories;
    }

    protected String[] getBandNames(S2SpatialResolution resolution) {
        String[] bandNames;

        switch (resolution) {
            case R10M:
                bandNames = new String[]{"B02", "B03", "B04", "B08"};
                break;
            case R20M:
                bandNames = new String[]{"B05", "B06", "B07", "B8A", "B11", "B12"};
                break;
            case R60M:
                bandNames = new String[]{"B01", "B09", "B10"};
                break;
            default:
                SystemUtils.LOG.warning("Invalid resolution: " + resolution);
                bandNames = null;
                break;
        }

        return bandNames;
    }

    public TileLayout retrieveTileLayoutFromGranuleMetadataFile(VirtualPath granuleMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        if (granuleMetadataFilePath.exists() && granuleMetadataFilePath.getFileName().toString().endsWith(".xml")) {
            VirtualPath granuleDirPath = granuleMetadataFilePath.getParent();
            tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granuleDirPath, resolution);
        }
        return tileLayoutForResolution;
    }

    public void addAnglesBands(S2Metadata.Tile tile, Metadata metadata){
        Arrays.stream(tile.getViewingIncidenceAnglesGrids()).forEach(tileInfo -> metadata.getBandList().add(VIEW_AZIMUTH_PREFIX + "_" + S2BandConstants.getBand(tileInfo.getBandId())));
        Arrays.stream(tile.getViewingIncidenceAnglesGrids()).forEach(tileInfo -> metadata.getBandList().add(VIEW_ZENITH_PREFIX + "_" + S2BandConstants.getBand(tileInfo.getBandId())));
    }
    public void addStaticAngleBands(Metadata metadata){
        metadata.getBandList().add(VIEW_ZENITH_PREFIX + "_mean");
        metadata.getBandList().add(VIEW_AZIMUTH_PREFIX + "_mean");
        metadata.getBandList().add(SUN_AZIMUTH_PREFIX);
        metadata.getBandList().add(SUN_ZENITH_PREFIX);
    }
}
