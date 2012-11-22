package org.esa.beam.dataio.s3.manifest;/*
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

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.FlagCoding;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.io.FileUtils;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class XProductReader extends AbstractProductReader {

    protected final List<Product> openProductList = new ArrayList<Product>();
    protected final Logger logger;

    public XProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = Logger.getLogger(getClass().getSimpleName());
    }

    protected static Band copyBand(Band sourceBand, Product targetProduct, boolean copySourceImage) {
        return ProductUtils.copyBand(sourceBand.getName(), sourceBand.getProduct(), targetProduct, copySourceImage);
    }

    protected static TiePointGrid copyBand(Band sourceBand, Product targetProduct, int subSamplingX, int subSamplingY,
                                           float offsetX, float offsetY) {
        final RenderedImage sourceImage = sourceBand.getGeophysicalImage();
        final int w = sourceImage.getWidth();
        final int h = sourceImage.getHeight();
        final float[] tiePoints = sourceImage.getData().getSamples(0, 0, w, h, 0, new float[w * h]);

        final String unit = sourceBand.getUnit();
        final TiePointGrid tiePointGrid = new TiePointGrid(sourceBand.getName(), w, h,
                                                           offsetX,
                                                           offsetY,
                                                           subSamplingX,
                                                           subSamplingY,
                                                           tiePoints,
                                                           unit != null && unit.toLowerCase().contains("degree"));
        final String description = sourceBand.getDescription();
        tiePointGrid.setDescription(description);
        tiePointGrid.setGeophysicalNoDataValue(sourceBand.getGeophysicalNoDataValue());
        tiePointGrid.setUnit(unit);
        targetProduct.addTiePointGrid(tiePointGrid);

        return tiePointGrid;
    }

    @Override
    protected final Product readProductNodesImpl() throws IOException {
        return createProduct();
    }

    private Product createProduct() throws IOException {
        readProducts(getFileNames());

        if (openProductList.size() == 1) {
            final Product targetProduct = openProductList.get(0);
            setMasks(targetProduct);
            openProductList.clear();
            return targetProduct;
        }

        final String productName = getProductName();
        final String productType = getReaderPlugIn().getFormatNames()[0];

        final Product masterProduct = findMasterProduct();
        final int w = masterProduct.getSceneRasterWidth();
        final int h = masterProduct.getSceneRasterHeight();
        final Product targetProduct = new Product(productName, productType, w, h, this);

        setTimes(targetProduct);
        targetProduct.setFileLocation(getInputFile());

        if (masterProduct.getGeoCoding() instanceof CrsGeoCoding) {
            ProductUtils.copyGeoCoding(masterProduct, targetProduct);
        }

        for (final Product p : openProductList) {
            final MetadataElement productAttributes = new MetadataElement(p.getName());
            final MetadataElement datasetAttributes = new MetadataElement("Dataset_Attributes");
            final MetadataElement variableAttributes = new MetadataElement("Variable_Attributes");
            ProductUtils.copyMetadata(p.getMetadataRoot().getElement("Global_Attributes"), datasetAttributes);
            for (final MetadataElement element : p.getMetadataRoot().getElement("Variable_Attributes").getElements()) {
                variableAttributes.addElement(element.createDeepClone());
            }
            productAttributes.addElement(datasetAttributes);
            productAttributes.addElement(variableAttributes);
            targetProduct.getMetadataRoot().addElement(productAttributes);
        }

        final Product[] sourceProducts = openProductList.toArray(new Product[openProductList.size()]);
        initialize(sourceProducts, targetProduct);
        addDataNodes(targetProduct);
        setGeoCoding(targetProduct);
        setAutoGrouping(sourceProducts, targetProduct);
        setMasks(sourceProducts, targetProduct);

        return targetProduct;
    }

    protected Product findMasterProduct() {
        return openProductList.get(0);
    }

    private void setMasks(Product[] sourceProducts, Product targetProduct) {
        for (Product sourceProduct : sourceProducts) {
            setMasks(targetProduct);
        }
    }

    protected void setMasks(Product targetProduct) {
        final Band[] bands = targetProduct.getBands();
        for (Band band : bands) {
            if(band.isFlagBand()) {
                final FlagCoding flagCoding = band.getFlagCoding();
                for (int j = 0; j < flagCoding.getNumAttributes(); j++) {
                    final MetadataAttribute attribute = flagCoding.getAttributeAt(j);
                    final String expression = band.getName() + "." + attribute.getName();
                    final String maskName = band.getName() + "_" + attribute.getName();
                    targetProduct.addMask(maskName, expression, expression, Color.RED, 0.5);
                }
            }
        }
//        if (targetProduct.getFlagCodingGroup() != null) {
//            for (int i = 0; i < targetProduct.getFlagCodingGroup().getNodeCount(); i++) {
//                final FlagCoding flagCoding = targetProduct.getFlagCodingGroup().get(i);
//                String flagCodingName = flagCoding.getName();
//                if(!targetProduct.containsBand(flagCodingName)) {
//                    flagCodingName = targetProduct.getName() + "_" + flagCoding.getName();
//                }
//                for (int j = 0; j < flagCoding.getNumAttributes(); j++) {
//                    final MetadataAttribute attribute = flagCoding.getAttributeAt(j);
//                    final String expression = flagCodingName + "." + attribute.getName();
//                    targetProduct.addMask(attribute.getName(), expression, expression, Color.RED, 0.5);
//                }
//            }
//        }
    }

    protected void setTimes(Product targetProduct) {
        final Product sourceProduct = findMasterProduct();
        final ProductData.UTC startTime = sourceProduct.getStartTime();
        final ProductData.UTC endTime = sourceProduct.getEndTime();
        targetProduct.setStartTime(startTime);
        targetProduct.setEndTime(endTime);
    }

    protected abstract List<String> getFileNames() throws IOException;

    @Override
    protected final void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight,
                                                int sourceStepX, int sourceStepY, Band destBand, int destOffsetX,
                                                int destOffsetY, int destWidth, int destHeight, ProductData destBuffer,
                                                ProgressMonitor pm) throws IOException {
        throw new IllegalStateException("Data are provided by images.");
    }

    @Override
    public final void close() throws IOException {
        for (final Product product : openProductList) {
            product.dispose();
        }
        openProductList.clear();
        super.close();
    }

    protected Band addBand(Band sourceBand, Product targetProduct) {
        return copyBand(sourceBand, targetProduct, true);
    }

    protected RasterDataNode addSpecialNode(Band sourceBand, Product targetProduct) {
        return null;
    }

    protected void setGeoCoding(Product targetProduct) throws IOException {
    }

    protected void configureTargetNode(Band sourceBand, RasterDataNode targetNode) {
    }

    protected void setAutoGrouping(Product[] sourceProducts, Product targetProduct) {
        final StringBuilder patternBuilder = new StringBuilder();
        for (final Product sourceProduct : sourceProducts) {
            if (sourceProduct.getAutoGrouping() != null) {
                if (patternBuilder.length() > 0) {
                    patternBuilder.append(":");
                }
                patternBuilder.append(sourceProduct.getAutoGrouping());
            }
        }
        targetProduct.setAutoGrouping(patternBuilder.toString());
    }

    protected void initialize(Product[] sourceProducts, Product targetProduct) {
    }

    protected void addDataNodes(Product targetProduct) {
        final int w = targetProduct.getSceneRasterWidth();
        final int h = targetProduct.getSceneRasterHeight();

        for (final Product sourceProduct : openProductList) {
            for (final Band sourceBand : sourceProduct.getBands()) {
                final RasterDataNode targetNode;
                if ((sourceBand.getSceneRasterWidth() == w && sourceBand.getSceneRasterHeight() == h)) {
                    targetNode = addBand(sourceBand, targetProduct);
                } else {
                    targetNode = addSpecialNode(sourceBand, targetProduct);
                }
                if (targetNode != null) {
                    configureTargetNode(sourceBand, targetNode);
                }
            }
        }
    }

    protected void readProducts(List<String> fileNames) throws IOException {
        for (final String fileName : fileNames) {
            readProduct(fileName);
        }
    }

    private Product readProduct(String fileName) throws IOException {
        final File file = new File(getInputFileParentDirectory(), fileName);
        final Product product = ProductIO.readProduct(file);
        if (product == null) {
            final String msg = MessageFormat.format("Cannot read file ''{0}''. No appropriate reader found.", fileName);
            logger.log(Level.SEVERE, msg);
            throw new IOException(msg);
        }
        openProductList.add(product);
        return product;
    }

    protected File getInputFile() {
        return new File(getInput().toString());
    }

    protected File getInputFileParentDirectory() {
        return getInputFile().getParentFile();
    }

    protected String getProductName() {
        return FileUtils.getFilenameWithoutExtension(getInputFileParentDirectory());
    }
}
