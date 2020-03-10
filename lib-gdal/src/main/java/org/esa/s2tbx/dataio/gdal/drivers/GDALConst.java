package org.esa.s2tbx.dataio.gdal.drivers;

/**
 * GDAL gdalconst JNI driver class
 *
 * @author Adrian Drăghici
 */
public class GDALConst {

    /**
     * The name of JNI GDAL gdalconst class
     */
    private static final String CLASS_NAME = "org.gdal.gdalconst.gdalconst";

    /**
     * Creates new instance for this driver
     */
    private GDALConst() {
        //nothing to init
    }

    /**
     * Calls the JNI GDAL gdalconst class GA_ReadOnly() method
     *
     * @return the JNI GDAL gdalconst class GA_ReadOnly() method result
     */
    public static Integer gaReadonly() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GA_ReadOnly", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconst class CE_Failure() method
     *
     * @return the JNI GDAL gdalconst class CE_Failure() method result
     */
    public static Integer ceFailure() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_Failure", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconst class CE_None() method
     *
     * @return the JNI GDAL gdalconst class CE_None() method result
     */
    public static Integer ceNone() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_None", Integer.class);
    }
}
