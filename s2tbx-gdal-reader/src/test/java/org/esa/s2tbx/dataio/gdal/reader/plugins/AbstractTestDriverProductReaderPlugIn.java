package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.GDALInstaller;
import org.esa.s2tbx.dataio.gdal.GDALUtils;
import org.esa.s2tbx.dataio.gdal.GdalInstallInfo;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Jean Coravu
 */
public abstract class AbstractTestDriverProductReaderPlugIn {
    private final AbstractDriverProductReaderPlugIn readerPlugIn;
    private final Set<String> extensions;
    private final String driverName;

    protected AbstractTestDriverProductReaderPlugIn(String driverName, AbstractDriverProductReaderPlugIn readerPlugIn) {
        this.driverName = driverName;
        this.readerPlugIn = readerPlugIn;
        this.extensions = new HashSet<String>();
    }

    protected AbstractTestDriverProductReaderPlugIn(String extension, String driverName, AbstractDriverProductReaderPlugIn readerPlugIn) {
        this(driverName, readerPlugIn);

        addExtensin(extension);
    }

    @Before
    public final void setUp() throws Exception {
        if (!GdalInstallInfo.INSTANCE.isPresent()) {
            GDALInstaller installer = new GDALInstaller();
            installer.install();
            if (GdalInstallInfo.INSTANCE.isPresent()) {
                GDALUtils.initDrivers();
            }
        }
    }

    @Test
    public final void testPluginIsLoaded() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            String formatName = getFormatNameToCheck();
            Iterator<ProductReaderPlugIn> iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(formatName);
            assertTrue(iterator.hasNext());

            ProductReaderPlugIn loadedPlugIn = iterator.next();
            assertEquals(this.readerPlugIn.getClass(), loadedPlugIn.getClass());

            assertTrue(!iterator.hasNext());
        }
    }

    @Test
    public final void testFormatNames() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            String[] formatNames = this.readerPlugIn.getFormatNames();
            assertNotNull(formatNames);

            assertEquals(1, formatNames.length);

            String formatName = getFormatNameToCheck();
            assertEquals(formatName, formatNames[0]);
        }
    }

    @Test
    public final void testInputTypes() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            Class[] classes = this.readerPlugIn.getInputTypes();
            assertNotNull(classes);

            assertEquals(2, classes.length);

            List<Class> list = Arrays.asList(classes);
            assertEquals(true, list.contains(File.class));
            assertEquals(true, list.contains(String.class));
        }
    }

    @Test
    public void testProductFileFilter() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            SnapFileFilter snapFileFilter = this.readerPlugIn.getProductFileFilter();
            assertNotNull(snapFileFilter);

            String[] defaultExtensions = new String[this.extensions.size()];
            this.extensions.toArray(defaultExtensions);
            assertArrayEquals(defaultExtensions, snapFileFilter.getExtensions());

            String formatName = getFormatNameToCheck();
            assertEquals(formatName, snapFileFilter.getFormatName());

            assertEquals(true, snapFileFilter.getDescription().contains(this.readerPlugIn.getDescription(Locale.getDefault())));
        }
    }

    protected final void addExtensin(String extension) {
        this.extensions.add(extension);
    }

    private String getFormatNameToCheck() {
        return "GDAL-" + this.driverName + "-READER";
    }
}
