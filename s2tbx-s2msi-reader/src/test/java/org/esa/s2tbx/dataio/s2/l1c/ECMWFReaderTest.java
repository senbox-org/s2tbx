package org.esa.s2tbx.dataio.s2.l1c;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.esa.s2tbx.dataio.s2.ECMWFTReader;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.runtime.LogUtils4Tests;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

public class ECMWFReaderTest {

    @BeforeClass
    public static void setupLogger() throws Exception {
        LogUtils4Tests.initLogger();
    }

    @Test
    public void testECMWFReader() throws IOException, URISyntaxException {
        String pathToTheTestClass = ECMWFReaderTest.class.getResource("/org/esa/s2tbx/dataio/s2/auxdata").toURI().getPath();
        if(IS_OS_WINDOWS) {
            pathToTheTestClass = pathToTheTestClass.substring(1);
        }
        File testClasseDir = new File(pathToTheTestClass);
        File cacheDirECMWFTest = new File(testClasseDir,"aux_ecmwf");
        if (!cacheDirECMWFTest.mkdir()) {
            fail("Unable to create test cache directory to test ECMWFT reader:"+cacheDirECMWFTest.getPath());
        }
        File dataPath = new File(testClasseDir,"AUX_ECMWFT");
        ECMWFTReader readerPlugin = new ECMWFTReader(dataPath.toPath(), cacheDirECMWFTest.toPath(),"");
        List<TiePointGrid> ecmwfGrids = readerPlugin.getECMWFGrids();
        assertNotNull(ecmwfGrids);
        assertEquals(3, ecmwfGrids.size());
        TiePointGrid tiePointGrid_total_column_water_vapour = ecmwfGrids.get(0);
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingX(), 4);
        assertEquals(1220.0, tiePointGrid_total_column_water_vapour.getSubSamplingY(), 4);
        float pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(700, 772);
        assertEquals(12.913931f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(3668, 6460);
        assertEquals(16.69695, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(9300, 3492);
        assertEquals(12.447721f, pixelValue, 0.1);
        pixelValue = tiePointGrid_total_column_water_vapour.getPixelFloat(10276, 10540);
        assertEquals(15.901403f, pixelValue, 0.1);

        TiePointGrid tiePointGrid_total_column_ozone = ecmwfGrids.get(1);
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingX(), 4);
        assertEquals(1220.0, tiePointGrid_total_column_ozone.getSubSamplingY(), 4);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(700, 772);
        assertEquals(0.005749957f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(3668, 6460);
        assertEquals(0.005775377f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(9300, 3492);
        assertEquals(0.005737951f, pixelValue, 0.0001);
        pixelValue = tiePointGrid_total_column_ozone.getPixelFloat(10276, 10540);
        assertEquals(0.005794613f, pixelValue, 0.0001);

        TiePointGrid tiePointGrid_mean_sea_level_pressure = ecmwfGrids.get(2);
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingX(), 4);
        assertEquals(1220.0, tiePointGrid_mean_sea_level_pressure.getSubSamplingY(), 4);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(700, 772);
        assertEquals(100860.1f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(3668, 6460);
        assertEquals(100774.3f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(9300, 3492);
        assertEquals(100841.4f, pixelValue, 0.1);
        pixelValue = tiePointGrid_mean_sea_level_pressure.getPixelFloat(10276, 10540);
        assertEquals(100753.3f, pixelValue, 0.1);
        cacheDirECMWFTest.deleteOnExit();
    }

}