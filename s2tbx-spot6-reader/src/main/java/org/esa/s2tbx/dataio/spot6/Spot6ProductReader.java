package org.esa.s2tbx.dataio.spot6;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.spot6.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.spot6.internal.MosaicMultiLevelSource;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.*;
import org.geotools.referencing.CRS;

import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by kraftek on 12/7/2015.
 */
public class Spot6ProductReader extends AbstractProductReader {

    private Spot6ProductReaderPlugin plugIn;
    private final static Map<Integer, Integer> typeMap = new HashMap<Integer, Integer>() {{
        put(ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_INT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT);
        put(ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT);
        put(ProductData.TYPE_UINT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_INT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT);
    }};
    private final Logger logger;

    protected Spot6ProductReader(Spot6ProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);
        plugIn = readerPlugIn;
        logger = Logger.getLogger(Spot6ProductReader.class.getName());
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        VirtualDirEx productDirectory = plugIn.getInput(getInput());
        VolumeMetadata metadata = VolumeMetadata.create(plugIn.getFileInput(getInput()).toPath());
        Product product = null;
        if (metadata != null) {
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.size() == 0) {
                throw new IOException("No raster found");
            }
            ImageMetadata imageMetadata = imageMetadataList.get(0);
            int width = imageMetadata.getRasterWidth();
            int height = imageMetadata.getRasterHeight();
            product = new Product(imageMetadata.getProductName(),
                                  imageMetadata.getFormatName(),
                                  width, height);
            product.getMetadataRoot().addElement(imageMetadata.getRootElement());
            product.setStartTime(imageMetadata.getProductStartTime());
            product.setEndTime(imageMetadata.getProductEndTime());
            product.setDescription(imageMetadata.getProductDescription());
            ImageMetadata.InsertionPoint origin = imageMetadata.getInsertPoint();
            if (imageMetadata.hasInsertPoint()) {
                String projectionCode = imageMetadata.getProjectionCode();
                try {
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(projectionCode),
                                                            width, height,
                                                            origin.x, origin.y,
                                                            origin.stepX, origin.stepY);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            } else {
                initTiePointGeoCoding(imageMetadata, product);
            }

            int numBands = imageMetadata.getNumBands();
            ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();
            int pixelDataType = imageMetadata.getPixelDataType();
            int tileRows = imageMetadata.getTileRowsCount();
            int tileCols = imageMetadata.getTileColsCount();
            int tileWidth = imageMetadata.getTileWidth();
            int tileHeight = imageMetadata.getTileHeight();
            int noDataValue = imageMetadata.getNoDataValue();

            Float[] solarIrradiances = imageMetadata.getSolarIrradiances();
            double[][] scalingAndOffsets = imageMetadata.getScalingAndOffsets();
            //Stx[] bandsStatistics = imageMetadata.getBandsStatistics();
            Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
            Product[][] tiles = new Product[tileCols][tileRows];
            for (String rasterFile : tileInfo.keySet()) {
                int[] coords = tileInfo.get(rasterFile);
                tiles[coords[0]][coords[1]] = ProductIO.readProduct(Paths.get(imageMetadata.getPath()).resolve(rasterFile).toFile());
            }
            for (int i = 0; i < numBands; i++) {
                Band virtualBand = new Band(bandInfos[i].getId(), pixelDataType, width, height);
                virtualBand.setSpectralBandIndex(i);
                virtualBand.setSolarFlux(solarIrradiances[i]);
                virtualBand.setUnit(bandInfos[i].getUnit());
                virtualBand.setNoDataValue(noDataValue);
                virtualBand.setNoDataValueUsed(true);
                virtualBand.setScalingFactor(scalingAndOffsets[i][0]/bandInfos[i].getGain());
                virtualBand.setScalingOffset(scalingAndOffsets[i][1]*bandInfos[i].getBias());
                //virtualBand.setStx(bandsStatistics[i]);
                int levels = tiles[0][0].getBandAt(0).getSourceImage().getModel().getLevelCount();
                Band[][] srcBands = new Band[tileRows][tileCols];
                for (int x = 0; x < tileRows; x++) {
                    for (int y = 0; y < tileCols; y++) {
                        srcBands[x][y] = tiles[x][y].getBandAt(i);
                    }
                }
                GeoCoding geoCoding = product.getSceneGeoCoding();
                MosaicMultiLevelSource bandSource =
                        new MosaicMultiLevelSource(srcBands,
                                                   width, height,
                                                   tileWidth, tileHeight, tileRows, tileCols,
                                                   levels, typeMap.get(pixelDataType), geoCoding);
                virtualBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                product.addBand(virtualBand);

            }
            //product.setPreferredTileSize(2048, 2048);
            product.setModified(false);
        }

        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
    }

    private void initTiePointGeoCoding(ImageMetadata imageMetadata, Product product) {
        float[][] cornerLonsLats = imageMetadata.getCornerLonsLats();
        int width = product.getSceneRasterWidth();
        int height = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, width, height, cornerLonsLats[1]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, width, height, cornerLonsLats[0]);
        product.addTiePointGrid(lonGrid);
        product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
    }

    private ProductData createProductData(int dataType, int size) {
        ProductData buffer;
        switch (dataType) {
            case ProductData.TYPE_UINT8:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
            case ProductData.TYPE_INT8:
                buffer = ProductData.createInstance(new byte[size]);
                break;
            case ProductData.TYPE_UINT16:
                buffer = ProductData.createUnsignedInstance(new short[size]);
                break;
            case ProductData.TYPE_INT16:
                buffer = ProductData.createInstance(new short[size]);
                break;
            case ProductData.TYPE_INT32:
                buffer = ProductData.createInstance(new int[size]);
                break;
            case ProductData.TYPE_UINT32:
                buffer = ProductData.createUnsignedInstance(new int[size]);
                break;
            case ProductData.TYPE_FLOAT32:
                buffer = ProductData.createInstance(new float[size]);
                break;
            default:
                buffer = ProductData.createUnsignedInstance(new byte[size]);
                break;
        }
        return buffer;
    }

    private Point2D.Float[][] getTileOrigins(ImageMetadata imageMetadata, ImageMetadata.InsertionPoint origin) {
        int maxWidth = imageMetadata.getRasterWidth();
        int maxHeight = imageMetadata.getRasterHeight();
        int rows = imageMetadata.getTileRowsCount();
        int cols = imageMetadata.getTileColsCount();
        int tileMaxWidth = imageMetadata.getTileWidth();
        int tileMaxHeight = imageMetadata.getTileHeight();
        Point2D.Float[][] origins = new Point2D.Float[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                if (x == 0 && y == 0) {
                    origins[x][y] = new Point2D.Float(origin.x, origin.y);
                } else {
                    origins[x][y] = new Point2D.Float(origin.x + x * tileMaxWidth * origin.stepX,
                                                      origin.y - y * tileMaxHeight * origin.stepY);
                }
            }
        }
        return origins;
    }
}
