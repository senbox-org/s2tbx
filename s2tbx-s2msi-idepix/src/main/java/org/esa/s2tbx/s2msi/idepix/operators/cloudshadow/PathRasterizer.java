package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.vividsolutions.jts.geom.*;
import org.esa.snap.core.datamodel.Placemark;
import org.esa.snap.core.datamodel.PlainFeatureFactory;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.SingleBandedOpImage;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.MathTransform2D;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import java.awt.*;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;

/**
 * todo: add comment
 *
 */
class PathRasterizer {
    /**
     * TODO ...
     *
     * @param x1      The pixel-X value of the start coordinate.
     * @param y1      The pixel-Y value of the start coordinate.
     * @param x2      The pixel-X value of the end coordinate.
     * @param y2      The pixel-Y value of the end coordinate.
     * @param product The product where ... TODO
     * @return the {@link RenderedImage image} containing the rasterized line.
     */
    static RenderedImage rasterizeLine(double x1, double y1, double x2, double y2, Product product) {
        final SimpleFeatureType featureType = Placemark.createGeometryFeatureType();
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("line", featureType);
        final GeometryFactory geometryFactory = new GeometryFactory();
        final Coordinate[] coordinates = {new Coordinate(x1, y1), new Coordinate(x2, y2)};
        final Geometry geometry = geometryFactory.createLineString(coordinates);
        final SimpleFeature lineFeature = PlainFeatureFactory.createPlainFeature(featureType, "line", geometry, null);
        featureCollection.add(lineFeature);

        return new FeatureOpImage(featureCollection, product);
    }

    private static final class FeatureOpImage extends SingleBandedOpImage {

        private static final byte FALSE = (byte) 0;
        private static final byte TRUE = (byte) 255;
//        private final AffineTransform m2iTransform;

        private SimpleFeatureCollection featureCollection;
        FeatureOpImage(SimpleFeatureCollection featureCollection, Product product) {
            super(DataBuffer.TYPE_BYTE,
                  product.getSceneRasterWidth(),
                  product.getSceneRasterHeight(),
                  product.getPreferredTileSize(),
                  null,
                  ResolutionLevel.MAXRES);
            this.featureCollection = featureCollection;
//            GeoCoding geoCoding = product.getGeoCoding();
//            AffineTransform transform = ImageManager.getImageToModelTransform(geoCoding);
//            try {
//                transform.invert();
//                m2iTransform = transform;
//            } catch (NoninvertibleTransformException e) {
//                throw new IllegalArgumentException("Could not invert model-to-image transformation.", e);
//            }
        }

        @Override
        protected void computeRect(PlanarImage[] sourceImages, WritableRaster tile, Rectangle destRect) {
            final BufferedImage image = new BufferedImage(colorModel,
                    RasterFactory.createWritableRaster(tile.getSampleModel(),
                            tile.getDataBuffer(),
                            new Point(0, 0)), false, null);
            final Graphics2D graphics2D = image.createGraphics();
            graphics2D.translate(-(tile.getMinX() + 0.5), -(tile.getMinY() + 0.5));
            graphics2D.setColor(Color.WHITE);

            FeatureIterator<SimpleFeature> featureIterator = featureCollection.features();
            try {
                AffineTransform transform = AffineTransform.getScaleInstance(1.0 / getScale(), 1.0 / getScale());
//                transform.concatenate(m2iTransform);
                AffineTransform2D transform2D = new AffineTransform2D(transform);

                while (featureIterator.hasNext()) {
                    SimpleFeature feature = featureIterator.next();
                    Object value = feature.getDefaultGeometry();
                    if (value instanceof Geometry) {
                        try {
                            renderGeometry((Geometry) value, graphics2D, transform2D);
                        } catch (Exception ignored) {
                            // ignore
                        }
                    }
                }
            } finally {
                featureIterator.close();
            }

            graphics2D.dispose();

            final byte[] data = ((DataBufferByte) tile.getDataBuffer()).getData();
            for (int i = 0; i < data.length; i++) {
                data[i] = (data[i] != 0) ? TRUE : FALSE;
            }
        }

        private static void renderGeometry(Geometry geom, Graphics2D graphics, MathTransform2D transform) throws Exception {
            if (geom instanceof Puntal) {
                Coordinate c = geom.getCoordinate();
                Point2D.Double pt = new Point2D.Double(c.x, c.y);
                transform.transform(pt, pt);
                graphics.drawLine((int) pt.x, (int) pt.y, (int) pt.x, (int) pt.y);
            } else if (geom instanceof Lineal) {
                LiteShape2 shape = new LiteShape2(geom, transform, null, false, true);
                graphics.draw(shape);
            } else if (geom instanceof Polygonal) {
                LiteShape2 shape = new LiteShape2(geom, transform, null, false, true);
                graphics.fill(shape);
            } else if (geom instanceof GeometryCollection) {
                GeometryCollection collection = (GeometryCollection) geom;
                for (int i = 0; i < collection.getNumGeometries(); i++) {
                    renderGeometry(collection.getGeometryN(i), graphics, transform);
                }
            }
        }
    }

}
