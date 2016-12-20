package org.esa.s2tbx.s2msi.aerosol.lut;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author olafd
 */
public class S2LutAccessorTest {

    private S2LutAccessor s2LutAccessor;
    private String[] dimNames;
    private float[][] dimValues;


    @Before
    public void setUp() throws IOException {
        URL lutResource = S2LutAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d");
        // test shall be performed in local environment only, i.e. if we have the LUT available as test resource
        Assume.assumeTrue(lutResource != null);

        assert lutResource != null;
        File lutFile = new File(lutResource.getPath());
        s2LutAccessor = new S2LutAccessor(lutFile);
        dimNames = S2LutConstants.dimNames;
        dimValues = S2LutConstants.dimValues;
    }

    @Test
    public void test_InitWithDescriptionFile() throws IOException {
        final File descriptionFile =
                new File(S2LutAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.dims.jsn").getPath());
        final S2LutAccessor accessor = new S2LutAccessor(descriptionFile);
        assertEquals(11, accessor.getNumberOfNonSpectralProperties());
        assertArrayEquals(dimNames, accessor.getTargetNames());
    }

    @Test
    public void testGetNumberOfNonSpectralProperties() {
        assertEquals(11, s2LutAccessor.getNumberOfNonSpectralProperties());
    }

    @Test
    public void testGetTargetNames() {
        assertArrayEquals(dimNames, s2LutAccessor.getTargetNames());
    }

    @Test
    public void testGetDimValues() throws Exception {
        for (int i = 0; i < dimValues.length; i++) {
            assertArrayEquals(dimValues[i], s2LutAccessor.getDimValues(dimNames[i]), 1e-8f);
        }
    }

    @Test
    public void testGetLUTShapes() {
        final int[] lutShapes = s2LutAccessor.getLUTShapes();
        assertEquals(dimValues.length + 1, lutShapes.length);
        for (int i = 0; i < dimValues.length; i++) {
            assertEquals(dimValues[i].length, lutShapes[i]);
        }
        assertEquals(7, lutShapes[lutShapes.length - 1]);
    }

}
