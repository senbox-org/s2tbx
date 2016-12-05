package org.esa.s2tbx.dataio.gdal.activator;

import org.esa.s2tbx.dataio.gdal.*;
import org.esa.snap.core.dataio.*;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.runtime.Activator;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.openide.util.Lookup;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class GDALReaderActivator implements Activator {
    private static final Logger logger = Logger.getLogger(GDALReaderActivator.class.getName());

    public GDALReaderActivator() {
    }

    @Override
    public void start() {
        try {
            GDALInstaller installer = new GDALInstaller();
            installer.install();
            if (GdalInstallInfo.INSTANCE.isPresent()) {
                gdal.AllRegister(); // GDAL init drivers

                List<GDALDriverInfo> writerItems = findWriterDrivers();
                if (writerItems.size() > 0) {
                    GDALDriverInfo[] writers = writerItems.toArray(new GDALDriverInfo[writerItems.size()]);
                    initActions(writers);
                }

            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
    }

    private void initActions(GDALDriverInfo[] writers) throws IOException {
        GDALProductWriterPlugIn plugIn = new GDALProductWriterPlugIn(writers);
        ProductIOPlugInManager.getInstance().addWriterPlugIn(plugIn);
        ActionRegistrationService ars = Lookup.getDefault().lookup(ActionRegistrationService.class);
        String actionDisplayName = "GDAL";
        AbstractAction action = new WriterPlugInExportProductAction();
        action.putValue(Action.NAME, actionDisplayName);
        action.putValue("displayName", actionDisplayName);
        String menuPath = "Menu/File/Export";
        String category = "GDAL";
        ars.registerAction(category, menuPath, action);
    }

    private List<GDALDriverInfo> findWriterDrivers() {
        List<GDALDriverInfo> writerItems = new ArrayList<>();
        int count = gdal.GetDriverCount();
        for (int i = 0; i < count; i++) {
            try {
                Driver driver = gdal.GetDriver(i);

                Map<Object, Object> map = driver.GetMetadata_Dict();
                Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
                String driverName = null;
                String driverDisplayName = null;
                String extensionName = null;
                String creationDataTypes = null;

                //System.out.println("\n -------- driver name="+driver.getShortName()+"  size="+map.size()+" -------");

                while (it.hasNext()) {// && (extensionName == null || driverName == null || driverDisplayName == null)) {
                    Map.Entry<Object, Object> entry = it.next();
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    if ("DCAP_CREATE".equalsIgnoreCase(key) && "YES".equalsIgnoreCase(value)) {
                        driverName = driver.getShortName();
                        driverDisplayName = driver.getLongName();
                    }
                    if ("DMD_EXTENSION".equalsIgnoreCase(key) && !StringUtils.isNullOrEmpty(value)) {
                        extensionName = value;
                    }
                    if ("DMD_CREATIONDATATYPES".equalsIgnoreCase(key) && !StringUtils.isNullOrEmpty(value)) {
                        creationDataTypes = value;
                    }
                    //System.out.println(" key="+key+"  value="+value);
                }
                if (extensionName != null && driverName != null && driverDisplayName != null) {
                    writerItems.add(new GDALDriverInfo("." + extensionName, driverName, driverDisplayName, creationDataTypes));
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return writerItems;
    }
}
