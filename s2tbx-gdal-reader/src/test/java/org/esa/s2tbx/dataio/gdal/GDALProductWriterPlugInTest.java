package org.esa.s2tbx.dataio.gdal;

import org.esa.s2tbx.dataio.gdal.activator.GDALDriverInfo;
import org.esa.s2tbx.dataio.gdal.activator.GDALPlugInActivator;
import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.gdal.gdal.gdal;

import javax.media.jai.JAI;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The system properties to set:
 * gdal.distribution.root.dir : the folder containing the GDAL distribution
 *
 * @author Jean Coravu
 */
public class GDALProductWriterPlugInTest extends AbstractGDALPlugInTest {
    private GDALProductWriterPlugIn plugIn;

    public GDALProductWriterPlugInTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GDALDriverInfo[] writerDrivers = GDALUtils.loadAvailableWriterDrivers();
        if (writerDrivers != null && writerDrivers.length > 0) {
            this.plugIn = new GDALProductWriterPlugIn(writerDrivers);
            ProductIOPlugInManager.getInstance().addWriterPlugIn(plugIn);
        }
    }

    public void testPluginIsLoaded() {
        String[] formatNamesToCheck = getFormatNamesToCheck();
        Iterator<ProductWriterPlugIn> iterator = ProductIOPlugInManager.getInstance().getWriterPlugIns(formatNamesToCheck[0]);
        ProductWriterPlugIn loadedPlugIn = iterator.next();
        assertEquals(this.plugIn.getClass(), loadedPlugIn.getClass());
    }

    public void testFormatNames() {
        String[] formatNames = this.plugIn.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);

        String[] formatNamesToCheck = getFormatNamesToCheck();
        assertEquals(formatNamesToCheck[0], formatNames[0]);
    }

    public void testOutputTypes() {
        Class[] classes = this.plugIn.getOutputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);

        List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    public void testProductFileFilter() {
        SnapFileFilter snapFileFilter = this.plugIn.getProductFileFilter();
        assertNull(snapFileFilter);
    }

    public void testEncodingQualificationWithNullProduct() throws Exception {
        EncodeQualification encodeQualification = this.plugIn.getEncodeQualification(null);
        assertNotNull(encodeQualification);
        assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
    }

    public void testEncodingQualificationWithNonNullProduct() throws Exception {
        Product product = new Product("tempProduct", "GDAL", 20, 30);
        product.setPreferredTileSize(JAI.getDefaultTileSize());
        EncodeQualification encodeQualification = this.plugIn.getEncodeQualification(product);
        assertNotNull(encodeQualification);
        assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
    }

    private String[] getFormatNamesToCheck() {
        return new String[] { "GDAL-WRITER" };
    }
}
