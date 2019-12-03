package org.esa.s2tbx.dataio.ikonos;

import org.esa.s2tbx.dataio.ikonos.metadata.IkonosMetadata;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

import java.awt.*;

/**
 * Created by jcoravu on 29/11/2019.
 */
class IkonosGeoTiffProductReader extends GeoTiffProductReader {

    private final IkonosMetadata metadata;
    private final Dimension productSize;
    private final Dimension defaultBandSize;
    private final ProductSubsetDef subsetDef;

    IkonosGeoTiffProductReader(ProductReaderPlugIn readerPlugIn, IkonosMetadata metadata, Dimension productSize, Dimension defaultBandSize, ProductSubsetDef subsetDef) {
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
                return IkonosProductReader.buildTiePointGridGeoCoding(this.metadata, this.defaultBandSize.width, this.defaultBandSize.height, this.subsetDef);
            }
        }
        return productGeoCoding;
    }
}
