package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.ILWISDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class ILWISDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public ILWISDriverProductWriterTest() {
        super("ILWIS", ".mpr", "Byte Int16 Int32 Float64", new ILWISDriverProductReaderPlugIn(), new ILWISDriverProductWriterPlugIn());
    }
}
