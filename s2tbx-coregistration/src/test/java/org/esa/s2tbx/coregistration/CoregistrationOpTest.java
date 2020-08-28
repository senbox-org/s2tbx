package org.esa.s2tbx.coregistration;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.VirtualBand;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;


/**
 * @author Denisa Stefanescu
 */
public class CoregistrationOpTest {

    private Path coregistrationTestsFolderPath;
    private Product masterSourceProduct;
    private Product slaveSourceProduct;

    @Before
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();

        GeoTiffProductReaderPlugIn readerPlugIn = new GeoTiffProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        File masterProductFile = this.coregistrationTestsFolderPath.resolve("subset_1_of_lidar_georef.tif").toFile();
        this.masterSourceProduct = reader.readProductNodes(masterProductFile, null);

        reader = readerPlugIn.createReaderInstance(); // create new reader object
        File slaveProductFile = this.coregistrationTestsFolderPath.resolve("subset_0_of_radar_bandep.tif").toFile();
        this.slaveSourceProduct = reader.readProductNodes(slaveProductFile, null);
    }

    @Test
    public void testSlaveProductVirtualBand() {
        Product product = this.slaveSourceProduct;
        assertNotNull(product.getProductReader());
        assertEquals(402, product.getSceneRasterWidth());
        assertEquals(271, product.getSceneRasterHeight());
        assertEquals("IMAGE", product.getProductType());

        assertEquals(4, product.getNumBands());

        Band band = product.getBandAt(0);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(108942, band.getNumDataElems());
        assertEquals("red", band.getName());
        assertEquals(402, band.getRasterWidth());
        assertEquals(271, band.getRasterHeight());

        assertEquals(108, band.getSampleInt(64, 84));
        assertEquals(121, band.getSampleInt(164, 184));
        assertEquals(86, band.getSampleInt(264, 114));
        assertEquals(124, band.getSampleInt(14, 18));
        assertEquals(155, band.getSampleInt(123, 230));
        assertEquals(91, band.getSampleInt(200, 100));
        assertEquals(145, band.getSampleInt(401, 270));

        band = product.getBandAt(2);
        assertNotNull(band);
        assertEquals(20, band.getDataType());
        assertEquals(108942, band.getNumDataElems());
        assertEquals("blue", band.getName());
        assertEquals(402, band.getRasterWidth());
        assertEquals(271, band.getRasterHeight());

        assertEquals(119, band.getSampleInt(64, 84));
        assertEquals(116, band.getSampleInt(164, 184));
        assertEquals(98, band.getSampleInt(264, 114));
        assertEquals(150, band.getSampleInt(14, 18));
        assertEquals(189, band.getSampleInt(123, 230));
        assertEquals(95, band.getSampleInt(200, 100));
        assertEquals(170, band.getSampleInt(401, 270));

        band = product.getBandAt(3);
        assertNotNull(band);
        assertTrue(band instanceof VirtualBand);
        assertEquals(30, band.getDataType());
        assertEquals(108942, band.getNumDataElems());
        assertEquals("gray", band.getName());
        assertEquals(402, band.getRasterWidth());
        assertEquals(271, band.getRasterHeight());

        assertEquals(110.98f, band.getSampleFloat(64, 84), 0.0f);
        assertEquals(101.57f, band.getSampleFloat(164, 184), 0.0f);
        assertEquals(90.86f, band.getSampleFloat(264, 114), 0.0f);
        assertEquals(111.52f, band.getSampleFloat(14, 18), 0.0f);
        assertEquals(161.69f, band.getSampleFloat(123, 230), 0.0f);
        assertEquals(93.21f, band.getSampleFloat(200, 100), 0.0f);
        assertEquals(161.32f, band.getSampleFloat(401, 270), 0.0f);
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path '" + testDirectoryPathProperty + "' is not valid.");
        }

        this.coregistrationTestsFolderPath = testFolderPath.resolve("_coregistration");
        if (!Files.exists(coregistrationTestsFolderPath)) {
            fail("The Co-registration test directory path '" + coregistrationTestsFolderPath.toString() + "' is not valid.");
        }
    }


    @Test
    public void testCoregistrationDefaultOpValues() {
        CoregistrationOp op = new CoregistrationOp();
        op.setParameterDefaultValues();

        assertEquals(4, op.getParameter("rank"));
        assertEquals(6, op.getParameter("levels"));
        assertEquals(2, op.getParameter("iterations"));
        assertEquals("32, 28, 24, 20, 16, 12, 8",op.getParameter("radius"));
        assertEquals("CoregistrationOp", op.getSpi().getOperatorAlias());

    }

    @Test
    public void testCoregistrationGivenOpValues() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("masterSourceBand", masterSourceProduct.getBandAt(0).getName());
        parameters.put("slaveSourceBand", slaveSourceProduct.getBandAt(0).getName());
        parameters.put("rank", 2);
        parameters.put("levels", 3);
        parameters.put("iterations", 4);
        parameters.put("radius", "20, 16, 12, 8");

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("Master", masterSourceProduct);
        sourceProducts.put("Slave", slaveSourceProduct);

        // create the operator
        Operator operator = GPF.getDefaultInstance().createOperator("CoregistrationOp", parameters, sourceProducts, null);

        assertNotEquals(4, operator.getParameter("rank"));
        assertEquals(2, operator.getParameter("rank"));
        assertNotEquals(6, operator.getParameter("levels"));
        assertEquals(3, operator.getParameter("levels"));
        assertNotEquals(2, operator.getParameter("iterations"));
        assertEquals(4, operator.getParameter("iterations"));
        assertNotEquals("32, 28, 24, 20, 16, 12, 8", operator.getParameter("radius"));
        assertEquals("20, 16, 12, 8", operator.getParameter("radius"));
    }

    @Test
    public void testCoregistrationOp() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("slaveSourceBand", slaveSourceProduct.getBand("gray").getName());
        parameters.put("masterSourceBand",masterSourceProduct.getBand("gray").getName());
        parameters.put("levels", 6);
        parameters.put("rank", 4);
        parameters.put("iterations", 2);
        parameters.put("radius", "32, 28, 24, 20, 16, 12, 8");

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("Master", masterSourceProduct);
        sourceProducts.put("Slave", slaveSourceProduct);

        // create the operator
        Operator operator = GPF.getDefaultInstance().createOperator("CoregistrationOp", parameters, sourceProducts, null);

        // execute the operator
        operator.execute(ProgressMonitor.NULL);

        // get the operator target product
        Product targetProduct = operator.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(402, targetProduct.getSceneRasterWidth());
        assertEquals(271, targetProduct.getSceneRasterHeight());

        assertEquals(slaveSourceProduct.getNumBands(), targetProduct.getNumBands());

        Band bandGray = targetProduct.getBandAt(0);
        Band bandRed = targetProduct.getBandAt(1);
        Band bandGreen = targetProduct.getBandAt(2);
        Band bandBlue = targetProduct.getBandAt(3);

        assertNotNull(bandGray);
        assertNotNull(bandRed);
        assertNotNull(bandGreen);
        assertNotNull(bandBlue);

        assertEquals(ProductData.TYPE_FLOAT32, bandGray.getDataType());
        assertEquals(ProductData.TYPE_FLOAT32, bandBlue.getDataType());
        assertEquals(ProductData.TYPE_FLOAT32, bandGreen.getDataType());
        assertEquals(ProductData.TYPE_FLOAT32, bandRed.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, bandGray.getNumDataElems());
        checkGrayBand(bandGray);

    }

    private static void checkGrayBand(Band band) {
        int bandValue = band.getSampleInt(64, 84);
        assertEquals(115, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(93, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(105, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(92, bandValue);

        bandValue = band.getSampleInt(123, 230);
        assertEquals(111, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(88, bandValue);
    }


}
