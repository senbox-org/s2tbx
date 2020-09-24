package org.esa.s2tbx.fcc.common;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.JAI;
import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jean Coravu
 */
@OperatorMetadata(
        alias = "BandsDifferenceOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class BandsDifferenceOp extends Operator {
    @SourceProducts(alias = "Source", description = "The first source product.")
    private Product[] sourceProducts;

    @TargetProduct
    private Product targetProduct;

    public BandsDifferenceOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        Product firstSourceProduct = this.sourceProducts[0];

        int firstProductWidth = firstSourceProduct.getSceneRasterWidth();
        int firstProductHeight = firstSourceProduct.getSceneRasterHeight();
        for (int i=1; i<this.sourceProducts.length; i++) {
            int productWidth = this.sourceProducts[i].getSceneRasterWidth();
            int productHeight = this.sourceProducts[i].getSceneRasterHeight();
            if (firstProductWidth != productWidth || firstProductHeight != productHeight) {
                throw new OperatorException("Different product sizes.");
            }
        }

        int firstBandCount = firstSourceProduct.getBandGroup().getNodeCount();
        for (int i=1; i<this.sourceProducts.length; i++) {
            int bandCount = this.sourceProducts[i].getBandGroup().getNodeCount();
            if (firstBandCount != bandCount) {
                throw new OperatorException("Different band count.");
            }
        }

        this.targetProduct = new Product("BandsDifference", "difference", firstProductWidth, firstProductHeight);
        Dimension tileSize = JAI.getDefaultTileSize();
        this.targetProduct.setPreferredTileSize(tileSize);
        this.targetProduct.setSceneGeoCoding(firstSourceProduct.getSceneGeoCoding());

        for (int i=0; i<firstBandCount; i++) {
            Band firstProductBand = firstSourceProduct.getBandAt(i);
            int firstBandWidth = firstProductBand.getRasterWidth();
            int firstBandHeight = firstProductBand.getRasterHeight();

            for (int k=1; k<this.sourceProducts.length; k++) {
                Band band = this.sourceProducts[k].getBandAt(i);
                if (firstProductBand.getDataType() != band.getDataType()) {
                    throw new OperatorException("Different band type.");
                }

                int bandWidth = band.getRasterWidth();
                int bandHeight = band.getRasterHeight();
                if (firstBandWidth != bandWidth || firstBandHeight != bandHeight) {
                    throw new OperatorException("Different band sizes.");
                }
            }

            Band targetBand = new Band("band_" + (i+1), firstProductBand.getDataType(), firstBandWidth, firstBandHeight);
            this.targetProduct.addBand(targetBand);
        }
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        try {
            Iterator<Map.Entry<Band, Tile>> it = targetTiles.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Band, Tile> entry = it.next();
                Band targetBand = entry.getKey();
                Tile targetTile = entry.getValue();
                int targetBandIndex = this.targetProduct.getBandIndex(targetBand.getName());

                Tile[] sourceTiles = new Tile[this.sourceProducts.length];
                for (int i=0; i<this.sourceProducts.length; i++) {
                    Product sourceProduct = this.sourceProducts[i];
                    Band sourceBand = sourceProduct.getBandAt(targetBandIndex);
                    sourceTiles[i] = getSourceTile(sourceBand, rectangle);
                }

                for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                    for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                        float value = sourceTiles[0].getSampleFloat(x, y);
                        for (int i=1; i<sourceTiles.length; i++) {
                            value -= sourceTiles[i].getSampleFloat(x, y);
                        }
                        targetTile.setSample(x, y, value);
                    }
                }
            }
        } finally {
            pm.done();
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(BandsDifferenceOp.class);
        }
    }
}