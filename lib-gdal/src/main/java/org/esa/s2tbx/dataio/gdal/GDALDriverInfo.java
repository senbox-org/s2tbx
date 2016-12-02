package org.esa.s2tbx.dataio.gdal;

/**
 * @author Jean Coravu
 */
public class GDALDriverInfo {
    private final String extensionName;
    private final String driverName;
    private final String driverDisplayName;

    public GDALDriverInfo(String extensionName, String driverName, String driverDisplayName) {
        this.extensionName = extensionName;
        this.driverName = driverName;
        this.driverDisplayName = driverDisplayName;
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
}
