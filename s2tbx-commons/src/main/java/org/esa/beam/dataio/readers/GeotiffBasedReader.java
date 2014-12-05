package org.esa.beam.dataio.readers;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.FileImageInputStreamSpi;
import org.esa.beam.dataio.VirtualDirEx;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.TreeNode;
import org.esa.beam.util.logging.BeamLogManager;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all GeoTIFF-based readers for S2TBX.
 * This class has been created from the need of gathering all common code of several similar readers into a single place.
 */
public abstract class GeotiffBasedReader<M extends XmlMetadata> extends AbstractProductReader {

    private final Class<M> metadataClass;
    protected List<M> metadata;
    protected Product product;
    protected final Logger logger;
    protected ImageInputStreamSpi imageInputStreamSpi;
    protected VirtualDirEx productDirectory;
    protected final Map<Band, Band> bandMap;
    protected List<String> rasterFileNames;

    protected GeotiffBasedReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = BeamLogManager.getSystemLogger();
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof  ParameterizedType) {
            this.metadataClass = (Class<M>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else if ((superclass = getClass().getSuperclass().getGenericSuperclass()) instanceof ParameterizedType) {
            this.metadataClass = (Class<M>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else {
            throw new ClassCastException("Cannot find parameterized type");
        }
        registerMetadataParser();
        registerSpi();
        bandMap = new HashMap<>();
        metadata = new ArrayList<>();
    }

    protected abstract String getMetadataExtension();

    protected abstract String getMetadataProfile();

    protected abstract String getProductGenericName();

    protected abstract String[] getBandNames();

    @Override
    public void close() throws IOException {
        if (productDirectory != null) {
            productDirectory.close();
        }
        if (imageInputStreamSpi != null) {
            IIORegistry.getDefaultInstance().deregisterServiceProvider(imageInputStreamSpi);
        }
        super.close();
    }

    protected void registerMetadataParser() {
        XmlMetadataParserFactory.registerParser(this.metadataClass, new XmlMetadataParser<M>(this.metadataClass));
    }

    protected void registerSpi() {
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
            imageInputStreamSpi = new FileImageInputStreamSpi();
            defaultInstance.registerServiceProvider(imageInputStreamSpi);
            if (toUnorder != null) {
                // Make the custom Spi to be the first one to be used.
                defaultInstance.setOrdering(ImageInputStreamSpi.class, imageInputStreamSpi, toUnorder);
            }
        }
    }

    protected File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
    }

    protected VirtualDirEx getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);
        if (inputFile.isFile() && !VirtualDirEx.isPackedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException(String.format("Unable to retrieve parent to file %s.", absoluteFile.getAbsolutePath()));
            }
        }
        return VirtualDirEx.create(inputFile);
    }

    protected Dimension getPreferredTileSize() {
        Dimension tileSize = null;
        if (product != null) {
            tileSize = product.getPreferredTileSize();
            if (tileSize == null) {
                Dimension suggestedTileSize = ImageManager.getPreferredTileSize(product);
                tileSize = new Dimension((int)suggestedTileSize.getWidth(), (int)suggestedTileSize.getHeight());
            }
        }
        return tileSize;
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = getInput(super.getInput());
        //File selection = getFileInput(super.getInput());
        String[] metadataFiles = productDirectory.findAll(getMetadataExtension());
        if (metadataFiles != null) {
            logger.info("Reading product metadata");
            for (String file : metadataFiles) {
                try {
                    File metadataFile = productDirectory.getFile(file);
                    M mData = XmlMetadata.create(metadataClass, metadataFile);
                    mData.setFileName(metadataFile.getName());
                    metadata.add(mData);
                } catch (Exception mex) {
                    logger.warning(String.format("Error while reading metadata file %s", file));
                }
            }
        } else {
            logger.info("No metadata file found");
        }
        if (metadata != null && metadata.size() > 0) {
            M firstMetadata = metadata.get(0);
            String metadataProfile = firstMetadata.getMetadataProfile();
            if (metadataProfile == null || !metadataProfile.startsWith(getMetadataProfile())) {
                IOException ex = new IOException("The selected product is not readable by this reader. Please use the appropriate filter");
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                throw ex;
            }
            if (firstMetadata.getRasterWidth() > 0 && firstMetadata.getRasterHeight() > 0) {
                createProduct(firstMetadata.getRasterWidth(), firstMetadata.getRasterHeight(), firstMetadata);
            }
//            if (metadata.size() == 1) {
//                addBands(product, firstMetadata);
//            } else {
//                String groupText = "";
                for (int i = 0; i < metadata.size(); i++) {
                    M currentMetadata = metadata.get(i);
                    addBands(product, currentMetadata, i);
                    //groupText += currentMetadata.getProductName() + ":";
                }
                //groupText = groupText.substring(0, groupText.length() - 1);
                //product.setAutoGrouping(groupText);
//            }
            addMetadataMasks(product, firstMetadata);
            readAdditionalMasks(productDirectory);

            product.setModified(false);
        } else {
            product.setModified(false);
        }
        return  product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        Band sourceBand = bandMap.get(destBand);
        GeoTiffReaderEx reader = (GeoTiffReaderEx)sourceBand.getProductReader();
        if (reader == null) {
            logger.severe("No reader found for band data");
        } else {
            reader.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, sourceBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }

    protected Product createProduct(int width, int height, M metadataFile) {
        product = new Product((metadataFile != null && metadataFile.getProductName() != null) ? metadataFile.getProductName() : getProductGenericName(),
                              getReaderPlugIn().getFormatNames()[0],
                              width, height);
        product.getMetadataRoot().addElement(metadataFile.getRootElement());
        ProductData.UTC centerTime = metadataFile.getCenterTime();
        if (centerTime != null) {
            product.setStartTime(centerTime);
            product.setEndTime(centerTime);
        } else {
            product.setStartTime(metadataFile.getProductStartTime());
            product.setEndTime(metadataFile.getProductEndTime());
        }
        product.setProductType(metadataFile.getMetadataProfile());
        product.setDescription(metadataFile.getProductDescription());
        return product;
    }

    protected void addBands(Product product, M componentMetadata, int componentIndex) {
        try {
            File rasterFile = null;
            rasterFileNames = getRasterFileNames();
            if (componentIndex >= rasterFileNames.size()) {
                throw new ArrayIndexOutOfBoundsException(String.format("Invalid component index: %d", componentIndex));
            }
            rasterFile = productDirectory.getFile(rasterFileNames.get(componentIndex));

            GeoTiffProductReader reader = new GeoTiffReaderEx(getReaderPlugIn());
            Product tiffProduct = reader.readProductNodes(rasterFile, null);
            if (tiffProduct != null) {
                if (product == null) {
                    product = createProduct(tiffProduct.getSceneRasterWidth(), tiffProduct.getSceneRasterHeight(), componentMetadata);
                }
                MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                if (tiffMetadata != null) {
                    XmlMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                }
                tiffProduct.transferGeoCodingTo(product, null);
                Dimension preferredTileSize = tiffProduct.getPreferredTileSize();
                if (preferredTileSize == null)
                    preferredTileSize = getPreferredTileSize();
                product.setPreferredTileSize(preferredTileSize);
                int numBands = tiffProduct.getNumBands();
                String bandPrefix = "";
                if (rasterFileNames.size() > 1) {
                    bandPrefix = "scene_" + String.valueOf(componentIndex) + "_";
                    String groupPattern = "";
                    for (int idx = 0; idx < rasterFileNames.size(); idx++) {
                        groupPattern += "scene_" + String.valueOf(idx) + ":";
                    }
                    groupPattern = groupPattern.substring(0, groupPattern.length() - 1);
                    product.setAutoGrouping(groupPattern);
                }
                for (int idx = 0; idx < numBands; idx++) {
                    Band srcBand = tiffProduct.getBandAt(idx);
                    String bandName = bandPrefix + getBandNames()[idx];
                    Band targetBand = product.addBand(bandName, srcBand.getDataType());
                    targetBand.setNoDataValue(srcBand.getNoDataValue());
                    targetBand.setNoDataValueUsed(srcBand.isNoDataValueUsed());
                    targetBand.setSpectralWavelength(srcBand.getSpectralWavelength());
                    targetBand.setSpectralBandwidth(srcBand.getSpectralBandwidth());
                    targetBand.setScalingFactor(srcBand.getScalingFactor());
                    targetBand.setScalingOffset(srcBand.getScalingOffset());
                    targetBand.setSolarFlux(srcBand.getSolarFlux());
                    targetBand.setUnit(srcBand.getUnit());
                    targetBand.setSampleCoding(srcBand.getSampleCoding());
                    targetBand.setImageInfo(srcBand.getImageInfo());
                    targetBand.setSpectralBandIndex(srcBand.getSpectralBandIndex());
                    targetBand.setDescription(srcBand.getDescription());
                    bandMap.put(targetBand, srcBand);
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    protected void addMetadataMasks(Product product, M metadata) {
    }
    protected void readAdditionalMasks(VirtualDirEx directory) {

    }

    protected void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<File>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    protected List<String> getRasterFileNames() {
        if (rasterFileNames == null) {
            rasterFileNames = new ArrayList<>();
            if (metadata != null) {
                for (M metadataComponent : metadata) {
                    String[] partialList = metadataComponent.getRasterFileNames();
                    if (partialList != null) {
                        for (String item : partialList) {
                            rasterFileNames.add(item);
                        }
                    }
                }
            }
            if (rasterFileNames.size() == 0) {
                try {
                    String[] allTiffFiles = productDirectory.findAll(".tif");
                    if (allTiffFiles != null) {
                        for (String item : allTiffFiles) {
                            rasterFileNames.add(item);
                        }
                    }
                } catch (IOException e) {
                }
            }
        }
        return rasterFileNames;
    }

    protected class GeoTiffReaderEx extends GeoTiffProductReader {

        public GeoTiffReaderEx(ProductReaderPlugIn readerPlugIn) {
            super(readerPlugIn);
        }

        @Override
        protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
            super.readBandRasterDataImpl(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }
}

