package org.esa.s2tbx.dataio.gdal.activator;

import org.gdal.gdal.gdal;

import java.text.MessageFormat;

/**
 * @author Jean Coravu
 */
public class GDALDriverInfo {
    private final String extensionName;
    private final String driverName;
    private final String driverDisplayName;
    private final String creationDataTypes;

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

    public boolean canExportProduct(int gdalDataType) {
        boolean allowedDataType = true;
        String gdalDataTypeName = gdal.GetDataTypeName(gdalDataType);
        if (this.creationDataTypes != null) {
            allowedDataType = this.creationDataTypes.contains(gdalDataTypeName);
        }
        return allowedDataType;
    }

    public String getFailedMessageToExportProduct(int gdalDataType, String separator) {
        String gdalDataTypeName = gdal.GetDataTypeName(gdalDataType);
        return MessageFormat.format("The GDAL driver ''{0}'' does not support the data type ''{1}'' to create a new product." + separator +
                        "The available types are ''{2}''." ,
                this.driverDisplayName, gdalDataTypeName, this.creationDataTypes);
    }
}
