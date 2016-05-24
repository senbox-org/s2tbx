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
        alias = "McariOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Vegetation Indices",
        description = "Modified Chlorophyll Absorption Ratio Index, developed to be responsive to chlorophyll variation",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class McariOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "mcari";

    @Parameter(label = "Red (B4) factor", defaultValue = "1.0F", description = "The value of the red source band (B4) is multiplied by this value.")
    private float redB4Factor;

    @Parameter(label = "Red (B5) factor", defaultValue = "1.0F", description = "The value of the red source band (B5) is multiplied by this value.")
    private float redB5Factor;

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "Red source band 4",
            description = "The red band (B4) for the MCARI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand4;

    @Parameter(label = "Red source band 5",
            description = "The red band (B5) for the MCARI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand5;

    @Parameter(label = "Green source band",
            description = "The green band for the MCARI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String greenSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing MCARI", rectangle.height);
        try {
            Tile redB4Tile = getSourceTile(getSourceProduct().getBand(redSourceBand4), rectangle);
            Tile redB5Tile = getSourceTile(getSourceProduct().getBand(redSourceBand5), rectangle);
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);

            Tile mcari = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile mcariFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float mcariValue;
            int mcariFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float redB4 = redB4Factor * redB4Tile.getSampleFloat(x, y);
                    final float redB5 = redB5Factor * redB5Tile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    mcariValue = ( (redB5 - redB4) - 0.2f*(redB5 - green) ) * (redB5 - redB4);

                    mcariFlagsValue = 0;
                    if (Float.isNaN(mcariValue) || Float.isInfinite(mcariValue)) {
                        mcariFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        mcariValue = 0.0f;
                    }
                    if (mcariValue < 0.0f) {
                        mcariFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (mcariValue > 1.0f) {
                        mcariFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    mcari.setSample(x, y, mcariValue);
                    mcariFlags.setSample(x, y, mcariFlagsValue);
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
        if (redSourceBand4 == null) {
            redSourceBand4 = findBand(650, 680, product); /* (600, 650) */
            getLogger().info("Using band '" + redSourceBand4 + "' as red input band (B4).");
        }
        if (redSourceBand5 == null) {
            redSourceBand5 = findBand(697, 712, product); /* (600, 650) */
            getLogger().info("Using band '" + redSourceBand5 + "' as red input band (B5).");
        }
        if (greenSourceBand == null) {
            greenSourceBand = findBand(543, 577, product); /* (495, 570) */
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }
        if (redSourceBand4 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B4). Please specify band.");
        }
        if (redSourceBand5 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B5). Please specify band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(McariOp.class);
        }

    }

}
