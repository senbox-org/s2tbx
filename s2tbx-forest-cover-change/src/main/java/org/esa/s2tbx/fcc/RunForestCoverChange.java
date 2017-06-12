package org.esa.s2tbx.fcc;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.intern.BandsExtractor;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;
import org.esa.s2tbx.fcc.intern.TrimmingHelper;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.s2tbx.landcover.dataio.CCILandCoverModelDescriptor;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import java.io.File;
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

            int[] indexes = new int[] {3, 4, 10, 11};

            Product firstProduct = BandsExtractor.generateBandsExtractor(firstInputProduct, indexes);
            firstProduct = BandsExtractor.resampleAllBands(firstProduct);

            Product secondProduct = BandsExtractor.generateBandsExtractor(secondInputProduct, indexes);
            secondProduct = BandsExtractor.resampleAllBands(secondProduct);

            String mergingCostCriterion = GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION;
            String regionMergingCriterion = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION;
            int totalIterationsForSecondSegmentation = 10;
            float threshold = 5.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.5f;

            File parentFolder = new File("D:\\Forest_cover_changes");

//            Product bandsDifferenceProduct = BandsExtractor.generateBandsDifference(firstProduct, secondProduct);
//
//            Product segmentationAllBandsTargetProduct = BandsExtractor.runSegmentation(firstProduct, secondProduct, bandsDifferenceProduct,
//                                                                            mergingCostCriterion, regionMergingCriterion,
//                                                                            totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
//            BandsExtractor.writeProduct(segmentationAllBandsTargetProduct, "severalSourcesGenericRegionMergingOp");

            Product firstSegmentationProduct = BandsExtractor.runSegmentation(firstProduct, mergingCostCriterion, regionMergingCriterion,
                                                                              totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
            BandsExtractor.writeProduct(firstSegmentationProduct, parentFolder, "firstSegmentation");

            Product secondSegmentationProduct = BandsExtractor.runSegmentation(secondProduct, mergingCostCriterion, regionMergingCriterion,
                                                                               totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);
            BandsExtractor.writeProduct(secondSegmentationProduct, parentFolder, "secondSegmentation");

            float percentagePixels = 95.0f;
            Product firstProductColorFill = BandsExtractor.runColorFillerOp(firstSegmentationProduct, percentagePixels);
            BandsExtractor.writeProduct(firstProductColorFill, parentFolder, "firstProductColorFill");

            Product secondProductColorFill = BandsExtractor.runColorFillerOp(secondSegmentationProduct, percentagePixels);
            BandsExtractor.writeProduct(secondProductColorFill, parentFolder, "secondProductColorFill");

            int[] bandsUsed = new int[] {0, 1, 2};

            Int2ObjectMap<PixelSourceBands> firstTrimmingStatistics = TrimmingHelper.doTrimming(firstProductColorFill, firstProduct, bandsUsed);
            IntSet firstSegmentationTrimmingRegionKeys = firstTrimmingStatistics.keySet();

            Int2ObjectMap<PixelSourceBands> secondTrimmingStatistics = TrimmingHelper.doTrimming(secondProductColorFill, secondProduct, bandsUsed);
            IntSet secondSegmentationTrimmingRegionKeys = secondTrimmingStatistics.keySet();

            Product unionMasksProduct = BandsExtractor.runUnionMasksOp(firstSegmentationTrimmingRegionKeys, firstProductColorFill,
                                                                       secondSegmentationTrimmingRegionKeys, secondProductColorFill);
            BandsExtractor.writeProduct(unionMasksProduct, parentFolder, "unionMasksProduct");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
