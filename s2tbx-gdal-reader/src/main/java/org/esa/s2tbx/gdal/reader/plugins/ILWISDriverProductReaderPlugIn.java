package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class ILWISDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public ILWISDriverProductReaderPlugIn() {
        super("ILWIS", "ILWIS Raster Map");

        addExtensin(".mpr");
        addExtensin(".mpl");
    }
}
