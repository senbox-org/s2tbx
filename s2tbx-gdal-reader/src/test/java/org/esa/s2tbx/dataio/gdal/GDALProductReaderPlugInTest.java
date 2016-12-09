package org.esa.s2tbx.dataio.gdal;

import junit.framework.TestCase;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;

/**
 * The system properties to set:
 * gdal.bin.dir : the folder containing the GDAL binaries
 * gdal.jni.libs.dir : the folder containing the following libraries: gdaljni.dll, gdalconstjni.dll, ogrjni.dll, osrjni.dll
 *
 * @author Jean Coravu
 */
public class GDALProductReaderPlugInTest extends AbstractGDALPlugInTest {
    private GDALProductReaderPlugin plugIn;

    public GDALProductReaderPlugInTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (GdalInstallInfo.INSTANCE.isPresent()) {
            gdal.AllRegister(); // GDAL init drivers
            this.plugIn = new GDALProductReaderPlugin();
        }
    }

    public void testPluginIsLoaded() {
        Iterator<ProductReaderPlugIn> iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(GDALProductReaderPlugin.FORMAT_NAME);
        ProductReaderPlugIn loadedPlugIn = iterator.next();
        assertEquals(this.plugIn.getClass(), loadedPlugIn.getClass());
    }

    public void testFormatNames() {
        String[] formatNames = this.plugIn.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals(GDALProductReaderPlugin.FORMAT_NAME, formatNames[0]);
    }

    public void testInputTypes() {
        Class[] classes = this.plugIn.getInputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);
        List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    public void testProductFileFilter() {
        SnapFileFilter snapFileFilter = this.plugIn.getProductFileFilter();
        assertNotNull(snapFileFilter);
        assertArrayEquals(this.plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(this.plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(this.plugIn.getDescription(Locale.getDefault())));
    }
}
