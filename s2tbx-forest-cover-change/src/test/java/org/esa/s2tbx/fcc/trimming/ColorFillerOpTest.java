package org.esa.s2tbx.fcc.trimming;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.SubProgressMonitor;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.internal.OperatorExecutor;
import org.esa.snap.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class ColorFillerOpTest {
    private Path colorFillerTestsFolderPath;
    private Product segmentationProduct;
    private IntSet validRegions;

    @Before
    public void setUp() throws Exception {
        assumeTrue(TestUtil.testdataAvailable());
        checkTestDirectoryExists();
        Class<?> sentinelReaderPlugInClass = Class.forName("org.esa.snap.core.dataio.dimap.DimapProductReaderPlugIn");
        ProductReaderPlugIn productReaderPlugIn = (ProductReaderPlugIn)sentinelReaderPlugInClass.newInstance();

        File currentProductFile = this.colorFillerTestsFolderPath.resolve("S2A_20160713T125925_A005524_T35UMP_grm.dim").toFile();
        this.segmentationProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        this.validRegions =  new IntOpenHashSet();
        validRegions.add(1);
        validRegions.add(10);
        validRegions.add(12);
        validRegions.add(20);
        validRegions.add(34);
        validRegions.add(45);
        validRegions.add(53);
        validRegions.add(63);
        validRegions.add(117);
        validRegions.add(293);
        validRegions.add(402);
        validRegions.add(800);
        validRegions.add(1005);
        validRegions.add(1200);
    }

    @Test
    public void testColorFillerOp() throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("validRegions", this.validRegions);
        Map<String, Product> sourceProducts = new HashMap<>();
        sourceProducts.put("segmentationSourceProduct", this.segmentationProduct);
        ColorFillerOp operator = (ColorFillerOp) GPF.getDefaultInstance().createOperator("ColorFillerOp", parameters, sourceProducts, null);
        Product targetProduct = operator.getTargetProduct();

        assertEquals(targetProduct.getName(), "S2A_20160713T125925_A005524_T35UMP_grm_fill");
        assertEquals(targetProduct.getSceneRasterSize(), new Dimension(549, 549));
        assertEquals(targetProduct.getNumBands(), 1);
        assertEquals(targetProduct.getProductType(),segmentationProduct.getProductType() );

        OperatorExecutor executor = OperatorExecutor.create(operator);
        executor.execute(SubProgressMonitor.create(ProgressMonitor.NULL, 95));
        ProductData productData = operator.getProductData();
        Band targetBand = targetProduct.getBandAt(0);
        targetBand.setData(productData);
        targetBand.setSourceImage(null);
        targetBand.getSourceImage();
        checkTargetBand(targetBand);
        chechSampleintTargetBand(targetBand);
    }

    private void chechSampleintTargetBand(Band band) {
        int bandValue = band.getSampleInt(5, 3);
        assertEquals(1, bandValue);

        bandValue = band.getSampleInt(133, 6);
        assertEquals(10, bandValue);

        bandValue = band.getSampleInt(167, 6);
        assertEquals(12, bandValue);

        bandValue = band.getSampleInt(305, 3);
        assertEquals(20, bandValue);

        bandValue = band.getSampleInt(239, 12);
        assertEquals(34, bandValue);

        bandValue = band.getSampleInt(415, 19);
        assertEquals(45, bandValue);

        bandValue = band.getSampleInt(406, 14);
        assertEquals(53, bandValue);

        bandValue = band.getSampleInt(441, 31);
        assertEquals(63, bandValue);

        bandValue = band.getSampleInt(440, 41);
        assertEquals(117, bandValue);

        bandValue = band.getSampleInt(88, 123);
        assertEquals(293, bandValue);

        bandValue = band.getSampleInt(414, 161);
        assertEquals(402, bandValue);

        bandValue = band.getSampleInt(231, 305);
        assertEquals(800, bandValue);

        bandValue = band.getSampleInt(399, 384);
        assertEquals(1005, bandValue);

        bandValue = band.getSampleInt(93, 454);
        assertEquals(1200, bandValue);
    }

    private void checkTargetBand(Band targetBand) {

        assertNotNull(targetBand);
        assertEquals(ProductData.TYPE_INT32, targetBand.getDataType());
        long size = segmentationProduct.getSceneRasterWidth() * segmentationProduct.getSceneRasterHeight();
        assertEquals(size, targetBand.getNumDataElems());
    }

    private void checkTestDirectoryExists() {
        String testDirectoryPathProperty = System.getProperty(TestUtil.PROPERTYNAME_DATA_DIR);
        assertNotNull("The system property '" + TestUtil.PROPERTYNAME_DATA_DIR + "' representing the test directory is not set.", testDirectoryPathProperty);
        Path testFolderPath = Paths.get(testDirectoryPathProperty);
        if (!Files.exists(testFolderPath)) {
            fail("The test directory path "+testDirectoryPathProperty+" is not valid.");
        }

        this.colorFillerTestsFolderPath = testFolderPath.resolve("_forest-cover-change");
        if (!Files.exists(colorFillerTestsFolderPath)) {
            fail("The Forest Cover Change test directory path "+ colorFillerTestsFolderPath.toString()+" is not valid.");
        }
    }
}
