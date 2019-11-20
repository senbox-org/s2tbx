package org.esa.s2tbx.dataio.spot6.internal;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.s2tbx.dataio.spot6.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeMetadata;
import org.esa.snap.core.dataio.XmlMetadataInspector;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.util.math.MathUtils;
import org.geotools.referencing.CRS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Metadata inspector for Spot6 products
 * @author Denisa Stefanescu
 */
public class Spot6MetadataInspector extends XmlMetadataInspector {

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        Metadata metadata = new Metadata();
        VolumeMetadata productMetadata = getProductMetadata(productPath);
        if(productMetadata != null) {
            List<ImageMetadata> imageMetadataList = productMetadata.getImageMetadataList();
            if (imageMetadataList.isEmpty()) {
                throw new IOException("No raster found");
            } else {
                metadata.setHasMasks(true);
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

                ImageMetadata maxResImageMetadata = productMetadata.getMaxResolutionImage();
                int width = productMetadata.getSceneWidth();
                int height = productMetadata.getSceneHeight();
                metadata.setProductWidth(String.valueOf(width));
                metadata.setProductHeight(String.valueOf(height));
                GeoPos geoPos1 = null;
                GeoPos geoPos2 = null;
                if (maxResImageMetadata.hasInsertPoint()) {
                    String crsCode = productMetadata.getMaxResolutionImage().getCRSCode();
                    ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
                    try {
                        GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                               width, height,
                                                               origin.x, origin.y,
                                                               origin.stepX, origin.stepY);
                        metadata.setGeoCoding(geoCoding);
                        geoPos1 = geoCoding.getGeoPos(new PixelPos(0, 0), null);
                        geoPos2 = geoCoding.getGeoPos(new PixelPos(width, height), null);
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
                        geoPos1 = geoCoding.getGeoPos(new PixelPos(0, 0), null);
                        geoPos2 = geoCoding.getGeoPos(new PixelPos(width, height), null);
                    }catch (Exception e){
                        logger.warning(e.getMessage());
                    }
                }
                if(geoPos1 != null && geoPos2 != null) {
                    metadata.setLatitudeNorth(String.valueOf(MathUtils.crop(geoPos1.getLat(), -90.0, 90.0)));
                    metadata.setLatitudeSouth(String.valueOf(MathUtils.crop(geoPos2.getLat(), -90.0, 90.0)));
                    metadata.setLongitudeEast(String.valueOf(MathUtils.crop(geoPos2.getLon(), -90.0, 90.0)));
                    metadata.setLongitudeWest(String.valueOf(MathUtils.crop(geoPos1.getLon(), -90.0, 90.0)));
                    metadata.setHasGeoCoding(true);
                }else{
                    metadata.setHasGeoCoding(false);
                }
            }
        }
        return metadata;
    }

    public VolumeMetadata getProductMetadata(Path productPath) throws IOException{
        VirtualDirEx productDirectory = VirtualDirEx.build(productPath);
        return VolumeMetadata.create(productDirectory.getFile(Spot6Constants.ROOT_METADATA).toPath());
    }
}
