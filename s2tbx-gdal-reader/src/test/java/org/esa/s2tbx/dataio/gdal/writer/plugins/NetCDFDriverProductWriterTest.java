package org.esa.s2tbx.dataio.gdal.writer.plugins;

import org.esa.s2tbx.dataio.gdal.reader.plugins.NetCDFDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class NetCDFDriverProductWriterTest extends AbstractTestDriverProductWriter {

    public NetCDFDriverProductWriterTest() {
        super("netCDF", ".nc", null, new NetCDFDriverProductReaderPlugIn(), new NetCDFDriverProductWriterPlugIn());
    }
}
