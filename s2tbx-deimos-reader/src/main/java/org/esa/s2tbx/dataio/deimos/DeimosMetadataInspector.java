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
            List<DeimosMetadata> metadataList = DeimosProductReader.readMetadata(productDirectory);

            Dimension defaultProductSize = DeimosProductReader.computeMaximumProductSize(metadataList);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(defaultProductSize.width);
            metadata.setProductHeight(defaultProductSize.height);

            TiePointGeoCoding productGeoCoding = DeimosProductReader.buildProductTiePointGridGeoCoding(metadataList.get(0), metadataList);
            metadata.setGeoCoding(productGeoCoding);

            for (int i = 0; i < metadataList.size(); i++) {
                DeimosMetadata currentMetadata = metadataList.get(i);
                String[] bandNames = currentMetadata.getBandNames();
                String bandPrefix = DeimosProductReader.computeBandPrefix(metadataList.size(), i);
                for (int k = 0; k < bandNames.length; k++) {
                    String bandName = bandPrefix + bandNames[k];
                    metadata.getBandList().add(bandName);
                }
            }

            metadata.getMaskList().add(DeimosConstants.NODATA_VALUE);
            metadata.getMaskList().add(DeimosConstants.SATURATED_VALUE);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
