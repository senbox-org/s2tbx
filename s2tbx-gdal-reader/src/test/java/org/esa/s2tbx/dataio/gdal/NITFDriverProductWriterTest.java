package org.esa.s2tbx.dataio.gdal;

/**
 * @author Jean Coravu
 */
public class NITFDriverProductWriterTest extends AbstractGDALDriverProductWriterTest {

    public NITFDriverProductWriterTest() {
        super(new NITFDriverProductWriterPlugIn());
    }
}
