package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.KRODriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class KRODriverProductWriterTest extends AbstractTestDriverProductWriter {

    public KRODriverProductWriterTest() {
        super("KRO", ".kro", "Byte UInt16 Float32", new KRODriverProductReaderPlugIn(), new KRODriverProductWriterPlugIn());
    }
}
