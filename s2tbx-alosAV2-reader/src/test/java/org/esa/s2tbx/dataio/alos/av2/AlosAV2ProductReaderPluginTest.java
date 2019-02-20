package org.esa.s2tbx.dataio.alos.av2;

import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Constants;
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

public class AlosAV2ProductReaderPluginTest {

    private AlosAV2ProductReaderPlugin plugin;
    private String productFolder = "_alos"+ File.separator;

    @Before
    public  void setup(){
        assumeTrue(TestUtil.testdataAvailable());
        plugin = new AlosAV2ProductReaderPlugin();
    }

    @Test
    public void alosAV2ReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(AlosAV2Constants.FORMAT_NAMES[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(AlosAV2ProductReaderPlugin.class, plugIn.getClass());
    }

    @Test
    public void testDecodeQualificationForXML() throws IOException {
        Date startDate = Calendar.getInstance().getTime();
        DecodeQualification decodeQualification = plugin.getDecodeQualification(
                TestUtil.getTestFile(productFolder + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.GTIF.aux.xml"));
        assertEquals(DecodeQualification.UNABLE, decodeQualification);
        decodeQualification = plugin.getDecodeQualification(
                TestUtil.getTestFile(productFolder + "AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410.SIP"+File.separator+"AL1_NESR_AV2_OBS_1C_20080715T181736_20080715T181748_013182_0539_1810_0410"+File.separator+"AL01_AV2_OBS_1C_20080715T181736_20080715T181748_ESR_013182_3985.DIMA"));
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
        assertEquals("AlosAV2Dimap", formatNames[0]);
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
