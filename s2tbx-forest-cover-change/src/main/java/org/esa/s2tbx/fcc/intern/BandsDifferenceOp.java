package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;

import javax.media.jai.JAI;
import java.awt.*;
import java.util.Map;

/**
 * @author Jean Coravu
 */
@OperatorMetadata(
        alias = "BandsDifferenceOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class BandsDifferenceOp extends Operator {
    @SourceProduct(alias = "source", description = "The first source product.")
    private Product firstSourceProduct;
    @SourceProduct(alias = "source", description = "The second source product.")
    private Product secondSourceProduct;
    @TargetProduct
    private Product targetProduct;

    public BandsDifferenceOp() {
    }

    @Override
    public void initialize() throws OperatorException {
        int firstProductWidth = this.firstSourceProduct.getSceneRasterWidth();
        int firstProductHeight = this.firstSourceProduct.getSceneRasterHeight();
        int secondProductWidth = this.secondSourceProduct.getSceneRasterWidth();
        int secondProductHeight = this.secondSourceProduct.getSceneRasterHeight();
        if (firstProductWidth != secondProductWidth || firstProductHeight != secondProductHeight) {
            throw new OperatorException("Different product sizes.");
        }

        int firstBandCount = this.firstSourceProduct.getBandGroup().getNodeCount();
        int secondBandCount = this.secondSourceProduct.getBandGroup().getNodeCount();
        if (firstBandCount != secondBandCount) {
            throw new OperatorException("Different band count.");
        }

        this.targetProduct = new Product("BandsDifference", "difference", firstProductWidth, secondProductHeight);
        Dimension tileSize = JAI.getDefaultTileSize();
        this.targetProduct.setPreferredTileSize(tileSize);

        for (int i=0; i<firstBandCount; i++) {
            Band firstBand = this.firstSourceProduct.getBandAt(i);
            Band secondBand = this.secondSourceProduct.getBandAt(i);
            if (firstBand.getDataType() != secondBand.getDataType()) {
                throw new OperatorException("Different band type.");
            }

            int firstBandWidth = firstBand.getRasterWidth();
            int firstBandHeight = firstBand.getRasterHeight();
            int secondBandWidth = secondBand.getRasterWidth();
            int secondBandHeight = secondBand.getRasterHeight();
            if (firstBandWidth != secondBandWidth || firstBandHeight != secondBandHeight) {
                throw new OperatorException("Different band sizes.");
            }

            Band targetBand = new Band("band_" + (i+1), firstBand.getDataType(), firstBandWidth, firstBandHeight);
            this.targetProduct.addBand(targetBand);
        }
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        try {
            int firstBandCount = this.firstSourceProduct.getBandGroup().getNodeCount();
            for (int i=0; i<firstBandCount; i++) {
                Band firstSourceBand = this.firstSourceProduct.getBandAt(i);
                Tile firstSourceTile = getSourceTile(firstSourceBand, rectangle);

                Band secondSourceBand = this.secondSourceProduct.getBandAt(i);
                Tile secondSourceTile = getSourceTile(secondSourceBand, rectangle);

                Band targetBand = this.targetProduct.getBandAt(i);
                Tile targetTile = targetTiles.get(targetBand);

                for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                    for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                        float value = firstSourceTile.getSampleFloat(x, y) - secondSourceTile.getSampleFloat(x, y);
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
