package org.esa.s2tbx.fcc.intern;

        import java.io.File;
        import java.io.IOException;
        import java.util.*;

        import com.bc.ceres.core.ProgressMonitor;
        import com.bc.ceres.core.SubProgressMonitor;
        import org.esa.s2tbx.grm.GenericRegionMergingOp;
        import org.esa.snap.core.datamodel.Band;
        import org.esa.snap.core.datamodel.Product;
        import org.esa.snap.core.datamodel.ProductNodeGroup;
        import org.esa.snap.core.gpf.GPF;
        import org.esa.snap.core.gpf.Operator;
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

    public static Product generateBandsCompositing(Product firstSourceProduct, Product secondSourceProduct, Product thirdSourceProduct) {
        Product[] products = new Product[]{firstSourceProduct, secondSourceProduct, thirdSourceProduct};
        Map<String, Object> parameters = new HashMap<>();
        return GPF.createProduct("BandsCompositingOp", parameters, products, null);
    }

    public static void writeProduct(Product outputProduct){
        File file = new File("D:\\Forest_cover_changes\\"+ outputProduct.getName());
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
    public static Product computeNDVIBands(Product sourceProduct, String redSourceBand, float redFactor, String nirSourceBand, float nirFactor) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("redFactor", redFactor);
        parameters.put("redSourceBand", redSourceBand);
        parameters.put("nirFactor", nirFactor);
        parameters.put("nirSourceBand", nirSourceBand);
        return GPF.createProduct("NdviOp", parameters, sourceProduct);
    }

    public static Product computeNDWIBands(Product sourceProduct, String mirSourceBand, float mirFactor, String nirSourceBand, float nirFactor) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mirFactor", mirFactor);
        parameters.put("mirSourceBand", mirSourceBand);
        parameters.put("nirFactor", nirFactor);
        parameters.put("nirSourceBand", nirSourceBand);
        return GPF.createProduct("NdwiOp", parameters, sourceProduct);
    }
    public static Product computeObjectSelection(Product sourceProduct) {
        Map<String, Object> parameters = new HashMap<>();
        return GPF.createProduct("ObjectsSelectionOp", parameters, sourceProduct);

    }

    public static void runColorFillerOp(Product firstProduct) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("percentagePixels", 95f);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("sourceProduct", firstProduct);
        ColorFillerOp colFillOp = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
        Product targetProductSelection = colFillOp.getTargetProduct();
        OperatorExecutor executor = OperatorExecutor.create(colFillOp);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        writeProduct(targetProductSelection);
    }

    public static Product runSegmentation(Product sourceProduct) {
        ProductNodeGroup<Band> bandGroup = sourceProduct.getBandGroup();
        int bandCount = bandGroup.getNodeCount();
        String[] sourceBandNames = new String[bandCount];
        for (int i=0; i<bandCount; i++) {
            sourceBandNames[i] = bandGroup.get(i).getName();
        }
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
}
