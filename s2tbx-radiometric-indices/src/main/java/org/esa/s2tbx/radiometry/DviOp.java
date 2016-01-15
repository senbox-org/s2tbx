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
        alias = "DviOp",
        category = "Optical/Thematic Land Processing",
        description = "Difference Vegetation Index retrieves the Isovegetation lines parallel to soil line",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class DviOp extends BaseIndexOp{

    // constants
    public static final String DVI_BAND_NAME = "dvi";
    public static final String DVI_FLAGS_BAND_NAME = "dvi_flags";

    public static final String DVI_ARITHMETIC_FLAG_NAME = "DVI_ARITHMETIC";
    public static final String DVI_LOW_FLAG_NAME = "DVI_NEGATIVE";
    public static final String DVI_HIGH_FLAG_NAME = "DVI_SATURATION";

    public static final int DVI_ARITHMETIC_FLAG_VALUE = 1;
    public static final int DVI_LOW_FLAG_VALUE = 1 << 1;
    public static final int DVI_HIGH_FLAG_VALUE = 1 << 2;

    @Parameter(label = "Red factor", defaultValue = "1.0F", description = "The value of the red source band is multiplied by this value.")
    private float redFactor;

    @Parameter(label = "NIR factor", defaultValue = "1.0F", description = "The value of the NIR source band is multiplied by this value.")
    private float nirFactor;

    @Parameter(label = "Red source band",
            description = "The red band for the DVI computation. If not provided, the " +
                    "operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String redSourceBand;

    @Parameter(label = "NIR source band",
            description = "The near-infrared band for the DVI computation. If not provided," +
                    " the operator will try to find the best fitting band.",
            rasterDataNodeType = Band.class)
    private String nirSourceBand;


    @Override
    public void initialize() throws OperatorException {

        super.initialize();

        loadSourceBands(sourceProduct);

        Band dviOutputBand = new Band(DVI_BAND_NAME, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        targetProduct.addBand(dviOutputBand);

        Band dviFlagsOutputBand = new Band(DVI_FLAGS_BAND_NAME, ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(),
                sourceProduct.getSceneRasterHeight());
        dviFlagsOutputBand.setDescription("dvi specific flags");

        FlagCoding flagCoding = super.createFlagCoding(getFlagCodingDescriptor());
        dviFlagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(dviFlagsOutputBand);

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing DVI", rectangle.height);
        try {
            Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            Tile dvi = targetTiles.get(targetProduct.getBand(DVI_BAND_NAME));
            Tile dviFlags = targetTiles.get(targetProduct.getBand(DVI_FLAGS_BAND_NAME));

            float dviValue;
            int dviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    final float red = redFactor * redTile.getSampleFloat(x, y);

                    dviValue = nir - red;

                    dviFlagsValue = 0;
                    if (Float.isNaN(dviValue) || Float.isInfinite(dviValue)) {
                        dviFlagsValue |= DVI_ARITHMETIC_FLAG_VALUE;
                        dviValue = 0.0f;
                    }
                    if (dviValue < 0.0f) {
                        dviFlagsValue |= DVI_LOW_FLAG_VALUE;
                    }
                    if (dviValue > 1.0f) {
                        dviFlagsValue |= DVI_HIGH_FLAG_VALUE;
                    }
                    dvi.setSample(x, y, dviValue);
                    dviFlags.setSample(x, y, dviFlagsValue);
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

        return new OperatorDescriptor("dvi", new MaskDescriptor[]{
                new MaskDescriptor(DVI_ARITHMETIC_FLAG_NAME, DVI_FLAGS_BAND_NAME + "." + DVI_ARITHMETIC_FLAG_NAME,
                        "An arithmetic exception occurred.",
                        Color.red.brighter(), 0.7),
                new MaskDescriptor(DVI_LOW_FLAG_NAME, DVI_FLAGS_BAND_NAME + "." + DVI_LOW_FLAG_NAME,
                        "dvi value is too low.",
                        Color.red, 0.7),
                new MaskDescriptor(DVI_HIGH_FLAG_NAME, DVI_FLAGS_BAND_NAME + "." + DVI_HIGH_FLAG_NAME,
                        "dvi value is too high.",
                        Color.red.darker(), 0.7)
        }
        );


    }


    private FlagCodingDescriptor getFlagCodingDescriptor() {
        return new FlagCodingDescriptor("dvi_flags", "DVI Flag Coding", new FlagDescriptor[]{
                new FlagDescriptor(DVI_ARITHMETIC_FLAG_NAME, DVI_ARITHMETIC_FLAG_VALUE, "DVI value calculation failed due to an arithmetic exception"),
                new FlagDescriptor(DVI_LOW_FLAG_NAME, DVI_LOW_FLAG_VALUE, "DVI value is too low"),
                new FlagDescriptor(DVI_HIGH_FLAG_NAME, DVI_HIGH_FLAG_VALUE, "DVI value is too high")});
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(DviOp.class);
        }

    }

}
