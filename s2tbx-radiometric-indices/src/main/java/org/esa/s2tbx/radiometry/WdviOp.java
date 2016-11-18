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
        alias = "WdviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Weighted Difference Vegetation Index retrieves the Isovegetation lines parallel to soil line. Soil line has an arbitrary slope and passes through origin",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class WdviOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "wdvi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Slope of the soil line", defaultValue = "0.5F", description = "Soil line has an arbitrary slope and passes through origin")
    private float slopeSoilLine; /* slope = tan(angle) */

    @Parameter(label = "Red source band",
            description = "The red band for the WDVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 600, maxWavelength = 650)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the WDVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 800, maxWavelength = 900)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }


    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing WDVI", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile wdvi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile wdviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float wdviValue;
            int wdviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    wdviValue = nir - (slopeSoilLine * red);

                    wdviFlagsValue = 0;
                    if (Float.isNaN(wdviValue) || Float.isInfinite(wdviValue)) {
                        wdviFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        wdviValue = 0.0f;
                    }
                    if (wdviValue < 0.0f) {
                        wdviFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (wdviValue > 1.0f) {
                        wdviFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    wdvi.setSample(x, y, wdviValue);
                    wdviFlags.setSample(x, y, wdviFlagsValue);
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    protected void loadSourceBands(Product product) throws OperatorException {
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
        this.sourceBandNames = new String[] { redSourceBand, nirSourceBand };
    }


    public static class Spi extends OperatorSpi {

        public Spi() {
            super(WdviOp.class);
        }

    }

}
