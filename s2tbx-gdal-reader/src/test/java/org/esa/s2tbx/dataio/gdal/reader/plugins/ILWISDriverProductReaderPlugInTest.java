package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.gdal.reader.plugins.ILWISDriverProductReaderPlugIn;

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
