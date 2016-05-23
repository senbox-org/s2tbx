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

/**
 * @author Dragos Mihailescu
 */
@OperatorMetadata(
        alias = "Msavi2Op",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Soil Indices",
        description = "This retrieves the second Modified Soil Adjusted Vegetation Index (MSAVI2).",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class Msavi2Op extends BaseIndexOp {

    // constants
    static final String BAND_NAME = "msavi2";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the RED source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the MSAVI2 computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the MSAVI2 computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing msavi2", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile msavi2 = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile msavi2Flags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float msavi2Value;
            int msavi2FlagValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    msavi2Value = (float) ((1f/2f) * (2*(nir + 1) - Math.sqrt( (2*nir + 1)*(2*nir + 1) - 8*(nir - red))));

                    msavi2FlagValue = 0;
                    if (Float.isNaN(msavi2Value) || Float.isInfinite(msavi2Value)) {
                        msavi2FlagValue |= ARITHMETIC_FLAG_VALUE;
                        msavi2Value = 0.0f;
                    }
                    if (msavi2Value < 0.0f) {
                        msavi2FlagValue |= LOW_FLAG_VALUE;
                    }
                    if (msavi2Value > 1.0f) {
                        msavi2FlagValue |= HIGH_FLAG_VALUE;
                    }
                    msavi2.setSample(x, y, msavi2Value);
                    msavi2Flags.setSample(x, y, msavi2FlagValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    @Override
    protected void loadSourceBands(Product product) {
        if (redSourceBand == null) {
            redSourceBand = findBand(600, 650, product);
            getLogger().info("Using band '" + redSourceBand + "' as red input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(800, 900, product);
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (redSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as red input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(Msavi2Op.class);
        }

    }
}
