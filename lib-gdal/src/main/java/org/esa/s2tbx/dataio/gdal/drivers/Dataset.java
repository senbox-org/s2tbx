package org.esa.s2tbx.dataio.gdal.drivers;

import java.util.Hashtable;

public class Dataset {

    private static final String CLASS_NAME = "org.gdal.gdal.Dataset";

    private Object jniDatasetInstance;

    Dataset(Object jniDatasetInstance) {
        this.jniDatasetInstance = jniDatasetInstance;
    }

    public Integer getRasterXSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterXSize", Integer.class, jniDatasetInstance, new Class[]{}, new Object[]{});
    }

    public Integer getRasterYSize() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterYSize", Integer.class, jniDatasetInstance, new Class[]{}, new Object[]{});
    }

    public Integer getRasterCount() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterCount", Integer.class, jniDatasetInstance, new Class[]{}, new Object[]{});
    }

    public Band getRasterBand(int nBand) {
        Object jniBandInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetRasterBand", Object.class, jniDatasetInstance, new Class[]{int.class}, new Object[]{nBand});
        if (jniBandInstance != null) {
            return new Band(jniBandInstance);
        }
        return null;
    }

    public Integer buildOverviews(String resampling, int[] overviewlist) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "BuildOverviews", Integer.class, jniDatasetInstance, new Class[]{String.class, int[].class}, new Object[]{resampling, overviewlist});
    }

    public void delete() {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "delete", null, jniDatasetInstance, new Class[]{}, new Object[]{});
    }

    public String getProjectionRef() {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetProjectionRef", String.class, jniDatasetInstance, new Class[]{}, new Object[]{});
    }

    public void getGeoTransform(double[] argout) {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetGeoTransform", null, jniDatasetInstance, new Class[]{double[].class}, new Object[]{argout});
    }

    public Driver getDriver() {
        Object jniDriverInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDriver", Object.class, jniDatasetInstance, new Class[]{}, new Object[]{});
        if (jniDriverInstance != null) {
            return new Driver(jniDriverInstance);
        }
        return null;
    }

    public Hashtable getMetadataDict(String pszDomain) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetMetadata_Dict", Hashtable.class, jniDatasetInstance, new Class[]{String.class}, new Object[]{pszDomain});
    }

    public String setProjection(String prj) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "SetProjection", String.class, jniDatasetInstance, new Class[]{String.class}, new Object[]{prj});
    }

}
