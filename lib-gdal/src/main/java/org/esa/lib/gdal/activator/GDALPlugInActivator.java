package org.esa.lib.gdal.activator;

import org.esa.s2tbx.dataio.gdal.GDALLoader;
import org.esa.snap.runtime.Activator;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GDAL Plugin Activator class to install the GDAL library and add the GDAL writer plugin.
 *
 * @author Jean Coravu
 */
public class GDALPlugInActivator implements Activator {
    private static final Logger logger = Logger.getLogger(GDALPlugInActivator.class.getName());

    public GDALPlugInActivator() {
        //nothing to init
    }

    /**
     * Starts the plugin activator
     */
    @Override
    public void start() {
        try {
            Path gdalDistributionBinFolderPath = GDALLoader.getInstance().initGDAL();
            GDALInstallInfo.INSTANCE.setLocations(gdalDistributionBinFolderPath);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Stops the plugin activator
     */
    @Override
    public void stop() {
        //nothing to do
    }
}
