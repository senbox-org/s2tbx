package org.esa.s2tbx.dataio.gdal.drivers;

import java.awt.image.IndexColorModel;

public class ColorTable {

    private static final String CLASS_NAME = "org.gdal.gdal.ColorTable";

    private Object jniColorTable;

    ColorTable(Object jniColorTable) {
        this.jniColorTable = jniColorTable;
    }

    public IndexColorModel getIndexColorModel(int bits) {
        return GDALReflection.callGDALLibraryMethod(CLASS_NAME, "GetIndexColorModel", IndexColorModel.class, jniColorTable, new Class[]{int.class}, new Object[]{bits});
    }
}
