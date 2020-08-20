package org.esa.s2tbx.dataio.pleiades.internal;

import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.pleiades.PleiadesProductReader;
import org.esa.s2tbx.dataio.pleiades.dimap.Constants;
import org.esa.s2tbx.dataio.pleiades.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.metadata.MetadataInspector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Metadata inspector for Pleiades products
 *
 * @author Denisa Stefanescu
 */

public class PleiadesMetadataInspector implements MetadataInspector {

    private static final Logger logger = Logger.getLogger(PleiadesMetadataInspector.class.getName());

    public PleiadesMetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        try (VirtualDirEx productDirectory = VirtualDirEx.build(productPath)) {
            VolumeMetadata productMetadata;
            try (FilePathInputStream inputStream = productDirectory.getInputStream(Constants.ROOT_METADATA)) {
                productMetadata = VolumeMetadata.create(inputStream);
            }
            List<ImageMetadata> imageMetadataList = productMetadata.getImageMetadataList();
            if (imageMetadataList.isEmpty()) {
                throw new IllegalStateException("No raster found.");
            }

            ImageMetadata maxResImageMetadata = productMetadata.getMaxResolutionImage();
            int defaultProductWidth = productMetadata.getSceneWidth();
            int defaultProductHeight = productMetadata.getSceneHeight();
            Metadata metadata = new Metadata(defaultProductWidth, defaultProductHeight);

            metadata.getMaskList().add("NODATA");
            metadata.getMaskList().add("SATURATED");

            for (ImageMetadata imageMetadata : imageMetadataList) {
                ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();
                Arrays.stream(bandInfos).forEach(bandInfo -> metadata.getBandList().add(bandInfo.getId()));

                if (imageMetadata.getMasks() != null && !imageMetadata.getMasks().isEmpty()) {
                    imageMetadata.getMasks().stream().forEach(mask -> {
                        logger.info(String.format("Parsing mask %s of component %s", mask.name, imageMetadata.getFileName()));
                        VectorDataNode node = GMLReader.parse(mask.name, mask.path);
                        if (node != null && node.getFeatureCollection().size() > 0) {
                            metadata.getMaskList().add(mask.name);
                        }
                    });
                }
            }

            GeoCoding geoCoding = PleiadesProductReader.buildGeoCoding(maxResImageMetadata, defaultProductWidth, defaultProductHeight, productMetadata, null, null);
            metadata.setGeoCoding(geoCoding);

            return metadata;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }
}
