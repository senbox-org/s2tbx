package org.esa.s2tbx.dataio.spot6;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.ColorIterator;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.s2tbx.dataio.spot6.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.spot6.dimap.Spot6Constants;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.spot6.dimap.VolumeMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.engine_utilities.util.Pair;
import org.esa.snap.engine_utilities.util.PathUtils;
import org.esa.snap.jp2.reader.JP2ImageFile;
import org.esa.snap.jp2.reader.JP2LocalFile;
import org.esa.snap.jp2.reader.internal.JP2MatrixBandMultiLevelSource;
import org.esa.snap.jp2.reader.internal.JP2MosaicBandMatrixCell;
import org.esa.snap.jp2.reader.metadata.Jp2XmlMetadata;
import org.esa.snap.lib.openjpeg.dataio.Utils;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;
import org.esa.snap.lib.openjpeg.utils.OpenJpegExecRetriever;
import org.esa.snap.lib.openjpeg.utils.OpenJpegUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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

    static {
        XmlMetadataParserFactory.registerParser(Jp2XmlMetadata.class, new XmlMetadataParser<>(Jp2XmlMetadata.class));
    }

    private Path localCacheFolder;
    private VirtualDirEx productDirectory;
    private VolumeMetadata metadata;

    protected Spot6ProductReader(Spot6ProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);
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
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand,
                                          int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {
        // do nothing
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        if (this.productDirectory != null) {
            throw new IllegalStateException("There is already a product directory.");
        }
        if (this.localCacheFolder != null) {
            throw new IllegalStateException("There is already a local cache folder.");
        }

        boolean success = false;
        try {
            Object productInput = super.getInput(); // invoke the 'getInput' method from the parent class
            ProductSubsetDef subsetDef = super.getSubsetDef(); // invoke the 'getSubsetDef' method from the parent class

            Path inputPath = BaseProductReaderPlugIn.convertInputToPath(productInput);
            this.productDirectory = VirtualDirEx.build(inputPath);

            this.localCacheFolder = initLocalCacheFolder(inputPath);

            try (FilePathInputStream metadataInputStream = this.productDirectory.getInputStream(Spot6Constants.ROOT_METADATA)) {
                metadata = VolumeMetadata.create(metadataInputStream);
            }
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.isEmpty()) {
                throw new IOException("No raster found");
            }
            boolean isMultiSize = (metadata.getImageMetadataList().size() > 1);
            int defaultProductWidth = metadata.getSceneWidth();
            int defaultProductHeight = metadata.getSceneHeight();
            ImageMetadata maxResImageMetadata = metadata.getMaxResolutionImage();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductWidth, defaultProductHeight);
            } else {
                productDefaultGeoCoding = buildProductGeoCoding(maxResImageMetadata, defaultProductWidth, defaultProductHeight, null, null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, isMultiSize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(metadata.getInternalReference(), metadata.getProductType(), productBounds.width, productBounds.height);
            product.setFileLocation(metadata.getPath().toFile());
            product.setStartTime(maxResImageMetadata.getProductStartTime());
            product.setEndTime(maxResImageMetadata.getProductEndTime());
            product.setDescription(maxResImageMetadata.getProductDescription());

            GeoCoding productGeoCoding = buildProductGeoCoding(maxResImageMetadata, defaultProductWidth, defaultProductHeight, productBounds, subsetDef);
            if (productGeoCoding instanceof TiePointGeoCoding) {
                TiePointGeoCoding tiePointGeoCoding = (TiePointGeoCoding) productGeoCoding;
                product.addTiePointGrid(tiePointGeoCoding.getLatGrid());
                product.addTiePointGrid(tiePointGeoCoding.getLonGrid());
            }
            product.setSceneGeoCoding(productGeoCoding);

            Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
            product.setPreferredTileSize(defaultJAIReadTileSize);

            Path colorPaletteFilePath = ((Spot6ProductReaderPlugin) getReaderPlugIn()).getColorPaletteFilePath();
            for (ImageMetadata imageMetadata : imageMetadataList) {
                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                }
                ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();

                int dataTypeFromMetadata = imageMetadata.getPixelDataType();
                int noDataValue = imageMetadata.getNoDataValue();
                Float[] solarIrradiances = imageMetadata.getSolarIrradiances();
                double[][] scalingAndOffsets = imageMetadata.getScalingAndOffsets();

                BandMatrixData result = buildMosaicMatrix(imageMetadata, this.localCacheFolder);

                int defaultBandWidth = result.getMosaicMatrix().computeTotalWidth();
                int defaultBandHeight = result.getMosaicMatrix().computeTotalHeight();

                ProductSubsetDef bandSubsetDef = null;
                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
                } else {
                    GeoCoding bandDefaultGeoCoding = buildBandGeoCoding(imageMetadata, defaultBandWidth, defaultBandHeight, defaultProductWidth, defaultProductHeight, null, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductWidth,
                                                                                    defaultProductHeight, defaultBandWidth, defaultBandHeight, isMultiSize);
                    bandSubsetDef = new ProductSubsetDef();
                    bandSubsetDef.setSubsetRegion(new PixelSubsetRegion(bandBounds, 0));
                }
                if (bandBounds.isEmpty()) {
                    continue;
                }
                product.setNumResolutionsMax(result.getLevelCount());
                AffineTransform2D transform2D = buildBandTransform(imageMetadata, productBounds, bandBounds);
                GeoCoding bandGeoCoding = buildBandGeoCoding(imageMetadata, defaultBandWidth, defaultBandHeight, defaultProductWidth, defaultProductHeight, bandBounds, bandSubsetDef);
                for (int i = 0; i < bandInfos.length; i++) {
                    String bandName = bandInfos[i].getId();
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band band = new ColorPaletteBand(bandName, dataTypeFromMetadata, bandBounds.width, bandBounds.height, colorPaletteFilePath);
                        band.setSpectralBandIndex(bandInfos.length > 1 ? i : -1);
                        band.setSpectralWavelength(bandInfos[i].getCentralWavelength());
                        band.setSpectralBandwidth(bandInfos[i].getBandwidth());
                        band.setSolarFlux(solarIrradiances[i]);
                        band.setUnit(bandInfos[i].getUnit());
                        band.setNoDataValue(noDataValue);
                        band.setNoDataValueUsed(true);
                        band.setScalingFactor(scalingAndOffsets[i][0] / bandInfos[i].getGain());
                        band.setScalingOffset(scalingAndOffsets[i][1] * bandInfos[i].getBias());
                        if (transform2D != null) {
                            band.setImageToModelTransform(transform2D);
                        }
                        if (bandGeoCoding != null) {
                            band.setGeoCoding(bandGeoCoding);
                        }
                        AffineTransform imageToModelTransform;
                        if (imageMetadata.isGeocoded()) {
                            GeoCoding geoCoding = band.getGeoCoding();
                            if (geoCoding == null) {
                                geoCoding = product.getSceneGeoCoding();
                            }
                            imageToModelTransform = Product.findImageToModelTransform(geoCoding);
                        } else {
                            imageToModelTransform = band.getImageToModelTransform();
                        }
                        int bandIndex = bandInfos[i].getIndex();
                        JP2MatrixBandMultiLevelSource multiLevelSource = new JP2MatrixBandMultiLevelSource(result.getLevelCount(), result.getMosaicMatrix(), bandBounds, imageToModelTransform, bandIndex,
                                                                                    (double)noDataValue, null, defaultJAIReadTileSize);
                        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
                        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));
                        product.addBand(band);
                    }
                }

                addMasks(product, imageMetadata, subsetDef);
                addGMLMasks(product, imageMetadata, isMultiSize, subsetDef);
            }

            success = true;

            return product;
        } catch (RuntimeException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException(exception);
        } finally {
            if (!success) {
                closeResources();
            }
        }
    }

    private void closeResources() throws IOException {
        try {
            if (this.localCacheFolder != null) {
                List<Path> files = PathUtils.listFiles(this.localCacheFolder);
                this.localCacheFolder.toFile().deleteOnExit();
                if (files != null) {
                    for (Path file : files) {
                        file.toFile().deleteOnExit();
                    }
                }
                this.localCacheFolder = null;
            }
        } finally {
            if (this.productDirectory != null) {
                this.productDirectory.close();
                this.productDirectory = null;
            }
        }
        System.gc();
    }

    private static AffineTransform2D buildBandTransform(ImageMetadata imageMetadata, Rectangle productBounds, Rectangle bandBounds) {
        if (!imageMetadata.hasInsertPoint() && (productBounds.width != bandBounds.width || productBounds.height != bandBounds.height)) {
            float m00 = (float) productBounds.width / bandBounds.width;
            float m11 = (float) productBounds.height / bandBounds.height;
            return new AffineTransform2D(m00, 0.0, 0.0, m11, 0.0, 0.0);
        }
        return null;
    }

    private static BandMatrixData buildMosaicMatrix(ImageMetadata imageMetadata, Path cacheDir) throws IOException, InterruptedException {
        int dataTypeFromMetadata = imageMetadata.getPixelDataType();
        int tileRows = imageMetadata.getTileRowsCount();
        int tileCols = imageMetadata.getTileColsCount();
        Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
        String[][] matrixFiles = new String[tileRows][tileCols];
        for (Map.Entry<String, int[]> entry : tileInfo.entrySet()) {
            int[] matrixCoordinates = entry.getValue();
            matrixFiles[matrixCoordinates[0]][matrixCoordinates[1]] = entry.getKey();
        }
        int levelCount = 0;
        int dataType = 0;
        MosaicMatrix mosaicMatrix = new MosaicMatrix(tileRows, tileCols);
        for (int rowIndex = 0; rowIndex < tileRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < tileCols; columnIndex++) {
                Path jp2File = imageMetadata.getPath().resolve(matrixFiles[rowIndex][columnIndex]);
                TileLayout tileLayout;
                if (OpenJpegUtils.canReadJP2FileHeaderWithOpenJPEG()) {
                    tileLayout = OpenJpegUtils.getTileLayoutWithOpenJPEG(OpenJpegExecRetriever.getOpjDump(), jp2File);
                } else {
                    boolean canSetFilePosition = true;//!imageFilePath.getVirtualDir().isArchive();
                    tileLayout = OpenJpegUtils.getTileLayoutWithInputStream(jp2File, 5 * 1024, canSetFilePosition);
                }
                if (rowIndex == 0 && columnIndex == 0) {
                    levelCount = tileLayout.numResolutions;
                    dataType = tileLayout.dataType;
                } else {
                    if (levelCount != tileLayout.numResolutions) {
                        throw new IllegalStateException("Different level count: rowIndex="+rowIndex+", columnIndex="+columnIndex+", levelCount="+levelCount+", tileLayout.numResolutions="+tileLayout.numResolutions+".");
                    }
                    if (dataType != tileLayout.dataType) {
                        throw new IllegalStateException("Different data type count: rowIndex="+rowIndex+", columnIndex="+columnIndex+", dataType="+dataType+", tileLayout.dataType="+tileLayout.dataType+".");
                    }
                }
                JP2LocalFile jp2LocalFile = new SpotJp2LocalFile(jp2File);
                JP2ImageFile jp2ImageFile = new JP2ImageFile(jp2LocalFile);
                int cellWidth = tileLayout.width;
                int cellHeight = tileLayout.height;
                JP2MosaicBandMatrixCell matrixCell = new JP2MosaicBandMatrixCell(jp2ImageFile, cacheDir, tileLayout, cellWidth, cellHeight);
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, false, false);
            }
        }
        int bandDataType = ImageManager.getProductDataType(dataType);
        if (bandDataType != dataTypeFromMetadata) {
            throw new IllegalStateException("Different data types: bandDataType="+bandDataType+", dataTypeFromMetadata="+dataTypeFromMetadata+".");
        }
        int defaultBandWidth = mosaicMatrix.computeTotalWidth();
        if (defaultBandWidth != imageMetadata.getRasterWidth()) {
            throw new IllegalStateException("Different widths.");
        }
        int defaultBandHeight = mosaicMatrix.computeTotalHeight();
        if (defaultBandHeight != imageMetadata.getRasterHeight()) {
            throw new IllegalStateException("Different height.");
        }
        return new BandMatrixData(dataType, levelCount, mosaicMatrix);
    }

    private static GeoCoding buildBandGeoCoding(ImageMetadata imageMetadata, int defaultBandWidth, int defaultBandHeight, int defaultProductWidth, int defaultProductHeight,
                                                Rectangle bandBounds, ProductSubsetDef bandSubsetDef)
                                                throws FactoryException, TransformException {

        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        if (imageMetadata.hasInsertPoint()) {
            CoordinateReferenceSystem crs = CRS.decode(imageMetadata.getCRSCode());
            geoCoding = ImageUtils.buildCrsGeoCoding(insertPoint.x, insertPoint.y, insertPoint.stepX, insertPoint.stepY, defaultBandWidth, defaultBandHeight, crs, bandBounds);
        } else if (defaultProductWidth != defaultBandWidth || defaultProductHeight != defaultBandHeight) {
            geoCoding = buildTiePointGridGeoCoding(imageMetadata, defaultBandWidth, defaultBandHeight, bandSubsetDef);
        }

        return geoCoding;
    }

    private static void addMasks(Product product, ImageMetadata metadata, ProductSubsetDef subsetDef) {
        ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
        if (!maskGroup.contains(Spot6Constants.NODATA) && (subsetDef == null || subsetDef.isNodeAccepted(Spot6Constants.NODATA))) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(Spot6Constants.NODATA, Spot6Constants.NODATA,
                    product.getSceneRasterWidth(), product.getSceneRasterHeight(),
                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(Spot6Constants.SATURATED) && (subsetDef == null || subsetDef.isNodeAccepted(Spot6Constants.SATURATED))) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(Spot6Constants.SATURATED, Spot6Constants.SATURATED,
                    product.getSceneRasterWidth(), product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private static void addGMLMasks(Product product, ImageMetadata metadata, boolean isMultiSize, ProductSubsetDef subsetDef) {
        List<ImageMetadata.MaskInfo> gmlMasks = metadata.getMasks();
        final Iterator<Color> colorIterator = ColorIterator.create();
        Band refBand = findReferenceBand(product, metadata.getRasterWidth());
        gmlMasks.stream().forEach(mask -> {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, String.format("Parsing mask %s of component %s", mask.name, metadata.getFileName()));
            }
            VectorDataNode node = GMLReader.parse(mask.name, mask.path);
            if (node != null && node.getFeatureCollection().size() > 0) {
                node.setOwner(product);
                String maskName = mask.name;
                if (isMultiSize) {
                    String resolution = "_" + new DecimalFormat("#.#").format(metadata.getPixelSize()) + "m";
                    maskName += resolution.endsWith(".") ? resolution.substring(0, resolution.length() - 1) : resolution;
                }
                if (subsetDef == null || subsetDef.isNodeAccepted(maskName)) {
                    if (refBand != null) {
                        product.addMask(maskName, node, mask.description, colorIterator.next(), 0.5, refBand);
                    } else {
                        product.addMask(mask.name, node, mask.description, colorIterator.next(), 0.5);
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

    private static GeoCoding buildProductGeoCoding(ImageMetadata maxResImageMetadata, int defaultRasterWidth, int defaultRasterHeight,
                                                   Rectangle productSubsetRegion, ProductSubsetDef subsetDef)
                                                   throws FactoryException, TransformException {

        if (maxResImageMetadata.hasInsertPoint()) {
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            CoordinateReferenceSystem mapCRS = CRS.decode(maxResImageMetadata.getCRSCode());
            return ImageUtils.buildCrsGeoCoding(origin.x, origin.y, origin.stepX, origin.stepY, defaultRasterWidth, defaultRasterHeight,
                                                mapCRS, productSubsetRegion, 0.5d, 0.5d);

        }
        return buildTiePointGridGeoCoding(maxResImageMetadata, defaultRasterWidth, defaultRasterHeight, subsetDef);
    }

    private static TiePointGeoCoding buildTiePointGridGeoCoding(ImageMetadata metadata, int defaultRasterWidth, int defaultRasterHeight, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getCornerLonsLats();
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private static Path initLocalCacheFolder(Path inputPath) throws IOException {
        String fullPathString = inputPath.toString();
        String md5sum = Utils.getMD5sum(fullPathString);
        if (md5sum == null) {
            throw new IOException("Unable to get md5sum of path " + fullPathString);
        }
        String readerDirName = "spot6-reader";//getReaderCacheDir();
        String productName = inputPath.getFileName().toString();
        Path cacheFolderPath = SystemUtils.getCacheDir().toPath();
        cacheFolderPath = cacheFolderPath.resolve("s2tbx");
        cacheFolderPath = cacheFolderPath.resolve(readerDirName);
        cacheFolderPath = cacheFolderPath.resolve(md5sum);
        cacheFolderPath = cacheFolderPath.resolve(productName);
        Path cacheDir = cacheFolderPath;
        if (!Files.exists(cacheDir)) {
            Files.createDirectories(cacheDir);
        }
        if (!Files.exists(cacheDir) || !Files.isDirectory(cacheDir) || !Files.isWritable(cacheDir)) {
            throw new IOException("Can't access package cache directory");
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Successfully set up cache dir for product " + productName + " to " + cacheDir.toString());
        }
        return cacheDir;
    }

    private static class SpotJp2LocalFile implements JP2LocalFile {

        private final Path jp2File;

        public SpotJp2LocalFile(Path jp2File) {
            this.jp2File = jp2File;
        }

        @Override
        public Path getLocalFile() throws IOException {
            return this.jp2File;
        }
    }

    private static class BandMatrixData {

        private final int dataType;
        private final int levelCount;
        private final MosaicMatrix mosaicMatrix;

        private BandMatrixData(int dataType, int levelCount, MosaicMatrix mosaicMatrix) {
            this.dataType = dataType;
            this.levelCount = levelCount;
            this.mosaicMatrix = mosaicMatrix;
        }

        public int getDataType() {
            return dataType;
        }

        public int getLevelCount() {
            return levelCount;
        }

        public MosaicMatrix getMosaicMatrix() {
            return mosaicMatrix;
        }
    }
}
