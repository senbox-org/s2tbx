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
    @SourceProducts(alias = "source", description = "The source products to be used for trimming.")
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
        this.trimmingStatistics = getTrimmingStatistics();
        this.statistics = new HashMap<>();
        this.statistics = computeStatistics();
    }

    private  Map<Integer, PixelSourceBands> computeStatistics() {
        Map<Integer, PixelSourceBands> statistic = new HashMap<>();
        for(Map.Entry<Integer, List<PixelSourceBands>> pair: this.trimmingStatistics.entrySet()){
            List<Float> band4PixelValues = new ArrayList<>();
            List<Float> band8PixelValues = new ArrayList<>();
            List<Float> band11PixelValues = new ArrayList<>();
            List<Float> band12PixelValues = new ArrayList<>();
            for(PixelSourceBands pixelValues: pair.getValue()){
                band4PixelValues.add(pixelValues.getValueB4Band());
                band8PixelValues.add(pixelValues.getValueB8Band());
                band11PixelValues.add(pixelValues.getValueB11Band());
                band12PixelValues.add(pixelValues.getValueB12Band());
            }
            float averageB4PixelValue = computeAveragePixel(band4PixelValues);
            float averageB8PixelValue = computeAveragePixel(band8PixelValues);
            float averageB11PixelValue = computeAveragePixel(band11PixelValues);
            float averageB12PixelValue = computeAveragePixel(band12PixelValues);
            PixelSourceBands averagePerBand = new PixelSourceBands(averageB4PixelValue,
                                                                   averageB8PixelValue,
                                                                   averageB11PixelValue,
                                                                   averageB12PixelValue);
            statistic.put(pair.getKey(), averagePerBand);
        }
        return statistic;
    }

    private float computeAveragePixel(List<Float> pixelValues) {
        float sum = 0;
        for (int i=0; i< pixelValues.size(); i++) {
            sum += i;
        }
        return sum / pixelValues.size();
    }

    private Map<Integer,List<PixelSourceBands>> getTrimmingStatistics() {
        Map<Integer,List<PixelSourceBands>> statistics;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bandsUsed", this.bandsUsed);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", this.sourceProduct);
        sourceProducts.put("sourceCompositionProduct", this.sourceCompositionProduct);
        TrimmingRegionComputingOp trimRegOp = (TrimmingRegionComputingOp) GPF.getDefaultInstance().createOperator("TrimmingRegionComputingOp", parameters, sourceProducts, null);
        Product targetProductSelection = trimRegOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(trimRegOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        statistics = trimRegOp.getPixelsStatistics();
        return  statistics;
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
