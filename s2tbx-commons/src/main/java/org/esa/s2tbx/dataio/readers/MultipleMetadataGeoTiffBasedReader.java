package org.esa.s2tbx.dataio.readers;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 9/12/2019.
 */
public abstract class MultipleMetadataGeoTiffBasedReader<MetadataType extends XmlMetadata> extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(MultipleMetadataGeoTiffBasedReader.class.getName());

    private VirtualDirEx productDirectory;
    private ImageInputStreamSpi imageInputStreamSpi;
    private List<GeoTiffImageReader> bandImageReaders;

    protected MultipleMetadataGeoTiffBasedReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.imageInputStreamSpi = ImageRegistryUtils.registerImageInputStreamSpi();
    }

    protected abstract MetadataType findFirstMetadataItem(List<MetadataType> metadataList);

    protected abstract TiePointGeoCoding buildTiePointGridGeoCoding(MetadataType firstMetadata, List<MetadataType> metadataList);

    protected abstract List<MetadataType> readMetadataList(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException;

    protected abstract String getProductType();

    protected abstract String getGenericProductName();

    protected abstract String[] getBandNames(MetadataType metadata);

    protected abstract List<Mask> buildMasks(int productWith, int productHeight, MetadataType firstMetadata, ProductSubsetDef subsetDef);

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
    protected Product readProductNodesImpl() throws IOException {
        boolean success = false;
        try {
            Path productPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
            this.productDirectory = VirtualDirEx.build(productPath, false, true);

            List<MetadataType> metadataList = readMetadataList(this.productDirectory);

            Dimension defaultProductSize = computeMaximumProductSize(metadataList);
            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductSize.width, defaultProductSize.height, subsetDef);

            MetadataType firstMetadata = findFirstMetadataItem(metadataList);
            if (firstMetadata == null) {
                throw new IllegalStateException("The selected product is not readable by this reader. Please use the appropriate filter");
            }

            String productName = (StringUtils.isNullOrEmpty(firstMetadata.getProductName())) ?  getGenericProductName() : firstMetadata.getProductName();
            Product product = new Product(productName, getProductType(), productBounds.width, productBounds.height, this);
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            String groupPattern = computeGroupPattern(metadataList.size());
            if (!StringUtils.isNullOrEmpty(groupPattern)) {
                product.setAutoGrouping(groupPattern);
            }
            ProductData.UTC centerTime = firstMetadata.getCenterTime();
            if (centerTime == null) {
                product.setStartTime(firstMetadata.getProductStartTime());
                product.setEndTime(firstMetadata.getProductEndTime());
            } else {
                product.setStartTime(centerTime);
                product.setEndTime(centerTime);
            }
            TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(firstMetadata, metadataList);
            if (productGeoCoding != null) {
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
            }

            List<String> rasterFileNames = getRasterFileNames(metadataList, this.productDirectory);
            if (rasterFileNames.size() < metadataList.size()) {
                throw new ArrayIndexOutOfBoundsException("Invalid size: rasterMetadataList=" + metadataList.size() + ", rasterFileNames=" + rasterFileNames.size());
            }
            Path metadataParentPath = this.productDirectory.getBaseFile().toPath();
            this.bandImageReaders = new ArrayList<>(metadataList.size());
            for (int i = 0; i < metadataList.size(); i++) {
                MetadataType currentMetadata = metadataList.get(i);
                GeoTiffImageReader geoTiffImageReader = GeoTiffImageReader.buildGeoTiffImageReader(metadataParentPath, rasterFileNames.get(i));
                this.bandImageReaders.add(geoTiffImageReader);

                Dimension defaultBandSize = geoTiffImageReader.validateSize(currentMetadata.getRasterWidth(), currentMetadata.getRasterHeight());
                Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductSize.width, defaultProductSize.height, defaultBandSize.width, defaultBandSize.height);
                GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
                Product getTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);

                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(currentMetadata.getRootElement());
                    if (getTiffProduct.getMetadataRoot() != null) {
                        XmlMetadata.CopyChildElements(getTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                    }
                }
                if (i == 0) {
                    if (productGeoCoding == null) {
                        getTiffProduct.transferGeoCodingTo(product, null);
                    }
                    if (getTiffProduct.getPreferredTileSize() != null) {
                        product.setPreferredTileSize(getTiffProduct.getPreferredTileSize());
                    }
                }

                // add bands
                String[] bandNames = getBandNames(currentMetadata);
                if (bandNames.length != getTiffProduct.getNumBands()) {
                    throw new IllegalStateException("Invalid size: metadata band count="+bandNames.length+", geo tiff product band count=" + getTiffProduct.getNumBands()+".");
                }
                String bandPrefix = computeBandPrefix(metadataList.size(), i);
                for (int k = 0; k < getTiffProduct.getNumBands(); k++) {
                    String bandName = bandPrefix + bandNames[k];
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band geoTiffBand = getTiffProduct.getBandAt(k);
                        geoTiffBand.setName(bandName);
                        product.addBand(geoTiffBand);
                    }
                }

                // remove the bands from the geo tif product
                getTiffProduct.getBandGroup().removeAll();
            }

            // add masks
            List<Mask> masks = buildMasks(product.getSceneRasterWidth(), product.getSceneRasterHeight(), firstMetadata, subsetDef);
            if (masks != null) {
                ProductNodeGroup<Mask> maskGroup = product.getMaskGroup();
                for (int j = 0; j < masks.size(); j++) {
                    maskGroup.add(masks.get(j));
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

    private static String computeGroupPattern(int metadataCount) {
        String groupPattern = "";
        if (metadataCount > 1) {
            for (int i = 0; i < metadataCount; i++) {
                if (i > 0) {
                    groupPattern += ":";
                }
                groupPattern += "scene_" + String.valueOf(i);
            }
        }
        return groupPattern;
    }

    public static String computeBandPrefix(int metadataCount, int bandIndex) {
        return (metadataCount > 1) ? ("scene_" + String.valueOf(bandIndex) + "_") : "";
    }

    private static <MetadataType extends XmlMetadata> List<String> getRasterFileNames(List<MetadataType> metadataList, VirtualDirEx productDirectory) {
        List<String> rasterFileNames = new ArrayList<>();
        if (metadataList != null) {
            for (MetadataType metadataComponent : metadataList) {
                String[] partialList = metadataComponent.getRasterFileNames();
                if (partialList != null) {
                    rasterFileNames.addAll(Arrays.asList(partialList));
                }
            }
        }
        if (rasterFileNames.size() == 0) {
            try {
                String[] allTiffFiles = productDirectory.findAll(".tif");
                if (allTiffFiles != null) {
                    rasterFileNames.addAll(Arrays.asList(allTiffFiles));
                }
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
        return rasterFileNames;
    }

    public static <MetadataType extends XmlMetadata> Dimension computeMaximumProductSize(List<MetadataType> metadata) {
        MetadataType item = metadata.get(0);
        int width = item.getRasterWidth();
        int height = item.getRasterHeight();
        for (int i=1; i<metadata.size(); i++) {
            item = metadata.get(i);
            if (width < item.getRasterWidth()) {
                width = item.getRasterWidth();
            }
            if (height < item.getRasterHeight()) {
                height = item.getRasterHeight();
            }
        }
        if (width <= 0) {
            throw new IllegalStateException("The product width " + width + " is invalid.");
        }
        if (height <= 0) {
            throw new IllegalStateException("The product height " + height + " is invalid.");
        }
        return new Dimension(width, height);
    }

    protected static <MetadataType extends XmlMetadata> List<MetadataType> readMetadata(VirtualDirEx productDirectory, String[] metadataFiles, Class<MetadataType> classType)
                                                                                        throws IOException, InstantiationException, ParserConfigurationException, SAXException {

        List<MetadataType> metadata = new ArrayList<>(metadataFiles.length);
        for (String file : metadataFiles) {
            try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(file)) {
                MetadataType metaDataItem = (MetadataType) XmlMetadataParserFactory.getParser(classType).parse(filePathInputStream);
                Path filePath = filePathInputStream.getPath();
                metaDataItem.setPath(filePath);
                metaDataItem.setFileName(filePath.getFileName().toString());
                metadata.add(metaDataItem);
            }
        }
        if (metadata.size() == 0) {
            throw new IllegalStateException("No metadata files.");
        }
        return metadata;
    }

    protected static<MetadataType extends XmlMetadata> MetadataType findFirstMetadataItem(List<MetadataType> metadataList, String metadataProfilePrefix) {
        for (int i=0; i<metadataList.size(); i++) {
            MetadataType rapidEyeMetadata = metadataList.get(i);
            String metadataProfile = rapidEyeMetadata.getMetadataProfile();
            if (metadataProfile != null && metadataProfile.startsWith(metadataProfilePrefix)) {
                return rapidEyeMetadata;
            }
        }
        throw new IllegalArgumentException("The metadata list is invalid.");
    }
}
