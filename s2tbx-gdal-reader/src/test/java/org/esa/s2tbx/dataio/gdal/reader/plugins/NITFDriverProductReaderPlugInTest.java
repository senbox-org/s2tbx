package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.NITFDriverProductReaderPlugIn;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public NITFDriverProductReaderPlugInTest() {
        super(".ntf", "NITF", new NITFDriverProductReaderPlugIn());
    }
}
