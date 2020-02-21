package org.esa.s2tbx.dataio.kompsat2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadata;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadataUtil;
import org.esa.s2tbx.dataio.kompsat2.metadata.Kompsat2Metadata;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by jcoravu on 16/12/2019.
 */
public class Kompsat2MetadataInspector implements MetadataInspector {

    public Kompsat2MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            String metadataFileName = Kompsat2ProductReader.buildMetadataFileName(productDirectory);
            Path imagesMetadataParentPath = Kompsat2ProductReader.buildImagesMetadataParentPath(productDirectory, metadataFileName);
            Kompsat2Metadata productMetadata = Kompsat2ProductReader.readProductMetadata(productDirectory, metadataFileName);
            List<BandMetadata> bandMetadataList = Kompsat2ProductReader.readBandMetadata(imagesMetadataParentPath);

            BandMetadataUtil metadataUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));

            Metadata metadata = new Metadata();
            metadata.setProductWidth(metadataUtil.getMaxNumColumns());
            metadata.setProductHeight(metadataUtil.getMaxNumLines());
            Dimension defaultProductSize = new Dimension(metadataUtil.getMaxNumColumns(), metadataUtil.getMaxNumLines());

            BandMetadata bandMetadataForDefaultProductGeoCoding = null;
            for (BandMetadata bandMetadata : bandMetadataList) {
                String bandName = Kompsat2ProductReader.getBandName(bandMetadata.getImageFileName());
                if (bandName.equals(Kompsat2Constants.BAND_NAMES[4])) {
                    bandMetadataForDefaultProductGeoCoding = bandMetadata;
                    break;
                }
            }
            GeoCoding productGeoCoding = Kompsat2ProductReader.buildDefaultGeoCoding(productMetadata, bandMetadataForDefaultProductGeoCoding, imagesMetadataParentPath, defaultProductSize, null, null);
            metadata.setGeoCoding(productGeoCoding);

            for (int bandIndex = 0; bandIndex < bandMetadataList.size(); bandIndex++) {
                BandMetadata bandMetadata = bandMetadataList.get(bandIndex);
                String bandName = Kompsat2ProductReader.getBandName(bandMetadata.getImageFileName());
                metadata.getBandList().add(bandName);
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
