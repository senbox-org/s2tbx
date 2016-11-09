package org.esa.s2tbx.dataio.gdal;

import junit.framework.TestCase;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Jean Coravu
 */
public class GDALProductReaderPluginTest extends TestCase {

    public GDALProductReaderPluginTest() {
    }

    public void testPluginIsLoaded() {
        Iterator<ProductReaderPlugIn> iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(GDALProductReaderPlugin.FORMAT_NAMES[0]);
        ProductReaderPlugIn plugIn = iterator.next();
        assertEquals(GDALProductReaderPlugin.class, plugIn.getClass());
    }

    public void testFormatNames() {
        GDALProductReaderPlugin plugIn = new GDALProductReaderPlugin();
        String[] formatNames = plugIn.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals(GDALProductReaderPlugin.FORMAT_NAMES[0], formatNames[0]);
    }

    public void testInputTypes() {
        GDALProductReaderPlugin plugIn = new GDALProductReaderPlugin();
        Class[] classes = plugIn.getInputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);
        List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    public void testProductFileFilter() {
        GDALProductReaderPlugin plugIn = new GDALProductReaderPlugin();
        SnapFileFilter snapFileFilter = plugIn.getProductFileFilter();
        assertNotNull(snapFileFilter);
        assertArrayEquals(plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(plugIn.getDescription(Locale.getDefault())));
    }
}
