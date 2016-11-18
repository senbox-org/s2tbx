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
        alias = "GndviOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Vegetation Radiometric Indices",
        description = "Green Normalized Difference Vegetation Index",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class GndviOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "gndvi";

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Green source band",
            description = "The green band for the GNDVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 543, maxWavelength = 577)
    private String greenSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the GNDVI computation. If not provided," +
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
        pm.beginTask("Computing GNDVI", rectangle.height);
        try {
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile gndvi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile gndviFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float gndviValue;
            int gndviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    gndviValue = (nir - green)/(nir + green);

                    gndviFlagsValue = 0;
                    if (Float.isNaN(gndviValue) || Float.isInfinite(gndviValue)) {
                        gndviFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        gndviValue = 0.0f;
                    }
                    if (gndviValue < 0.0f) {
                        gndviFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (gndviValue > 1.0f) {
                        gndviFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    gndvi.setSample(x, y, gndviValue);
                    gndviFlags.setSample(x, y, gndviFlagsValue);
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
        if (greenSourceBand == null) {
            greenSourceBand = findBand(543, 577, product); /* (495, 570) */
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(773, 793, product); /* (800, 900) */
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { greenSourceBand, nirSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(GndviOp.class);
        }

    }

}
