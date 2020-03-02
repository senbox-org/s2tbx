/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotTake5Metadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import javax.imageio.spi.ImageInputStreamSpi;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This product reader is used for products of SPOT4 TAKE5 experiment, products distributed by Theia.
 * The input format of the product is an archive with the ".tgz" extension or an xml file corresponding to the metadata.
 * If the input is an archive, the archive is expected to have a subfolder, containing the xml metadata file, that should have the same name as this subfolder.
 * If the input as a xml metadata file, the rest of the files (images and masks) are expected to be found in the location defined by the metadata values,
 * depending on the location of the metadata file.
 * In both cases, the input file (the archive or the metadata file) shoud start with "SPOT4_HRVIR1_XS_" (HVIR1 is the sensor of SPOT4 satellites).
 *
 * @author Ramona Manda
 * modified 20190515 for VFS compatibility by Oana H.
 */
public class SpotTake5ProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(SpotTake5ProductReader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(SpotTake5Metadata.class, new XmlMetadataParser<SpotTake5Metadata>(SpotTake5Metadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    public SpotTake5ProductReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    public TreeNode<File> getProductComponents() {
        TreeNode<File> result = super.getProductComponents();
        if (this.productDirectory.isCompressed()) {
            return result;
        } else {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            SpotTake5Metadata imageMetadata;
            try {
                imageMetadata = readImageMetadata(productPath, this.productDirectory);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            for (String inputFile : imageMetadata.getTiffFiles().values()) {
                try {
                    TreeNode<File> productFile = new TreeNode<File>(inputFile);
                    productFile.setContent(this.productDirectory.getFile(inputFile));
                    result.addChild(productFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (String inputFile : imageMetadata.getMaskFiles().values()) {
                try {
                    TreeNode<File> productFile = new TreeNode<File>(inputFile);
                    productFile.setContent(this.productDirectory.getFile(inputFile));
                    result.addChild(productFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            SpotTake5Metadata imageMetadata = readImageMetadata(productPath, this.productDirectory);

            Dimension defaultProductSize = new Dimension(imageMetadata.getRasterWidth(), imageMetadata.getRasterHeight());
            ProductSubsetDef subsetDef = getSubsetDef();

            Map<String, String> tiffFiles = imageMetadata.getTiffFiles();
            List<String> sortedKeys = new ArrayList<>(tiffFiles.keySet());
            Collections.sort(sortedKeys);

            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;
            boolean isMultiSize = isMultiSize(sortedKeys, tiffFiles, imageMetadata);
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
            } else {
                String key = sortedKeys.get(sortedKeys.size() - 1);
                String tiffFile = imageMetadata.getMetaSubFolder() + tiffFiles.get(key);
                File rasterFile = this.productDirectory.getFile(tiffFile);
                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath())) {
                    productDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
                    productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, isMultiSize);
                }
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(imageMetadata.getProductName(), SpotConstants.SPOT4_TAKE5_FORMAT_NAME[0], productBounds.width, productBounds.height, this);
            product.setFileLocation(productPath.toFile());
            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(imageMetadata.getRootElement());
            }
            ProductData.UTC startTime = imageMetadata.getDatePdv();
            product.setStartTime(startTime);
            product.setEndTime(startTime);
            product.setDescription(SpotConstants.SPOT4_TAKE5_FORMAT + " level:" + imageMetadata.getMetadataProfile() + " zone:" + imageMetadata.getGeographicZone());
            product.setAutoGrouping(buildBandsGroupPattern());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);

            // all the bands of the tiff files are added to the product
            String[] bandNames = imageMetadata.getBandNames();
            this.bandImageReaders = new ArrayList<>(sortedKeys.size());
            for (int i = sortedKeys.size() - 1; i >= 0; i--) {
                String key = sortedKeys.get(i);
                String tiffFile = imageMetadata.getMetaSubFolder() + tiffFiles.get(key);
                String bandNamePrefix = key + "_";
                Product geoTiffProduct = readGeoTiffProduct(tiffFile, defaultProductSize, productDefaultGeoCoding, subsetDef, isMultiSize);
                if (geoTiffProduct != null) {
                    if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                        if (geoTiffProduct.getMetadataRoot() != null) {
                            XmlMetadata.CopyChildElements(geoTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                        }
                    }
                    geoTiffProduct.transferGeoCodingTo(product, null);
                    for (int bandIndex = 0; bandIndex < geoTiffProduct.getNumBands(); bandIndex++) {
                        String bandName = (bandIndex < bandNames.length) ? bandNames[bandIndex] : (SpotConstants.DEFAULT_BAND_NAME_PREFIX + bandIndex);
                        if (product.getBand(bandName) != null) {
                            bandName = bandNamePrefix + bandName; // the product contains already the band
                        }
                        if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                            Band geoTiffBand = geoTiffProduct.getBandAt(bandIndex);
                            geoTiffBand.setName(bandName);
                            geoTiffBand.setDescription(bandName);
                            if (geoTiffBand.getUnit() == null) {
                                geoTiffBand.setUnit(SpotConstants.VALUE_NOT_AVAILABLE);
                            }
                            geoTiffBand.setNoDataValueUsed(true);
                            product.addBand(geoTiffBand);
                        }
                    }
                    geoTiffProduct.getBandGroup().removeAll(); // remove the bands from the geo tif product
                }
            }

            // for each mask found in the metadata, the first band of the mask is added to the product, in order to create the masks
            Map<String, Band> maskBands = new HashMap<String, Band>();
            for (Map.Entry<String, String> entry : imageMetadata.getMaskFiles().entrySet()) {
                String bandName = entry.getKey();
                if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                    String tiffFile = imageMetadata.getMetaSubFolder() + entry.getValue();
                    Product geoTiffProduct = readGeoTiffProduct(tiffFile, defaultProductSize, productDefaultGeoCoding, subsetDef, isMultiSize);
                    if (geoTiffProduct != null) {
                        Band geoTiffBand = geoTiffProduct.getBandAt(0);
                        geoTiffBand.setName(bandName);
                        geoTiffBand.setDescription(bandName);
                        if (geoTiffBand.getUnit() == null) {
                            geoTiffBand.setUnit(SpotConstants.VALUE_NOT_AVAILABLE);
                        }
                        geoTiffBand.setNoDataValueUsed(true);
                        product.addBand(geoTiffBand);
                        maskBands.put(entry.getKey(), geoTiffBand);
                        geoTiffProduct.getBandGroup().removeAll(); // remove the bands from the geo tif product
                    }
                }
            }

            // saturated flags & masks
            if (maskBands.keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_SATURATION)) {
                FlagCoding saturatedFlagCoding = createSaturatedFlagCoding();
                product.getFlagCodingGroup().add(saturatedFlagCoding);
                maskBands.get(SpotConstants.SPOT4_TAKE5_TAG_SATURATION).setSampleCoding(saturatedFlagCoding);
                List<Mask> saturatedMasks = createMasksFromFlagCodding(product.getSceneRasterWidth(), product.getSceneRasterHeight(), saturatedFlagCoding);
                for (Mask mask : saturatedMasks) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(mask.getName())) {
                        product.getMaskGroup().add(mask);
                    }
                }
            }

            // clouds flags & masks
            if (maskBands.keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS)) {
                FlagCoding cloudsFlagCoding = createCloudsFlagCoding();
                product.getFlagCodingGroup().add(cloudsFlagCoding);
                maskBands.get(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS).setSampleCoding(cloudsFlagCoding);
                List<Mask> cloudMasks = createMasksFromFlagCodding(product.getSceneRasterWidth(), product.getSceneRasterHeight(), cloudsFlagCoding);
                for (Mask mask : cloudMasks) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(mask.getName())) {
                        product.getMaskGroup().add(mask);
                    }
                }
            }

            // diverse flags & masks
            if (maskBands.keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE)) {
                FlagCoding diverseFlagCoding = createDiverseFlagCoding();
                product.getFlagCodingGroup().add(diverseFlagCoding);
                maskBands.get(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE).setSampleCoding(diverseFlagCoding);
                List<Mask> diverseMasks = createMasksFromFlagCodding(product.getSceneRasterWidth(), product.getSceneRasterHeight(), diverseFlagCoding);
                for (Mask mask : diverseMasks) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(mask.getName())) {
                        product.getMaskGroup().add(mask);
                    }
                }
            }

            success = true;

            return product;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (!success) {
                closeResources();
            }
        }
    }

    private void closeResources() {
        try {
            if (this.bandImageReaders != null) {
                for (GeoTiffImageReader geoTiffImageReader : this.bandImageReaders) {
                    try {
                        geoTiffImageReader.close();
                    } catch (Exception ignore) {
                        // ignore
                    }
                }
                this.bandImageReaders.clear();
                this.bandImageReaders = null;
            }
        } finally {
            try {
                if (this.imageInputStreamSpi != null) {
                    ImageRegistryUtils.deregisterImageInputStreamSpi(this.imageInputStreamSpi);
                    this.imageInputStreamSpi = null;
                }
            } finally {
                if (this.productDirectory != null) {
                    this.productDirectory.close();
                    this.productDirectory = null;
                }
            }
        }
        System.gc();
    }

    private Product readGeoTiffProduct(String tiffFile, Dimension defaultProductSize, GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef, boolean isMultiSize) throws Exception {
        File rasterFile = this.productDirectory.getFile(tiffFile);
        GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath());
        this.bandImageReaders.add(geoTiffImageReader);
        int defaultBandWidth = geoTiffImageReader.getImageWidth();
        int defaultBandHeight = geoTiffImageReader.getImageHeight();
        Rectangle bandBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
        } else {
            GeoCoding bandDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
            bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, defaultBandWidth, defaultBandHeight, isMultiSize);
        }
        if (bandBounds.isEmpty()) {
            return null; // no intersection
        }
        GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
        return geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);
    }

    public static SpotTake5Metadata readImageMetadata(Path productPath, VirtualDirEx productDirectory) throws IOException {
        File imageMetadataFile = null;
        boolean isPacked = false;
        String metaSubFolder = "";
        if (VirtualDirEx.isPackedFile(productPath)) {
            isPacked = true;
            //if the input is an archive, check the metadata file as being the name of the archive, followed by ".xml", right under the unpacked archive folder
            String path = productDirectory.getBasePath();
            String metaFile = path.substring(path.lastIndexOf(productPath.getFileSystem().getSeparator()) + 1, path.lastIndexOf("."));
            try {
                imageMetadataFile = productDirectory.getFile(metaFile + SpotConstants.SPOT4_TAKE5_METADATA_FILE_EXTENSION);
            } catch (FileNotFoundException ex) {
                //if the metadata is not found as described above, the subfolders as checked, and for each subfolder, an xml file with the same name as the subfolder is searched
                for (String entrySubFolder : productDirectory.getTempDir().list()) {
                    if (new File(productDirectory.getTempDir(), entrySubFolder).isDirectory()) {
                        try {
                            imageMetadataFile = productDirectory.getFile(entrySubFolder + productPath.getFileSystem().getSeparator() + entrySubFolder + SpotConstants.SPOT4_TAKE5_METADATA_FILE_EXTENSION);
                            //if the metadata is found under a subfolder of the archive, this subfolder must be used in order to compute the path for the rest of the files found in the metadata file
                            //metaSubFolder = entrySubFolder + File.separator;
                            metaSubFolder = entrySubFolder + productPath.getFileSystem().getSeparator();
                            break;
                        } catch (Exception ex2) {
                            logger.warning(ex2.getMessage());
                        }
                    }
                }
            }
        } else {
            imageMetadataFile = productDirectory.getFile(productPath.toString());
            if (!imageMetadataFile.isFile()) {
                imageMetadataFile = productDirectory.getFile(productDirectory.findFirst(SpotConstants.SPOT4_TAKE5_METADATA_FILE_EXTENSION));
            }
        }

        SpotTake5Metadata imageMetadata = GenericXmlMetadata.create(SpotTake5Metadata.class, imageMetadataFile);
        imageMetadata.setMetaSubFolder(metaSubFolder);

        String productLevel = imageMetadata.getMetadataProfile();
        // for N2, the masks may not be present in metadata, but in a folder named MASK
        String masksFolderName = imageMetadata.getMasksFolder();
        if (productLevel.startsWith("N2") && masksFolderName != null) {
            //File masksFolder = new File(imageMetadataFile.getParent(), masksFolderName);
            //File masksFolder = this.input.getFile(Paths.get(imageMetadataFile.getParent()).getFileName().toString() + inputPath.getFileSystem().getSeparator() + masksFolderName);
            String parentFolderName = imageMetadataFile.toPath().getParent().getFileName().toString();
            File masksFolder = null;
            if (isPacked) {
                // no way to verify the existence inside tgz, the method always returns true, so check at temp dir level where the tgz is unpacked
                File tempDir = productDirectory.getTempDir();
                if (tempDir != null && tempDir.list() != null) {
                    if (Arrays.asList(tempDir.list()).contains(masksFolderName)) {
                        masksFolder = productDirectory.getFile(masksFolderName);
                    } else if (Arrays.asList(tempDir.list()).contains(parentFolderName)) {
                        // seek the MASK folder inside the parent folder name
                        File parentFolder = productDirectory.getFile(parentFolderName);
                        if (Arrays.asList(parentFolder.list()).contains(masksFolderName)) {
                            masksFolder = productDirectory.getFile(Paths.get(parentFolderName) + productPath.getFileSystem().getSeparator() + masksFolderName);
                        }

                    }
                }
            } else if (productDirectory.exists(masksFolderName) || productDirectory.exists(Paths.get(parentFolderName) + productPath.getFileSystem().getSeparator() + masksFolderName)) {
                if (productDirectory.exists(masksFolderName)) {
                    masksFolder = productDirectory.getFile(masksFolderName);
                } else {
                    masksFolder = productDirectory.getFile(Paths.get(parentFolderName) + productPath.getFileSystem().getSeparator() + masksFolderName);
                }
            }

            if (masksFolder != null) {
                String[] files = masksFolder.list();
                Map<String, String> maskFiles = imageMetadata.getMaskFiles();
                if (files != null) {
                    for (String file : files) {
                        String path = masksFolderName + productDirectory.getFileSystemSeparator() + file;
                        if (file.contains("SAT")) {
                            maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_SATURATION, path);
                        } else if (file.contains("NUA")) {
                            maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS, path);
                        } else if (file.contains("DIV")) {
                            maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE, path);
                        }
                    }
                }
            }

            Map<String, String> rasterFiles = imageMetadata.getTiffFiles();
            if (!rasterFiles.containsKey(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT) ||
                    rasterFiles.get(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT) == null ||
                    rasterFiles.get(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT).isEmpty()) {
                String[] rasterNames = imageMetadataFile.getParentFile().list((dir, name) -> name.contains("AOT"));
                if (rasterNames != null && rasterNames.length > 0) {
                    rasterFiles.put(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT, rasterNames[0]);
                }
            }
        }
        return imageMetadata;
    }

    private static String buildBandsGroupPattern() {
        return SpotConstants.SPOT4_TAKE5_TAG_GEOTIFF + SpotConstants.BAND_GROUP_SEPARATOR +
                SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT + SpotConstants.BAND_GROUP_SEPARATOR +
                SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_ENV + SpotConstants.BAND_GROUP_SEPARATOR +
                SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_PENTE + SpotConstants.BAND_GROUP_SEPARATOR +
                SpotConstants.SPOT4_TAKE5_TAG_ORTHO_VAP_EAU + SpotConstants.BAND_GROUP_SEPARATOR +
                SpotConstants.SPOT4_TAKE5_GROUP_MASKS;
    }

    /**
     * Creates and adds masks to the product, by using the given flag coding. Each added mask uses the width and the height of the product.
     *
     * @param flagCoding for each flag of this flagCoding parameter, a new mask is added, with the same name as the flag
     * @return the list of all the masks added to the product
     */
    private static List<Mask> createMasksFromFlagCodding(int productWidth, int productHeight, FlagCoding flagCoding) {
        String flagCodingName = flagCoding.getName();
        List<Mask> masks = new ArrayList<Mask>();
        for (String flagName : flagCoding.getFlagNames()) {
            MetadataAttribute flag = flagCoding.getFlag(flagName);
            masks.add(Mask.BandMathsType.create(flagName,
                    flag.getDescription(),
                    productWidth, productHeight,
                    flagCodingName + "." + flagName,
                    ColorIterator.next(),
                    0.5));
        }
        return masks;
    }

    /**
     * Creates the flags for the saturation mask; the flags are:
     * <ul><li>XS1 band is saturated</li></ul>
     * <ul><li>XS2 band is saturated</li></ul>
     * <ul><li>XS3 band is saturated</li></ul>
     * <ul><li>SWIR band is saturated</li></ul>
     *
     * @return the flagCoding created with the saturated flags, and added to the product
     */
    public static FlagCoding createSaturatedFlagCoding() {
        String bandName = SpotConstants.SPOT4_TAKE5_TAG_SATURATION;
        FlagCoding flagCoding = new FlagCoding(bandName);
        flagCoding.addFlag("XS1_saturated", 1, "XS1 band is saturated");
        flagCoding.addFlag("XS2_saturated", 2, "XS2 band is saturated");
        flagCoding.addFlag("XS3_saturated", 4, "XS3 band is saturated");
        flagCoding.addFlag("SWIR_saturated", 8, "SWIR band is saturated");
        return flagCoding;
    }

    /**
     * Creates the flags for the clouds mask; the flags are:
     * <ul><li>all clouds (except thin ones) or shadows</li></ul>
     * <ul><li>all clouds (except thin ones)</li></ul>
     * <ul><li>cloud detected through absolute threshold</li></ul>
     * <ul><li>cloud detected through multi-t threshold</li></ul>
     * <ul><li>very thin clouds</li></ul>
     * <ul><li>high clouds detected with 1.38 µm band (LANDSAT 8 only)</li></ul>
     * <ul><li>cloud shadows matched with a cloud</li></ul>
     * <ul><li>cloud shadows in the zone where clouds could be outside the image (less reliable)</li></ul>
     *
     * @return the flagCoding created with the clouds flags, and added to the product
     */
    public static FlagCoding createCloudsFlagCoding() {
        String bandName = SpotConstants.SPOT4_TAKE5_TAG_CLOUDS;
        FlagCoding flagCoding = new FlagCoding(bandName);
        flagCoding.addFlag("clouds_or_shadows", 1, "all clouds (except thin ones) or shadows");
        flagCoding.addFlag("clouds", 2, "all clouds (except thin ones)");
        flagCoding.addFlag("cloud_absolute_threshold", 4, "cloud detected through absolute threshold");
        flagCoding.addFlag("cloud_multi_t_threshold", 8, "cloud detected through multi-t threshold");
        flagCoding.addFlag("thin_clouds", 16, "very thin clouds");
        flagCoding.addFlag("clouds_1.38band", 32, "high clouds detected with 1.38 µm band (LANDSAT 8 only)");
        flagCoding.addFlag("shadows_matched_clouds", 64, "cloud shadows matched with a cloud");
        flagCoding.addFlag("shadows_for_clouds_outside", 128, "cloud shadows in the zone where clouds could be outside the image (less reliable)");
        return flagCoding;
    }

    /**
     * Creates the flags for the diverse masks (snow, water); the flags are:
     * <ul><li>no diverse data</li></ul>
     * <ul><li>water</li></ul>
     * <ul><li>snow</li></ul>
     * <ul><li>Sun too low for terrain correction (limitation of correction factor that tends to the infinity, correction is false)</li></ul>
     * <ul><li>Sun too low for terrain correction (correction might be inaccurate)</li></ul>
     *
     * @return the flagCoding created with the saturated flags, and added to the
     */
    public static FlagCoding createDiverseFlagCoding() {
        String bandName = SpotConstants.SPOT4_TAKE5_TAG_DIVERSE;
        FlagCoding flagCoding = new FlagCoding(bandName);
        flagCoding.addFlag("no_div_data", 1, "no diverse data");
        flagCoding.addFlag("water", 2, "water");
        flagCoding.addFlag("snow", 4, "snow");
        flagCoding.addFlag("false_correction", 8, "Sun too low for terrain correction (limitation of correction factor that tends to the infinity, correction is false)");
        flagCoding.addFlag("inaccurate_correction", 16, "Sun too low for terrain correction (correction might be inaccurate)");
        return flagCoding;
    }

    private boolean isMultiSize(List<String> sortedKeys, Map<String, String> tiffFiles, SpotTake5Metadata imageMetadata) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int bandWidth = 0;
        int bandHeight = 0;
        for (int i = sortedKeys.size() - 1; i >= 0; i--) {
            String key = sortedKeys.get(i);
            String tiffFile = imageMetadata.getMetaSubFolder() + tiffFiles.get(key);
            File rasterFile = this.productDirectory.getFile(tiffFile);
            GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath());
            if(bandWidth == 0){
                bandWidth = geoTiffImageReader.getImageWidth();
            }else if(bandWidth != geoTiffImageReader.getImageWidth()){
                return true;
            }
            if(bandHeight == 0){
                bandHeight = geoTiffImageReader.getImageHeight();
            }else if(bandHeight != geoTiffImageReader.getImageHeight()){
                return true;
            }
        }
        return false;
    }

    /**
     * Class used to define a list of colors for the masks to be coloured
     */
    private static class ColorIterator {

        @SuppressWarnings("CanBeFinal")
        static ArrayList<Color> colors;
        static Iterator<Color> colorIterator;

        static {
            colors = new ArrayList<Color>();
            colors.add(Color.red);
            colors.add(Color.red.darker());
            colors.add(Color.blue);
            colors.add(Color.blue.darker());
            colors.add(Color.green);
            colors.add(Color.green.darker());
            colors.add(Color.yellow);
            colors.add(Color.yellow.darker());
            colors.add(Color.magenta);
            colors.add(Color.magenta.darker());
            colors.add(Color.pink);
            colors.add(Color.pink.darker());
            colors.add(Color.cyan);
            colors.add(Color.cyan.darker());
            colors.add(Color.orange);
            colors.add(Color.orange.darker());
            colors.add(Color.blue.darker().darker());
            colors.add(Color.green.darker().darker());
            colors.add(Color.yellow.darker().darker());
            colors.add(Color.magenta.darker().darker());
            colors.add(Color.pink.darker().darker());
            colorIterator = colors.iterator();
        }

        static Color next() {
            if (!colorIterator.hasNext()) {
                colorIterator = colors.iterator();
            }
            return colorIterator.next();
        }
    }

}
