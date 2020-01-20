package org.esa.s2tbx.dataio.alos.pri;

import org.esa.s2tbx.dataio.alos.pri.internal.AlosPRIMetadata;
import org.esa.s2tbx.dataio.alos.pri.internal.ImageMetadata;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;

/**
 * Created by jcoravu on 2/12/2019.
 */
class AlosPRIGeoTiffProductReader extends GeoTiffProductReader {

    private final AlosPRIMetadata metadata;
    private final ImageMetadata imageMetadata;
    private final Dimension defaultProductSize;
    private final Rectangle subsetRegion;

    AlosPRIGeoTiffProductReader(ProductReaderPlugIn readerPlugIn, AlosPRIMetadata metadata, ImageMetadata imageMetadata, Dimension defaultProductSize, Rectangle subsetRegion) {
        super(readerPlugIn, null);

        this.metadata = metadata;
        this.imageMetadata = imageMetadata;
        this.defaultProductSize = defaultProductSize;
        this.subsetRegion = subsetRegion;
    }

    @Override
    protected GeoCoding buildBandGeoCoding(GeoCoding productGeoCoding, int bandWidth, int bandHeight) throws Exception {
        GeoCoding geoCoding = null;
        if (this.imageMetadata.hasInsertPoint()) {
            CoordinateReferenceSystem crs = CRS.decode(this.imageMetadata.getCrsCode());
            ImageMetadata.InsertionPoint insertPoint = this.imageMetadata.getInsertPoint();
            geoCoding = ImageUtils.buildCrsGeoCoding(insertPoint.x, insertPoint.y, insertPoint.stepX, insertPoint.stepY,
                                                     this.defaultProductSize, crs, this.subsetRegion);
        } else if (this.defaultProductSize.width != bandWidth) {
            geoCoding = AlosPRIProductReader.buildTiePointGridGeoCoding(this.metadata, bandWidth, bandHeight, getSubsetDef());
        }
        return geoCoding;
    }

    @Override
    protected AffineTransform2D buildBandImageToModelTransform(int bandWidth, int bandHeight) {
        if (!this.imageMetadata.hasInsertPoint() && this.defaultProductSize.width != bandWidth) {
            return new AffineTransform2D((float) this.defaultProductSize.width / bandWidth, 0.0, 0.0, (float) this.defaultProductSize.hashCode() / bandHeight, 0.0, 0.0);
        }
        return null;
    }
}
