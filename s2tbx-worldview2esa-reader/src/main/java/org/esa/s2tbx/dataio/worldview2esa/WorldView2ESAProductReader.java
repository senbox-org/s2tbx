package org.esa.s2tbx.dataio.worldview2esa;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.s2tbx.dataio.worldview2esa.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileComponent;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2esa.metadata.WorldView2ESAMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Basic reader for WorldView 2 ESA archive products.
 *
 * @author Denisa Stefanescu
 */

public class WorldView2ESAProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(WorldView2ESAProductReader.class.getName());

    private static final String EXCLUSION_STRING = ".SI.XML";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    private VirtualDirEx productDirectory;
    private Product product;
    private int numMultiSpectralBands;
    private Map<Product, String> tilesMultiSpectral;
    private Map<Product, String> tilesPanchromatic;
    private Set<WeakReference<Product>> tileRefs;

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    protected WorldView2ESAProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.tilesMultiSpectral = new HashMap<>();
        this.tilesPanchromatic = new HashMap<>();
        this.tileRefs = new HashSet<>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Object inputObject = getInput();

        long startTime = System.currentTimeMillis();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Start reading WorldView2ESA product, input: " + inputObject.toString());
        }

        final Path inputPath = BaseProductReaderPlugIn.convertInputToPath(inputObject);
        final VirtualDirEx productDirectoryTemp = VirtualDirEx.build(inputPath, false, true);
        try {
            String fileName = productDirectoryTemp.getBaseFile().getName();
            if (productDirectoryTemp.isCompressed()) {
                fileName = fileName.substring(0, fileName.lastIndexOf(WorldView2ESAConstants.PRODUCT_FILE_SUFFIX));
            } else {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }

            WorldView2ESAMetadata metadata;
            String fileNameWithExtension = fileName + WorldView2ESAConstants.METADATA_FILE_SUFFIX;
            try (FilePathInputStream filePathInputStream = productDirectoryTemp.getInputStream(fileNameWithExtension)) {
                metadata = WorldView2ESAMetadata.create(filePathInputStream);
            }
            if (productDirectoryTemp.isArchive() || !productDirectoryTemp.exists(fileName)) {
                // unzip the necessary files in a temporary directory
                Path path = productDirectoryTemp.getFile(fileName + WorldView2ESAConstants.ARCHIVE_FILE_EXTENSION).toPath();
                this.productDirectory = VirtualDirEx.build(path, false, true);
            } else {
                this.productDirectory = productDirectoryTemp;
            }

            String[] directoryFileList = this.productDirectory.listAllFiles();

            // the input is a ZIP file or contains a ZIP file
            final List<String> selectedProductFiles = new ArrayList<>();

            // if the input does not contain ZIP files
            for (String file : directoryFileList) {
                if ((file.endsWith(WorldView2ESAConstants.METADATA_EXTENSION) || file.endsWith(WorldView2ESAConstants.IMAGE_EXTENSION))
                        && !file.endsWith(WorldView2ESAConstants.METADATA_FILE_SUFFIX)
                        && !file.endsWith(EXCLUSION_STRING)) {
                    if (!selectedProductFiles.contains(file)) {
                        selectedProductFiles.add(file);
                    }
                }
            }

            final List<TileMetadata> tileMetadataList = new ArrayList<>();
            for (String fileMetadata : selectedProductFiles) {
                if (fileMetadata.endsWith(WorldView2ESAConstants.METADATA_EXTENSION)) {
                    try (FilePathInputStream filePathInputStream = this.productDirectory.getInputStream(fileMetadata)) {
                        TileMetadata tileMetadata = TileMetadata.create(filePathInputStream);
                        tileMetadataList.add(tileMetadata);
                    }
                }
            }
            if (!tileMetadataList.isEmpty()) {
                int width = 0;
                int height = 0;
                double stepSize = 0.0;
                double originX = 0.0;
                double originY = 0.0;
                String crsCode = null;
                for (TileMetadata tileMetadata : tileMetadataList) {
                    final TileComponent tileComponent = tileMetadata.getTileComponent();
                    if (tileComponent.getBandID().equals("P")) {
                        width = tileComponent.getNumColumns();
                        height = tileComponent.getNumRows();
                        stepSize = tileComponent.getStepSize();
                        originX = tileComponent.getOriginX();
                        originY = tileComponent.getOriginY();
                        crsCode = tileComponent.computeCRSCode();
                    }
                }
                this.product = new Product(metadata.getProductName(), WorldView2ESAConstants.PRODUCT_TYPE, width, height);
                this.product.setStartTime(metadata.getProductStartTime());
                this.product.setEndTime(metadata.getProductEndTime());
                this.product.setDescription(metadata.getProductDescription());
                this.product.setProductReader(this);
                this.product.setFileLocation(inputPath.toFile());
                try {
                    assert crsCode != null;
                    final GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                                 width, height,
                                                                 originX, originY,
                                                                 stepSize, stepSize);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Generate product lists: selected products files size: "+selectedProductFiles.size() + ", metadata list size: "+ tileMetadataList.size());
                }

                generateProductLists(selectedProductFiles, tileMetadataList);

                for (TileMetadata tileMetadata : tileMetadataList) {

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Read products for tile metadata '" + tileMetadata.getPath()+"'.");
                    }

                    this.product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                    final int pixelDataType = tileMetadata.getPixelDataType();
                    String[] bandNames;
                    if (numMultiSpectralBands < 4) {
                        bandNames = WorldView2ESAConstants.NATURAL_COLORS;
                    }
                    if (numMultiSpectralBands == 4) {
                        bandNames = WorldView2ESAConstants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
                    } else {
                        bandNames = WorldView2ESAConstants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
                    }
                    final Map<String, int[]> tileInfo = tileMetadata.getRasterTileInfo();
                    final int tileRows = tileMetadata.getTileRowsCount();
                    final int tileCols = tileMetadata.getTileColsCount();
                    final Product[][] tiles = new Product[tileCols][tileRows];

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Read products tile count " + tileInfo.keySet().size()+".");
                    }

                    for (String rasterString : tileInfo.keySet()) {
                        final int[] coords = tileInfo.get(rasterString);
                        if (!selectedProductFiles.isEmpty()) {
                            for (String file : selectedProductFiles) {
                                if (file.contains(rasterString)) {
                                    rasterString = file;
                                }
                            }
                        }
                        Product localProduct = readProduct(rasterString);
                        tiles[coords[1]][coords[0]] = localProduct;
                        this.tileRefs.add(new WeakReference<Product>(localProduct));
                    }
                    final int levels = getProductLevels();
                    if (tileMetadata.getTileComponent().getBandID().equals("MS1") || tileMetadata.getTileComponent().getBandID().equals("Multi")) {
                        for (int index = 0; index < this.numMultiSpectralBands; index++) {
                            final Band targetBand = createTargetBand(tileMetadata, levels, bandNames, index, tiles, pixelDataType, this.tilesMultiSpectral);
                            this.product.addBand(targetBand);
                        }
                    } else {
                        final Band targetBand = createTargetBand(tileMetadata, levels, new String[]{bandNames[bandNames.length - 1]}, 0, tiles, pixelDataType, this.tilesPanchromatic);
                        this.product.addBand(targetBand);
                    }
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.d;
                logger.log(Level.FINE, "Finish reading WorldView2ESA product, input: " + inputObject.toString() + "', elapsed time: " + elapsedTimeInSeconds + " seconds.");
            }

            return this.product;
        } finally {
            productDirectoryTemp.close();
        }
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
        if (this.tilesPanchromatic != null) {
            this.tilesPanchromatic.clear();
            this.tilesPanchromatic = null;
        }
        if (this.tilesMultiSpectral != null) {
            this.tilesMultiSpectral.clear();
            this.tilesMultiSpectral = null;
        }
        super.close();
    }

    private Band createTargetBand(final TileMetadata tileMetadata, final int levels, final String[] bandNames, final int index, final Product[][] tiles, final int pixelDataType, final Map<Product, String> tile) {
        final int tileRows = tileMetadata.getTileRowsCount();
        final int tileCols = tileMetadata.getTileColsCount();
        int bandWidth = tileMetadata.getRasterWidth();
        int bandHeight = tileMetadata.getRasterHeight();
        //compute the band width and height when not all tiles are delivered
        if (tileMetadata.getTileComponent().getNumOfTiles() != tileMetadata.getTileComponent().getDeliveredTiles().size()) {
            Map<String, Integer> rasterValues = computeBandWidthAndHeight(tileRows, tileCols, tiles, index);
            bandWidth = rasterValues.get(WIDTH);
            bandHeight = rasterValues.get(HEIGHT);
        }
        Band targetBand = new Band(bandNames[index], pixelDataType,
                                   bandWidth, bandHeight);
        final Band band = setInputSpecificationBand(tile, index);
        setBandProperties(targetBand, band);
        initBandsGeoCoding(targetBand, tileMetadata.getTileComponent());
        final Band[][] srcBands = new Band[tileCols][tileRows];
        for (int x = 0; x < tileCols; x++) {
            for (int y = 0; y < tileRows; y++) {
                srcBands[x][y] = tiles[x][y].getBandAt(index);
            }
        }

        final MosaicMultiLevelSource bandSource =
                new MosaicMultiLevelSource(srcBands,
                                           bandWidth, bandHeight,
                                           tileMetadata.getTileComponent().getNumColumns(), tileMetadata.getTileComponent().getNumRows(),
                                           tileCols, tileRows, levels,
                                           targetBand.getGeoCoding() != null ?
                                                   Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                                   Product.findImageToModelTransform(product.getSceneGeoCoding()));


        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
        targetBand.setScalingFactor(tileMetadata.getTileComponent().getScalingFactor(targetBand.getName()));
        return targetBand;
    }

    private Band setInputSpecificationBand(final Map<Product, String> map, final int index) {
        final Map.Entry<Product, String> entry = map.entrySet().iterator().next();
        final Product p = entry.getKey();
        return p.getBandAt(index);
    }

    /**
     * Compute the band width and height based on the delivered tiles dimensions
     *
     * @param tileRows
     * @param tileCols
     * @param tiles
     * @param bandIndex
     * @return
     */
    private Map<String, Integer> computeBandWidthAndHeight(final int tileRows, final int tileCols, final Product[][] tiles, final int bandIndex) {
        Map<String, Integer> rasterValues = new HashMap<String, Integer>() {{
            put(WIDTH, 0);
            put(HEIGHT, 0);
        }};
        for (int x = 0; x < tileCols; x++) {
            int tileBandWidth = 0;
            int tileBandHeight = 0;
            for (int y = 0; y < tileRows; y++) {
                if ((tileCols > tileRows && x != tileCols - 1) || tileCols <= tileRows) {
                    if ((y != tileRows - 1 && x < tileRows && y < tileCols) || (y == tileRows - 1 && tileRows < tileCols && x <tileRows) || tileCols == tileRows) {
                        tileBandWidth = tileBandWidth + tiles[y][x].getBandAt(bandIndex).getRasterWidth();
                    }
                }
                if (x != tileCols - 1 || (x == tileCols - 1 && (tileCols > tileRows || tileRows > tileCols)) || tileCols == tileRows) {
                    tileBandHeight = tileBandHeight + tiles[x][y].getBandAt(bandIndex).getRasterHeight();
                }
            }
            if (tileRows < tileCols && x != tileCols - 1 && x <tileRows) {
                int a = tileCols - tileRows;
                for (int i = 0; i < a; i++) {
                    tileBandWidth = tileBandWidth + tiles[tileRows + i][x].getBandAt(bandIndex).getRasterWidth();
                }
            }
            if (tileBandWidth > rasterValues.get(WIDTH)) {
                rasterValues.put(WIDTH, tileBandWidth);
            }
            if (tileBandHeight > rasterValues.get(HEIGHT)) {
                rasterValues.put(HEIGHT, tileBandHeight);
            }
        }
        if (tileRows > tileCols && tileCols>1) {
            int a = tileRows - tileCols;
            int tileBandWidth = 0;
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < tileCols; j++) {
                    tileBandWidth = tileBandWidth + tiles[j][tileCols + i].getBandAt(bandIndex).getRasterWidth();
                }
            }
            if (tileBandWidth > rasterValues.get(WIDTH)) {
                rasterValues.put(WIDTH, tileBandWidth);
            }
        }
        return rasterValues;
    }

    private int getProductLevels() {
        final Map.Entry<Product, String> entryFirst = this.tilesMultiSpectral.entrySet().iterator().next();
        int levels = entryFirst.getKey().getBandAt(0).getSourceImage().getModel().getLevelCount();
        final int levelsMultiSpectral = getLevel(this.tilesMultiSpectral, levels);
        final int levelsPanchromatic = getLevel(this.tilesPanchromatic, levels);
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

    private int getLevel(final Map<Product, String> tiles, final int levels) {
        int level = levels;
        for (Map.Entry<Product, String> entry : tiles.entrySet()) {
            final Product p = entry.getKey();
            for (Band band : p.getBands()) {
                final int bandLevel = band.getSourceImage().getModel().getLevelCount();
                if (bandLevel < level) {
                    level = bandLevel;
                }
            }
        }
        return level;
    }

    private void initBandsGeoCoding(final Band targetBand, final TileComponent tileComp) {
        GeoCoding geoCoding = null;
        final int width = tileComp.getNumColumns();
        final int height = tileComp.getNumRows();
        final double stepSize = tileComp.getStepSize();
        final double originX = tileComp.getOriginX();
        final double originY = tileComp.getOriginY();
        final String crsCode = tileComp.computeCRSCode();
        try {
            assert crsCode != null;
            final CoordinateReferenceSystem crs = CRS.decode(crsCode);
            geoCoding = new CrsGeoCoding(crs,
                                         width, height,
                                         originX, originY,
                                         stepSize, stepSize, 0.0, 0.0);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        targetBand.setGeoCoding(geoCoding);
    }

    private void setBandProperties(final Band targetBand, final Band band) {
        targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
        targetBand.setSpectralWavelength(band.getSpectralWavelength());
        targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
        targetBand.setSolarFlux(band.getSolarFlux());
        targetBand.setUnit(band.getUnit());
        targetBand.setNoDataValue(band.getNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingOffset(band.getScalingOffset());
        targetBand.setDescription(band.getDescription());
    }

    private void generateProductLists(final List<String> selectedProductFiles, final List<TileMetadata> tileMetadataList) throws IOException {
        for (TileMetadata tileMetadata : tileMetadataList) {
            final TileComponent tileComponent = tileMetadata.getTileComponent();
            for (int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                String filePath = null;
                for (String filePaths : selectedProductFiles) {
                    if (filePaths.contains(tileComponent.getTileNames()[filesIndex])) {
                        filePath = filePaths;
                    }
                }
                if (filePath != null) {
                    Product p = readProduct(filePath);
                    if (tileComponent.getBandID().equals("P")) {
                        this.tilesPanchromatic.put(p, tileComponent.getTileNames()[filesIndex]);
                    } else {
                        this.tilesMultiSpectral.put(p, tileComponent.getTileNames()[filesIndex]);
                        if (this.numMultiSpectralBands == 0) {
                            this.numMultiSpectralBands = p.getNumBands();
                        }
                    }
                    tileComponent.setDeliveredTiles(tileComponent.getTileNames()[filesIndex]);
                } else {
                    logger.warning(tileComponent.getTileNames()[filesIndex] + " is missing");
                }
            }
        }
    }

    private Product readProduct(String filePath) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Read product from relative file path '" + filePath + "'.");
        }

        File file = this.productDirectory.getFile(filePath);
        return ProductIO.readProduct(file);
    }
}
