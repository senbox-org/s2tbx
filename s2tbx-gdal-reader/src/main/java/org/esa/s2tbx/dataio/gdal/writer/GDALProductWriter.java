package org.esa.s2tbx.dataio.gdal.writer;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.gdal.GDALUtils;
import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.utils.StringHelper;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic writer for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class GDALProductWriter extends AbstractProductWriter {
    private static final Logger logger = Logger.getLogger(GDALProductWriter.class.getName());

    private final GDALDriverInfo writerDriver;

    private Dataset gdalDataset;
    private Map<Band, org.gdal.gdal.Band> bandsMap;
    private int gdalDataType;
    private Driver gdalDriver;

    public GDALProductWriter(ProductWriterPlugIn writerPlugIn, GDALDriverInfo writerDriver) {
        super(writerPlugIn);

        this.writerDriver = writerDriver;
    }

    @Override
    protected void writeProductNodesImpl() throws IOException {
        Object output = getOutput();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Saving the product into the file '" + output.toString() + "' using the GDAL plugin writer '" + getWriterPlugIn().getClass().getName() + "'.");
        }

        Path outputFile = getFileInput(output);
        if (outputFile == null) {
            throw new IllegalArgumentException("The file '"+ output.toString() + "' to save the product is invalid.");
        }

        Product sourceProduct = getSourceProduct();

        int imageWidth = sourceProduct.getSceneRasterWidth();
        int imageHeight = sourceProduct.getSceneRasterHeight();
        int bandCount = sourceProduct.getNumBands();

        Band sourceBand = sourceProduct.getBandAt(0);
        this.gdalDataType = GDALUtils.getGDALDataType(sourceBand.getDataType());
        for (int i=1; i<bandCount; i++) {
            sourceBand = sourceProduct.getBandAt(i);
            if (this.gdalDataType != GDALUtils.getGDALDataType(sourceBand.getDataType())) {
                throw new IllegalArgumentException("GDAL Geotiff writer cannot write a product containing bands with different data types (the data type of band index " + i + " is " + sourceBand.getDataType() + ", different from band index 0).");
            }
        }

        String fileName = outputFile.toFile().getName();
        if (!StringHelper.endsWithIgnoreCase(fileName, this.writerDriver.getExtensionName())) {
            throw new IllegalArgumentException("The extension of the file name '" + fileName + "' is unknown.");
        }
        if (!this.writerDriver.canExportProduct(this.gdalDataType)) {
            String gdalDataTypeName = gdal.GetDataTypeName(this.gdalDataType);
            String message = MessageFormat.format("The GDAL driver ''{0}'' does not support the data type ''{1}'' to create a new product." +
                            " The available types are ''{2}''.",
                    this.writerDriver.getDriverDisplayName(), gdalDataTypeName, this.writerDriver.getCreationDataTypes());
            throw new IllegalArgumentException(message);
        }

        this.gdalDriver = gdal.GetDriverByName(this.writerDriver.getDriverName());
        if (this.gdalDriver == null) {
            throw new NullPointerException("The GDAL driver '" + this.writerDriver.getDriverDisplayName() + "' ("+this.writerDriver.getDriverName()+") used to write the product does not exist.");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,"Using the GDAL driver '" + this.gdalDriver.getLongName() + "' ("+this.gdalDriver.getShortName()+") to save the product.");
        }

        this.gdalDataset = this.gdalDriver.Create(outputFile.toString(), imageWidth, imageHeight, bandCount, this.gdalDataType);
        if (this.gdalDataset == null) {
            throw new NullPointerException("Failed creating the file to export the product for driver '" + this.gdalDriver.getLongName() + "'.");
        }
        this.bandsMap = new HashMap<Band, org.gdal.gdal.Band>(bandCount);

        GeoCoding geoCoding = sourceProduct.getSceneGeoCoding();
        if (geoCoding == null) {
            this.gdalDataset.SetProjection("");
        } else {
            this.gdalDataset.SetProjection(geoCoding.getGeoCRS().toWKT());
        }
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
            gdalBand = this.gdalDataset.GetRasterBand(bandIndex + 1);
            if (gdalBand == null) {
                throw new NullPointerException("Failed creating the band with index " + bandIndex + " to export the product for driver '" + this.gdalDriver.getLongName() + "'.");
            }
            this.bandsMap.put(sourceBand, gdalBand);
        }

        int result = 0;
        pm.beginTask("Writing band '" + sourceBand.getName() + "'...", sourceHeight);
        try {
            if (this.gdalDataType == gdalconstConstants.GDT_Byte) {
                byte[] data = (byte[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Int16) {
                short[] data = (short[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_UInt16) {
                short[] data = (short[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Int32) {
                int[] data = (int[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_UInt32) {
                int[] data = (int[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Float32) {
                float[] data = (float[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == gdalconstConstants.GDT_Float64) {
                double[] data = (double[])sourceBuffer.getElems();
                result = gdalBand.WriteRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else {
                throw new IllegalArgumentException("Unknown GDAL data type " + this.gdalDataType + ".");
            }

            if (result != gdalconst.CE_None) {
                throw new IllegalArgumentException("Failed to write the data for band name '" + sourceBand.getName() + "' and driver '" + this.gdalDriver.getLongName()+"'.");
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
        if (this.gdalDataset != null) {
            this.gdalDataset.delete();
        }
    }

    @Override
    public void deleteOutput() throws IOException {
    }

    private static Path getFileInput(Object input) {
        if (input instanceof String) {
            return Paths.get((String) input);
        } else if (input instanceof File) {
            return ((File) input).toPath();
        } else if (input instanceof Path) {
            return (Path) input;
        }
        return null;
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
}
