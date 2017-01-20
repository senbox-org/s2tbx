package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.StringUtils;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class containing static methods.
 *
 * @author Jean Coravu
 */
public class GDALUtils {
    private static final Logger logger = Logger.getLogger(GDALUtils.class.getName());

    private static final Map<Integer, Integer> bandToGDALDataTypes;
    static {
        bandToGDALDataTypes = new HashMap<Integer, Integer>();
        bandToGDALDataTypes.put(ProductData.TYPE_UINT8, gdalconstConstants.GDT_Byte);
        bandToGDALDataTypes.put(ProductData.TYPE_INT16, gdalconstConstants.GDT_Int16);
        bandToGDALDataTypes.put(ProductData.TYPE_UINT16, gdalconstConstants.GDT_UInt16);
        bandToGDALDataTypes.put(ProductData.TYPE_INT32, gdalconstConstants.GDT_Int32);
        bandToGDALDataTypes.put(ProductData.TYPE_UINT32, gdalconstConstants.GDT_UInt32);
        bandToGDALDataTypes.put(ProductData.TYPE_FLOAT32, gdalconstConstants.GDT_Float32);
        bandToGDALDataTypes.put(ProductData.TYPE_FLOAT64, gdalconstConstants.GDT_Float64);
    }

    /**
     * Get the GDAL data type corresponding to the data type of a band.
     *
     * @param bandDataType  The data type of the band to convert to the GDAL data type
     *
     * @return              The GDAL data type
     */
    public static int getGDALDataType(int bandDataType) {
        Integer gdalResult = bandToGDALDataTypes.get(bandDataType);
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
        Iterator<Map.Entry<Integer, Integer>> it = bandToGDALDataTypes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            if (entry.getValue().intValue() == gdalDataType) {
                return entry.getKey().intValue();
            }
        }
        throw new IllegalArgumentException("Unknown band data type " + gdalDataType + ".");
    }

    /**
     *  Load the writer drivers if the GDAL library is installed.
     *
     * @return  The GDAL writer drivers
     */
    public static GDALDriverInfo[] loadAvailableWriterDrivers() {
        gdal.AllRegister(); // GDAL init drivers

        Set<String> driversToIgnore = new HashSet<String>();
        driversToIgnore.add("VRT"); // Writing through VRTSourcedRasterBand is not supported.
        driversToIgnore.add("JML");
        driversToIgnore.add("XLSX");
        driversToIgnore.add("ODS");
        driversToIgnore.add("PGDUMP");
        driversToIgnore.add("DXF");
        driversToIgnore.add("MBTiles"); // IWriteBlock() not supported if georeferencing not set
        driversToIgnore.add("WAsP");
        driversToIgnore.add("OGR_GMT");
        driversToIgnore.add("Leveller"); // MINUSERPIXELVALUE must be specified.
        driversToIgnore.add("Terragen"); // Inverted, flat, or unspecified span for Terragen file.
        driversToIgnore.add("NTv2"); // Attempt to create NTv2 file with unsupported band number '1'.
        driversToIgnore.add("ADRG"); // ADRG driver doesn't support 1 bands. Must be 3 (rgb) bands.
        driversToIgnore.add("KML"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GPX"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GML"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("CSV"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("BNA"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("DGN"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("S57"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("ESRI Shapefile"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GPKG"); // Raster table tempFile not correctly initialized due to missing call to SetGeoTransform()
        driversToIgnore.add("PCRaster"); // PCRaster driver: value scale can not be determined; specify PCRASTER_VALUESCALE.

        List<GDALDriverInfo> writerItems = new ArrayList<>();
        int count = gdal.GetDriverCount();
        for (int i = 0; i < count; i++) {
            try {
                Driver driver = gdal.GetDriver(i);

                Map<Object, Object> map = driver.GetMetadata_Dict();
                Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
                String driverName = null;
                String driverDisplayName = null;
                String extensionName = null;
                String creationDataTypes = null;

                while (it.hasNext()) {// && (extensionName == null || driverName == null || driverDisplayName == null)) {
                    Map.Entry<Object, Object> entry = it.next();
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    if ("DCAP_CREATE".equalsIgnoreCase(key) && "YES".equalsIgnoreCase(value)) {
                        driverName = driver.getShortName();
                        driverDisplayName = driver.getLongName();
                    }
                    if ("DMD_EXTENSION".equalsIgnoreCase(key) && !StringUtils.isNullOrEmpty(value)) {
                        if (value.contains(" ")) {
                            int index = value.indexOf(" ");
                            extensionName = value.substring(0, index).trim();
                        } else if (value.contains("/")) {
                            int index = value.indexOf("/");
                            extensionName = value.substring(0, index).trim();
                        } else {
                            extensionName = value;
                        }
                    }
                    if ("DMD_CREATIONDATATYPES".equalsIgnoreCase(key) && !StringUtils.isNullOrEmpty(value)) {
                        creationDataTypes = value;
                    }
                }
                if ("Leveller".equalsIgnoreCase(driverName) && creationDataTypes == null) {
                    creationDataTypes = "Float32"; // the band type is always Float32. (http://www.gdal.org/frmt_leveller.html)
                }
                if (extensionName != null && driverName != null && driverDisplayName != null) {
                    if (!driversToIgnore.contains(driverName)) {
                        writerItems.add(new GDALDriverInfo("." + extensionName, driverName, driverDisplayName, creationDataTypes));
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return writerItems.toArray(new GDALDriverInfo[writerItems.size()]);
    }
}
