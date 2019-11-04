package org.esa.s2tbx.dataio.ikonos;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.ikonos.internal.GeoTiffImageReader;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.s2tbx.dataio.ikonos.internal.IkonosMultiLevelSource;
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
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.dataio.FileImageInputStreamSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.JAI;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


/**
 * Basic reader for Ikonos products.
 *
 * @author Denisa Stefanescu
 */

public class IkonosProductReader extends AbstractProductReader {

    private VirtualDirEx productDirectory;
    private Product product;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    protected IkonosProductReader(final ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        registerSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        Path inputFile = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
        this.productDirectory = VirtualDirEx.build(inputFile, false, true);
        try {
//            ProductSubsetDef productSubsetDef = new ProductSubsetDef();
//            productSubsetDef.setRegion(100, 100, 1000, 1000);
//            setSubsetDef(productSubsetDef);

            IkonosMetadata metadata;
            String metadataFileName = buildMedataFileName(this.productDirectory);
            try (FilePathInputStream filePathInputStream = this.productDirectory.getInputStream(metadataFileName)) {
                metadata = IkonosMetadata.create(filePathInputStream);
            }
            int extensionIndex = metadataFileName.lastIndexOf(IkonosConstants.METADATA_FILE_SUFFIX);
            String zipArchiveRelativeFilePath = metadataFileName.substring(0, extensionIndex) + IkonosConstants.ARCHIVE_FILE_EXTENSION;

            // unzip the necessary files in a temporary directory
            Path zipArchivePath = this.productDirectory.getFile(zipArchiveRelativeFilePath).toPath();
            VirtualDirEx zipArchiveProductDirectory = VirtualDirEx.build(zipArchivePath, false, true);

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

            ProductSubsetDef productSubset = getSubsetDef();
            BandMetadataUtil metadataUtil = new BandMetadataUtil(bandMetadataList.toArray(new BandMetadata[bandMetadataList.size()]));
            int imageWidth = metadataUtil.getMaxNumColumns();
            int imageHeight = metadataUtil.getMaxNumLines();
            if (productSubset != null) {
                if (productSubset.getRegion().width > imageWidth) {
                    throw new IllegalStateException("The region width " + productSubset.getRegion().width + " is greater than the product with " + imageWidth + ".");
                }
                imageWidth = productSubset.getRegion().width;
                if (productSubset.getRegion().height > imageHeight) {
                    throw new IllegalStateException("The region height " + productSubset.getRegion().height + " is greater than the product height " + imageHeight + ".");
                }
                imageHeight = productSubset.getRegion().height;
            }
            Dimension defaultTileSize = JAI.getDefaultTileSize();
            TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(metadata, imageWidth, imageHeight);

            this.product = new Product(metadata.getProductName(), IkonosConstants.PRODUCT_GENERIC_NAME, imageWidth, imageHeight, this);
            this.product.setStartTime(metadata.getProductStartTime());
            this.product.setEndTime(metadata.getProductEndTime());
            this.product.setDescription(metadata.getProductDescription());
            this.product.getMetadataRoot().addElement(metadata.getRootElement());
            this.product.setFileLocation(inputFile.toFile());
            this.product.setPreferredTileSize(defaultTileSize);
            this.product.addTiePointGrid(productGeoCoding.getLatGrid());
            this.product.addTiePointGrid(productGeoCoding.getLonGrid());
            this.product.setSceneGeoCoding(productGeoCoding);

            this.bandImageReaders = new ArrayList<>(bandMetadataList.size());
            for (int bandIndex = 0; bandIndex < bandMetadataList.size(); bandIndex++) {
                BandMetadata bandMetadata = bandMetadataList.get(bandIndex);
                int dataType = getTIFFImageDataType(zipArchiveProductDirectory, bandMetadata.getImageFileName());
                int bandWidth = bandMetadata.getNumColumns();
                int bandHeight = bandMetadata.getNumLines();
                Point bandSubsetOffset = new Point(0, 0);
                if (productSubset != null) {
                    if (productSubset.getRegion().width > bandWidth) {
                        throw new IllegalStateException("The region width " + productSubset.getRegion().width + " is greater than the band with " + bandWidth + ".");
                    }
                    bandWidth = productSubset.getRegion().width;
                    bandSubsetOffset.x = productSubset.getRegion().x;

                    if (productSubset.getRegion().height > bandHeight) {
                        throw new IllegalStateException("The region height " + productSubset.getRegion().height + " is greater than the band height " + bandHeight + ".");
                    }
                    bandHeight = productSubset.getRegion().height;
                    bandSubsetOffset.y = productSubset.getRegion().y;
                }

                String bandName = null;
                Double bandGain = null;
                for (int bandNameIndex = 0; bandNameIndex < IkonosConstants.BAND_NAMES.length - 1; bandNameIndex++) {
                    if (bandMetadata.getImageFileName().contains(IkonosConstants.FILE_NAMES[bandNameIndex])) {
                        switch (IkonosConstants.BAND_NAMES[bandNameIndex]) {
                            case "1":
                                bandName = "Blue";
                                break;
                            case "2":
                                bandName = "Green";
                                break;
                            case "3":
                                bandName = "Red";
                                break;
                            case "4":
                                bandName = "Near";
                                break;
                        }
                        bandGain = IkonosConstants.BAND_GAIN[bandNameIndex];
                    }
                }
                if (bandName == null) {
                    bandName = IkonosConstants.BAND_NAMES[4];
                }

                int bandDataType = ImageManager.getProductDataType(dataType);
                Band targetBand = new Band(bandName, bandDataType, bandWidth, bandHeight);
                if (bandGain != null) {
                    targetBand.setScalingFactor(bandGain.doubleValue());
                }

                GeoCoding bandGeoCoding = null;
                if (imageWidth != targetBand.getRasterWidth() || imageHeight != targetBand.getRasterHeight()) {
                    double matrix00 = (double) imageWidth / targetBand.getRasterWidth();
                    double matrix11 = (double) imageHeight / targetBand.getRasterHeight();
                    AffineTransform2D transform2D = new AffineTransform2D(matrix00, 0.0d, 0.0d, matrix11, 0.0d, 0.0d);
                    targetBand.setImageToModelTransform(transform2D);
                    bandGeoCoding = buildTiePointGridGeoCoding(metadata, targetBand.getRasterWidth(), targetBand.getRasterHeight());
                    targetBand.setGeoCoding(bandGeoCoding);
                }

                GeoTiffImageReader geoTiffImageReader = new GeoTiffImageReader(zipArchiveProductDirectory, bandMetadata.getImageFileName());
                this.bandImageReaders.add(geoTiffImageReader);

                IkonosMultiLevelSource bandSource = new IkonosMultiLevelSource(geoTiffImageReader, dataType, bandSubsetOffset, bandWidth, bandHeight, defaultTileSize, bandGeoCoding);
                targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));

                this.product.addBand(targetBand);
            }
            return this.product;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm)
            throws IOException {
        // do nothing
    }

    @Override
    public void close() throws IOException {
        System.gc();

        if (this.bandImageReaders != null) {
            for (GeoTiffImageReader geoTiffImageReader : this.bandImageReaders) {
                geoTiffImageReader.close();
            }
        }

        if (this.product != null) {
            for (Band band : this.product.getBands()) {
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

        super.close();
    }

    private TiePointGeoCoding buildTiePointGridGeoCoding(IkonosMetadata metadata, int sceneWidth, int sceneHeight) {
        float[][] cornerLonsLats = metadata.getMetadataComponent().getTiePointGridPoints();
        TiePointGrid latGrid = createTiePointGrid(IkonosConstants.LAT_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[0]);
        TiePointGrid lonGrid = createTiePointGrid(IkonosConstants.LON_DS_NAME, 2, 2, 0, 0, sceneWidth, sceneHeight, cornerLonsLats[1]);
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    private void registerSpi() {
        final IIORegistry defaultInstance = IIORegistry.getDefaultInstance();
        Iterator<ImageInputStreamSpi> serviceProviders = defaultInstance.getServiceProviders(ImageInputStreamSpi.class, true);
        ImageInputStreamSpi toUnorder = null;
        if (defaultInstance.getServiceProviderByClass(FileImageInputStreamSpi.class) == null) {
            // register only if not already registered
            while (serviceProviders.hasNext()) {
                ImageInputStreamSpi current = serviceProviders.next();
                if (current.getInputClass() == File.class) {
                    toUnorder = current;
                    break;
                }
            }
            this.imageInputStreamSpi = new FileImageInputStreamSpi();
            defaultInstance.registerServiceProvider(imageInputStreamSpi);
            if (toUnorder != null) {
                // Make the custom Spi to be the first one to be used.
                defaultInstance.setOrdering(ImageInputStreamSpi.class, imageInputStreamSpi, toUnorder);
            }
        }
    }

    private static String buildMedataFileName(VirtualDirEx productDirectory) {
        File baseFile = productDirectory.getBaseFile();
        // product file name differs from archive file name
        String fileName;
        if (productDirectory.isArchive()) {
            fileName = baseFile.getName().substring(0, baseFile.getName().lastIndexOf(IkonosConstants.PRODUCT_FILE_SUFFIX));
        } else {
            fileName = baseFile.getName().substring(0, baseFile.getName().lastIndexOf("."));
        }
        return fileName + IkonosConstants.METADATA_FILE_SUFFIX;
    }

    private static int getTIFFImageDataType(VirtualDirEx productDirectory, String tiffImageRelativeFilePath) throws IOException {
        try (FilePathInputStream inputStream = productDirectory.getInputStream(tiffImageRelativeFilePath)) {
            if (tiffImageRelativeFilePath.endsWith(IkonosConstants.IMAGE_ARCHIVE_EXTENSION)) {
                // the Gzip archive contains only the TIFF image file
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                     GZIPInputStream gzipInputStream = new GZIPInputStream(bufferedInputStream);
                     ImageInputStream imageInputStream = ImageIO.createImageInputStream(gzipInputStream)) {

                    TIFFImageReader imageReader = GeoTiffImageReader.getTIFFImageReader(imageInputStream);
                    return GeoTiffImageReader.getTIFFImageDataType(imageReader);
                }
            } else {
                try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
                    TIFFImageReader imageReader = GeoTiffImageReader.getTIFFImageReader(imageInputStream);
                    return GeoTiffImageReader.getTIFFImageDataType(imageReader);
                }
            }
        }
    }
}
