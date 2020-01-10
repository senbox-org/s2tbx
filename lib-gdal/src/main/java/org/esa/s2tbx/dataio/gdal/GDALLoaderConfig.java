package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.runtime.Config;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class GDALLoaderConfig {

    private static final String INSTANCE_CONFIG = "s2tbx";
    private static final String PREFERENCE_KEY_USE_INSTALLED_GDAL = "s2tbx.dataio.gdal.installed";

    private static final Logger logger = Logger.getLogger(GDALInstaller.class.getName());
    private static final GDALLoaderConfig instance = new GDALLoaderConfig();

    private boolean useInstalledGDALLibrary;

    private GDALLoaderConfig() {
        useInstalledGDALLibrary = getUseInstalledGDALLibrary();
    }

    public static GDALLoaderConfig getInstance() {
        return instance;
    }

    private boolean getUseInstalledGDALLibrary() {
        final Preferences preferences = Config.instance(INSTANCE_CONFIG).load().preferences();
        return preferences.getBoolean(PREFERENCE_KEY_USE_INSTALLED_GDAL, true);
    }

    public void setUseInstalledGDALLibrary(boolean useInstalledGDALLibrary) {
        this.useInstalledGDALLibrary = useInstalledGDALLibrary;
        final Preferences preferences = Config.instance(INSTANCE_CONFIG).load().preferences();
        preferences.putBoolean(PREFERENCE_KEY_USE_INSTALLED_GDAL, this.useInstalledGDALLibrary);
        try {
            preferences.flush();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public boolean useInstalledGDALLibrary() {
        return useInstalledGDALLibrary;
    }

}
