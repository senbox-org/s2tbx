package org.esa.beam.dataio.s3.manifest;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.util.ProductUtils;
import org.esa.beam.util.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ManifestProductReader extends AbstractProductReader {

    private final List<Product> openProductList = new ArrayList<Product>();

    private final Logger logger;

    protected ManifestProductReader(ProductReaderPlugIn readerPlugIn) {
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
        final File inputFile = getInputFile();
        final Manifest manifest = createManifestFile(inputFile);

        return createProduct(manifest);
    }

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

    protected abstract List<String> getFileNames(Manifest manifest);

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

    private Product createProduct(Manifest manifest) throws IOException {
        readProducts(getFileNames(manifest));

        final String productName = getProductName();
        final String productType = getReaderPlugIn().getFormatNames()[0];

        final Product sourceProduct = openProductList.get(0);
        final int w = sourceProduct.getSceneRasterWidth();
        final int h = sourceProduct.getSceneRasterHeight();
        final Product targetProduct = new Product(productName, productType, w, h, this);

        final ProductData.UTC startTime = sourceProduct.getStartTime();
        final ProductData.UTC endTime = sourceProduct.getEndTime();
        if (startTime == null || endTime == null) {
            targetProduct.setStartTime(manifest.getStartTime());
            targetProduct.setEndTime(manifest.getStopTime());
        } else {
            targetProduct.setStartTime(startTime);
            targetProduct.setEndTime(endTime);
        }
        targetProduct.setFileLocation(getInputFile());

        if (sourceProduct.getGeoCoding() instanceof CrsGeoCoding) {
            ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
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

        return targetProduct;
    }

    private void addDataNodes(Product targetProduct) {
        final int w = targetProduct.getSceneRasterWidth();
        final int h = targetProduct.getSceneRasterHeight();

        for (final Product sourceProduct : openProductList) {
            for (final Band sourceBand : sourceProduct.getBands()) {
                final RasterDataNode targetNode;
                if (sourceBand.getSceneRasterWidth() == w && sourceBand.getSceneRasterHeight() == h) {
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

    private void readProducts(List<String> fileNames) throws IOException {
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

    private File getInputFile() {
        return new File(getInput().toString());
    }

    private File getInputFileParentDirectory() {
        return getInputFile().getParentFile();
    }

    private String getProductName() {
        return FileUtils.getFilenameWithoutExtension(getInputFileParentDirectory());
    }

    private Manifest createManifestFile(File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        try {
            return Manifest.createManifest(createXmlDocument(inputStream));
        } finally {
            inputStream.close();
        }
    }

    private Document createXmlDocument(InputStream inputStream) throws IOException {
        final String msg = "Cannot create document from manifest XML file.";

        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            logger.log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        }
    }
}
