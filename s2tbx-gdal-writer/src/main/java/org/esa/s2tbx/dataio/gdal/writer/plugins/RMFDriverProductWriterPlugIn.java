package org.esa.s2tbx.dataio.gdal.writer.plugins;

/**
 * Writer plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public class RMFDriverProductWriterPlugIn extends AbstractDriverProductWriterPlugIn {

    public RMFDriverProductWriterPlugIn() {
        super(".rsw", "RMF", "Raster Matrix Format", "Byte Int16 Int32 Float64");
    }
}
