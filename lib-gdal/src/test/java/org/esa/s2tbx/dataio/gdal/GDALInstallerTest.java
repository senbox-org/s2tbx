package org.esa.s2tbx.dataio.gdal;

import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Jean Coravu
 */
public class GDALInstallerTest extends TestCase {

    public GDALInstallerTest() {
    }

    public void testInstall() {
        try {
            GDALInstaller installer = new GDALInstaller();
            installer.install();
            if (!GdalInstallInfo.INSTANCE.isPresent()) {
                // the GDAL library has not been installed
                if (org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS) {
                    fail("Failed to install the GDAL library on Windows operating system.");
                }
            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.close();
            String exceptionStackTrace = stringWriter.getBuffer().toString();
            fail("Failed to install the GDAL library. The exception stack trace is: " + exceptionStackTrace);
        }
    }
}
