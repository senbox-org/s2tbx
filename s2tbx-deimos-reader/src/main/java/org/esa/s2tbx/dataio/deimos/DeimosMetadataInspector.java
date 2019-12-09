package org.esa.s2tbx.dataio.deimos;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosConstants;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosMetadata;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.TiePointGeoCoding;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by jcoravu on 9/12/2019.
 */
public class DeimosMetadataInspector implements MetadataInspector {

    public DeimosMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            List<DeimosMetadata> deimosMetadata = DeimosProductReader.readMetadata(productDirectory);

            Dimension defaultProductSize = DeimosProductReader.computeMaximumProductSize(deimosMetadata);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(defaultProductSize.width);
            metadata.setProductHeight(defaultProductSize.height);
            for (int i = 0; i < deimosMetadata.size(); i++) {
                DeimosMetadata currentMetadata = deimosMetadata.get(i);
                if (DeimosConstants.PROCESSING_1R.equals(currentMetadata.getProcessingLevel())) {
                    TiePointGeoCoding productGeoCoding = DeimosProductReader.buildTiePointGridGeoCoding(deimosMetadata.get(0));
                    metadata.setGeoCoding(productGeoCoding);
                }

                String[] bandNames = currentMetadata.getBandNames();
                String bandPrefix = DeimosProductReader.computeBandPrefix(deimosMetadata.size(), i);
                for (int k = 0; k < bandNames.length; k++) {
                    String bandName = bandPrefix + bandNames[k];
                    metadata.getBandList().add(bandName);
                }
            }

            metadata.getMaskList().add(DeimosConstants.NODATA_VALUE);
            metadata.getMaskList().add(DeimosConstants.SATURATED_VALUE);
            metadata.setHasMasks(true);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
