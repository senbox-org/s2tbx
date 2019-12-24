package org.esa.s2tbx.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class SAGADriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public SAGADriverProductWriterPlugInTest() {
        super("SAGA", new SAGADriverProductWriterPlugIn());
    }
}
