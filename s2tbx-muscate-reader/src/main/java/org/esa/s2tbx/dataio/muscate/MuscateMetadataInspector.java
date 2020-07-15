package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.MetadataInspector;
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
            ProductFilePathsHelper filePathsHelper = new ProductFilePathsHelper(productDirectory);

            MuscateMetadata productMetadata = filePathsHelper.getMetadata();

            Metadata metadata = new Metadata(productMetadata.getRasterWidth(), productMetadata.getRasterHeight());
            metadata.setGeoCoding(productMetadata.buildCrsGeoCoding());

            // add bands
            List<String> imageBandNames = computeImageBandNames(filePathsHelper, productDirectory, productMetadata);
            metadata.getBandList().addAll(imageBandNames);
            addAngleBands(metadata,productMetadata);

            // add masks
            for (MuscateMask muscateMask : productMetadata.getMasks()) {
                if (muscateMask != null && muscateMask.nature != null) {
                    computeMaskNames(metadata, productMetadata, filePathsHelper, productDirectory, muscateMask);
                }
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private static void computeMaskNames(Metadata metadata, MuscateMetadata productMetadata, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMask muscateMask)
                                                 throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        float versionFloat = productMetadata.getVersion();
        Set<String> addedFiles = new HashSet<>();
        if (muscateMask.nature.equals(MuscateMask.AOT_INTERPOLATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeAOTMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETAILED_CLOUD_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    computeCloudMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_MASK_VERSION) {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    computeCloudMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Cloud, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.CLOUD_SHADOW_MASK)) {
            if (versionFloat < MuscateMask.CLOUD_SHADOW_MASK_VERSION) {
                // in some old products the Nature is Cloud_Shadow instead of Geophysics. Perhaps an error?
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    computeGeophysicsMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            } else {
                for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                    addedFiles.add(muscateMaskFile.path);
                    computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Cloud_Shadow, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.EDGE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    computeEdgeMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.SATURATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    computeSaturationMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.GEOPHYSICS_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    computeGeophysicsMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DETECTOR_FOOTPRINT_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add( muscateMaskFile.path)) {
                    computeDetectorFootprintMaskNames( muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.DEFECTIVE_PIXEL_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                if (addedFiles.add(muscateMaskFile.path)) {
                    computeDefectivePixelMaskNames(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
                }
            }
        } else if (muscateMask.nature.equals(MuscateMask.HIDDEN_SURFACE_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Hidden_Surface, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.SNOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Snow, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.SUN_TOO_LOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Sun_Too_Low, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.TANGENT_SUN_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Tangent_Sun, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.TOPOGRAPHY_SHADOW_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Topography_Shadow, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.WATER_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeGeophysicsMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, MuscateConstants.GEOPHYSICAL_BIT.Water, metadata);
            }
        } else if (muscateMask.nature.equals(MuscateMask.WVC_INTERPOLATION_MASK)) {
            for (MuscateMaskFile muscateMaskFile : muscateMask.getMaskFiles()) {
                addedFiles.add(muscateMaskFile.path);
                computeWVCMaskName(muscateMaskFile.path, filePathsHelper, productDirectory, productMetadata, metadata);
            }
        }
    }

    private static void computeAOTMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata produMuscateMetadata, Metadata metadata)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, produMuscateMetadata);
        if (geoposition != null) {
            metadata.getMaskList().add(MuscateProductReader.computeAOTMaskName(geoposition));
            String bandName = "Aux_IA_" + geoposition.id;
            if(!metadata.getBandList().contains(bandName)){
                metadata.getBandList().add(bandName);
            }
        }
    }

    private static void computeWVCMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                             throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            metadata.getMaskList().add(MuscateProductReader.computeWVCMaskName(geoposition));
            String bandName = "Aux_IA_" + geoposition.id;
            if(!metadata.getBandList().contains(bandName)){
                metadata.getBandList().add(bandName);
            }
        }
    }

    private static void computeDefectivePixelMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                                               throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            String[] orderedBandNames = productMetadata.getOrderedBandNames(geoposition.id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                metadata.getMaskList().add(MuscateProductReader.computeDefectivePixelMaskName(orderedBandNames[i]));
            }
            String bandName = String.format("Aux_Mask_Defective_Pixel_%s", geoposition.id);
            metadata.getBandList().add(bandName);
        }
    }

    private static void computeDetectorFootprintMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                                                  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            String[] orderedBandNames = productMetadata.getOrderedBandNames(geoposition.id);
            for (int i = 0; i < orderedBandNames.length; i++) {
                String maskName = MuscateProductReader.computeDetectorFootprintMaskName(tiffImageRelativeFilePath, orderedBandNames[i]);
                metadata.getMaskList().add(maskName);
            }
            int detector = MuscateProductReader.getDetectorFromFilename(tiffImageRelativeFilePath);
            String bandName = String.format("Aux_Mask_Detector_Footprint_%s_%02d", geoposition.id, detector);
            metadata.getBandList().add(bandName);
        }
    }

    private static void computeSaturationMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                                           throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            List<String> bands = productMetadata.getBandNames(geoposition.id);
            for (int bitCount=0; bitCount<bands.size(); bitCount++) {
                String bandId = bands.get(bitCount);
                metadata.getMaskList().add(MuscateProductReader.computeSaturationMaskName(bandId));
            }
            String bandName = "Aux_Mask_Saturation_" + geoposition.id;
            metadata.getBandList().add(bandName);
        }
    }

    private static void computeEdgeMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                              throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            metadata.getMaskList().add(MuscateProductReader.computeEdgeMaskName(geoposition));
            String bandName = "Aux_Mask_Edge_" + geoposition.id;
            metadata.getBandList().add(bandName);
        }
    }

    private static void computeGeophysicsMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMedatada, Metadata metadata)
                                                           throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        for (MuscateConstants.GEOPHYSICAL_BIT geophysical_bit : MuscateConstants.GEOPHYSICAL_BIT.values()) {
            computeGeophysicsMaskName(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMedatada, geophysical_bit, metadata);
        }
    }

    private static void computeGeophysicsMaskName(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory,
                                                    MuscateMetadata productMetadata, MuscateConstants.GEOPHYSICAL_BIT geophysicalBit, Metadata metadata)
                                                    throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            String maskName = MuscateProductReader.computeGeographicMaskName(geophysicalBit, geoposition);
            metadata.getMaskList().add(maskName);
            String bandName = "Aux_Mask_MG2_" + geoposition.id;
            if(!metadata.getBandList().contains(bandName)){
                metadata.getBandList().add(bandName);
            }
        }
    }

    private static void computeCloudMaskNames(String tiffImageRelativeFilePath, ProductFilePathsHelper filePathsHelper, VirtualDirEx productDirectory, MuscateMetadata productMetadata, Metadata metadata)
                                                      throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        MuscateMetadata.Geoposition geoposition = findGeoPosition(tiffImageRelativeFilePath, filePathsHelper, productDirectory, productMetadata);
        if (geoposition != null) {
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskAllName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskAllCloudName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskReflectanceName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskReflectanceVarianceName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskExtensionName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskInsideShadowName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskOutsideShadowName(geoposition));
            metadata.getMaskList().add(MuscateProductReader.computeCloudMaskCirrusName(geoposition));
            String bandName = "Aux_Mask_Cloud_" + geoposition.id;
            if(!metadata.getBandList().contains(bandName)){
                metadata.getBandList().add(bandName);
            }
        }
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

        String tiffImageFilePath = filePathsHelper.computeImageRelativeFilePath(tiffImageRelativeFilePath);
        try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(productDirectory.getBaseFile().toPath(), tiffImageFilePath)) {
            int defaultBandWidth = geoTiffImageReader.getImageWidth();
            int defaultBandHeight = geoTiffImageReader.getImageHeight();
            return metadata.getGeoposition(defaultBandWidth, defaultBandHeight);
        }
    }

    private static void addAngleBands(Metadata metadata, MuscateMetadata productMetadata){
        // add Zenith
        metadata.getBandList().add("sun_zenith");
        // add Azimuth
        metadata.getBandList().add("sun_azimuth");
        // viewing angles
        for (String bandId : productMetadata.getBandNames()) {
            MuscateMetadata.AnglesGrid anglesGrid = productMetadata.getViewingAnglesGrid(bandId);
            // add Zenith
            metadata.getBandList().add("view_zenith_" + anglesGrid.getBandId());
            // add Azimuth
            metadata.getBandList().add("view_azimuth_" + anglesGrid.getBandId());
        }
        // add mean angles
        MuscateMetadata.AnglesGrid meanViewingAnglesGrid = productMetadata.getMeanViewingAnglesGrid();
        if (meanViewingAnglesGrid != null) {
            // add Zenith
            metadata.getBandList().add("view_zenith_mean");
            // add Azimuth
            metadata.getBandList().add("view_azimuth_mean");
        }
    }
}
