package org.esa.beam.dataio.s2;

import junit.framework.Assert;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;

import java.io.File;

/**
 * Created by opicas-p on 11/07/2014.
 */
public class ShortenTest {
    @Test
    public void testFileName() throws Exception
    {
        String visualStudioPath = "C:\\Program Files (x86)\\Microsoft Visual Studio 10.0";

        if(SystemUtils.IS_OS_WINDOWS)
        {
            File directory =  new File(visualStudioPath);
            if(directory.exists())
            {
                String shortPath = Utils.GetShortPathName(visualStudioPath);
                Assert.assertEquals("C:\\PROGRA~2\\MICROS~1.0", shortPath);
            }
        }
    }

    @Test
    public void testVeryLongFileName() throws Exception
    {
        String visualStudioPath = "D:\\is tmp\\TheDataIsVeryVeryLongHere\\S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE\\GRANULE\\S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLH_N01.01\\IMG_DATA\\S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLH_B04.jp2";

        if(SystemUtils.IS_OS_WINDOWS)
        {
            String shortPath = Utils.GetIterativeShortPathName(visualStudioPath);
            Assert.assertEquals("D:\\ISTMP~1\\THEDAT~1\\S2A_OP~1.SAF\\GRANULE\\S270DA~1.01\\IMG_DATA\\S2A_OP~4.JP2", shortPath);
        }
    }

    @Test
    public void testVeryLongFileName2() throws Exception
    {
        String visualStudioPath = "D:\\is tmp\\TheDataIsVeryVeryLongHere\\S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE\\GRANULE\\S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLH_N01.01\\IMG_DATA\\S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLH_B04.jpx";

        if(SystemUtils.IS_OS_WINDOWS)
        {
            String shortPath = Utils.GetIterativeShortPathName(visualStudioPath);
            Assert.assertEquals("", shortPath);
        }
    }
}
