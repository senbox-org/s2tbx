package org.esa.s2tbx.dataio.gdal.writer;

import org.esa.s2tbx.dataio.gdal.reader.plugins.NITFDriverProductReaderPlugIn;
import org.esa.s2tbx.dataio.gdal.writer.plugins.NITFDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public NITFDriverProductWriterTest() {
        super("NITF", ".ntf", "Byte UInt16 Int16 UInt32 Int32 Float32", new NITFDriverProductReaderPlugIn(), new NITFDriverProductWriterPlugIn());
    }
}
