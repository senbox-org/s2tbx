package org.esa.s2tbx.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public PNMDriverProductWriterPlugInTest() {
        super("PNM", new PNMDriverProductWriterPlugIn());
    }
}
