package org.esa.s2tbx.dataio.worldview2esa;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadataList;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.GeoCoding;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 6/1/2020.
 */
public class WorldView2ESAMetadataInspector implements MetadataInspector {

    public WorldView2ESAMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            String metadataFileName = WorldView2ESAProductReader.buildMetadataFileName(productDirectory);
            Path imagesMetadataParentPath = WorldView2ESAProductReader.buildImagesMetadataParentPath(productDirectory, metadataFileName);
            TileMetadataList tileMetadataList = WorldView2ESAProductReader.readTileMetadataList(imagesMetadataParentPath);

            Dimension defaultProductSize = tileMetadataList.computeDefaultProductSize();
            if (defaultProductSize == null) {
                throw new NullPointerException("The product default size is null.");
            }

            Metadata metadata = new Metadata(defaultProductSize.width, defaultProductSize.height);

            GeoCoding productGeoCoding = tileMetadataList.buildProductGeoCoding(null);
            metadata.setGeoCoding(productGeoCoding);

            java.util.List<TileMetadata> tilesMetadata = tileMetadataList.getTiles();
            for (TileMetadata tileMetadata : tilesMetadata) {
                String[] bandNames = tileMetadataList.computeBandNames(tileMetadata);
                for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                    metadata.getBandList().add(bandNames[bandIndex]);
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
