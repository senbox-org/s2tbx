package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.PNMDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.PNMDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public PNMDriverProductWriterTest() {
        super("PNM", ".pnm", "Byte UInt16", new PNMDriverProductReaderPlugIn(), new PNMDriverProductWriterPlugIn());
    }
}
