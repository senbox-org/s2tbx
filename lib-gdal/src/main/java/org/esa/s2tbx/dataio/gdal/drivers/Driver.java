package org.esa.s2tbx.dataio.gdal.drivers;

public class Driver {

    private static final String CLASS_NAME = "org.gdal.gdal.Driver";

    private Object jniDriverInstance;

    Driver(Object jniDriverInstance) {
        this.jniDriverInstance = jniDriverInstance;
    }

    public String getShortName() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "getShortName", String.class, jniDriverInstance, new Class[]{}, new Object[]{});
    }

    public String getLongName() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "getLongName", String.class, jniDriverInstance, new Class[]{}, new Object[]{});
    }

    public Dataset create(String utf8Path, int xsize, int ysize, int bands, int eType) {
        Object jniDatasetInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "Create", Object.class, jniDriverInstance, new Class[]{String.class, int.class, int.class, int.class, int.class}, new Object[]{utf8Path, xsize, xsize, ysize, bands, eType});
        if (jniDatasetInstance != null) {
            return new Dataset(jniDatasetInstance);
        }
        return null;
    }
}
