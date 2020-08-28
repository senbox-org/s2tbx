package org.esa.s2tbx.dataio.alos.pri;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIConstants;
import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alos.pri.internal.ImageMetadata;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.readers.MultipleMetadataGeoTiffBasedReader;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

            boolean isMultisize = alosPriMetadata.isMultiSize();
            GeoCoding productDefaultGeoCoding = null;
            Rectangle productBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                productBounds = new Rectangle(0, 0, defaultProductSize.width, defaultProductSize.height);
            } else {
                productDefaultGeoCoding = buildGeoCoding(alosPriMetadata, defaultProductSize, null, null);
                productBounds = subsetDef.getSubsetRegion().computeProductPixelRegion(productDefaultGeoCoding, defaultProductSize.width, defaultProductSize.height, isMultisize);
            }
            if (productBounds.isEmpty()) {
                throw new IllegalStateException("Empty product bounds.");
            }

            Product product = new Product(alosPriMetadata.getProductName(), AlosPRIConstants.FORMAT_NAMES[0], productBounds.width, productBounds.height, this);
            product.setStartTime(alosPriMetadata.getProductStartTime());
            product.setEndTime(alosPriMetadata.getProductEndTime());
            product.setDescription(alosPriMetadata.getProductDescription());
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                product.getMetadataRoot().addElement(alosPriMetadata.getRootElement());
            }
            GeoCoding productGeoCoding = buildGeoCoding(alosPriMetadata, defaultProductSize, productBounds, subsetDef);
            if (productGeoCoding instanceof TiePointGeoCoding) {
                product.addTiePointGrid(((TiePointGeoCoding) productGeoCoding).getLatGrid());
                product.addTiePointGrid(((TiePointGeoCoding) productGeoCoding).getLonGrid());
            }
            product.setSceneGeoCoding(productGeoCoding);

            this.bandImageReaders = new ArrayList<>(alosPriMetadata.getImageMetadataList().size());
            ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
            // each metadata represents a subproduct
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
                    double noDataValue = imageMetadata.getNoDataValue();
                    Product geoTiffProduct = readGeoTiffProduct(productDefaultGeoCoding, defaultProductSize, imagesMetadataParentPath, alosPriMetadata,
                                                                imageMetadata, subsetDef, isMultisize, noDataValue);
                    if (geoTiffProduct != null) {
                        Band geoTiffBand = geoTiffProduct.getBandAt(0);
                        geoTiffBand.setName(imageMetadata.getBandName());
                        geoTiffBand.setUnit(imageMetadata.getBandUnit());
                        geoTiffBand.setDescription(imageMetadata.getBandDescription());
                        geoTiffBand.setScalingFactor(imageMetadata.getGain());
                        geoTiffBand.setNoDataValue(noDataValue);
                        geoTiffBand.setNoDataValueUsed(true);

                        product.addBand(geoTiffBand);

                        // remove the bands from the geo tif product
                        geoTiffProduct.getBandGroup().removeAll();
                    }
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

    private Product readGeoTiffProduct(GeoCoding productDefaultGeoCoding, Dimension defaultProductSize, Path imagesMetadataParentPath,
                                       AlosPRIMetadata alosPriMetadata, ImageMetadata imageMetadata, ProductSubsetDef subsetDef,
                                       boolean isMultisize, double noDataValue) throws Exception {

        boolean success = false;
        GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(imagesMetadataParentPath, imageMetadata.getImageRelativeFilePath());
        try {
            Dimension defaultSubProductSize = geoTiffImageReader.validateSize(imageMetadata.getRasterWidth(), imageMetadata.getRasterHeight());
            Rectangle subProductBounds;
            if (subsetDef == null || subsetDef.getSubsetRegion() == null) {
                subProductBounds = new Rectangle(defaultSubProductSize.width, defaultSubProductSize.height);
            } else {
                GeoCoding subProductDefaultGeoCoding = GeoTiffProductReader.readGeoCoding(geoTiffImageReader, null);
                // use the default product size to compute the band of a subproduct because the subset region
                // is set according to the default product size
                subProductBounds = subsetDef.getSubsetRegion().computeBandPixelRegion(productDefaultGeoCoding, subProductDefaultGeoCoding, defaultProductSize.width,
                                                                               defaultProductSize.height, defaultSubProductSize.width, defaultSubProductSize.height, isMultisize);
            }
            if (subProductBounds.isEmpty()) {
                return null; // no intersection
            }
            AlosPRIGeoTiffProductReader geoTiffProductReader = new AlosPRIGeoTiffProductReader(getReaderPlugIn(), alosPriMetadata, imageMetadata, defaultSubProductSize, subProductBounds);
            Product geoTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, subProductBounds, noDataValue);

            success = true;

            return geoTiffProduct;
        } finally {
            if (success) {
                this.bandImageReaders.add(geoTiffImageReader);
            } else {
                geoTiffImageReader.close();
            }
        }
    }

    private static Mask buildSaturatedMask(int productWith, int productHeight, int saturatedValue) {
        return Mask.BandMathsType.create(AlosPRIConstants.SATURATED, AlosPRIConstants.SATURATED, productWith, productHeight, String.valueOf(saturatedValue), Color.ORANGE, 0.5);
    }

    private static Mask buildNoDataMask(int productWith, int productHeight, int noDataValue) {
        return Mask.BandMathsType.create(AlosPRIConstants.NODATA, AlosPRIConstants.NODATA, productWith, productHeight, String.valueOf(noDataValue), Color.BLACK, 0.5);
    }

    public static AlosPRIMetadata readMetadata(VirtualDirEx productDirectory, String metadataRelativeFilePath, Path imagesMetadataParentPath)
                                               throws IOException, SAXException, ParserConfigurationException, InstantiationException {

        AlosPRIMetadata alosPriMetadata = MultipleMetadataGeoTiffBasedReader.readProductMetadata(productDirectory, metadataRelativeFilePath, AlosPRIMetadata.class);
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
                    ImageMetadata imageMetadata = MultipleMetadataGeoTiffBasedReader.readProductMetadata(zipArchiveProductDirectory, relativeFilePath, ImageMetadata.class);
                    int extensionIndex = relativeFilePath.lastIndexOf(AlosPRIConstants.IMAGE_METADATA_EXTENSION);
                    String gtifImageRelativeFilePath = relativeFilePath.substring(0, extensionIndex) + AlosPRIConstants.IMAGE_EXTENSION;
                    if (!zipArchiveProductDirectory.exists(gtifImageRelativeFilePath)) {
                        throw new FileNotFoundException("The GTIF image file path '" + gtifImageRelativeFilePath + "' does not exists into the product directory '" + zipArchiveProductDirectory.getBasePath() + "'.");
                    }
                    imageMetadata.setImageRelativeFilePath(gtifImageRelativeFilePath);
                    componentMetadata.add(imageMetadata);
                }
            }
            return componentMetadata;
        }
    }

    public static TiePointGeoCoding buildTiePointGridGeoCoding(AlosPRIMetadata metadata, int defaultRasterWidth, int defaultRasterHeight, ProductSubsetDef subsetDef) {
        float[][] cornerLonsLats = metadata.getMaxCorners();
        TiePointGrid latGrid = buildTiePointGrid(AlosPRIConstants.LAT_DS_NAME, 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[1], TiePointGrid.DISCONT_NONE);
        TiePointGrid lonGrid = buildTiePointGrid(AlosPRIConstants.LON_DS_NAME, 2, 2, 0, 0, defaultRasterWidth, defaultRasterHeight, cornerLonsLats[0], TiePointGrid.DISCONT_AT_180);
        if (subsetDef != null && subsetDef.getSubsetRegion() != null) {
            lonGrid = TiePointGrid.createSubset(lonGrid, subsetDef);
            latGrid = TiePointGrid.createSubset(latGrid, subsetDef);
        }
        return new TiePointGeoCoding(latGrid, lonGrid);
    }

    public static GeoCoding buildGeoCoding(AlosPRIMetadata alosPriMetadata, Dimension defaultProductSize, Rectangle subsetBounds, ProductSubsetDef subsetDef)
                                           throws FactoryException, TransformException {

        if (alosPriMetadata.hasInsertPoint()) {
            CoordinateReferenceSystem mapCRS = CRS.decode(alosPriMetadata.getCrsCode());
            ImageMetadata.InsertionPoint origin = alosPriMetadata.getProductOrigin();
            return ImageUtils.buildCrsGeoCoding(origin.x, origin.y, origin.stepX, origin.stepY, defaultProductSize.width, defaultProductSize.height, mapCRS, subsetBounds);
        } else {
            return buildTiePointGridGeoCoding(alosPriMetadata, defaultProductSize.width, defaultProductSize.height, subsetDef);
        }
    }
}
