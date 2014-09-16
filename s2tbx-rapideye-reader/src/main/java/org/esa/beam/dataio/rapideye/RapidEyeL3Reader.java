package org.esa.beam.dataio.rapideye;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.FileImageInputStreamSpi;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.TreeNode;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Reader for RapidEye L3 (GeoTIFF) products.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL3Reader extends RapidEyeReader {

    private ImageInputStreamSpi channelImageInputStreamSpi;
    private final Map<Band, Band> bandMap;

    public RapidEyeL3Reader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        bandMap = new HashMap<Band, Band>();
        registerSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = RapidEyeReader.getInput(getInput());
//        String dirName = productDirectory.getBasePath().substring(productDirectory.getBasePath().lastIndexOf(File.separator) + 1);
        File selection = RapidEyeReader.getFileInput(getInput());
        String metadataFileName;
        File metadataFile;
        if (selection.isFile() && selection.getName().toLowerCase().endsWith(RapidEyeConstants.METADATA_FILE_SUFFIX)) {
            //metadataFileName = selection.getAbsolutePath();
            metadataFile = selection;
        } else {
            metadataFileName = productDirectory.findFirst(RapidEyeConstants.METADATA_FILE_SUFFIX);
            metadataFile = productDirectory.getFile(metadataFileName);
        }
        if (metadataFile.exists()) {
            logger.info("Reading product metadata");
            metadata = XmlMetadata.create(RapidEyeMetadata.class, metadataFile);
            if (metadata == null) {
                logger.warning(String.format("Error while reading metadata file %s", metadataFile.getName()));
            } else {
                metadata.setFileName(metadataFile.getName());
            }
        } else {
            logger.info("No metadata file found");
        }
        if (metadata != null) {
            String metadataProfile = metadata.getMetadataProfile();
            if (metadataProfile == null || !metadataProfile.startsWith(RapidEyeConstants.PROFILE_L3)) {
                IOException ex = new IOException("The selected product is not a RapidEye L3 product. Please use the appropriate filter");
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                throw ex;
            }
            if (metadata.getRasterWidth() > 0 && metadata.getRasterHeight() > 0) {
                createProduct(metadata.getRasterWidth(), metadata.getRasterHeight(), metadataProfile);
            }
            addBands(product, metadata);
            readMasks(productDirectory);

            product.setModified(false);
        } else {

            product.setModified(false);
        }
        return  product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        Band tiffBand = bandMap.get(destBand);
        ProductReader reader = tiffBand.getProductReader();
        if (reader == null) {
            logger.severe("No reader found for band data");
        } else {
            logger.info("Band:" + tiffBand.getName() + ",destOffsetX=" + destOffsetX + ",destOffsetY=" + destOffsetY + ",destWidth=" + destWidth + ",destHeight=" + destHeight);
            reader.readBandRasterData(tiffBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
        }
    }

    private Product createProduct(int width, int height, String metadataProfile) {
        product = new Product(metadata.getProductName() != null ? metadata.getProductName() : RapidEyeConstants.PRODUCT_GENERIC_NAME,
                                RapidEyeConstants.L3_FORMAT_NAMES[0],
                                width, height);
        product.getMetadataRoot().addElement(metadata.getRootElement());
        product.setStartTime(metadata.getProductStartTime());
        product.setEndTime(metadata.getProductEndTime());
        product.setProductType(metadataProfile);
        return product;
    }

    private void addBands(Product product, RapidEyeMetadata metadataFile) {
        try {
            File rasterFile = productDirectory.getFile(metadataFile.getRasterFileNames(false)[0]);
            GeoTiffProductReader reader = new GeoTiffProductReader(getReaderPlugIn());
            Product tiffProduct = reader.readProductNodes(rasterFile, null);
            if (tiffProduct != null) {
                if (product == null) {
                    product = createProduct(tiffProduct.getSceneRasterWidth(), tiffProduct.getSceneRasterHeight(), metadata.getMetadataProfile());
                }
                MetadataElement tiffMetadata = tiffProduct.getMetadataRoot();
                if (tiffMetadata != null) {
                    RapidEyeMetadata.CopyChildElements(tiffMetadata, product.getMetadataRoot());
                }
                tiffProduct.transferGeoCodingTo(product, null);
                Dimension preferredTileSize = tiffProduct.getPreferredTileSize();
                if (preferredTileSize == null)
                    preferredTileSize = getPreferredTileSize();
                product.setPreferredTileSize(preferredTileSize);
                int numBands = tiffProduct.getNumBands();
                for (int idx = 0; idx < numBands; idx++) {
                    Band srcBand = tiffProduct.getBandAt(idx);
                    Band targetBand = product.addBand(RapidEyeConstants.BAND_NAMES[idx], srcBand.getDataType());
                    targetBand.setNoDataValue(srcBand.getNoDataValue());
                    targetBand.setNoDataValueUsed(srcBand.isNoDataValueUsed());
                    targetBand.setSpectralWavelength(srcBand.getSpectralWavelength() > 0 ? srcBand.getSpectralWavelength() : RapidEyeConstants.WAVELENGTHS[idx]);
                    targetBand.setSpectralBandwidth(srcBand.getSpectralBandwidth() > 0 ? srcBand.getSpectralBandwidth() : RapidEyeConstants.BANDWIDTHS[idx]);
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
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (channelImageInputStreamSpi != null) {
            IIORegistry.getDefaultInstance().deregisterServiceProvider(channelImageInputStreamSpi);
        }
        super.close();
    }

    private void registerSpi() {
        // We will register a new Spi for creating NIO-based ImageInputStreams.
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
            channelImageInputStreamSpi = new FileImageInputStreamSpi();
            defaultInstance.registerServiceProvider(channelImageInputStreamSpi);
            if (toUnorder != null) {
                // Make the custom Spi to be the first one to be used.
                defaultInstance.setOrdering(ImageInputStreamSpi.class, channelImageInputStreamSpi, toUnorder);
            }
        }
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isThisZipFile()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            String metaFileName = metadata.getFileName();
                try{
                    addProductComponentIfNotPresent(metaFileName, productDirectory.getFile(metaFileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", metaFileName));
                }
            String[] nitfFiles = metadata.getRasterFileNames(false);
            for(String fileName : nitfFiles){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String maskFileName = metadata.getMaskFileName();
            if (maskFileName != null) {
                try{
                    addProductComponentIfNotPresent(maskFileName, productDirectory.getFile(maskFileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", maskFileName));
                }
            }
            return result;
        }
    }
}
