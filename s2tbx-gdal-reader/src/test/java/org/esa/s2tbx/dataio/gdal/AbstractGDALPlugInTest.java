package org.esa.s2tbx.dataio.gdal;

import junit.framework.TestCase;
import org.esa.snap.utils.NativeLibraryUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jean Coravu
 */
public abstract class AbstractGDALPlugInTest extends TestCase {

    protected AbstractGDALPlugInTest() {
    }

    @Override
    protected void setUp() throws Exception {
        checkJNILibsDirectoryExists();
        checkGDALBinDirectoryExists();
    }

    private void checkGDALBinDirectoryExists() {
        String gdalBinPropertyName = "gdal.bin.dir";

        String gdalBinDirectoryPathProperty = System.getProperty(gdalBinPropertyName);
        assertNotNull("The system property '" + gdalBinPropertyName + "' representing the directory containing the libraries is not set.", gdalBinDirectoryPathProperty);
        File gdalBinFolder = new File(gdalBinDirectoryPathProperty);
        if (!gdalBinFolder.isDirectory()) {
            fail("The directory path containing the libraries '"+gdalBinDirectoryPathProperty+"' is not valid.");
        }

        Path gdalBinPath = Paths.get(gdalBinDirectoryPathProperty);
        GdalInstallInfo.INSTANCE.setBinLocation(gdalBinPath);
    }

    private void checkJNILibsDirectoryExists() {
        String jniLibsPropertyName = "gdal.jni.libs.dir";

        String jniLibsDirectoryPathProperty = System.getProperty(jniLibsPropertyName);
        assertNotNull("The system property '" + jniLibsPropertyName + "' representing the directory containing the JNI libraries is not set.", jniLibsDirectoryPathProperty);
        File jniLibsFolder = new File(jniLibsDirectoryPathProperty);
        if (!jniLibsFolder.isDirectory()) {
            fail("The directory path containing the JNI libraries '"+jniLibsDirectoryPathProperty+"' is not valid.");
        }

        Path jniLibsPath = Paths.get(jniLibsDirectoryPathProperty);
        NativeLibraryUtils.registerNativePaths(jniLibsPath);
    }
}
