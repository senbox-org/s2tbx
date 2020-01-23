package org.esa.s2tbx.dataio.readers;

import com.bc.ceres.core.ProgressMonitor;
import org.apache.commons.lang.StringUtils;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.dataio.ImageRegistryUtils;
import org.esa.snap.dataio.geotiff.GeoTiffImageReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.xml.sax.SAXException;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
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

    protected abstract MetadataType findFirstMetadataItem(MetadataList<MetadataType> metadataList);

    protected abstract TiePointGeoCoding buildTiePointGridGeoCoding(MetadataType firstMetadata, MetadataList<MetadataType> metadataList, ProductSubsetDef productSubsetDef);

    protected abstract String getProductType();

    protected abstract String getGenericProductName();

    protected abstract String[] getBandNames(MetadataType metadata);

    protected abstract List<Mask> buildMasks(int productWith, int productHeight, MetadataType firstMetadata, ProductSubsetDef subsetDef);

    protected abstract MetadataList<MetadataType> readMetadataList(VirtualDirEx productDirectory) throws IOException, InstantiationException, ParserConfigurationException, SAXException;

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

            MetadataList<MetadataType> metadataList = readMetadataList(this.productDirectory);
            if (metadataList == null) {
                throw new NullPointerException("The metadata list is null.");
            }
            if (metadataList.getCount() == 0) {
                throw new IllegalStateException("The metadata list is empty.");
            }
            MetadataType firstMetadata = findFirstMetadataItem(metadataList);
            if (firstMetadata == null) {
                throw new IllegalStateException("The selected product is not readable by this reader. Please use the appropriate filter");
            }

            this.bandImageReaders = new ArrayList<>(metadataList.getCount());
            int defaultProductWidth = 0;
            int defaultProductHeight = 0;
            for (int i=0; i<metadataList.getCount(); i++) {
                boolean inputStreamSuccess = false;
                GeoTiffImageReader geoTiffImageReader;
                FilePathInputStream filePathInputStream = this.productDirectory.getInputStream(metadataList.getMetadataImageRelativePath(i));
                try {
                    geoTiffImageReader = new GeoTiffImageReader(filePathInputStream, null);
                    this.bandImageReaders.add(geoTiffImageReader);
                    inputStreamSuccess = true;
                } finally {
                    if (!inputStreamSuccess) {
                        filePathInputStream.close();
                    }
                }
                defaultProductWidth = Math.max(defaultProductWidth, geoTiffImageReader.getImageWidth());
                defaultProductHeight = Math.max(defaultProductHeight, geoTiffImageReader.getImageHeight());
            }
            if (defaultProductWidth <= 0) {
                throw new IllegalStateException("The product width " + defaultProductWidth + " is invalid.");
            }
            if (defaultProductHeight <= 0) {
                throw new IllegalStateException("The product height " + defaultProductHeight + " is invalid.");
            }

            String metadataProductName = firstMetadata.getProductName();
            String productName;
            if (StringUtils.isBlank(metadataProductName) || "N/A".equalsIgnoreCase(metadataProductName)) {
                productName = getGenericProductName();
            } else {
                productName = metadataProductName;
            }

            ProductSubsetDef subsetDef = getSubsetDef();
            Rectangle productBounds = ImageUtils.computeProductBounds(defaultProductWidth, defaultProductHeight, subsetDef);

            Product product = new Product(productName, getProductType(), productBounds.width, productBounds.height, this);
            product.setFileLocation(productPath.toFile());
            Dimension preferredTileSize = JAIUtils.computePreferredTileSize(product.getSceneRasterWidth(), product.getSceneRasterHeight(), 1);
            product.setPreferredTileSize(preferredTileSize);
            String groupPattern = computeGroupPattern(metadataList.getCount());
            if (!StringUtils.isBlank(groupPattern)) {
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
            product.setProductType(firstMetadata.getMetadataProfile());
            product.setDescription(firstMetadata.getProductDescription());

            TiePointGeoCoding productGeoCoding = buildTiePointGridGeoCoding(firstMetadata, metadataList, subsetDef);
            if (productGeoCoding != null) {
                product.addTiePointGrid(productGeoCoding.getLatGrid());
                product.addTiePointGrid(productGeoCoding.getLonGrid());
                product.setSceneGeoCoding(productGeoCoding);
            }

            for (int i = 0; i < this.bandImageReaders.size(); i++) {
                GeoTiffImageReader geoTiffImageReader = this.bandImageReaders.get(i);
                int bandCount = geoTiffImageReader.getSampleModel().getNumBands();
                MetadataType currentMetadata = metadataList.getMetadataAt(i);
                int defaultBandWidth = geoTiffImageReader.getImageWidth();
                int defaultBandHeight = geoTiffImageReader.getImageHeight();
                Rectangle bandBounds = ImageUtils.computeBandBoundsBasedOnPercent(productBounds, defaultProductWidth, defaultProductHeight, defaultBandWidth, defaultBandHeight);
                GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(getReaderPlugIn(), null);
                Product getTiffProduct = geoTiffProductReader.readProduct(geoTiffImageReader, null, bandBounds);
                if (bandCount != getTiffProduct.getNumBands()) {
                    throw new IllegalStateException("Different band count: geo tiff image band count=" + bandCount+", geo tif product band count="+getTiffProduct.getNumBands()+".");
                }
                if (subsetDef == null || !subsetDef.isIgnoreMetadata()) {
                    product.getMetadataRoot().addElement(currentMetadata.getRootElement());
                    if (getTiffProduct.getMetadataRoot() != null) {
                        XmlMetadata.CopyChildElements(getTiffProduct.getMetadataRoot(), product.getMetadataRoot());
                    }
                }
                if (i == 0) {
                    // the first image
                    if (productGeoCoding == null) {
                        getTiffProduct.transferGeoCodingTo(product, null);
                    }
                }

                // add bands
                String[] bandNames = getBandNames(currentMetadata);
                String bandPrefix = computeBandPrefix(metadataList.getCount(), i);
                for (int bandIndex= 0; bandIndex < getTiffProduct.getNumBands(); bandIndex++) {
                    String bandName = bandPrefix + ((bandIndex < bandNames.length) ? bandNames[bandIndex] : ("band_" + bandIndex));
                    if (subsetDef == null || subsetDef.isNodeAccepted(bandName)) {
                        Band geoTiffBand = getTiffProduct.getBandAt(bandIndex);
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

    public static <MetadataType extends XmlMetadata> RastersMetadata computeMaximumDefaultProductSize(MetadataList<MetadataType> metadataList, VirtualDirEx productDirectory)
                                                                     throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {

        int defaultProductWidth = 0;
        int defaultProductHeight = 0;
        RastersMetadata rastersMetadata = new RastersMetadata();
        for (int i = 0; i < metadataList.getCount(); i++) {
            try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(metadataList.getMetadataImageRelativePath(i))) {
                try (GeoTiffImageReader geoTiffImageReader = new GeoTiffImageReader(filePathInputStream, null)) {
                    defaultProductWidth = Math.max(defaultProductWidth, geoTiffImageReader.getImageWidth());
                    defaultProductHeight = Math.max(defaultProductHeight, geoTiffImageReader.getImageHeight());
                    int bandCount = geoTiffImageReader.getSampleModel().getNumBands();
                    rastersMetadata.setRasterBandCount(metadataList.getMetadataAt(i), bandCount);
                }
            }
        }
        if (defaultProductWidth <= 0) {
            throw new IllegalStateException("The product width " + defaultProductWidth + " is invalid.");
        }
        if (defaultProductHeight <= 0) {
            throw new IllegalStateException("The product height " + defaultProductHeight + " is invalid.");
        }
        rastersMetadata.setMaximumSize(defaultProductWidth, defaultProductHeight);
        return rastersMetadata;
    }

    protected static <MetadataType extends XmlMetadata> MetadataList<MetadataType> readMetadata(VirtualDirEx productDirectory, String metadataFileSuffix, Class<MetadataType> classType)
                                                throws IOException, InstantiationException, ParserConfigurationException, SAXException {

        String[] existingRelativeFilePaths = productDirectory.listAllFiles();
        MetadataList<MetadataType> metadataList = new MetadataList<>();
        for (String relativeFilePath : existingRelativeFilePaths) {
            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(relativeFilePath, metadataFileSuffix)) {
                MetadataType metaDataItem;
                try (FilePathInputStream filePathInputStream = productDirectory.getInputStream(relativeFilePath)) {
                    metaDataItem = (MetadataType) XmlMetadataParserFactory.getParser(classType).parse(filePathInputStream);
                    Path filePath = filePathInputStream.getPath();
                    metaDataItem.setPath(filePath);
                    metaDataItem.setFileName(filePath.getFileName().toString());
                }
                String existingImageRelativePath = null;
                String[] rasterFileNames = metaDataItem.getRasterFileNames();
                if (rasterFileNames != null && rasterFileNames.length > 0) {
                    // find the image file using the raster file names
                    for (int i=0; i<rasterFileNames.length && existingImageRelativePath == null; i++) {
                        for (int k=0; k<existingRelativeFilePaths.length && existingImageRelativePath == null; k++) {
                            existingImageRelativePath = findImageRelativePath(rasterFileNames[i], existingRelativeFilePaths);
                        }
                    }
                }
                if (existingImageRelativePath == null) {
                    // find the image file using the metadata file name
                    existingImageRelativePath = findImageRelativePath(relativeFilePath, existingRelativeFilePaths);
                }
                if (existingImageRelativePath == null) {
                    throw new IllegalStateException("There is no image file for metadata file '" + relativeFilePath + "'.");
                }
                metadataList.addMetadata(metaDataItem, existingImageRelativePath);
            }
        }
        return metadataList;
    }

    private static String findImageRelativePath(String rasterFileNameToFind, String[] existingRelativeFilePaths) {
        int lastPointIndex = rasterFileNameToFind.lastIndexOf('.');
        if (lastPointIndex <= 0) {
            throw new IllegalStateException("The raster file name to find '" + rasterFileNameToFind+"' has no extension.");
        }
        String rasterFileNameWithoutExtension = rasterFileNameToFind.substring(0, lastPointIndex);
        String existingImageRelativePath = null;
        for (int k=0; k<existingRelativeFilePaths.length && existingImageRelativePath == null; k++) {
            String existingRelativeFilePath = existingRelativeFilePaths[k];
            if (StringUtils.endsWithIgnoreCase(existingRelativeFilePath, ".tif") || StringUtils.endsWithIgnoreCase(existingRelativeFilePath, ".tiff")) {
                for (int index=rasterFileNameWithoutExtension.length()-1; index>=0 && existingImageRelativePath == null; index--) {
                    if (existingRelativeFilePath.regionMatches(true, 0, rasterFileNameWithoutExtension, 0, index)) {
                        existingImageRelativePath = existingRelativeFilePath;
                    }
                }
            }
        }
        return existingImageRelativePath;
    }
}
