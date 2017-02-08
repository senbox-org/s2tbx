package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.utils.TestUtil;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu
 */
public class GDALInstallerTest {

    public GDALInstallerTest() {
    }

    @Test
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
