package org.esa.s2tbx.fcc;


import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.esa.s2tbx.fcc.intern.BandsExtractor;
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

            System.out.println("firstInputProduct="+firstInputProduct);
            System.out.println("secondInputProduct="+secondInputProduct);

            int[] indexes = new int[] {2, 3, 7, 11};

            Product firstProduct = BandsExtractor.generateBandsExtractor(firstInputProduct, indexes);
            firstProduct = BandsExtractor.resampleAllBands(firstProduct);

            Product secondProduct = BandsExtractor.generateBandsExtractor(secondInputProduct, indexes);
            secondProduct = BandsExtractor.resampleAllBands(secondProduct);

            Product bandsDifferenceProduct = BandsExtractor.generateBandsDifference(firstProduct, secondProduct);

            System.out.println("firstProduct="+firstProduct);
            System.out.println("secondProduct="+secondProduct);
            System.out.println("bandsDifferenceProduct="+bandsDifferenceProduct);

            Product bandsCompositingProduct = BandsExtractor.generateBandsCompositing(firstProduct, secondProduct, bandsDifferenceProduct);
            System.out.println("bandsCompositingProduct="+bandsCompositingProduct);
            BandsExtractor.writeProduct(bandsCompositingProduct);

            Product targetProduct = BandsExtractor.runSegmentation(bandsCompositingProduct);
            System.out.println("segmentation targetProduct="+targetProduct);
            BandsExtractor.writeProduct(targetProduct);

            Product ndviFirstProduct = BandsExtractor.computeNDVIBands(firstProduct, "B3", 1.0f, "B4", 1.0f);
            Product ndviSecondProduct = BandsExtractor.computeNDVIBands(secondProduct, "B3", 1.0f, "B4", 1.0f);

            Product ndwiFirstProduct = BandsExtractor.computeNDWIBands(firstProduct, "B3", 1.0f, "B4", 1.0f);
            Product ndwiSecondProduct = BandsExtractor.computeNDWIBands(secondProduct, "B3", 1.0f, "B4", 1.0f);

            System.out.println("ndviFirstProduct="+ndviFirstProduct);
            System.out.println("ndviSecondProduct="+ndviSecondProduct);
            System.out.println("ndwiFirstProduct="+ndwiFirstProduct);
            System.out.println("ndwiSecondProduct="+ndwiSecondProduct);
            Product targetProductColorFill = BandsExtractor.runColorFillerOp(targetProduct);

            BandsExtractor.runTrimmingOp(bandsCompositingProduct, targetProductColorFill);

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
