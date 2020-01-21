package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotViewMetadata;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.GeoCoding;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 11/12/2019.
 */
public class SpotViewMetadataInspector implements MetadataInspector {

    public SpotViewMetadataInspector() {
    }

    @Override
    public MetadataInspector.Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            SpotViewMetadata productMetadata = SpotViewProductReader.readProductMetadata(productDirectory);
            SpotDimapMetadata imageMetadata = SpotViewProductReader.readImageMetadata(productDirectory);

            MetadataInspector.Metadata metadata = new MetadataInspector.Metadata();
            metadata.setProductWidth(productMetadata.getRasterWidth());
            metadata.setProductHeight(productMetadata.getRasterHeight());

            GeoCoding geoCoding = SpotViewProductReader.buildTiePointGridGeoCoding(productMetadata, imageMetadata, null);
            if (geoCoding == null) {
                geoCoding = SpotViewProductReader.buildCrsGeoCoding(productMetadata.getRasterWidth(), productMetadata.getRasterHeight(), productMetadata, imageMetadata);
            }
            metadata.setGeoCoding(geoCoding);

            String[] bandNames = productMetadata.getBandNames();
            for (int i = 0; i < bandNames.length; i++) {
                metadata.getBandList().add(bandNames[i]);
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
