package org.esa.s2tbx.dataio.worldview2;

import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.apache.commons.lang.StringUtils;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.s2tbx.dataio.worldview2.metadata.TileComponent;
import org.esa.s2tbx.dataio.worldview2.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2.metadata.TileMetadataList;
import org.esa.s2tbx.dataio.worldview2.metadata.WorldView2Metadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixCell;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixMultiLevelSource;
import org.esa.snap.engine_utilities.util.Pair;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import java.awt.*;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 */

class WorldView2ProductReader extends AbstractProductReader {

    static {
        XmlMetadataParserFactory.registerParser(WorldView2Metadata.class, new XmlMetadataParser<>(WorldView2Metadata.class));
        XmlMetadataParserFactory.registerParser(TileMetadata.class, new XmlMetadataParser<>(TileMetadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;

    public WorldView2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand,
                                          int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, com.bc.ceres.core.ProgressMonitor pm)
                                          throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, false);

            WorldView2Metadata metadata = readMetadata(this.productDirectory);
            int subProductCount = metadata.getSubProductCount();
            if (subProductCount == 0) {
                throw new IllegalStateException("The product is empty.");
            }

            Dimension defaultProductSize = metadata.computeDefaultProductSize();
            if (defaultProductSize == null) {
                throw new NullPointerException("The product default size is null.");
            }
            ProductSubsetDef subsetDef = getSubsetDef();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;
            boolean isMultiSize = metadata.isMultiSize();
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
            } else {
                productDefaultGeoCoding = metadata.buildProductGeoCoding(null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, isMultiSize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            String productName = metadata.getOrderNumber();
            if (StringUtils.isBlank(productName)) {
                productName = this.productDirectory.getBaseFile().getName();
            }

            Product product = new Product(productName, WorldView2Constants.PRODUCT_TYPE, productBounds.width, productBounds.height, this);
            product.setStartTime(metadata.getProductStartTime());
            product.setEndTime(metadata.getProductEndTime());
            product.setDescription(metadata.getProductDescription());
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);

            GeoCoding productGeoCoding = metadata.buildProductGeoCoding(productBounds);
            if (productGeoCoding != null) {
                product.setSceneGeoCoding(productGeoCoding);
            }

            Dimension defaultJAIReadTileSize = JAI.getDefaultTileSize();
            product.setPreferredTileSize(defaultJAIReadTileSize);

            Path parentFolderPath = this.productDirectory.getBaseFile().toPath();
            String autoGroupPattern = "";
            String bandPrefix = "";
            for (int k=0; k<subProductCount; k++) {
                String subProductName = metadata.getSubProductNameAt(k);
                TileMetadataList subProductTileMetadataList = metadata.getSubProductTileMetadataListAt(k);

                if (subProductCount > 1) {
                    if (autoGroupPattern.length() > 0) {
                        autoGroupPattern += ":";
                    }
                    autoGroupPattern += subProductName;
                    bandPrefix = subProductName + "_";
                }

                Dimension defaultSubProductSize = subProductTileMetadataList.computeDefaultProductSize();
                if (defaultSubProductSize == null) {
                    throw new NullPointerException("The subproduct default size is null.");
                }

                GeoCoding subProductDefaultGeoCoding = null;
                GeoCoding subProductGeoCoding = null;
                boolean canContinue = true;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    // do nothing
                } else {
                    subProductDefaultGeoCoding = subProductTileMetadataList.buildProductGeoCoding(null);
                    Rectangle subProductBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, subProductDefaultGeoCoding,
                                                          defaultProductSize.width, defaultProductSize.height, defaultSubProductSize.width, defaultSubProductSize.height, isMultiSize);
                    if (subProductBounds.isEmpty()) {
                        canContinue = false; // no intersection
                    } else {
                        subProductGeoCoding = subProductTileMetadataList.buildProductGeoCoding(subProductBounds);
                    }
                }
                if (canContinue) {
                    Set<String> tiffImageRelativeFiles = subProductTileMetadataList.getTiffImageRelativeFiles();
                    List<TileMetadata> subProductTiles = subProductTileMetadataList.getTiles();
                    for (TileMetadata tileMetadata : subProductTiles) {
                        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                            product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                        }
                        Pair<Integer, MosaicMatrix> result = buildMosaicMatrix(tileMetadata, parentFolderPath, tiffImageRelativeFiles);
                        int dataBufferType = result.getFirst().intValue();
                        MosaicMatrix subProductMosaicMatrix = result.getSecond();
                        TileComponent tileComponent = tileMetadata.getTileComponent();
                        String[] bandNames = subProductTileMetadataList.computeBandNames(tileMetadata);
                        for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                            String bandName = bandPrefix + bandNames[bandIndex];
                            if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                                // use the default product size to compute the band of a subproduct because the subset region
                                // is set according to the default product size
                                Band band = buildSubProductBand(defaultProductSize, subProductDefaultGeoCoding, subProductMosaicMatrix, dataBufferType, bandName,
                                                                bandIndex, tileMetadata, subProductGeoCoding, subsetDef, isMultiSize, defaultJAIReadTileSize);
                                if (band != null) {
                                    band.setScalingFactor(tileComponent.getScalingFactor(bandNames[bandIndex]));
                                    Integer spectralWavelength = WorldView2Constants.BAND_WAVELENGTH.get(bandName);
                                    if (spectralWavelength != null) {
                                        band.setSpectralWavelength(spectralWavelength.floatValue());
                                    }
                                    product.addBand(band);
                                }
                            }
                        }
                    }
                }
            }
            product.setAutoGrouping(autoGroupPattern);

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

    private void closeResources() {
        try {
            if (this.imageInputStreamSpi != null) {
                ImageRegistryUtils.deregisterImageInputStreamSpi(this.imageInputStreamSpi);
                this.imageInputStreamSpi = null;
            }
        } finally {
            if (this.productDirectory != null) {
                this.productDirectory.close();
                this.productDirectory = null;
            }
        }
        System.gc();
    }

    private Pair<Integer, MosaicMatrix> buildMosaicMatrix(TileMetadata tileMetadata, Path parentFolderPath, Set<String> tiffImageRelativeFiles)
                                           throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        Map<String, int[]> tileInfo = tileMetadata.computeRasterTileInfo();
        int tileRowCount = tileMetadata.getTileRowsCount();
        int tileColumnCount = tileMetadata.getTileColsCount();
        String[][] geoTiffImagePaths = new String[tileRowCount][tileColumnCount];
        for (String rasterString : tileInfo.keySet()) {
            int[] coordinates = tileInfo.get(rasterString);
            if (!tiffImageRelativeFiles.isEmpty()) {
                for (String file : tiffImageRelativeFiles) {
                    if (file.contains(rasterString)) {
                        rasterString = file;
                        break;
                    }
                }
            }
            geoTiffImagePaths[coordinates[0]][coordinates[1]] = rasterString;
        }

        Path localTempFolder = this.productDirectory.makeLocalTempFolder();
        int dataType = 0;
        MosaicMatrix mosaicMatrix = new MosaicMatrix(tileRowCount, tileColumnCount);
        for (int rowIndex=0; rowIndex<tileRowCount; rowIndex++) {
            for (int columnIndex=0; columnIndex<tileColumnCount; columnIndex++) {
                String rasterString = geoTiffImagePaths[rowIndex][columnIndex];
                int cellWidth;
                int cellHeight;
                int dataBufferType;
                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(parentFolderPath, rasterString)) {
                    cellWidth = geoTiffImageReader.getImageWidth();
                    cellHeight = geoTiffImageReader.getImageHeight();
                    dataBufferType = geoTiffImageReader.getSampleModel().getDataType();
                }
                if (columnIndex == 0 && rowIndex == 0) {
                    dataType = dataBufferType;
                } else if (dataType != dataBufferType) {
                    throw new IllegalStateException("Different data type count: rowIndex=" + rowIndex + ", columnIndex=" + columnIndex + ", dataType=" + dataType + ", dataBufferType=" + dataBufferType + ".");
                }
                GeoTiffMatrixCell matrixCell = new GeoTiffMatrixCell(cellWidth, cellHeight, dataBufferType, parentFolderPath, rasterString, localTempFolder);
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, true, true);
            }
        }
        return new Pair(dataType, mosaicMatrix);
    }

    private static Band buildSubProductBand(Dimension defaultProductSize, GeoCoding subProductDefaultGeoCoding, MosaicMatrix subProductMosaicMatrix,
                                            int dataBufferType, String bandName, int bandIndex, TileMetadata tileMetadata, GeoCoding subProductGeoCoding,
                                            ProductSubsetDef subsetDef, boolean isMultiSize, Dimension defaultJAIReadTileSize)
                                  throws FactoryException, TransformException {

        int defaultBandWidth = subProductMosaicMatrix.computeTotalWidth();
        int defaultBandHeight = subProductMosaicMatrix.computeTotalHeight();
        Rectangle bandBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
        } else {
            GeoCoding bandDefaultGeoCoding = tileMetadata.buildBandGeoCoding(null);
            bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(subProductDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, defaultBandWidth, defaultBandHeight, isMultiSize);
        }
        if (bandBounds.isEmpty()) {
            return null; // no intersection
        }
        GeoCoding bandGeoCoding = tileMetadata.buildBandGeoCoding(bandBounds);
        if (bandGeoCoding == null) {
            bandGeoCoding = subProductGeoCoding;
        }

        Band band = new Band(bandName, ImageManager.getProductDataType(dataBufferType), bandBounds.width, bandBounds.height);
        band.setNoDataValueUsed(true);
        if (bandGeoCoding != null) {
            band.setGeoCoding(bandGeoCoding);
        }

        int maximumBandLevelCount = subProductMosaicMatrix.computeMinimumLevelCount();
        int bandLevelCount = DefaultMultiLevelModel.getLevelCount(bandBounds.width, bandBounds.height);
        if (bandLevelCount > maximumBandLevelCount) {
            bandLevelCount = maximumBandLevelCount;
        }
        GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(bandLevelCount, subProductMosaicMatrix, bandBounds, bandIndex, bandGeoCoding, null, defaultJAIReadTileSize);
        ImageLayout imageLayout = multiLevelSource.buildMultiLevelImageLayout();
        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource, imageLayout));

        return band;
    }

    public static WorldView2Metadata readMetadata(VirtualDirEx productDirectory) throws Exception {
        String[] allFileNames = productDirectory.listAllFiles();
        String metadataFileName = null;
        for (String relativeFilePath : allFileNames) {
            if (StringUtils.endsWithIgnoreCase(relativeFilePath, WorldView2Constants.METADATA_FILE_SUFFIX)) {
                metadataFileName = relativeFilePath;
            }
        }
        if (metadataFileName == null) {
            throw new NullPointerException("The product has no metadata file.");
        }
        WorldView2Metadata worldView2Metadata;
        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataFileName)) {
            worldView2Metadata = (WorldView2Metadata)XmlMetadataParserFactory.getParser(WorldView2Metadata.class).parse(filePathInputStream);
        }

        String subProductNames[] = worldView2Metadata.findSubProductNames();
        for (int i=0; i<subProductNames.length; i++) {
            TileMetadataList tileMetadataList = new TileMetadataList();
            for (String fileRelativePath : allFileNames) {
                if (fileRelativePath.contains(subProductNames[i])) {
                    if (fileRelativePath.endsWith(WorldView2Constants.METADATA_EXTENSION) && !fileRelativePath.endsWith(WorldView2Constants.METADATA_FILE_SUFFIX)
                            && !fileRelativePath.endsWith(WorldView2Metadata.EXCLUSION_STRING)) {

                        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(fileRelativePath)) {
                            TileMetadata tileMetadata = TileMetadata.create(filePathInputStream);
                            tileMetadataList.addTileMetadata(tileMetadata);
                        }
                    } else if (fileRelativePath.endsWith(WorldView2Constants.IMAGE_EXTENSION)) {
                        tileMetadataList.getTiffImageRelativeFiles().add(fileRelativePath);
                    }
                }
            }
            tileMetadataList.sortTilesByFileName();
            int multiSpectralBandCount = 0;
            int bandsDataType = 0;
            for (TileMetadata tileMetadata : tileMetadataList.getTiles()) {
                TileComponent tileComponent = tileMetadata.getTileComponent();
                for (int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                    for (String imageFileRelativePath : tileMetadataList.getTiffImageRelativeFiles()) {
                        if (imageFileRelativePath.contains(tileComponent.getTileNames()[filesIndex])) {
                            tileComponent.addDeliveredTile(tileComponent.getTileNames()[filesIndex]);
                            if (!tileComponent.getBandID().equals("P")) {
                                if (multiSpectralBandCount == 0) {
                                    Path parentFolderPath = productDirectory.getBaseFile().toPath();
                                    try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(parentFolderPath, imageFileRelativePath)) {
                                        SampleModel sampleModel = geoTiffImageReader.getBaseImage().getSampleModel();
                                        multiSpectralBandCount = sampleModel.getNumBands();
                                        bandsDataType = ImageManager.getProductDataType(sampleModel.getDataType());
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            tileMetadataList.setBandsData(multiSpectralBandCount, bandsDataType);
            worldView2Metadata.addSubProductTileMetadataList(subProductNames[i], tileMetadataList);
        }
        worldView2Metadata.sortSubProductsByName();

        return worldView2Metadata;
    }
}