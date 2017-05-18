package org.esa.s2tbx.dataio.gdal;

import org.apache.commons.lang.SystemUtils;
import org.esa.s2tbx.dataio.gdal.activator.GDALDistributionInstaller;
import org.esa.s2tbx.dataio.gdal.activator.GDALInstallInfo;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * @author Jean Coravu
 */
public class GDALDistributionInstallerTest {

    public GDALDistributionInstallerTest() {
    }

    @Before
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
    }

    @Test
    public void testInstall() {
//        try {
//            if (!GDALInstallInfo.INSTANCE.isPresent()) {
//                GDALDistributionInstaller.install();
//            }
//        } catch (Throwable e) {
//            // the GDAL library has not been installed
//            StringWriter stringWriter = new StringWriter();
//            PrintWriter printWriter = new PrintWriter(stringWriter);
//            e.printStackTrace(printWriter);
//            printWriter.close();
//            String exceptionStackTrace = stringWriter.getBuffer().toString();
//            if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX) {
//                fail("Failed to install the GDAL library. The exception stack trace is: " + exceptionStackTrace);
//            }
//        }
    }
}
