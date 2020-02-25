package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.pleiades.dimap.Constants;
import org.esa.s2tbx.dataio.pleiades.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.pleiades.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.ColorIterator;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ImageUtils;
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Reader for Pleiades products.
 *
 * @author Cosmin Cara
 * modified 20191120 to read a specific area from the input product by Denisa Stefanescu
 */
public class PleiadesProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(PleiadesProductReader.class.getName());

    private final static Map<Integer, Integer> TYPE_MAP = new HashMap<Integer, Integer>() {{
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

    protected PleiadesProductReader(PleiadesProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);

        this.tileRefs = new HashSet<>();
    }

    @Override
    public TreeNode<File> getProductComponents() {
        TreeNode<File> result = super.getProductComponents();

        if (!this.productDirectory.isCompressed()) {
            // if the volume metadata file is present, but it is not in the list, add it!
            try {
                File volumeMetadataPhysicalFile = this.productDirectory.getFile(Constants.ROOT_METADATA);
                if (this.metadata != null) {
                    addProductComponentIfNotPresent(Constants.ROOT_METADATA, volumeMetadataPhysicalFile, result);
                    for (VolumeMetadata component : this.metadata.getVolumeMetadataList()) {
                        try {
                            File fullPathComp = this.productDirectory.getFile(component.getPath().toString());
                            addProductComponentIfNotPresent(component.getFileName(), fullPathComp, result);
                            for (VolumeComponent vComponent: component.getComponents()){
                                if (vComponent.getType().equals(Constants.METADATA_FORMAT)) {
                                    File fullPathVComp = this.productDirectory.getFile(fullPathComp.getParent() + File.separator + vComponent.getRelativePath());
                                    addProductComponentIfNotPresent(vComponent.getRelativePath(), fullPathVComp, result);
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
        }
        return result;
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());

        this.productDirectory = VirtualDirEx.build(inputPath);

        try (FilePathInputStream inputStream = this.productDirectory.getInputStream(Constants.ROOT_METADATA)) {
            this.metadata = VolumeMetadata.create(inputStream);
        }

        Product product = null;
        if (this.metadata != null) {
            List<ImageMetadata> imageMetadataList = this.metadata.getImageMetadataList();
            if (imageMetadataList.size() == 0) {
                throw new IOException("No raster found");
            }
            this.isMultiSize = this.metadata.getImageMetadataList().size() > 1;
            int productDefaultWidth = this.metadata.getSceneWidth();
            int productDefaultHeight = this.metadata.getSceneHeight();
            Dimension defaultProductBounds = new Dimension(productDefaultWidth, productDefaultHeight);

            ImageMetadata maxResImageMetadata = this.metadata.getMaxResolutionImage();
            ProductSubsetDef subsetDef = getSubsetDef();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productSubsetRegion;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productSubsetRegion = new Rectangle(0, 0, productDefaultWidth, productDefaultHeight);
            } else {
                productDefaultGeoCoding = buildGeoCoding(maxResImageMetadata, defaultProductBounds, metadata, null, null);
                productSubsetRegion = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, productDefaultWidth, productDefaultHeight, isMultiSize);
            }

            product = new Product(this.metadata.getInternalReference(), this.metadata.getProductType(), productSubsetRegion.width, productSubsetRegion.height);
            product.setFileLocation(this.metadata.getPath().toFile());
            product.setStartTime(maxResImageMetadata.getProductStartTime());
            product.setEndTime(maxResImageMetadata.getProductEndTime());
            product.setDescription(maxResImageMetadata.getProductDescription());
            GeoCoding geoCoding = buildGeoCoding(maxResImageMetadata, defaultProductBounds, metadata, productSubsetRegion, subsetDef);
            if (geoCoding instanceof TiePointGeoCoding){
                TiePointGeoCoding tiePointGeoCoding = (TiePointGeoCoding) geoCoding;
                product.addTiePointGrid(tiePointGeoCoding.getLatGrid());
                product.addTiePointGrid(tiePointGeoCoding.getLonGrid());
            }
            product.setSceneGeoCoding(geoCoding);

            PleiadesProductReaderPlugin readerPlugin = (PleiadesProductReaderPlugin)getReaderPlugIn();
            Path colorPaletteFilePath = readerPlugin.getColorPaletteFilePath();

            for (ImageMetadata imageMetadata : imageMetadataList) {
                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
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

                Rectangle bandSubsetRegion;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandSubsetRegion = new Rectangle(bandWidth, bandHeight);
                } else {
                    GeoCoding bandDefaultGeoCoding = initBandGeoCoding(imageMetadata, bandWidth, bandHeight, productDefaultWidth, null, null);
                    bandSubsetRegion = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, productDefaultWidth, productDefaultHeight, bandWidth, bandHeight, isMultiSize);
                }

                int subsetTileCols = tileCols;
                int subsetTileRows = tileRows;
                int subsetTileEndRow = tileRows;
                int subsetTileEndCol = tileCols;
                int subsetTileStartCol = 0;
                int subsetTileStartRow = 0;
                ProductSubsetDef bandSubsetDef = null;
                if (subsetDef != null) {
                    if(tileCols > 1 || tileRows > 1) {
                        //we need to compute the tiles on row and column from where the selected subset starts
                        subsetTileStartRow = bandSubsetRegion.y / tileHeight;
                        subsetTileStartCol = bandSubsetRegion.x / tileWidth;

                        //image width and height are already the one from the subset region (were retrieved when the product vas instantiated)
                        subsetTileEndCol = (bandSubsetRegion.width  + bandSubsetRegion.x) /tileWidth;
                        if ((bandSubsetRegion.width  + bandSubsetRegion.x)  % tileWidth != 0) {
                            subsetTileEndCol++;
                        }
                        subsetTileEndRow = (bandSubsetRegion.height + bandSubsetRegion.y) / tileHeight;
                        if ((bandSubsetRegion.height + bandSubsetRegion.y) % tileHeight != 0) {
                            subsetTileEndRow++;
                        }

                        subsetTileCols = subsetTileEndCol - subsetTileStartCol;
                        subsetTileRows = subsetTileEndRow - subsetTileStartRow;
                    }
                }
                float factorX = (float) productSubsetRegion.width / bandSubsetRegion.width;
                float factorY = (float) productSubsetRegion.height / bandSubsetRegion.height;

                Float[] solarIrradiances = imageMetadata.getSolarIrradiances();
                Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
                Product[][] tiles = new Product[subsetTileCols][subsetTileRows];
                for (String rasterFile : tileInfo.keySet()) {
                    boolean readTile = true;
                    int[] coords = tileInfo.get(rasterFile);
                    if(subsetDef != null && (subsetTileStartCol > coords[1] || subsetTileEndCol -1 < coords[1] ||
                            subsetTileStartRow > coords[0] || subsetTileEndRow - 1 < coords[0])){
                        readTile = false;
                    }
                    if (readTile) {
                        if(subsetDef != null) {
                            bandSubsetDef = new ProductSubsetDef();
                            if (tileCols == 1 && tileRows == 1) {
                                bandSubsetDef.setSubsetRegion(new PixelSubsetRegion(bandSubsetRegion, 0));
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
                                    coordHeight = bandSubsetRegion.height - (coords [0] * tileHeight - bandSubsetRegion.y);
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

                                bandSubsetDef.setSubsetRegion(new PixelSubsetRegion(new Rectangle(tileOffsetX, tileOffsetY, coordWidth, coordHeight), 0));
                            }
                        }
                        Path path = imageMetadata.getPath().resolve(rasterFile);
                        tiles[coords[1] - subsetTileStartCol][coords[0] - subsetTileStartRow] = ProductIO.readProduct(path.toFile(), bandSubsetDef);
                        this.tileRefs.add(new WeakReference<Product>(tiles[coords[1] - subsetTileStartCol][coords[0] - subsetTileStartRow]));
                    }
                }
                int levels = tiles[0][0].getBandAt(0).getSourceImage().getModel().getLevelCount();
                if (levels > product.getNumResolutionsMax()) {
                    product.setNumResolutionsMax(levels);
                }

                int colorWidth = Math.round(productSubsetRegion.width / factorX);
                int colorHeight = Math.round(productSubsetRegion.height / factorY);
                for (int i = 0; i < numBands; i++) {
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandInfos[i].getId())) {
                        Band targetBand = new ColorPaletteBand(bandInfos[i].getId(), pixelDataType, colorWidth, colorHeight, colorPaletteFilePath);
                        targetBand.setSpectralBandIndex(numBands > 1 ? i : -1);
                        targetBand.setSpectralWavelength(bandInfos[i].getCentralWavelength());
                        targetBand.setSpectralBandwidth(bandInfos[i].getBandwidth());
                        targetBand.setSolarFlux(solarIrradiances[i]);
                        targetBand.setUnit(bandInfos[i].getUnit());
                        targetBand.setNoDataValue(noDataValue);
                        targetBand.setNoDataValueUsed(true);
                        if (!bandInfos[i].getUnit().toLowerCase().contains("mw")) {
                            targetBand.setScalingFactor(1 / bandInfos[i].getGain());
                        } else {
                            targetBand.setScalingFactor(1 / bandInfos[i].getGain() * 0.1);
                        }
                        targetBand.setScalingOffset(bandInfos[i].getBias());
                        initBandGeoCoding(imageMetadata, targetBand, bandWidth, bandHeight, productSubsetRegion.width, productSubsetRegion.height, bandSubsetRegion, bandSubsetDef);
                        Band[][] srcBands = new Band[subsetTileCols][subsetTileRows];
                        for (int x = 0; x < subsetTileCols; x++) {
                            for (int y = 0; y < subsetTileRows; y++) {
                                srcBands[x][y] = tiles[x][y].getBandAt(bandInfos[i].getIndex());
                            }
                        }

                        MosaicMultiLevelSource bandSource =
                                new MosaicMultiLevelSource(srcBands,
                                                           bandSubsetRegion.width, bandSubsetRegion.height,
                                                           tileWidth, tileHeight, subsetTileCols, subsetTileRows,
                                                           levels, TYPE_MAP.get(pixelDataType),
                                                           imageMetadata.isGeocoded() ?
                                                                   targetBand.getGeoCoding() != null ?
                                                                           Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                                                           Product.findImageToModelTransform(product.getSceneGeoCoding()) :
                                                                   targetBand.getImageToModelTransform(),
                                                           bandSubsetRegion, new Point(subsetTileStartRow,subsetTileStartCol));
                        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
                        product.addBand(targetBand);
                    }
                }
                addMasks(product, imageMetadata, subsetDef);
                addGMLMasks(product, imageMetadata, subsetDef);

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
            }
            ref.clear();
        }

        super.close();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {
        // do nothing
    }

	private static GeoCoding addTiePointGridGeo(ImageMetadata metadata, int width, int height, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getCornerLonsLats();
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, width, height, cornerLonsLats[1]);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, width, height, cornerLonsLats[0]);
        if(subsetDef != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void initBandGeoCoding(ImageMetadata imageMetadata, Band band, int defaultBandWidth, int defaultBandHeight, int sceneWidth, int sceneHeight, Rectangle productSubsetDef, ProductSubsetDef subsetDef) {
        int bandWidth = band.getRasterWidth();
        int bandHeight = band.getRasterHeight();
        if (!imageMetadata.hasInsertPoint() && sceneWidth != bandWidth) {
                AffineTransform2D transform2D = new AffineTransform2D((float) sceneWidth / bandWidth, 0.0, 0.0, (float) sceneHeight / bandHeight, 0.0, 0.0);
                band.setImageToModelTransform(transform2D);
        }
        band.setGeoCoding(initBandGeoCoding(imageMetadata, defaultBandWidth, defaultBandHeight, sceneWidth, productSubsetDef, subsetDef));
    }

    private GeoCoding initBandGeoCoding(ImageMetadata imageMetadata, int bandWidth, int bandHeight, int sceneWidth, Rectangle productSubsetDef, ProductSubsetDef subsetDef) {
        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        String crsCode = imageMetadata.getCRSCode();
        try {
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            if (imageMetadata.hasInsertPoint()) {
                geoCoding =  ImageUtils.buildCrsGeoCoding(insertPoint.x, insertPoint.y,
                                                         insertPoint.stepX, insertPoint.stepY,
                                                         bandWidth, bandHeight,
                                                         crs, productSubsetDef);
            }else {
                if (sceneWidth != bandWidth) {
                    geoCoding = addTiePointGridGeo(imageMetadata, bandWidth, bandHeight, subsetDef);
                }
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return geoCoding;
    }

    private void addMasks(Product target, ImageMetadata metadata, ProductSubsetDef subsetDef) {
        ProductNodeGroup<Mask> maskGroup = target.getMaskGroup();
        if (!maskGroup.contains(Constants.NODATA)&& (subsetDef == null ||
                subsetDef.isNodeAccepted(Constants.NODATA))) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.NODATA, Constants.NODATA,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(Constants.SATURATED) && (subsetDef == null ||
                (subsetDef.getNodeNames() != null &&
                        subsetDef.isNodeAccepted(Constants.SATURATED)))) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.SATURATED, Constants.SATURATED,
                                                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                                                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private void addGMLMasks(Product target, ImageMetadata metadata, ProductSubsetDef subsetDef) {
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
                if(subsetDef == null || subsetDef.isNodeAccepted(maskName)) {
                    if (refBand != null) {
                        target.addMask(maskName, node, mask.description, colorIterator.next(), 0.5, refBand);
                    } else {
                        target.addMask(mask.name, node, mask.description, colorIterator.next(), 0.5);
                    }
                }
            }
        });
    }

    private static Band findReferenceBand(Product product, int width) {
        Band referenceBand = null;
        for (Band band : product.getBands()) {
            if (band.getRasterWidth() == width) {
                referenceBand = band;
                break;
            }
        }
        return referenceBand;
    }

    private static void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<File>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    public static GeoCoding buildGeoCoding(ImageMetadata maxResImageMetadata, Dimension defaultProductBounds, VolumeMetadata metadata, Rectangle productSubsetRegion, ProductSubsetDef subsetDef){
        if (maxResImageMetadata.hasInsertPoint()) {
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            String crsCode = maxResImageMetadata.getCRSCode();
            try {
                return ImageUtils.buildCrsGeoCoding(origin.x, origin.y,
                                                                   origin.stepX, origin.stepY,
                                                                   defaultProductBounds,
                                                                   CRS.decode(crsCode), productSubsetRegion);
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
        return addTiePointGridGeo(maxResImageMetadata, metadata.getSceneWidth() , metadata.getSceneHeight(), subsetDef);
    }
}
