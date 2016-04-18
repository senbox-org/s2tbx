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
        alias = "MsaviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Indices to Minimize Soil Noise",
        description = "This retrieves the Modified Soil Adjusted Vegetation Index (MSAVI).",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class MsaviOp extends BaseIndexOp {

    // constants
    static final String BAND_NAME = "msavi";

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the RED source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Soil line slope", defaultValue = "0.5F", description = "The soil line slope.")
    private float slope;

    @Parameter(label = "Red source band",
            description = "The red band for the MSAVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the MSAVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing msavi", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile msavi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile msaviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float msaviValue;
            int msaviFlagValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    final float ndviIndexValue = (nir - red) / (nir + red);
                    final float wdviIndexValue =  nir - (slope * red);
                    final float correctionFactor = 1 - (2 * slope * ndviIndexValue * wdviIndexValue);

                    msaviValue = (1 + correctionFactor) * (nir - red) / (nir + red + correctionFactor);

                    msaviFlagValue = 0;
                    if (Float.isNaN(msaviValue) || Float.isInfinite(msaviValue)) {
                        msaviFlagValue |= ARITHMETIC_FLAG_VALUE;
                        msaviValue = 0.0f;
                    }
                    if (msaviValue < 0.0f) {
                        msaviFlagValue |= LOW_FLAG_VALUE;
                    }
                    if (msaviValue > 1.0f) {
                        msaviFlagValue |= HIGH_FLAG_VALUE;
                    }
                    msavi.setSample(x, y, msaviValue);
                    msaviFlags.setSample(x, y, msaviFlagValue);
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
            super(MsaviOp.class);
        }

    }
}
