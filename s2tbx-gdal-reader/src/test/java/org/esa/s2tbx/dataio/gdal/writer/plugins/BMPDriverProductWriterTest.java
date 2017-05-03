package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.BMPDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class BMPDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public BMPDriverProductWriterTest() {
        super("BMP", ".bmp", "Byte", new BMPDriverProductReaderPlugIn(), new BMPDriverProductWriterPlugIn());
    }
}
