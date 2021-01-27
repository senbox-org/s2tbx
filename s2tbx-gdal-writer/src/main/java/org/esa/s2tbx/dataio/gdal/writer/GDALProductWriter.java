/*
 *
 *  * Copyright (C) 2017 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */
package org.esa.s2tbx.dataio.gdal.writer;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.lib.gdal.activator.GDALDriverInfo;
import org.esa.s2tbx.dataio.gdal.GDALLoader;
import org.esa.s2tbx.dataio.gdal.drivers.Dataset;
import org.esa.s2tbx.dataio.gdal.drivers.Driver;
import org.esa.s2tbx.dataio.gdal.drivers.GDAL;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConst;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConstConstants;
import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.runtime.Config;
import org.esa.snap.utils.StringHelper;
import org.geotools.referencing.operation.transform.AffineTransform2D;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    /*
    Add concurrent map for accessing GDAL bands from dataset in separated threads.
     The key for ConcurrentMap is the ID of thread
     The value for ConcurrentMap is the map with bands processed by specific thread
     The key for map with bands processed by specific thread is the bandIndex from product
     The value for map with bands processed by specific thread is the org.esa.s2tbx.dataio.gdal.drivers.Band object
     (SIITBX-435)
     */
    private ConcurrentMap<Long, Map<Integer, org.esa.s2tbx.dataio.gdal.drivers.Band>> gdalBands = new ConcurrentHashMap<>();
    private int gdalDataType;
    private Driver gdalDriver;

    public GDALProductWriter(ProductWriterPlugIn writerPlugIn, GDALDriverInfo writerDriver) {
        super(writerPlugIn);

        this.writerDriver = writerDriver;
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

    @Override
    protected void writeProductNodesImpl() {
        Object output = getOutput();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Saving the product into the file '" + output.toString() + "' using the GDAL plugin writer '" + getWriterPlugIn().getClass().getName() + "'.");
        }

        Path outputFile = getFileInput(output);
        if (outputFile == null) {
            throw new IllegalArgumentException("The file '" + output.toString() + "' to save the product is invalid.");
        }

        Product sourceProduct = getSourceProduct();

        int imageWidth = sourceProduct.getSceneRasterWidth();
        int imageHeight = sourceProduct.getSceneRasterHeight();
        int bandCount = sourceProduct.getNumBands();

        Band sourceBand = sourceProduct.getBandAt(0);
        this.gdalDataType = GDALLoader.getInstance().getGDALDataType(sourceBand.getDataType());
        for (int i = 1; i < bandCount; i++) {
            sourceBand = sourceProduct.getBandAt(i);
            if (this.gdalDataType != GDALLoader.getInstance().getGDALDataType(sourceBand.getDataType())) {
                throw new IllegalArgumentException("GDAL Geotiff writer cannot write a product containing bands with different data types (the data type of band index " + i + " is " + sourceBand.getDataType() + ", different from band index 0).");
            }
        }

        String fileName = outputFile.toFile().getName();
        if (!StringHelper.endsWithIgnoreCase(fileName, this.writerDriver.getExtensionName())) {
            throw new IllegalArgumentException("The extension of the file name '" + fileName + "' is unknown.");
        }
        if (!this.writerDriver.canExportProduct(this.gdalDataType)) {
            String gdalDataTypeName = GDAL.getDataTypeName(this.gdalDataType);
            String message = MessageFormat.format("The GDAL driver ''{0}'' does not support the data type ''{1}'' to create a new product." +
                            " The available types are ''{2}''.",
                    this.writerDriver.getDriverDisplayName(), gdalDataTypeName, this.writerDriver.getCreationDataTypes());
            throw new IllegalArgumentException(message);
        }

        this.gdalDriver = GDAL.getDriverByName(this.writerDriver.getDriverName());
        if (this.gdalDriver == null) {
            throw new NullPointerException("The GDAL driver '" + this.writerDriver.getDriverDisplayName() + "' (" + this.writerDriver.getDriverName() + ") used to write the product does not exist.");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Using the GDAL driver '" + this.gdalDriver.getLongName() + "' (" + this.gdalDriver.getShortName() + ") to save the product.");
        }

        final String gdalWriteOptions = Config.instance().preferences().get("snap.dataio.gdal.creationoptions", "");
        if (!gdalWriteOptions.isEmpty()) {
            String[] options = StringUtils.stringToArray(gdalWriteOptions, ";");
            this.gdalDataset = this.gdalDriver.create(outputFile.toString(), imageWidth, imageHeight, bandCount, this.gdalDataType, options);
        }else{
            this.gdalDataset = this.gdalDriver.create(outputFile.toString(), imageWidth, imageHeight, bandCount, this.gdalDataType);
        }
        if (this.gdalDataset == null) {
            throw new NullPointerException("Failed creating the file to export the product for driver '" + this.gdalDriver.getLongName() + "'.");
        }

        GeoCoding geoCoding = sourceProduct.getSceneGeoCoding();
        if (geoCoding == null) {
            this.gdalDataset.setProjection("");
        } else {
            this.gdalDataset.setProjection(geoCoding.getMapCRS().toWKT());
            if (geoCoding.getImageToMapTransform() instanceof AffineTransform2D) {
                AffineTransform2D transform = (AffineTransform2D) geoCoding.getImageToMapTransform();
                double[] gdalGeoTransform = new double[6];
                gdalGeoTransform[0] = transform.getTranslateX();
                gdalGeoTransform[3] = transform.getTranslateY();
                gdalGeoTransform[1] = transform.getScaleX();
                gdalGeoTransform[5] = transform.getScaleY();

                this.gdalDataset.setGeoTransform(gdalGeoTransform);
            }
        }
    }

    /**
     * Gets the org.esa.s2tbx.dataio.gdal.drivers.Band object from concurrent map for accessing GDAL bands from dataset in separated threads.
     *
     * @param bandIndex the bandIndex from product
     * @return the org.esa.s2tbx.dataio.gdal.drivers.Band object
     * (SIITBX-435)
     */
    private org.esa.s2tbx.dataio.gdal.drivers.Band getGDALBand(int bandIndex) {
        Long threadId = Thread.currentThread().getId();
        if (!this.gdalBands.containsKey(threadId)) {
            this.gdalBands.put(threadId, new HashMap<>());
        }
        if (!this.gdalBands.get(threadId).containsKey(bandIndex)) {
            this.gdalBands.get(threadId).put(bandIndex, this.gdalDataset.getRasterBand(bandIndex + 1));
        }
        return this.gdalBands.get(threadId).get(bandIndex);
    }

    @Override
    public void writeBandRasterData(Band sourceBand, int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, ProductData sourceBuffer, ProgressMonitor pm) {
        Guardian.assertNotNull("sourceBand", sourceBand);
        Guardian.assertNotNull("sourceBuffer", sourceBuffer);
        checkBufferSize(sourceWidth, sourceHeight, sourceBuffer);

        long sourceBandWidth = sourceBand.getRasterWidth();
        long sourceBandHeight = sourceBand.getRasterHeight();
        checkSourceRegionInsideBandRegion(sourceWidth, sourceBandWidth, sourceHeight, sourceBandHeight, sourceOffsetX, sourceOffsetY);

        Product sourceProduct = getSourceProduct();
        int bandIndex = sourceProduct.getBandIndex(sourceBand.getName());// use bandIndex as key instead of Band object for getting org.esa.s2tbx.dataio.gdal.drivers.Band object from the map (SIITBX-435)
        org.esa.s2tbx.dataio.gdal.drivers.Band gdalBand = getGDALBand(bandIndex);// Gets the org.esa.s2tbx.dataio.gdal.drivers.Band object from concurrent map for accessing GDAL bands from dataset in separated threads by threadId and bandIndex (SIITBX-435)
        if (gdalBand == null) {
            throw new NullPointerException("Failed creating the band with index " + bandIndex + " to export the product for driver '" + this.gdalDriver.getLongName() + "'.");
        }

        int result;
        pm.beginTask("Writing band '" + sourceBand.getName() + "'...", sourceHeight);
        try {
            if (this.gdalDataType == GDALConstConstants.gdtByte()) {
                byte[] data = (byte[]) sourceBuffer.getElems();
                result = gdalBand.writeRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == GDALConstConstants.gdtInt16() || this.gdalDataType == GDALConstConstants.gdtUint16()) {
                short[] data = (short[]) sourceBuffer.getElems();
                result = gdalBand.writeRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == GDALConstConstants.gdtInt32() || this.gdalDataType == GDALConstConstants.gdtUint32()) {
                int[] data = (int[]) sourceBuffer.getElems();
                result = gdalBand.writeRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == GDALConstConstants.gdtFloat32()) {
                float[] data = (float[]) sourceBuffer.getElems();
                result = gdalBand.writeRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else if (this.gdalDataType == GDALConstConstants.gdtFloat64()) {
                double[] data = (double[]) sourceBuffer.getElems();
                result = gdalBand.writeRaster(sourceOffsetX, sourceOffsetY, sourceWidth, sourceHeight, this.gdalDataType, data);
            } else {
                throw new IllegalArgumentException("Unknown GDAL data type " + this.gdalDataType + ".");
            }

            if (result != GDALConst.ceNone()) {
                throw new IllegalArgumentException("Failed to write the data for band name '" + sourceBand.getName() + "' and driver '" + this.gdalDriver.getLongName() + "'.");
            }
            pm.worked(1);
        } finally {
            pm.done();
        }
    }

    @Override
    public void flush() {
        //nothing to do
    }

    @Override
    public void close() {
        if (this.gdalDataset != null) {
            this.gdalDataset.delete();
        }
        this.gdalBands.forEach((threadId, gdalBands) -> gdalBands.clear());
        this.gdalBands.clear();
    }

    @Override
    public void deleteOutput() {
        //nothing to do
    }
}
