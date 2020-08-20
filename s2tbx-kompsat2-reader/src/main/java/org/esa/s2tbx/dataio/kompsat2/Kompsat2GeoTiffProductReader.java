package org.esa.s2tbx.dataio.kompsat2;

import org.esa.s2tbx.dataio.kompsat2.metadata.BandMetadata;
import org.esa.s2tbx.dataio.kompsat2.metadata.Kompsat2Metadata;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.awt.*;

/**
 * Created by jcoravu on 16/12/2019.
 */
public class Kompsat2GeoTiffProductReader extends GeoTiffProductReader {

    private final Dimension productSize;
    private final Kompsat2Metadata metadata;
    private final Dimension defaultBandSize;
    private final ProductSubsetDef subsetDef;

    public Kompsat2GeoTiffProductReader(ProductReaderPlugIn readerPlugIn, Kompsat2Metadata metadata, Dimension productSize, Dimension defaultBandSize, ProductSubsetDef subsetDef) {
        super(readerPlugIn, null);

        this.metadata = metadata;
        this.productSize = productSize;
        this.defaultBandSize = defaultBandSize;
        this.subsetDef = subsetDef;
    }

    @Override
    protected GeoCoding buildBandGeoCoding(GeoCoding productGeoCoding, int bandWidth, int bandHeight) {
        if (productGeoCoding == null) {
            if (this.productSize.width != bandWidth || this.productSize.height != bandHeight) {
                return Kompsat2ProductReader.buildTiePointGridGeoCoding(this.metadata, this.defaultBandSize.width, this.defaultBandSize.height, this.subsetDef);
            }
        }
        return productGeoCoding;
    }
}
