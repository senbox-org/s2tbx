package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.s2tbx.dataio.gdal.drivers.GDAL;

/**
 * Simple class containing information about a GDAL driver.
 *
 * @author Jean Coravu
 */
public class GDALDriverInfo {
    private final String extensionName;
    private final String driverName;
    private final String driverDisplayName;
    private final String creationDataTypes;

    /**
     * @param extensionName     The driver extension name
     * @param driverName        The driver name
     * @param driverDisplayName The driver display name
     * @param creationDataTypes The data types used to create a band (ex: Byte Int16 UInt16 Int32 UInt32 Float32 Float64)
     */
    public GDALDriverInfo(String extensionName, String driverName, String driverDisplayName, String creationDataTypes) {
        this.extensionName = extensionName;
        this.driverName = driverName;
        this.driverDisplayName = driverDisplayName;
        this.creationDataTypes = creationDataTypes;
    }

    public String getDriverDisplayName() {
        return driverDisplayName;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getCreationDataTypes() {
        return creationDataTypes;
    }

    /**
     * Check if the available creation data types of the driver contains the GDAL data type.
     *
     * @param gdalDataType The GDAl data type to check
     * @return true if the driver can export the product containing the specified data type; false otherwise
     */
    public boolean canExportProduct(int gdalDataType) {
        boolean allowedDataType = true;
        String gdalDataTypeName = GDAL.getDataTypeName(gdalDataType);
        if (this.creationDataTypes != null) {
            allowedDataType = this.creationDataTypes.contains(gdalDataTypeName);
        }
        return allowedDataType;
    }

    public final String getWriterPluginFormatName() {
        return "GDAL-" + this.driverName + "-WRITER";
    }
}
