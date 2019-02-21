package org.esa.s2tbx.dataio.alos.pri;

import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIConstants;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class AlosPRIProductReaderPluginTest {

    private AlosPRIProductReaderPlugin plugin;
    private String productFolder = "_alos"+ File.separator;

    @Before
    public  void setup(){
        assumeTrue(TestUtil.testdataAvailable());
        plugin = new AlosPRIProductReaderPlugin();
    }

    @Test
    public void alosAV2ReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(AlosPRIConstants.FORMAT_NAMES[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(AlosPRIProductReaderPlugin.class, plugIn.getClass());
    }

    @Test
    public void testDecodeQualificationForXML() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        DecodeQualification decodeQualification = plugin.getDecodeQualification(
                TestUtil.getTestFile(productFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP"+File.separator+"AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410"+File.separator+"ALPSMB038921910"+File.separator+"AL01_PRI_M1B_1C_20061017T212809_20061017T212815_ESR_003892_2B3E.DIMA"));
        assertEquals(DecodeQualification.UNABLE, decodeQualification);
        decodeQualification = plugin.getDecodeQualification(
                TestUtil.getTestFile(productFolder + "AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.SIP"+File.separator+"AL1_NESR_PSM_OB1_1C_20061017T212724_20061017T212730_003892_0629_1855_0410.MD.XML"));
        assertEquals(DecodeQualification.INTENDED, decodeQualification);
        Date endDate = Calendar.getInstance().getTime();
        assertTrue("The decoding time for the file is too big!", (endDate.getTime() - startDate.getTime()) / 1000 < 30);//30 sec
    }

    @Test
    public void testFileExtensions() {
        final String[] fileExtensions = plugin.getDefaultFileExtensions();
        assertNotNull(fileExtensions);
        final List<String> extensionList = Arrays.asList(fileExtensions);
        assertEquals(3, extensionList.size());
        assertEquals(".xml", extensionList.get(0));
        assertEquals(".zip", extensionList.get(1));
        assertEquals(".dima", extensionList.get(2));
    }

    @Test
    public void testFormatNames() {
        final String[] formatNames = plugin.getFormatNames();
        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals("AlosPRIDimap", formatNames[0]);
    }

    @Test
    public void testInputTypes() {
        final Class[] classes = plugin.getInputTypes();
        assertNotNull(classes);
        assertEquals(2, classes.length);
        final List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    @Test
    public void testProductFileFilter() {
        final SnapFileFilter snapFileFilter = plugin.getProductFileFilter();
        assertNotNull(snapFileFilter);
        assertArrayEquals(plugin.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(plugin.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(plugin.getDescription(Locale.getDefault())));
    }
}
