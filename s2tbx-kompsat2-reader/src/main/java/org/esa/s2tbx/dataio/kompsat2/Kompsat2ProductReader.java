package org.esa.s2tbx.dataio.kompsat2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.kompsat2.internal.Kompsat2Constants;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadata;
import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadataUtil;
import org.esa.s2tbx.dataio.kompsat2.metadata.Kompsat2Component;
import org.esa.s2tbx.dataio.kompsat2.metadata.Kompsat2Metadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.MultipleMetadataGeoTiffBasedReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic reader for Kompsat 2 products.
 *
 * @author Razvan Dumitrascu
 * modified 20201002 to compute product and band bounds based on subset information by Denisa Stefanescu
 */

public class Kompsat2ProductReader extends AbstractProductReader {

    static {
        XmlMetadataParserFactory.registerParser(Kompsat2Metadata.class, new XmlMetadataParser(Kompsat2Metadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    public Kompsat2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {

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
            Path imagesMetadataParentPath = buildImagesMetadataParentPath(this.productDirectory, metadataFileName);
            Kompsat2Metadata productMetadata = readProductMetadata(this.productDirectory, metadataFileName);
            List<BandMetadata> bandMetadataList = readBandMetadata(imagesMetadataParentPath);

            Product product = readProduct(productMetadata, bandMetadataList, imagesMetadataParentPath);
            product.setFileLocation(productPath.toFile());

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

    private Product readProduct(Kompsat2Metadata productMetadata, List<BandMetadata> bandMetadataList, Path imagesMetadataParentPath) throws Exception {
        BandMetadataUtil metadataUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));

        Dimension defaultProductSize = new Dimension(metadataUtil.getMaxNumColumns(), metadataUtil.getMaxNumLines());
        ProductSubsetDef subsetDef = getSubsetDef();
        GeoCoding productDefaultGeoCoding = null;
        Rectangle productBounds;
        if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
            productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
        } else {
            BandMetadata bandMetadataForDefaultProductGeoCoding = null;
            for (BandMetadata bandMetadata : bandMetadataList) {
                String bandName = getBandName(bandMetadata.getImageFileName());
                if (bandName.equals(Kompsat2Constants.BAND_NAMES[4])) {
                    bandMetadataForDefaultProductGeoCoding = bandMetadata;
                    break;
                }
            }
            productDefaultGeoCoding = buildDefaultGeoCoding(productMetadata, bandMetadataForDefaultProductGeoCoding, imagesMetadataParentPath, defaultProductSize, null, null);
            productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, metadataUtil.isMultiSize());
        }
        if (productBounds.isEmpty()) {
            throw new IllegalStateException("Empty product bounds.");
        }

        Product product = new Product(productMetadata.getProductName(), Kompsat2Constants.KOMPSAT2_PRODUCT, productBounds.width, productBounds.height, this);
        product.setStartTime(productMetadata.getProductStartTime());
        product.setEndTime(productMetadata.getProductEndTime());
        product.setDescription(productMetadata.getProductDescription());

        Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
        product.setPreferredTileSize(preferredTileSize);
        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            product.getMetadataRoot().addElement(productMetadata.getRootElement());
        }

        this.bandImageReaders = new ArrayList<>(bandMetadataList.size());

