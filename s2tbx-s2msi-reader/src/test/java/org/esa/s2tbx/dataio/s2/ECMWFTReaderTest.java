package org.esa.s2tbx.dataio.s2;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ECMWFTReaderTest {



    @Test
    public void testECMWFTReader()
    {
        try{
            Path file = Paths.get("/home/florian/dev/AUX_ECMWFT");
            ECMWFTReader readerPlugin = new ECMWFTReader(file,Paths.get("/home/florian/.snap/var/cache"));
            readerPlugin.getECMWFBands();
        } catch (Exception e) {
            
            e.printStackTrace();
            org.junit.Assert.fail(e.getMessage());
        }
    }
}