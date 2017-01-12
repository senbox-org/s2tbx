package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.s2tbx.dataio.gdal.GDALInstaller;
import org.esa.s2tbx.dataio.gdal.GDALProductWriterPlugIn;
import org.esa.s2tbx.dataio.gdal.GDALUtils;
import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.runtime.Activator;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;

import java.util.*;
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
            GDALInstaller installer = new GDALInstaller();
            installer.install();

            GDALDriverInfo[] writerDrivers = GDALUtils.loadAvailableWriterDrivers();
            if (writerDrivers != null && writerDrivers.length > 0) {
                GDALProductWriterPlugIn plugIn = new GDALProductWriterPlugIn(writerDrivers);
                ProductIOPlugInManager.getInstance().addWriterPlugIn(plugIn);

                GdalInstallInfo.INSTANCE.setWriteDriverCount(writerDrivers.length);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
    }
}
