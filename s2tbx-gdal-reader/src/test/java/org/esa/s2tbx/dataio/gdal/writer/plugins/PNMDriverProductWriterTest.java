package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.PNMDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class PNMDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public PNMDriverProductWriterTest() {
        super("PNM", ".pnm", "Byte UInt16", new PNMDriverProductReaderPlugIn(), new PNMDriverProductWriterPlugIn());
    }
}
