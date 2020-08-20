package org.esa.s2tbx.dataio.alos.av2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Metadata;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 4/12/2019.
 */
public class AlosAV2MetadataInspector implements MetadataInspector {

    public AlosAV2MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath, false, true)) {
            Path imageMetadataParentPath = AlosAV2ProductReader.buildImageMetadataParentPath(productDirectory);

            AlosAV2Metadata alosAV2Metadata;
            String imageMetadataRelativeFilePath;
            try (VirtualDirEx imageMetadataProductDirectory = VirtualDirEx.build(imageMetadataParentPath, false, false)) {
                imageMetadataRelativeFilePath = AlosAV2ProductReader.findImageMetadataRelativeFilePath(imageMetadataProductDirectory);
                alosAV2Metadata = AlosAV2ProductReader.readMetadata(imageMetadataProductDirectory, imageMetadataRelativeFilePath);
            }

            Metadata metadata = new Metadata();
            metadata.setProductWidth(alosAV2Metadata.getRasterWidth());
            metadata.setProductHeight(alosAV2Metadata.getRasterHeight());

            GeoCoding productGeoCoding = AlosAV2ProductReader.buildTiePointGridGeoCoding(alosAV2Metadata);
            if(productGeoCoding == null) {
                int extensionIndex = imageMetadataRelativeFilePath.lastIndexOf(AlosAV2Constants.IMAGE_METADATA_EXTENSION);
                String tiffImageRelativeFilePath = imageMetadataRelativeFilePath.substring(0, extensionIndex) + AlosAV2Constants.IMAGE_FILE_EXTENSION;
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imageMetadataParentPath, tiffImageRelativeFilePath);
                productGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
            }
            metadata.setGeoCoding(productGeoCoding);

            String[] bandNames = alosAV2Metadata.getBandNames();
            for (int i = 0; i < bandNames.length; i++) {
                metadata.getBandList().add(bandNames[i]);
            }

            if (alosAV2Metadata.getNoDataValue() >= 0) {
                metadata.getMaskList().add(AlosAV2Constants.NODATA);
            }
            if (alosAV2Metadata.getSaturatedPixelValue() >= 0) {
                metadata.getMaskList().add(AlosAV2Constants.SATURATED);
            }

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
