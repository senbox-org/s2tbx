package org.esa.s2tbx.coregistration;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
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
    private ProductReaderPlugIn a;

    @Before
    public final void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();

        GeoTiffProductReaderPlugIn readerPlugIn = new GeoTiffProductReaderPlugIn();
        ProductReader reader = readerPlugIn.createReaderInstance();

        File masterProductFile = this.coregistrationTestsFolderPath.resolve("subset_1_of_lidar_georef.tif").toFile();
        this.masterSourceProduct = reader.readProductNodes(masterProductFile, null);

        File slaveProductFile = this.coregistrationTestsFolderPath.resolve("subset_0_of_radar_bandep.tif").toFile();
        this.slaveSourceProduct = reader.readProductNodes(slaveProductFile, null);

    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path '" + testDirectoryPathProperty + "' is not valid.");
        }

        this.coregistrationTestsFolderPath = testFolderPath.resolve(File.separator + "_coregistration");
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
    }

    @Test
    public void testCoregistrationOp() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("slaveSourceBand", slaveSourceProduct.getBand("gray").getName());
        parameters.put("masterSourceBand",masterSourceProduct.getBand("gray").getName());
        parameters.put("levels", 6);
        parameters.put("rank", 4);
        parameters.put("iterations", 2);

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
        assertEquals(110, bandValue);

        bandValue = band.getSampleInt(164, 184);
        assertEquals(100, bandValue);

        bandValue = band.getSampleInt(264, 114);
        assertEquals(90, bandValue);

        bandValue = band.getSampleInt(14, 18);
        assertEquals(108, bandValue);

        bandValue = band.getSampleInt(123, 230);
        assertEquals(162, bandValue);

        bandValue = band.getSampleInt(200, 100);
        assertEquals(93, bandValue);
    }


}
