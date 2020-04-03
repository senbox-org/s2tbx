package org.esa.s2tbx.dataio.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class GTiffDriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public GTiffDriverProductWriterPlugInTest() {
        super("GTiff", new GTiffDriverProductWriterPlugIn());
    }
}
