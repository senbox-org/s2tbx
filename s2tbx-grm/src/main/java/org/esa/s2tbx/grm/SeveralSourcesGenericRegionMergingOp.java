package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.GraphDataSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.SourceProducts;

import java.awt.Rectangle;
import java.util.StringTokenizer;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "SeveralSourcesGenericRegionMergingOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class SeveralSourcesGenericRegionMergingOp extends AbstractGenericRegionMergingOp {
    @SourceProducts(alias = "source", description = "The source product.")
    private Product[] sourceProducts;

    @Parameter(label = "Source bands matrix", description = "The source bands for the computation.")
    private String[][] sourceBandNames;

    private int totalSourceBandCount;

    public SeveralSourcesGenericRegionMergingOp() {
    }

    @Override
    protected void validateSourceBandNames() {
        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
            throw new OperatorException("Please select at least one band.");
        }
        if (this.sourceBandNames.length != this.sourceProducts.length) {
            throw new OperatorException("The source product count does not match the number of rows of the source band names matrix.");
        }

        int productSceneRasterWidth = getFirstSourceProductWidth();
        int productSceneRasterHeight = getFirstSourceProductHeight();
        for (int i=0; i<this.sourceProducts.length; i++) {
            if (productSceneRasterWidth != this.sourceProducts[i].getSceneRasterWidth() ||productSceneRasterHeight != this.sourceProducts[i].getSceneRasterWidth()) {
                throw new OperatorException("Please select the bands with the same resolution.");
            }
        }

        Product firstProduct = this.sourceProducts[0];
        String[] firstBandNamesOfFirstProduct = this.sourceBandNames[0];
        Band firstSelectedSourceBand = firstProduct.getBand(firstBandNamesOfFirstProduct[0]);
        int bandRasterWidth = firstSelectedSourceBand.getRasterWidth();
        int bandRasterHeight = firstSelectedSourceBand.getRasterHeight();
        this.totalSourceBandCount = 0;
        for (int i=0; i<this.sourceBandNames.length; i++) {
            String[] bandNames = this.sourceBandNames[i];
            this.totalSourceBandCount += bandNames.length;
            Product product = this.sourceProducts[i];
            for (int j=0; j<bandNames.length; j++) {
                Band band = product.getBand(bandNames[j]);
                if (bandRasterWidth != band.getRasterWidth() ||bandRasterHeight != band.getRasterHeight()) {
                    throw new OperatorException("Please select the bands with the same resolution.");
                }
            }
        }
    }

    @Override
    protected int getTargetSceneRasterWidth() {
        return getFirstSourceProductWidth();
    }

    @Override
    protected int getTargetSceneRasterHeight() {
        return getFirstSourceProductHeight();
    }

    @Override
    protected String getTargetProductName() {
        return this.sourceProducts[0].getName();
    }

    @Override
    protected String getTargetProductType() {
        return this.sourceProducts[0].getProductType();
    }

    @Override
    protected GraphDataSource[] getSourceTiles(BoundingBox tileRegion) {
        GraphDataSource[] sourceTiles = new GraphDataSource[this.totalSourceBandCount];
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        for (int i=0, k = 0; i<this.sourceBandNames.length; i++) {
            String[] bandNames = this.sourceBandNames[i];
            Product product = this.sourceProducts[i];
            for (int j=0; j<bandNames.length; j++) {
                Band band = product.getBand(bandNames[j]);
                Tile tile = getSourceTile(band, rectangleToRead);
                sourceTiles[k++] = new GraphDataSource(tile);
            }
        }
        return sourceTiles;
    }

    private int getFirstSourceProductWidth() {
        return this.sourceProducts[0].getSceneRasterWidth();
    }

    private int getFirstSourceProductHeight() {
        return this.sourceProducts[0].getSceneRasterHeight();
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(SeveralSourcesGenericRegionMergingOp.class);
        }
    }
}
