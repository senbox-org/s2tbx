package org.esa.s2tbx.gdal.writer;

import org.esa.s2tbx.gdal.reader.plugins.GSBGDriverProductReaderPlugIn;
import org.esa.s2tbx.gdal.writer.plugins.GSBGDriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class GSBGDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public GSBGDriverProductWriterTest() {
        super("GSBG", ".grd", "Byte Int16 UInt16 Float32", new GSBGDriverProductReaderPlugIn(), new GSBGDriverProductWriterPlugIn());
    }
}
