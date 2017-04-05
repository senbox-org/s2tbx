package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.GSBGDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class GSBGDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public GSBGDriverProductWriterTest() {
        super("GSBG", ".grd", "Byte Int16 UInt16 Float32", new GSBGDriverProductReaderPlugIn(), new GSBGDriverProductWriterPlugIn());
    }
}
