package org.esa.s2tbx.dataio.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class NetCDFDriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public NetCDFDriverProductWriterPlugInTest() {
        super("netCDF", new NetCDFDriverProductWriterPlugIn());
    }
}
