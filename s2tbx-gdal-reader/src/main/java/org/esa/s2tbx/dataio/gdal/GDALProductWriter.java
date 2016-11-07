package org.esa.s2tbx.dataio.gdal;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.gdal.internal.BufferTypeDescriptor;
import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.Guardian;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.awt.image.DataBuffer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class GDALProductWriter extends AbstractProductWriter {
    private static final Logger logger = Logger.getLogger(GDALProductWriter.class.getName());

    private final String driverName;

    private Dataset dataset;
    private Map<Band, org.gdal.gdal.Band> bandsMap;
    private int gdalDataType;

    public GDALProductWriter(ProductWriterPlugIn writerPlugIn, String driverName) {
        super(writerPlugIn);

        this.driverName = driverName;
    }

    @Override
    protected void writeProductNodesImpl() throws IOException {
        Object output = getOutput();

        logger.info("Saving the product using the GDAL writer into the file '" + output.toString() + "'.");

        Path outputFile = GDALProductReader.getFileInput(output);
        if (outputFile == null) {
            throw new IOException("The file '"+ output.toString() + "' to save the product is invalid.");
        }

        Product sourceProduct = getSourceProduct();

        int imageWidth = sourceProduct.getSceneRasterWidth();
        int imageHeight = sourceProduct.getSceneRasterHeight();
        int bandCount = sourceProduct.getNumBands();

        Band sourceBand = sourceProduct.getBandAt(0);
        this.gdalDataType = getGDALDataType(sourceBand.getDataType());
        for (int i=1; i<bandCount; i++) {
            sourceBand = sourceProduct.getBandAt(i);
            if (this.gdalDataType != getGDALDataType(sourceBand.getDataType())) {
                throw new IllegalArgumentException("Different data type " + sourceBand.getDataType() + " for band index " + i + ".");
            }
        }

        Driver driver = gdal.GetDriverByName(this.driverName);

        this.dataset = driver.Create(outputFile.toString(), imageWidth, imageHeight, bandCount, this.gdalDataType);
        this.bandsMap = new HashMap<Band, org.gdal.gdal.Band>(bandCount);
    }

    @Override
    public void writeBandRasterData(Band sourceBand, int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, ProductData sourceBuffer, ProgressMonitor pm) throws IOException {
        Guardian.assertNotNull("sourceBand", sourceBand);
        Guardian.assertNotNull("sourceBuffer", sourceBuffer);
        checkBufferSize(sourceWidth, sourceHeight, sourceBuffer);

        long sourceBandWidth = sourceBand.getRasterWidth();
        long sourceBandHeight = sourceBand.getRasterHeight();
        checkSourceRegionInsideBandRegion(sourceWidth, sourceBandWidth, sourceHeight, sourceBandHeight, sourceOffsetX, sourceOffsetY);

        Product sourceProduct = getSourceProduct();
        org.gdal.gdal.Band gdalBand = this.bandsMap.get(sourceBand);
        if (gdalBand == null) {
            int bandIndex = sourceProduct.getBandIndex(sourceBand.getName());
            gdalBand = this.dataset.GetRasterBand(bandIndex + 1);
            this.bandsMap.put(sourceBand, gdalBand);
        }

        pm.beginTask("Writing band '" + sourceBand.getName() + "'...", sourceHeight);
        try {
            if (this.gdalDataType == gdalconstConstants.GDT_Byte) {
                byte[] data = (byte[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Int16) {
                short[] data = (short[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_UInt16) {
                short[] data = (short[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Int32) {
                int[] data = (int[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_UInt32) {
                int[] data = (int[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Float32) {
                float[] data = (float[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Float64) {
                double[] data = (double[])sourceBuffer.getElems();
                gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else {
                throw new IllegalArgumentException("Unknown GDAL data type " + this.gdalDataType + ".");
            }

            pm.worked(1);
        } finally {
            pm.done();
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        this.dataset.delete();
    }

    @Override
    public void deleteOutput() throws IOException {
    }

    private static void checkBufferSize(int sourceWidth, int sourceHeight, ProductData sourceBuffer) {
        int expectedBufferSize = sourceWidth * sourceHeight;
        int actualBufferSize = sourceBuffer.getNumElems();
        Guardian.assertEquals("sourceWidth * sourceHeight", actualBufferSize, expectedBufferSize);  /*I18N*/
    }

    private static void checkSourceRegionInsideBandRegion(int sourceWidth, long sourceBandWidth, int sourceHeight,
                                                          long sourceBandHeight, int sourceOffsetX, int sourceOffsetY) {

        Guardian.assertWithinRange("sourceWidth", sourceWidth, 1, sourceBandWidth);
        Guardian.assertWithinRange("sourceHeight", sourceHeight, 1, sourceBandHeight);
        Guardian.assertWithinRange("sourceOffsetX", sourceOffsetX, 0, sourceBandWidth - sourceWidth);
        Guardian.assertWithinRange("sourceOffsetY", sourceOffsetY, 0, sourceBandHeight - sourceHeight);
    }

    private static int getGDALDataType(int bandDataType) {
        if (bandDataType == ProductData.TYPE_UINT8) {
            return gdalconstConstants.GDT_Byte;
        }
        if (bandDataType == ProductData.TYPE_INT16) {
            return gdalconstConstants.GDT_Int16;
        }
        if (bandDataType == ProductData.TYPE_UINT16) {
            return gdalconstConstants.GDT_UInt16;
        }
        if (bandDataType == ProductData.TYPE_INT32) {
            return gdalconstConstants.GDT_Int32;
        }
        if (bandDataType == ProductData.TYPE_UINT32) {
            return gdalconstConstants.GDT_UInt32;
        }
        if (bandDataType == ProductData.TYPE_FLOAT32) {
            return gdalconstConstants.GDT_Float32;
        }
        if (bandDataType == ProductData.TYPE_FLOAT64) {
            return gdalconstConstants.GDT_Float64;
        }
        throw new IllegalArgumentException("Unknown band data type " + bandDataType + ".");
    }
}
