package org.esa.s2tbx.fcc;

import org.esa.s2tbx.dataio.s2.ortho.plugins.Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn;
import org.esa.snap.core.datamodel.Product;

import java.io.File;
import java.io.IOException;

/**
 * Created by jcoravu on 22/5/2017.
 */
public class RunForestCoverChange {

    public static void main(String arg[]) {
        System.out.println("RunForestCoverChange");
        File file = new File("D:\\S2A_USER_PRD_MSIL2A_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.SAFE\\S2A_USER_MTD_SAFL2A_PDMC_20150812T193220_R108_V20150730T103914_20150730T103914.xml");
        try {
            Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn readerPlugIn = new Sentinel2L1CProduct_Multi_UTM32N_ReaderPlugIn();
            Product product = readerPlugIn.createReaderInstance().readProductNodes(file, null);
            System.out.println("product="+product);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
