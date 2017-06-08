package org.esa.s2tbx.fcc.intern;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.esa.s2tbx.fcc.mahalanobis.MahalanobisDistance;
import org.esa.s2tbx.grm.segmentation.Node;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
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
    @SourceProduct(alias = "source", description = "The segmentation source product with segments that have more than 95% forest cover")
    private Product sourceProduct;

    @SourceProduct(alias = "sourceCompositionProduct ", description = "The source products to be used for trimming.")
    private Product sourceCompositionProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "bandsUsed", description = "the index from the sourceCompositionProduct to be used")
    private int[] bandsUsed;

    private Int2ObjectMap<PixelSourceBands> statistics;
    private Int2ObjectMap<List<PixelSourceBands>> trimmingStatistics;

    @Override
    public void initialize() throws OperatorException {
        this.trimmingStatistics = computeTrimmingStatistics();
        this.statistics = computeStatistics();

        Object2DoubleMap<PixelSourceBands> result = MahalanobisDistance.computeMahalanobisSquareMatrix(this.statistics.values());

//        Int2ObjectMap<PixelSourceBands> statistic = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
        ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = this.statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
        }
    }

    public Int2ObjectMap<PixelSourceBands> computeStatistics() {
        Int2ObjectMap<PixelSourceBands> statistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();
        ObjectIterator<Int2ObjectMap.Entry<List<PixelSourceBands>>> it = this.trimmingStatistics.int2ObjectEntrySet().iterator();

//        double averageSumB4PixelValue = 0.0d;
//        double averageSumB8PixelValue = 0.0d;
//        double averageSumB11PixelValue = 0.0d;
//        double averageSumStandardDeviationB8PixelValue = 0.0d;
        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<PixelSourceBands>> entry = it.next();
            List<PixelSourceBands> pixelsList = entry.getValue();

            double sumB4PixelValue = 0.0d;
            double sumB8PixelValue = 0.0d;
            double sumB11PixelValue = 0.0d;
            double sumStandardDeviationB8PixelValue = 0.0d;
            for (PixelSourceBands pixelValues: pixelsList) {
                sumB4PixelValue += pixelValues.getMeanValueB4Band();
                sumB8PixelValue += pixelValues.getMeanValueB8Band();
                sumB11PixelValue += pixelValues.getMeanValueB11Band();
                sumStandardDeviationB8PixelValue += pixelValues.getStandardDeviationValueB8Band();
            }

            double averageB4PixelValue = sumB4PixelValue / pixelsList.size();
            double averageB8PixelValue = sumB8PixelValue / pixelsList.size();
            double averageB11PixelValue = sumB11PixelValue / pixelsList.size();
            // compute the standard deviation
            double averageStandardDeviationB8PixelValue = sumStandardDeviationB8PixelValue / pixelsList.size();
            double sum = 0.0d;
            for (PixelSourceBands pixelValues: pixelsList) {
                double value = pixelValues.getStandardDeviationValueB8Band() - averageStandardDeviationB8PixelValue;
                sum += Math.pow((value), 2);
            }
            double average = sum / (double)pixelsList.size();
            averageStandardDeviationB8PixelValue = Math.sqrt(average);

//            averageSumB4PixelValue += averageB4PixelValue;
//            averageSumB8PixelValue += averageB8PixelValue;
//            averageSumB11PixelValue += averageB11PixelValue;
//            averageSumStandardDeviationB8PixelValue += averageStandardDeviationB8PixelValue;

            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue, averageB8PixelValue, averageB11PixelValue, averageStandardDeviationB8PixelValue);
            statistics.put(entry.getIntKey(), averagePerBand);
        }

//        double centeredAverageB4PixelValue = averageSumB4PixelValue / (double) statistics.size();
//        double centeredAverageB8PixelValue = averageSumB8PixelValue / (double) statistics.size();
//        double centeredAverageB11PixelValue = averageSumB11PixelValue / (double) statistics.size();
//        double centeredAverageStandardDeviationB8PixelValue = averageSumStandardDeviationB8PixelValue / (double) statistics.size();

        return statistics;
    }

    private static void computeDistance(Int2ObjectMap<PixelSourceBands> statistics, double centeredAverageB4PixelValue, double centeredAverageB8PixelValue,
                                 double centeredAverageB11PixelValue, double centeredAverageStandardDeviationB8PixelValue) {

        ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
        }
    }

    private Int2ObjectMap<List<PixelSourceBands>> computeTrimmingStatistics() {
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

    public Map<Integer, PixelSourceBands> getStatistics() {
        return this.statistics;
    }

    public static class Spi extends OperatorSpi {

        public Spi(){
            super(TrimmingOp.class);
        }
    }
}
