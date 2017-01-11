package org.esa.s2tbx.dataio.gdal;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by kraftek on 11/15/2016.
 */
public enum GdalInstallInfo {
    INSTANCE;

    private Path binLocation;
    private Path driversLocation;
    private Path dataLocation;
    private Path appsLocation;
    private String[] fileTypes;
    private String[] extensions;
    private GDALWriterPlugInListener listener;
    private Integer writeDriverCount;

    void setLocations(Path binLocation, Path appsLocation, Path driversLocation, Path dataLocation) {
        this.binLocation = binLocation;
        this.appsLocation = appsLocation;
        this.driversLocation = driversLocation;
        this.dataLocation = dataLocation;
        fireListener();
    }

    public synchronized void setListener(GDALWriterPlugInListener listener) {
        this.listener = listener;
        fireListener();
    }

    public synchronized void setWriteDriverCount(int writeDriverCount) {
        this.writeDriverCount = writeDriverCount;
        fireListener();
    }

    private void fireListener() {
        if (this.writeDriverCount != null && this.listener != null) {
            this.listener.writeDriversSuccessfullyInstalled();
            this.listener = null;
            this.writeDriverCount = null;
        }
    }

    public Path getBinLocation() {
        return binLocation;
    }

    public Path getDriversLocation() { return driversLocation; }

    public Path getAppsLocation() { return appsLocation; }

    public Path getDataLocation() { return dataLocation; }

    public boolean isPresent() {
        return binLocation != null && Files.exists(binLocation);
    }

    public String[] getFormatNames() { return fileTypes; }

    public String[] getExtensions() { return extensions; }
}
