package org.esa.s2tbx.dataio.gdal.drivers;

import java.nio.ByteBuffer;

public class Band {

    private static final String CLASS_NAME = "org.gdal.gdal.Band";

    private Object jniBandInstance;

    public Band(Object jniBandInstance) {
        this.jniBandInstance = jniBandInstance;
    }

    public Integer getDataType() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "getDataType", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getBlockXSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetBlockXSize", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getBlockYSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetBlockYSize", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getXSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetXSize", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getYSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetYSize", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getOverviewCount() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetOverviewCount", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer getRasterColorInterpretation() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterColorInterpretation", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public ColorTable getRasterColorTable() {
        Object jniColorTable = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterColorTable", Object.class, jniBandInstance, new Class[]{}, new Object[]{});
        if (jniColorTable != null) {
            return new ColorTable(jniColorTable);
        }
        return null;
    }

    public String getDescription() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDescription", String.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Band getOverview(int i) {
        Object newJNIBandInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetOverview", Object.class, jniBandInstance, new Class[]{int.class}, new Object[]{i});
        if (newJNIBandInstance != null) {
            return new Band(newJNIBandInstance);
        }
        return null;
    }

    public void getOffset(Double[] val) {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetOffset", null, jniBandInstance, new Class[]{double[].class}, new Object[]{val});
    }

    public void getScale(Double[] val) {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetScale", null, jniBandInstance, new Class[]{double[].class}, new Object[]{val});
    }

    public String getUnitType() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetUnitType", String.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public void getNoDataValue(Double[] val) {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetNoDataValue", null, jniBandInstance, new Class[]{double[].class}, new Object[]{val});
    }

    public Band getMaskBand() {
        Object newJNIBandInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetMaskBand", Object.class, jniBandInstance, new Class[]{}, new Object[]{});
        if (newJNIBandInstance != null) {
            return new Band(newJNIBandInstance);
        }
        return null;
    }

    public Integer getMaskFlags() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetMaskFlags", Integer.class, jniBandInstance, new Class[]{}, new Object[]{});
    }

    public Integer readBlockDirect(int nXBlockOff, int nYBlockOff, ByteBuffer nioBuffer) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "ReadBlock_Direct", Integer.class, jniBandInstance, new Class[]{int.class, int.class, ByteBuffer.class}, new Object[]{nXBlockOff, nYBlockOff, nioBuffer});
    }

    public Integer writeRaster(int xoff, int yoff, int xsize, int ysize, int bufType, byte[] array) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "WriteRaster", Integer.class, jniBandInstance, new Class[]{int.class, int.class, int.class, int.class, int.class, byte[].class}, new Object[]{xoff, yoff, xsize, ysize, bufType, array});
    }

    public Integer writeRaster(int xoff, int yoff, int xsize, int ysize, int bufType, short[] array) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "WriteRaster", Integer.class, jniBandInstance, new Class[]{int.class, int.class, int.class, int.class, int.class, short[].class}, new Object[]{xoff, yoff, xsize, ysize, bufType, array});
    }

    public Integer writeRaster(int xoff, int yoff, int xsize, int ysize, int bufType, int[] array) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "WriteRaster", Integer.class, jniBandInstance, new Class[]{int.class, int.class, int.class, int.class, int.class, int[].class}, new Object[]{xoff, yoff, xsize, ysize, bufType, array});
    }

    public Integer writeRaster(int xoff, int yoff, int xsize, int ysize, int bufType, float[] array) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "WriteRaster", Integer.class, jniBandInstance, new Class[]{int.class, int.class, int.class, int.class, int.class, float[].class}, new Object[]{xoff, yoff, xsize, ysize, bufType, array});
    }

    public Integer writeRaster(int xoff, int yoff, int xsize, int ysize, int bufType, double[] array) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "WriteRaster", Integer.class, jniBandInstance, new Class[]{int.class, int.class, int.class, int.class, int.class, double[].class}, new Object[]{xoff, yoff, xsize, ysize, bufType, array});
    }
}
