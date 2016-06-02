package org.esa.s2tbx.radiometry;

import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dragos Mihailescu
 */
public abstract class BaseIndexOp extends Operator {

    protected static final String FLAGS_BAND_NAME = "flags";

    protected static final String ARITHMETIC_FLAG_NAME = "ARITHMETIC";
    protected static final String LOW_FLAG_NAME = "NEGATIVE";
    protected static final String HIGH_FLAG_NAME = "SATURATION";

    protected static final int ARITHMETIC_FLAG_VALUE = 1;
    protected static final int LOW_FLAG_VALUE = 1 << 1;
    protected static final int HIGH_FLAG_VALUE = 1 << 2;

    @SourceProduct(alias = "source", description = "The source product.")
    protected Product sourceProduct;
    @TargetProduct
    protected Product targetProduct;

    private FlagCoding flagCoding;
    private List<MaskDescriptor> maskDescriptors;

    protected BaseIndexOp() {
        maskDescriptors = new ArrayList<>();
    }

    protected class MaskDescriptor {

        String name;
        String expression;
        String description;
        Color color;
        double transparency;

        public MaskDescriptor(String name, String expression, String description, Color color, double transparency) {
            this.name = name;
            this.expression = expression;
            this.description = description;
            this.color = color;
            this.transparency = transparency;
        }
    }

    protected class FlagDescriptor {
        public String name;
        public int value;
        public String description;

        public FlagDescriptor(String name, int value, String description) {
            this.name = name;
            this.value = value;
            this.description = description;
        }
    }

    public abstract String getBandName();

    @Override
    public void initialize() throws OperatorException {

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();

        initDefaultMasks();

        String name = getBandName();

        targetProduct = new Product(name, sourceProduct.getProductType() + "_" + name, sceneWidth, sceneHeight);
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        loadSourceBands(sourceProduct);

        Band outputBand = new Band(name, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        targetProduct.addBand(outputBand);

        Band flagsOutputBand = new Band(FLAGS_BAND_NAME, ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        flagsOutputBand.setDescription(name + " specific flags");

        FlagCoding flagCoding = initFlagCoding();
        flagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(flagsOutputBand);

        for (MaskDescriptor maskDescriptor : getMaskDescriptors()) {
                targetProduct.addMask(maskDescriptor.name, maskDescriptor.expression, maskDescriptor.description, maskDescriptor.color, maskDescriptor.transparency);
        }

    }

    protected void addMaskDescriptor(String name, String expression, String description, Color color, double transparency) {
        maskDescriptors.add(new MaskDescriptor(name, expression, description, color, transparency));
    }

    protected List<MaskDescriptor> getMaskDescriptors() { return maskDescriptors; }

    protected void addFlagDescriptor(String name, int value, String description) {
        MetadataAttribute attribute = new MetadataAttribute(name, ProductData.TYPE_INT32);
        attribute.getData().setElemInt(value);
        attribute.setDescription(description);
        flagCoding.addAttribute(attribute);
    }

    protected abstract void loadSourceBands(Product product);

    private void initDefaultMasks() {
        addMaskDescriptor(ARITHMETIC_FLAG_NAME, FLAGS_BAND_NAME + "." + ARITHMETIC_FLAG_NAME,
                            "An arithmetic exception occurred.", Color.red.brighter(), 0.7);
        addMaskDescriptor(LOW_FLAG_NAME, FLAGS_BAND_NAME + "." + LOW_FLAG_NAME,
                            "Index value is too low.", Color.red, 0.7);
        addMaskDescriptor(HIGH_FLAG_NAME, FLAGS_BAND_NAME + "." + HIGH_FLAG_NAME,
                            "Index value is too high.", Color.red.darker(), 0.7);
    }

    private FlagCoding initFlagCoding() {
        flagCoding = new FlagCoding(FLAGS_BAND_NAME);
        flagCoding.setDescription("Index Flag Coding");
        addFlagDescriptor(ARITHMETIC_FLAG_NAME, ARITHMETIC_FLAG_VALUE, "Value calculation failed due to an arithmetic exception");
        addFlagDescriptor(LOW_FLAG_NAME, LOW_FLAG_VALUE, "Index value is too low");
        addFlagDescriptor(HIGH_FLAG_NAME, HIGH_FLAG_VALUE, "Index value is too high");
        return flagCoding;
    }

    protected String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float bestBandLowerDelta = Float.MAX_VALUE;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float lowerDelta = bandWavelength - minWavelength;
                if (lowerDelta < bestBandLowerDelta && bandWavelength <= maxWavelength && bandWavelength >= minWavelength) {
                    bestBand = band.getName();
                    bestBandLowerDelta = lowerDelta;
                }
            }
        }
        return bestBand;
    }

}
