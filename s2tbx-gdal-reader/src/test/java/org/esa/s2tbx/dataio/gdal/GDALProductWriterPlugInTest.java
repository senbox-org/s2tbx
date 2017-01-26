package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.media.jai.JAI;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jean Coravu
 */
public class GDALProductWriterPlugInTest {
    private GDALProductWriterPlugIn plugIn;

    @Before
    public void setUp() throws Exception {
        GDALInstaller installer = new GDALInstaller();
        installer.install();
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            GDALDriverInfo[] writerDrivers = GDALUtils.loadAvailableWriterDrivers();
            if (writerDrivers != null && writerDrivers.length > 0) {
                this.plugIn = new GDALProductWriterPlugIn(writerDrivers);
                ProductIOPlugInManager.getInstance().addWriterPlugIn(plugIn);
            }
        }
    }

    @Test
    public void testPluginIsLoaded() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            String[] formatNamesToCheck = getFormatNamesToCheck();
            Iterator<ProductWriterPlugIn> iterator = ProductIOPlugInManager.getInstance().getWriterPlugIns(formatNamesToCheck[0]);
            ProductWriterPlugIn loadedPlugIn = iterator.next();
            assertEquals(this.plugIn.getClass(), loadedPlugIn.getClass());
        }
    }

    @Test
    public void testFormatNames() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            String[] formatNames = this.plugIn.getFormatNames();
            assertNotNull(formatNames);
            assertEquals(1, formatNames.length);

            String[] formatNamesToCheck = getFormatNamesToCheck();
            assertEquals(formatNamesToCheck[0], formatNames[0]);
        }
    }

    @Test
    public void testOutputTypes() {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            Class[] classes = this.plugIn.getOutputTypes();
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
            SnapFileFilter snapFileFilter = this.plugIn.getProductFileFilter();
            assertNull(snapFileFilter);
        }
    }

    @Test
    public void testEncodingQualificationWithNullProduct() throws Exception {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            EncodeQualification encodeQualification = this.plugIn.getEncodeQualification(null);
            assertNotNull(encodeQualification);
            assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
        }
    }

    @Test
    public void testEncodingQualificationWithNonNullProduct() throws Exception {
        if (GdalInstallInfo.INSTANCE.isPresent()) {
            Product product = new Product("tempProduct", "GDAL", 20, 30);
            product.setPreferredTileSize(JAI.getDefaultTileSize());
            EncodeQualification encodeQualification = this.plugIn.getEncodeQualification(product);
            assertNotNull(encodeQualification);
            assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
        }
    }

    private String[] getFormatNamesToCheck() {
        return new String[] { "GDAL-WRITER" };
    }
}
