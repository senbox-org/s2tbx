package org.esa.s2tbx.dataio.ikonos;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.ikonos.metadata.*;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.JAI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

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
    public MetadataInspector getMetadataInspector() {
        return new IkonosMetadataInspector();
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

            Path zipArchivePath = buildZipArchivePath(this.productDirectory, metadataFileName);

            Product product = readProduct(metadata, zipArchivePath);
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

        Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, getSubsetDef());

        Product product = new Product(metadata.getProductName(), IkonosConstants.PRODUCT_GENERIC_NAME, productBounds.width, productBounds.height, this);
        product.setStartTime(metadata.getProductStartTime());
        product.setEndTime(metadata.getProductEndTime());
        product.setDescription(metadata.getProductDescription());
        if((getSubsetDef() != null  && !getSubsetDef().isIgnoreMetadata()) || getSubsetDef() == null) {
            product.getMetadataRoot().addElement(metadata.getRootElement());
        }
        product.setPreferredTileSize(JAI.getDefaultTileSize());

        this.bandImageReaders = new ArrayList<>(bandMetadataList.size());

        for (int bandIndex = 0; bandIndex < bandMetadataList.size(); bandIndex++) {

            BandMetadata bandMetadata = bandMetadataList.get(bandIndex);
            boolean bandIsSelected = true;
            if (getSubsetDef() != null && !Arrays.asList(getSubsetDef().getNodeNames()).contains("allBands")) {
                if (!Arrays.asList(getSubsetDef().getNodeNames()).contains(getBandName(bandMetadata.getImageFileName()))) {
                    bandIsSelected = false;
                }
            }
            if(!bandIsSelected && getBandName(bandMetadata.getImageFileName()).equals(IkonosConstants.BAND_NAMES[4])){
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(zipArchivePath, bandMetadata.getImageFileName());
                this.bandImageReaders.add(geoTiffImageReader);
                Dimension defaultBandSize = new Dimension(geoTiffImageReader.getImageWidth(), geoTiffImageReader.getImageHeight());
                Rectangle bandBounds = ImageUtils.computeBandBounds(productBounds, defaultProductSize, defaultBandSize, metadataUtil.getProductStepX(), metadataUtil.getProductStepY(), bandMetadata.getPixelSizeX(), bandMetadata.getPixelSizeY());
                IkonosGeoTiffProductReader geoTiffProductReader = new IkonosGeoTiffProductReader(getReaderPlugIn(), metadata, new Dimension(productBounds.width, productBounds.height), defaultBandSize, getSubsetDef());
                Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, zipArchivePath, bandBounds);
                if (geoTiffProduct.getBandAt(0).getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                    product.setSceneGeoCoding(geoTiffProduct.getBandAt(0).getGeoCoding());
                }
            }
            if(bandIsSelected) {
                if (bandMetadata.getNumColumns() > defaultProductSize.width) {
                    throw new IllegalStateException("The band width " + bandMetadata.getNumColumns() + " from the metadata file is greater than the product width " + defaultProductSize.width + ".");
                }
                if (bandMetadata.getNumLines() > defaultProductSize.height) {
                    throw new IllegalStateException("The band height " + bandMetadata.getNumLines() + " from the metadata file is greater than the product height " + defaultProductSize.height + ".");
                }

                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(zipArchivePath, bandMetadata.getImageFileName());
                this.bandImageReaders.add(geoTiffImageReader);

                Dimension defaultBandSize = new Dimension(geoTiffImageReader.getImageWidth(), geoTiffImageReader.getImageHeight());

                if (defaultBandSize.width != bandMetadata.getNumColumns()) {
                    throw new IllegalStateException("The band width " + bandMetadata.getNumColumns() + " from the metadata file is not equal with the image width " + defaultBandSize.width + ".");
                }
                if (defaultBandSize.height != bandMetadata.getNumLines()) {
                    throw new IllegalStateException("The band height " + bandMetadata.getNumLines() + " from the metadata file is not equal with the image height " + defaultBandSize.height + ".");
                }

                Rectangle bandBounds = ImageUtils.computeBandBounds(productBounds, defaultProductSize, defaultBandSize, metadataUtil.getProductStepX(), metadataUtil.getProductStepY(), bandMetadata.getPixelSizeX(), bandMetadata.getPixelSizeY());

                // read the Geo Tiff product
                Dimension productSize = new Dimension(productBounds.width, productBounds.height);
                IkonosGeoTiffProductReader geoTiffProductReader = new IkonosGeoTiffProductReader(getReaderPlugIn(), metadata, productSize, defaultBandSize, getSubsetDef());
                Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, zipArchivePath, bandBounds);

                if (geoTiffProduct.getSceneGeoCoding() == null && product.getSceneGeoCoding() == null) {
                    TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(metadata, defaultProductSize.width, defaultProductSize.height, getSubsetDef());
                    product.addTiePointGrid(productGeoCoding.getLatGrid());
                    product.addTiePointGrid(productGeoCoding.getLonGrid());
                    product.setSceneGeoCoding(productGeoCoding);
                }

                Band geoTiffBand = geoTiffProduct.getBandAt(0);
                String bandName = getBandName(bandMetadata.getImageFileName());
                Double bandGain = getBandGain(bandMetadata.getImageFileName());
                if (bandName.equals(IkonosConstants.BAND_NAMES[4])) {
                    bandGain = Arrays.asList(IkonosConstants.BAND_GAIN).stream().mapToDouble(p -> p).sum() / (IkonosConstants.BAND_NAMES.length - 1);
                    if (geoTiffBand.getGeoCoding() != null && product.getSceneGeoCoding() == null) {
                        product.setSceneGeoCoding(geoTiffBand.getGeoCoding());
                    }
                }
                geoTiffBand.setName(bandName);
                geoTiffBand.setScalingFactor(bandGain.doubleValue());
                geoTiffBand.setNoDataValueUsed(geoTiffBand.isNoDataValueUsed());
                geoTiffBand.setUnit(IkonosConstants.BAND_MEASURE_UNIT);

                product.addBand(geoTiffBand);


                // remove the bands from the geo tif product
                geoTiffProduct.getBandGroup().removeAll();
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
        TiePointGrid latGrid = buildTiePointGrid("latitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid("longitude", 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getRegion() != null) {
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

    public static Path buildZipArchivePath(VirtualDirEx productDirectory, String metadataFileName) throws IOException {
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
        VirtualDirEx zipArchiveProductDirectory = VirtualDirEx.build(zipArchivePath, false, false);
        try {
            String[] allFileNames = zipArchiveProductDirectory.listAllFiles();
            List<BandMetadata> bandMetadataList = new ArrayList<>();
            Map<String, Double> metadataInformationList = new HashMap<>();
            for (String itemName : allFileNames) {
                if (itemName.endsWith(IkonosConstants.IMAGE_METADATA_EXTENSION)) {
                    BandMetadata bandMetadata = IkonosMetadata.parseIMGMetadataFile(zipArchiveProductDirectory, itemName);
                    bandMetadataList.add(bandMetadata);
                } else if (itemName.endsWith(IkonosConstants.IMAGE_COMMON_METADATA_EXTENSION)) {
                    Map<String, Double> metadataInformation = IkonosMetadata.parseMetadataFile(zipArchiveProductDirectory, itemName);
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
        } finally {
            zipArchiveProductDirectory.close();
        }
    }
}
