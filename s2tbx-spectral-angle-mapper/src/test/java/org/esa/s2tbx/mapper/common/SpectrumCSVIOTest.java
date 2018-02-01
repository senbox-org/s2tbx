package org.esa.s2tbx.mapper.common;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Razvan Dumitrascu
 */

public class SpectrumCSVIOTest {

    @Test
    public void testIOWithDifferentSizeInputs() throws IOException {
        SpectrumInput[] expectedSpectrumInput = new SpectrumInput[3];
        expectedSpectrumInput[0] = new SpectrumInput("spec1", new int[]{10,-1, -1}, new int[]{10, -1, -1});
        expectedSpectrumInput[0].setIsShapeDefined(false);
        expectedSpectrumInput[1] = new SpectrumInput("spec2", new int[]{20, 21, -1}, new int[]{20, 21, -1});
        expectedSpectrumInput[1].setIsShapeDefined(false);
        expectedSpectrumInput[2] = new SpectrumInput("spec3", new int[]{30, 40, 35}, new int[]{30, 30, 40});
        expectedSpectrumInput[2].setIsShapeDefined(false);

        testIO(expectedSpectrumInput);
    }
    @Test
    public void testIOWithSameSizeInputs() throws IOException {
        SpectrumInput[] expectedSpectrumInput = new SpectrumInput[3];
        expectedSpectrumInput[0] = new SpectrumInput("spec1", new int[]{10}, new int[]{10});
        expectedSpectrumInput[0].setIsShapeDefined(false);
        expectedSpectrumInput[1] = new SpectrumInput("spec2", new int[]{20}, new int[]{20});
        expectedSpectrumInput[1].setIsShapeDefined(false);
        expectedSpectrumInput[2] = new SpectrumInput("spec3", new int[]{30}, new int[]{30});
        expectedSpectrumInput[2].setIsShapeDefined(false);

        testIO(expectedSpectrumInput);
    }
    @Test
    public void testIOWithShapeDefinedInputs() throws IOException {
        SpectrumInput[] expectedSpectrumInput = new SpectrumInput[3];
        expectedSpectrumInput[0] = new SpectrumInput("spec1", new int[]{10, -1}, new int[]{10, -1});
        expectedSpectrumInput[0].setIsShapeDefined(true);
        expectedSpectrumInput[1] = new SpectrumInput("spec2", new int[]{20, 21}, new int[]{20, 21});
        expectedSpectrumInput[1].setIsShapeDefined(true);
        expectedSpectrumInput[2] = new SpectrumInput("spec3", new int[]{30, -1}, new int[]{30, -1});
        expectedSpectrumInput[2].setIsShapeDefined(true);

        testIO(expectedSpectrumInput);
    }
    private void testIO(SpectrumInput[] expectedSpectrumInputs) throws IOException {
        StringWriter writer1 = new StringWriter();
        SpectrumCsvIO.writeSpectrumInputs(expectedSpectrumInputs, writer1);

        SpectrumInput[] actualSpectrumInputs = SpectrumCsvIO.readSpectrum(new StringReader(writer1.toString()));
        assertEqualSpectrumInputs(actualSpectrumInputs, expectedSpectrumInputs);

        StringWriter writer2 = new StringWriter();
        SpectrumCsvIO.writeSpectrumInputs(expectedSpectrumInputs, writer2);
        assertEquals(writer1.toString(), writer2.toString());
    }

    private void assertEqualSpectrumInputs(SpectrumInput[] actualSpectrumInputs, SpectrumInput[] expectedSpectrumInputs) {
        assertNotNull(actualSpectrumInputs.length);
        assertEquals(expectedSpectrumInputs.length, actualSpectrumInputs.length);
        for (int i = 0; i < expectedSpectrumInputs.length; i++) {
            assertEqualSpectrumInputs(expectedSpectrumInputs[i], actualSpectrumInputs[i]);
        }
    }

    private void assertEqualSpectrumInputs(SpectrumInput expectedSpectrumInput, SpectrumInput actualSpectrumInput) {
        assertNotNull(actualSpectrumInput);
        assertEquals(expectedSpectrumInput.getName(), actualSpectrumInput.getName());
        assertEquals(expectedSpectrumInput.getIsShapeDefined(), actualSpectrumInput.getIsShapeDefined());
        assertEquals(actualSpectrumInput.getXPixelPolygonPositions().length, actualSpectrumInput.getYPixelPolygonPositions().length);
        assertEquals(expectedSpectrumInput.getXPixelPolygonPositions().length, expectedSpectrumInput.getYPixelPolygonPositions().length);
        assertEquals(expectedSpectrumInput.getXPixelPolygonPositions().length, actualSpectrumInput.getXPixelPolygonPositions().length);
        assertEquals(expectedSpectrumInput.getYPixelPolygonPositions().length, actualSpectrumInput.getYPixelPolygonPositions().length);
        for (int i = 0; i < expectedSpectrumInput.getXPixelPolygonPositions().length; i++) {
            assertEquals(expectedSpectrumInput.getXPixelPolygonPositions()[i], (expectedSpectrumInput.getXPixelPolygonPositions()[i]));
            assertEquals(expectedSpectrumInput.getYPixelPolygonPositions()[i], (expectedSpectrumInput.getYPixelPolygonPositions()[i]));
        }
    }

}
