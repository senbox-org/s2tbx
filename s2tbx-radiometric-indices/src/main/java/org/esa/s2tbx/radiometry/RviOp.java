package org.esa.s2tbx.radiometry;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.*;
import java.util.Map;

@OperatorMetadata(
        alias = "RviOp",
        category = "Optical/Thematic Land Processing",
        description = "Ratio Vegetation Index retrieves the Isovegetation lines converge at origin",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class RviOp extends BaseIndexOp{

    // constants
    public static final String RVI_BAND_NAME = "rvi";
    public static final String RVI_FLAGS_BAND_NAME = "rvi_flags";

    public static final String RVI_ARITHMETIC_FLAG_NAME = "RVI_ARITHMETIC";
    public static final String RVI_LOW_FLAG_NAME = "RVI_NEGATIVE";
    public static final String RVI_HIGH_FLAG_NAME = "RVI_SATURATION";

    public static final int RVI_ARITHMETIC_FLAG_VALUE = 1;
    public static final int RVI_LOW_FLAG_VALUE = 1 << 1;
    public static final int RVI_HIGH_FLAG_VALUE = 1 << 2;

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the RVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the RVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;


    @Override
    public void initialize() throws OperatorException {

        super.initialize();

        loadSourceBands(sourceProduct);

        Band rviOutputBand = new Band(RVI_BAND_NAME, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        targetProduct.addBand(rviOutputBand);

        Band rviFlagsOutputBand = new Band(RVI_FLAGS_BAND_NAME, ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        rviFlagsOutputBand.setDescription("rvi specific flags");

        FlagCoding flagCoding = super.createFlagCoding(getFlagCodingDescriptor());
        rviFlagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(rviFlagsOutputBand);

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing RVI", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile rvi = targetTiles.get(targetProduct.getBand(RVI_BAND_NAME));
            Tile rviFlags = targetTiles.get(targetProduct.getBand(RVI_FLAGS_BAND_NAME));

            float rviValue;
            int rviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    rviValue = nir / red;

                    rviFlagsValue = 0;
                    if (Float.isNaN(rviValue) || Float.isInfinite(rviValue)) {
                        rviFlagsValue |= RVI_ARITHMETIC_FLAG_VALUE;
                        rviValue = 0.0f;
                    }
                    if (rviValue < 0.0f) {
                        rviFlagsValue |= RVI_LOW_FLAG_VALUE;
                    }
                    if (rviValue > 1.0f) {
                        rviFlagsValue |= RVI_HIGH_FLAG_VALUE;
                    }
                    rvi.setSample(x, y, rviValue);
                    rviFlags.setSample(x, y, rviFlagsValue);
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
    }


    @Override
    protected OperatorDescriptor getOperatorDescriptor() {

        return new OperatorDescriptor("rvi", new MaskDescriptor[]{
                new MaskDescriptor(RVI_ARITHMETIC_FLAG_NAME, RVI_FLAGS_BAND_NAME + "." + RVI_ARITHMETIC_FLAG_NAME,
                        "An arithmetic exception occurred.",
                        Color.red.brighter(), 0.7),
                new MaskDescriptor(RVI_LOW_FLAG_NAME, RVI_FLAGS_BAND_NAME + "." + RVI_LOW_FLAG_NAME,
                        "rvi value is too low.",
                        Color.red, 0.7),
                new MaskDescriptor(RVI_HIGH_FLAG_NAME, RVI_FLAGS_BAND_NAME + "." + RVI_HIGH_FLAG_NAME,
                        "rvi value is too high.",
                        Color.red.darker(), 0.7)
        }
        );


    }


    private FlagCodingDescriptor getFlagCodingDescriptor() {
        return new FlagCodingDescriptor("rvi_flags", "RVI Flag Coding", new FlagDescriptor[]{
                new FlagDescriptor(RVI_ARITHMETIC_FLAG_NAME, RVI_ARITHMETIC_FLAG_VALUE, "RVI value calculation failed due to an arithmetic exception"),
                new FlagDescriptor(RVI_LOW_FLAG_NAME, RVI_LOW_FLAG_VALUE, "RVI value is too low"),
                new FlagDescriptor(RVI_HIGH_FLAG_NAME, RVI_HIGH_FLAG_VALUE, "RVI value is too high")});
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(RviOp.class);
        }

    }

}
