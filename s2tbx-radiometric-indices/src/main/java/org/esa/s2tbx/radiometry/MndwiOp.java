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
        alias = "MndwiOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Water Radiometric Indices",
        description = "Modified Normalized Difference Water Index, allowing for the measurement of surface water extent",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class MndwiOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "mndwi";

    @Parameter(label = "Green factor", defaultValue = "1.0F", description = "The value of the green source band is multiplied by this value.")
    private float greenFactor;

    @Parameter(label = "MIR factor", defaultValue = "1.0F", description = "The value of the MIR source band is multiplied by this value.")
    private float mirFactor;

    @Parameter(label = "Green source band",
            description = "The green band for the MNDWI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 495, maxWavelength = 570)
    private String greenSourceBand;

    @Parameter(label = "MIR source band",
            description = "The mid-infrared band for the MNDWI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    @BandParameter(minWavelength = 3000, maxWavelength = 8000)
    private String mirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing MNDWI", rectangle.height);
        try {
            Tile greenTile = getSourceTile(getSourceProduct().getBand(greenSourceBand), rectangle);
            Tile mirTile = getSourceTile(getSourceProduct().getBand(mirSourceBand), rectangle);

            Tile mndwi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile mndwiFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float mndwiValue;
            int mndwiFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float mir = mirFactor * mirTile.getSampleFloat(x, y);
                    final float green = greenFactor * greenTile.getSampleFloat(x, y);

                    mndwiValue = (green - mir)/(green + mir);

                    mndwiFlagsValue = 0;
                    if (Float.isNaN(mndwiValue) || Float.isInfinite(mndwiValue)) {
                        mndwiFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        mndwiValue = 0.0f;
                    }
                    if (mndwiValue < 0.0f) {
                        mndwiFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (mndwiValue > 1.0f) {
                        mndwiFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    mndwi.setSample(x, y, mndwiValue);
                    mndwiFlags.setSample(x, y, mndwiFlagsValue);
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
            greenSourceBand = findBand(495, 570, product); /* (500, 590) */
            getLogger().info("Using band '" + greenSourceBand + "' as GREEN input band.");
        }
        if (mirSourceBand == null) {
            mirSourceBand = findBand(3000, 8000, product);
            getLogger().info("Using band '" + mirSourceBand + "' as MIR input band.");
        }
        if (greenSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as green input band. Please specify band.");
        }
        if (mirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as mir input band. Please specify band.");
        }
        this.sourceBandNames = new String[] { greenSourceBand, mirSourceBand };
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(MndwiOp.class);
        }

    }

}
