package org.esa.s2tbx.radiometry;

import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;

import java.awt.*;

/**
 * Created by dmihailescu on 1/12/2016.
 */


public abstract class BaseIndexOp extends Operator {

    @SourceProduct(alias = "source", description = "The source product.")
    protected Product sourceProduct;
    @TargetProduct
    protected Product targetProduct;

    protected BaseIndexOp() {
    }


    protected class OperatorDescriptor {
        public String name;
        public MaskDescriptor[] maskDescriptors;

        public OperatorDescriptor(String name, MaskDescriptor[] maskDescriptors) {
            this.name = name;
            this.maskDescriptors = maskDescriptors;
        }
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

    protected class FlagCodingDescriptor {
        public String name;
        public String description;
        public FlagDescriptor[] descriptors;

        public FlagCodingDescriptor(String name, String description, FlagDescriptor[] descriptors) {
            this.name = name;
            this.description = description;
            this.descriptors = descriptors;
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


    @Override
    public void initialize() throws OperatorException {

        int sceneWidth = sourceProduct.getSceneRasterWidth();
        int sceneHeight = sourceProduct.getSceneRasterHeight();
        OperatorDescriptor operatorDescriptor = getOperatorDescriptor();

        targetProduct = new Product(operatorDescriptor.name, sourceProduct.getProductType() + "_" + operatorDescriptor.name, sceneWidth, sceneHeight);
        ProductUtils.copyTiePointGrids(sourceProduct, targetProduct);
        ProductUtils.copyGeoCoding(sourceProduct, targetProduct);
        ProductUtils.copyFlagBands(sourceProduct, targetProduct, true);
        ProductUtils.copyMasks(sourceProduct, targetProduct);
        ProductUtils.copyOverlayMasks(sourceProduct, targetProduct);

        for (MaskDescriptor maskDescriptor : operatorDescriptor.maskDescriptors) {
            targetProduct.addMask(maskDescriptor.name, maskDescriptor.expression, maskDescriptor.description, maskDescriptor.color, maskDescriptor.transparency);
        }
    }

    //abstract void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm);

    abstract protected OperatorDescriptor getOperatorDescriptor();

    abstract protected void loadSourceBands(Product product);

    protected FlagCoding createFlagCoding(FlagCodingDescriptor flagCodingDescriptor) {
        FlagCoding flagCoding = new FlagCoding(flagCodingDescriptor.name);
        flagCoding.setDescription(flagCodingDescriptor.description);

        for (FlagDescriptor descriptor : flagCodingDescriptor.descriptors) {
            MetadataAttribute attribute = new MetadataAttribute(descriptor.name, ProductData.TYPE_INT32);
            attribute.getData().setElemInt(descriptor.value);
            attribute.setDescription(descriptor.description);
            flagCoding.addAttribute(attribute);
        }

        return flagCoding;
    }


    static String findBand(float minWavelength, float maxWavelength, Product product) {
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
