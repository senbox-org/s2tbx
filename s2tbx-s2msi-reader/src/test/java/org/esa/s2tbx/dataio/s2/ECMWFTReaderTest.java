package org.esa.s2tbx.dataio.s2;

import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.SystemUtils;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ECMWFTReaderTest {



    @Test
    public void testECMWFTReader()
    {
        try{
            URL url = getClass().getResource("l1c/data/AUX_ECMWFT");
            Path file = Paths.get(url.getPath());
            Path cacheFolderPath = SystemUtils.getCacheDir().toPath();
            cacheFolderPath = cacheFolderPath.resolve("s2tbx");
            System.out.println(cacheFolderPath.toAbsolutePath());
            ECMWFTReader readerPlugin = new ECMWFTReader(file,cacheFolderPath);
            readerPlugin.getECMWFGrids();
            List<TiePointGrid> ecmwfGrids = readerPlugin.getECMWFGrids();
            assertNotNull(ecmwfGrids);
            assertEquals("GRIB files are not completely read","3",Integer.toString(ecmwfGrids.size()));
            assertEquals("ECMWF_total_column_water_vapour_surface", ecmwfGrids.get(0).getName());
            assertEquals("ECMWF_total_column_ozone_surface", ecmwfGrids.get(1).getName());
            assertEquals("ECMWF_mean_sea_level_pressure_surface", ecmwfGrids.get(2).getName());
            assertNotNull(ecmwfGrids.get(0).getTiePoints());
            assertNotNull(ecmwfGrids.get(1).getTiePoints());
            assertNotNull(ecmwfGrids.get(2).getTiePoints());
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.Assert.fail(e.getMessage());
        }
    }
}