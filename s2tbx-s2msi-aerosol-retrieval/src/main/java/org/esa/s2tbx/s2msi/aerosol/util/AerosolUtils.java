/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esa.s2tbx.s2msi.aerosol.util;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.aerosol.AotConsts;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.common.BandMathsOp;
import org.esa.snap.core.util.Guardian;

import java.awt.*;
import java.io.IOException;

/**
 *
 * @author akheckel
 */
public class AerosolUtils {

    public static void createFlagMasks(Product targetProduct) {
        Guardian.assertNotNull("targetProduct", targetProduct);
        int w = targetProduct.getSceneRasterWidth();
        int h = targetProduct.getSceneRasterHeight();

        MyMaskColor mColor = new MyMaskColor();
        ProductNodeGroup<Mask> tarMG = targetProduct.getMaskGroup();
        ProductNodeGroup<FlagCoding> tarFCG = targetProduct.getFlagCodingGroup();
        for (int node=0; node<tarFCG.getNodeCount(); node++){
            FlagCoding fc = tarFCG.get(node);
            for (int i=0; i<fc.getNumAttributes(); i++){
                MetadataAttribute f = fc.getAttributeAt(i);
                String expr = fc.getName() + "." + f.getName();
                Mask m = Mask.BandMathsType.create(f.getName(), f.getDescription(), w, h, expr, mColor.next(), 0.5);
                tarMG.add(m);
            }
        }
    }

    public static Band createTargetBand(AotConsts bandFeat, int rasterWidth, int rasterHeight) {
        Band targetBand = new Band(bandFeat.name,
                                   bandFeat.type,
                                   rasterWidth,
                                   rasterHeight);
        targetBand.setDescription(bandFeat.description);
        targetBand.setNoDataValue(bandFeat.noDataValue);
        targetBand.setNoDataValueUsed(bandFeat.noDataUsed);
        targetBand.setUnit(bandFeat.unit);
        targetBand.setScalingFactor(bandFeat.scale);
        targetBand.setScalingOffset(bandFeat.offset);
        return targetBand;
    }

    public static Band createBooleanExpressionBand(String expression, Product sourceProduct) {
        BandMathsOp.BandDescriptor bandDescriptor = new BandMathsOp.BandDescriptor();
        bandDescriptor.name = "band1";
        bandDescriptor.expression = expression;
        bandDescriptor.type = ProductData.TYPESTRING_INT8;

        BandMathsOp bandMathsOp = new BandMathsOp();
        bandMathsOp.setParameterDefaultValues();
        bandMathsOp.setSourceProduct(sourceProduct);
        bandMathsOp.setTargetBandDescriptors(bandDescriptor);
        return bandMathsOp.getTargetProduct().getBandAt(0);
    }

    public static double[] normalize(double[] doubles) {
        double sum = 0;
        for (double d : doubles) {
            sum += d;
        }
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] /= sum;
        }
        return doubles;
    }

}
