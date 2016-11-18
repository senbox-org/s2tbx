package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.radiometry.annotations.BandParameter;
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
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Modified Chlorophyll Absorption Ratio Index, developed to be responsive to chlorophyll variation",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class McariOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "mcari";

    @Parameter(label = "Red 1 factor", defaultValue = "1.0F", description = "The value of the red source band (B4) is multiplied by this value.")
    private float red1Factor;

    @Parameter(label = "Red 2 factor", defaultValue = "1.0F", description = "The value of the red source band (B5) is multiplied by this value.")
    private float red2Factor;

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "Red 1 source band",
            description = "The first red band for the MCARI computation. Choose B4 for Sentinel-2. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 650, maxWavelength = 680)
    private String red1SourceBand;

    @Parameter(label = "Red 2 source band",
            description = "The second red band for the MCARI computation. Choose B5 for Sentinel-2. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 697, maxWavelength = 712)
    private String red2SourceBand;

    @Parameter(label = "Green source band",
            description = "The green band for the MCARI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 543, maxWavelength = 577)
    private String greenSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing MCARI", rectangle.height);
        try {
            Tile red1Tile = getSourceTile(getSourceProduct().getBand(red1SourceBand), rectangle);
            Tile red2Tile = getSourceTile(getSourceProduct().getBand(red2SourceBand), rectangle);
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);

            Tile mcari = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile mcariFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float mcariValue;
            int mcariFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float redB4 = red1Factor * red1Tile.getSampleFloat(x, y);
                    final float redB5 = red2Factor * red2Tile.getSampleFloat(x, y);
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
        if (red1SourceBand == null) {
            red1SourceBand = findBand(650, 680, product); /* (600, 650) */
            getLogger().info("Using band '" + red1SourceBand + "' as red input band (B4).");
        }
        if (red2SourceBand == null) {
            red2SourceBand = findBand(697, 712, product); /* (600, 650) */
            getLogger().info("Using band '" + red2SourceBand + "' as red input band (B5).");
        }
        if (greenSourceBand == null) {
            greenSourceBand = findBand(543, 577, product); /* (495, 570) */
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }
        if (red1SourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B4). Please specify band.");
        }
        if (red2SourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B5). Please specify band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { red1SourceBand, red2SourceBand, greenSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(McariOp.class);
        }

    }

}
