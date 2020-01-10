package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.drivers.GDAL;
import org.esa.s2tbx.dataio.gdal.drivers.GDALConstConstants;
import org.esa.snap.core.datamodel.ProductData;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GDALLoader {

    private static final GDALLoader INSTANCE = new GDALLoader();
    private static final Logger logger = Logger.getLogger(GDALLoader.class.getName());

    private boolean ready = false;
    private GDALVersion gdalVersion;
    private URLClassLoader gdalVersionLoader;
    private Path gdalJNILocation;

    private Map<Integer, Integer> bandToGDALDataTypes;

    private GDALLoader() {

    }

    public static GDALLoader getInstance() {
        return INSTANCE;
    }

    public Path initGDAL() throws IOException {
        if (!ready) {
            gdalVersion = GDALVersion.getGDALVersion();
            gdalJNILocation = GDALDistributionInstaller.setupJNI(gdalVersion);
            gdalVersionLoader = new URLClassLoader(new URL[]{gdalVersion.getJNILibraryFilePath().toUri().toURL()}, GDALLoader.class.getClassLoader());
            ready = true;
            initDrivers();
            postGDALInit();
        }
        return gdalJNILocation;
    }

    private void postGDALInit() {
        bandToGDALDataTypes = new HashMap<>();
        bandToGDALDataTypes.put(ProductData.TYPE_UINT8, GDALConstConstants.GDT_Byte());
        bandToGDALDataTypes.put(ProductData.TYPE_UINT16, GDALConstConstants.GDT_Int16());
        bandToGDALDataTypes.put(ProductData.TYPE_INT32, GDALConstConstants.GDT_Int32());
        bandToGDALDataTypes.put(ProductData.TYPE_UINT32, GDALConstConstants.GDT_UInt32());
        bandToGDALDataTypes.put(ProductData.TYPE_FLOAT32, GDALConstConstants.GDT_Float32());
        bandToGDALDataTypes.put(ProductData.TYPE_FLOAT64, GDALConstConstants.GDT_Float64());
    }

    public URLClassLoader getGDALVersionLoader() {
        if (!ready) {
            throw new IllegalStateException("GDAL Loader not ready.");
        }
        return gdalVersionLoader;
    }

    /**
     * Get the GDAL data type corresponding to the data type of a band.
     *
     * @param bandDataType The data type of the band to convert to the GDAL data type
     * @return The GDAL data type
     */
    public int getGDALDataType(int bandDataType) {
        if (!ready) {
            throw new IllegalStateException("GDAL library not initialized");
        }
        Integer gdalResult = bandToGDALDataTypes.get(bandDataType);
        if (gdalResult != null) {
            return gdalResult;
        }
        throw new IllegalArgumentException("Unknown band data type " + bandDataType + ".");
    }

    /**
     * Get the data type of the band corresponding to the GDAL data type.
     *
     * @param gdalDataType The GDAL data type to convert to the data type of the band
     * @return The data type of the band
     */
    public int getBandDataType(int gdalDataType) {
        if (!ready) {
            throw new IllegalStateException("GDAL library not initialized");
        }
        for (Map.Entry<Integer, Integer> entry : bandToGDALDataTypes.entrySet()) {
            if (entry.getValue() == gdalDataType) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unknown band data type " + gdalDataType + ".");
    }

    /**
     * Init the drivers if the GDAL library is installed.
     */
    private void initDrivers() {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Init the GDAL drivers on " + gdalVersion.getOsCategory().getOperatingSystemName() + ".");
        }
        GDAL.allRegister();// GDAL init drivers
    }
}
