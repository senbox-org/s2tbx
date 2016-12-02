//package org.esa.s2tbx.dataio.gdal;
//
//import junit.framework.TestCase;
//import org.esa.snap.core.dataio.EncodeQualification;
//import org.esa.snap.core.dataio.ProductIOPlugInManager;
//import org.esa.snap.core.dataio.ProductWriterPlugIn;
//import org.esa.snap.core.util.io.SnapFileFilter;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//
//import static org.junit.Assert.assertArrayEquals;
//
///**
// * @author Jean Coravu
// */
//public abstract class AbstractGDALProductWriterPlugInTest extends TestCase {
//    private final AbstractGDALProductWriterPlugIn plugIn;
//
//    protected AbstractGDALProductWriterPlugInTest(AbstractGDALProductWriterPlugIn plugIn) {
//        this.plugIn = plugIn;
//    }
//
//    protected abstract String[] getFormatNamesToCheck();
//
//    public void testPluginIsLoaded() {
//        String[] formatNamesToCheck = getFormatNamesToCheck();
//        Iterator<ProductWriterPlugIn> iterator = ProductIOPlugInManager.getInstance().getWriterPlugIns(formatNamesToCheck[0]);
//        ProductWriterPlugIn loadedPlugIn = iterator.next();
//        assertEquals(this.plugIn.getClass(), loadedPlugIn.getClass());
//    }
//
//    public void testFormatNames() {
//        String[] formatNames = this.plugIn.getFormatNames();
//        assertNotNull(formatNames);
//        assertEquals(1, formatNames.length);
//
//        String[] formatNamesToCheck = getFormatNamesToCheck();
//        assertEquals(formatNamesToCheck[0], formatNames[0]);
//    }
//
//    public void testOutputTypes() {
//        Class[] classes = this.plugIn.getOutputTypes();
//        assertNotNull(classes);
//        assertEquals(2, classes.length);
//
//        List<Class> list = Arrays.asList(classes);
//        assertEquals(true, list.contains(File.class));
//        assertEquals(true, list.contains(String.class));
//    }
//
//    public void testProductFileFilter() {
//        SnapFileFilter snapFileFilter = this.plugIn.getProductFileFilter();
//        assertNotNull(snapFileFilter);
//        assertArrayEquals(this.plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
//        assertEquals(this.plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
//        assertEquals(true, snapFileFilter.getDescription().contains(this.plugIn.getDescription(Locale.getDefault())));
//    }
//
//    public void testEncodingQualification() throws Exception {
//        EncodeQualification encodeQualification = this.plugIn.getEncodeQualification(null);
//        assertNotNull(encodeQualification);
//        assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
//    }
//}
