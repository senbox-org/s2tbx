package org.esa.beam.dataio.rapideye;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.metadata.XmlMetadata;
import org.esa.beam.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.beam.dataio.rapideye.nitf.NITFMetadata;
import org.esa.beam.dataio.rapideye.nitf.NITFReaderWrapper;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.util.TreeNode;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Reader for RapidEye L1 (NITF) products.
 *
 * @author  Cosmin Cara
 */
public class RapidEyeL1Reader extends RapidEyeReader {

    private final Map<Band, NITFReaderWrapper> readerMap;

    public RapidEyeL1Reader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        readerMap = new HashMap<Band, NITFReaderWrapper>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = RapidEyeReader.getInput(getInput());
        //String dirName = productDirectory.getBasePath().substring(productDirectory.getBasePath().lastIndexOf(File.separator) + 1);
        String metadataFileName = productDirectory.findFirst(RapidEyeConstants.METADATA_FILE_SUFFIX);
        File metadataFile = productDirectory.getFile(metadataFileName);
        // First parse *_metadata.xml
        if (metadataFile.exists()) {
            logger.info("Reading product metadata");
            metadata = XmlMetadata.create(RapidEyeMetadata.class, metadataFile);
            if (metadata == null) {
                logger.warning(String.format("Error while reading metadata file %s", metadataFile.getName()));
            } else {
                metadata.setFileName(metadataFile.getName());
                String metadataProfile = metadata.getMetadataProfile();
                if (metadataProfile == null || !metadataProfile.startsWith(RapidEyeConstants.PROFILE_L1)) {
                    IOException ex = new IOException("The selected product is not a RapidEye L1 product. Please use the appropriate filter");
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                    throw ex;
                }
            }
        } else {
            logger.info("No metadata file found");
        }
        // Second, parse other *.xml if exist
        parseAdditionalMetadataFiles();

        try {
            String[] nitfFiles = getRasterFileNames(productDirectory);
            for (int i = 0; i < nitfFiles.length; i++) {
                NITFReaderWrapper reader = new NITFReaderWrapper(productDirectory.getFile(nitfFiles[i]));
                if (product == null) {
                    product = new Product(metadata != null ? metadata.getProductName() : RapidEyeConstants.PRODUCT_GENERIC_NAME,
                            RapidEyeConstants.L1_FORMAT_NAMES[0],
                            metadata != null ? metadata.getRasterWidth() : reader.getWidth(),
                            metadata != null ? metadata.getRasterHeight() : reader.getHeight(),
                            this);
                    if (metadata != null) {
                        product.setProductType(metadata.getMetadataProfile());
                        product.setStartTime(metadata.getProductStartTime());
                        product.setEndTime(metadata.getProductEndTime());
                        product.getMetadataRoot().addElement(metadata.getRootElement());
                        NITFMetadata nitfMetadata = reader.getMetadata();
                        if (nitfMetadata != null)
                            product.getMetadataRoot().addElement(nitfMetadata.getMetadataRoot());
                    }
                    product.setPreferredTileSize(getPreferredTileSize());
                }
                addBandToProduct(product, reader, i);
            }
            if (product != null) {
                readMasks(productDirectory);
                initGeoCoding(product);
                product.setModified(false);
            }
        } catch (IIOException e) {
            logger.severe("Product is not a valid RapidEye L1 data product!");
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        pm.beginTask("Reading band data...", 3);
        NITFReaderWrapper reader = readerMap.get(destBand);
        try {
            reader.readBandData(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destBuffer, pm);
        } finally {
            pm.done();
        }

    }

    @Override
    public void close() throws IOException {
        if (readerMap != null) {
            for (NITFReaderWrapper wrapper : readerMap.values()) {
                wrapper.close();
            }
            readerMap.clear();
        }
        super.close();
    }

