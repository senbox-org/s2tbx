package org.esa.s2tbx.dataio.mosaic.internal;

import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.RasterDataNode;
import org.esa.snap.core.util.math.MathUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.resources.geometry.XRectangle2D;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */

public class S2tbxImageGeometry {
    private double referencePixelX;
    private double referencePixelY;
    private double easting;
    private double northing;
    private double orientation;
    private double pixelSizeX;
    private double pixelSizeY;
    private double preferedPixelSize;
    private AffineTransform i2m;
    private int width;
    private int height;
    private CoordinateReferenceSystem mapCrs;

    private S2tbxImageGeometry() {
    }

    public S2tbxImageGeometry(Rectangle bounds, CoordinateReferenceSystem mapCrs, AffineTransform image2map) {
        this.i2m = image2map;
        this.width = bounds.width;
        this.height = bounds.height;
        this.mapCrs = mapCrs;
    }

    public AffineTransform getImage2MapTransform() {
        if (i2m != null) {
            return i2m;
        } else {
            return createImageToMapTransform(referencePixelX, referencePixelY, easting, northing, pixelSizeX, pixelSizeY, orientation);
        }
    }

    public Rectangle getImageRect() {
        return new Rectangle(width, height);
    }

    public CoordinateReferenceSystem getMapCrs() {
        return mapCrs;
    }

    public void changeYAxisDirection() {
        pixelSizeY = -pixelSizeY;
    }

    public double getReferencePixelX() {
        return referencePixelX;
    }

    public double getReferencePixelY() {
        return referencePixelY;
    }

    public double getEasting() {
        return easting;
    }

    public double getNorthing() {
        return northing;
    }

    public double getOrientation() {
        return orientation;
    }

    public double getPixelSizeX() {
        return pixelSizeX;
    }

    public double getPixelSizeY() {
        return pixelSizeY;
    }

    public static Point2D calculateEastingNorthing(Product sourceProduct, CoordinateReferenceSystem targetCrs,
                                                   double referencePixelX, double referencePixelY,
                                                   double pixelSizeX, double pixelSizeY) {
        Rectangle2D mapBoundary = createMapBoundary(sourceProduct, targetCrs);
        double easting = mapBoundary.getX() + referencePixelX * pixelSizeX;
        double northing = (mapBoundary.getY() + mapBoundary.getHeight()) - referencePixelY * pixelSizeY;
        return new Point2D.Double(easting, northing);
    }

    public static Rectangle calculateProductSize(Product sourceProduct, CoordinateReferenceSystem targetCrs,
                                                 double pixelSizeX, double pixelSizeY) {
        Rectangle2D mapBoundary = createMapBoundary(sourceProduct, targetCrs);
        double mapW = mapBoundary.getWidth();
        double mapH = mapBoundary.getHeight();
        int width = 1 + (int) Math.floor(mapW / pixelSizeX);
        int height = 1 + (int) Math.floor(mapH / pixelSizeY);
        return new Rectangle(width, height);
    }

    public static S2tbxImageGeometry createTargetGeometry(RasterDataNode rasterDataNode, CoordinateReferenceSystem targetCrs,
                                                          Double pixelSizeX, Double pixelSizeY, Integer width,
                                                          Integer height,
                                                          Double orientation, Double easting, Double northing,
                                                          Double referencePixelX, Double referencePixelY) {
        return createTargetGeometry(createMapBoundary(rasterDataNode, targetCrs), rasterDataNode.getRasterWidth(),
                rasterDataNode.getRasterHeight(), targetCrs, pixelSizeX, pixelSizeY, width,
                height, orientation, easting, northing, referencePixelX, referencePixelY);
    }

    public static S2tbxImageGeometry createTargetGeometry(Product sourceProduct, CoordinateReferenceSystem targetCrs,
                                                          Double pixelSizeX, Double pixelSizeY, Integer width,
                                                          Integer height,
                                                          Double orientation, Double easting, Double northing,
                                                          Double referencePixelX, Double referencePixelY) {
        return createTargetGeometry(createMapBoundary(sourceProduct, targetCrs), sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight(), targetCrs, pixelSizeX, pixelSizeY, width,
                height, orientation, easting, northing, referencePixelX, referencePixelY);
    }

