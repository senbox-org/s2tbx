package org.esa.beam.dataio.s2;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by opicas-p on 20/02/2015.
 */
public class NamespaceReaderTest {

    public Object getMask(String uri) throws Exception
    {
        NamespaceFilter gr = new NamespaceFilter();
        gr.parse(uri);
        return gr;
    }

    @Test
    public void testDefect() throws Exception
    {
        Object mat = getMask("l1c/gml-eop/reseop2.xml");

        Assert.assertNotNull(mat);
    }
}
