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
 * gdal.distribution.root.dir : the folder containing the GDAL distribution
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
        String formatName = getFormatNameToTest();
        Iterator<ProductReaderPlugIn> iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(formatName);
        ProductReaderPlugIn loadedPlugIn = iterator.next();
        assertEquals(this.plugIn.getClass(), loadedPlugIn.getClass());
    }

    public void testFormatNames() {
        String[] formatNames = this.plugIn.getFormatNames();
        assertNotNull(formatNames);

        assertEquals(1, formatNames.length);

        String formatName = getFormatNameToTest();
        assertEquals(formatName, formatNames[0]);
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

        String[] defaultExtensions = new String[] { ".*" };
        assertArrayEquals(defaultExtensions, snapFileFilter.getExtensions());

        String formatName = getFormatNameToTest();
        assertEquals(formatName, snapFileFilter.getFormatName());

        assertEquals(true, snapFileFilter.getDescription().contains(this.plugIn.getDescription(Locale.getDefault())));
    }

    private static String getFormatNameToTest() {
        return "GDAL-READER";
    }
}
