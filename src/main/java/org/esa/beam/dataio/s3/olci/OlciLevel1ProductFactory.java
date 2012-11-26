package org.esa.beam.dataio.s3.olci;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

import com.bc.ceres.glevel.MultiLevelImage;
import org.esa.beam.dataio.s3.ProductFactory;
import org.esa.beam.dataio.s3.Sentinel3ProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductSubsetDef;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeGroup;
import org.esa.beam.framework.datamodel.TiePointGeoCoding;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.util.ProductUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OlciLevel1ProductFactory implements ProductFactory {

    private static final float[] spectralWavelengths = new float[21];
    private static final float[] spectralBandwidths = new float[21];

    static {
        getSpectralBandsProperties(spectralWavelengths, spectralBandwidths);
    }

    static void getSpectralBandsProperties(float[] wavelengths, float[] bandwidths) {
        final Properties properties = new Properties();

        try {
            properties.load(OlciLevel1ProductFactory.class.getResourceAsStream("spectralBands.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (int i = 0; i < wavelengths.length; i++) {
            wavelengths[i] = Float.parseFloat(properties.getProperty("wavelengths." + i));
        }
        for (int i = 0; i < bandwidths.length; i++) {
            bandwidths[i] = Float.parseFloat(properties.getProperty("bandwidths." + i));
        }
    }

    private final Sentinel3ProductReader productReader;
    private final Logger logger;
    private List<Product> bandProducts;
    private List<Product> annotationProducts;

    public OlciLevel1ProductFactory(Sentinel3ProductReader productReader) {
        this.productReader = productReader;
        this.logger = Logger.getLogger(getClass().getSimpleName());
    }

    @Override
    public Product createProduct() throws IOException {
        File inputFile = productReader.getInputFile();
        OlciL1bManifest manifest = createManifestFile(inputFile);
        Product product = new Product(manifest.getProductName(), manifest.getProductType(),
                                      manifest.getColumnCount(), manifest.getLineCount(), productReader);
        product.setDescription(manifest.getDescription());
        product.setStartTime(manifest.getStartTime());
        product.setEndTime(manifest.getStopTime());
        product.setFileLocation(inputFile);
        MetadataElement root = product.getMetadataRoot();
        root.addElement(manifest.getFixedHeader());
        root.addElement(manifest.getMainProductHeader());
        root.addElement(manifest.getSpecificProductHeader());
        attachBandsToProduct(manifest, product);
        attachAnnotationDataToProduct(manifest, product);
        product.setAutoGrouping("TOA_radiances_Oa:error_estimates_Oa:TOA_radiances_Ob:error_estimates_Ob");
        return product;
    }

    private void attachAnnotationDataToProduct(OlciL1bManifest manifest, Product product) {
        List<DataSetPointer> annotationPointers = manifest.getDataSetPointers(DataSetPointer.Type.A);
        annotationPointers = removeOrphanedDataSetPointers(annotationPointers);
        annotationProducts = createDataSetProducts(annotationPointers);
        attachBandsFromAnnotationDataToProduct(annotationProducts, product);
        attachGeoCodingToProduct(annotationProducts, product);
        attachFlagCodingToProduct(annotationProducts, product);
        attachTiePointsToProduct(annotationProducts, product);
    }

    private void attachTiePointsToProduct(List<Product> annotationProducts, Product product) {
        for (final Product annotationProduct : annotationProducts) {
            if ("TiePoints".equals(annotationProduct.getName())) {
                final Band[] tiePointBands = annotationProduct.getBands();
                final MetadataElement metadataRoot = annotationProduct.getMetadataRoot();
                final MetadataElement globalAttributes = metadataRoot.getElement("Global_Attributes");
                final int subsampling = globalAttributes.getAttributeInt("subsampling_factor");
                for (final Band band : tiePointBands) {
                    final MultiLevelImage sourceImage = band.getGeophysicalImage();
                    final int width = sourceImage.getWidth();
                    final int height = sourceImage.getHeight();
                    final float[] tiePointData = new float[width * height];
                    sourceImage.getData().getSamples(0, 0, width, height, 0, tiePointData);
                    final TiePointGrid tiePointGrid = new TiePointGrid(band.getName(), band.getRasterWidth(),
                                                                       band.getRasterHeight(), 0, 0, subsampling,
                                                                       subsampling, tiePointData, true);
                    product.addTiePointGrid(tiePointGrid);
                }
            }
        }
        if (product.getTiePointGrid("TP_latitude") != null && product.getTiePointGrid("TP_longitude") != null) {
            product.setGeoCoding(new TiePointGeoCoding(product.getTiePointGrid("TP_latitude"),
                                                       product.getTiePointGrid("TP_longitude")));
        }
    }

    private void attachFlagCodingToProduct(List<Product> annotationProducts, Product product) {
        for (Product annotationProduct : annotationProducts) {
            if (annotationProduct.getFlagCodingGroup().getNodeCount() > 0) {
                ProductUtils.copyFlagBands(annotationProduct, product, true);
                setMasks(annotationProduct.getFlagCodingGroup(), product);
            }
        }
    }

    protected void setMasks(ProductNodeGroup<FlagCoding> flagCodingGroup, Product targetProduct) {
        for (int i = 0; i < flagCodingGroup.getNodeCount(); i++) {
            final FlagCoding flagCoding = flagCodingGroup.get(i);
            for (int j = 0; j < flagCoding.getNumAttributes(); j++) {
                final MetadataAttribute attribute = flagCoding.getAttributeAt(j);
                final String expression = flagCoding.getName() + "." + attribute.getName();
                targetProduct.addMask(attribute.getName(), expression, attribute.getDescription(), Color.RED, 0.5);
            }
        }
    }

    private void attachBandsFromAnnotationDataToProduct(List<Product> annotationProducts, Product product) {
        for (Product annotationProduct : annotationProducts) {
            if (annotationProduct.containsBand("/detector_index")) {
                ProductUtils.copyBand("/detector_index", annotationProduct, product, true);
                break;
            }
        }
    }

    private void attachGeoCodingToProduct(List<Product> annotationProducts, Product product) {
        for (Product annotationProduct : annotationProducts) {
            if (annotationProduct.getGeoCoding() != null) {
                ProductUtils.copyGeoCoding(annotationProduct, product);
            }
        }
    }

    private void attachBandsToProduct(OlciL1bManifest manifest, Product product) {
        List<DataSetPointer> measurementPointers = manifest.getDataSetPointers(DataSetPointer.Type.M);
        measurementPointers = removeOrphanedDataSetPointers(measurementPointers);
        bandProducts = createDataSetProducts(measurementPointers);
        addRadianceBands(bandProducts, product);
    }

    private List<Product> createDataSetProducts(List<DataSetPointer> dataSetPointers) {
        List<Product> dataSetProducts = new ArrayList<Product>();
        for (DataSetPointer dataSetPointer : dataSetPointers) {
            try {
                File dataSetFile = new File(productReader.getInputFileParentDirectory(), dataSetPointer.getFileName());
                final ProductReader productReader = ProductIO.getProductReaderForInput(dataSetFile);
                if (productReader != null) {
                    final ProductSubsetDef subsetDef = defineSubset(dataSetPointer.getFileName());
                    final Product product = productReader.readProductNodes(dataSetFile, subsetDef);
                    if (product != null) {
                        dataSetProducts.add(product);
                    } else {
                        String msg = String.format("Could not read file '%s.",
                                                   dataSetPointer.getFileName());
                        logger.log(Level.WARNING, msg);
                    }
                } else {
                    String msg = String.format("Could not read file '%s. No appropriate reader found.",
                                               dataSetPointer.getFileName());
                    logger.log(Level.WARNING, msg);
                }
            } catch (IOException e) {
                String msg = String.format("Not able to read file '%s.", dataSetPointer.getFileName());
                logger.log(Level.WARNING, msg, e);
            }
        }
        return dataSetProducts;
    }

    private ProductSubsetDef defineSubset(String fileName) {
        ProductSubsetDef subsetDef = null;
        if (fileName.equals("generalInfo.nc")) {
            subsetDef = new ProductSubsetDef();
            subsetDef.addNodeName("detector_index");
        }
        return subsetDef;
    }

    private void addRadianceBands(List<Product> bandProducts, Product product) {
        for (final Product bandProduct : bandProducts) {
            for (final Band sourceBand : bandProduct.getBands()) {
                if (hasSameRasterDimension(product, bandProduct)) {
                    String bandName = sourceBand.getName();
                    Band targetBand = ProductUtils.copyBand(bandName, bandProduct, product, true);
                    if (bandName.matches("TOA_radiances_Oa[0-2][0-9]")) {
                        final int channel = Integer.parseInt(bandName.substring(16, 18));
                        targetBand.setSpectralBandIndex(channel - 1);
                        targetBand.setSpectralWavelength(spectralWavelengths[channel - 1]);
                        targetBand.setSpectralBandwidth(spectralBandwidths[channel - 1]);
                    }
                }
            }
        }
    }

    private boolean hasSameRasterDimension(Product productOne, Product productTwo) {
        int widthOne = productOne.getSceneRasterWidth();
        int heightOne = productOne.getSceneRasterHeight();
        int widthTwo = productTwo.getSceneRasterWidth();
        int heightTwo = productTwo.getSceneRasterHeight();
        return widthOne == widthTwo && heightOne == heightTwo;
    }

    private List<DataSetPointer> removeOrphanedDataSetPointers(List<DataSetPointer> dataSetPointers) {
        File parentFile = productReader.getInputFileParentDirectory();
        List<DataSetPointer> filteredPointers = new ArrayList<DataSetPointer>();
        for (DataSetPointer dataSetPointer : dataSetPointers) {
            String fileName = dataSetPointer.getFileName();
            File dataSetFile = new File(parentFile, fileName);
            if (!dataSetFile.exists()) {
                String patchedFileName = patchFileName(fileName);
                dataSetFile = new File(parentFile, patchedFileName);
                dataSetPointer.setFileName(patchedFileName);
            }
            if (dataSetFile.exists()) {
                filteredPointers.add(dataSetPointer);
            }
        }
        return filteredPointers;
    }

    static String patchFileName(String fileName) {
        if (fileName.matches("radianceOa[1-9].nc")) {
            return fileName.substring(0, 8) + "sOa0" + fileName.substring(10);
        }
        if (fileName.matches("radianceOa[1-2][0-9].nc")) {
            return fileName.substring(0, 8) + "sOa" + fileName.substring(10);
        }
        return fileName;
    }

    private OlciL1bManifest createManifestFile(File inputFile) throws IOException {
        InputStream manifestInputStream = new FileInputStream(inputFile);
        try {
            return new OlciL1bManifest(createXmlDocument(manifestInputStream));
        } finally {
            manifestInputStream.close();
        }
    }

    private Document createXmlDocument(InputStream inputStream) throws IOException {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public void dispose() throws IOException {
        for (Product bandProduct : bandProducts) {
            bandProduct.dispose();
        }
        for (Product annotationProduct : annotationProducts) {
            annotationProduct.dispose();
        }
    }

}
