package org.esa.s2tbx.dataio.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class SAGADriverProductWriterPlugInTest extends AbstractDriverProductWriterPlugInTest {

    public SAGADriverProductWriterPlugInTest() {
        super("SAGA", new SAGADriverProductWriterPlugIn());
    }
}
