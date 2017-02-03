package org.esa.s2tbx.dataio.gdal.reader.plugins;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class MFFDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public MFFDriverProductReaderPlugIn() {
        super(".hdr", "MFF", "Vexcel MFF Raster");
    }
}
