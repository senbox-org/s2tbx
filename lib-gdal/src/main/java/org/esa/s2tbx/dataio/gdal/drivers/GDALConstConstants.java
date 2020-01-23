package org.esa.s2tbx.dataio.gdal.drivers;

/**
 * GDAL gdalconstConstants JNI driver class
 *
 * @author Adrian Drăghici
 */
public class GDALConstConstants {

    /**
     * The name of JNI GDAL gdalconstConstants class
     */
    private static final String CLASS_NAME = "org.gdal.gdalconst.gdalconstConstants";

    /**
     * Creates new instance for this driver
     */
    private GDALConstConstants() {
        //nothing to init
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_Byte() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_Byte() method result
     */
    public static Integer gdtByte() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Byte", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_Int16() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_Int16() method result
     */
    public static Integer gdtInt16() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Int16", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_UInt16() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_UInt16() method result
     */
    public static Integer gdtUint16() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_UInt16", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_Int32() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_Int32() method result
     */
    public static Integer gdtInt32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Int32", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_UInt32() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_UInt32() method result
     */
    public static Integer gdtUint32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_UInt32", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_Float32() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_Float32() method result
     */
    public static Integer gdtFloat32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Float32", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GDT_Float64() method
     *
     * @return the JNI GDAL gdalconstConstants class GDT_Float64() method result
     */
    public static Integer gdtFloat64() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Float64", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GMF_NODATA() method
     *
     * @return the JNI GDAL gdalconstConstants class GMF_NODATA() method result
     */
    public static Integer gmfNodata() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_NODATA", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GMF_PER_DATASET() method
     *
     * @return the JNI GDAL gdalconstConstants class GMF_PER_DATASET() method result
     */
    public static Integer gmfPerDataset() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_PER_DATASET", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GMF_ALPHA() method
     *
     * @return the JNI GDAL gdalconstConstants class GMF_ALPHA() method result
     */
    public static Integer gmfAlpha() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_ALPHA", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GMF_ALL_VALID() method
     *
     * @return the JNI GDAL gdalconstConstants class GMF_ALL_VALID() method result
     */
    public static Integer gmfAllValid() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_ALL_VALID", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class CE_None() method
     *
     * @return the JNI GDAL gdalconstConstants class CE_None() method result
     */
    public static Integer ceNone() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_None", Integer.class);
    }

    /**
     * Calls the JNI GDAL gdalconstConstants class GCI_PaletteIndex() method
     *
     * @return the JNI GDAL gdalconstConstants class GCI_PaletteIndex() method result
     */
    public static Integer gciPaletteindex() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GCI_PaletteIndex", Integer.class);
    }
}
