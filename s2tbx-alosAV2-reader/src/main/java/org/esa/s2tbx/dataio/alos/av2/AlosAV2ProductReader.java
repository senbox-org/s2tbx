package org.esa.s2tbx.dataio.alos.av2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Metadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This product reader is intended for reading ALOS AVNIR-2 files
 * from compressed ESA archive files or from (uncompressed) file system.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2ProductReader extends AbstractProductReader {

    static {
        XmlMetadataParserFactory.registerParser(AlosAV2Metadata.class, new XmlMetadataParser<>(AlosAV2Metadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private GeoTiffImageReader geoTiffImageReader;

    public AlosAV2ProductReader(ProductReaderPlugIn readerPlugIn) {
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

            Path imageMetadataParentPath = buildImageMetadataParentPath(this.productDirectory);

            AlosAV2Metadata alosAV2Metadata;
            String imageMetadataRelativeFilePath;
            try (VirtualDirEx imageMetadataProductDirectory = VirtualDirEx.build(imageMetadataParentPath, false, false)) {
                imageMetadataRelativeFilePath = findImageMetadataRelativeFilePath(imageMetadataProductDirectory);
                alosAV2Metadata = readMetadata(imageMetadataProductDirectory, imageMetadataRelativeFilePath);
            }
            int extensionIndex = imageMetadataRelativeFilePath.lastIndexOf(AlosAV2Constants.IMAGE_METADATA_EXTENSION);
            String tiffImageRelativeFilePath = imageMetadataRelativeFilePath.substring(0, extensionIndex) + AlosAV2Constants.IMAGE_FILE_EXTENSION;

            this.geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imageMetadataParentPath, tiffImageRelativeFilePath);
            boolean isMultiSize = (alosAV2Metadata.getRasterWidth() != geoTiffImageReader.getImageWidth() || alosAV2Metadata.getRasterHeight() != geoTiffImageReader.getImageHeight());
            Dimension defaultProductSize = new Dimension(alosAV2Metadata.getRasterWidth(), alosAV2Metadata.getRasterHeight());
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
            } else {
                GeoCoding productDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, isMultiSize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(alosAV2Metadata.getProductName(), AlosAV2Constants.FORMAT_NAMES[0], productBounds.width, productBounds.height, this);
            product.setDescription(alosAV2Metadata.getProductDescription());
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            ProductData.UTC centerTime = alosAV2Metadata.getCenterTime();
            if (centerTime == null) {
                product.setStartTime(alosAV2Metadata.getProductStartTime());
                product.setEndTime(alosAV2Metadata.getProductEndTime());
            } else {
                product.setStartTime(centerTime);
                product.setEndTime(centerTime);
            }

            GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
            Product geoTiffProduct = geoTiffProductReader.readProduct(this.geoTiffImageReader, null, productBounds);

            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(alosAV2Metadata.getRootElement());
                if (geoTiffProduct.getMetadataRoot() != null) {
                    XmlMetadata.CopyChildElements(geoTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                }
            }

            geoTiffProduct.transferGeoCodingTo(product, null);

            TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(alosAV2Metadata);
            if (productGeoCoding != null) {
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
            }

            String[] bandNames = alosAV2Metadata.getBandNames();
            if (bandNames.length != geoTiffProduct.getNumBands()) {
                throw new IllegalStateException("The band count " + geoTiffProduct.getNumBands() + " of the GeoTiff product is not equal with the band count "+bandNames.length+ " from the image metadata file.");
            }

            // add bands
            for (int i=0; i<geoTiffProduct.getNumBands(); i++) {
                if (subsetDef == null || subsetDef.isNodeAccepted(bandNames[i])) {
                    String bandUnit = alosAV2Metadata.getBandUnits().get(bandNames[i]);
                    float scalingOffset = alosAV2Metadata.getBias(bandNames[i]);
                    float scalingFactor = alosAV2Metadata.getGain(bandNames[i]);

                    Band geoTiffBand = geoTiffProduct.getBandAt(i);
                    geoTiffBand.setName(bandNames[i]);
                    geoTiffBand.setUnit(bandUnit);
                    geoTiffBand.setScalingOffset(scalingOffset);
                    geoTiffBand.setScalingFactor(scalingFactor);

                    product.addBand(geoTiffBand);
                }
            }

            // remove the bands from the geo tif product
            geoTiffProduct.getBandGroup().removeAll();

            // add masks
            ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            if (subsetDef == null || subsetDef.isNodeAccepted(AlosAV2Constants.NODATA)) {
                int noDataValue = alosAV2Metadata.getNoDataValue();
                if (noDataValue >= 0) {
                    maskGroup.add(Mask.BandMathsType.create(AlosAV2Constants.NODATA, AlosAV2Constants.NODATA, product.getSceneRasterWidth(), product.getSceneRasterHeight(),
                            String.valueOf(noDataValue), alosAV2Metadata.getNoDataColor(), 0.5));
                }
            }
            if (subsetDef == null || subsetDef.isNodeAccepted(AlosAV2Constants.SATURATED)) {
                int saturatedValue = alosAV2Metadata.getSaturatedPixelValue();
                if (saturatedValue >= 0) {
                    maskGroup.add(Mask.BandMathsType.create(AlosAV2Constants.SATURATED, AlosAV2Constants.SATURATED, product.getSceneRasterWidth(), product.getSceneRasterHeight(),
                            String.valueOf(saturatedValue), alosAV2Metadata.getSaturatedColor(), 0.5));
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

    private void closeResources() {
        try {
            if (this.geoTiffImageReader != null) {
                this.geoTiffImageReader.close();
                this.geoTiffImageReader = null;
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

    public static TiePointGeoCoding buildTiePointGridGeoCoding(AlosAV2Metadata alosAV2Metadata) {
        if (!AlosAV2Constants.PROCESSING_1B.equals(alosAV2Metadata.getProcessingLevel())) {
            AlosAV2Metadata.InsertionPoint[] geoPositionPoints = alosAV2Metadata.getGeopositionPoints();
            if (geoPositionPoints != null) {
                int numPoints = geoPositionPoints.length;
                if (numPoints > 1 && (int) (numPoints / Math.sqrt((double) numPoints)) == numPoints) {
                    float stepX = geoPositionPoints[1].stepX - geoPositionPoints[0].stepX;
                    float stepY = geoPositionPoints[1].stepY - geoPositionPoints[0].stepY;
                    float[] latitudes = new float[numPoints];
                    float[] longitudes = new float[numPoints];
                    for (int i = 0; i < numPoints; i++) {
                        latitudes[i] = geoPositionPoints[i].y;
                        longitudes[i] = geoPositionPoints[i].x;
                    }
                    int latitudeGridSize = (int) Math.sqrt(latitudes.length);
                    TiePointGrid latGrid = buildTiePointGrid("latitude", latitudeGridSize, latitudeGridSize, 0, 0, stepX, stepY, latitudes, TiePointGrid.DISCONT_NONE);
                    int longitudeGridSize = (int) Math.sqrt(longitudes.length);
                    TiePointGrid lonGrid = buildTiePointGrid("longitude", longitudeGridSize, longitudeGridSize, 0, 0, stepX, stepY, longitudes, TiePointGrid.DISCONT_AT_180);
                    return new TiePointGeoCoding(latGrid, lonGrid);
                }
            }
        }
        return null;
    }

    public static Path buildImageMetadataParentPath(VirtualDirEx productDirectory) throws IOException {
        String baseItemName = productDirectory.getBaseFile().getName();
        if (productDirectory.isArchive()) {
            // the product directory is an archive
            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(baseItemName, AlosAV2Constants.PRODUCT_ARCHIVE_FILE_SUFFIX)) {
                int index = org.apache.commons.lang.StringUtils.lastIndexOfIgnoreCase(baseItemName, AlosAV2Constants.PRODUCT_ARCHIVE_FILE_SUFFIX);
                String identifier = baseItemName.substring(0, index);
                String zipArchiveFileName = identifier + AlosAV2Constants.IMAGE_ARCHIVE_FILE_EXTENSION;
                return productDirectory.getFile(zipArchiveFileName).toPath();
            }
        } else {
            // the product directory is a folder
            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(baseItemName, AlosAV2Constants.PRODUCT_FOLDER_SUFFIX)) {
                int index = org.apache.commons.lang.StringUtils.lastIndexOfIgnoreCase(baseItemName, AlosAV2Constants.PRODUCT_FOLDER_SUFFIX);
                String identifier = baseItemName.substring(0, index);
                if (productDirectory.exists(identifier)) {
                    // the identifier exists and it is a directory
                    return productDirectory.getFile(identifier).toPath();
                }
                // the identifier does not exists and add the .zip extension
                String zipArchiveFileName = identifier + AlosAV2Constants.IMAGE_ARCHIVE_FILE_EXTENSION;
                return productDirectory.getFile(zipArchiveFileName).toPath();
            }
        }
        // search the image metadata file
        String[] relativePaths = productDirectory.listAllFiles();
        for (String relativeFilePath : relativePaths) {
            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(relativeFilePath, AlosAV2Constants.IMAGE_METADATA_EXTENSION)) {
                return productDirectory.getBaseFile().toPath();
            }
        }
        throw new IllegalArgumentException("The product directory '" + productDirectory.getBaseFile().toPath()+"' does not contain the image metadata file.");
    }

    public static String findImageMetadataRelativeFilePath(VirtualDirEx imageMetadataProductDirectory) throws IOException {
        String[] relativePaths = imageMetadataProductDirectory.listAllFiles();
        String imageMetadataRelativeFilePath = null;
        for (String relativeFilePath : relativePaths) {
            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(relativeFilePath, AlosAV2Constants.IMAGE_METADATA_EXTENSION)) {
                if (imageMetadataRelativeFilePath == null) {
                    imageMetadataRelativeFilePath = relativeFilePath;
                } else {
                    throw new IllegalStateException("Multiple metadata files."); // only one image metadata file is allowed
                }
            }
        }
        return imageMetadataRelativeFilePath;
    }

    public static AlosAV2Metadata readMetadata(VirtualDirEx imageMetadataProductDirectory, String imageMetadataRelativeFilePath)
                                               throws InstantiationException, IOException, ParserConfigurationException, SAXException {

        try (FilePathInputStream filePathInputStream = imageMetadataProductDirectory.getInputStream(imageMetadataRelativeFilePath)) {
            AlosAV2Metadata metadataItem = (AlosAV2Metadata) XmlMetadataParserFactory.getParser(AlosAV2Metadata.class).parse(filePathInputStream);
            String metadataProfile = metadataItem.getMetadataProfile();
            if (metadataProfile != null) {
                metadataItem.setName(metadataProfile);
            }
            return metadataItem;
        }
    }
}
