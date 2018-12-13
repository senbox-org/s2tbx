package org.esa.s2tbx.dataio.worldviewESAarchive;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.worldviewESAarchive.common.WorldViewESAarchiveConstants;
import org.esa.s2tbx.dataio.worldviewESAarchive.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.worldviewESAarchive.metadata.TileComponent;
import org.esa.s2tbx.dataio.worldviewESAarchive.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldviewESAarchive.metadata.WorldViewESAarchiveMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.jai.JAIUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Basic reader for WorldView 2 ESA archive products.
 *
 * @author Denisa Stefanescu
 */

public class WorldViewESAarchiveProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(WorldViewESAarchiveProductReader.class.getName());

    private static final String EXCLUSION_STRING = ".SI.XML";
    private VirtualDirEx productDirectory;
    private Product product;
    private WorldViewESAarchiveMetadata metadata;
    private int bandDataType;
    private int numMultiSpectralBands;
    private HashMap<Product, String> tilesMultiSpectral;
    private HashMap<Product, String> tilesPanchromatic;
    private Set<WeakReference<Product>> tileRefs;
    private int minRows = 0;
    private int minCols = 0;

    private final static Map<Integer, Integer> typeMap = new HashMap<Integer, Integer>() {{
        put(ProductData.TYPE_UINT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_INT8, DataBuffer.TYPE_BYTE);
        put(ProductData.TYPE_UINT16, DataBuffer.TYPE_USHORT);
        put(ProductData.TYPE_INT16, DataBuffer.TYPE_SHORT);
        put(ProductData.TYPE_UINT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_INT32, DataBuffer.TYPE_INT);
        put(ProductData.TYPE_FLOAT32, DataBuffer.TYPE_FLOAT);
    }};

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    protected WorldViewESAarchiveProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        this.tilesMultiSpectral = new HashMap<>();
        tilesPanchromatic = new HashMap<>();
        this.tileRefs = new HashSet<>();
    }

    /**
     * Force deletion of directory
     *
     * @param path path to file/directory
     * @return return true if successful
     */
    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return (path.delete());
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {

        WorldViewESAarchiveProductReaderPlugin readerPlugin = (WorldViewESAarchiveProductReaderPlugin) getReaderPlugIn();
        final File inputFile = getInputFile();
        this.productDirectory = readerPlugin.getInput(getInput());
        String productFilePath = this.productDirectory.getBasePath();
        String fileName;

        if (this.productDirectory.isCompressed()) {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf(WorldViewESAarchiveConstants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = productFilePath.substring(productFilePath.lastIndexOf("\\") + 1, productFilePath.lastIndexOf("."));
        }

        this.metadata = WorldViewESAarchiveMetadata.create(this.productDirectory.getFile(fileName + WorldViewESAarchiveConstants.METADATA_FILE_SUFFIX).toPath());
        if (metadata != null) {
            Set<String> selectedProductFiles = new HashSet<>();
            String dir = null;
            File[] imageDirectoryFileList = null;
            String[] directoryFileList = null;
            if (productDirectory.isArchive() || !productDirectory.exists(fileName)) {
                this.metadata.unZipImageFiles(this.productDirectory.getFile(fileName + WorldViewESAarchiveConstants.ARCHIVE_FILE_EXTENSION).toPath().toString());
                dir = metadata.getImageDirectoryPath();
                productDirectory = VirtualDirEx.create(new File(dir));
                imageDirectoryFileList = new File(dir).listFiles();
            } else {
                productDirectory.setFolderDepth(4);
                directoryFileList = productDirectory.listAllFiles();
            }

            //the input is a ZIP file or contains a ZIP file
            if (imageDirectoryFileList != null) {
                for (File file : imageDirectoryFileList) {
                    Matcher matcher = Pattern.compile(WorldViewESAarchiveConstants.PATH_ZIP_FILE_NAME_PATTERN).matcher(file.getName());
                    while (matcher.find()) {
                        if (file.isDirectory()) {
                            for (File subfile : file.listFiles()) {
                                if ((subfile.getName().contains(WorldViewESAarchiveConstants.METADATA_EXTENSION)
                                        || subfile.getName().contains(WorldViewESAarchiveConstants.IMAGE_EXTENSION))
                                        && !subfile.getName().contains(WorldViewESAarchiveConstants.METADATA_FILE_SUFFIX)
                                        && !subfile.getName().contains(EXCLUSION_STRING)) {
                                    selectedProductFiles.add(file.getName() + File.separator + subfile.getName());
                                }
                            }
                        } else {
                            if ((file.getName().contains(WorldViewESAarchiveConstants.METADATA_EXTENSION)
                                    || file.getName().contains(WorldViewESAarchiveConstants.IMAGE_EXTENSION))
                                    && !file.getName().contains(WorldViewESAarchiveConstants.METADATA_FILE_SUFFIX)
                                    && !file.getName().contains(EXCLUSION_STRING)) {
                                selectedProductFiles.add(file.getName());
                            }
                        }
                    }
                }
            }
            // if the input does not contain ZIP files
            if (directoryFileList != null) {
                for (String file : directoryFileList) {
                    if ((file.contains(WorldViewESAarchiveConstants.METADATA_EXTENSION)
                            || file.contains(WorldViewESAarchiveConstants.IMAGE_EXTENSION))
                            && !file.contains(WorldViewESAarchiveConstants.METADATA_FILE_SUFFIX)
                            && !file.contains(EXCLUSION_STRING)) {
                        selectedProductFiles.add(file);
                    }
                }
            }

            List<TileMetadata> tileMetadataList = new ArrayList<>();
            for (String fileMetadata : selectedProductFiles) {
                if (fileMetadata.endsWith(WorldViewESAarchiveConstants.METADATA_EXTENSION)) {
                    TileMetadata tileMetadata = TileMetadata.create(productDirectory.getFile(fileMetadata).toPath());
                    tileMetadataList.add(tileMetadata);
                }
            }
            if (tileMetadataList.size() != 0) {
                int width = 0;
                int height = 0;
                double stepSize = 0.0;
                double originX = 0.0;
                double originY = 0.0;
                String crsCode = null;
                for (TileMetadata tileMetadata : tileMetadataList) {
                    TileComponent tileComponent = tileMetadata.getTileComponent();
                    if (tileComponent.getBandID().equals("P")) {
                        width = tileComponent.getNumColumns();
                        height = tileComponent.getNumRows();
                        stepSize = tileComponent.getStepSize();
                        originX = tileComponent.getOriginX();
                        originY = tileComponent.getOriginY();
                        crsCode = tileComponent.computeCRSCode();
                    }
                }
                this.product = new Product(this.metadata.getProductName(), WorldViewESAarchiveConstants.PRODUCT_TYPE, width, height);
                this.product.setStartTime(this.metadata.getProductStartTime());
                this.product.setEndTime(this.metadata.getProductEndTime());
                this.product.setDescription(this.metadata.getProductDescription());
                this.product.setProductReader(this);
                this.product.setFileLocation(inputFile);
                for (TileMetadata tileMetadata : tileMetadataList) {
                    this.product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                }
                try {
                    assert crsCode != null;
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                            width, height,
                            originX, originY,
                            stepSize, stepSize);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
                generateProductLists(selectedProductFiles, tileMetadataList);
                for (TileMetadata tileMetadata : tileMetadataList) {
                    this.product.getMetadataRoot().addElement(tileMetadata.getRootElement());

                    int bandWidth = tileMetadata.getRasterWidth();
                    int bandHeight = tileMetadata.getRasterHeight();

                    int pixelDataType = tileMetadata.getPixelDataType();

                    String[] bandNames;
                    if (numMultiSpectralBands < 4) {
                        bandNames = WorldViewESAarchiveConstants.NATURAL_COLORS;
                    }
                    if (numMultiSpectralBands == 4) {
                        bandNames = WorldViewESAarchiveConstants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
                    } else {
                        bandNames = WorldViewESAarchiveConstants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
                    }
                    Map<String, int[]> tileInfo = tileMetadata.getRasterTileInfo();
                    int tileRows = tileMetadata.getTileRowsCount();
                    int tileCols = tileMetadata.getTileColsCount();
                    Product[][] tiles = new Product[tileCols][tileRows];
                    for (String rasterFile : tileInfo.keySet()) {
                        int[] coords = tileInfo.get(rasterFile);
                        if (imageDirectoryFileList != null) {
                            for (String file : selectedProductFiles) {
                                if (file.contains(rasterFile)) {
                                    rasterFile = file;
                                }
                            }
                        }
                        tiles[coords[1]][coords[0]] = ProductIO.readProduct(productDirectory.getFile(rasterFile));
                        tileRefs.add(new WeakReference<Product>(tiles[coords[1]][coords[0]]));
                    }
                    int levels = getProductLevels();
                    if (tileMetadata.getTileComponent().getBandID().equals("MS1") ||
                            tileMetadata.getTileComponent().getBandID().equals("Multi")) {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        for (int index = 0; index < this.numMultiSpectralBands; index++) {

                            Band targetBand = createTargetBand(levels, bandNames, index, tiles, pixelDataType, this.tilesMultiSpectral, tileComp, tileRows, tileCols, bandWidth, bandHeight);
                            this.product.addBand(targetBand);
                        }
                    } else {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        Band targetBand = createTargetBand(levels, new String[]{bandNames[bandNames.length - 1]}, 0, tiles, pixelDataType, this.tilesPanchromatic, tileComp, tileRows, tileCols, bandWidth, bandHeight);
                        this.product.addBand(targetBand);
                    }
                }
            }
        }
        return this.product;
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

    @Override
    public void close() throws IOException {
        System.gc();
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                }
            }
        }
        if (this.productDirectory != null) {
            this.productDirectory.close();
            this.productDirectory = null;
        }
        if (this.metadata != null) {
            this.metadata = null;
        }
        if (this.tilesPanchromatic != null) {
            this.tilesPanchromatic.clear();
            this.tilesPanchromatic = null;
        }
        if (this.tilesMultiSpectral != null) {
            this.tilesMultiSpectral.clear();
            this.tilesMultiSpectral = null;
        }
        if (this.metadata != null) {
            File imageDir = new File(this.metadata.getImageDirectoryPath());
            if (imageDir.exists()) {
                deleteDirectory(imageDir);
            }
        }
        super.close();
    }

    /**
     * Return a file from the input and if the input file does not exist an error is throws
     *
     * @return inputFile
     * @throws FileNotFoundException
     */
    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        return inputFile;
    }

    private Band createTargetBand(int levels, String[] bandNames, int index, Product[][] tiles, int pixelDataType, HashMap<Product, String> tile, TileComponent tileComp, int tileRows, int tileCols, int bandWidth, int bandHeight) {
        Band targetBand = new Band(bandNames[index], this.bandDataType,
                tileComp.getNumColumns(), tileComp.getNumRows());
        Band band = setInputSpecificationBand(tile, index);
        final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
        setBandProperties(targetBand, band);
        initBandsGeoCoding(targetBand, tileComp);
        Band[][] srcBands = new Band[tileCols][tileRows];
        for (int x = 0; x < tileCols; x++) {
            for (int y = 0; y < tileRows; y++) {
                srcBands[x][y] = tiles[x][y].getBandAt(index);
            }
        }
        MosaicMultiLevelSource bandSource =
                new MosaicMultiLevelSource(srcBands,
                        bandWidth, bandHeight, tileSize.width, tileSize.height,
                        tileComp.getUpperLeftColumnOffset()[index], tileComp.getUpperLeftRowOffset()[index], tileCols, tileRows,
                        levels, typeMap.get(pixelDataType),
                        targetBand.getGeoCoding() != null ?
                                Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                Product.findImageToModelTransform(product.getSceneGeoCoding()));

        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
        targetBand.setScalingFactor(tileComp.getScalingFactor(targetBand.getName()));
        return targetBand;
    }

    private Band setInputSpecificationBand(HashMap<Product, String> map, int index) {
        Map.Entry<Product, String> entry = map.entrySet().iterator().next();
        Product p = entry.getKey();
        return p.getBandAt(index);
    }

    private int getProductLevels() {
        Map.Entry<Product, String> entryFirst = this.tilesMultiSpectral.entrySet().iterator().next();
        int levels = entryFirst.getKey().getBandAt(0).getSourceImage().getModel().getLevelCount();
        int levelsMultiSpectral = getLevel(this.tilesMultiSpectral, levels);
        int levelsPanchromatic = getLevel(this.tilesPanchromatic, levels);
        if (levelsMultiSpectral < levels) {
            levels = levelsMultiSpectral;
        } else if (levelsPanchromatic < levels) {
            levels = levelsPanchromatic;
        }
        if (levels > product.getNumResolutionsMax()) {
            product.setNumResolutionsMax(levels);
        }
        return levels;
    }

    private int getLevel(HashMap<Product, String> tiles, int levels) {
        int level = levels;
        for (Map.Entry<Product, String> entry : tiles.entrySet()) {
            Product p = entry.getKey();
            for (Band band : p.getBands()) {
                int bandLevel = band.getSourceImage().getModel().getLevelCount();
                if (bandLevel < level) {
                    level = bandLevel;
                }
            }
        }
        return level;
    }

    private void initBandsGeoCoding(Band targetBand, TileComponent tileComp) {
        GeoCoding geoCoding = null;
        int width = tileComp.getNumColumns();
        int height = tileComp.getNumRows();
        double stepSize = tileComp.getStepSize();
        double originX = tileComp.getOriginX();
        double originY = tileComp.getOriginY();
        String crsCode = tileComp.computeCRSCode();
        try {
            assert crsCode != null;
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            geoCoding = new CrsGeoCoding(crs,
                    width, height,
                    originX, originY,
                    stepSize, stepSize, 0.0, 0.0);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        targetBand.setGeoCoding(geoCoding);
    }

    private void setBandProperties(Band targetBand, Band band) {
        targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
        targetBand.setSpectralWavelength(band.getSpectralWavelength());
        targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
        targetBand.setSolarFlux(band.getSolarFlux());
        targetBand.setUnit(band.getUnit());
        targetBand.setNoDataValue(band.getNoDataValue());
//        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingFactor(band.getScalingFactor());
        targetBand.setScalingOffset(band.getScalingOffset());
        targetBand.setDescription(band.getDescription());

    }

    private void generateProductLists(Set<String> selectedProductFiles, List<TileMetadata> tileMetadataList) throws IOException {
        for (TileMetadata tileMetadata : tileMetadataList) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            for (int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                String filePath = null;
                for (String filePaths : selectedProductFiles) {
                    if (filePaths.contains(tileComponent.getTileNames()[filesIndex])) {
                        filePath = filePaths;
                    }
                }
                if (filePath != null) {
                    Product p = ProductIO.readProduct(Paths.get(productDirectory.getBasePath()).resolve(filePath).toFile());
                    this.bandDataType = p.getBandAt(0).getDataType();
                    if (tileComponent.getBandID().equals("P")) {
                        this.tilesPanchromatic.put(p, tileComponent.getTileNames()[filesIndex]);
                    } else {
                        this.tilesMultiSpectral.put(p, tileComponent.getTileNames()[filesIndex]);
                        if (this.numMultiSpectralBands == 0) {
                            this.numMultiSpectralBands = p.getNumBands();
                        }
                    }
                    tileComponent.setDeliveredTiles(tileComponent.getTileNames()[filesIndex]);
                    setMinRows(tileComponent.getNumRows());
                    setMinCols(tileComponent.getNumColumns());
                } else {
                    logger.warning(tileComponent.getTileNames()[filesIndex] + " is missing");

                }
            }
        }
    }

    public void setMinRows(int numRows) {
        if (minRows == 0) {
            minRows = numRows;
        } else if (minRows > numRows) {
            minRows = numRows;
        }
    }

    public void setMinCols(int numCols) {
        if (minCols == 0) {
            minCols = numCols;
        } else if (minCols > numCols) {
            minCols = numCols;
        }
    }
}
