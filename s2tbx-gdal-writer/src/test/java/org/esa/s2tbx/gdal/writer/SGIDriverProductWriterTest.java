package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.SGIDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.SGIDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class SGIDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public SGIDriverProductWriterTest() {
        super("SGI", ".rgb", "Byte", new SGIDriverProductReaderPlugIn(), new SGIDriverProductWriterPlugIn());
    }
}
