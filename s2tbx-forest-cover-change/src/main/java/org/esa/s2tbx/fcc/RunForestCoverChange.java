package org.esa.s2tbx.fcc;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.s2tbx.landcover.dataio.CCILandCoverModelDescriptor;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import java.io.File;

import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.landcover.dataio.LandCoverModelRegistry;

/**
 * @author Jean Coravu
 */
public class RunForestCoverChange {

    public static void main(String arg[]) {
        try {
            initLogging();

            Class<?> sentinelReaderPlugInClass = Class.forName("org.esa.snap.core.dataio.dimap.DimapProductReaderPlugIn");
            ProductReaderPlugIn productReaderPlugIn = (ProductReaderPlugIn)sentinelReaderPlugInClass.newInstance();

            File file1 = new File("\\\\cv-dev-srv01\\Satellite_Imagery\\Forest_Mapping\\S2A_OPER_MTD_L1C_TL_SGS__20160404T132741_A004094_T34TFQ_resampled.dim");
            Product firstInputProduct = productReaderPlugIn.createReaderInstance().readProductNodes(file1, null);

            File file2 = new File("\\\\cv-dev-srv01\\Satellite_Imagery\\Forest_Mapping\\S2A_MSIL1C_20170409T092031_N0204_R093_T34TFQ_20170409T092026_resampled.dim");
            Product secondInputProduct = productReaderPlugIn.createReaderInstance().readProductNodes(file2, null);

            LandCoverModelRegistry landCoverModelRegistry = LandCoverModelRegistry.getInstance();
            landCoverModelRegistry.addDescriptor(new CCILandCoverModelDescriptor());

            String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 10;
            float threshold = 5.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.5f;
            float treeCoverPercentagePixels = 95.0f;
            File parentFolder = new File("D:\\Forest_cover_changes");

            Product targetProduct = runForestCoverChange(firstInputProduct, secondInputProduct, mergingCostCriterion, regionMergingCriterion,
                                                totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight, treeCoverPercentagePixels);
            writeProduct(targetProduct, parentFolder, "unionMasksProduct");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeProduct(Product outputProduct, File parentFolder, String fileName){
        File file = new File(parentFolder, fileName);
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

    public static Product runForestCoverChange(Product firstSourceProduct, Product secondSourceProduct,
                                               String mergingCostCriterion, String regionMergingCriterion, int totalIterationsForSecondSegmentation,
                                               float threshold, float spectralWeight, float shapeWeight, float percentage) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mergingCostCriterion", mergingCostCriterion);
        parameters.put("regionMergingCriterion", regionMergingCriterion);
        parameters.put("totalIterationsForSecondSegmentation", totalIterationsForSecondSegmentation);
        parameters.put("threshold", threshold);
        parameters.put("spectralWeight", spectralWeight);
        parameters.put("shapeWeight", shapeWeight);
        parameters.put("forestCoverPercentage", percentage);

        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("currentSourceProduct", firstSourceProduct);
        sourceProducts.put("previousSourceProduct", secondSourceProduct);

        Operator operator = GPF.getDefaultInstance().createOperator("ForestCoverChange", parameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();
        operator.doExecute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));

        return targetProduct;
    }


    private static void initLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.FINE);
        Handler[] handlers = rootLogger.getHandlers();
        for (int i=0; i<handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        ConsoleHandler rootHandler = new ConsoleHandler();
        rootHandler.setFormatter(new SimpleFormatter());
        rootHandler.setLevel(rootLogger.getLevel());
        rootLogger.addHandler(rootHandler);
    }
}
