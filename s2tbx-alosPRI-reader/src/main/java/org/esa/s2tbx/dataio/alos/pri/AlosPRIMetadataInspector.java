package org.esa.s2tbx.dataio.alos.pri;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alos.pri.internal.ImageMetadata;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.TiePointGeoCoding;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 2/12/2019.
 */
public class AlosPRIMetadataInspector implements MetadataInspector {

    public AlosPRIMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            String metadataFileName = AlosPRIProductReader.buildMetadataFileName(productDirectory);
            Path zipArchivePath = AlosPRIProductReader.buildZipArchivePath(productDirectory, metadataFileName);
            AlosPRIMetadata alosPriMetadata = AlosPRIProductReader.readMetadata(productDirectory, metadataFileName, zipArchivePath);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(alosPriMetadata.getRasterWidth());
            metadata.setProductHeight(alosPriMetadata.getRasterHeight());

            TiePointGeoCoding productGeoCoding = AlosPRIProductReader.buildTiePointGridGeoCoding(alosPriMetadata, metadata.getProductWidth(), metadata.getProductHeight(), null);
            metadata.setGeoCoding(productGeoCoding);

            for (int bandIndex = 0; bandIndex < alosPriMetadata.getImageMetadataList().size(); bandIndex++) {
                ImageMetadata imageMetadata = alosPriMetadata.getImageMetadataList().get(bandIndex);
                metadata.getBandList().add(imageMetadata.getBandName());
            }

            return metadata;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
