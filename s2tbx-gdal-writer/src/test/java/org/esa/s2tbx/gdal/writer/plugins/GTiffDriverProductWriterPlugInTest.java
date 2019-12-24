package org.esa.s2tbx.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class GTiffDriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public GTiffDriverProductWriterPlugInTest() {
        super("GTiff", new GTiffDriverProductWriterPlugIn());
    }
}
