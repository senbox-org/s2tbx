package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.s2tbx.dataio.gdal.GDALLoader;
import org.esa.snap.runtime.Activator;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Activator class to installDistribution the GDAL library and add the GDAL writer plugin.
 *
 * @author Jean Coravu
 */
public class GDALPlugInActivator implements Activator {
    private static final Logger logger = Logger.getLogger(GDALPlugInActivator.class.getName());

    public GDALPlugInActivator() {
        //nothing to init
    }

    @Override
    public void start() {
        try {
            Path gdalDistributionRootFolderPath = GDALLoader.getInstance().initGDAL();
            GDALInstallInfo.INSTANCE.setLocations(gdalDistributionRootFolderPath);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        //nothing to do
    }
}
