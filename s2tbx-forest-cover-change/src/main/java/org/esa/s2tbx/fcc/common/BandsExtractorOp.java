package org.esa.s2tbx.fcc.common;

import com.vividsolutions.jts.geom.Geometry;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
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
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "BandsExtractorOp",
        version="1.0",
        category = "",
        description = "Creates a new product out of the source product containing only the indexes bands given",
        authors = "Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class BandsExtractorOp extends Operator {
    @SourceProduct(alias = "Source", description = "The source product to be modified.")
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(label = "Source bands", description = "The source bands for the computation.", rasterDataNodeType = Band.class)
    private String[] sourceBandNames;

    @Override
    public void initialize() throws OperatorException {
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        this.targetProduct = extractBands(this.sourceProduct, this.sourceBandNames);
    }

    public static Product extractBands(Product sourceProduct, String[] sourceBandNames) {
        Product product = new Product(sourceProduct.getName(), sourceProduct.getProductType(), sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        product.setStartTime(sourceProduct.getStartTime());
        product.setEndTime(sourceProduct.getEndTime());
        product.setNumResolutionsMax(sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(sourceProduct, product);
        ProductUtils.copyGeoCoding(sourceProduct, product);
        ProductUtils.copyTiePointGrids(sourceProduct, product);
        ProductUtils.copyVectorData(sourceProduct, product);
        ProductUtils.copyMasks(sourceProduct, product);
        for (int i=0; i<sourceBandNames.length; i++) {
            Band sourceBand = sourceProduct.getBand(sourceBandNames[i]);
            String sourceBandName = sourceBand.getName();
            String targetBandName = sourceBandName;
            ProductUtils.copyBand(sourceBandName, sourceProduct, targetBandName, product, true);

            Band targetBand = product.getBand(targetBandName);
            ProductUtils.copyGeoCoding(sourceBand, targetBand);
        }

        return product;
    }


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(BandsExtractorOp.class);
        }
    }
}
