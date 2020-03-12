package org.esa.s2tbx.dataio.ikonos;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.ikonos.metadata.BandMetadata;
import org.esa.s2tbx.dataio.ikonos.metadata.BandMetadataUtil;
import org.esa.s2tbx.dataio.ikonos.metadata.IkonosMetadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.JAI;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic reader for Ikonos products.
 *
 * @author Denisa Stefanescu
 */
public class IkonosProductReader extends AbstractProductReader {

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    protected IkonosProductReader(final ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            String metadataFileName = buildMetadataFileName(this.productDirectory);

            IkonosMetadata metadata;
            try (FilePathInputStream filePathInputStream = this.productDirectory.getInputStream(metadataFileName)) {
                metadata = IkonosMetadata.create(filePathInputStream);
            }

            Path imagesMetadataParentPath = buildImagesMetadataParentPath(this.productDirectory, metadataFileName);

            Product product = readProduct(metadata, imagesMetadataParentPath);
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

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm)
                                          throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    private Product readProduct(IkonosMetadata metadata, Path zipArchivePath) throws Exception {
        List<BandMetadata> bandMetadataList = readBandMetadata(zipArchivePath);
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
                if (bandName.equals(IkonosConstants.BAND_NAMES[4])) {
                    bandMetadataForDefaultProductGeoCoding = bandMetadata;
                    break;
                }
            }
            productDefaultGeoCoding = buildDefaultGeoCoding(metadata, bandMetadataForDefaultProductGeoCoding, zipArchivePath, defaultProductSize, null, null);
            productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, metadataUtil.isMultiSize());
        }
        if (productBounds.isEmpty()) {
            throw new IllegalStateException("Empty product bounds.");
        }

        Product product = new Product(metadata.getProductName(), IkonosConstants.PRODUCT_GENERIC_NAME, productBounds.width, productBounds.height, this);
        product.setStartTime(metadata.getProductStartTime());
        product.setEndTime(metadata.getProductEndTime());
        product.setDescription(metadata.getProductDescription());
        product.setPreferredTileSize(JAI.getDefaultTileSize());
        if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
            product.getMetadataRoot().addElement(metadata.getRootElement());
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
            if (!bandIsSelected && bandName.equals(IkonosConstants.BAND_NAMES[4])) {
                try (GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(zipArchivePath, bandMetadata.getImageFileName())) {
                    Dimension defaultBandSize = new Dimension(geoTiffImageReader.getImageWidth(), geoTiffImageReader.getImageHeight());
                    Rectangle bandBounds;
                    if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                        bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                    } else {
                        GeoCoding bandDefaultGeoCoding = buildDefaultGeoCoding(metadata, bandMetadata, zipArchivePath, defaultProductSize, geoTiffImageReader, null);
                        bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width,
                                                                                        defaultProductSize.height, defaultBandSize.width, defaultBandSize.height, metadataUtil.isMultiSize());
                    }
                    if (!bandBounds.isEmpty()) {
                        // there is an intersection
                        IkonosGeoTiffProductReader geoTiffProductReader = new IkonosGeoTiffProductReader(getReaderPlugIn(), metadata, product.getSceneRasterSize(), defaultBandSize, getSubsetDef());
                        Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);
                        if (geoTiffProduct.getBandAt(0).getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                            product.setSceneGeoCoding(geoTiffProduct.getBandAt(0).getGeoCoding());
                        }
                    }
                }
            }
            if (bandIsSelected) {
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(zipArchivePath, bandMetadata.getImageFileName());
                this.bandImageReaders.add(geoTiffImageReader);

                Dimension defaultBandSize = geoTiffImageReader.validateSize(bandMetadata.getNumColumns(), bandMetadata.getNumLines());
                Rectangle bandBounds;
                if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                    bandBounds = new Rectangle(defaultBandSize.width, defaultBandSize.height);
                } else {
                    GeoCoding bandDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
                    bandBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, bandDefaultGeoCoding, defaultProductSize.width,
                            defaultProductSize.height, defaultBandSize.width, defaultBandSize.height, metadataUtil.isMultiSize());
                }
                if (!bandBounds.isEmpty()) {
                    // there is an intersection

                    // read the Geo Tiff product
                    IkonosGeoTiffProductReader geoTiffProductReader = new IkonosGeoTiffProductReader(getReaderPlugIn(), metadata, product.getSceneRasterSize(), defaultBandSize, getSubsetDef());
                    Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);

                    if (geoTiffProduct.getSceneGeoCoding() == null && product.getSceneGeoCoding() == null) {
                        TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(metadata, defaultProductSize.width, defaultProductSize.height, getSubsetDef());
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
                    if (bandName.equals(IkonosConstants.BAND_NAMES[4])) {
                        bandGain = Arrays.asList(IkonosConstants.BAND_GAIN).stream().mapToDouble(p -> p).sum() / (IkonosConstants.BAND_NAMES.length - 1);
                        if (geoTiffBand.getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                            product.setSceneGeoCoding(geoTiffBand.getGeoCoding());
                        }
                    } else {
                        bandGain = getBandGain(bandMetadata.getImageFileName());
                    }
                    geoTiffBand.setName(bandName);
                    geoTiffBand.setScalingFactor(bandGain.doubleValue());
                    geoTiffBand.setUnit(IkonosConstants.BAND_MEASURE_UNIT);
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

    public static TiePointGeoCoding buildTiePointGridGeoCoding(IkonosMetadata metadata, int defaultRasterWidth, int defaultRasterHeight, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getSubsetRegion() != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    public static String getBandName(String bandFileName) {
        for (int bandNameIndex = 0; bandNameIndex < IkonosConstants.BAND_NAMES.length - 1; bandNameIndex++) {
            if (bandFileName.contains(IkonosConstants.FILE_NAMES[bandNameIndex])) {
                switch (IkonosConstants.BAND_NAMES[bandNameIndex]) {
                    case "1":
                        return "Blue";
                    case "2":
                        return "Green";
                    case "3":
                        return "Red";
                    case "4":
                        return "Near";
                }
            }
        }
        return IkonosConstants.BAND_NAMES[4]; // the default band name
    }

    private static Double getBandGain(String bandFileName) {
        for (int bandNameIndex = 0; bandNameIndex < IkonosConstants.BAND_NAMES.length - 1; bandNameIndex++) {
            if (bandFileName.contains(IkonosConstants.FILE_NAMES[bandNameIndex])) {
                return IkonosConstants.BAND_GAIN[bandNameIndex];
            }
        }
        return null;
    }

    public static Path buildImagesMetadataParentPath(VirtualDirEx productDirectory, String metadataFileName) throws IOException {
        int extensionIndex = metadataFileName.lastIndexOf(IkonosConstants.METADATA_FILE_SUFFIX);
        String fileNameWithoutExtension = metadataFileName.substring(0, extensionIndex);
        String zipArchiveFileName;
        if (!productDirectory.isArchive() && productDirectory.exists(fileNameWithoutExtension)) {
            zipArchiveFileName = fileNameWithoutExtension;
        } else {
            zipArchiveFileName = fileNameWithoutExtension + IkonosConstants.ARCHIVE_FILE_EXTENSION;
        }
        return productDirectory.getFile(zipArchiveFileName).toPath();
    }

    public static String buildMetadataFileName(VirtualDirEx productDirectory) {
        String baseItemName = productDirectory.getBaseFile().getName();
        // product file name differs from archive file name
        int index;
        if (productDirectory.isArchive()) {
            index = baseItemName.lastIndexOf(IkonosConstants.PRODUCT_FILE_SUFFIX);
        } else {
            index = baseItemName.lastIndexOf(".");
        }
        if (index > 0) {
            return baseItemName.substring(0, index) + IkonosConstants.METADATA_FILE_SUFFIX;
        }
        throw new IllegalStateException("Invalid values: index " + index + ", baseItemName="+baseItemName+".");
    }

    public static List<BandMetadata> readBandMetadata(Path zipArchivePath) throws IOException {
        try (VirtualDirEx zipArchiveProductBands = VirtualDirEx.build(zipArchivePath, false, false)) {
            String[] allFileNames = zipArchiveProductBands.listAllFiles();
            List<BandMetadata> bandMetadataList = new ArrayList<>();
            Map<String, Double> metadataInformationList = new HashMap<>();
            for (String itemName : allFileNames) {
                if (itemName.endsWith(IkonosConstants.IMAGE_METADATA_EXTENSION)) {
                    BandMetadata bandMetadata = IkonosMetadata.parseIMGMetadataFile(zipArchiveProductBands, itemName);
                    bandMetadataList.add(bandMetadata);
                } else if (itemName.endsWith(IkonosConstants.IMAGE_COMMON_METADATA_EXTENSION)) {
                    Map<String, Double> metadataInformation = IkonosMetadata.parseMetadataFile(zipArchiveProductBands, itemName);
                    metadataInformationList.putAll(metadataInformation);
                }
            }
            for (BandMetadata band : bandMetadataList) {
                band.setNominalAzimuth(metadataInformationList.get(IkonosConstants.TAG_NOMINAL_AZIMUTH));
                band.setNominalElevation(metadataInformationList.get(IkonosConstants.TAG_NOMINAL_ELEVATION));
                band.setSunAngleAzimuth(metadataInformationList.get(IkonosConstants.TAG_SUN_ANGLE_AZIMUTH));
                band.setSunAngleElevation(metadataInformationList.get(IkonosConstants.TAG_SUN_ANGLE_ELEVATION));
            }
            return bandMetadataList;
        }
    }

    public static GeoCoding buildDefaultGeoCoding(IkonosMetadata metadata, BandMetadata bandMetadata, Path zipArchivePath, Dimension defaultProductSize, GeoTiffImageReader geoTiffImageReader, ProductSubsetDef subsetDef) throws Exception {
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
