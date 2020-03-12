///*
// * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
// * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
// *
// * This program is free software; you can redistribute it and/or modify it
// * under the terms of the GNU General Public License as published by the Free
// * Software Foundation; either version 3 of the License, or (at your option)
// * any later version.
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// * more details.
// *
// * You should have received a copy of the GNU General Public License along
// * with this program; if not, see http://www.gnu.org/licenses/
// */
//
//package org.esa.s2tbx.dataio.readers;
//
//import com.bc.ceres.core.ProgressMonitor;
//import org.esa.s2tbx.commons.FilePathInputStream;
//import org.esa.s2tbx.dataio.ColorPaletteBand;
//import org.esa.s2tbx.dataio.VirtualDirEx;
//import org.esa.snap.core.metadata.XmlMetadata;
//import org.esa.snap.core.metadata.XmlMetadataParser;
//import org.esa.snap.core.metadata.XmlMetadataParserFactory;
//import org.esa.snap.core.dataio.AbstractProductReader;
//import org.esa.snap.core.dataio.DecodeQualification;
//import org.esa.snap.core.dataio.ProductReaderPlugIn;
//import org.esa.snap.core.datamodel.Band;
//import org.esa.snap.core.datamodel.MetadataElement;
//import org.esa.snap.core.datamodel.Product;
//import org.esa.snap.core.datamodel.ProductData;
//import org.esa.snap.core.image.ImageManager;
//import org.esa.snap.core.util.StringUtils;
//import org.esa.snap.core.util.TreeNode;
//import org.esa.snap.dataio.FileImageInputStreamSpi;
//import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
//import org.esa.snap.utils.CollectionHelper;
//import org.xml.sax.SAXException;
//
//import javax.imageio.spi.IIORegistry;
//import javax.imageio.spi.ImageInputStreamSpi;
//import javax.xml.parsers.ParserConfigurationException;
//import java.awt.Dimension;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Base class for all GeoTIFF-based readers for S2TBX.
// * This class has been created from the need of gathering all common code of several similar readers into a single place.
// */
////TODO Jean remove the class
//@Deprecated
//public abstract class GeoTiffBasedReader<M extends XmlMetadata> extends AbstractProductReader {
//
//    protected static final Logger logger = Logger.getLogger(GeoTiffBasedReader.class.getName());
//
//    private final Class<M> metadataClass;
//    protected final Map<Band, Band> bandMap;
//    protected final Path colorPaletteFilePath;
//
//    protected List<M> metadata;
//    protected Product product;
//    protected ImageInputStreamSpi imageInputStreamSpi;
//    protected VirtualDirEx productDirectory;
//    private List<String> rasterFileNames;
//
//    protected GeoTiffBasedReader(ProductReaderPlugIn readerPlugIn, Path colorPaletteFilePath) {
//        super(readerPlugIn);
//
//        this.colorPaletteFilePath = colorPaletteFilePath;
//        this.metadataClass = getTypeArgument();
//        registerMetadataParser();
//        registerSpi();
//        this.bandMap = new HashMap<>();
//        this.metadata = new ArrayList<>();
//    }
//
//    /**
//     * Gets the metadata file extension.
//     *
//     * @return the metadata file extension
//     */
//    protected abstract String getMetadataExtension();
//
//    /**
//     * Gets the profile of the metadata.
//     *
//     * @return the profile of the metadata.
//     */
//    protected abstract String getMetadataProfile();
//
//    /**
//     * Gets a generic product name, in case none is found in metadata
//     *
//     * @return  the generic product name
//     */
//    protected abstract String getProductGenericName();
//
//    protected abstract String getMetadataFileSuffix();
//
//    /**
//     * Gets the names of the bands.
//     * It is the responsibility of the extender to provide the band names
//     * either from metadata, or from predefined constants.
//     *
//     * @return  An array with the band names.
//     */
//    protected abstract String[] getBandNames();
//
//    @Override
//    public void close() throws IOException {
//        if (this.productDirectory != null) {
//            this.productDirectory.close();
//        }
//        if (this.imageInputStreamSpi != null) {
//            IIORegistry.getDefaultInstance().deregisterServiceProvider(this.imageInputStreamSpi);
//        }
//
//        super.close();
//    }
//
//    protected Class<M> getTypeArgument() {
//        Class<M> arg;
//        Type superclass = getClass().getGenericSuperclass();
//        Type type;
//        if (superclass instanceof  ParameterizedType) {
//            type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
//            arg = (Class<M>) type;
//        } else if ((superclass = getClass().getSuperclass().getGenericSuperclass()) instanceof ParameterizedType) {
//            type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
//            arg = (Class<M>) type;
//        } else {
//            throw new ClassCastException("Cannot find parameterized type");
//        }
//        return arg;
//    }
//
//    /**
//     * Registers a customized XML parser for the metadata type of this reader, in the XML metadata parser factory.
//     */
//    protected void registerMetadataParser() {
//        XmlMetadataParserFactory.registerParser(this.metadataClass, new XmlMetadataParser<>(this.metadataClass));
//    }
//
//    /**
//     * Registers a file image input strwM SPI for image input stream, if none is yet registered.
//     */
//    protected void registerSpi() {
//        IIORegistry defaultInstance = IIORegistry.getDefaultInstance();
//        if (defaultInstance.getServiceProviderByClass(FileImageInputStreamSpi.class) == null) {
//            // register only if not already registered
//            ImageInputStreamSpi toUnorder = null;
//            Iterator<ImageInputStreamSpi> serviceProviders = defaultInstance.getServiceProviders(ImageInputStreamSpi.class, true);
//            while (serviceProviders.hasNext()) {
//                ImageInputStreamSpi current = serviceProviders.next();
//                if (current.getInputClass() == File.class) {
//                    toUnorder = current;
//                    break;
//                }
//            }
//            this.imageInputStreamSpi = new FileImageInputStreamSpi();
//            defaultInstance.registerServiceProvider(this.imageInputStreamSpi);
//            if (toUnorder != null) {
//                // Make the custom Spi to be the first one to be used.
//                defaultInstance.setOrdering(ImageInputStreamSpi.class, this.imageInputStreamSpi, toUnorder);
//            }
//        }
//    }
//
//    /**
//     * Returns a wrapping VirtualDirEx object over the input product.
//     *
//     * @return  An instance of VirtualDirEx
//     * @throws IOException
//     */
//    protected VirtualDirEx buildProductDirectory(Path inputPath) throws IOException {
//        return VirtualDirEx.build(inputPath, false, true);
//    }
//
//    /**
//     * Returns the preferred tile size, either from product (if defined) or from the underlying ImageManager.
//     *
//     * @return  The preferred tile dimensions.
//     */
//    private static Dimension getPreferredTileSize(Product product) {
//        Dimension tileSize = null;
//        if (product != null) {
//            tileSize = product.getPreferredTileSize();
//            if (tileSize == null) {
//                Dimension suggestedTileSize = ImageManager.getPreferredTileSize(product);
//                tileSize = new Dimension((int)suggestedTileSize.getWidth(), (int)suggestedTileSize.getHeight());
//            }
//        }
//        return tileSize;
//    }
//
//    protected String[] getMetadataFiles() throws IOException {
//        return this.productDirectory.findAll(getMetadataExtension());
//    }
//
//    @Override
//    protected Product readProductNodesImpl() throws IOException {
//        if (getReaderPlugIn().getDecodeQualification(super.getInput()) == DecodeQualification.UNABLE) {
//            throw new IOException("The selected product cannot be read with the current reader.");
//        }
//
//        Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
//
//        this.productDirectory = buildProductDirectory(inputPath);
//
//        String[] metadataFiles = getMetadataFiles();
//        if (metadataFiles == null) {
//            logger.info("No metadata file found");
//        } else {
//            logger.info("Reading product metadata");
//            for (String file : metadataFiles) {
//                try {
//                    try (FilePathInputStream filePathInputStream = this.productDirectory.getInputStream(file)) {
//                        M metaDataItem;
//                        try {
//                            metaDataItem = (M) XmlMetadataParserFactory.getParser(this.metadataClass).parse(filePathInputStream);
//                        } catch (InstantiationException | ParserConfigurationException | SAXException e) {
//                            throw new IllegalStateException(e);
//                        }
//                        Path filePath = filePathInputStream.getPath();
//                        metaDataItem.setPath(filePath);
//                        metaDataItem.setFileName(filePath.getFileName().toString());
//                        this.metadata.add(metaDataItem);
//                    }
//                } catch (IOException mex) {
//                    logger.warning(String.format("Error while reading metadata file %s", file));
//                }
//            }
//        }
//
//        if (this.metadata != null && this.metadata.size() > 0) {
//            List<M> rasterMetadataList = CollectionHelper.where(this.metadata, m -> m.getFileName().endsWith(getMetadataFileSuffix()));
//            M firstMetadata = getFirstMetadataItem(rasterMetadataList);
//            if (firstMetadata == null) {
//                IOException ex = new IOException("The selected product is not readable by this reader. Please use the appropriate filter");
//                logger.log(Level.SEVERE, ex.getMessage(), ex);
//                throw ex;
//            }
//
//            if (firstMetadata.getRasterWidth() > 0 && firstMetadata.getRasterHeight() > 0) {
//                createProduct(firstMetadata.getRasterWidth(), firstMetadata.getRasterHeight(), firstMetadata);
//            }
//            for (int i = 0; i < rasterMetadataList.size(); i++) {
//                M currentMetadata = rasterMetadataList.get(i);
//                addBands(currentMetadata, i);
//            }
//            addMetadataMasks(firstMetadata);
//            readAdditionalMasks(this.productDirectory);
//
//            this.product.setModified(false);
//        } else {
//            if (this.product != null) {
//                this.product.setModified(false);
//            }
//        }
//        return this.product;
//    }
//
//    @Override
//    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY,
//                                          Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
//                                          throws IOException {
//
//        Band sourceBand = this.bandMap.get(destBand);
//        GeoTiffReaderEx reader = (GeoTiffReaderEx)sourceBand.getProductReader();
//        if (reader == null) {
//            logger.severe("No reader found for band data");
//        } else {
//            reader.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, sourceBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
//        }
//    }
//
//    /**
//     * Creates and initializes the product to be manipulated by this reader.
//     *
//     * @param width the width (in pixels) of the product
//     * @param height    the height (in pixels) of the product
//     * @param metadataFile  The (primary) metadata file
//     * @return  An instance of the product
//     */
//    protected void createProduct(int width, int height, M metadataFile) {
//        String name = (metadataFile != null && metadataFile.getProductName() != null) ? metadataFile.getProductName() : getProductGenericName();
//        String type = getReaderPlugIn().getFormatNames()[0];
//        this.product = new Product(name, type, width, height);
//        File fileLocation = null;
//        try {
//            // in case of zip products, getTempDir returns the temporary location of the uncompressed product
//            fileLocation = this.productDirectory.getTempDir();
//        } catch (IOException e) {
//            logger.warning(e.getMessage());
//        }
//        if (fileLocation == null) {
//            fileLocation = this.productDirectory.getBaseFile();
//        }
//        this.product.setFileLocation(fileLocation);
//        if (metadataFile != null) {
//            this.product.getMetadataRoot().addElement(metadataFile.getRootElement());
//            ProductData.UTC centerTime = metadataFile.getCenterTime();
//            if (centerTime != null) {
//                this.product.setStartTime(centerTime);
//                this.product.setEndTime(centerTime);
//            } else {
//                this.product.setStartTime(metadataFile.getProductStartTime());
//                this.product.setEndTime(metadataFile.getProductEndTime());
//            }
//            this.product.setProductType(metadataFile.getMetadataProfile());
//            this.product.setDescription(metadataFile.getProductDescription());
//        }
//    }
//
//    /**
//     * Reads the product bands from the product rasters, using the given component metadata (a product may have several components, and hence
//     * several metadata files).
//     *
//     * @param componentMetadata The metadata of the original product component
//     * @param componentIndex    The index of the current product component (0 if only one component)
//     */
//    protected void addBands(M componentMetadata, int componentIndex) {
//        try {
//            this.rasterFileNames = getRasterFileNames();
//            if (componentIndex >= this.rasterFileNames.size()) {
//                throw new ArrayIndexOutOfBoundsException(String.format("Invalid component index: %d", componentIndex));
//            }
//            String rasterFileName = this.rasterFileNames.get(componentIndex);
//            File rasterFile = this.productDirectory.getFile(rasterFileName);
//
//            GeoTiffProductReader reader = new GeoTiffReaderEx(getReaderPlugIn());
//            Product tiffProduct = reader.readProductNodes(rasterFile, null);
//            if (tiffProduct != null) {
//                if (this.product == null) {
//                    createProduct(tiffProduct.getSceneRasterWidth(), tiffProduct.getSceneRasterHeight(), componentMetadata);
//                }
//                MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
//                if (tiffMetadata != null) {
//                    XmlMetadata.CopyChildElements(tiffMetadata, this.product.getMetadataRoot());
//                }
//                tiffProduct.transferGeoCodingTo(this.product, null);
//                Dimension preferredTileSize = tiffProduct.getPreferredTileSize();
//                if (preferredTileSize == null) {
//                    preferredTileSize = getPreferredTileSize(this.product);
//                }
//                this.product.setPreferredTileSize(preferredTileSize);
//                int numBands = tiffProduct.getNumBands();
//                String bandPrefix = "";
//                if (this.rasterFileNames.size() > 1) {
//                    bandPrefix = "scene_" + String.valueOf(componentIndex) + "_";
//                    String groupPattern = computeGroupPattern();
//                    if (!StringUtils.isNullOrEmpty(groupPattern)) {
//                        this.product.setAutoGrouping(groupPattern);
//                    }
//                }
//                for (int idx = 0; idx < numBands; idx++) {
//                    Band srcBand = tiffProduct.getBandAt(idx);
//                    String bandName = bandPrefix + getBandNames()[idx];
//                    Band targetBand = new ColorPaletteBand(bandName, srcBand.getDataType(), this.product.getSceneRasterWidth(), this.product.getSceneRasterHeight(), this.colorPaletteFilePath);
//                    targetBand.setNoDataValue(srcBand.getNoDataValue());
//                    targetBand.setNoDataValueUsed(srcBand.isNoDataValueUsed());
//                    targetBand.setSpectralWavelength(srcBand.getSpectralWavelength());
//                    targetBand.setSpectralBandwidth(srcBand.getSpectralBandwidth());
//                    targetBand.setScalingFactor(srcBand.getScalingFactor());
//                    targetBand.setScalingOffset(srcBand.getScalingOffset());
//                    targetBand.setSolarFlux(srcBand.getSolarFlux());
//                    targetBand.setUnit(srcBand.getUnit());
//                    targetBand.setSampleCoding(srcBand.getSampleCoding());
//                    targetBand.setImageInfo(srcBand.getImageInfo());
//                    targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
//                    targetBand.setDescription(srcBand.getDescription());
//
//                    this.product.addBand(targetBand);
//                    this.bandMap.put(targetBand, srcBand);
//                }
//            }
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Reads from the given metadata object and adds product masks.
//     * The default (i.e. base) implementation does nothing, therefore this method should be overridden by subclasses.
//     * @param metadata  The metadata object from which masks are read.
//     */
//    protected void addMetadataMasks(M metadata) {
//    }
//
//    /**
//     * Reads masks not found in metadata from additional mask files (if any).
//     * The default (i.e. base) implementation does nothing, therefore this method should be overridden by subclasses.
//     * @param directory The virtual directory of the product.
//     */
//    protected void readAdditionalMasks(VirtualDirEx directory) {
//
//    }
//
//    protected void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
//        TreeNode<File> resultComponent = null;
//        for (TreeNode node : currentComponents.getChildren()) {
//            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
//                //noinspection unchecked
//                resultComponent = node;
//                break;
//            }
//        }
//        if (resultComponent == null) {
//            resultComponent = new TreeNode<>(componentId, componentFile);
//            currentComponents.addChild(resultComponent);
//        }
//    }
//
//    /**
//     * Returns a list of raster file names for the product.
//     * @return  a list of raster file names
//     */
//    protected List<String> getRasterFileNames() {
//        if (rasterFileNames == null) {
//            rasterFileNames = new ArrayList<>();
//            if (metadata != null) {
//                for (M metadataComponent : metadata) {
//                    String[] partialList = metadataComponent.getRasterFileNames();
//                    if (partialList != null) {
//                        rasterFileNames.addAll(Arrays.asList(partialList));
//                    }
//                }
//            }
//            if (rasterFileNames.size() == 0) {
//                try {
//                    String[] allTiffFiles = productDirectory.findAll(".tif");
//                    if (allTiffFiles != null) {
//                        rasterFileNames.addAll(Arrays.asList(allTiffFiles));
//                    }
//                } catch (IOException e) {
//                    logger.warning(e.getMessage());
//                }
//            }
//        }
//        return rasterFileNames;
//    }
//
//    /**
//     * Computes the grouping of bands if the product has multiple raster files.
//     * @return  the grouping expression or an empty string if there is at most one raster file.
//     */
//    protected String computeGroupPattern() {
//        String groupPattern = "";
//        this.rasterFileNames = getRasterFileNames();
//        if (this.rasterFileNames.size() > 1) {
//            for (int idx = 0; idx < this.rasterFileNames.size(); idx++) {
//                groupPattern += "scene_" + String.valueOf(idx) + ":";
//            }
//        }
//        return groupPattern.substring(0, groupPattern.length() - 1);
//    }
//
//    @Override
//    public TreeNode<File> getProductComponents() {
//        TreeNode<File> result = super.getProductComponents();
//
//        if (this.productDirectory.isCompressed()) {
//            return result;
//        } else {
//            TreeNode<File>[] nodesClone = result.getChildren().clone();
//            for(TreeNode<File> node : nodesClone){
//                result.removeChild(node);
//            }
//            for(XmlMetadata metaFile: metadata){
//                TreeNode<File> productFile = new TreeNode<>(metaFile.getFileName());
//                result.addChild(productFile);
//            }
//            for(String inputFile: getRasterFileNames()){
//                TreeNode<File> productFile = new TreeNode<>(inputFile);
//                result.addChild(productFile);
//            }
//            return result;
//        }
//    }
//
//    private M getFirstMetadataItem(List<M> rasterMetadataList) {
//        if (rasterMetadataList != null && rasterMetadataList.size() > 0) {
//            M firstMetadata = rasterMetadataList.get(0);
//            if (firstMetadata != null) {
//                String metadataProfile = firstMetadata.getMetadataProfile();
//                if (metadataProfile != null && metadataProfile.startsWith(getMetadataProfile())) {
//                    return firstMetadata;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * We need this class in order to raise the visibility of readBandRasterDataImpl method,
//     * in order to be able to pass to the underlying GeoTiffProductReader the stepping parameters.
//     */
//    protected class GeoTiffReaderEx extends GeoTiffProductReader {
//
//        public GeoTiffReaderEx(ProductReaderPlugIn readerPlugIn) {
//            super(readerPlugIn);
//        }
//
//        @Override
//        protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
//            super.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
//        }
//    }
//}
//
