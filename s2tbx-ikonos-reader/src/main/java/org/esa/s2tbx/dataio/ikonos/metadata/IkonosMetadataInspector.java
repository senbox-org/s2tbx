package org.esa.s2tbx.dataio.ikonos.metadata;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.IkonosProductReader;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.TiePointGeoCoding;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 28/11/2019.
 */
public class IkonosMetadataInspector implements MetadataInspector {

    public IkonosMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            String metadataFileName = IkonosProductReader.buildMetadataFileName(productDirectory);

            IkonosMetadata ikonosMetadata;
            try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataFileName)) {
                ikonosMetadata = IkonosMetadata.create(filePathInputStream);
            }

            Path zipArchivePath = IkonosProductReader.buildZipArchivePath(productDirectory, metadataFileName);

            java.util.List<BandMetadata> bandMetadataList = IkonosProductReader.readBandMetadata(zipArchivePath);
            BandMetadataUtil metadataUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));

            Metadata metadata = new Metadata();
            metadata.setProductWidth(metadataUtil.getMaxNumColumns());
            metadata.setProductHeight(metadataUtil.getMaxNumLines());

            TiePointGeoCoding productGeoCoding = IkonosProductReader.buildTiePointGridGeoCoding(ikonosMetadata, metadata.getProductWidth(), metadata.getProductHeight(), null);
            metadata.setGeoCoding(productGeoCoding);

            for (int bandIndex = 0; bandIndex < bandMetadataList.size(); bandIndex++) {
                BandMetadata bandMetadata = bandMetadataList.get(bandIndex);
                String bandName = IkonosProductReader.getBandName(bandMetadata.getImageFileName());
                metadata.getBandList().add(bandName);
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
