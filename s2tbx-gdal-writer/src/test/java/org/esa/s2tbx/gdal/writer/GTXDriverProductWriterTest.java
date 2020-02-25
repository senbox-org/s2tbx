package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.GTXDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.GTXDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class GTXDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public GTXDriverProductWriterTest() {
        super("GTX", ".gtx", "Float32", new GTXDriverProductReaderPlugIn(), new GTXDriverProductWriterPlugIn());
    }
}
