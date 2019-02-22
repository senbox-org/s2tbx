package org.esa.s2tbx.dataio.ikonos;

import org.esa.s2tbx.dataio.ikonos.internal.IkonosConstants;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class IkonosProductReaderPluginTest {

    private IkonosProductReaderPlugin plugIn;
    private String productsFolder = "_ikonos" + File.separator;

    @Before
    public void setup() {
        assumeTrue(TestUtil.testdataAvailable());
        plugIn = new IkonosProductReaderPlugin();
    }

    @Test
    public void deimosReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(IkonosConstants.FORMAT_NAMES[0]);
        final ProductReaderPlugIn readerPlugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(IkonosProductReaderPlugin.class, readerPlugIn.getClass());
    }

    @Test
    public void testDecodeQualificationForXML() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        DecodeQualification decodeQualification = plugIn.getDecodeQualification(TestUtil.getTestFile(productsFolder + "IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.SIP"+File.separator+"IK2_OPER_OSA_GEO_1P_20080820T092600_N38-054_E023-986_0001.MD.XML"));
        assertEquals(DecodeQualification.INTENDED, decodeQualification);
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The decoding time for the file is too big!", (endDate.getTime() - startDate.getTime()) / 1000 < 30);
    }

    @Test
    public void testFileExtensions() {
        final String[] fileExtension = plugIn.getDefaultFileExtensions();
        assertNotNull(fileExtension);
        final List<String> extensionList = Arrays.asList(fileExtension);
        assertEquals(2, extensionList.size());
        assertEquals(".xml", extensionList.get(0));
        assertEquals(".zip", extensionList.get(1));
    }

    @Test
    public void testFormatNames() {
        final String[] formatNames = plugIn.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals("IkonosGeoTIFF", formatNames[0]);
    }

    @Test
    public void testInputTypes() {
        final Class[] classes = plugIn.getInputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);
        final List<Class> listOfCLasses = Arrays.asList(classes);
        assertEquals(true, listOfCLasses.contains(File.class));
        assertEquals(true, listOfCLasses.contains(String.class));
    }

    @Test
    public void testProductFileFilter() {
        final SnapFileFilter snapFileFilter = plugIn.getProductFileFilter();
        assertNotNull(snapFileFilter);
        assertArrayEquals(plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(plugIn.getDescription(Locale.getDefault())));
    }
}
