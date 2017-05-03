package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.GTXDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class GTXDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public GTXDriverProductWriterTest() {
        super("GTX", ".gtx", "Float32", new GTXDriverProductReaderPlugIn(), new GTXDriverProductWriterPlugIn());
    }
}
