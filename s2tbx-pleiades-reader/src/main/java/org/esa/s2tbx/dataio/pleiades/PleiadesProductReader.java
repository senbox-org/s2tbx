package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.pleiades.dimap.Constants;
import org.esa.s2tbx.dataio.pleiades.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.pleiades.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.readers.ColorIterator;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.util.TreeNode;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.Color;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Reader for SPOT 6/7 products.
 *
 * @author Cosmin Cara
 */
public class PleiadesProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(PleiadesProductReader.class.getName());

    private final static Map<Integer, Integer> typeMap = new HashMap<Integer, Integer>() {{
        put(ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_INT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT);
        put(ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT);
        put(ProductData.TYPE_UINT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_INT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT);
    }};

    private VirtualDirEx productDirectory;
    private VolumeMetadata metadata;
    private Set<WeakReference<Product>> tileRefs;

    protected PleiadesProductReader(PleiadesProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);

        this.tileRefs = new HashSet<>();
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isCompressed()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            //if the volume metadata file is present, but it is not in the list, add it!
            try {
                File volumeMetadataPhysicalFile = productDirectory.getFile(Constants.ROOT_METADATA);
                if (metadata != null) {
                    addProductComponentIfNotPresent(Constants.ROOT_METADATA, volumeMetadataPhysicalFile, result);
                    for (VolumeMetadata component : metadata.getVolumeMetadataList()) {
                        try {
                            File fullPathComp = productDirectory.getFile(component.getPath());
                            addProductComponentIfNotPresent(component.getFileName(), fullPathComp, result);
                            for (VolumeComponent vComponent: component.getComponents()){
                                if(vComponent.getType().equals(Constants.METADATA_FORMAT)){
                                    File fullPathVComp = productDirectory.getFile(fullPathComp.getParent() + File.separator + vComponent.getPath().toString());
                                    addProductComponentIfNotPresent(vComponent.getPath().getFileName().toString(), fullPathVComp, result);
                                    if(vComponent.getComponentMetadata() != null && vComponent.getComponentMetadata() instanceof ImageMetadata){
                                        ImageMetadata image = (ImageMetadata)vComponent.getComponentMetadata();
                                        for (String raster : image.getRasterFileNames()){
                                            addProductComponentIfNotPresent(raster, productDirectory.getFile(fullPathVComp.getParent() + File.separator + raster), result);
                                        }
                                        for (ImageMetadata.MaskInfo mask : image.getMasks()){
                                            addProductComponentIfNotPresent(mask.name, mask.path.toFile(), result);
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            logger.warning(ex.getMessage());
                        }
                    }
                }
            } catch (IOException ex) {
                logger.warning(ex.getMessage());
            }

            return result;
        }
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        PleiadesProductReaderPlugin readerPlugin = (PleiadesProductReaderPlugin)getReaderPlugIn();
        productDirectory = readerPlugin.getInput(getInput());
        metadata = VolumeMetadata.create(productDirectory.getFile(Constants.ROOT_METADATA).toPath());
        Product product = null;
        if (metadata != null) {
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.size() == 0) {
                throw new IOException("No raster found");
            }
            int width = metadata.getSceneWidth();
            int height = metadata.getSceneHeight();
            product = new Product(metadata.getInternalReference(),
                                  metadata.getProductType(),
                                  width, height);
            product.setFileLocation(new File(metadata.getPath()));
            ImageMetadata maxResImageMetadata = metadata.getMaxResolutionImage();
            product.setStartTime(maxResImageMetadata.getProductStartTime());
            product.setEndTime(maxResImageMetadata.getProductEndTime());
            product.setDescription(maxResImageMetadata.getProductDescription());
            //product.setNumResolutionsMax(imageMetadataList.size());
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            if (maxResImageMetadata.hasInsertPoint()) {
                String crsCode = maxResImageMetadata.getCRSCode();
                try {
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                            width, height,
                                                            origin.x, origin.y,
                                                            origin.stepX, origin.stepY);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            } else {
                initProductTiePointGeoCoding(maxResImageMetadata, product);
            }

            Path colorPaletteFilePath = readerPlugin.getColorPaletteFilePath();

            for (ImageMetadata imageMetadata : imageMetadataList) {
                product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                int numBands = imageMetadata.getNumBands();
                ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();

                int pixelDataType = imageMetadata.getPixelDataType();
                int tileRows = imageMetadata.getTileRowsCount();
                int tileCols = imageMetadata.getTileColsCount();
                int tileWidth = imageMetadata.getTileWidth();
                int tileHeight = imageMetadata.getTileHeight();
                int noDataValue = imageMetadata.getNoDataValue();
                int bandWidth = imageMetadata.getRasterWidth();
                int bandHeight = imageMetadata.getRasterHeight();
                float factorX = (float) width / bandWidth;
                float factorY = (float) height / bandHeight;

                Float[] solarIrradiances = imageMetadata.getSolarIrradiances();
                double[][] scalingAndOffsets = imageMetadata.getScalingAndOffsets();
                Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
                Product[][] tiles = new Product[tileCols][tileRows];
                for (String rasterFile : tileInfo.keySet()) {
                    int[] coords = tileInfo.get(rasterFile);
                    tiles[coords[0]][coords[1]] = ProductIO.readProduct(Paths.get(imageMetadata.getPath()).resolve(rasterFile).toFile());
                    tileRefs.add(new WeakReference<Product>(tiles[coords[0]][coords[1]]));
                }
                int levels = tiles[0][0].getBandAt(0).getSourceImage().getModel().getLevelCount();
                if (levels > product.getNumResolutionsMax()) {
                    product.setNumResolutionsMax(levels);
                }
                //final Stx[] statistics = imageMetadata.getBandsStatistics();
                for (int i = 0; i < numBands; i++) {
                    Band targetBand = new ColorPaletteBand(bandInfos[i].getId(), pixelDataType, Math.round(width / factorX),
                                                           Math.round(height / factorY), colorPaletteFilePath);
                    targetBand.setSpectralBandIndex(numBands > 1 ? i : -1);
                    targetBand.setSpectralWavelength(bandInfos[i].getCentralWavelength());
                    targetBand.setSpectralBandwidth(bandInfos[i].getBandwidth());
                    targetBand.setSolarFlux(solarIrradiances[i]);
                    targetBand.setUnit(bandInfos[i].getUnit());
                    targetBand.setNoDataValue(noDataValue);
                    targetBand.setNoDataValueUsed(true);
                    targetBand.setScalingFactor(scalingAndOffsets[i][0] / bandInfos[i].getGain());
                    targetBand.setScalingOffset(scalingAndOffsets[i][1] * bandInfos[i].getBias());
                    initBandGeoCoding(imageMetadata, targetBand, width, height);
                    Band[][] srcBands = new Band[tileRows][tileCols];
                    for (int x = 0; x < tileRows; x++) {
                        for (int y = 0; y < tileCols; y++) {
                            srcBands[x][y] = tiles[x][y].getBandAt(bandInfos[i].getIndex());
                        }
                    }

                    MosaicMultiLevelSource bandSource =
                            new MosaicMultiLevelSource(srcBands,
                                    bandWidth, bandHeight,
                                    tileWidth, tileHeight, tileRows, tileCols,
                                    levels, typeMap.get(pixelDataType),
                                    imageMetadata.isGeocoded() ?
                                            targetBand.getGeoCoding() != null ?
                                                    Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                                    Product.findImageToModelTransform(product.getSceneGeoCoding()) :
                                            targetBand.getImageToModelTransform());
                    targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                    /*if (statistics[i] != null) {
                        targetBand.setStx(statistics[i]);
                        targetBand.setImageInfo(
                                new ImageInfo(
                                        new ColorPaletteDef(new ColorPaletteDef.Point[] {
                                                new ColorPaletteDef.Point(statistics[i].getMinimum(), Color.BLACK),
                                                new ColorPaletteDef.Point(statistics[i].getMean(), Color.GRAY),
                                                new ColorPaletteDef.Point(statistics[i].getMaximum(), Color.WHITE)
                                        })));
                    }*/
                    product.addBand(targetBand);
                }
                addMasks(product, imageMetadata);
                addGMLMasks(product, imageMetadata);
            }
            product.setModified(false);
        }

        return product;
    }

    @Override
    public void close() throws IOException {
        System.gc();
        for (WeakReference<Product> ref : tileRefs) {
            Product product = ref.get();
            if (product != null) {
                product.closeIO();
                product = null;
            }
            ref.clear();
        }

        super.close();
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

    private void initProductTiePointGeoCoding(ImageMetadata imageMetadata, Product product) {
        float[][] cornerLonsLats = imageMetadata.getCornerLonsLats();
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[1]);
        product.addTiePointGrid(latGrid);
        TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        product.addTiePointGrid(lonGrid);
        product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
    }

    private GeoCoding addTiePointGridGeo(ImageMetadata metadata, int width, int height) {
        float[][] cornerLonsLats =  metadata.getCornerLonsLats();
        int sceneWidth = width;
        int sceneHeight = height;
        TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, sceneWidth , sceneHeight, cornerLonsLats[1]);
        TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void initBandGeoCoding(ImageMetadata imageMetadata, Band band, int sceneWidth, int sceneHeight) {
        int bandWidth = imageMetadata.getRasterWidth();
        int bandHeight = imageMetadata.getRasterHeight();
        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        String crsCode = imageMetadata.getCRSCode();
        try {
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            if (imageMetadata.hasInsertPoint()) {
                    geoCoding = new CrsGeoCoding(crs,
                            bandWidth, bandHeight,
                            insertPoint.x, insertPoint.y,
                            insertPoint.stepX, insertPoint.stepY, 0.0, 0.0);
            } else {
                if (sceneWidth != bandWidth) {
                    AffineTransform2D transform2D = new AffineTransform2D((float) sceneWidth / bandWidth, 0.0, 0.0, (float) sceneHeight / bandHeight, 0.0, 0.0);
                    geoCoding = addTiePointGridGeo(imageMetadata, bandWidth, bandHeight);
                    band.setImageToModelTransform(transform2D);
                }
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        band.setGeoCoding(geoCoding);
    }

    private void addMasks(Product target, ImageMetadata metadata) {
        ProductNodeGroup<Mask> maskGroup = target.getMaskGroup();
        if (!maskGroup.contains(Constants.NODATA)) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.NODATA, Constants.NODATA,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(Constants.SATURATED)) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.SATURATED, Constants.SATURATED,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private void addGMLMasks(Product target, ImageMetadata metadata) {
        List<ImageMetadata.MaskInfo> gmlMasks = metadata.getMasks();
        final Iterator<Color> colorIterator = ColorIterator.create();
        Band refBand = findReferenceBand(target, metadata.getRasterWidth());
        boolean isMultiSize = this.metadata.getImageMetadataList().size() > 1;
        gmlMasks.stream().forEach(mask -> {
            logger.info(String.format("Parsing mask %s of component %s", mask.name, metadata.getFileName()));
            VectorDataNode node = GMLReader.parse(mask.name, mask.path);
            if (node != null && node.getFeatureCollection().size() > 0) {
                node.setOwner(target);
                String maskName = mask.name;
                if (isMultiSize) {
                    String resolution = "_" + new DecimalFormat("#.#").format(metadata.getPixelSize()) + "m";
                    maskName += resolution.endsWith(".") ? resolution.substring(0, resolution.length() - 1) : resolution;
                }
                if (refBand != null) {
                    target.addMask(maskName, node, mask.description, colorIterator.next(), 0.5, refBand);
                } else {
                    target.addMask(mask.name, node, mask.description, colorIterator.next(), 0.5);
                }
            }
        });
    }

    private Band findReferenceBand(Product product, int width) {
        Band referenceBand = null;
        for (Band band : product.getBands()) {
            if (band.getRasterWidth() == width) {
                referenceBand = band;
                break;
            }
        }
        return referenceBand;
    }

    private void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<File>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

}
