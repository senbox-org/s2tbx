package org.esa.s2tbx.fcc;

import org.esa.s2tbx.fcc.intern.BandsExtractor;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;

import java.io.File;

/**
 * @author Jean Coravu
 */
public class RunForestCoverChange {

    public static void main(String arg[]) {
        try {
            Class<?> sentinelReaderPlugInClass = Class.forName("org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn");
            ProductReaderPlugIn productReaderPlugIn = (ProductReaderPlugIn)sentinelReaderPlugInClass.newInstance();

            File file1 = new File("\\\\cv-dev-srv01\\Satellite_Imagery\\Sen2Three_Testdata_L2A\\S2A_USER_PRD_MSIL2A_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.SAFE\\S2A_USER_MTD_SAFL2A_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.xml");
            Product firstInputProduct = productReaderPlugIn.createReaderInstance().readProductNodes(file1, null);

            File file2 = new File("\\\\cv-dev-srv01\\Satellite_Imagery\\Sen2Three_Testdata_L2A\\S2A_USER_PRD_MSIL2A_PDMC_20150812T211918_R065_V20150812T102902_20150806T102902.SAFE\\S2A_USER_MTD_SAFL2A_PDMC_20150812T211918_R065_V20150806T102902_20150806T102902.xml");
            Product secondInputProduct = productReaderPlugIn.createReaderInstance().readProductNodes(file2, null);

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

            Product ndviFirstProduct = BandsExtractor.computeNDVIBands(firstProduct, "B3", 1.0f, "B4", 1.0f);
            Product ndviSecondProduct = BandsExtractor.computeNDVIBands(secondProduct, "B3", 1.0f, "B4", 1.0f);

            Product ndwiFirstProduct = BandsExtractor.computeNDWIBands(firstProduct, "B3", 1.0f, "B4", 1.0f);
            Product ndwiSecondProduct = BandsExtractor.computeNDWIBands(secondProduct, "B3", 1.0f, "B4", 1.0f);

            System.out.println("ndviFirstProduct="+ndviFirstProduct);
            System.out.println("ndviSecondProduct="+ndviSecondProduct);
            System.out.println("ndwiFirstProduct="+ndwiFirstProduct);
            System.out.println("ndwiSecondProduct="+ndwiSecondProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
