package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.BMPDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.BMPDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class BMPDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public BMPDriverProductWriterTest() {
        super("BMP", ".bmp", "Byte", new BMPDriverProductReaderPlugIn(), new BMPDriverProductWriterPlugIn());
    }
}
