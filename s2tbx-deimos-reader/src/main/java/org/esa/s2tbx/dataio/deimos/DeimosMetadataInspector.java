package org.esa.s2tbx.dataio.deimos;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosConstants;
import org.esa.s2tbx.dataio.deimos.dimap.DeimosMetadata;
import org.esa.s2tbx.dataio.readers.MetadataList;
import org.esa.s2tbx.dataio.readers.RastersMetadata;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 9/12/2019.
 */
public class DeimosMetadataInspector implements MetadataInspector {

    public DeimosMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            MetadataList<DeimosMetadata> metadataList = DeimosProductReader.readMetadata(productDirectory);

            RastersMetadata rastersMetadata = DeimosProductReader.computeMaximumDefaultProductSize(metadataList, productDirectory);

            Metadata metadata = new Metadata(rastersMetadata.getMaximumWidh(), rastersMetadata.getMaximumHeight());

            GeoCoding productGeoCoding = addGeoCoding(metadataList, productDirectory);
            metadata.setGeoCoding(productGeoCoding);

            for (int i = 0; i < metadataList.getCount(); i++) {
                DeimosMetadata currentMetadata = metadataList.getMetadataAt(i);
                String[] bandNames = currentMetadata.getBandNames();
                String bandPrefix = DeimosProductReader.computeBandPrefix(metadataList.getCount(), i);
                int rasterBandCount = rastersMetadata.getRasterBandCount(currentMetadata);
                for (int bandIndex = 0; bandIndex < rasterBandCount; bandIndex++) {
                    String bandName = bandPrefix + ((bandIndex < bandNames.length) ? bandNames[bandIndex] : ("band_" + bandIndex));
                    metadata.addBandName(bandName);
                }
            }

            metadata.addMaskName(DeimosConstants.NODATA_VALUE);
            metadata.addMaskName(DeimosConstants.SATURATED_VALUE);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    private GeoCoding addGeoCoding(MetadataList<DeimosMetadata> metadataList, VirtualDirEx productDirectory) throws Exception {
        GeoCoding productGeoCoding = DeimosProductReader.buildProductTiePointGridGeoCoding(metadataList.getMetadataAt(0), metadataList, null);
        if(productGeoCoding == null) {
            boolean inputStreamSuccess = false;
            GeoTiffImageReader geoTiffImageReader;
            FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataList.getMetadataImageRelativePath(0));
            try {
                geoTiffImageReader = new GeoTiffImageReader(filePathInputStream, null);
                inputStreamSuccess = true;
            } finally {
                if (!inputStreamSuccess) {
                    filePathInputStream.close();
                }
            }
            if (productGeoCoding == null) {
                productGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
            }
        }
        return productGeoCoding;
    }
}
