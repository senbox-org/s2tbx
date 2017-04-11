package org.esa.s2tbx.dataio.gdal;

import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.gdal.activator.GDALDistributionInstaller;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.fail;

/**
 * @author Jean Coravu
 */
public class GDALDistributionInstallerTest {

    public GDALDistributionInstallerTest() {
    }

    @Test
    public void testInstall() {
        try {
            GDALDistributionInstaller.install();
            if (!GdalInstallInfo.INSTANCE.isPresent()) {
                // the GDAL library has not been installed
                if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX) {
                    fail("Failed to install the GDAL library.");
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
