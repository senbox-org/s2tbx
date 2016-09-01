/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.rapideye;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.nitf.NITFMetadata;
import org.esa.s2tbx.dataio.nitf.NITFReaderWrapper;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.rcp.colormanip.ColorPaletteManager;

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
    private static final String RAPID_EYE_L1_COLOR_PALETTE_FILE_NAME = "gradient_rapid_eye_l1_red.cpd";

    static {
        ColorPaletteManager.getDefault().copyColorPaletteFileFromResources(RapidEyeL1Reader.class.getClassLoader(), "org/esa/s2tbx/dataio/rapideye/", RAPID_EYE_L1_COLOR_PALETTE_FILE_NAME);
    }

    private final Map<Band, NITFReaderWrapper> readerMap;

    public RapidEyeL1Reader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        readerMap = new HashMap<>();
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
            String[] nitfFiles = getRasterFileNames();
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
                readMasks();
                initGeoCoding(product);
                product.setModified(false);
            }
        } catch (IIOException e) {
            logger.severe("Product is not a valid RapidEye L1 data product!");
        }
        product.setFileLocation(new File(productDirectory.getBasePath()));
        GeoTiffBasedReader.setBandColorPalettes(product, RapidEyeL1Reader.RAPID_EYE_L1_COLOR_PALETTE_FILE_NAME);
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
            readerMap.values().forEach(NITFReaderWrapper::close);
            readerMap.clear();
        }
        super.close();
    }

    private String[] getRasterFileNames() {
        String[] fileNames;
        if (metadata != null) {
            fileNames = metadata.getRasterFileNames();
        } else {
            try {
                List<String> files = new ArrayList<>();
                String[] productFiles = productDirectory.list(".");
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

    private String[] getMetadataFileNames(String exclusion) {
        String[] fileNames;
        try {
            List<String> files = new ArrayList<>();
            String[] productFiles = productDirectory.list(".");
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
        String[] fileNames = getMetadataFileNames(RapidEyeConstants.METADATA_FILE_SUFFIX);
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
        product.setSceneGeoCoding(geoCoding);
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isCompressed()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            String[] fileNames = getMetadataFileNames(RapidEyeConstants.METADATA_FILE_SUFFIX);
            for(String fileName : fileNames){
                try{
                    addProductComponentIfNotPresent(fileName, productDirectory.getFile(fileName), result);
                } catch (IOException e) {
                    logger.warning(String.format("Error encountered while searching file %s", fileName));
                }
            }
            String[] nitfFiles = getRasterFileNames();
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
