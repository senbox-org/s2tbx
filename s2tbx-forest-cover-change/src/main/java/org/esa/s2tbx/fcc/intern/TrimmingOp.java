package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "TrimmingOp",
        version="1.0",
        category = "",
        description = "",
        authors = "Razvan Dumitrascu, Jean Coravu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class TrimmingOp extends Operator{

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(alias = "source", description = "The segmentation source product with segments that have more than 95% forest cover")
    private Product sourceProduct;

    @SourceProducts(alias = "sourceCompositionProduct ", description = "The source products to be used for trimming.")
    private Product sourceCompositionProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "bandsUsed", description = "the index from the sourceCompositionProduct to be used")
    int[] bandsUsed;

    private Map<Integer, PixelSourceBands> statistics;
    private Map<Integer, List<PixelSourceBands>> trimmingStatistics;

    @Override
    public void initialize() throws OperatorException {
        this.trimmingStatistics = computeTrimmingStatistics();
        this.statistics = new HashMap<>();
        this.statistics = computeStatistics();
    }

    public Map<Integer, PixelSourceBands> computeStatistics() {
        Map<Integer, PixelSourceBands> statistic = new HashMap<>();
        for(Map.Entry<Integer, List<PixelSourceBands>> pair: trimmingStatistics.entrySet()){
            List<Double> meanBand4PixelValues = new ArrayList<>();
            List<Double> meanBand8PixelValues = new ArrayList<>();
            List<Double> meanBand11PixelValues = new ArrayList<>();
            List<Double> standardDevationBand8PixelValues = new ArrayList<>();
            for(PixelSourceBands pixelValues: pair.getValue()){
                meanBand4PixelValues.add(pixelValues.getMeanValueB4Band());
                meanBand8PixelValues.add(pixelValues.getMeanValueB8Band());
                meanBand11PixelValues.add(pixelValues.getMeanValueB11Band());
                standardDevationBand8PixelValues.add(pixelValues.getStandardDeviationValueB8Band());
            }
            double averageB4PixelValue = computeAveragePixel(meanBand4PixelValues);
            double averageB8PixelValue = computeAveragePixel(meanBand8PixelValues);
            double averageB11PixelValue = computeAveragePixel(meanBand11PixelValues);
            double averageB12PixelValue = ComputeStandardDeviation(standardDevationBand8PixelValues);
            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue,
                                                                   averageB8PixelValue,
                                                                   averageB11PixelValue,
                                                                   averageB12PixelValue);
            statistic.put(pair.getKey(), averagePerBand);
        }
        return statistic;
    }

    private double ComputeStandardDeviation(List<Double> standardDevationBand8PixelValues) {
       double mean  = computeAveragePixel(standardDevationBand8PixelValues);
        List<Double> l = new ArrayList<>();
        for(double stdDevBand11PixelValues : standardDevationBand8PixelValues){
            l.add(Math.pow((stdDevBand11PixelValues-mean),2));
        }
        return Math.sqrt(computeAveragePixel(l));
    }

    private static double computeAveragePixel(List<Double> pixelValues) {
        float sum = 0;
        for (int i=0; i< pixelValues.size(); i++) {
            sum += i;
        }
        return sum / pixelValues.size();
    }

    private Map<Integer,List<PixelSourceBands>> computeTrimmingStatistics() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bandsUsed", this.bandsUsed);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", this.sourceProduct);
        sourceProducts.put("sourceCompositionProduct", this.sourceCompositionProduct);
        TrimmingRegionComputingOp trimRegOp = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
        trimRegOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(trimRegOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        return trimRegOp.getPixelsStatistics();
    }

    public Map<Integer, PixelSourceBands> getStatistics(){
        return this.statistics;
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingOp.class);
        }
    }
}
