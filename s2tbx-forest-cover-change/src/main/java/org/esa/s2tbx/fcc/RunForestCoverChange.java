package org.esa.s2tbx.fcc;


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

            int[] indexes = new int[] {2, 3, 7, 11};

            Product firstProduct = BandsExtractor.generateBandsExtractor(firstInputProduct, indexes);
            firstProduct = BandsExtractor.resampleAllBands(firstProduct);

            Product secondProduct = BandsExtractor.generateBandsExtractor(secondInputProduct, indexes);
            secondProduct = BandsExtractor.resampleAllBands(secondProduct);

            Product bandsDifferenceProduct = BandsExtractor.generateBandsDifference(firstProduct, secondProduct);

            Product segmentationAllBandsTargetProduct = BandsExtractor.runSegmentation(firstProduct, secondProduct, bandsDifferenceProduct);
            BandsExtractor.writeProduct(segmentationAllBandsTargetProduct, "severalSourcesGenericRegionMergingOp");

            Product firstSegmentationProduct = BandsExtractor.runSegmentation(firstProduct);
            System.out.println("segmentation segmentationFirstBandsTargetProduct="+firstSegmentationProduct);
            BandsExtractor.writeProduct(firstSegmentationProduct, "firstSegmentation");

            Product secondSegmentationProduct = BandsExtractor.runSegmentation(secondProduct);
            System.out.println("segmentation segmentationSecondBandsTargetProduct="+secondSegmentationProduct);
            BandsExtractor.writeProduct(secondSegmentationProduct, "secondSegmentation");

            Product firstProductColorFill = BandsExtractor.runColorFillerOp(firstSegmentationProduct);
            BandsExtractor.writeProduct(firstProductColorFill, "firstProductColorFill");

            Product secondProductColorFill = BandsExtractor.runColorFillerOp(secondSegmentationProduct);
            BandsExtractor.writeProduct(secondProductColorFill, "firstProductColorFill");

//            Product targetProductColorFill = BandsExtractor.runColorFillerOp(segmentationAllBandsTargetProduct);

            int[] bandsUsed = new int[] {0, 1, 2};

            Int2ObjectMap<PixelSourceBands> firstTrimmingStatistics = TrimmingHelper.doTrimming(firstProductColorFill, firstProduct, bandsUsed);
//            Int2ObjectMap<PixelSourceBands> firstTrimmingStatistics = TrimmingHelper.doTrimming(targetProductColorFill, firstProduct, bandsUsed);
            IntSet firstSegmentationTrimmingRegionKeys = firstTrimmingStatistics.keySet();
            System.out.print("firstSegmentationTrimmingRegionKeys.size="+firstSegmentationTrimmingRegionKeys.size());

            Int2ObjectMap<PixelSourceBands> secondTrimmingStatistics = TrimmingHelper.doTrimming(secondProductColorFill, secondProduct, bandsUsed);
//            Int2ObjectMap<PixelSourceBands> secondTrimmingStatistics = TrimmingHelper.doTrimming(targetProductColorFill, secondProduct, bandsUsed);
            IntSet secondSegmentationTrimmingRegionKeys = secondTrimmingStatistics.keySet();
            System.out.print("secondSegmentationTrimmingRegionKeys.size="+secondSegmentationTrimmingRegionKeys.size());




            Product unionMasksProduct = BandsExtractor.runUnionMasksOp(firstSegmentationTrimmingRegionKeys, firstProductColorFill,
                                                                       secondSegmentationTrimmingRegionKeys, secondProductColorFill);

            BandsExtractor.writeProduct(unionMasksProduct, "unionMasksProduct");
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
