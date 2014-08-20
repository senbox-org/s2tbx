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
}
