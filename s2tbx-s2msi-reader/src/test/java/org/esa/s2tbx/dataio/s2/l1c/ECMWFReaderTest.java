package org.esa.s2tbx.dataio.s2.l1c;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.xstream.io.path.Path;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.ECMWFTReader;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.runtime.LogUtils4Tests;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ECMWFReaderTest {

    @BeforeClass
    public static void setupLogger() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testECMWFReader() throws IOException {
       
        File testECMWFFile = getTestDataDir("");
        File dataPath = getTestDataDir("AUX_ECMWFT");
        ECMWFTReader readerPlugin = new ECMWFTReader(dataPath.toPath(), testECMWFFile.toPath());
        List<TiePointGrid> ecmwfGrids = readerPlugin.getECMWFGrids();
        assertNotNull(ecmwfGrids);
        assertEquals(3, ecmwfGrids.size());
        TiePointGrid tiePointGrid_total_column_water_vapour = ecmwfGrids.get(0);
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingY(),4);
        float pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(700, 772);
        assertEquals(12.913931f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(3668, 6460);
        assertEquals(16.69695, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(9300, 3492);
        assertEquals(12.447721f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(10276, 10540);
        assertEquals(15.901403f, pixelValue, 0.1);

        TiePointGrid tiePointGrid_total_column_ozone = ecmwfGrids.get(1);
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingY(),4);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(700, 772);
        assertEquals(0.005749957f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(3668, 6460);
        assertEquals(0.005775377f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(9300, 3492);
        assertEquals(0.005737951f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(10276, 10540);
        assertEquals(0.005794613f, pixelValue, 0.0001);

        TiePointGrid tiePointGrid_mean_sea_level_pressure = ecmwfGrids.get(2);
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingX(),4);
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingY(),4);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(700, 772);
        assertEquals(100860.1f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(3668, 6460);
        assertEquals(100774.3f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(9300, 3492);
        assertEquals(100841.4f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(10276, 10540);
        assertEquals(100753.3f, pixelValue, 0.1);
     

    }


    private static File getTestDataDir() {
        File dir = new File("./src/test/data/");
        if (!dir.exists()) {
            dir = new File("./s2tbx-s2msi-reader/src/test/data/");
            if (!dir.exists()) {
                Assert.fail("Can't find my test data. Where is '" + dir + "'?");
            }
        }
        return dir;
    }

    public static File getTestDataDir(String path) {
        return new File(getTestDataDir(), path);
    }
}