    private static S2tbxImageGeometry createTargetGeometry(Rectangle2D mapBoundary, int sourceWidth, int sourceHeight,
                                                           CoordinateReferenceSystem targetCrs,
                                                           final Double pixelSizeX, final Double pixelSizeY, Integer width,
                                                           Integer height,
                                                           Double orientation, Double easting, Double northing,
                                                           Double referencePixelX, Double referencePixelY) {
        S2tbxImageGeometry ig = new S2tbxImageGeometry();
        ig.mapCrs = targetCrs;
        ig.orientation = orientation == null ? 0.0 : orientation;
        double mapW = mapBoundary.getWidth();
        double mapH = mapBoundary.getHeight();

        if (pixelSizeX == null || pixelSizeY == null) {
            double pixelSize = Math.min(mapW / sourceWidth, mapH / sourceHeight);
            if (MathUtils.equalValues(pixelSize, 0.0)) {
                pixelSize = 1.0f;
            }
            ig.pixelSizeX = pixelSize;
            ig.pixelSizeY = pixelSize;
        } else {
            ig.pixelSizeX = pixelSizeX;
            ig.pixelSizeY = pixelSizeY;
        }

        if (width == null) {
            ig.width = 1 + (int) Math.floor(mapW / ig.pixelSizeX);
        } else {
            ig.width = width;

        }
        if (height == null) {
            ig.height = 1 + (int) Math.floor(mapH / ig.pixelSizeY);
        } else {
            ig.height = height;
        }

        if (referencePixelX == null || referencePixelY == null) {
            ig.referencePixelX = 0.5 * ig.width;
            ig.referencePixelY = 0.5 * ig.height;
        } else {
            ig.referencePixelX = referencePixelX;
            ig.referencePixelY = referencePixelY;
        }
        if (easting == null || northing == null) {
            ig.easting = mapBoundary.getX() + ig.referencePixelX * ig.pixelSizeX;
            ig.northing = (mapBoundary.getY() + mapBoundary.getHeight()) - ig.referencePixelY * ig.pixelSizeY;
        } else {
            ig.easting = easting;
            ig.northing = northing;
        }
        return ig;
    }

    public static S2tbxImageGeometry createCollocationTargetGeometry(Product targetProduct, Product collocationProduct) {
        GeoCoding geoCoding = collocationProduct.getSceneGeoCoding();
        final AffineTransform modelTransform = Product.findImageToModelTransform(geoCoding);
        final double pixelSizeX = modelTransform.getScaleX();
        final double pixelSizeY = modelTransform.getScaleY();
        final int width = collocationProduct.getSceneRasterWidth();
        final int height = collocationProduct.getSceneRasterHeight();
        final double easting = modelTransform.getTranslateX();
        final double northing = modelTransform.getTranslateY();
        final double referencePixelY = 0.0;
        final double referencePixelX = 0.0;
        return S2tbxImageGeometry.createTargetGeometry(targetProduct,
                collocationProduct.getSceneCRS(),
                pixelSizeX, pixelSizeY,
                width, height,
                null,
                easting, northing,
                referencePixelX, referencePixelY);

    }

    private static Rectangle2D createMapBoundary(final RasterDataNode rdn, CoordinateReferenceSystem targetCrs) {
        try {
            GeoCoding geoCoding = rdn.getGeoCoding();
            final double sourceW =  (rdn.getRasterWidth()*rdn.getImageToModelTransform().getScaleX())/10;
            final double sourceH =  (rdn.getRasterHeight()*rdn.getImageToModelTransform().getScaleX())/10;
            return createMapBoundary(geoCoding, sourceW, sourceH, targetCrs);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static Rectangle2D createMapBoundary(final Product product, CoordinateReferenceSystem targetCrs) {
        try {
            GeoCoding sceneGeoCoding = product.getSceneGeoCoding();
            final int sourceW = product.getSceneRasterWidth();
            final int sourceH = product.getSceneRasterHeight();
            return createMapBoundary(sceneGeoCoding, sourceW, sourceH, targetCrs);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Rectangle2D createMapBoundary(GeoCoding geoCoding, double sourceW, double sourceH,
                                                 CoordinateReferenceSystem targetCrs) throws TransformException, FactoryException {
        final CoordinateReferenceSystem sourceCrs = geoCoding.getImageCRS();
        final Rectangle2D rect = XRectangle2D.createFromExtremums(0.0, 0.0, sourceW - 1e-12, sourceH - 1e-12);
        int pointsPerSide = (int)Math.max(sourceH, sourceW) / 10;
        pointsPerSide = Math.max(9, pointsPerSide);
        final ReferencedEnvelope sourceEnvelope = new ReferencedEnvelope(rect, sourceCrs);
        final ReferencedEnvelope targetEnvelope = sourceEnvelope.transform(targetCrs, true, pointsPerSide);
        double minX = targetEnvelope.getMinX();
        double width = targetEnvelope.getWidth();
        if (geoCoding.isCrossingMeridianAt180()) {
            minX = -180.0;
            width = 360;
        }
        return new Rectangle2D.Double(minX, targetEnvelope.getMinY(), width, targetEnvelope.getHeight());
    }

    static AffineTransform createImageToMapTransform(double referencePixelX,
                                                     double referencePixelY,
                                                     double easting,
                                                     double northing,
                                                     double pixelSizeX,
                                                     double pixelSizeY,
                                                     double orientation) {
        AffineTransform i2m = new AffineTransform();
        i2m.translate(easting, northing);
        i2m.scale(pixelSizeX, pixelSizeY);
        i2m.rotate(Math.toRadians(-orientation));
        i2m.translate(-referencePixelX, -referencePixelY);
        return i2m;
    }
}
