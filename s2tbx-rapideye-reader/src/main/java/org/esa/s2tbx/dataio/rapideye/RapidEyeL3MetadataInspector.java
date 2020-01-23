package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.s2tbx.dataio.readers.MetadataList;
import org.esa.s2tbx.dataio.readers.RastersMetadata;
import org.esa.snap.core.metadata.MetadataInspector;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 9/12/2019.
 */
public class RapidEyeL3MetadataInspector implements MetadataInspector {

    public RapidEyeL3MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            MetadataList<RapidEyeMetadata> metadataList = RapidEyeL3Reader.readMetadata(productDirectory);

            RastersMetadata rastersMetadata = RapidEyeL3Reader.computeMaximumDefaultProductSize(metadataList, productDirectory);

            Metadata metadata = new Metadata(rastersMetadata.getMaximumWidh(), rastersMetadata.getMaximumHeight());

            for (int i = 0; i < metadataList.getCount(); i++) {
                String[] bandNames = RapidEyeConstants.BAND_NAMES;
                String bandPrefix = RapidEyeL3Reader.computeBandPrefix(metadataList.getCount(), i);
                for (int k = 0; k < bandNames.length; k++) {
                    String bandName = bandPrefix + bandNames[k];
                    metadata.addBandName(bandName);
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
