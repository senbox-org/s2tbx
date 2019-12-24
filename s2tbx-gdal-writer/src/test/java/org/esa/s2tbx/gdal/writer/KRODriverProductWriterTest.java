package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.KRODriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.KRODriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class KRODriverProductWriterTest extends AbstractTestDriverProductWriter {

    public KRODriverProductWriterTest() {
        super("KRO", ".kro", "Byte UInt16 Float32", new KRODriverProductReaderPlugIn(), new KRODriverProductWriterPlugIn());
    }
}
