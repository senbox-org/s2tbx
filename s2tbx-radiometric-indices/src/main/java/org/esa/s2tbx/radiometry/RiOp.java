package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.*;
import java.util.Map;

@OperatorMetadata(
        alias = "RiOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Other Radiometric Indices",
        description = "The Redness Index was developed to identify soil colour variations.",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class RiOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ri";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the RI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "Green source band",
            description = "The green band for the RI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String greenSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing RI", rectangle.height);
        try {

            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);

            Tile ri = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile riFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float riValue;
            int riFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float red = redFactor * redTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    riValue = (red * red)/(green * green * green);

                    riFlagsValue = 0;
                    if (Float.isNaN(riValue) || Float.isInfinite(riValue)) {
                        riFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        riValue = 0.0f;
                    }
                    if (riValue < 0.0f) {
                        riFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (riValue > 1.0f) {
                        riFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    ri.setSample(x, y, riValue);
                    riFlags.setSample(x, y, riFlagsValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    @Override
    protected void loadSourceBands(Product product) throws OperatorException {
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product); /* Band Centre = 550.7 nm, Band width 88.6 nm*/
            getLogger().info("Using band '" + redSourceBand + "' as RED input band.");
        }
        if (greenSourceBand == null) {
            greenSourceBand = findBand(495, 570, product); /* Band Centre = 664.8 nm, Band width 65.8 nm*/
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }

        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(RiOp.class);
        }

    }

}
