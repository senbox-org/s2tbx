package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class NetCDFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public NetCDFDriverProductReaderPlugInTest() {
        super(".nc", "netCDF", new NetCDFDriverProductReaderPlugIn());
    }
}
