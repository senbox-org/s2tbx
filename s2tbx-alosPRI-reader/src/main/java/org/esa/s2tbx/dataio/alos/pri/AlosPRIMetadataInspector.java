package org.esa.s2tbx.dataio.alos.pri;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIConstants;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alos.pri.internal.ImageMetadata;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;

import java.awt.*;
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
            Path imagesMetadataParentPath = AlosPRIProductReader.buildImagesMetadataParentPath(productDirectory, metadataFileName);
            AlosPRIMetadata alosPriMetadata = AlosPRIProductReader.readMetadata(productDirectory, metadataFileName, imagesMetadataParentPath);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(alosPriMetadata.getRasterWidth());
            metadata.setProductHeight(alosPriMetadata.getRasterHeight());
            Dimension defaultProductSize = new Dimension(metadata.getProductWidth(), metadata.getProductHeight());
            GeoCoding productGeoCoding = AlosPRIProductReader.buildGeoCoding(alosPriMetadata, defaultProductSize, null, null);
            metadata.setGeoCoding(productGeoCoding);

            for (int bandIndex = 0; bandIndex < alosPriMetadata.getImageMetadataList().size(); bandIndex++) {
                ImageMetadata imageMetadata = alosPriMetadata.getImageMetadataList().get(bandIndex);
                metadata.getBandList().add(imageMetadata.getBandName());
            }

            metadata.getMaskList().add(AlosPRIConstants.NODATA);
            metadata.getMaskList().add(AlosPRIConstants.SATURATED);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
