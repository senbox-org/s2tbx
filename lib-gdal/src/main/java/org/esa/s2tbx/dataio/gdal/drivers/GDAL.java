package org.esa.s2tbx.dataio.gdal.drivers;

public class GDAL {

    private static final String CLASS_NAME = "org.gdal.gdal.gdal";

    private GDAL() {
        //nothing to init
    }

    public static void allRegister() {
        GDALReflection.callGDALLibraryMethod(CLASS_NAME, "AllRegister", null, null, new Class[]{}, new Object[]{});
    }

    public static String getDataTypeName(int gdalDataType) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDataTypeName", String.class, null, new Class[]{int.class}, new Object[]{gdalDataType});
    }

    public static Integer getDataTypeByName(String pszDataTypeName) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDataTypeByName", Integer.class, null, new Class[]{String.class}, new Object[]{pszDataTypeName});
    }

    public static Dataset open(String utf8Path, int eAccess) {
        Object jniDatasetInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "Open", Object.class, null, new Class[]{String.class, int.class}, new Object[]{utf8Path, eAccess});
        if (jniDatasetInstance != null) {
            return new Dataset(jniDatasetInstance);
        }
        return null;
    }

    public static String getColorInterpretationName(int eColorInterp) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetColorInterpretationName", String.class, null, new Class[]{int.class}, new Object[]{eColorInterp});
    }

    public static Integer getDataTypeSize(int eDataType) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDataTypeSize", Integer.class, null, new Class[]{int.class}, new Object[]{eDataType});
    }

    public static Driver getDriverByName(String name) {
        Object jniDriverInstance = GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetDriverByName", Object.class, null, new Class[]{String.class}, new Object[]{name});
        if (jniDriverInstance != null) {
            return new Driver(jniDriverInstance);
        }
        return null;
    }
}
