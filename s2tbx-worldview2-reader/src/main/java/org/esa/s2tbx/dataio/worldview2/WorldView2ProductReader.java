package org.esa.s2tbx.dataio.worldview2;

import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
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
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.imageio.spi.ImageInputStreamSpi;
import java.awt.*;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 */

class WorldView2ProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(WorldView2ProductReader.class.getName());

    static {
        XmlMetadataParserFactory.registerParser(WorldView2Metadata.class, new XmlMetadataParser<>(WorldView2Metadata.class));
        XmlMetadataParserFactory.registerParser(TileMetadata.class, new XmlMetadataParser<>(TileMetadata.class));
    }

    private VirtualDirEx productDirectory;
    private List<GeoTiffImageReader> bandImageReaders;
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
            int subProductCount = metadata.getProducts().size();
            if (subProductCount == 0) {
                throw new IllegalStateException("The product is empty.");
            }

            Dimension defaultProductSize = metadata.computeDefaultProductSize();
            if (defaultProductSize == null) {
                throw new NullPointerException("The product default size is null.");
            }
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, subsetDef);
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

            this.bandImageReaders = new ArrayList<>();
            Path parentFolderPath = this.productDirectory.getBaseFile().toPath();
            String autoGroupPattern = "";
            String bandPrefix = "";
            for (Map.Entry<String, TileMetadataList> entry : metadata.getProducts().entrySet()) {
                String subProductName = entry.getKey();
                if (subProductCount > 1) {
                    if (autoGroupPattern.length() > 0) {
                        autoGroupPattern += ":";
                    }
                    autoGroupPattern += subProductName;
                    bandPrefix = subProductName + "_";
                }

                TileMetadataList subProductTileMetadataList = entry.getValue();
                List<TileMetadata> subProductTiles = subProductTileMetadataList.getTiles();

                Dimension defaultSubProductSize = subProductTileMetadataList.computeDefaultProductSize();
                if (defaultSubProductSize == null) {
                    throw new NullPointerException("The subproduct default size is null.");
                }
                Rectangle subProductBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultSubProductSize.width, defaultSubProductSize.height);
                GeoCoding subProductGeoCoding = subProductTileMetadataList.buildProductGeoCoding(subProductBounds);

                int bandsDataType = subProductTileMetadataList.getBandsDataType();
                Set<String> tiffImageRelativeFiles = subProductTileMetadataList.getTiffImageRelativeFiles();
                for (TileMetadata tileMetadata : subProductTiles) {
                    if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                        product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                    }
                    MosaicMatrix mosaicMatrix = buildMosaicMatrix(tileMetadata, parentFolderPath, tiffImageRelativeFiles);
                    TileComponent tileComponent = tileMetadata.getTileComponent();
                    String[] bandNames = subProductTileMetadataList.computeBandNames(tileMetadata);
                    for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                        String bandName = bandPrefix + bandNames[bandIndex];
                        if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                            Band band = buildBand(defaultProductSize, productBounds, mosaicMatrix, bandsDataType, bandName, bandIndex, tileMetadata, subProductGeoCoding, preferredTileSize);
                            band.setScalingFactor(tileComponent.getScalingFactor(bandNames[bandIndex]));
                            product.addBand(band);
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
            if (this.bandImageReaders != null) {
                for (GeoTiffImageReader geoTiffImageReader : this.bandImageReaders) {
                    try {
                        geoTiffImageReader.close();
                    } catch (Exception ignore) {
                        // ignore
                    }
                }
                this.bandImageReaders.clear();
                this.bandImageReaders = null;
            }
        } finally {
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
        }
        System.gc();
    }

    private MosaicMatrix buildMosaicMatrix(TileMetadata tileMetadata, Path parentFolderPath, Set<String> tiffImageRelativeFiles)
                                           throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        Map<String, int[]> tileInfo = tileMetadata.computeRasterTileInfo();
        int tileRowCount = tileMetadata.getTileRowsCount();
        int tileColumnCount = tileMetadata.getTileColsCount();
        GeoTiffImageReader[][] geoTiffImageReaders = new GeoTiffImageReader[tileRowCount][tileColumnCount];
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
            GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(parentFolderPath, rasterString);
            this.bandImageReaders.add(geoTiffImageReader);
            geoTiffImageReaders[coordinates[0]][coordinates[1]] = geoTiffImageReader;
        }

        MosaicMatrix mosaicMatrix = new MosaicMatrix(tileRowCount, tileColumnCount);
        for (int rowIndex=0; rowIndex<tileRowCount; rowIndex++) {
            for (int columnIndex=0; columnIndex<tileColumnCount; columnIndex++) {
                GeoTiffImageReader geoTiffImageReader = geoTiffImageReaders[rowIndex][columnIndex];
                SampleModel sampleModel = geoTiffImageReader.getBaseImage().getSampleModel();
                GeoTiffMatrixCell matrixCell = new GeoTiffMatrixCell(geoTiffImageReader.getImageWidth(), geoTiffImageReader.getImageHeight(), geoTiffImageReader, sampleModel.getDataType());
                mosaicMatrix.setCellAt(rowIndex, columnIndex, matrixCell, true, true);
            }
        }
        return mosaicMatrix;
    }

    private static Band buildBand(Dimension defaultProductSize, Rectangle productBounds, MosaicMatrix mosaicMatrix, int bandDataType,
                                  String bandName, int bandIndex, TileMetadata tileMetadata, GeoCoding subProductGeoCoding, Dimension preferredTileSize)
                                  throws FactoryException, TransformException {

        int defaultBandWidth = mosaicMatrix.computeTotalWidth();
        int defaultBandHeight = mosaicMatrix.computeTotalHeight();
        Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultBandWidth, defaultBandHeight);

        GeoCoding bandGeoCoding = tileMetadata.buildBandGeoCoding(bandBounds);
        if (bandGeoCoding == null) {
            bandGeoCoding = subProductGeoCoding;
        }

        Band band = new Band(bandName, bandDataType, bandBounds.width, bandBounds.height);
        band.setNoDataValueUsed(true);
        if (bandGeoCoding != null) {
            band.setGeoCoding(bandGeoCoding);
        }
        GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(mosaicMatrix, bandBounds, preferredTileSize, bandIndex, bandGeoCoding);
        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource));
        return band;
    }

    private static GeoCoding buildBandGeoCoding(TileComponent tileComponent) {
        GeoCoding geoCoding = null;
        String crsCode = tileComponent.computeCRSCode();
        if (crsCode != null) {
            try {
                CoordinateReferenceSystem crs = CRS.decode(crsCode);
                int width = tileComponent.getNumColumns();
                int height = tileComponent.getNumRows();
                double stepSize = tileComponent.getStepSize();
                double originX = tileComponent.getOriginX();
                double originY = tileComponent.getOriginY();
                geoCoding = new CrsGeoCoding(crs, width, height, originX, originY, stepSize, stepSize, 0.0, 0.0);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to read the band geo coding.", e);
            }
        }
        return geoCoding;
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

        String productNames[] = worldView2Metadata.findProductNames();
        for (int i=0; i<productNames.length; i++) {
            TileMetadataList tileMetadataList = new TileMetadataList();
            for (String fileRelativePath : allFileNames) {
                if (fileRelativePath.contains(productNames[i])) {
                    if (fileRelativePath.endsWith(WorldView2Constants.METADATA_EXTENSION) && !fileRelativePath.endsWith(WorldView2Constants.METADATA_FILE_SUFFIX)
                            && !fileRelativePath.endsWith(WorldView2Metadata.EXCLUSION_STRING)) {

                        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(fileRelativePath)) {
                            TileMetadata tileMetadata = TileMetadata.create(filePathInputStream);
                            tileMetadataList.getTiles().add(tileMetadata);
                        }
                    } else if (fileRelativePath.endsWith(WorldView2Constants.IMAGE_EXTENSION)) {
                        tileMetadataList.getTiffImageRelativeFiles().add(fileRelativePath);
                    }
                }
            }
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
            worldView2Metadata.addProductTileMetadataList(productNames[i], tileMetadataList);
        }

        return worldView2Metadata;
    }
}