package org.esa.s2tbx.dataio.gdal.reader;

import org.esa.s2tbx.commons.VirtualFile;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.TiePointGeoCoding;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 19/12/2019.
 */
public class GDALMetadataInspector implements MetadataInspector {

    public GDALMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        org.gdal.gdal.Dataset gdalDataset = null;
        VirtualFile virtualFile = null;
        try {
            virtualFile = new VirtualFile(productPath);
            gdalDataset = GDALProductReader.openGDALDataset(virtualFile.getLocalFile());

            Metadata metadata = new Metadata();
            metadata.setProductWidth(gdalDataset.getRasterXSize());
            metadata.setProductHeight(gdalDataset.getRasterYSize());

            GeoCoding productGeoCoding = GDALProductReader.buildGeoCoding(gdalDataset);
            metadata.setGeoCoding(productGeoCoding);

            int bandCount = gdalDataset.getRasterCount();
            for (int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
                // bands are not 0-base indexed, so we must add 1
                org.gdal.gdal.Band gdalBand = gdalDataset.GetRasterBand(bandIndex + 1);

                String bandName = GDALProductReader.computeBandName(gdalBand, bandIndex);
                metadata.getBandList().add(bandName);

                String maskName = GDALProductReader.computeMaskName(gdalBand, bandName);
                if (maskName != null) {
                    metadata.getMaskList().add(maskName);
                }
            }
            metadata.setHasMasks(metadata.getMaskList().size() > 0);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            try {
                if (gdalDataset != null) {
                    gdalDataset.delete();
                }
            } finally {
                if (virtualFile != null) {
                    virtualFile.close();
                }
            }
        }
    }
}
