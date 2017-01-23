package org.esa.s2tbx.dataio.jp2;

import org.esa.snap.core.dataio.EncodeQualification;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2ProductWriterPluginTest {
    private JP2ProductWriterPlugIn plugIn;

    @Before
    public void setup() {
        plugIn = new JP2ProductWriterPlugIn();
    }

    @Test
    public void testFileExtensions() {
        final String[] fileExtensions = plugIn.getDefaultFileExtensions();

        assertNotNull(fileExtensions);
        final List<String> extensionList = Arrays.asList(fileExtensions);
        assertEquals(2, extensionList.size());
        assertEquals(true, extensionList.contains(".jp2"));
        assertEquals(true, extensionList.contains(".jpeg2000"));
    }

    @Test
    public void testFormatNames() {
        final String[] formatNames = plugIn.getFormatNames();

        assertNotNull(formatNames);
        assertEquals(1, formatNames.length);
        assertEquals("JPEG2000", formatNames[0]);
    }

    @Test
    public void testOutputTypes() {
        final Class[] classes = plugIn.getOutputTypes();

        assertNotNull(classes);
        assertEquals(2, classes.length);
        final List<Class> list = Arrays.asList(classes);
        assertEquals(true, list.contains(File.class));
        assertEquals(true, list.contains(String.class));
    }

    @Test
    public void testProductFileFilter() {
        final SnapFileFilter snapFileFilter = plugIn.getProductFileFilter();

        assertNotNull(snapFileFilter);
        assertArrayEquals(plugIn.getDefaultFileExtensions(), snapFileFilter.getExtensions());
        assertEquals(plugIn.getFormatNames()[0], snapFileFilter.getFormatName());
        assertEquals(true, snapFileFilter.getDescription().contains(plugIn.getDescription(Locale.getDefault())));
    }

    @Test
    public void testEncodingQualification() throws Exception {
        Product product = new Product("Name", "JP2", 4, 4);

        EncodeQualification encodeQualification = plugIn.getEncodeQualification(product);
        assertNotNull(encodeQualification);
        assertEquals(EncodeQualification.Preservation.PARTIAL, encodeQualification.getPreservation());
        assertNotNull(encodeQualification.getInfoString());

        TiePointGrid lat = new TiePointGrid("lat", 4, 4, 0, 0, 1, 1, new float[16]);
        TiePointGrid lon = new TiePointGrid("lon", 4, 4, 0, 0, 1, 1, new float[16]);
        product.addTiePointGrid(lat);
        product.addTiePointGrid(lon);
        product.setSceneGeoCoding(new TiePointGeoCoding(lat, lon));
        encodeQualification = plugIn.getEncodeQualification(product);
        assertEquals(EncodeQualification.Preservation.PARTIAL, encodeQualification.getPreservation());
        assertNotNull(encodeQualification.getInfoString());

        product.setSceneGeoCoding(new CrsGeoCoding(DefaultGeographicCRS.WGS84, 4, 4, 0, 0, 1, 1));
        encodeQualification = plugIn.getEncodeQualification(product);
        assertEquals(EncodeQualification.Preservation.FULL, encodeQualification.getPreservation());
    }
}
