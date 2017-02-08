package org.esa.s2tbx.dataio.gdal.writer;

import org.esa.s2tbx.dataio.gdal.reader.plugins.KEADriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.writer.plugins.KEADriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class KEADriverProductWriterTest extends AbstractDriverProductWriterTest {

    public KEADriverProductWriterTest() {
        super("KEA", ".kea", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64", new KEADriverProductReaderPlugIn(), new KEADriverProductWriterPlugIn());
    }
}
