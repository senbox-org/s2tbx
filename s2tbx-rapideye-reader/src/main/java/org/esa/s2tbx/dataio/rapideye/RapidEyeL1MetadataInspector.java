package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.TiePointGeoCoding;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 9/12/2019.
 */
public class RapidEyeL1MetadataInspector implements MetadataInspector {

    public RapidEyeL1MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            RapidEyeMetadata productMetadata = RapidEyeL1Reader.readMetadata(productDirectory);

            Metadata metadata = new Metadata();
            metadata.setProductWidth(productMetadata.getRasterWidth());
            metadata.setProductHeight(productMetadata.getRasterHeight());

            TiePointGeoCoding productGeoCoding = RapidEyeL1Reader.buildTiePointGridGeoCoding(productMetadata, metadata.getProductWidth(), metadata.getProductHeight(), null);
            metadata.setGeoCoding(productGeoCoding);

            // add bands
            String[] nitfFiles = productMetadata.getRasterFileNames();
            if (nitfFiles != null) {
                for (int i = 0; i < nitfFiles.length; i++) {
                    String bandName = RapidEyeL1Reader.getBandName(i);
                    metadata.getBandList().add(bandName);
                }
            }

            // add masks
            String maskFileName = productMetadata.getMaskFileName();
            if (maskFileName != null) {
                metadata.getBandList().add(RapidEyeL1Reader.UNUSABLE_DATA_BAND_NAME);

                FlagCoding flagCoding = RapidEyeL1Reader.createFlagCoding();
                for (String flagName : flagCoding.getFlagNames()) {
                    metadata.getMaskList().add(flagName);
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
