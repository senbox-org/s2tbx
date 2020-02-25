package org.esa.s2tbx.gdal.reader.plugins;

/**
 * @author Cosmin Cara
 */
public class ERMapperDriverProductReaderPlugIn extends AbstractDriverProductReaderPlugIn {

    public ERMapperDriverProductReaderPlugIn() {
        super(".ers", "ERS", "ERMapper Data Format");
    }
}
