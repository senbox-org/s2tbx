package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.lut.LUTAccessor;
import org.esa.s2tbx.s2msi.aerosol.lut.LUTConstants;
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
//@Ignore("Produces NPE in setup")
public class LUTAccessorTest {

    private URL lutResource;
    private String lutPath;
    private LUTAccessor lutAccessor;
    private String[] dimNames;
    private double[][] dimValues;


    @Before
    public void setUp() throws IOException {
        lutResource = LUTAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d");
        // test shall be performed in local environment only, i.e. if we have the LUT available as test resource
        Assume.assumeTrue(lutResource != null);

        dimNames = LUTConstants.dimNames;
        dimValues = LUTConstants.dimValues;
        File lutFile = new File(lutResource.getPath());
        lutPath = lutFile.getAbsolutePath();
        lutAccessor = new LUTAccessor(lutFile);
    }

    @Test
    public void test_InitWithDescriptionFile() throws IOException {
        final File descriptionFile =
                new File(LUTAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.dims.jsn").getPath());
        final LUTAccessor accessor = new LUTAccessor(descriptionFile);
        assertEquals(11, accessor.getNumberOfNonSpectralProperties());
        assertArrayEquals(dimNames, accessor.getTargetNames());
    }

    @Test
    public void testGetNumberOfNonSpectralProperties() {
        assertEquals(11, lutAccessor.getNumberOfNonSpectralProperties());
    }

    @Test
    public void testGetTargetNames() {
        assertArrayEquals(dimNames, lutAccessor.getTargetNames());
    }

    @Test
    public void testGetDimValues() throws Exception {
        for (int i = 0; i < dimValues.length; i++) {
            assertArrayEquals(dimValues[i], lutAccessor.getDimValues(dimNames[i]), 1e-8);
        }
    }

    @Test
    public void testGetLUTPath() {
        assertEquals(lutPath, lutAccessor.getLUTPath());
    }

    @Test
    public void testGetLUTShapes() {
        final int[] lutShapes = lutAccessor.getLUTShapes();
        assertEquals(dimValues.length + 1, lutShapes.length);
        for (int i = 0; i < dimValues.length; i++) {
            assertEquals(dimValues[i].length, lutShapes[i]);
        }
        assertEquals(7, lutShapes[lutShapes.length - 1]);
    }

    @Test
    public void testGetDimIndex() {
        for (int i = 0; i < dimNames.length; i++) {
            assertEquals(i, lutAccessor.getDimIndex(dimNames[i]));
        }
    }

    @Test
    public void testGetDimName() {
        for (int i = 0; i < dimNames.length; i++) {
            assertEquals(dimNames[i], lutAccessor.getDimName(i));
        }
    }

    @Test
    public void testGetModelType() {
        assertEquals("MidLatitudeSummer", lutAccessor.getModelType());
    }

//    @Test
//    public void testGetReflectancesType() {
//        assertEquals("irradiance_reflectances", lutAccessor.getReflectancesType());
//    }
//
//    @Test
//    public void testGetInterpolations() {
//        assertArrayEquals(new String[]{"Lin", "Lin", "Lin", "Lin", "Exp", "Exp", "Exp", "Lin", "Lin"}, lutAccessor.getInterpolations());
//    }
//
//    @Test
//    public void testGetUnit() {
//        String[] firstDimUnits = new String[]{"deg", "deg", "deg", "PSU", "m^-1", "m^-1", "m^-1", "", ""};
//        for (int i = 0; i < targetNames.length; i++) {
//            assertEquals(firstDimUnits[i], lutAccessor.getUnit(targetNames[i]));
//        }
//    }
//
//    @Test
//    public void testGetDescription() {
//        String[] firstDimDescriptions = new String[]{"Sun zenith angle", "View zenith angle",
//                "Difference between sun azimuth angle and view azimuth angle |saa-vaa|", "salinity",
//                "absorption of pigment normalized at 442 nm",
//                "absorption by detritus and gelbstoff normalized at 442 nm",
//                "scattering by variable particle fractions",
//                "ratio between scattering by white light and scattering by blue light",
//                "ratio between detritus and gelbstoff (absorption of detritus / a_dg)"};
//        for (int i = 0; i < targetNames.length; i++) {
//            assertEquals(firstDimDescriptions[i], lutAccessor.getDescription(targetNames[i]));
//        }
//    }

}
