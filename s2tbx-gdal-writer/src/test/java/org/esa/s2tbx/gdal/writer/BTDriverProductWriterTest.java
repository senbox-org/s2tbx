package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.BTDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.BTDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class BTDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public BTDriverProductWriterTest() {
        super("BT", ".bt", "Int16 Int32 Float32", new BTDriverProductReaderPlugIn(), new BTDriverProductWriterPlugIn());
    }
}
