package org.esa.beam.dataio.s2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Norman Fomferra
 */
public class S2ProductInfoTest {
    private L1cMetadata header;

    @Before
    public void before() throws JDOMException, IOException {
        InputStream stream = getClass().getResourceAsStream("l1c/MTD_GPPL1C_054_20091210235100_20091210235130_0001.xml");
        //todo change test
        //header = L1cMetadata.parseHeader(new InputStreamReader(stream));
    }

    @Test
    public void testIt() throws Exception {


    }

}
