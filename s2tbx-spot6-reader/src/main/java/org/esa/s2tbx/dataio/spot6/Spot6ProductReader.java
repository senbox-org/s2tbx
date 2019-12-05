package org.esa.s2tbx.dataio.spot6;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.ColorIterator;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.s2tbx.dataio.spot6.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.spot6.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.spot6.internal.Spot6MetadataInspector;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.TreeNode;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reader for SPOT 6/7 products.
 *
 * @author Cosmin Cara
 * modified 20190513 for VFS compatibility by Oana H.
 * modified 20191120 to read a specific area from the input product by Denisa Stefanescu
 */
public class Spot6ProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(Spot6ProductReader.class.getName());

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
    private boolean isMultiSize = false;

    protected Spot6ProductReader(Spot6ProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);

        this.tileRefs = new HashSet<>();
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new Spot6MetadataInspector();
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isCompressed()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            //if the volume metadata file is present, but it is not in the list, add it!
            try {
                File volumeMetadataPhysicalFile = this.productDirectory.getFile(Spot6Constants.ROOT_METADATA);
                if (this.metadata != null) {
                    addProductComponentIfNotPresent(Spot6Constants.ROOT_METADATA, volumeMetadataPhysicalFile, result);
                    for (VolumeMetadata component : this.metadata.getVolumeMetadataList()) {
                        try {
                            File fullPathComp = this.productDirectory.getFile(component.getPath().toString());
                            addProductComponentIfNotPresent(component.getFileName(), fullPathComp, result);
                            for (VolumeComponent vComponent: component.getComponents()){
                                if(vComponent.getType().equals(Spot6Constants.METADATA_FORMAT)){
                                    File fullPathVComp = this.productDirectory.getFile(fullPathComp.getParent() + File.separator + vComponent.getRelativePath());
                                    addProductComponentIfNotPresent(Paths.get(vComponent.getRelativePath()).getFileName().toString(), fullPathVComp, result);
                                    if(vComponent.getComponentMetadata() != null && vComponent.getComponentMetadata() instanceof ImageMetadata){
                                        ImageMetadata image = (ImageMetadata)vComponent.getComponentMetadata();
                                        for (String raster : image.getRasterFileNames()){
                                            addProductComponentIfNotPresent(raster, this.productDirectory.getFile(fullPathVComp.getParent() + File.separator + raster), result);
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
        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
        this.productDirectory = VirtualDirEx.build(inputPath);

        metadata = VolumeMetadata.create(productDirectory.getFile(Spot6Constants.ROOT_METADATA).toPath());
        Product product = null;
        if (metadata != null) {
            Rectangle productSubsetRegion = null;
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.size() == 0) {
                throw new IOException("No raster found");
            }
            this.isMultiSize = this.metadata.getImageMetadataList().size() > 1;
            int width = metadata.getSceneWidth();
            int height = metadata.getSceneHeight();
            int offsetX = 0;
            int offsetY = 0;
            ImageMetadata maxResImageMetadata = metadata.getMaxResolutionImage();
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            if(getSubsetDef() != null && getSubsetDef().getRegion() != null){
                productSubsetRegion = getSubsetDef().getRegion();
                width = productSubsetRegion.width;
                height = productSubsetRegion.height;
                offsetX = productSubsetRegion.x;
                offsetY = productSubsetRegion.y;
            }
            product = new Product(metadata.getInternalReference(),
                                  metadata.getProductType(),
                                  width, height);
            product.setFileLocation(metadata.getPath().toFile());
            product.setStartTime(maxResImageMetadata.getProductStartTime());
            product.setEndTime(maxResImageMetadata.getProductEndTime());
            product.setDescription(maxResImageMetadata.getProductDescription());
            if (maxResImageMetadata.hasInsertPoint()) {
                String crsCode = maxResImageMetadata.getCRSCode();
                try {
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                            width, height,
                                                            origin.x + (offsetX * origin.stepX),
                                                           origin.y - (offsetY * origin.stepY),
                                                            origin.stepX, origin.stepY);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            } else {
                initProductTiePointGeoCoding(maxResImageMetadata, product);
            }

            Spot6ProductReaderPlugin readerPlugin = (Spot6ProductReaderPlugin)getReaderPlugIn();
            Path colorPaletteFilePath = readerPlugin.getColorPaletteFilePath();

            for (ImageMetadata imageMetadata : imageMetadataList) {
                if ((getSubsetDef() != null && !getSubsetDef().isIgnoreMetadata()) || getSubsetDef() == null) {
                    product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                }
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

                ProductSubsetDef bandSubsetDef = null;
                Rectangle bandSubsetRegion = null;

                Float[] solarIrradiances = imageMetadata.getSolarIrradiances();
                double[][] scalingAndOffsets = imageMetadata.getScalingAndOffsets();
                Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
                int subsetTileCols = tileCols;
                int subsetTileRows = tileRows;
                int subsetTileEndRow = tileRows;
                int subsetTileEndCol = tileCols;
                int subsetTileStartCol = 0;
                int subsetTileStartRow = 0;
                if (productSubsetRegion != null) {
                    if(isMultiSize && (bandWidth < maxResImageMetadata.getRasterWidth() || bandHeight < maxResImageMetadata.getRasterHeight())){
                        bandWidth = (int) (productSubsetRegion.width * origin.stepX / imageMetadata.getInsertPoint().stepX);
                        bandHeight = (int) (productSubsetRegion.height * origin.stepY / imageMetadata.getInsertPoint().stepY);
                        int startX = (int)(productSubsetRegion.x / imageMetadata.getInsertPoint().stepX * origin.stepX);
                        int startY = (int) (productSubsetRegion.y / imageMetadata.getInsertPoint().stepY * origin.stepY);
                        bandSubsetRegion = new Rectangle(startX,startY,bandWidth,bandHeight);
                    }else {
                        bandWidth = productSubsetRegion.width;
                        bandHeight = productSubsetRegion.height;
                        bandSubsetRegion = productSubsetRegion;
                    }
                    if(tileCols > 1 || tileRows > 1) {
                        //we need to compute the tiles on row and column from where the selected subset starts
                        subsetTileStartRow = bandSubsetRegion.y / tileHeight;
                        subsetTileStartCol = bandSubsetRegion.x / tileWidth;

                        //image width and height are already the one from the subset region (were retrieved when the product vas instantiated)
                        subsetTileEndCol = (bandWidth  + bandSubsetRegion.x) /tileWidth;
                        if ((bandWidth  + bandSubsetRegion.x)  % tileWidth != 0) {
                            subsetTileEndCol++;
                        }
                        subsetTileEndRow = (bandHeight + bandSubsetRegion.y) / tileHeight;
                        if ((bandHeight + bandSubsetRegion.y) % tileHeight != 0) {
                            subsetTileEndRow++;
                        }

                        subsetTileCols = subsetTileEndCol - subsetTileStartCol;
                        subsetTileRows = subsetTileEndRow - subsetTileStartRow;
                    }
                }
                float factorX = (float) width / bandWidth;
                float factorY = (float) height / bandHeight;
                Product[][] tiles = new Product[subsetTileRows][subsetTileCols];
                for (String rasterFile : tileInfo.keySet()) {
                    boolean readTile = true;
                    int[] coords = tileInfo.get(rasterFile);
                    if(getSubsetDef() != null && (subsetTileStartCol > coords[1] || subsetTileEndCol -1 < coords[1] ||
                            subsetTileStartRow > coords[0] || subsetTileEndRow - 1 < coords[0])){
                        readTile = false;
                    }
                    if (readTile) {
                        if(productSubsetRegion != null) {
                            bandSubsetDef = new ProductSubsetDef();
                            if (tileCols == 1 && tileRows == 1) {
                                bandSubsetDef.setRegion(bandSubsetRegion);
                            } else {
                                int tileOffsetX = 0;
                                int tileOffsetY = 0;
                                int coordHeight = tileHeight;
                                int coordWidth = tileWidth;

                                if (coords[0] == subsetTileStartRow) {
                                    tileOffsetY = bandSubsetRegion.y - coords[0] * tileHeight;
                                    if(coords[0] != subsetTileEndRow - 1) {
                                        coordHeight = tileHeight - tileOffsetY;
                                    }else{
                                        coordHeight = bandSubsetRegion.height;
                                    }
                                }
                                if (coords[0] > subsetTileStartRow && coords[0] == subsetTileEndRow - 1) {
                                    coordHeight = bandSubsetRegion.height - (coords[0] * tileHeight - bandSubsetRegion.y);
                                }

                                if (coords[1] == subsetTileStartCol) {
                                    tileOffsetX = bandSubsetRegion.x - coords[1] * tileWidth;
                                    if(coords[1] != subsetTileEndCol - 1) {
                                        coordWidth = tileWidth - tileOffsetX;
                                    }else{
                                        coordWidth = bandSubsetRegion.width;
                                    }
                                }
                                if (coords[1] > subsetTileStartCol && coords[1] == subsetTileEndCol - 1) {
                                    coordWidth = bandSubsetRegion.width - (coords[1] * tileWidth - bandSubsetRegion.x);
                                }
                                bandSubsetDef.setRegion(new Rectangle(tileOffsetX, tileOffsetY, coordWidth, coordHeight));
                            }
                        }
                        tiles[coords[0]-subsetTileStartRow][coords[1]-subsetTileStartCol] = ProductIO.readProduct(imageMetadata.getPath().resolve(rasterFile).toFile(), bandSubsetDef);
                        tileRefs.add(new WeakReference<Product>(tiles[coords[0]-subsetTileStartRow][coords[1]-subsetTileStartCol]));
                    }
                }
                int levels = tiles[0][0].getBandAt(0).getSourceImage().getModel().getLevelCount();
                if (levels > product.getNumResolutionsMax()) {
                    product.setNumResolutionsMax(levels);
                }
                final Stx[] statistics = imageMetadata.getBandsStatistics();
                for (int i = 0; i < numBands; i++) {
                    if (getSubsetDef() == null || getSubsetDef().isNodeAccepted(bandInfos[i].getId())) {
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
                        initBandGeoCoding(imageMetadata, targetBand, width, height, bandSubsetRegion);
                        Band[][] srcBands = new Band[subsetTileRows][subsetTileCols];
                        for (int x = 0; x < subsetTileRows; x++) {
                            for (int y = 0; y < subsetTileCols; y++) {
                                srcBands[x][y] = tiles[x][y].getBandAt(bandInfos[i].getIndex());
                            }
                        }

                        MosaicMultiLevelSource bandSource =
                                new MosaicMultiLevelSource(srcBands,
                                                           bandWidth, bandHeight,
                                                           tileWidth, tileHeight, subsetTileCols, subsetTileRows,
                                                           levels, typeMap.get(pixelDataType),
                                                           imageMetadata.isGeocoded() ?
                                                                   targetBand.getGeoCoding() != null ?
                                                                           Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                                                           Product.findImageToModelTransform(product.getSceneGeoCoding()) :
                                                                   targetBand.getImageToModelTransform(), bandSubsetRegion, new Point(subsetTileStartRow,subsetTileStartCol));
                        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                        if (statistics[i] != null) {
                            //targetBand.setStx(statistics[i]);
                        /*targetBand.setImageInfo(
                                new ImageInfo(
                                        new ColorPaletteDef(newil intrebai daca vrea mall sau  ColorPaletteDef.Point[] {
                                                new ColorPaletteDef.Point(statistics[i].getMinimum(), Color.BLACK),
                                                new ColorPaletteDef.Point(statistics[i].getMean(), Color.GRAY),
                                                new ColorPaletteDef.Point(statistics[i].getMaximum(), Color.WHITE)
                                        })));//, (int) Math.pow(2, imageMetadata.getPixelNBits()))));*/
                        }
                        product.addBand(targetBand);
                    }
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
        TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, metadata.getSceneWidth() , metadata.getSceneHeight(), cornerLonsLats[1]);
        TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, metadata.getSceneWidth(), metadata.getSceneHeight(), cornerLonsLats[0]);
        if(getSubsetDef() != null && getSubsetDef().getRegion()!=null) {
            lonGrid = TiePointGrid.createSubset(lonGrid,getSubsetDef());
            latGrid = TiePointGrid.createSubset(latGrid,getSubsetDef());
        }
        product.addTiePointGrid(lonGrid);
        product.addTiePointGrid(latGrid);
        product.setSceneGeoCoding(new TiePointGeoCoding(latGrid, lonGrid));
    }

    private GeoCoding addTiePointGridGeo(ImageMetadata metadata, int width, int height) {
        float[][] cornerLonsLats =  metadata.getCornerLonsLats();
        TiePointGrid latGrid = createTiePointGrid("latitude", 2, 2, 0, 0, width , height, cornerLonsLats[1]);
        TiePointGrid lonGrid = createTiePointGrid("longitude", 2, 2, 0, 0, width, height, cornerLonsLats[0]);
        if(getSubsetDef() != null && getSubsetDef().getRegion()!=null) {
            lonGrid = TiePointGrid.createSubset(lonGrid,getSubsetDef());
            latGrid = TiePointGrid.createSubset(latGrid,getSubsetDef());
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void initBandGeoCoding(ImageMetadata imageMetadata, Band band, int sceneWidth, int sceneHeight, Rectangle bandSubsetRegion) {
        int bandWidth = band.getRasterWidth();
        int bandHeight = band.getRasterHeight();
        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        String crsCode = imageMetadata.getCRSCode();
        try {
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            if (imageMetadata.hasInsertPoint()) {
                    geoCoding = new CrsGeoCoding(crs,
                                                 bandWidth, bandHeight,
                                                 insertPoint.x + (bandSubsetRegion.x * insertPoint.stepX), insertPoint.y - (bandSubsetRegion.y * insertPoint.stepY),
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
        if(band.getGeoCoding() == null) {
            band.setGeoCoding(geoCoding);
        }
    }

    private void addMasks(Product target, ImageMetadata metadata) {
        ProductNodeGroup<Mask> maskGroup = target.getMaskGroup();
        if (!maskGroup.contains(Spot6Constants.NODATA) && (getSubsetDef() == null ||
                (getSubsetDef() != null && getSubsetDef().isNodeAccepted(Spot6Constants.NODATA)))) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(Spot6Constants.NODATA, Spot6Constants.NODATA,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(Spot6Constants.SATURATED) && (getSubsetDef() == null ||
                (getSubsetDef() != null && getSubsetDef().isNodeAccepted(Spot6Constants.SATURATED)))) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(Spot6Constants.SATURATED, Spot6Constants.SATURATED,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private void addGMLMasks(Product target, ImageMetadata metadata) {
        List<ImageMetadata.MaskInfo> gmlMasks = metadata.getMasks();
        final Iterator<Color> colorIterator = ColorIterator.create();
        Band refBand = findReferenceBand(target, metadata.getRasterWidth());
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
                if(getSubsetDef() == null || (getSubsetDef() != null && getSubsetDef().isNodeAccepted(maskName))) {
                    if (refBand != null) {
                        target.addMask(maskName, node, mask.description, colorIterator.next(), 0.5, refBand);
                    } else {
                        target.addMask(mask.name, node, mask.description, colorIterator.next(), 0.5);
                    }
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
