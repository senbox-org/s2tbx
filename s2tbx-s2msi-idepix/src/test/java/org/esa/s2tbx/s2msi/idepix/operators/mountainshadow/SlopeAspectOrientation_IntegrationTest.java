package org.esa.s2tbx.s2msi.idepix.operators.mountainshadow;

import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.util.io.FileUtils;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tonio Fincke
 */
public class SlopeAspectOrientation_IntegrationTest {

    private File targetDirectory;

    @Before
    public void setUp() {
        targetDirectory = new File("sao_test_out");
        if (!targetDirectory.mkdirs()) {
            fail("Unable to create test target directory");
        }
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(new SlopeAspectOrientationOp.Spi());
    }

    @After
    public void tearDown() {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(new SlopeAspectOrientationOp.Spi());
        if (targetDirectory.isDirectory()) {
            if (!FileUtils.deleteTree(targetDirectory)) {
                fail("Unable to delete test directory");
            }
        }
    }

    @Test
    public void testSlopeAspectOrientationOp() throws FactoryException, TransformException, IOException {
        final int width = 4;
        final int height = 4;
        final Product product = new Product("SAO_Test", "sao_test", width, height);
        final CrsGeoCoding crsGeoCoding =
                new CrsGeoCoding(CRS.decode("EPSG:32650"), width, height, 699960.0, 4000020.0, 10.0, 10.0, 0.0, 0.0);
        product.setSceneGeoCoding(crsGeoCoding);
        final Band elevationBand = new Band("elevation", ProductData.TYPE_FLOAT32, width, height);
        float[] elevationData = new float[]{
                10.0f, 15.0f, 17.5f, 12.5f,
                12.0f, 14.0f, 16.0f, 13.0f,
                13.0f, 11.0f, 13.0f, 14.0f,
                14.0f, 12.0f, 14.0f, 11.0f};
        elevationBand.setDataElems(elevationData);
        product.addBand(elevationBand);

        final Map<String, Object> parameters = new HashMap<>();
        final Product targetProduct = GPF.createProduct("Idepix.Sentinel2.SlopeAspectOrientation", parameters, product);
        final String targetFilePath = targetDirectory.getPath() + File.separator + "sao_test.dim";
        ProductIO.writeProduct(targetProduct, targetFilePath, "BEAM-DIMAP");

        assertEquals(true, targetProduct.containsBand(SlopeAspectOrientationOp.SLOPE_BAND_NAME));
        assertEquals(true, targetProduct.containsBand(SlopeAspectOrientationOp.ASPECT_BAND_NAME));
        assertEquals(true, targetProduct.containsBand(SlopeAspectOrientationOp.ORIENTATION_BAND_NAME));

        final Band slopeBand = targetProduct.getBand(SlopeAspectOrientationOp.SLOPE_BAND_NAME);
        final Band aspectBand = targetProduct.getBand(SlopeAspectOrientationOp.ASPECT_BAND_NAME);
        final Band orientationBand = targetProduct.getBand(SlopeAspectOrientationOp.ORIENTATION_BAND_NAME);

        float[][] expectedSlope = new float[][]{
                {0.21798114f, 0.32035214f, 0.11440312f, 0.22131443f},
                {0.10711748f, 0.22345093f, 0.14396477f, 0.124354996f},
                {0.070593186f, 0.070593186f, 0.11134102f, 0.11134102f},
                {0.11134102f, 0.049958397f, 0.0f, 0.14048971f}};
        float[][] expectedAspect = {
                {-1.28474486f, -1.62733972f, 1.96140337f, 1.57079637f},
                {-0.95054686f, -2.12064958f, 3.01189017f, 1.57079637f},
                {0.78539819f, -2.3561945f, -2.67794514f, 2.67794514f},
                {1.10714877f, -0.f, -3.14159274f, 2.3561945f}};
        float[][] expectedOrientation = {{
                0.021341953f, 0.021341953f, 0.02063076f, 0.021341952f},
                {0.02134193f, 0.02134193f, 0.02134193f, 0.02134193f},
                {0.021341905f, 0.021341905f, 0.042664386f, 0.021341903f},
                {0.02134188f, 0.021341879f, 0.02063069f, 0.021341879f}};
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertEquals(slopeBand.getSampleFloat(x, y), expectedSlope[y][x], 1e-7);
                assertEquals(aspectBand.getSampleFloat(x, y), expectedAspect[y][x], 1e-8);
                assertEquals(orientationBand.getSampleFloat(x, y), expectedOrientation[y][x], 1e-7);
            }
        }
    }

}
