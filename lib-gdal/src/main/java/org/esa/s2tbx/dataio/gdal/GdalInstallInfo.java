package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.core.util.SystemUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    public Path getBinLocation() {
        return binLocation;
    }

    void setBinLocation(Path binLocation) { this.binLocation = binLocation; }

    public Path getDriversLocation() { return driversLocation; }
    void setDriversLocation(Path driversLocation) { this.driversLocation = driversLocation; }

    public Path getAppsLocation() { return appsLocation; }
    void setAppsLocation(Path appsLocation) { this.appsLocation = appsLocation; }

    public Path getDataLocation() { return dataLocation; }
    void setDataLocation(Path dataLocation) { this.dataLocation = dataLocation; }

    public boolean isPresent() {
        return binLocation != null && Files.exists(binLocation);
    }

    public String[] getFormatNames() { return fileTypes; }

    public String[] getExtensions() { return extensions; }
}
