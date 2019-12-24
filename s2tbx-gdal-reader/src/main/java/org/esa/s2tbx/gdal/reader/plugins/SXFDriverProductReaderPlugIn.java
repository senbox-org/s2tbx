package org.esa.s2tbx.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class SXFDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public SXFDriverProductReaderPlugIn() {
        super(".sxf", "SXF", "Storage and eXchange Format");
    }
}
