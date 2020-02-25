package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.RSTDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.RSTDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public RSTDriverProductWriterTest() {
        super("RST", ".rst", "Byte Int16 Float32", new RSTDriverProductReaderPlugIn(), new RSTDriverProductWriterPlugIn());
    }
}
