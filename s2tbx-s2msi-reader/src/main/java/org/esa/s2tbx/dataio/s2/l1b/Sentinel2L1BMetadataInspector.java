package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.l1b.metadata.L1bMetadata;
import org.esa.s2tbx.dataio.s2.l1b.metadata.L1bProductMetadataReader;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.IndexCoding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class Sentinel2L1BMetadataInspector implements MetadataInspector {

    private final Sentinel2L1BProductReader.ProductInterpretation interpretation;

    public Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation interpretation) {
        this.interpretation = interpretation;
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        VirtualPath virtualPath = null;
        try {
            Path inputPath = S2ProductNamingUtils.processInputPath(productPath);
            virtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);

            L1bProductMetadataReader productMetadataReader = new L1bProductMetadataReader(virtualPath);
            VirtualPath inputVirtualPath = productMetadataReader.getNamingConvention().getInputXml();
            if (inputVirtualPath.exists()) {
                S2Config config = productMetadataReader.readTileLayouts(inputVirtualPath);
                if (config == null) {
                    throw new NullPointerException(String.format("Unable to retrieve the JPEG tile layout associated to product [%s]", inputVirtualPath.getFileName().toString()));
                }
                L1bMetadata l1bMetadataHeader = productMetadataReader.readMetadataHeader(inputVirtualPath, config);
                S2SpatialResolution productResolution = S2SpatialResolution.R10M;
                if (this.interpretation == Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_20M) {
                    productResolution = S2SpatialResolution.R20M;
                } else if (this.interpretation == Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_60M) {
                    productResolution = S2SpatialResolution.R60M;
                }

                L1bSceneDescription sceneDescription = L1bSceneDescription.create(l1bMetadataHeader, productResolution);

                Metadata metadata = new Metadata();
                metadata.setProductWidth(sceneDescription.getSceneRectangle().width);
                metadata.setProductHeight(sceneDescription.getSceneRectangle().height);

                List<L1bMetadata.Tile> tileList = l1bMetadataHeader.computeTiles();
                Map<String, Sentinel2L1BProductReader.L1BBandInfo> bandInfoByKey = l1bMetadataHeader.computeBandInfoByKey(tileList);

                List<String> bandIndexes = new ArrayList<>(bandInfoByKey.keySet());
                Collections.sort(bandIndexes);

                for (String bandIndex : bandIndexes) {
                    Sentinel2L1BProductReader.L1BBandInfo tileBandInfo = bandInfoByKey.get(bandIndex);
                    if (isMultiResolution() || tileBandInfo.getBandInformation().getResolution() == productResolution) {
                        metadata.getBandList().add(tileBandInfo.getBandName());
                    }
                }

                if (sceneDescription.getOrderedTileIds().size() > 1 && !bandInfoByKey.isEmpty()) {
                    List<S2SpatialResolution> resolutions = Sentinel2L1BProductReader.computeResolutions(this.interpretation);
                    if (!(resolutions.isEmpty() || tileList.isEmpty())) {
                        List<Sentinel2L1BProductReader.L1BBandInfo> tileInfoList = Sentinel2L1BProductReader.computeTileIndexesList(resolutions, tileList, sceneDescription, config);
                        if (tileInfoList.size() > 0) {
                            // add the index bands
                            for (Sentinel2L1BProductReader.L1BBandInfo bandInfo : tileInfoList) {
                                if (isMultiResolution() || bandInfo.getBandInformation().getResolution() == productResolution) {
                                    metadata.getBandList().add(bandInfo.getBandInformation().getPhysicalBand());
                                }
                            }

                            // add the index masks
                            for (Sentinel2L1BProductReader.L1BBandInfo bandInfo : tileInfoList) {
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
                }

                return metadata;
            } else {
                throw new FileNotFoundException(inputVirtualPath.getFullPathString());
            }
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

    private boolean isMultiResolution() {
        return this.interpretation == Sentinel2L1BProductReader.ProductInterpretation.RESOLUTION_MULTI;
    }
}
