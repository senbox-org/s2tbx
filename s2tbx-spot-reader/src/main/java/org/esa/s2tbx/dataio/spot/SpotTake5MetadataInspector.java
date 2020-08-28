package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotTake5Metadata;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jcoravu on 10/12/2019.
 */
public class SpotTake5MetadataInspector implements MetadataInspector {

    public SpotTake5MetadataInspector() {
    }

    @Override
    public MetadataInspector.Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            SpotTake5Metadata spotImageMetadata = SpotTake5ProductReader.readImageMetadata(productPath, productDirectory);

            MetadataInspector.Metadata metadata = new MetadataInspector.Metadata();
            metadata.setProductWidth(spotImageMetadata.getRasterWidth());
            metadata.setProductHeight(spotImageMetadata.getRasterHeight());

            // all the bands of the tiff files are added to the product
            Set<String> uniqueBandNames = new HashSet<>();
            Map<String, String> tiffFiles = spotImageMetadata.getTiffFiles();
            List<String> sortedKeys = new ArrayList<>(tiffFiles.keySet());
            Collections.sort(sortedKeys);
            String[] bandNames = spotImageMetadata.getBandNames();
            for (int i = sortedKeys.size() - 1; i >= 0; i--) {
                String key = sortedKeys.get(i);
                String tiffFile = spotImageMetadata.getMetaSubFolder() + tiffFiles.get(key);
                String bandNamePrefix = sortedKeys.get(i) + "_";
                File rasterFile = productDirectory.getFile(tiffFile);
                Product geoTiffProduct = GeoTiffProductReader.readMetadataProduct(rasterFile.toPath(), false); // 'false' => do not read the geo coding
                for (int bandIndex = 0; bandIndex < geoTiffProduct.getNumBands(); bandIndex++) {
                    String bandName = (bandIndex < bandNames.length) ? bandNames[bandIndex] : (SpotConstants.DEFAULT_BAND_NAME_PREFIX + bandIndex);
                    if (!uniqueBandNames.add(bandName)) {
                        bandName = bandNamePrefix + bandName; // the product contains already the band
                    }
                    metadata.getBandList().add(bandName);
                }
            }
            String key = sortedKeys.get(sortedKeys.size() - 1);
            String tiffFile = spotImageMetadata.getMetaSubFolder() + tiffFiles.get(key);
            File rasterFile = productDirectory.getFile(tiffFile);
            GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(rasterFile.toPath());
            GeoCoding geoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
            metadata.setGeoCoding(geoCoding);

            // for each mask found in the metadata, the first band of the mask is added to the product, in order to create the masks
            for (Map.Entry<String, String> entry : spotImageMetadata.getMaskFiles().entrySet()) {
                String bandName = entry.getKey();
                metadata.getBandList().add(bandName);
            }

            // saturated flags & masks
            if (spotImageMetadata.getMaskFiles().keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_SATURATION)) {
                FlagCoding saturatedFlagCoding = SpotTake5ProductReader.createSaturatedFlagCoding();
                for (String flagName : saturatedFlagCoding.getFlagNames()) {
                    metadata.getMaskList().add(flagName);
                }
            }

            // clouds flags & masks
            if (spotImageMetadata.getMaskFiles().keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS)) {
                FlagCoding cloudsFlagCoding = SpotTake5ProductReader.createCloudsFlagCoding();
                for (String flagName : cloudsFlagCoding.getFlagNames()) {
                    metadata.getMaskList().add(flagName);
                }
            }

            // diverse flags & masks
            if (spotImageMetadata.getMaskFiles().keySet().contains(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE)) {
                FlagCoding diverseFlagCoding = SpotTake5ProductReader.createDiverseFlagCoding();
                for (String flagName : diverseFlagCoding.getFlagNames()) {
                    metadata.getMaskList().add(flagName);
                }
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
