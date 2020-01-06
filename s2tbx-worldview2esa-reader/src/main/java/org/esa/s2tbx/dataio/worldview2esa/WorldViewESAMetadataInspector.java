package org.esa.s2tbx.dataio.worldview2esa;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadataList;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.GeoCoding;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 6/1/2020.
 */
public class WorldViewESAMetadataInspector implements MetadataInspector {

    public WorldViewESAMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            String metadataFileName = WorldView2ESAProductReader.buildMetadataFileName(productDirectory);
            Path imagesMetadataParentPath = WorldView2ESAProductReader.buildImagesMetadataParentPath(productDirectory, metadataFileName);
            TileMetadataList tileMetadataList = WorldView2ESAProductReader.readTileMetadataList(imagesMetadataParentPath);

            Dimension defaultProductSize = tileMetadataList.computeDefaultProductSize();

            Metadata metadata = new Metadata();
            metadata.setProductWidth(defaultProductSize.width);
            metadata.setProductHeight(defaultProductSize.height);

            GeoCoding productGeoCoding = WorldView2ESAProductReader.buildProductGeoCoding(tileMetadataList.getTiles());
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
