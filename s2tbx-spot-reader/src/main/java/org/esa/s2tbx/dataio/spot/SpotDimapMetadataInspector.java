package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotSceneMetadata;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 13/12/2019.
 */
public class SpotDimapMetadataInspector implements MetadataInspector {

    private static final Logger logger = Logger.getLogger(SpotDimapMetadataInspector.class.getName());

    public SpotDimapMetadataInspector() {
    }

    @Override
    public MetadataInspector.Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            SpotSceneMetadata spotMetadata = SpotSceneMetadata.create(productDirectory, logger);
            MetadataInspector.Metadata metadata;
            if (SpotDimapProductReader.isSingleVolumeMetadata(spotMetadata.getVolumeMetadata())) {
                metadata = readSingleVolumeMetadata(productDirectory, spotMetadata.getComponentMetadata(0));
            } else {
                metadata = readMultipleVolumeMetadata(productDirectory, spotMetadata, null);
            }
            metadata.getMaskList().add(SpotConstants.NODATA_VALUE);
            metadata.getMaskList().add(SpotConstants.SATURATED_VALUE);
            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private static MetadataInspector.Metadata readMultipleVolumeMetadata(VirtualDirEx productDirectory, SpotSceneMetadata spotMetadata, Rectangle subsetRegion) throws Exception {
        MetadataInspector.Metadata metadata = new MetadataInspector.Metadata();
        metadata.setProductWidth(spotMetadata.getExpectedVolumeWidth());
        metadata.setProductHeight(spotMetadata.getExpectedVolumeHeight());

        java.util.List<SpotDimapMetadata> componentMetadataList = spotMetadata.getComponentsMetadata();
        for (int fileIndex=0; fileIndex<componentMetadataList.size(); fileIndex++) {
            SpotDimapMetadata componentMetadata = componentMetadataList.get(fileIndex);
            String rasterFileName = SpotDimapProductReader.getTiffImageForMultipleVolume(componentMetadata);
            File rasterFile = productDirectory.getFile(rasterFileName);
            GeoCoding geoCoding = GeoTiffProductReader.readGeoCoding(rasterFile.toPath(), subsetRegion);
            if (geoCoding != null) {
                metadata.setGeoCoding(geoCoding);
                break;
            }
        }

        SpotDimapMetadata firstDimapMetadata = spotMetadata.getComponentsMetadata().get(0);
        String[] bandNames = firstDimapMetadata.getBandNames();
        for (int i = 0; i < bandNames.length; i++) {
            metadata.getBandList().add(bandNames[i]);
        }

        return metadata;
    }

    private static MetadataInspector.Metadata readSingleVolumeMetadata(VirtualDirEx productDirectory, SpotDimapMetadata dimapMetadata) throws Exception {
        MetadataInspector.Metadata metadata = new MetadataInspector.Metadata();
        metadata.setProductWidth(dimapMetadata.getRasterWidth());
        metadata.setProductHeight(dimapMetadata.getRasterHeight());

        String rasterFileName = SpotDimapProductReader.getTiffImageForSingleVolume(dimapMetadata);
        File rasterFile = productDirectory.getFile(rasterFileName);
        Product geoTiffProduct = GeoTiffProductReader.readMetadataProduct(rasterFile.toPath(), true); // 'true' => read the geo coding
        metadata.setGeoCoding(geoTiffProduct.getSceneGeoCoding());

        String[] bandNames = dimapMetadata.getBandNames();
        for (int bandIndex = 0; bandIndex < geoTiffProduct.getNumBands(); bandIndex++) {
            String bandName = (bandIndex < bandNames.length) ? bandNames[bandIndex] : (SpotConstants.DEFAULT_BAND_NAME_PREFIX + bandIndex);
            metadata.getBandList().add(bandName);
        }

        return metadata;
    }
}
