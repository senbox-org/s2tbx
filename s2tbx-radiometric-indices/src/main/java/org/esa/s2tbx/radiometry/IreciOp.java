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
        alias = "IreciOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Vegetation Indices",
        description = "Inverted red-edge chlorophyll index",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class IreciOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ireci";

    @Parameter(label = "Red (B4) factor", defaultValue = "1.0F", description = "The value of the red source band (B4) is multiplied by this value.")
    private float redB4Factor;

    @Parameter(label = "Red (B5) factor", defaultValue = "1.0F", description = "The value of the red source band (B5) is multiplied by this value.")
    private float redB5Factor;

    @Parameter(label = "Red (B6) factor", defaultValue = "1.0F", description = "The value of the red source band (B6) is multiplied by this value.")
    private float redB6Factor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band 4",
            description = "The red band (B4) for the IRECI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand4;

    @Parameter(label = "Red source band 5",
            description = "The red band (B5) for the IRECI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand5;

    @Parameter(label = "Red source band 6",
            description = "The red band (B6) for the IRECI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand6;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the IRECI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing IRECI", rectangle.height);
        try {
            Tile redB4Tile = getSourceTile(getSourceProduct().getBand(redSourceBand4), rectangle);
            Tile redB5Tile = getSourceTile(getSourceProduct().getBand(redSourceBand5), rectangle);
            Tile redB6Tile = getSourceTile(getSourceProduct().getBand(redSourceBand6), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile ireci = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ireciFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ireciValue;
            int ireciFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float redB4 = redB4Factor * redB4Tile.getSampleFloat(x, y);
                    final float redB5 = redB5Factor * redB5Tile.getSampleFloat(x, y);
                    final float redB6 = redB6Factor * redB6Tile.getSampleFloat(x, y);

                    ireciValue = (nir - redB4) / (redB5 / redB6);

                    ireciFlagsValue = 0;
                    if (Float.isNaN(ireciValue) || Float.isInfinite(ireciValue)) {
                        ireciFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        ireciValue = 0.0f;
                    }
                    if (ireciValue < 0.0f) {
                        ireciFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (ireciValue > 1.0f) {
                        ireciFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    ireci.setSample(x, y, ireciValue);
                    ireciFlags.setSample(x, y, ireciFlagsValue);
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
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(IreciOp.class);
        }

    }

}
