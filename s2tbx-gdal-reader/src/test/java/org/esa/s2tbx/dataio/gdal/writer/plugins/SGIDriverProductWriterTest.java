package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.SGIDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class SGIDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public SGIDriverProductWriterTest() {
        super("SGI", ".rgb", "Byte", new SGIDriverProductReaderPlugIn(), new SGIDriverProductWriterPlugIn());
    }
}
