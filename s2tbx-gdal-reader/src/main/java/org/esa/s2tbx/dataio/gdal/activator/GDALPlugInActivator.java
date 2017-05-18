package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.snap.runtime.Activator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Activator class to install the GDAL library and add the GDAL writer plugin.
 *
 * @author Jean Coravu
 */
public class GDALPlugInActivator implements Activator {
    private static final Logger logger = Logger.getLogger(GDALPlugInActivator.class.getName());

    public GDALPlugInActivator() {
    }

    @Override
    public void start() {
        try {
            GDALDistributionInstaller.install();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
    }
}
