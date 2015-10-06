package org.esa.s2tbx.dataio.nitf;

import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generic NITF Reader.
 *
 * @author Cosmin Cara
 */
public class NITFReader extends AbstractProductReader {

    protected Product product;
    protected NITFReaderWrapper reader;
    protected final Logger logger = Logger.getLogger(NITFReader.class.getName());
    protected File inputFile;

    public NITFReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        try {
            Object input = getInput();
            if (input != null) {
                inputFile = input instanceof File ? (File) input : new File(input.toString());
                reader = new NITFReaderWrapper(inputFile);
                if (product == null) {
                    NITFMetadata metadata = reader.getMetadata();
                    product = new Product(metadata != null ? metadata.getFileTitle() : "NITF Image",
                            NITFConstants.FORMAT_NAMES[0],
                            metadata != null ? metadata.getWidth() : reader.getWidth(),
                            metadata != null ? metadata.getHeight() : reader.getHeight(),
                            this);
                    if (metadata != null) {
                        product.setProductType("Generic NITF");
                        product.setStartTime(metadata.getFileDate());
                        product.setEndTime(metadata.getFileDate());
                        product.getMetadataRoot().addElement(metadata.getMetadataRoot());
                        int numBands = metadata.getNumBands();
                        for (int i = 0; i < numBands; i++) {
                            addBandToProduct(product, reader, i);
                        }
                    }
                }
            }
            if (product != null) {
                product.setModified(false);
                product.setFileLocation(inputFile);
            }
        } catch (IIOException e) {
            logger.severe("Product is not a valid NITF data product!");
        }
        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        pm.beginTask("Reading band data...", 3);
        try {
            reader.readBandData(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, sourceStepX, sourceStepY, destBuffer, pm);
        } finally {
            pm.done();
        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        super.close();
    }

    private void addBandToProduct(Product product, NITFReaderWrapper reader, int bandIndex) {
        Assert.notNull(product);
        Assert.notNull(reader);
        NITFMetadata nitfMetadata = reader.getMetadata();
        Band band = product.addBand(RapidEyeConstants.BAND_NAMES[bandIndex], nitfMetadata.getDataType());
        band.setSpectralWavelength(nitfMetadata.getWavelength());
        band.setUnit(nitfMetadata.getUnit());
        band.setSpectralBandIndex(bandIndex);
    }
}
