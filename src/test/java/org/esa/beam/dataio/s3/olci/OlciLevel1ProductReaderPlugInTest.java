/*
 * Copyright (c) 2012. Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package org.esa.beam.dataio.s3.olci;

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductIOPlugInManager;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.*;

public class OlciLevel1ProductReaderPlugInTest {

    private OlciLevel1ProductReaderPlugIn plugIn;

    @Before
    public void setup() {
        plugIn = new OlciLevel1ProductReaderPlugIn();
    }

    @Test
    public void testIfPlugInIsLoaded() {
        ProductIOPlugInManager ioPlugInManager = ProductIOPlugInManager.getInstance();
        Iterator<ProductReaderPlugIn> readerPlugIns = ioPlugInManager.getReaderPlugIns(
                OlciLevel1ProductReaderPlugIn.FORMAT_NAME);
        assertTrue(readerPlugIns.hasNext());
        assertTrue(readerPlugIns.next() instanceof OlciLevel1ProductReaderPlugIn);

    }

    @Test
    public void testDecodeQualificationWithFullResolution() {
        String validPath = createPath("OL", OlciL1bModes.EFR.name());
        assertEquals(DecodeQualification.INTENDED, plugIn.getDecodeQualification(validPath));
    }

    @Test
    public void testDecodeQualificationWithReducedResolution() {
        String validPath = createPath("OL", OlciL1bModes.ERR.name());
        assertEquals(DecodeQualification.INTENDED, plugIn.getDecodeQualification(validPath));
    }

    @Test
    public void testDecodeQualificationWithInvalidDataSource() {
        String invalidDataSource = "SL";
        String invalidPath = createPath(invalidDataSource, OlciL1bModes.ERR.name());
        assertEquals(DecodeQualification.UNABLE, plugIn.getDecodeQualification(invalidPath));
    }

    @Test
    public void testDecodeQualificationWithInvalidMode() {
        String invalidMode = "ABC";
        String invalidPath = createPath("OL", invalidMode);
        assertEquals(DecodeQualification.UNABLE, plugIn.getDecodeQualification(invalidPath));
    }

    @Test
    public void testDecodeQualificationWithoutFile() {
        String invalidPath = "/OL_1_ERR_TTTTTTTTTTTT_instanceID_GGG_CCCC_VV";
        assertEquals(DecodeQualification.UNABLE, plugIn.getDecodeQualification(invalidPath));
    }

    @Test
    public void testDecodeQualificationWithWrongFile() {
        String invalidPath = "/OL_1_ERR_TTTTTTTTTTTT_instanceID_GGG_CCCC_VV/someFile.doc";
        assertEquals(DecodeQualification.UNABLE, plugIn.getDecodeQualification(invalidPath));
    }

    @Test
    public void testSupportedInputTypes() {
        Class[] inputTypes = plugIn.getInputTypes();
        assertEquals(2, inputTypes.length);
        assertEquals(String.class, inputTypes[0]);
        assertEquals(File.class, inputTypes[1]);
    }

    @Test
    public void testCreateReaderInstanceReturnsNewInstanceEachTime() {
        ProductReader firstInstance = plugIn.createReaderInstance();
        assertNotNull(firstInstance);
        ProductReader secondInstance = plugIn.createReaderInstance();
        assertNotSame(secondInstance, firstInstance);
    }
    private String createPath(String dataSource, String mode) {
        String validParentDirectory = String.format("/%s_1_%s_TTTTTTTTTTTT_instanceID_GGG_CCCC_VV/", dataSource, mode);
        String aFileInDirectory = "L1b_EO_manifest.xml";
        return validParentDirectory + aFileInDirectory;
    }
}
