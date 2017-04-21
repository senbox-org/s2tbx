package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.runtime.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by kraftek on 11/15/2016.
 */
public enum GdalInstallInfo {
    INSTANCE;

    private Path binLocation;
    private GDALWriterPlugInListener listener;

    public synchronized void setLocations(Path binLocation) {
        this.binLocation = binLocation;
        try {
            Config config = Config.instance("s2tbx");
            config.load();
            Preferences preferences = config.preferences();
            preferences.put("gdal.apps.path",
                            this.binLocation.resolve("bin")
                                            .resolve("gdal")
                                            .resolve("apps")
                                            .toString());
            preferences.flush();
        } catch (BackingStoreException exception) {
            // ignore exception
        }
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

    public Path getLocation() { return this.binLocation; }
}
