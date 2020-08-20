package org.esa.s2tbx.dataio.spot6.internal;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.s2tbx.dataio.spot6.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeMetadata;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.metadata.MetadataInspector;
import org.geotools.referencing.CRS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Metadata inspector for Spot6 products
 * @author Denisa Stefanescu
 */

public class Spot6MetadataInspector implements MetadataInspector {
    private static final Logger logger = Logger.getLogger(Spot6MetadataInspector.class.getName());

    public Spot6MetadataInspector() {
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        VolumeMetadata productMetadata= getProductMetadata(productPath);
        ImageMetadata maxResImageMetadata = productMetadata.getMaxResolutionImage();
        Metadata metadata = new Metadata();
        int width = productMetadata.getSceneWidth();
        int height = productMetadata.getSceneHeight();
        metadata.setProductWidth(width);
        metadata.setProductHeight(height);
        addBandsAndMasks(productMetadata, metadata);
        if (maxResImageMetadata.hasInsertPoint()) {
            String crsCode = productMetadata.getMaxResolutionImage().getCRSCode();
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            try {
                GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                       width, height,
                                                       origin.x, origin.y,
                                                       origin.stepX, origin.stepY);
                metadata.setGeoCoding(geoCoding);
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }else{
            float[][] cornerLonsLats = maxResImageMetadata.getCornerLonsLats();
            try {
                TiePointGrid latGrid = new TiePointGrid("latitude", 2, 2, 0, 0, width, height, cornerLonsLats[1]);
                TiePointGrid lonGrid = new TiePointGrid("longitude", 2, 2, 0, 0, width, height, cornerLonsLats[0]);
                TiePointGeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                metadata.setGeoCoding(geoCoding);
            }catch (Exception e){
                logger.warning(e.getMessage());
            }
        }
        return metadata;
    }

    private VolumeMetadata getProductMetadata(Path productPath) throws IOException{
        VirtualDirEx productDirectory = VirtualDirEx.build(productPath);
        return VolumeMetadata.create(productDirectory.getFile(Spot6Constants.ROOT_METADATA).toPath());
    }

    private void addBandsAndMasks(VolumeMetadata productMetadata, Metadata metadata) throws IOException {
        List<ImageMetadata> imageMetadataList = productMetadata.getImageMetadataList();
        if (imageMetadataList.isEmpty()) {
            throw new IOException("No raster found");
        } else {
            metadata.getMaskList().add("NODATA");
            metadata.getMaskList().add("SATURATED");
            for (ImageMetadata imageMetadata : imageMetadataList) {
                ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();
                Arrays.stream(bandInfos).forEach(bandInfo -> metadata.getBandList().add(bandInfo.getId()));

                if (imageMetadata.getMasks() != null && !imageMetadata.getMasks().isEmpty()) {
                    imageMetadata.getMasks().stream().forEach(mask -> {
                        VectorDataNode node = GMLReader.parse(mask.name, mask.path);
                        if (node != null && node.getFeatureCollection().size() > 0) {
                            metadata.getMaskList().add(mask.name);
                        }
                    });
                }
            }
        }
    }

}
