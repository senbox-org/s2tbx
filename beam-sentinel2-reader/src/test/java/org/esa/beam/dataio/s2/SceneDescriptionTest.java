package org.esa.beam.dataio.s2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * @author Norman Fomferra
 */
public class SceneDescriptionTest {

    private Header header;
    private SceneDescription sceneDescription;

    @Before
    public void before() throws JDOMException, IOException {
        InputStream stream = getClass().getResourceAsStream("l1c/MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml");
        header = Header.parseHeader(new InputStreamReader(stream));
        sceneDescription = SceneDescription.create(header);
    }

    @Test
    public void testStuff() {
        assertEquals(new Rectangle(0,0,40960,30960), sceneDescription.getSceneRectangle());
        assertEquals(new Rectangle(0,20000,10960,10960), sceneDescription.getTileRectangle(0));
        assertEquals(new Rectangle(20004,0,10960,10960 ), sceneDescription.getTileRectangle(10));
        assertEquals(11, sceneDescription.getTileCount());
        assertEquals(4, sceneDescription.getTileGridWidth());
        assertEquals(3, sceneDescription.getTileGridHeight());

        assertEquals("15SUC", sceneDescription.getTileId(0));
        assertEquals("15SUD", sceneDescription.getTileId(1));
        assertEquals("15SVC", sceneDescription.getTileId(2));
        assertEquals("15SVD", sceneDescription.getTileId(3));
        assertEquals("15SWC", sceneDescription.getTileId(4));
        assertEquals("15SWD", sceneDescription.getTileId(5));
        assertEquals("15SXC", sceneDescription.getTileId(6));
        assertEquals("15SXD", sceneDescription.getTileId(7));
        assertEquals("15TUE", sceneDescription.getTileId(8));
        assertEquals("15TVE", sceneDescription.getTileId(9));
        assertEquals("15TWE", sceneDescription.getTileId(10));

        assertEquals(0, sceneDescription.getTileIndex("15SUC"));
        assertEquals(1, sceneDescription.getTileIndex("15SUD"));
        assertEquals(2, sceneDescription.getTileIndex("15SVC"));
        assertEquals(3, sceneDescription.getTileIndex("15SVD"));
        assertEquals(4, sceneDescription.getTileIndex("15SWC"));
        assertEquals(5, sceneDescription.getTileIndex("15SWD"));
        assertEquals(6, sceneDescription.getTileIndex("15SXC"));
        assertEquals(7, sceneDescription.getTileIndex("15SXD"));
        assertEquals(8, sceneDescription.getTileIndex("15TUE"));
        assertEquals(9, sceneDescription.getTileIndex("15TVE"));
        assertEquals(10, sceneDescription.getTileIndex("15TWE"));
    }

}
