package org.esa.s2tbx.dataio.alos.pri;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIConstants;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alos.pri.internal.ImageMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.MetadataInspector;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This product reader is intended for reading ALOS PRISM files
 *
 * @author Denisa Stefanescu
 */

public class AlosPRIProductReader extends AbstractProductReader {

    static {
        XmlMetadataParserFactory.registerParser(AlosPRIMetadata.class, new AlosPRIMetadata.AlosPRIMetadataParser(AlosPRIMetadata.class));
    }

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    public AlosPRIProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new AlosPRIMetadataInspector();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            String metadataFileName = buildMetadataFileName(this.productDirectory);
            Path imagesMetadataParentPath = buildImagesMetadataParentPath(this.productDirectory, metadataFileName);

            AlosPRIMetadata alosPriMetadata = readMetadata(this.productDirectory, metadataFileName, imagesMetadataParentPath);

            Dimension defaultProductSize = new Dimension(alosPriMetadata.getRasterWidth(), alosPriMetadata.getRasterHeight());
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, subsetDef);

            Product product = new Product(alosPriMetadata.getProductName(), AlosPRIConstants.FORMAT_NAMES[0], productBounds.width, productBounds.height, this);
            product.setStartTime(alosPriMetadata.getProductStartTime());
            product.setEndTime(alosPriMetadata.getProductEndTime());
            product.setDescription(alosPriMetadata.getProductDescription());
            product.setFileLocation(productPath.toFile());
            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(alosPriMetadata.getRootElement());
            }
            if (alosPriMetadata.hasInsertPoint()) {
                CoordinateReferenceSystem mapCRS = CRS.decode(alosPriMetadata.getCrsCode());
                ImageMetadata.InsertionPoint origin = alosPriMetadata.getProductOrigin();
                GeoCoding geoCoding = new CrsGeoCoding(mapCRS, defaultProductSize.width, defaultProductSize.height, origin.x, origin.y, origin.stepX, origin.stepY);
                product.setSceneGeoCoding(geoCoding);
            } else {
                TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(alosPriMetadata, defaultProductSize.width, defaultProductSize.height, subsetDef);
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
            }

            this.bandImageReaders = new ArrayList<>(alosPriMetadata.getImageMetadataList().size());
            ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            for (ImageMetadata imageMetadata : alosPriMetadata.getImageMetadataList()) {
                if (subsetDef == null || subsetDef.isNodeAccepted(imageMetadata.getBandName())) {
                    if (imageMetadata.getRasterWidth() > defaultProductSize.width) {
                        throw new IllegalStateException("The band width " + imageMetadata.getRasterWidth() + " from the metadata file is greater than the product width " + defaultProductSize.width + ".");
                    }
                    if (imageMetadata.getRasterHeight() > defaultProductSize.height) {
                        throw new IllegalStateException("The band height " + imageMetadata.getRasterHeight() + " from the metadata file is greater than the product height " + defaultProductSize.height + ".");
                    }
                    if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                        product.getMetadataRoot().addElement(imageMetadata.getRootElement());
                    }

                    GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, imageMetadata.getImageRelativeFilePath());
                    this.bandImageReaders.add(geoTiffImageReader);

                    Dimension defaultBandSize = geoTiffImageReader.validateSize(imageMetadata.getRasterWidth(), imageMetadata.getRasterHeight());
                    Rectangle bandBounds = ImageUtils.computeBandBounds(productBounds, defaultProductSize, defaultBandSize, alosPriMetadata.getStepSizeX(), alosPriMetadata.getStepSizeY(), imageMetadata.getPixelSizeX(), imageMetadata.getPixelSizeY());

                    AlosPRIGeoTiffProductReader geoTiffProductReader = new AlosPRIGeoTiffProductReader(getReaderPlugIn(), alosPriMetadata, imageMetadata, defaultProductSize);
                    Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, imagesMetadataParentPath, bandBounds);

                    Band geoTiffBand = geoTiffProduct.getBandAt(0);
                    geoTiffBand.setName(imageMetadata.getBandName());
                    geoTiffBand.setUnit(imageMetadata.getBandUnit());
                    geoTiffBand.setDescription(imageMetadata.getBandDescription());
                    geoTiffBand.setScalingFactor(imageMetadata.getGain());
                    geoTiffBand.setNoDataValue(imageMetadata.getNoDataValue());
                    geoTiffBand.setNoDataValueUsed(true);

                    product.addBand(geoTiffBand);

                    // remove the bands from the geo tif product
                    geoTiffProduct.getBandGroup().removeAll();
                }

                if (subsetDef == null || subsetDef.isNodeAccepted(AlosPRIConstants.NODATA)) {
                    if (!maskGroup.contains(AlosPRIConstants.NODATA)) {
                        maskGroup.add(buildNoDataMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), imageMetadata.getNoDataValue()));
                    }
                }
                if (subsetDef == null || subsetDef.isNodeAccepted(AlosPRIConstants.SATURATED)) {
                    if (!maskGroup.contains(AlosPRIConstants.SATURATED)) {
                        maskGroup.add(buildSaturatedMask(product.getSceneRasterWidth(), product.getSceneRasterHeight(), imageMetadata.getSaturatedValue()));
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

    @Override
    public void close() throws IOException {
        super.close();

        closeResources();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
            throws IOException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void readTiePointGridRasterData(TiePointGrid tpg, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
            throws IOException {
        // do nothing
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

    private static Mask buildSaturatedMask(int productWith, int productHeight, int saturatedValue) {
        return Mask.BandMathsType.create(AlosPRIConstants.SATURATED, AlosPRIConstants.SATURATED, productWith, productHeight, String.valueOf(saturatedValue), Color.ORANGE, 0.5);
    }

    private static Mask buildNoDataMask(int productWith, int productHeight, int noDataValue) {
        return Mask.BandMathsType.create(AlosPRIConstants.NODATA, AlosPRIConstants.NODATA, productWith, productHeight, String.valueOf(noDataValue), Color.BLACK, 0.5);
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(AlosPRIMetadata metadata, int defaultRasterWidth, int defaultRasterHeight, ProductSubsetDef subsetDef) {
        float offsetX = (metadata.getMaxInsertPointX() - metadata.getMinInsertPointX()) / metadata.getStepSizeX();
        float offsetY = (metadata.getMaxInsertPointY() - metadata.getMinInsertPointY()) / metadata.getStepSizeY();
        return buildTiePointGridGeoCoding(metadata, defaultRasterWidth, defaultRasterHeight, offsetX, offsetY, subsetDef);
    }

    public static AlosPRIMetadata readMetadata(VirtualDirEx productDirectory, String metadataRelativeFilePath, Path imagesMetadataParentPath)
            throws IOException, SAXException, ParserConfigurationException, InstantiationException {

        AlosPRIMetadata alosPriMetadata;
        try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataRelativeFilePath)) {
            alosPriMetadata = XmlMetadata.loadMetadata(AlosPRIMetadata.class, filePathInputStream);
        }
        List<ImageMetadata> imageMetadataList = readImagesMetadata(imagesMetadataParentPath);
        if (imageMetadataList.isEmpty()) {
            throw new IllegalStateException("No raster found.");
        }
        alosPriMetadata.setComponentMetadata(imageMetadataList);
        return alosPriMetadata;
    }

    public static String buildMetadataFileName(VirtualDirEx productDirectory) {
        String baseItemName = productDirectory.getBaseFile().getName();
        int index;
        if (productDirectory.isArchive()) {
            index = baseItemName.lastIndexOf(AlosPRIConstants.PRODUCT_FILE_SUFFIX);
        } else {
            index = baseItemName.lastIndexOf(".");
        }
        if (index > 0) {
            return baseItemName.substring(0, index) + AlosPRIConstants.METADATA_FILE_SUFFIX;
        }
        throw new IllegalStateException("Invalid values: index " + index + ", baseItemName="+baseItemName+".");
    }

    public static Path buildImagesMetadataParentPath(VirtualDirEx productDirectory, String metadataFileName) throws IOException {
        int extensionIndex = metadataFileName.lastIndexOf(AlosPRIConstants.METADATA_FILE_SUFFIX);
        String fileNameWithoutExtension = metadataFileName.substring(0, extensionIndex);
        String zipArchiveFileName;
        if (!productDirectory.isArchive() && productDirectory.exists(fileNameWithoutExtension)) {
            zipArchiveFileName = fileNameWithoutExtension;
        } else {
            zipArchiveFileName = fileNameWithoutExtension + AlosPRIConstants.ARCHIVE_FILE_EXTENSION;
        }
        return productDirectory.getFile(zipArchiveFileName).toPath();
    }

    private static List<ImageMetadata> readImagesMetadata(Path imagesMetadataParentPath) throws IOException, SAXException, ParserConfigurationException, InstantiationException {
        try (VirtualDirEx zipArchiveProductDirectory = VirtualDirEx.build(imagesMetadataParentPath, false, false)) {
            String[] allFileNames = zipArchiveProductDirectory.listAllFiles();
            List<ImageMetadata> componentMetadata = new ArrayList<>();
            for (String relativeFilePath : allFileNames) {
                if (relativeFilePath.endsWith(AlosPRIConstants.IMAGE_METADATA_EXTENSION)) {
                    try (FilePathInputStream filePathInputStream = zipArchiveProductDirectory.getInputStream(relativeFilePath)) {
                        ImageMetadata imageMetadata = XmlMetadata.loadMetadata(ImageMetadata.class, filePathInputStream);
                        int extensionIndex = relativeFilePath.lastIndexOf(AlosPRIConstants.IMAGE_METADATA_EXTENSION);
                        String gtifImageRelativeFilePath = relativeFilePath.substring(0, extensionIndex) + AlosPRIConstants.IMAGE_EXTENSION;
                        if (!zipArchiveProductDirectory.exists(gtifImageRelativeFilePath)) {
                            throw new FileNotFoundException("The GTIF image file path '" + gtifImageRelativeFilePath + "' does not exists into the product directory '" + zipArchiveProductDirectory.getBasePath() + "'.");
                        }
                        imageMetadata.setImageRelativeFilePath(gtifImageRelativeFilePath);
                        componentMetadata.add(imageMetadata);
                    }
                }
            }
            return componentMetadata;
        }
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(AlosPRIMetadata metadata, int defaultRasterWidth, int defaultRasterHeight, float offsetX, float offsetY, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getMaxCorners();
        TiePointGrid latGrid = buildTiePointGrid(AlosPRIConstants.LAT_DS_NAME, 2, 2, offsetX, offsetY, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid(AlosPRIConstants.LON_DS_NAME, 2, 2, offsetX, offsetY, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getRegion() != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }
}
