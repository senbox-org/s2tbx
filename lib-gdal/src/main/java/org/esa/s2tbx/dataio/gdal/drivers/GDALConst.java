package org.esa.s2tbx.dataio.gdal.drivers;

public class GDALConst {

    private static final String CLASS_NAME = "org.gdal.gdalconst.gdalconst";

    private GDALConst() {
        //nothing to init
    }

    public static Integer GA_ReadOnly() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "GA_ReadOnly", Integer.class);
    }

    public static Integer CE_Failure() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_Failure", Integer.class);
    }

    public static Integer CE_None() {
        return GDALReflection.fetchGDALLibraryConstant(CLASS_NAME, "CE_None", Integer.class);
    }
}
