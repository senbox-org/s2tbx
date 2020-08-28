package org.esa.s2tbx.dataio.pleiades;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.apache.commons.lang.StringUtils;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.ColorPaletteBand;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.pleiades.dimap.Constants;
import org.esa.s2tbx.dataio.pleiades.dimap.ImageMetadata;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.pleiades.dimap.VolumeMetadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.ColorIterator;
import org.esa.s2tbx.dataio.readers.GMLReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.subset.PixelSubsetRegion;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixCell;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;
import org.esa.snap.engine_utilities.util.PathUtils;
import org.esa.snap.jp2.reader.JP2ImageFile;
import org.esa.snap.jp2.reader.JP2LocalFile;
import org.esa.snap.jp2.reader.internal.JP2MosaicBandMatrixCell;
import org.esa.snap.lib.openjpeg.dataio.Utils;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;
import org.esa.snap.lib.openjpeg.utils.OpenJpegExecRetriever;
import org.esa.snap.lib.openjpeg.utils.OpenJpegUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.JAI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reader for Pleiades products.
 *
 * @author Cosmin Cara
 * modified 20191120 to read a specific area from the input product by Denisa Stefanescu
 */
public class PleiadesProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(PleiadesProductReader.class.getName());

    private Path localJp2CacheFolder;
    private VirtualDirEx productDirectory;
    private VolumeMetadata metadata;

    protected PleiadesProductReader(PleiadesProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);
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
        if (this.productDirectory != null) {
            throw new IllegalStateException("There is already a product directory.");
        }
        if (this.localJp2CacheFolder != null) {
            throw new IllegalStateException("There is already a local cache folder.");
        }

        boolean success = false;
        try {
            Object productInput = super.getInput(); // invoke the 'getInput' method from the parent class
            ProductSubsetDef subsetDef = super.getSubsetDef(); // invoke the 'getSubsetDef' method from the parent class

            Path inputPath = BaseProductReaderPlugIn.convertInputToPath(productInput);
            this.productDirectory = VirtualDirEx.build(inputPath);

            try (FilePathInputStream inputStream = this.productDirectory.getInputStream(Constants.ROOT_METADATA)) {
                this.metadata = VolumeMetadata.create(inputStream);
            }
            List<ImageMetadata> imageMetadataList = metadata.getImageMetadataList();
            if (imageMetadataList.isEmpty()) {
                throw new IOException("No raster found");
            }

            boolean isMultiSize = (this.metadata.getImageMetadataList().size() > 1);
            int defaultProductWidth = this.metadata.getSceneWidth();
            int defaultProductHeight = this.metadata.getSceneHeight();

            ImageMetadata maxResImageMetadata = this.metadata.getMaxResolutionImage();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductWidth, defaultProductHeight);
            } else {
                productDefaultGeoCoding = buildGeoCoding(maxResImageMetadata, defaultProductWidth, defaultProductHeight, metadata, null, null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductWidth, defaultProductHeight, isMultiSize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(this.metadata.getInternalReference(), this.metadata.getProductType(), productBounds.width, productBounds.height, this);
            product.setFileLocation(this.metadata.getPath().toFile());
            product.setStartTime(maxResImageMetadata.getProductStartTime());
            product.setEndTime(maxResImageMetadata.getProductEndTime());
            product.setDescription(maxResImageMetadata.getProductDescription());
            GeoCoding productGeoCoding = buildGeoCoding(maxResImageMetadata, defaultProductWidth, defaultProductHeight, metadata, productBounds, subsetDef);
            if (productGeoCoding instanceof TiePointGeoCoding){
                TiePointGeoCoding tiePointGeoCoding = (TiePointGeoCoding) productGeoCoding;
                product.addTiePointGrid(tiePointGeoCoding.getLatGrid());
                product.addTiePointGrid(tiePointGeoCoding.getLonGrid());
            }
            product.setSceneGeoCoding(productGeoCoding);

            Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
            product.setPreferredTileSize(defaultJAIReadTileSize);

            Path colorPaletteFilePath = ((PleiadesProductReaderPlugin)getReaderPlugIn()).getColorPaletteFilePath();
            for (ImageMetadata imageMetadata : imageMetadataList) {
                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                }
                int numBands = imageMetadata.getNumBands();
                ImageMetadata.BandInfo[] bandInfos = imageMetadata.getBandsInformation();

                int dataTypeFromMetadata = imageMetadata.getPixelDataType();
                int noDataValue = imageMetadata.getNoDataValue();

                BandMatrixData result = buildMosaicMatrix(imageMetadata, inputPath);

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
                if (bandGeoCoding == null) {
                    bandGeoCoding = productGeoCoding;
                }
                Float[] solarIrradiances = imageMetadata.getSolarIrradiances();

                for (int i = 0; i < bandInfos.length; i++) {
                    String bandName = bandInfos[i].getId();
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band band = new ColorPaletteBand(bandName, dataTypeFromMetadata, bandBounds.width, bandBounds.height, colorPaletteFilePath);
                        band.setSpectralBandIndex(numBands > 1 ? i : -1);
                        band.setSpectralWavelength(bandInfos[i].getCentralWavelength());
                        band.setSpectralBandwidth(bandInfos[i].getBandwidth());
                        band.setSolarFlux(solarIrradiances[i]);
                        band.setUnit(bandInfos[i].getUnit());
                        band.setNoDataValue(noDataValue);
                        band.setNoDataValueUsed(true);
                        if (!bandInfos[i].getUnit().toLowerCase().contains("mw")) {
                            band.setScalingFactor(1 / bandInfos[i].getGain());
                        } else {
                            band.setScalingFactor(1 / bandInfos[i].getGain() * 0.1);
                        }
                        band.setScalingOffset(bandInfos[i].getBias());
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

                        int maximumBandLevelCount = result.getLevelCount();
                        int bandLevelCount = DefaultMultiLevelModel.getLevelCount(bandBounds.width, bandBounds.height);
                        if (bandLevelCount > maximumBandLevelCount) {
                            bandLevelCount = maximumBandLevelCount;
                        }

                        DefaultMultiLevelImage multiLevelImage = result.buildBandSourceImage(bandLevelCount, noDataValue, defaultJAIReadTileSize,  bandIndex,
                                bandBounds, bandGeoCoding, imageToModelTransform);
                        band.setSourceImage(multiLevelImage);

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

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm)
            throws IOException {
        // do nothing
    }

    private void closeResources() throws IOException {
        try {
            if (this.localJp2CacheFolder != null) {
                List<Path> files = PathUtils.listFiles(this.localJp2CacheFolder);
                this.localJp2CacheFolder.toFile().deleteOnExit();
                if (files != null) {
                    for (Path file : files) {
                        file.toFile().deleteOnExit();
                    }
                }
                this.localJp2CacheFolder = null;
            }
        } finally {
            if (this.productDirectory != null) {
                this.productDirectory.close();
                this.productDirectory = null;
            }
        }
        System.gc();
    }

    private static Path initLocalCacheFolder(Path inputPath) throws IOException {
        String fullPathString = inputPath.toString();
        String md5sum = Utils.getMD5sum(fullPathString);
        if (md5sum == null) {
            throw new IllegalStateException("Unable to get md5sum of path '" + fullPathString+"'.");
        }
        String readerDirName = "pleiades-reader";//getReaderCacheDir();
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

    private static TiePointGeoCoding buildTiePointGridGeoCoding(ImageMetadata metadata, int width, int height, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getCornerLonsLats();
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, width, height, cornerLonsLats[1]);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, width, height, cornerLonsLats[0]);
        if (subsetDef != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private static AffineTransform2D buildBandTransform(ImageMetadata imageMetadata, Rectangle productBounds, Rectangle bandBounds) {
        if (!imageMetadata.hasInsertPoint() && (productBounds.width != bandBounds.width || productBounds.height != bandBounds.height)) {
            float m00 = (float) productBounds.width / bandBounds.width;
            float m11 = (float) productBounds.height / bandBounds.height;
            return new AffineTransform2D(m00, 0.0, 0.0, m11, 0.0, 0.0);
        }
        return null;
    }

    private static GeoCoding buildBandGeoCoding(ImageMetadata imageMetadata, int defaultBandWidth, int defaultBandHeight, int defaultProductWidth,
                                                int defaultProductHeight, Rectangle bandBounds, ProductSubsetDef bandSubsetDef)
                                                throws FactoryException, TransformException {

        GeoCoding geoCoding = null;
        ImageMetadata.InsertionPoint insertPoint = imageMetadata.getInsertPoint();
        String crsCode = imageMetadata.getCRSCode();
        if (imageMetadata.hasInsertPoint()) {
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            geoCoding = ImageUtils.buildCrsGeoCoding(insertPoint.x, insertPoint.y, insertPoint.stepX, insertPoint.stepY, defaultBandWidth, defaultBandHeight, crs, bandBounds);
        } else if (defaultProductWidth != defaultBandWidth || defaultProductHeight != defaultBandHeight) {
            geoCoding = buildTiePointGridGeoCoding(imageMetadata, defaultBandWidth, defaultBandHeight, bandSubsetDef);
        }
        return geoCoding;
    }

    private static void addMasks(Product target, ImageMetadata metadata, ProductSubsetDef subsetDef) {
        ProductNodeGroup<Mask> maskGroup = target.getMaskGroup();
        if (!maskGroup.contains(Constants.NODATA) && (subsetDef == null || subsetDef.isNodeAccepted(Constants.NODATA))) {
            int noDataValue = metadata.getNoDataValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.NODATA, Constants.NODATA,
                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                    String.valueOf(noDataValue), Color.BLACK, 0.5));
        }
        if (!maskGroup.contains(Constants.SATURATED) && (subsetDef == null || (subsetDef.getNodeNames() != null && subsetDef.isNodeAccepted(Constants.SATURATED)))) {
            int saturatedValue = metadata.getSaturatedValue();
            maskGroup.add(Mask.BandMathsType.create(Constants.SATURATED, Constants.SATURATED,
                    target.getSceneRasterWidth(), target.getSceneRasterHeight(),
                    String.valueOf(saturatedValue), Color.ORANGE, 0.5));
        }
    }

    private static void addGMLMasks(Product target, ImageMetadata metadata, boolean isMultiSize, ProductSubsetDef subsetDef) {
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
                if (subsetDef == null || subsetDef.isNodeAccepted(maskName)) {
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

    public static GeoCoding buildGeoCoding(ImageMetadata maxResImageMetadata, int defaultProductWidth, int defaultProductHeight, VolumeMetadata metadata,
                                           Rectangle productSubsetRegion, ProductSubsetDef subsetDef)
            throws FactoryException, TransformException {

        if (maxResImageMetadata.hasInsertPoint()) {
            ImageMetadata.InsertionPoint origin = maxResImageMetadata.getInsertPoint();
            CoordinateReferenceSystem mapCRS = CRS.decode(maxResImageMetadata.getCRSCode());
            return ImageUtils.buildCrsGeoCoding(origin.x, origin.y, origin.stepX, origin.stepY, defaultProductWidth, defaultProductHeight, mapCRS, productSubsetRegion);
        }
        return buildTiePointGridGeoCoding(maxResImageMetadata, metadata.getSceneWidth() , metadata.getSceneHeight(), subsetDef);
    }

    private BandMatrixData buildMosaicMatrix(ImageMetadata imageMetadata, Path inputPath)
            throws IOException, InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException {

        int tileRows = imageMetadata.getTileRowsCount();
        int tileCols = imageMetadata.getTileColsCount();
        Map<String, int[]> tileInfo = imageMetadata.getRasterTileInfo();
        String[][] matrixFiles = new String[tileRows][tileCols];
        boolean allFilesJP2 = true;
        boolean allFilesGeoTiff = true;
        for (Map.Entry<String, int[]> entry : tileInfo.entrySet()) {
            int[] matrixCoordinates = entry.getValue();
            String filePath = entry.getKey();
            boolean extensionMatches = Arrays.stream(GeoTiffProductReaderPlugIn.TIFF_FILE_EXTENSION).anyMatch(filePath.toLowerCase()::endsWith);
            if (!extensionMatches) {
                allFilesGeoTiff = false;
            }
            if (!StringUtils.endsWithIgnoreCase(entry.getKey(), "jp2")) {
                allFilesJP2 = false;
            }
            matrixFiles[matrixCoordinates[0]][matrixCoordinates[1]] = entry.getKey();
        }
        if (allFilesJP2) {
            if (this.localJp2CacheFolder == null) {
                this.localJp2CacheFolder = initLocalCacheFolder(inputPath);
            }
            return buildJP2MosaicMatrix(imageMetadata, matrixFiles, this.localJp2CacheFolder);
        }
        if (allFilesGeoTiff) {
            Path geotiffMatrixTempFolder = this.productDirectory.makeLocalTempFolder();
            return buildGeoTiffMosaicMatrix(imageMetadata, matrixFiles, geotiffMatrixTempFolder);
        }
        throw new IllegalStateException("Unknown image files.");
    }

    private static BandGeoTiffMatrixData buildGeoTiffMosaicMatrix(ImageMetadata imageMetadata, String[][] matrixFiles, Path localTempFolder)
            throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, InstantiationException {

        int tileRows = imageMetadata.getTileRowsCount();
        int tileCols = imageMetadata.getTileColsCount();
        int levelCount = 0;
        int dataType = 0;
        Path imagesMetadataParentPath = imageMetadata.getPath();
        MosaicMatrix mosaicMatrix = new MosaicMatrix(tileRows, tileCols);
        for (int rowIndex = 0; rowIndex < tileRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < tileCols; columnIndex++) {
                String imageRelativeFilePath = matrixFiles[rowIndex][columnIndex];
                int cellWidth;
                int cellHeight;
                int dataBufferType;
                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, imageRelativeFilePath)) {
                    cellWidth = geoTiffImageReader.getImageWidth();
                    cellHeight = geoTiffImageReader.getImageHeight();
                    dataBufferType = geoTiffImageReader.getSampleModel().getDataType();
                }
                int cellLevelCount = DefaultMultiLevelModel.getLevelCount(cellWidth, cellHeight);
                if (columnIndex == 0 && rowIndex == 0) {
                    dataType = dataBufferType;
                    levelCount = cellLevelCount;
                } else {
                    if (dataType != dataBufferType) {
                        throw new IllegalStateException("Different data type count: rowIndex=" + rowIndex + ", columnIndex=" + columnIndex + ", dataType=" + dataType + ", dataBufferType=" + dataBufferType + ".");
                    }
                    if (levelCount != cellLevelCount) {
                        throw new IllegalStateException("Different level count: rowIndex="+rowIndex+", columnIndex="+columnIndex+", levelCount="+levelCount+", cellLevelCount="+cellLevelCount+".");
                    }
                }
                GeoTiffMatrixCell matrixCell = new GeoTiffMatrixCell(cellWidth, cellHeight, dataBufferType, imagesMetadataParentPath, imageRelativeFilePath, localTempFolder);
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, true, true);
            }
        }

        validateMatrix(imageMetadata, dataType, mosaicMatrix);
        return new BandGeoTiffMatrixData(dataType, levelCount, mosaicMatrix);
    }

    private static BandJP2MatrixData buildJP2MosaicMatrix(ImageMetadata imageMetadata, String[][] matrixFiles, Path cacheFolder) throws IOException, InterruptedException {
        int tileRows = imageMetadata.getTileRowsCount();
        int tileCols = imageMetadata.getTileColsCount();
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
                JP2LocalFile jp2LocalFile = new JP2LocalFileImpl(jp2File);
                JP2ImageFile jp2ImageFile = new JP2ImageFile(jp2LocalFile);
                int cellWidth = tileLayout.width;
                int cellHeight = tileLayout.height;
                JP2MosaicBandMatrixCell matrixCell = new JP2MosaicBandMatrixCell(jp2ImageFile, cacheFolder, tileLayout, cellWidth, cellHeight);
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, false, false);
            }
        }

        validateMatrix(imageMetadata, dataType, mosaicMatrix);
        return new BandJP2MatrixData(dataType, levelCount, mosaicMatrix);
    }

    private static void validateMatrix(ImageMetadata imageMetadata, int dataType, MosaicMatrix mosaicMatrix) {
        int dataTypeFromMetadata = imageMetadata.getPixelDataType();
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
    }

    private static class JP2LocalFileImpl implements JP2LocalFile {

        private final Path jp2File;

        private JP2LocalFileImpl(Path jp2File) {
            this.jp2File = jp2File;
        }

        @Override
        public Path getLocalFile() throws IOException {
            return this.jp2File;
        }
    }
}
