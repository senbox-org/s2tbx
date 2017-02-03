package org.esa.s2tbx.dataio.gdal;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by kraftek on 11/15/2016.
 */
public enum GdalInstallInfo {
    INSTANCE;

    private Path binLocation;
    private GDALWriterPlugInListener listener;

    synchronized void setLocations(Path binLocation, Path appsLocation, Path driversLocation, Path dataLocation) {
        this.binLocation = binLocation;
        fireListener();
    }

    public synchronized void setListener(GDALWriterPlugInListener listener) {
        this.listener = listener;
        fireListener();
    }

    private void fireListener() {
        if (this.listener != null && isPresent()) {
            this.listener.writeDriversSuccessfullyInstalled();
            this.listener = null;
        }
    }

    public boolean isPresent() {
        return this.binLocation != null && Files.exists(this.binLocation);
    }
}
