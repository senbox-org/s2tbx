package org.esa.s2tbx.dataio.gdal.writer;

import org.esa.s2tbx.dataio.gdal.reader.plugins.BTDriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.writer.plugins.BTDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class BTDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public BTDriverProductWriterTest() {
        super("BT", ".bt", "Int16 Int32 Float32", new BTDriverProductReaderPlugIn(), new BTDriverProductWriterPlugIn());
    }
}
