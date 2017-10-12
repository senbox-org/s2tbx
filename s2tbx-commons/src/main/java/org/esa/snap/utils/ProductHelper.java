package org.esa.snap.utils;

import com.vividsolutions.jts.geom.Geometry;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * @author Jean Coravu
 */
public class ProductHelper {

    private ProductHelper() {
    }

    public static void copyMasks(Product sourceProduct, Product targetProduct, String[] sourceMaskNames) {
        double scaleX = (double) sourceProduct.getSceneRasterWidth() / targetProduct.getSceneRasterWidth();
        double scaleY = (double) sourceProduct.getSceneRasterHeight() / targetProduct.getSceneRasterHeight();
        GeoCoding sceneGeoCoding = sourceProduct.getSceneGeoCoding();
        AffineTransform referenceImageToModelTransform = null;
        if (sceneGeoCoding != null && sceneGeoCoding.getImageToMapTransform() instanceof AffineTransform) {
            AffineTransform mapTransform = (AffineTransform) sceneGeoCoding.getImageToMapTransform();
            referenceImageToModelTransform = new AffineTransform(scaleX * mapTransform.getScaleX(), 0, 0, scaleY * mapTransform.getScaleY(),
                    mapTransform.getTranslateX(), mapTransform.getTranslateY());
        } else {
            referenceImageToModelTransform = new AffineTransform(scaleX, 0.0d, 0.0d, scaleY, 0.0d, 0.0d);
        }

        ProductNodeGroup<Mask> sourceMaskGroup = sourceProduct.getMaskGroup();
        for (int i = 0; i < sourceMaskNames.length; i++) {
            Mask sourceMask = sourceMaskGroup.get(sourceMaskNames[i]);
            Mask.ImageType imageType = sourceMask.getImageType();
            if (imageType.getName().equals(Mask.BandMathsType.TYPE_NAME)) {
                String expression = Mask.BandMathsType.getExpression(sourceMask);
                Mask targetMask = Mask.BandMathsType.create(sourceMask.getName(), sourceMask.getDescription(),
                        targetProduct.getSceneRasterWidth(),
                        targetProduct.getSceneRasterHeight(), expression,
                        sourceMask.getImageColor(), sourceMask.getImageTransparency());
                targetProduct.addMask(targetMask);
            } else if (imageType.getName().equals(Mask.VectorDataType.TYPE_NAME)) {
                VectorDataNode vectorDataMaskNode = Mask.VectorDataType.getVectorData(sourceMask);
                String vectorDataNodeName = vectorDataMaskNode.getName();
                // deal with case that a mask's vector data node is not in a product's vector data group
                if (sourceProduct.getVectorDataGroup().get(vectorDataNodeName) == null) {
                    VectorDataNode targetVectorDataNode = transferVectorDataNode(targetProduct, vectorDataMaskNode, referenceImageToModelTransform);
                    if (targetVectorDataNode != null) {
                        targetProduct.addMask(sourceMask.getName(), targetVectorDataNode, sourceMask.getDescription(), sourceMask.getImageColor(),
                                sourceMask.getImageTransparency());
                    }
                }
            } else if (imageType.canTransferMask(sourceMask, targetProduct)) {
                imageType.transferMask(sourceMask, targetProduct);
            }
        }
    }

    private static VectorDataNode transferVectorDataNode(Product targetProduct, VectorDataNode sourceVectorDataNode, AffineTransform referenceImageToModelTransform) {
        AffineTransform referenceModelToImageTransform;
        try {
            referenceModelToImageTransform = referenceImageToModelTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            return null;
        }
        final GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();
        final AffineTransform targetImageToModelTransform = Product.findImageToModelTransform(targetProduct.getSceneGeoCoding());
        referenceModelToImageTransform.concatenate(targetImageToModelTransform);
        final AffineTransform2D mathTransform = new AffineTransform2D(referenceModelToImageTransform);
        transformer.setMathTransform(mathTransform);
        final FeatureCollection<SimpleFeatureType, SimpleFeature> sourceCollection = sourceVectorDataNode.getFeatureCollection();
        final DefaultFeatureCollection targetCollection = new DefaultFeatureCollection(sourceCollection.getID(), sourceCollection.getSchema());
        final FeatureIterator<SimpleFeature> featureIterator = sourceCollection.features();
        while (featureIterator.hasNext()) {
            final SimpleFeature srcFeature = featureIterator.next();
            final Object defaultGeometry = srcFeature.getDefaultGeometry();
            if (defaultGeometry != null && defaultGeometry instanceof Geometry) {
                try {
                    final Geometry transformedGeometry = transformer.transform((Geometry) defaultGeometry);
                    final SimpleFeature targetFeature = SimpleFeatureBuilder.copy(srcFeature);
                    targetFeature.setDefaultGeometry(transformedGeometry);
                    targetCollection.add(targetFeature);
                } catch (TransformException e) {
                    return null;
                }
            }
        }
        VectorDataNode targetVectorDataNode = new VectorDataNode(sourceVectorDataNode.getName(), sourceCollection.getSchema());
        targetVectorDataNode.getFeatureCollection().addAll((FeatureCollection<?, ?>) targetCollection);
        targetVectorDataNode.setDefaultStyleCss(sourceVectorDataNode.getDefaultStyleCss());
        targetVectorDataNode.setDescription(sourceVectorDataNode.getDescription());
        targetVectorDataNode.setOwner(targetProduct);
        return targetVectorDataNode;
    }
}
