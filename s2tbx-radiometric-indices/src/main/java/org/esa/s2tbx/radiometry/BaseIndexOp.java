package org.esa.s2tbx.radiometry;

import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String RESAMPLE_NONE = "None";
    public static final String RESAMPLE_LOWEST = "Lowest resolution";
    public static final String RESAMPLE_HIGHEST = "Hightest resolution";

    @SourceProduct(alias = "source", description = "The source product.")
    protected Product sourceProduct;
    @TargetProduct
    protected Product targetProduct;

    @Parameter(label = "Resample Type",
            description = "If selected bands differ in size, the resample method used before computing the index",
            defaultValue = RESAMPLE_NONE, valueSet = { RESAMPLE_NONE, RESAMPLE_LOWEST, RESAMPLE_HIGHEST })
    protected String resampleType;
    @Parameter(alias = "upsampling",
            label = "Upsampling method",
            description = "The method used for interpolation (upsampling to a finer resolution).",
            valueSet = {"Nearest", "Bilinear", "Bicubic"},
            defaultValue = "Nearest")
    protected String upsamplingMethod;
    @Parameter(alias = "downsampling",
            label = "Downsampling method",
            description = "The method used for aggregation (downsampling to a coarser resolution).",
            valueSet = {"First", "Min", "Max", "Mean", "Median"},
            defaultValue = "First")
    private String downsamplingMethod;

    protected String[] sourceBandNames;

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

        loadSourceBands(sourceProduct);
        int sceneWidth = 0, sceneHeight = 0;
        int resampleStrategy;
        switch (this.resampleType) {
            case RESAMPLE_LOWEST:
                resampleStrategy = 1;
                break;
            case RESAMPLE_HIGHEST:
                resampleStrategy = 2;
                break;
            default:
                resampleStrategy = 0;
                break;
        }
        if (resampleStrategy != 0 && this.sourceBandNames != null && this.sourceBandNames.length > 0) {
            for (String bandName : this.sourceBandNames) {
                Band band = this.sourceProduct.getBand(bandName);
                int bandRasterWidth = band.getRasterWidth();
                if (resampleStrategy == 2) {
                    if (sceneWidth < bandRasterWidth) {
                        sceneWidth = bandRasterWidth;
                        sceneHeight = band.getRasterHeight();
                    }
                } else {
                    if (sceneWidth == 0 || sceneWidth >= bandRasterWidth) {
                        sceneWidth = bandRasterWidth;
                        sceneHeight = band.getRasterHeight();
                    }
                }
            }
        } else {
            sceneWidth = sourceProduct.getSceneRasterWidth();
            sceneHeight = sourceProduct.getSceneRasterHeight();
        }
        if (resampleStrategy != 0) {
            this.sourceProduct = resample(this.sourceProduct, sceneWidth, sceneHeight);
        }

        initDefaultMasks();

        String name = getBandName();

        targetProduct = new Product(name, sourceProduct.getProductType() + "_" + name, sceneWidth, sceneHeight);
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        //Band outputBand = new Band(name, ProductData.TYPE_FLOAT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        Band outputBand = new Band(name, ProductData.TYPE_FLOAT32, sceneWidth, sceneHeight);
        targetProduct.addBand(outputBand);

        //Band flagsOutputBand = new Band(FLAGS_BAND_NAME, ProductData.TYPE_INT32, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        Band flagsOutputBand = new Band(FLAGS_BAND_NAME, ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        flagsOutputBand.setDescription(name + " specific flags");

        FlagCoding flagCoding = initFlagCoding();
        flagsOutputBand.setSampleCoding(flagCoding);

        targetProduct.getFlagCodingGroup().add(flagCoding);
        targetProduct.addBand(flagsOutputBand);

        for (MaskDescriptor maskDescriptor : getMaskDescriptors()) {
            targetProduct.addMask(maskDescriptor.name, maskDescriptor.expression, maskDescriptor.description, maskDescriptor.color, maskDescriptor.transparency);
        }

    }

    @Override
    public Product getSourceProduct() {
        return this.sourceProduct;
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

    private Product resample(Product source, int targetWidth, int targetHeight) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("referenceBandName", null);
        parameters.put("targetWidth", targetWidth);
        parameters.put("targetHeight", targetHeight);
        parameters.put("targetResolution", null);
        if (RESAMPLE_LOWEST.equals(this.resampleType)) {
            parameters.put("downsampling", this.downsamplingMethod);
        } else if (RESAMPLE_HIGHEST.equals(this.resampleType)) {
            parameters.put("upsampling", this.upsamplingMethod);
        }
        return GPF.createProduct("Resample", parameters, source);
    }

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

    public static String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float minDelta = Float.MAX_VALUE;
        float mean = (minWavelength + maxWavelength) / 2;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float delta = Math.abs(bandWavelength - mean);
                if (delta < minDelta) {
                    bestBand = band.getName();
                    minDelta = delta;
                }
            }
        }
        return bestBand;
    }

}
