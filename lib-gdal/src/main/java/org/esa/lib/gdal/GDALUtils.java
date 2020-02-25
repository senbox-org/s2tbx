package org.esa.lib.gdal;

import org.esa.snap.core.datamodel.ProductData;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.util.*;

/**
 * Utility class containing static methods.
 *
 * @author Jean Coravu
 */
public class GDALUtils {

    private static final Map<Integer, Integer> BAND_TO_GDAL_DATA_TYPES;
    static {
        BAND_TO_GDAL_DATA_TYPES = new HashMap<Integer, Integer>();
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_UINT8, gdalconstConstants.GDT_Byte);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_INT16, gdalconstConstants.GDT_Int16);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_UINT16, gdalconstConstants.GDT_UInt16);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_INT32, gdalconstConstants.GDT_Int32);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_UINT32, gdalconstConstants.GDT_UInt32);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_FLOAT32, gdalconstConstants.GDT_Float32);
        BAND_TO_GDAL_DATA_TYPES.put(ProductData.TYPE_FLOAT64, gdalconstConstants.GDT_Float64);
    }

    /**
     * Get the GDAL data type corresponding to the data type of a band.
     *
     * @param bandDataType  The data type of the band to convert to the GDAL data type
     *
     * @return              The GDAL data type
     */
    public static int getGDALDataType(int bandDataType) {
        Integer gdalResult = BAND_TO_GDAL_DATA_TYPES.get(bandDataType);
        if (gdalResult != null) {
            return gdalResult.intValue();
        }
        throw new IllegalArgumentException("Unknown band data type " + bandDataType + ".");
    }

    /**
     * Get the data type of the band corresponding to the GDAL data type.
     *
     * @param gdalDataType  The GDAL data type to convert to the data type of the band
     *
     * @return              The data type of the band
     */
    public static int getBandDataType(int gdalDataType) {
        Iterator<Map.Entry<Integer, Integer>> it = BAND_TO_GDAL_DATA_TYPES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            if (entry.getValue().intValue() == gdalDataType) {
                return entry.getKey().intValue();
            }
        }
        throw new IllegalArgumentException("Unknown band data type " + gdalDataType + ".");
    }

    /**
     *  Init the drivers if the GDAL library is installed.
     */
    public static void initDrivers() {
        gdal.AllRegister(); // GDAL init drivers
    }
}
