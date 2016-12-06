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

    public static List<GDALDriverInfo> findWriterDrivers() {
        Set<String> driversToIgnore = new HashSet<String>();
        driversToIgnore.add("VRT"); // Writing through VRTSourcedRasterBand is not supported.
        driversToIgnore.add("JML");
        driversToIgnore.add("XLSX");
        driversToIgnore.add("ODS");
        driversToIgnore.add("PGDUMP");
        driversToIgnore.add("DXF");
        driversToIgnore.add("MBTiles"); // IWriteBlock() not supported if georeferencing not set
        driversToIgnore.add("WAsP");
        driversToIgnore.add("OGR_GMT");
        driversToIgnore.add("Leveller"); // MINUSERPIXELVALUE must be specified.
        driversToIgnore.add("Terragen"); // Inverted, flat, or unspecified span for Terragen file.
        driversToIgnore.add("NTv2"); // Attempt to create NTv2 file with unsupported band number '1'.
        driversToIgnore.add("ADRG"); // ADRG driver doesn't support 1 bands. Must be 3 (rgb) bands.
        driversToIgnore.add("KML"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GPX"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GML"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("CSV"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("BNA"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("DGN"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("S57"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("ESRI Shapefile"); // Dataset does not support the AddBand() method.
        driversToIgnore.add("GPKG"); // Raster table tempFile not correctly initialized due to missing call to SetGeoTransform()
        driversToIgnore.add("PCRaster"); // PCRaster driver: value scale can not be determined; specify PCRASTER_VALUESCALE.

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
                        if (value.contains(" ")) {
                            int index = value.indexOf(" ");
                            extensionName = value.substring(0, index).trim();
                        } else if (value.contains("/")) {
                            int index = value.indexOf("/");
                            extensionName = value.substring(0, index).trim();
                        } else {
                            extensionName = value;
                        }
                    }
                    if ("DMD_CREATIONDATATYPES".equalsIgnoreCase(key) && !StringUtils.isNullOrEmpty(value)) {
                        creationDataTypes = value;
                    }
                    //System.out.println(" key="+key+"  value="+value);
                }
                if ("Leveller".equalsIgnoreCase(driverName) && creationDataTypes == null) {
                    creationDataTypes = "Float32"; // The band type is always Float32. (http://www.gdal.org/frmt_leveller.html)
                }
                if (extensionName != null && driverName != null && driverDisplayName != null) {
                    if (!driversToIgnore.contains(driverName)) {
                        writerItems.add(new GDALDriverInfo("." + extensionName, driverName, driverDisplayName, creationDataTypes));
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return writerItems;
    }
}
