package org.esa.s2tbx.dataio.s2.ortho.metadata;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2IndexBandInformation;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2ProductNamingUtils;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.l1c.metadata.S2L1cProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l2a.metadata.S2L2aProductMetadataReader;
import org.esa.s2tbx.dataio.s2.l3.metadata.S2L3ProductMetadataReader;
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.IndexCoding;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denisa Stefanescu
 */
public class Sentinel2OrthoMetadataInspector implements MetadataInspector {
    protected Logger logger = Logger.getLogger(getClass().getName());

    private final S2Config.Sentinel2ProductLevel productLevel;
    private final String epsg;

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
            } else if (productLevel == S2Config.Sentinel2ProductLevel.L1C) {
                productMetadataReader = new S2L1cProductMetadataReader(virtualPath, epsg);
            } else if (productLevel == S2Config.Sentinel2ProductLevel.L3) {
                productMetadataReader = new S2L3ProductMetadataReader(virtualPath, epsg);
            }
            VirtualPath inputVirtualPath = productMetadataReader.getNamingConvention().getInputXml();
            S2Config config = productMetadataReader.readTileLayouts(inputVirtualPath);
            S2OrthoMetadata metadataHeader = (S2OrthoMetadata)productMetadataReader.readMetadataHeader(inputVirtualPath, config);
            S2OrthoSceneLayout sceneDescription = S2OrthoSceneLayout.create(metadataHeader);
            List<S2Metadata.Tile> tileList = metadataHeader.getTileList();

            Metadata metadata = new Metadata();
            metadata.setProductWidth(sceneDescription.getSceneDimension(productResolution).width);
            metadata.setProductHeight(sceneDescription.getSceneDimension(productResolution).height);
            metadataHeader.addStaticAngleBands(metadata);
            List<Sentinel2ProductReader.BandInfo> bandInfoList = metadataHeader.computeBandInfoByKey(tileList);
            for (S2Metadata.Tile tile : tileList) {
                metadataHeader.addAnglesBands(tile,metadata);
            }
            for (Sentinel2ProductReader.BandInfo bandInfo : bandInfoList) {
                metadata.getBandList().add(bandInfo.getBandName());
                if (bandInfo.getBandInformation() instanceof S2IndexBandInformation) {
                    S2IndexBandInformation indexBandInformation = (S2IndexBandInformation) bandInfo.getBandInformation();
                    IndexCoding indexCoding = indexBandInformation.getIndexCoding();
                    Arrays.stream(indexCoding.getIndexNames()).forEach(index -> metadata.getMaskList().add(indexBandInformation.getPrefix() + index.toLowerCase()));
                }
            }
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
}
