package org.esa.s2tbx.dataio.gdal.drivers;

public class GDALConstConstants {

    private static final String CLASS_NAME = "org.gdal.gdalconst.gdalconstConstants";

    private GDALConstConstants() {
        //nothing to init
    }

    public static Integer GDT_Byte() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Byte", Integer.class);
    }

    public static Integer GDT_Int16() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Int16", Integer.class);
    }

    public static Integer GDT_UInt16() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_UInt16", Integer.class);
    }

    public static Integer GDT_Int32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Int32", Integer.class);
    }

    public static Integer GDT_UInt32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_UInt32", Integer.class);
    }

    public static Integer GDT_Float32() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Float32", Integer.class);
    }

    public static Integer GDT_Float64() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GDT_Float64", Integer.class);
    }

    public static Integer GMF_NODATA() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_NODATA", Integer.class);
    }

    public static Integer GMF_PER_DATASET() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_PER_DATASET", Integer.class);
    }

    public static Integer GMF_ALPHA() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_ALPHA", Integer.class);
    }

    public static Integer GMF_ALL_VALID() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GMF_ALL_VALID", Integer.class);
    }

    public static Integer CE_None() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_None", Integer.class);
    }

    public static Integer GCI_PaletteIndex() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GCI_PaletteIndex", Integer.class);
    }
}
