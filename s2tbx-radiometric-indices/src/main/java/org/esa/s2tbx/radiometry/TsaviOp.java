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
 * @author Cosmin Cara
 */
@OperatorMetadata(
        alias = "TsaviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "This retrieves the Transformed Soil Adjusted Vegetation Index (TSAVI).",
        authors = "Cosmin Cara",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class TsaviOp extends BaseIndexOp {

    // constants
    static final String BAND_NAME = "tsavi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the RED source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Soil line slope", defaultValue = "0.5F", description = "The soil line slope.")
    private float slope;

    @Parameter(label = "Soil line intercept", defaultValue = "0.5F", description = "The soil line intercept.")
    private float intercept;

    @Parameter(label = "Adjustment", defaultValue = "0.08F", description = "Adjustment factor to minimize soil background.")
    private float adjustment;

    @Parameter(label = "Red source band",
            description = "The red band for the TSAVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the TSAVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing tsavi", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile tsavi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile tsaviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float tsaviValue;
            int tsaviFlagValue;

            float constant1 = slope * intercept;
            float constant2 =  adjustment * (1 + slope * slope);

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    tsaviValue = slope * (nir - slope * red - intercept) / (intercept * nir + red - constant1 + constant2);

                    tsaviFlagValue = 0;
                    if (Float.isNaN(tsaviValue) || Float.isInfinite(tsaviValue)) {
                        tsaviFlagValue |= ARITHMETIC_FLAG_VALUE;
                        tsaviValue = 0.0f;
                    }
                    if (tsaviValue < 0.0f) {
                        tsaviFlagValue |= LOW_FLAG_VALUE;
                    }
                    if (tsaviValue > 1.0f) {
                        tsaviFlagValue |= HIGH_FLAG_VALUE;
                    }
                    tsavi.setSample(x, y, tsaviValue);
                    tsaviFlags.setSample(x, y, tsaviFlagValue);
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
            super(TsaviOp.class);
        }

    }
}
