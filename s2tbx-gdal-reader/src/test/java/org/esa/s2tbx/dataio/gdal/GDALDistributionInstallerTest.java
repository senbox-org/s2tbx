package org.esa.s2tbx.dataio.gdal;

import org.junit.Test;

/**
 * @author Jean Coravu
 */
public class GDALDistributionInstallerTest {

    public GDALDistributionInstallerTest() {
    }

    @Test
    public void testInstall() {
        /*try {
            if (!GdalInstallInfo.INSTANCE.isPresent()) {
                GDALDistributionInstaller.install();
            }
        } catch (Throwable e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            printWriter.close();
            String exceptionStackTrace = stringWriter.getBuffer().toString();
            // the GDAL library has not been installed
            if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX) {
                fail("Failed to install the GDAL library. The exception stack trace is: " + exceptionStackTrace);
            }
        }*/
    }
}
