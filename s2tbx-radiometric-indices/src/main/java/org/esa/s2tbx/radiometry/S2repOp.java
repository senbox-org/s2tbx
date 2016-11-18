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
        alias = "S2repOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Sentinel-2 red-edge position index",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class S2repOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "s2rep";

    @Parameter(label = "Red (B4) factor", defaultValue = "1.0F", description = "The value of the red source band (B4) is multiplied by this value.")
    private float redB4Factor;

    @Parameter(label = "Red (B5) factor", defaultValue = "1.0F", description = "The value of the red source band (B5) is multiplied by this value.")
    private float redB5Factor;

    @Parameter(label = "Red (B6) factor", defaultValue = "1.0F", description = "The value of the red source band (B6) is multiplied by this value.")
    private float redB6Factor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band 4",
            description = "The red band (B4) for the S2REP computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 650, maxWavelength = 680)
    private String redSourceBand4;

    @Parameter(label = "Red source band 5",
            description = "The red band (B5) for the S2REP computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 697, maxWavelength = 712)
    private String redSourceBand5;

    @Parameter(label = "Red source band 6",
            description = "The red band (B6) for the S2REP computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 732, maxWavelength = 747)
    private String redSourceBand6;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the S2REP computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 773, maxWavelength = 793)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing S2REP", rectangle.height);
        try {
            Tile redB4Tile = getSourceTile(getSourceProduct().getBand(redSourceBand4), rectangle);
            Tile redB5Tile = getSourceTile(getSourceProduct().getBand(redSourceBand5), rectangle);
            Tile redB6Tile = getSourceTile(getSourceProduct().getBand(redSourceBand6), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile s2rep = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile s2repFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float s2repValue;
            int s2repFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float redB4 = redB4Factor * redB4Tile.getSampleFloat(x, y);
                    final float redB5 = redB5Factor * redB5Tile.getSampleFloat(x, y);
                    final float redB6 = redB6Factor * redB6Tile.getSampleFloat(x, y);

                    s2repValue = 705.0f + 35.0f * ( ( ( (nir + redB4) / 2.0f ) - redB5 ) / (redB6 - redB5) );

                    s2repFlagsValue = 0;
                    if (Float.isNaN(s2repValue) || Float.isInfinite(s2repValue)) {
                        s2repFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        s2repValue = 0.0f;
                    }
                    if (s2repValue < 0.0f) {
                        s2repFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (s2repValue > 1.0f) {
                        s2repFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    s2rep.setSample(x, y, s2repValue);
                    s2repFlags.setSample(x, y, s2repFlagsValue);
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
        if (redSourceBand6 == null) {
            redSourceBand6 = findBand(732, 747, product); /* (600, 650) */
            getLogger().info("Using band '" + redSourceBand6 + "' as red input band (B6).");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(773, 793, product); /* (800, 900) */
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (redSourceBand4 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B4). Please specify band.");
        }
        if (redSourceBand5 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B5). Please specify band.");
        }
        if (redSourceBand6 == null) {
            throw new OperatorException("Unable to find band that could be used as red input band (B6). Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { redSourceBand4, redSourceBand5, redSourceBand6, nirSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2repOp.class);
        }

    }

}
