package org.esa.s2tbx.fcc.intern;

        import java.io.File;
        import java.io.IOException;
        import java.util.*;

        import com.bc.ceres.core.ProgressMonitor;
        import com.bc.ceres.core.SubProgressMonitor;
        import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
        import it.unimi.dsi.fastutil.ints.IntSet;
        import org.esa.s2tbx.grm.GenericRegionMergingOp;
        import org.esa.snap.core.datamodel.Band;
        import org.esa.snap.core.datamodel.Product;
        import org.esa.snap.core.datamodel.ProductNodeGroup;
        import org.esa.snap.core.gpf.GPF;
        import org.esa.snap.core.gpf.Operator;
        import org.esa.snap.core.gpf.OperatorException;
        import org.esa.snap.core.gpf.OperatorSpi;
        import org.esa.snap.core.gpf.annotations.Parameter;
        import org.esa.snap.core.gpf.annotations.SourceProduct;
        import org.esa.snap.core.gpf.annotations.TargetProduct;
        import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
        import org.esa.snap.core.gpf.descriptor.SourceProductDescriptor;
        import org.esa.snap.core.gpf.descriptor.SourceProductsDescriptor;
        import org.esa.snap.core.gpf.internal.OperatorExecutor;
        import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class BandsExtractor {

    public static Product resampleAllBands(Product sourceProduct) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", sourceProduct.getSceneRasterWidth());
        parameters.put("targetHeight", sourceProduct.getSceneRasterHeight());
        Product targetProduct = GPF.createProduct("Resample", parameters, sourceProduct);
        targetProduct.setName(sourceProduct.getName());
        return targetProduct;
    }

    public static Product generateBandsDifference(Product firstSourceProduct, Product secondSourceProduct) {
        Product[] products = new Product[] {firstSourceProduct, secondSourceProduct};
        Map<String, Object> parameters = new HashMap<>();
        return GPF.createProduct("BandsDifferenceOp", parameters, products, null);
    }

    public static Product generateBandsExtractor(Product firstSourceProduct, int[] indexes) {
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", firstSourceProduct);
        parameters.put("indexes", indexes);
        return GPF.createProduct("BandsExtractorOp", parameters, sourceProducts, null);
    }

    public static void writeProduct(Product outputProduct, String fileName){
        File file = new File("D:\\Forest_cover_changes\\"+ fileName);
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String formatName = "BEAM-DIMAP";
        GPF.writeProduct(outputProduct, file, formatName, false, false, ProgressMonitor.NULL);
    }

    public static Product runUnionMasksOp(IntSet currentSegmentationTrimmingRegionKeys, Product currentSegmentationSourceProduct,
                                          IntSet previousSegmentationTrimmingRegionKeys, Product previousSegmentationSourceProduct) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("currentSegmentationTrimmingRegionKeys", currentSegmentationTrimmingRegionKeys);
        parameters.put("previousSegmentationTrimmingRegionKeys", previousSegmentationTrimmingRegionKeys);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSegmentationSourceProduct", currentSegmentationSourceProduct);
        sourceProducts.put("previousSegmentationSourceProduct", previousSegmentationSourceProduct);
        Operator unionMasksOp = GPF.getDefaultInstance().createOperator("UnionMasksOp", parameters, sourceProducts, null);
        Product targetProductSelection = unionMasksOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(unionMasksOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProductSelection;
    }

    public static Product runColorFillerOp(Product firstProduct) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("percentagePixels", 95f);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", firstProduct);
        ColorFillerOp colFillOp = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
        Product targetProductSelection = colFillOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(colFillOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        writeProduct(targetProductSelection, "colorFillerOp");

        return targetProductSelection;
    }

    public static Product runSegmentation(Product firstSourceProduct, Product secondSourceProduct, Product bandsDifferenceProduct) {
        String[][] sourceBandNames = new String[3][];
        sourceBandNames[0] = buildBandNamesArray(firstSourceProduct);
        sourceBandNames[1] = buildBandNamesArray(secondSourceProduct);
        sourceBandNames[2] = buildBandNamesArray(bandsDifferenceProduct);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION);
        parameters.put("regionMergingCriterion", GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION);
        parameters.put("totalIterationsForSecondSegmentation", 10);
        parameters.put("threshold", 5.0f);
        parameters.put("spectralWeight", 0.5f);
        parameters.put("shapeWeight", 0.5f);
        parameters.put("sourceBandNames", sourceBandNames);

        Product[] sourceProducts = new Product[] {firstSourceProduct, secondSourceProduct, bandsDifferenceProduct};

        String operatorName = "SeveralSourcesGenericRegionMergingOp";

        Map<String, Product> sourceProductMap = new HashMap<>(sourceProducts.length * 3);
        if (sourceProducts.length > 0) {
            OperatorSpi operatorSpi = GPF.getDefaultInstance().getOperatorSpiRegistry().getOperatorSpi(operatorName);
            if (operatorSpi == null) {
                throw new OperatorException(
                        String.format("Unknown operator '%s'. Note that operator aliases are case sensitive.", operatorName));
            }

            OperatorDescriptor operatorDescriptor = operatorSpi.getOperatorDescriptor();
            SourceProductDescriptor[] sourceProductDescriptors = operatorDescriptor.getSourceProductDescriptors();
            if(sourceProductDescriptors.length > 0) {
                sourceProductMap.put(GPF.SOURCE_PRODUCT_FIELD_NAME, sourceProducts[0]);
            }

            SourceProductsDescriptor sourceProductsDescriptor = operatorDescriptor.getSourceProductsDescriptor();
            if(sourceProductsDescriptor != null) {
                for (int i = 0; i < sourceProducts.length; i++) {
                    Product sourceProduct = sourceProducts[i];
                    sourceProductMap.put(GPF.SOURCE_PRODUCT_FIELD_NAME + "." + (i + 1), sourceProduct);
                    // kept for backward compatibility
                    // since BEAM 4.9 the pattern above is preferred
                    sourceProductMap.put(GPF.SOURCE_PRODUCT_FIELD_NAME + (i + 1), sourceProduct);
                }
            }
        }

        Operator operator = GPF.getDefaultInstance().createOperator(operatorName, parameters, sourceProductMap, null);
        Product targetProduct = operator.getTargetProduct();
        targetProduct.setSceneGeoCoding(firstSourceProduct.getSceneGeoCoding());
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProduct;
    }

    public static Product runSegmentation(Product sourceProduct) {
        ProductNodeGroup<Band> bandGroup = sourceProduct.getBandGroup();
        String[] sourceBandNames = buildBandNamesArray(sourceProduct);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION);
        parameters.put("regionMergingCriterion", GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION);
        parameters.put("totalIterationsForSecondSegmentation", 10);
        parameters.put("threshold", 5.0f);
        parameters.put("spectralWeight", 0.5f);
        parameters.put("shapeWeight", 0.5f);
        parameters.put("sourceBandNames", sourceBandNames);

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("sourceProduct", sourceProduct);

        Operator operator = GPF.getDefaultInstance().createOperator("GenericRegionMergingOp", parameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();
        targetProduct.setSceneGeoCoding(sourceProduct.getSceneGeoCoding());
        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProduct;
    }

    public static void runProductTrimmingAndMahalanobis(Product segmentationSourceProduct, Product sourceProduct, int[] bandsUsed) {
        Int2ObjectMap<PixelSourceBands> statistics = TrimmingHelper.doTrimming(segmentationSourceProduct, sourceProduct, bandsUsed);

        TrimmingHelper.doMahalanobis(segmentationSourceProduct, sourceProduct, bandsUsed, statistics);
    }

    private static String[] buildBandNamesArray(Product sourceProduct) {
        ProductNodeGroup<Band> bandGroup = sourceProduct.getBandGroup();
        int bandCount = bandGroup.getNodeCount();
        String[] sourceBandNames = new String[bandCount];
        for (int i=0; i<bandCount; i++) {
            sourceBandNames[i] = bandGroup.get(i).getName();
        }
        return sourceBandNames;
    }
}
