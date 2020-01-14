package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jcoravu on 17/12/2019.
 */
public class MuscateMetadataInspector implements MetadataInspector {

    public MuscateMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, false)) {
            String[] filePaths = productDirectory.listAllFiles();
            MuscateMetadata productMetadata = MuscateProductReader.readMetadata(productDirectory, filePaths);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(productMetadata.getRasterWidth());
            metadata.setProductHeight(productMetadata.getRasterHeight());
            metadata.setGeoCoding(productMetadata.buildCrsGeoCoding());

            ProductFilePathsHelper filePathsHelper = new ProductFilePathsHelper(filePaths, productDirectory.getFileSystemSeparator());

            // add bands
            List<String> imageBandNames = computeImageBandNames(filePathsHelper, productDirectory, productMetadata);
            metadata.getBandList().addAll(imageBandNames);

            // add masks
            for (MuscateMask muscateMask : productMetadata.getMasks()) {
                if (muscateMask != null && muscateMask.nature != null) {
                    List<String> maskNames = computeMaskNames(productMetadata, filePathsHelper, productDirectory, muscateMask);
                    metadata.getMaskList().addAll(maskNames);
                }
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private static List<String> computeMaskNames(MuscateMetadata metadata, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMask muscateMask)
                                                 throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> maskNamesToReturn = new ArrayList<>();
        float versionFloat = metadata.getVersion();
        Set<String> addedFiles = new HashSet<>();
        if (muscateMask.nature.equals(MuscateMask.AOT_INTERPOLATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeAOTMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETAILED_CLOUD_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    List<String> cloudMaskNames = computeCloudMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(cloudMaskNames);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_MASK_VERSION) {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    List<String> cloudMaskNames = computeCloudMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(cloudMaskNames);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Cloud);
                    if (maskName != null) {
                        maskNamesToReturn.add(maskName);
                    }
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_SHADOW_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_SHADOW_MASK_VERSION) {
                // in some old products the Nature is Cloud_Shadow instead of Geophysics. Perhaps an error?
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    List<String> cloudShadowMaskNames = computeGeophysicsMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(cloudShadowMaskNames);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Cloud_Shadow);
                    if (maskName != null) {
                        maskNamesToReturn.add(maskName);
                    }
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.EDGE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    String maskName = computeEdgeMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    if (maskName != null) {
                        maskNamesToReturn.add(maskName);
                    }
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.SATURATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    List<String> saturationMaskNames = computeSaturationMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(saturationMaskNames);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.GEOPHYSICS_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    List<String> cloudShadowMaskNames = computeGeophysicsMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(cloudShadowMaskNames);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETECTOR_FOOTPRINT_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add( muscateMaskFile.path)) {
                    List<String> detectorFootprintMaskNames = computeDetectorFootprintMaskNames( muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(detectorFootprintMaskNames);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DEFECTIVE_PIXEL_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    List<String> defectivePixelMaskNames = computeDefectivePixelMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                    maskNamesToReturn.addAll(defectivePixelMaskNames);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.HIDDEN_SURFACE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Hidden_Surface);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.SNOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Snow);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.SUN_TOO_LOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Sun_Too_Low);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.TANGENT_SUN_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Tangent_Sun);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.TOPOGRAPHY_SHADOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Topography_Shadow);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.WATER_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata, MuscateConstants.GEOPHYSICAL_BIT.Water);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.WVC_INTERPOLATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                String maskName = computeWVCMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, metadata);
                if (maskName != null) {
                    maskNamesToReturn.add(maskName);
                }
            }
        }

        return maskNamesToReturn;
    }

    private static String computeAOTMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            return MuscateProductReader.computeAOTMaskName(geoposition);
        }
        return null;
    }

    private static String computeWVCMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                             throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            return MuscateProductReader.computeWVCMaskName(geoposition);
        }
        return null;
    }

    private static List<String> computeDefectivePixelMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                               throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> maskNames = new ArrayList<>();
        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            String[] orderedBandNames = metadata.getOrderedBandNames(geoposition.id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                maskNames.add(MuscateProductReader.computeDefectivePixelMaskName(orderedBandNames[i]));
            }
        }
        return maskNames;
    }

    private static List<String> computeDetectorFootprintMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                                  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> maskNames = new ArrayList<>();
        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            String[] orderedBandNames = metadata.getOrderedBandNames(geoposition.id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                String maskName = MuscateProductReader.computeDetectorFootprintMaskName(tiffImageRelativeFilePath, orderedBandNames[i]);
                maskNames.add(maskName);
            }
        }
        return maskNames;
    }

    private static List<String> computeSaturationMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                           throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> maskNames = new ArrayList<>();
        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            List<String> bands = metadata.getBandNames(geoposition.id);
            for (int bitCount=0; bitCount<bands.size(); bitCount++) {
                String bandId = bands.get(bitCount);
                maskNames.add(MuscateProductReader.computeSaturationMaskName(bandId));
            }
        }
        return maskNames;
    }

    private static String computeEdgeMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                              throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            return MuscateProductReader.computeEdgeMaskName(geoposition);
        }
        return null;
    }

    private static List<String> computeGeophysicsMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                           throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> cloudMaskNames = new ArrayList<>();
        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            for (MuscateConstants.GEOPHYSICAL_BIT geophysicalBit : MuscateConstants.GEOPHYSICAL_BIT.values()) {
                String maskName = MuscateProductReader.computeGeographicMaskName(geophysicalBit, geoposition);
                cloudMaskNames.add(maskName);
            }
        }
        return cloudMaskNames;
    }

    private static String computeGeophysicsMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory,
                                                    MuscateMetadata metadata, MuscateConstants.GEOPHYSICAL_BIT geophysicalBit)
                                                    throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            return MuscateProductReader.computeGeographicMaskName(geophysicalBit, geoposition);
        }
        return null;
    }

    private static List<String> computeCloudMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                      throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> cloudMaskNames = new ArrayList<>();
        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
        if (geoposition != null) {
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskAllName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskAllCloudName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskReflectanceName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskReflectanceVarianceName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskExtensionName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskInsideShadowName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskOutsideShadowName(geoposition));
            cloudMaskNames.add(MuscateProductReader.computeCloudMaskCirrusName(geoposition));
        }
        return cloudMaskNames;
    }

    private static List<String> computeImageBandNames(ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                      throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        List<String> bandNamesToReturn = new ArrayList<>();
        List<MuscateImage> muscateImages = metadata.getImages();
        for (MuscateImage muscateImage : muscateImages) {
            if (muscateImage != null && muscateImage.nature != null) {
                if (muscateImage.nature.equals(MuscateImage.AEROSOL_OPTICAL_THICKNESS_IMAGE)) {
                    List<String> bandNames = computeImageBandNames(muscateImage.getImageFiles(), filePathsHelper, productDirectory, metadata, MuscateProductReader.buildAOTImageBandNameCallback());
                    bandNamesToReturn.addAll(bandNames);
                } else if (muscateImage.nature.equals(MuscateImage.FLAT_REFLECTANCE_IMAGE)) {
                    List<String> bandNames = computeImageBandNames(muscateImage.getImageFiles(), filePathsHelper, productDirectory, metadata, MuscateProductReader.buildFlatReflectanceImageBandNameCallback());
                    bandNamesToReturn.addAll(bandNames);
                } else if (muscateImage.nature.equals(MuscateImage.SURFACE_REFLECTANCE_IMAGE)) {
                    List<String> bandNames = computeImageBandNames(muscateImage.getImageFiles(), filePathsHelper, productDirectory, metadata, MuscateProductReader.buildSurfaceReflectanceImageBandNameCallback());
                    bandNamesToReturn.addAll(bandNames);
                } else if (muscateImage.nature.equals(MuscateImage.WATER_VAPOR_CONTENT_IMAGE)) {
                    List<String> bandNames = computeImageBandNames(muscateImage.getImageFiles(), filePathsHelper, productDirectory, metadata, MuscateProductReader.buildWVCImageBandNameCallback());
                    bandNamesToReturn.addAll(bandNames);
                }
            }
        }
        return bandNamesToReturn;
    }

    private static List<String> computeImageBandNames(List<String> imageFileList, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory,
                                                      MuscateMetadata metadata, MuscateProductReader.BandNameCallback bandNameCallback)
                                                      throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        List<String> bandNames = new ArrayList<>();
        for (String tiffImageRelativeFilePath : imageFileList) {
            MuscateMetadata.Geoposition geoPosition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, metadata);
            if (geoPosition != null) {
                String bandName = bandNameCallback.buildBandName(geoPosition, tiffImageRelativeFilePath);
                bandNames.add(bandName);
            }
        }
        return bandNames;
    }

    private static MuscateMetadata.Geoposition findGeoPosition(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata metadata)
                                                               throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        String tiffImageFilePath = filePathsHelper.computeImageRelativeFilePath(productDirectory, tiffImageRelativeFilePath);
        try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(productDirectory.getBaseFile().toPath(), tiffImageFilePath)) {
            int defaultBandWidth = geoTiffImageReader.getImageWidth();
            int defaultBandHeight = geoTiffImageReader.getImageHeight();
            return metadata.getGeoposition(defaultBandWidth, defaultBandHeight);
        }
    }
}
