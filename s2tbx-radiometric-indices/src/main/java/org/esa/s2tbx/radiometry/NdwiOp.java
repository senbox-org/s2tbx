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
        alias = "NdwiOp",
        version="1.0",
        category = "Optical/Thematic Land Processing/Radiometric Indices/Water Indices",
        description = "The Normalized Difference Water Index was developed for the extraction of water features",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class NdwiOp extends BaseIndexOp{

    // constants
    public static final String BAND_NAME = "ndwi";

    @Parameter(label = "MIR factor", defaultValue = "1.0F", description = "The value of the MIR source band is multiplied by this value.")
    private float mirFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "MIR source band",
            description = "The mid-infrared band for the NDWI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String mirSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the NDWI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;

    @Override
    public String getBandName() {
        return BAND_NAME;
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing NDWI", rectangle.height);
        try {
            Tile mirTile = getSourceTile(getSourceProduct().getBand(mirSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile ndwi = targetTiles.get(targetProduct.getBand(BAND_NAME));
            Tile ndwiFlags = targetTiles.get(targetProduct.getBand(FLAGS_BAND_NAME));

            float ndwiValue;
            int ndwiFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {

                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float mir = mirFactor * mirTile.getSampleFloat(x, y);

                    ndwiValue = (nir - mir)/(nir + mir);

                    ndwiFlagsValue = 0;
                    if (Float.isNaN(ndwiValue) || Float.isInfinite(ndwiValue)) {
                        ndwiFlagsValue |= ARITHMETIC_FLAG_VALUE;
                        ndwiValue = 0.0f;
                    }
                    if (ndwiValue < 0.0f) {
                        ndwiFlagsValue |= LOW_FLAG_VALUE;
                    }
                    if (ndwiValue > 1.0f) {
                        ndwiFlagsValue |= HIGH_FLAG_VALUE;
                    }
                    ndwi.setSample(x, y, ndwiValue);
                    ndwiFlags.setSample(x, y, ndwiFlagsValue);
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
        if (mirSourceBand == null) {
            mirSourceBand = findBand(3000, 8000, product);
            getLogger().info("Using band '" + mirSourceBand + "' as MIR input band.");
        }
        if (nirSourceBand == null) {
            nirSourceBand = findBand(800, 900, product);
            getLogger().info("Using band '" + nirSourceBand + "' as NIR input band.");
        }
        if (mirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as mir input band. Please specify band.");
        }
        if (nirSourceBand == null) {
            throw new OperatorException("Unable to find band that could be used as nir input band. Please specify band.");
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(NdwiOp.class);
        }

    }

}
