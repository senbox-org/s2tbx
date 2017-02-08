package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * @author Jean Coravu
 */
public class ILWISDriverProductReaderPlugInTest extends AbstractTestDriverProductReaderPlugIn {

    public ILWISDriverProductReaderPlugInTest() {
        super("ILWIS", new ILWISDriverProductReaderPlugIn());

        addExtensin(".mpr");
        addExtensin(".mpl");
    }
}
