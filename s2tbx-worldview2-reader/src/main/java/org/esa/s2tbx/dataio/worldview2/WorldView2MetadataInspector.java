package org.esa.s2tbx.dataio.worldview2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.worldview2.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2.metadata.TileMetadataList;
import org.esa.s2tbx.dataio.worldview2.metadata.WorldView2Metadata;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.GeoCoding;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by jcoravu on 7/1/2020.
 */
public class WorldView2MetadataInspector implements MetadataInspector {

    public WorldView2MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, false)) {
            WorldView2Metadata worldView2Metadata = WorldView2ProductReader.readMetadata(productDirectory);
            int subProductCount = worldView2Metadata.getProducts().size();
            if (subProductCount == 0) {
                throw new IllegalStateException("The product is empty.");
            }

            Dimension defaultProductSize = worldView2Metadata.computeDefaultProductSize();
            if (defaultProductSize == null) {
                throw new NullPointerException("The product default size is null.");
            }

            Metadata metadata = new Metadata(defaultProductSize.width, defaultProductSize.height);

            GeoCoding productGeoCoding = worldView2Metadata.buildProductGeoCoding(null);
            metadata.setGeoCoding(productGeoCoding);

            String bandPrefix = "";
            for (Map.Entry<String, TileMetadataList> entry : worldView2Metadata.getProducts().entrySet()) {
                String subProductName = entry.getKey();
                if (subProductCount > 1) {
                    bandPrefix = subProductName + "_";
                }

                TileMetadataList tileMetadataList = entry.getValue();
                java.util.List<TileMetadata> tiles = tileMetadataList.getTiles();

                for (TileMetadata tileMetadata : tiles) {
                    String[] bandNames = tileMetadataList.computeBandNames(tileMetadata);
                    for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                        String bandName = bandPrefix + bandNames[bandIndex];
                        metadata.getBandList().add(bandName);
                    }
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