    private String[] getRasterFileNames(ZipVirtualDir folder) {
        String[] fileNames;
        if (metadata != null) {
            fileNames = metadata.getRasterFileNames(true);
        } else {
            try {
                List<String> files = new ArrayList<String>();
                String[] productFiles = folder.list(".");
                for (String file : productFiles) {
                    if (file.toLowerCase().endsWith(RapidEyeConstants.NTF_EXTENSION))
                        files.add(file);
                }
                fileNames = new String[files.size()];
                fileNames = files.toArray(fileNames);
            } catch (IOException e) {
                fileNames = new String[0];
                logger.warning(e.getMessage());
            }
        }
        return fileNames;
    }

    private String[] getMetadataFileNames(ZipVirtualDir folder, String exclusion) {
        String[] fileNames;
        try {
            List<String> files = new ArrayList<String>();
            String[] productFiles = folder.list(".");
            for (String file : productFiles) {
                String lCase = file.toLowerCase();
                if ((exclusion == null || !lCase.endsWith(exclusion)) && lCase.endsWith(RapidEyeConstants.METADATA_EXTENSION))
                    files.add(file);
            }
            fileNames = files.toArray(new String[files.size()]);
        } catch (IOException e) {
            fileNames = new String[0];
            logger.warning(e.getMessage());
        }
        return fileNames;
    }

    private void parseAdditionalMetadataFiles() {
        String[] fileNames = getMetadataFileNames(productDirectory, RapidEyeConstants.METADATA_FILE_SUFFIX);
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                try {
                    logger.info(String.format("Reading metadata file %s", fileName));
                    RapidEyeMetadata metadataFile = XmlMetadata.create(RapidEyeMetadata.class, productDirectory.getFile(fileName));
                    if (metadataFile == null) {
                        logger.warning(String.format("Error while reading metadata file %s", fileName));
                    } else {
                        metadataFile.setFileName(fileName);
                        MetadataElement newNode = null;
                        if (fileName.endsWith("_rpc.xml")) {
                            newNode = new MetadataElement("Rational Polynomial Coefficients");
                            XmlMetadata.CopyChildElements(metadataFile.getRootElement(), newNode);
                        } else if (fileName.endsWith("_sci.xml")) {
                            newNode = new MetadataElement("Spacecraft Information");
                            XmlMetadata.CopyChildElements(metadataFile.getRootElement(), newNode);
                        }
                        if (newNode != null)
                            metadata.getRootElement().addElement(newNode);
                    }
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while opening file %s", fileName));
                }
            }
        }
    }

    private void addBandToProduct(Product product, NITFReaderWrapper reader, int bandIndex) {
        Assert.notNull(product);
        Assert.notNull(reader);
        Band band = product.addBand(RapidEyeConstants.BAND_NAMES[bandIndex], metadata.getPixelFormat());
        band.setSpectralWavelength(RapidEyeConstants.WAVELENGTHS[bandIndex]);
        band.setUnit("nm");
        band.setSpectralBandwidth(RapidEyeConstants.BANDWIDTHS[bandIndex]);
        band.setSpectralBandIndex(bandIndex);
        band.setScalingFactor(metadata.getScaleFactor(bandIndex));
        readerMap.put(band, reader);
    }

    private TiePointGrid addTiePointGrid(int width, int height, Product product, String gridName, float[] tiePoints) {
        final TiePointGrid tiePointGrid = createTiePointGrid(gridName, 2, 2, 0, 0, width, height, tiePoints);
        product.addTiePointGrid(tiePointGrid);
        return tiePointGrid;
    }

    private void initGeoCoding(Product product) {
        TiePointGrid latGrid = addTiePointGrid(product.getSceneRasterWidth(), product.getSceneRasterHeight(), product, "latitude", metadata.getCornersLatitudes());
        TiePointGrid lonGrid = addTiePointGrid(product.getSceneRasterWidth(), product.getSceneRasterHeight(), product, "longitude", metadata.getCornersLongitudes());
        GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
        product.setGeoCoding(geoCoding);
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isThisZipFile()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            String[] fileNames = getMetadataFileNames(productDirectory, RapidEyeConstants.METADATA_FILE_SUFFIX);
            for(String fileName : fileNames){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String[] nitfFiles = getRasterFileNames(productDirectory);
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