        for (BandMetadata bandMetadata : bandMetadataList) {
            if (bandMetadata.getNumColumns() > defaultProductSize.width) {
                throw new IllegalStateException("The band width " + bandMetadata.getNumColumns() + " from the metadata file is greater than the product width " + defaultProductSize.width + ".");
            }
            if (bandMetadata.getNumLines() > defaultProductSize.height) {
                throw new IllegalStateException("The band height " + bandMetadata.getNumLines() + " from the metadata file is greater than the product height " + defaultProductSize.height + ".");
            }

            String bandName = getBandName(bandMetadata.getImageFileName());
            boolean bandIsSelected = (subsetDef == null || subsetDef.isNodeAccepted(bandName));
            if (!bandIsSelected && bandName.equals(Kompsat2Constants.BAND_NAMES[4])) {
                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, bandMetadata.getImageFileName())) {
                    Dimension defaultBandSize = geoTiffImageReader.validateSize(bandMetadata.getNumColumns(), bandMetadata.getNumLines());
                    Kompsat2GeoTiffProductReader geoTiffProductReader = new Kompsat2GeoTiffProductReader(getReaderPlugIn(), productMetadata, product.getSceneRasterSize(), defaultProductSize, subsetDef);
                    Rectangle bandBounds;
                    if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                        bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                    } else {
                        GeoCoding bandDefaultGeoCoding = buildDefaultGeoCoding(productMetadata, bandMetadata, imagesMetadataParentPath, defaultProductSize, geoTiffImageReader, null);
                        bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width,
                                                                                        defaultProductSize.height, defaultBandSize.width, defaultBandSize.height, metadataUtil.isMultiSize());
                    }

                    Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);
                    if (geoTiffProduct.getBandAt(0).getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                        product.setSceneGeoCoding(geoTiffProduct.getBandAt(0).getGeoCoding());
                    }
                }
            }
            if (bandIsSelected) {
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, bandMetadata.getImageFileName());
                this.bandImageReaders.add(geoTiffImageReader);

                Dimension defaultBandSize = geoTiffImageReader.validateSize(bandMetadata.getNumColumns(), bandMetadata.getNumLines());
                Kompsat2GeoTiffProductReader geoTiffProductReader = new Kompsat2GeoTiffProductReader(getReaderPlugIn(), productMetadata, product.getSceneRasterSize(), defaultProductSize, subsetDef);
                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                } else {
                    GeoCoding bandDefaultGeoCoding = buildDefaultGeoCoding(productMetadata, bandMetadata, imagesMetadataParentPath, defaultProductSize, geoTiffImageReader, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width,
                                                                                    defaultProductSize.height, defaultBandSize.width, defaultBandSize.height, metadataUtil.isMultiSize());
                }
                if (!bandBounds.isEmpty()) {
                    // there is an intersection
                    Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);

                    if (geoTiffProduct.getSceneGeoCoding() == null && product.getSceneGeoCoding() == null) {
                        TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(productMetadata, defaultProductSize.width, defaultProductSize.height, getSubsetDef());
                        product.addTiePointGrid(productGeoCoding.getLatGrid());
                        product.addTiePointGrid(productGeoCoding.getLonGrid());
                        product.setSceneGeoCoding(productGeoCoding);
                    }

                    if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                        if (geoTiffProduct.getMetadataRoot() != null) {
                            XmlMetadata.CopyChildElements(geoTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                        }
                    }

                    Band geoTiffBand = geoTiffProduct.getBandAt(0);
                    Double bandGain;
                    if (bandName.equals(Kompsat2Constants.BAND_NAMES[4])) {
                        bandGain = Arrays.asList(Kompsat2Constants.KOMPSAT2_GAIN_VALUES).stream().mapToDouble(p -> p).sum() / (Kompsat2Constants.BAND_NAMES.length - 1);
                        if (geoTiffBand.getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                            product.setSceneGeoCoding(geoTiffBand.getGeoCoding());
                        }
                    } else {
                        bandGain = getBandGain(bandMetadata.getImageFileName());
                    }
                    geoTiffBand.setName(bandName);
                    geoTiffBand.setScalingFactor(bandGain.doubleValue());
                    geoTiffBand.setUnit(Kompsat2Constants.KOMPSAT2_UNIT);
                    geoTiffBand.setSpectralWavelength(Kompsat2Constants.BandWaveLengthConstants.getWavelengthCentral(bandName));
                    geoTiffBand.setNoDataValueUsed(true);

                    product.addBand(geoTiffBand);

                    // remove the bands from the geo tif product
                    geoTiffProduct.getBandGroup().removeAll();
                }
            }
        }

        return product;
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

    public static String getBandName(String bandFileName) {
        for (int bandNameIndex = 0; bandNameIndex < Kompsat2Constants.BAND_NAMES.length - 1; bandNameIndex++) {
            if (bandFileName.contains(Kompsat2Constants.FILE_NAMES[bandNameIndex])) {
                return Kompsat2Constants.BAND_NAMES[bandNameIndex];
            }
        }
        return Kompsat2Constants.BAND_NAMES[4]; // the default band name
    }

    private static Double getBandGain(String bandFileName) {
        for (int bandNameIndex = 0; bandNameIndex < Kompsat2Constants.BAND_NAMES.length - 1; bandNameIndex++) {
            if (bandFileName.contains(Kompsat2Constants.FILE_NAMES[bandNameIndex])) {
                return Kompsat2Constants.KOMPSAT2_GAIN_VALUES[bandNameIndex];
            }
        }
        return null;
    }

    public static String buildMetadataFileName(VirtualDirEx productDirectory) {
        String baseItemName = productDirectory.getBaseFile().getName();
        int index;
        if (productDirectory.isArchive()) {
            index = baseItemName.lastIndexOf(Kompsat2Constants.PRODUCT_FILE_SUFFIX);
        } else {
            index = baseItemName.lastIndexOf(".");
        }
        if (index > 0) {
            return baseItemName.substring(0, index) + Kompsat2Constants.METADATA_FILE_SUFFIX;
        }
        throw new IllegalStateException("Invalid values: index " + index + ", baseItemName=" + baseItemName + ".");
    }

    public static Path buildImagesMetadataParentPath(VirtualDirEx productDirectory, String metadataFileName) throws IOException {
        int extensionIndex = metadataFileName.lastIndexOf(Kompsat2Constants.METADATA_FILE_SUFFIX);
        String fileNameWithoutExtension = metadataFileName.substring(0, extensionIndex);
        String zipArchiveFileName;
        if (!productDirectory.isArchive() && productDirectory.exists(fileNameWithoutExtension)) {
            zipArchiveFileName = fileNameWithoutExtension;
        } else {
            zipArchiveFileName = fileNameWithoutExtension + Kompsat2Constants.ARCHIVE_FILE_EXTENSION;
        }
        return productDirectory.getFile(zipArchiveFileName).toPath();
    }

    public static List<BandMetadata> readBandMetadata(Path imagesMetadataParentPath)
                                                      throws IOException, SAXException, ParserConfigurationException, InstantiationException {

        try (VirtualDirEx zipArchiveProductDirectory = VirtualDirEx.build(imagesMetadataParentPath, false, false)) {
            String[] allFileNames = zipArchiveProductDirectory.listAllFiles();
            List<BandMetadata> componentMetadata = new ArrayList<>();
            for (String relativeFilePath : allFileNames) {
                if (relativeFilePath.endsWith(Kompsat2Constants.IMAGE_METADATA_EXTENSION)) {
                    try (FilePathInputStream filePathInputStream = zipArchiveProductDirectory.getInputStream(relativeFilePath)) {
                        int extensionIndex = relativeFilePath.lastIndexOf(Kompsat2Constants.IMAGE_METADATA_EXTENSION);
                        String tifImageRelativeFilePath = relativeFilePath.substring(0, extensionIndex) + Kompsat2Constants.IMAGE_EXTENSION;
                        if (!zipArchiveProductDirectory.exists(tifImageRelativeFilePath)) {
                            throw new FileNotFoundException("The TIF image file path '" + tifImageRelativeFilePath + "' does not exists into the product directory '" + zipArchiveProductDirectory.getBasePath() + "'.");
                        }
                        BandMetadata bandMetadata = new BandMetadata(tifImageRelativeFilePath);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePathInputStream))) {
                            String line = reader.readLine();
                            while (line != null) {
                                if (line.startsWith(Kompsat2Constants.TAG_BAND_WIDTH)) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setBandwidth(Double.parseDouble(splitLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_BITS_PER_PIXEL)) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setBitsPerPixel(Integer.parseInt(splitLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_NUMBER_COLUMNS_MS_IMAGE) ||
                                        (line.startsWith(Kompsat2Constants.TAG_NUMBER_COLUMNS_PAN_IMAGE))) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setNumColumns(Integer.parseInt(splitLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_NUMBER_ROWS_MS_IMAGE) ||
                                        (line.startsWith(Kompsat2Constants.TAG_NUMBER_ROWS_PAN_IMAGE))) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setNumLines(Integer.parseInt(splitLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_PIXEL_SIZE)) {
                                    String[] splitLine = line.split("\\t");
                                    String[] splitResultLine = splitLine[1].split(" ");
                                    bandMetadata.setStepSizeX(Double.parseDouble(splitResultLine[0]));
                                    bandMetadata.setStepSizeY(Double.parseDouble(splitResultLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_AZIMUTH_ANGLE)) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setAzimuth(Double.parseDouble(splitLine[1]));
                                } else if (line.startsWith(Kompsat2Constants.TAG_INCIDENCE_ANGLE)) {
                                    String[] splitLine = line.split("\\t");
                                    bandMetadata.setIncidenceAngle(Double.parseDouble(splitLine[1]));
                                }
                                line = reader.readLine();
                            }
                        }
                        componentMetadata.add(bandMetadata);
                    }
                }
            }
            return componentMetadata;
        }
    }

    public static Kompsat2Metadata readProductMetadata(VirtualDirEx productDirectory, String metadataRelativeFilePath)
                                                       throws IOException, SAXException, ParserConfigurationException, InstantiationException {

        Kompsat2Metadata metadata = MultipleMetadataGeoTiffBasedReader.readProductMetadata(productDirectory, metadataRelativeFilePath, Kompsat2Metadata.class);
        String directoryName = metadata.getAttributeValue(Kompsat2Constants.PATH_ZIP_FILE_NAME, null);
        String tiePointGridPointsString = metadata.getAttributeValue(Kompsat2Constants.PATH_TIE_POINT_GRID, null);
        String crsCode = metadata.getAttributeValue(Kompsat2Constants.PATH_CRS_NAME, null);
        String originPos = metadata.getAttributeValue(Kompsat2Constants.PATH_ORIGIN, null);
        if (directoryName != null) {
            Kompsat2Component component = new Kompsat2Component();
            component.setImageDirectoryName(directoryName);
            if (tiePointGridPointsString != null) {
                component.setTiePointGridPoints(Kompsat2Metadata.parseTiePointGridAttribute(tiePointGridPointsString));
            }
            if (crsCode != null) {
                component.setCrsCode(crsCode);
            }
            if (originPos != null) {
                component.setOriginPos(originPos);
            }
            metadata.setMetadataComponent(component);
        }
        return metadata;
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(Kompsat2Metadata k2Metadata, int defaultRasterWidth, int defaultRasterHeight, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = k2Metadata.getMetadataComponent().getTiePointGridPoints();
        TiePointGrid latGrid = buildTiePointGrid(Kompsat2Constants.LAT_DS_NAME, 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid(Kompsat2Constants.LON_DS_NAME, 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getSubsetRegion() != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    public static GeoCoding buildDefaultGeoCoding(Kompsat2Metadata metadata, BandMetadata bandMetadata, Path zipArchivePath, Dimension defaultProductSize, GeoTiffImageReader geoTiffImageReader, ProductSubsetDef subsetDef) throws Exception {
        if(geoTiffImageReader == null) {
            geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(zipArchivePath, bandMetadata.getImageFileName());
        }
        GeoCoding productGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
        if (productGeoCoding == null) {
            productGeoCoding = buildTiePointGridGeoCoding(metadata, defaultProductSize.width, defaultProductSize.height, subsetDef);
        }
        return productGeoCoding;
    }
}
