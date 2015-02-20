package org.esa.beam.dataio.s2;

import junit.framework.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by opicas-p on 20/02/2015.
 */
public class GMLReaderTest {

    public InputStream getStream() throws Exception
    {
        InputStream stream = getClass().getResourceAsStream("l1c/gml/S2A_OPER_MSK_DEFECT_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");
        return stream;
    }

    @Test
    public void test1() throws Exception
    {

        GMLReader gr = new GMLReader();

        InputStream o = getStream();

        Assert.assertNotNull(o);

        gr.parseGMLReader(o);
    }
}
