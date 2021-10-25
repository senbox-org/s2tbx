package org.esa.s2tbx.dataio.s2.ortho.metadata;

import org.esa.s2tbx.dataio.gdal.drivers.Band;
import org.esa.s2tbx.dataio.s2.ColorIterator;
import org.esa.s2tbx.dataio.s2.S2BandConstants;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2ProductNamingUtils;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader.BandInfo;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.gml.EopPolygon;
import org.esa.s2tbx.dataio.s2.l1c.metadata.S2L1cProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l2a.metadata.S2L2aProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l2hf.l2f.metadata.S2L2fProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l2hf.l2h.metadata.S2L2hProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l3.metadata.S2L3ProductMetadataReader;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo148;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoUtils;
import org.esa.s2tbx.dataio.s2.ortho.Sentinel2OrthoProductReader;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Placemark;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2OrthoMetadataInspector implements MetadataInspector {
    protected Logger logger = Logger.getLogger(getClass().getName());

    public static final String VIEW_ZENITH_PREFIX = "view_zenith";
    public static final String VIEW_AZIMUTH_PREFIX = "view_azimuth";
    public static final String SUN_ZENITH_PREFIX = "sun_zenith";
    public static final String SUN_AZIMUTH_PREFIX = "sun_azimuth";

    private final S2Config.Sentinel2ProductLevel productLevel;
    private final String epsg;
    private int maskLevel;

    public Sentinel2OrthoMetadataInspector(S2Config.Sentinel2ProductLevel level, String epsg) {
        this.productLevel = level;
        this.epsg = epsg;
    }

    public Metadata getMetadata(Path productPath) throws IOException {
        VirtualPath virtualPath = null;
        try {
            Path inputPath = S2ProductNamingUtils.processInputPath(productPath);
            virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
            INamingConvention namingConvention = NamingConventionFactory.createNamingConvention(virtualPath);
            AbstractS2OrthoMetadataReader productMetadataReader = null;
            S2SpatialResolution productResolution = namingConvention.getResolution();
            if (productLevel == S2Config.Sentinel2ProductLevel.L2A) {
                productMetadataReader = new S2L2aProductMetadataReader(virtualPath, epsg);
                this.maskLevel = MaskInfo.L2A;
            }else if (productLevel == S2Config.Sentinel2ProductLevel.L2H) {
                productMetadataReader = new S2L2hProductMetadataReader(virtualPath, epsg);
                this.maskLevel = MaskInfo.L2A;
            }else if (productLevel == S2Config.Sentinel2ProductLevel.L2F) {
                productMetadataReader = new S2L2fProductMetadataReader(virtualPath, epsg);
                this.maskLevel = MaskInfo.L2A;
            } else if (productLevel == S2Config.Sentinel2ProductLevel.L1C) {
                productMetadataReader = new S2L1cProductMetadataReader(virtualPath, epsg);
                this.maskLevel = MaskInfo.L1C;
            } else if (productLevel == S2Config.Sentinel2ProductLevel.L3) {
                productMetadataReader = new S2L3ProductMetadataReader(virtualPath, epsg);
                this.maskLevel = MaskInfo.L3;
            }
            VirtualPath inputVirtualPath = productMetadataReader.getNamingConvention().getInputXml();
            S2Config config = productMetadataReader.readTileLayouts(inputVirtualPath);
            S2OrthoMetadata metadataHeader = (S2OrthoMetadata) productMetadataReader.readMetadataHeader(inputVirtualPath, config);
            S2OrthoSceneLayout sceneDescription = S2OrthoSceneLayout.create(metadataHeader);
            List<S2Metadata.Tile> tileList = metadataHeader.getTileList();

            Metadata metadata = new Metadata();
            metadata.setProductWidth(sceneDescription.getSceneDimension(productResolution).width);
            metadata.setProductHeight(sceneDescription.getSceneDimension(productResolution).height);
            Dimension defaultProductSize = new Dimension(sceneDescription.getSceneDimension(productResolution).width, sceneDescription.getSceneDimension(productResolution).height);
            addStaticAngleBands(metadata);
            List<Sentinel2ProductReader.BandInfo> bandInfoList = metadataHeader.computeBandInfoByKey(tileList);
            for (S2Metadata.Tile tile : tileList) {
                addAnglesBands(tile, metadata);
            }
            for (Sentinel2ProductReader.BandInfo bandInfo : bandInfoList) {
                metadata.getBandList().add(bandInfo.getBandName());
                if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                    S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                    IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                    Arrays.stream(indexCoding.getIndexNames()).forEach(index -> metadata.getMaskList().add(indexBandInformation.getPrefix() + index.toLowerCase()));
                }
            }
            if (MaskInfo.values() != null) {
                for (MaskInfo maskInfo : MaskInfo.values()) {
                    if (!maskInfo.isPresentAtLevel(maskLevel))
                        continue;
                    if (!maskInfo.isEnabled())
                        continue;
                    if (!maskInfo.isPerBand()) {
                        // cloud masks are provided once and valid for all bands
                        addVectorMask(tileList, maskInfo, null, metadata);
                    } else {
                        // for other masks, we have one mask instance for each spectral band
                        for (Sentinel2ProductReader.BandInfo bandInfo : bandInfoList) {
                            if (bandInfo.getBandInformation() instanceof S2SpectralInformation) {
                                addVectorMask(tileList, maskInfo, (S2SpectralInformation) bandInfo.getBandInformation(), metadata);
                            }
                        }
                    }
                }
            }
            if(MaskInfo148.values() != null) {
                for (MaskInfo148 maskInfo : MaskInfo148.values()) {
                    if (!maskInfo.isPresentAtLevel(maskLevel))
                        continue;
                    if (!maskInfo.isEnabled())
                        continue;
                    if (!maskInfo.isPerBand()) {
                        // cloud masks are provided once and valid for all bands
                        addRasterMask(tileList, maskInfo,  null, null, bandInfoList, metadata);
                    } else {
                        // for other masks, we have one mask instance for each spectral band
                        for (Sentinel2ProductReader.BandInfo bandInfo : bandInfoList) {
                            if (bandInfo.getBandInformation() instanceof S2SpectralInformation) {
                                addRasterMask(tileList, maskInfo, (S2SpectralInformation) bandInfo.getBandInformation(), bandInfo, bandInfoList, metadata);
                            }
                        }
                    }
                }
            }
            addIndexMasks(bandInfoList, metadata);
            GeoCoding geoCoding = Sentinel2OrthoProductReader.buildGeoCoding(sceneDescription, CRS.decode(epsg), productResolution.resolution, productResolution.resolution, defaultProductSize, null);
            metadata.setGeoCoding(geoCoding);
            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (virtualPath != null) {
                virtualPath.close();
            }
        }
    }

    private static void addAnglesBands(S2Metadata.Tile tile, MetadataInspector.Metadata metadata) {
        Arrays.stream(tile.getViewingIncidenceAnglesGrids()).forEach(tileInfo -> metadata.getBandList().add(VIEW_AZIMUTH_PREFIX + "_" + S2BandConstants.getBand(tileInfo.getBandId())));
        Arrays.stream(tile.getViewingIncidenceAnglesGrids()).forEach(tileInfo -> metadata.getBandList().add(VIEW_ZENITH_PREFIX + "_" + S2BandConstants.getBand(tileInfo.getBandId())));
    }

    private static void addStaticAngleBands(MetadataInspector.Metadata metadata) {
        metadata.getBandList().add(VIEW_ZENITH_PREFIX + "_mean");
        metadata.getBandList().add(VIEW_AZIMUTH_PREFIX + "_mean");
        metadata.getBandList().add(SUN_AZIMUTH_PREFIX);
        metadata.getBandList().add(SUN_ZENITH_PREFIX);
    }


    private void addVectorMask(List<S2Metadata.Tile> tileList, MaskInfo maskInfo, S2SpectralInformation spectralInfo, MetadataInspector.Metadata metadata) {
        List<EopPolygon>[] productPolygons = new List[maskInfo.getSubType().length];
        for (int i = 0; i < maskInfo.getSubType().length; i++) {
            productPolygons[i] = new ArrayList<>();
        }


        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {

            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {

                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (!maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        continue;
                    }
                }

                maskFilesFound = true;

                List<EopPolygon> polygonsForTile;

                polygonsForTile = S2OrthoUtils.readPolygons(maskFilename.getPath());

                for (int i = 0; i < maskInfo.getSubType().length; i++) {
                    final int pos = i;
                    productPolygons[i].addAll(polygonsForTile.stream().filter(p -> p.getType().equals(maskInfo.getSubType()[pos])).collect(Collectors.toList()));
                }
            }
        }
        if (maskFilesFound) {
            for (int i = 0; i < maskInfo.getSubType().length; i++) {
                final SimpleFeatureType type = Placemark.createGeometryFeatureType();
                final DefaultFeatureCollection collection = S2OrthoUtils.createDefaultFeatureCollection(productPolygons, i, type);
                if (spectralInfo == null) {
                    // This mask is not specific to a band
                    // So we need one version of it for each resolution present in the band list
                    for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
                        if (!maskInfo.isPerPolygon()) {
                            String snapName = String.format("%s_%dm", maskInfo.getSnapName()[i], resolution.resolution);
                            metadata.getMaskList().add(snapName);
                        } else {
                            SimpleFeatureIterator simpleFeatureIterator = collection.features();
                            List<String> distictPolygonsOrdered = S2OrthoUtils.createDistictPolygonsOrdered(simpleFeatureIterator);
                            simpleFeatureIterator.close();

                            ColorIterator.reset();
                            for (String subId : distictPolygonsOrdered) {
                                String snapName = String.format("%s_%dm", subId, resolution.resolution);
                                metadata.getMaskList().add(snapName);
                            }
                        }
                    }
                } else {
                    // This mask is specific to a band
                    String bandName = spectralInfo.getPhysicalBand();

                    if (!maskInfo.isPerPolygon()) {
                        String snapName = maskInfo.getSnapNameForBand(bandName, i);
                        metadata.getMaskList().add(snapName);
                    } else {
                        SimpleFeatureIterator simpleFeatureIterator = collection.features();
                        List<String> distictPolygonsOrdered = S2OrthoUtils.createDistictPolygonsOrdered(simpleFeatureIterator);
                        simpleFeatureIterator.close();

                        ColorIterator.reset();
                        for (String subId : distictPolygonsOrdered) {
                            metadata.getMaskList().add(subId);
                        }
                    }
                }
            }
        }
    }

    private void addRasterMask(List<S2Metadata.Tile> tileList, MaskInfo148 maskInfo, S2SpectralInformation spectralInfo,
            BandInfo bandInfo,List<BandInfo> bandInfoList, MetadataInspector.Metadata metadata)
            throws IOException {

        VirtualPath maskPath = null;
        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {
            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {
                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        maskPath = maskFilename.getPath();
                        maskFilesFound = true;
                        break;
                    }
                } else {
                    maskPath = maskFilename.getPath();
                    maskFilesFound = true;
                    break;
                }

            }
        }

        if (maskPath == null || !maskFilesFound) {
            return;
        }

        if (spectralInfo == null) {
            for (int i = 0; i < maskInfo.getSubType().length; i++) {
                metadata.getMaskList().add(maskInfo.getSnapName(i));
            }
        } else {
            Band band = null;
            for (int i = 0; i < maskInfo.getSubType().length; i++) {
                // // This mask is specific to a band
                String bandName = spectralInfo.getPhysicalBand();
                String maskName = maskInfo.getSnapNameForBand(bandName, i);
                if(maskInfo.getMainType().contains("MSK_DETFOO"))
                    maskName = maskInfo.getSnapNameForDEFTOO(bandName, i);
                metadata.getMaskList().add(maskName);
            }
        }
    }

    private void addIndexMasks(List<Sentinel2ProductReader.BandInfo> bandInfoList, MetadataInspector.Metadata metadata) {
        for (Sentinel2ProductReader.BandInfo bandInfo : bandInfoList) {
            if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                for (String indexName : indexCoding.getIndexNames()) {
                    String maskName = indexBandInformation.getPrefix() + indexName.toLowerCase();
                    metadata.getMaskList().add(maskName);
                }
            }
        }

    }
}
