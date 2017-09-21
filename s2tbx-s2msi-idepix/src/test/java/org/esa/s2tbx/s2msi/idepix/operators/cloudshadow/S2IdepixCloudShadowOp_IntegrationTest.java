package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.util.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.fail;

/**
 * @author Tonio Fincke
 */
public class S2IdepixCloudShadowOp_IntegrationTest {

    private File targetDirectory;
    private S2IdepixCloudShadowOp.Spi spi;

    @Before
    public void setUp() {
        targetDirectory = new File("test_out");
        if (!targetDirectory.mkdirs()) {
            fail("Unable to create test target directory");
        }
        spi = new S2IdepixCloudShadowOp.Spi();
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(spi);
    }

    @After
    public void tearDown() {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(spi);

        if (targetDirectory.isDirectory()) {
            if (!FileUtils.deleteTree(targetDirectory)) {
                fail("Unable to delete test directory");
            }
        }
    }

    @Test
    public void testS2IdepixCloudShadowOp() throws IOException {
        final URL s2ProductURL = S2IdepixCloudShadowOp_IntegrationTest.class.getResource("s2_classif.dim");
        final Product s2Product = ProductIO.readProduct(s2ProductURL.getFile());
        final HashMap<String, Product> productsMap = new HashMap<>();
        productsMap.put("s2ClassifProduct", s2Product);
        final String[] modes = {"LandWater", "MultiBand", "SingleBand"};
        for (String mode : modes) {
            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("mode", mode);
            final Product cloudShadowProduct = GPF.createProduct("CCICloudShadow", parameters, productsMap);
            final String targetFilePath = targetDirectory.getPath() + File.separator + "largeTestScene" + mode + ".dim";
            ProductIO.writeProduct(cloudShadowProduct, targetFilePath, "BEAM-DIMAP");
        }
        int x = 1;
    }

}
