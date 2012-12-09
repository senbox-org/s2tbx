package org.esa.beam.dataio.s2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Norman Fomferra
 */
public class S2ProductInfoTest {
    private Header header;

    @Before
    public void before() throws JDOMException, IOException {
        InputStream stream = getClass().getResourceAsStream("l1c/MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml");
        header = Header.parseHeader(new InputStreamReader(stream));
    }

    @Test
    public void testIt() throws Exception {


    }

}
