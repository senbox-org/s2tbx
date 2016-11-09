package org.esa.s2tbx.dataio.gdal;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductWriterPlugInTest extends AbstractGDALProductWriterPlugInTest {

    public NITFDriverProductWriterPlugInTest() {
        super(new NITFDriverProductWriterPlugIn());
    }

    @Override
    protected String[] getFormatNamesToCheck() {
        return NITFDriverProductWriterPlugIn.FORMAT_NAMES;
    }
}
