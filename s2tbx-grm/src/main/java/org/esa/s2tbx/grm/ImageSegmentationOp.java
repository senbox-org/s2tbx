package org.esa.s2tbx.grm;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.GraphDataSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;

import java.awt.Rectangle;

/**
 * @author  Jean Coravu
 */
@OperatorMetadata(
        alias = "ImageSegmentationOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Generic Region Merging' operator computes the distinct regions from a product",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class ImageSegmentationOp extends AbstractGenericRegionMergingOp {
    @SourceProduct(alias = "Source Product TM", description = "The source product to be modified.")
    private Product currentSourceProduct;
    @SourceProduct(alias = "Source Product ETM", description = "The source product to be modified.")
    private Product previousSourceProduct;

    public ImageSegmentationOp() {
    }

    @Override
    protected void validateSourceBandNames() {
//        if (this.sourceBandNames == null || this.sourceBandNames.length == 0) {
//            throw new OperatorException("Please select at least one band.");
//        }
//        if (this.sourceBandNames.length != this.sourceProducts.length) {
//            throw new OperatorException("The source product count does not match the number of rows of the source band names matrix.");
//        }
//
//        int productSceneRasterWidth = getFirstSourceProductWidth();
//        int productSceneRasterHeight = getFirstSourceProductHeight();
//        for (int i=0; i<this.sourceProducts.length; i++) {
//            if (productSceneRasterWidth != this.sourceProducts[i].getSceneRasterWidth() ||productSceneRasterHeight != this.sourceProducts[i].getSceneRasterWidth()) {
//                throw new OperatorException("Please select the bands with the same resolution.");
//            }
//        }
//
//        Product firstProduct = this.sourceProducts[0];
//        String[] firstBandNamesOfFirstProduct = this.sourceBandNames[0];
//        Band firstSelectedSourceBand = firstProduct.getBand(firstBandNamesOfFirstProduct[0]);
//        int bandRasterWidth = firstSelectedSourceBand.getRasterWidth();
//        int bandRasterHeight = firstSelectedSourceBand.getRasterHeight();
//        this.totalSourceBandCount = 0;
//        for (int i=0; i<this.sourceBandNames.length; i++) {
//            String[] bandNames = this.sourceBandNames[i];
//            this.totalSourceBandCount += bandNames.length;
//            Product product = this.sourceProducts[i];
//            for (int j=0; j<bandNames.length; j++) {
//                Band band = product.getBand(bandNames[j]);
//                if (bandRasterWidth != band.getRasterWidth() ||bandRasterHeight != band.getRasterHeight()) {
//                    throw new OperatorException("Please select the bands with the same resolution.");
//                }
//            }
//        }
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
        return this.currentSourceProduct.getName();
    }

    @Override
    protected String getTargetProductType() {
        return this.currentSourceProduct.getProductType();
    }

    @Override
    protected GraphDataSource[] getSourceTiles(BoundingBox tileRegion) {
        Rectangle rectangleToRead = new Rectangle(tileRegion.getLeftX(), tileRegion.getTopY(), tileRegion.getWidth(), tileRegion.getHeight());
        GraphDataSource[] sourceTiles = new GraphDataSource[12];
        int index = 0;
        for (int i=0; i<4; i++) {
            Band band = this.currentSourceProduct.getBandAt(i);
            Tile tile = getSourceTile(band, rectangleToRead);
            sourceTiles[index++] = new GraphDataSource(tile);
        }
        for (int i=0; i<4; i++) {
            Band band = this.previousSourceProduct.getBandAt(i);
            Tile tile = getSourceTile(band, rectangleToRead);
            sourceTiles[index++] = new GraphDataSource(tile);
        }
        for (int i=0; i<4; i++) {
            Band currentBand = this.currentSourceProduct.getBandAt(i);
            Band previousBand = this.previousSourceProduct.getBandAt(i);
            Tile currentSourceTile = getSourceTile(currentBand, rectangleToRead);
            Tile previousSourceTile = getSourceTile(previousBand, rectangleToRead);
            sourceTiles[index++] = new DifferenceGraphDataSource(currentSourceTile, previousSourceTile);
        }
        return sourceTiles;
    }

    private int getFirstSourceProductWidth() {
        return this.currentSourceProduct.getSceneRasterWidth();
    }

    private int getFirstSourceProductHeight() {
        return this.currentSourceProduct.getSceneRasterHeight();
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ImageSegmentationOp.class);
        }
    }

    private static class DifferenceGraphDataSource extends GraphDataSource {
        private final Tile previousSourceTile;

        public DifferenceGraphDataSource(Tile currentSourceTile, Tile previousSourceTile) {
            super(currentSourceTile);

            this.previousSourceTile = previousSourceTile;
        }

        @Override
        public float getSampleFloat(int x, int y) {
            return super.getSampleFloat(x, y) - this.previousSourceTile.getSampleFloat(x, y);
        }
    }
}

