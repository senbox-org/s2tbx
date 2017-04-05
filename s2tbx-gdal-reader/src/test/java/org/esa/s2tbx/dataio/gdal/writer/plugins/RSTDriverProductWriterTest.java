package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.RSTDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class RSTDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public RSTDriverProductWriterTest() {
        super("RST", ".rst", "Byte Int16 Float32", new RSTDriverProductReaderPlugIn(), new RSTDriverProductWriterPlugIn());
    }
}
