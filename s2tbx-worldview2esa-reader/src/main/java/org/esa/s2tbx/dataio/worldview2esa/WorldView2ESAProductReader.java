package org.esa.s2tbx.dataio.worldview2esa;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileComponent;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2esa.metadata.TileMetadataList;
import org.esa.s2tbx.dataio.worldview2esa.metadata.WorldView2ESAMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.image.MosaicMatrix;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixCell;
import org.esa.snap.dataio.geotiff.GeoTiffMatrixMultiLevelSource;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

    static {
        XmlMetadataParserFactory.registerParser(WorldView2ESAMetadata.class, new XmlMetadataParser<>(WorldView2ESAMetadata.class));
        XmlMetadataParserFactory.registerParser(TileMetadata.class, new XmlMetadataParser<>(TileMetadata.class));
    }

    private static final String EXCLUSION_STRING = ".SI.XML";

    private VirtualDirEx productDirectory;
    private List<GeoTiffImageReader> bandImageReaders;
    private ImageInputStreamSpi imageInputStreamSpi;

    public WorldView2ESAProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
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
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            String metadataFileName = buildMetadataFileName(this.productDirectory);
            WorldView2ESAMetadata metadata = readMetadata(this.productDirectory, metadataFileName);
            Path imagesMetadataParentPath = buildImagesMetadataParentPath(this.productDirectory, metadataFileName);
            TileMetadataList tileMetadataList = readTileMetadataList(imagesMetadataParentPath);

            Dimension defaultProductSize = tileMetadataList.computeDefaultProductSize();
            if (defaultProductSize == null) {
                throw new NullPointerException("The product default size is null.");
            }

            ProductSubsetDef subsetDef = getSubsetDef();
            GeoCoding productDefaultGeoCoding = null;
            if(subsetDef != null){
                productDefaultGeoCoding = tileMetadataList.buildProductGeoCoding(null);
            }
            Rectangle productBounds = ImageUtils.computeProductBounds(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, subsetDef);

            Product product = new Product(metadata.getProductName(), WorldView2ESAConstants.PRODUCT_TYPE, productBounds.width, productBounds.height, this);
            product.setStartTime(metadata.getProductStartTime());
            product.setEndTime(metadata.getProductEndTime());
            product.setDescription(metadata.getProductDescription());
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            GeoCoding productGeoCoding = tileMetadataList.buildProductGeoCoding(productBounds);
            if (productGeoCoding != null) {
                product.setSceneGeoCoding(productGeoCoding);
            }

            // add bands
            this.bandImageReaders = new ArrayList<>();
            Set<String> tiffImageRelativeFiles = tileMetadataList.getTiffImageRelativeFiles();
            List<TileMetadata> tilesMetadata = tileMetadataList.getTiles();
            for (TileMetadata tileMetadata : tilesMetadata) {
                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                }
                MosaicMatrix mosaicMatrix = buildMosaicMatrix(tileMetadata, tiffImageRelativeFiles, imagesMetadataParentPath);
                String[] bandNames = tileMetadataList.computeBandNames(tileMetadata);
                for (int bandIndex = 0; bandIndex < bandNames.length; bandIndex++) {
                    String bandName = bandNames[bandIndex];
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band band = buildBand(defaultProductSize, mosaicMatrix, tileMetadata, bandName, bandIndex, preferredTileSize, productGeoCoding, productDefaultGeoCoding, subsetDef);
                        product.addBand(band);
                    }
                }
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

    private MosaicMatrix buildMosaicMatrix(TileMetadata tileMetadata, Set<String> tiffImageRelativeFiles, Path imagesMetadataParentPath) throws Exception {
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
            GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, rasterString);
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

    private static Band buildBand(Dimension defaultProductSize, MosaicMatrix mosaicMatrix, TileMetadata tileMetadata,
                                  String bandName, int bandIndex, Dimension preferredTileSize, GeoCoding productGeoCoding,
                                  GeoCoding productDefaultGeoCoding, ProductSubsetDef subsetDef) {

        int defaultBandWidth = mosaicMatrix.computeTotalWidth();
        int defaultBandHeight = mosaicMatrix.computeTotalHeight();
        Rectangle bandBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            bandBounds = new Rectangle(defaultBandWidth, defaultBandHeight);
        } else {
            GeoCoding bandDefaultGeoCoding = buildBandGeoCoding(tileMetadata.getTileComponent(), defaultBandWidth, defaultBandHeight, null);
            bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width,
                                                                            defaultProductSize.height, defaultBandWidth, defaultBandHeight);
        }

        int productDataType = tileMetadata.getProductDataType();
        Band band = new Band(bandName, productDataType, bandBounds.width, bandBounds.height);
        band.setSpectralWavelength(WorldView2ESAConstants.BAND_WAVELENGTH.get(band.getName()));
        band.setNoDataValueUsed(true);

        GeoCoding bandGeoCoding = buildBandGeoCoding(tileMetadata.getTileComponent(), defaultBandWidth, defaultBandHeight, bandBounds);
        if (bandGeoCoding == null) {
            bandGeoCoding = productGeoCoding;
        }
        if (bandGeoCoding != null) {
            band.setGeoCoding(bandGeoCoding);
        }
        GeoTiffMatrixMultiLevelSource multiLevelSource = new GeoTiffMatrixMultiLevelSource(mosaicMatrix, bandBounds, preferredTileSize, bandIndex, bandGeoCoding);
        band.setSourceImage(new DefaultMultiLevelImage(multiLevelSource));

        band.setScalingFactor(tileMetadata.getTileComponent().getScalingFactor(band.getName()));
        return band;
    }

    private static GeoCoding buildBandGeoCoding(TileComponent tileComponent, int defaultBandWidth, int defaultBandHeight, Rectangle subsetBounds) {
        String crsCode = tileComponent.computeCRSCode();
        GeoCoding geoCoding = null;
        if (crsCode != null) {
            try {
                CoordinateReferenceSystem crs = CRS.decode(crsCode);
                double stepSize = tileComponent.getStepSize();
                double originX = tileComponent.getOriginX();
                double originY = tileComponent.getOriginY();
                geoCoding = ImageUtils.buildCrsGeoCoding(originX, originY, stepSize, stepSize, defaultBandWidth, defaultBandHeight, crs, subsetBounds);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to read the band geo coding.", e);
            }
        }
        return geoCoding;
    }

    public static String buildMetadataFileName(VirtualDirEx productDirectory) {
        String baseItemName = productDirectory.getBaseFile().getName();
        // product file name differs from archive file name
        int index;
        if (productDirectory.isArchive()) {
            index = baseItemName.lastIndexOf(WorldView2ESAConstants.PRODUCT_FILE_SUFFIX);
        } else {
            index = baseItemName.lastIndexOf(".");
        }
        if (index > 0) {
            return baseItemName.substring(0, index) + WorldView2ESAConstants.METADATA_FILE_SUFFIX;
        }
        throw new IllegalStateException("Invalid values: index " + index + ", baseItemName="+baseItemName+".");
    }

    public static Path buildImagesMetadataParentPath(VirtualDirEx productDirectory, String metadataFileName) throws IOException {
        int extensionIndex = metadataFileName.lastIndexOf(WorldView2ESAConstants.METADATA_FILE_SUFFIX);
        String fileNameWithoutExtension = metadataFileName.substring(0, extensionIndex);
        String zipArchiveFileName;
        if (!productDirectory.isArchive() && productDirectory.exists(fileNameWithoutExtension)) {
            zipArchiveFileName = fileNameWithoutExtension;
        } else {
            zipArchiveFileName = fileNameWithoutExtension + WorldView2ESAConstants.ARCHIVE_FILE_EXTENSION;
        }
        return productDirectory.getFile(zipArchiveFileName).toPath();
    }

    public static TileMetadataList readTileMetadataList(Path imagesMetadataParentPath) throws Exception {
        TileMetadataList tileMetadataList = new TileMetadataList();
        try (VirtualDirEx productBandsDirectory = VirtualDirEx.build(imagesMetadataParentPath, false, false)) {
            String[] allFileNames = productBandsDirectory.listAllFiles();
            for (String fileRelativePath : allFileNames) {
                if (fileRelativePath.endsWith(WorldView2ESAConstants.METADATA_EXTENSION) && !fileRelativePath.endsWith(WorldView2ESAConstants.METADATA_FILE_SUFFIX)
                        && !fileRelativePath.endsWith(EXCLUSION_STRING)) {

                    try (FilePathInputStream filePathInputStream = productBandsDirectory.getInputStream(fileRelativePath)) {
                        TileMetadata tileMetadata = TileMetadata.create(filePathInputStream);
                        tileMetadata.setFileName(fileRelativePath);
                        tileMetadataList.getTiles().add(tileMetadata);
                    }
                } else if (fileRelativePath.endsWith(WorldView2ESAConstants.IMAGE_EXTENSION)) {
                    tileMetadataList.getTiffImageRelativeFiles().add(fileRelativePath);
                }
            }
        }
        int multiSpectralBandCount = 0;
        for (TileMetadata tileMetadata : tileMetadataList.getTiles()) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            for (int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                for (String imageFileRelativePath : tileMetadataList.getTiffImageRelativeFiles()) {
                    if (imageFileRelativePath.contains(tileComponent.getTileNames()[filesIndex])) {
                        tileComponent.addDeliveredTile(tileComponent.getTileNames()[filesIndex]);
                        if (!tileComponent.getBandID().equals("P")) {
                            if (multiSpectralBandCount == 0) {
                                int bandCount;
                                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, imageFileRelativePath)) {
                                    SampleModel sampleModel = geoTiffImageReader.getBaseImage().getSampleModel();
                                    bandCount = sampleModel.getNumBands();
                                }
                                multiSpectralBandCount = bandCount;
                            }
                        }
                        break;
                    }
                }
            }
        }
        tileMetadataList.setMultiSpectralBandCount(multiSpectralBandCount);
        return tileMetadataList;
    }

    public static WorldView2ESAMetadata readMetadata(VirtualDirEx productDirectory, String metadataFileName)
                                                     throws InstantiationException, IOException, ParserConfigurationException, SAXException {

        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataFileName)) {
            return (WorldView2ESAMetadata)XmlMetadataParserFactory.getParser(WorldView2ESAMetadata.class).parse(filePathInputStream);
        }
    }
}
