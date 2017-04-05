package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.KEADriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class KEADriverProductWriterTest extends AbstractTestDriverProductWriter {

    public KEADriverProductWriterTest() {
        super("KEA", ".kea", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64", new KEADriverProductReaderPlugIn(), new KEADriverProductWriterPlugIn());
    }
}
