package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.snap.core.metadata.MetadataInspector;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by jcoravu on 9/12/2019.
 */
public class RapidEyeL3MetadataInspector implements MetadataInspector {

    public RapidEyeL3MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            List<RapidEyeMetadata> metadataList = RapidEyeL3Reader.readMetadata(productDirectory);

            Dimension defaultProductSize = RapidEyeL3Reader.computeMaximumProductSize(metadataList);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(defaultProductSize.width);
            metadata.setProductHeight(defaultProductSize.height);

            for (int i = 0; i < metadataList.size(); i++) {
                String[] bandNames = RapidEyeConstants.BAND_NAMES;
                String bandPrefix = RapidEyeL3Reader.computeBandPrefix(metadataList.size(), i);
                for (int k = 0; k < bandNames.length; k++) {
                    String bandName = bandPrefix + bandNames[k];
                    metadata.getBandList().add(bandName);
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
