/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2;

import com.bc.ceres.glevel.MultiLevelImage;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.commons.FilePath;
import org.esa.s2tbx.dataio.Utils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegUtils;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.quicklooks.Quicklook;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all Sentinel-2 product readers
 *
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2ProductReader extends AbstractProductReader {

    protected static final Logger logger = Logger.getLogger(Sentinel2ProductReader.class.getName());

    private final S2Config config;

    private Path cacheDir;
    private Product product;

    protected INamingConvention namingConvention;

    protected Sentinel2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.config = new S2Config();
    }

    /**
     * For a given resolution, gets the list of band names.
     * For example, for 10m L1C, {"B02", "B03", "B04", "B08"} should be returned
     *
     * @param resolution the resolution for which the band names should be returned
     * @return then band names or {@code null} if not applicable.
     */
    protected abstract String[] getBandNames(S2SpatialResolution resolution);

    /**
     * @return The configuration file specific to a product reader
     */
    public S2Config getConfig() {
        return config;
    }

    public S2SpatialResolution getProductResolution() {
        return S2SpatialResolution.R10M;
    }

    public boolean isMultiResolution() {
        return true;
    }

    public Path getCacheDir() {
        return cacheDir;
    }

    protected abstract Product buildMosaicProduct(VirtualPath inputVirtualPath) throws IOException;

    protected abstract String getReaderCacheDir();

    protected void initCacheDir(VirtualPath productPath) throws IOException {
        Path versionFile = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("version/version.properties");
        Properties versionProp = new Properties();
        try (InputStream inputStream = Files.newInputStream(versionFile)) {
            versionProp.load(inputStream);
        }
        String version = versionProp.getProperty("project.version");
        if (version == null) {
            throw new IOException("Unable to get project.version property from " + versionFile);
        }
        String fullPathString = productPath.getFullPathString();
        String md5sum = Utils.getMD5sum(fullPathString);
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + fullPathString);
        }
        String readerDirName = getReaderCacheDir();
        String productName = productPath.getFileName().toString();
        Path cacheFolderPath = SystemUtils.getCacheDir().toPath();
        cacheFolderPath = cacheFolderPath.resolve("s2tbx");
        cacheFolderPath = cacheFolderPath.resolve(readerDirName);
        cacheFolderPath = cacheFolderPath.resolve(version);
        cacheFolderPath = cacheFolderPath.resolve(md5sum);
        cacheFolderPath = cacheFolderPath.resolve(productName);
        this.cacheDir = cacheFolderPath;
        if (!Files.exists(this.cacheDir)) {
            Files.createDirectories(this.cacheDir);
        }
        if (!Files.exists(this.cacheDir) || !Files.isDirectory(this.cacheDir) || !Files.isWritable(this.cacheDir)) {
            throw new IOException("Can't access package cache directory");
        }
        logger.fine("Successfully set up cache dir for product " + productName + " to " + this.cacheDir.toString());
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        logger.fine("readProductNodeImpl, input: " + getInput().toString());

        Object inputObject = getInput();
        VirtualPath virtualPath;
        if (inputObject instanceof File) {
            File inputFile = (File) inputObject;
            Path inputPath = processInputPath(inputFile.toPath());
            virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
        } else if (inputObject instanceof VirtualPath) {
            virtualPath = (VirtualPath) getInput();
        } else if (inputObject instanceof Path) {
            Path inputPath = processInputPath((Path) inputObject);
            virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
        } else {
            throw new IllegalArgumentException("Unknown input type '" + inputObject + "'.");
        }

        this.namingConvention = NamingConventionFactory.createNamingConvention(virtualPath);
        if (this.namingConvention == null) {
            throw new NullPointerException("The naming convention is null.");
        } else if (this.namingConvention.hasValidStructure()) {
            VirtualPath inputVirtualPath = this.namingConvention.getInputXml();
            if (inputVirtualPath.exists()) {
                this.product = buildMosaicProduct(inputVirtualPath);

                this.product.setFileLocation(inputVirtualPath.getFilePath().getPath().toFile());

                Path qlFile = getQuickLookFile(inputVirtualPath);
                if (qlFile != null) {
                    this.product.getQuicklookGroup().add(new Quicklook(product, Quicklook.DEFAULT_QUICKLOOK_NAME, qlFile.toFile()));
                }

                this.product.setModified(false);
                return this.product;
            } else {
                throw new FileNotFoundException(inputVirtualPath.getFullPathString());
            }
        } else {
            throw new IllegalStateException("The naming convention structure is invalid.");
        }
    }

    private Path getQuickLookFile(VirtualPath inputVirtualPath) throws IOException {
        VirtualPath parentPath = inputVirtualPath.getParent();
        if (parentPath != null) {
            String[] files = parentPath.list();
            if (files != null && files.length > 0) {
                for (String relativePath : files) {
                    if (relativePath.endsWith(".png") && (relativePath.startsWith("S2") || relativePath.startsWith("BWI_"))) {
                        return inputVirtualPath.resolveSibling(relativePath).getLocalFile();
                    }
                }
            }
        }
        return null;
    }

    /**
     * update the tile layout in S2Config
     *
     * @param metadataFilePath the path to the product metadata file
     * @param isGranule        true if it is the metadata file of a granule
     * @return false when every tileLayout is null
     */
    protected boolean updateTileLayout(VirtualPath metadataFilePath, boolean isGranule) {
        boolean valid = false;
        for (S2SpatialResolution layoutResolution : S2SpatialResolution.values()) {
            TileLayout tileLayout;
            if (isGranule) {
                tileLayout = retrieveTileLayoutFromGranuleMetadataFile(metadataFilePath, layoutResolution);
            } else {
                tileLayout = retrieveTileLayoutFromProduct(metadataFilePath, layoutResolution);
            }
            this.config.updateTileLayout(layoutResolution, tileLayout);
            if (tileLayout != null) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataFilePath the complete path to the granule metadata file
     * @param resolution              the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
    public TileLayout retrieveTileLayoutFromGranuleMetadataFile(VirtualPath granuleMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        if (granuleMetadataFilePath.exists() && granuleMetadataFilePath.getFileName().toString().endsWith(".xml")) {
            VirtualPath granuleDirPath = granuleMetadataFilePath.getParent();
            tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granuleDirPath, resolution);
        }
        return tileLayoutForResolution;
    }


    /**
     * From a product path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param productMetadataFilePath the complete path to the product metadata file
     * @param resolution              the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
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

    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataPath the complete path to the granule directory
     * @param resolution          the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
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

    /**
     * get an iterator to image files in pathToImages containing files for the given resolution
     * <p>
     * This method is based on band names, if resolution can't be based on band names or if image files are not in
     * pathToImages (like for L2A products), this method has to be overriden
     *
     * @param pathToImages the path to the directory containing the images
     * @param resolution   the resolution for which we want to get images
     * @return a {@link DirectoryStream<Path>}, iterator on the list of image path
     * @throws IOException if an I/O error occurs
     */
    protected List<VirtualPath> getImageDirectories(VirtualPath pathToImages, S2SpatialResolution resolution) throws IOException {
        List<VirtualPath> imageDirectories = new ArrayList<>();
        String[] bandNames = getBandNames(resolution);
        if (bandNames != null && bandNames.length > 0) {
            VirtualPath[] imagePaths = pathToImages.listPaths();
            if (imagePaths != null && imagePaths.length > 0) {
                for (String bandName : bandNames) {
                    for (VirtualPath imagePath : imagePaths) {
                        if (imagePath.getFileName().toString().endsWith(bandName + ".jp2")) {
                            imageDirectories.add(imagePath);
                        }
                    }
                }
            }
        }
        return imageDirectories;
    }

    protected Band addBand(Product product, BandInfo bandInfo, Dimension nativeResolutionDimensions) {
        Dimension dimension = new Dimension();
        if (isMultiResolution()) {
            dimension.width = nativeResolutionDimensions.width;
            dimension.height = nativeResolutionDimensions.height;
        } else {
            dimension.width = product.getSceneRasterWidth();
            dimension.height = product.getSceneRasterHeight();
        }

        Band band = new Band(bandInfo.getBandName(), S2Config.SAMPLE_PRODUCT_DATA_TYPE, dimension.width, dimension.height);

        S2BandInformation bandInformation = bandInfo.getBandInformation();
        band.setScalingFactor(bandInformation.getScalingFactor());

        if (bandInformation instanceof S2SpectralInformation) {
            S2SpectralInformation spectralInfo = (S2SpectralInformation) bandInformation;
            band.setSpectralWavelength((float) spectralInfo.getWavelengthCentral());
            band.setSpectralBandwidth((float) spectralInfo.getSpectralBandwith());
            band.setSpectralBandIndex(spectralInfo.getBandId());

            band.setNoDataValueUsed(false);
            band.setNoDataValue(0);
            band.setValidPixelExpression(String.format("%s.raw > %s", bandInfo.getBandName(), S2Config.RAW_NO_DATA_THRESHOLD));
        } else if (bandInformation instanceof S2IndexBandInformation) {
            S2IndexBandInformation indexBandInfo = (S2IndexBandInformation) bandInformation;
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
            band.setSampleCoding(indexBandInfo.getIndexCoding());
            band.setImageInfo(indexBandInfo.getImageInfo());
        } else {
            band.setSpectralWavelength(0);
            band.setSpectralBandwidth(0);
            band.setSpectralBandIndex(-1);
        }

        product.addBand(band);
        return band;
    }

    @Override
    public void close() throws IOException {
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                }
            }
        }
        if (this.namingConvention != null && this.namingConvention.getInputXml() != null) {
            this.namingConvention.getInputXml().close();
        }

        super.close();
    }

    private static Path processInputPath(Path inputPath) {
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

    public static class BandInfo {
        private final Map<String, VirtualPath> tileIdToPathMap;
        private final S2BandInformation bandInformation;
        private final TileLayout imageLayout;

        public BandInfo(Map<String, VirtualPath> tileIdToPathMap, S2BandInformation spectralInformation, TileLayout imageLayout) {
            this.tileIdToPathMap = Collections.unmodifiableMap(tileIdToPathMap);
            this.bandInformation = spectralInformation;
            this.imageLayout = imageLayout;
        }

        public S2BandInformation getBandInformation() {
            return bandInformation;
        }

        public Map<String, VirtualPath> getTileIdToPathMap() {
            return tileIdToPathMap;
        }

        public TileLayout getImageLayout() {
            return imageLayout;
        }

        public String getBandName() {
            return getBandInformation().getPhysicalBand();
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
