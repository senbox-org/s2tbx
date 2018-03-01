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
public class S2IdepixMountainShadowOp_IntegrationTest {

    private File targetDirectory;

    @Before
    public void setUp() {
        targetDirectory = new File("ms_test_out");
        if (!targetDirectory.mkdirs()) {
            fail("Unable to create test target directory");
        }
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(new S2IdepixMountainShadowOp.Spi());
    }

    @After
    public void tearDown() {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(new S2IdepixMountainShadowOp.Spi());
        if (targetDirectory.isDirectory()) {
            if (!FileUtils.deleteTree(targetDirectory)) {
                fail("Unable to delete test directory");
            }
        }
    }

    @Test
    public void testMountainShadowOp_MissingSAO() throws FactoryException, TransformException, IOException {
        final int width = 4;
        final int height = 4;
        final Product product = new Product("MS_Test", "ms_test", width, height);
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
        final Band szaBand = new Band("sun_zenith", ProductData.TYPE_FLOAT32, width, height);
        float[] szaData = new float[]{
                85.001f, 85.002f, 85.003f, 85.004f,
                85.011f, 85.012f, 85.013f, 85.014f,
                85.021f, 85.022f, 85.023f, 85.024f,
                85.031f, 85.032f, 85.033f, 85.034f};
        szaBand.setDataElems(szaData);
        product.addBand(szaBand);
        final Band saaBand = new Band("sun_azimuth", ProductData.TYPE_FLOAT32, width, height);
        float[] saaData = new float[]{
                150.001f, 150.002f, 150.003f, 150.004f,
                150.011f, 150.012f, 150.013f, 150.014f,
                150.021f, 150.022f, 150.023f, 150.024f,
                150.031f, 150.032f, 150.033f, 150.034f};
        saaBand.setDataElems(saaData);
        product.addBand(saaBand);

        final Map<String, Object> parameters = new HashMap<>();
        final Product targetProduct = GPF.createProduct("Idepix.Sentinel2.MountainShadow", parameters, product);
        final String targetFilePath = targetDirectory.getPath() + File.separator + "ms_test.dim";
        ProductIO.writeProduct(targetProduct, targetFilePath, "BEAM-DIMAP");

        assertEquals(true, targetProduct.containsBand(S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME));
        final Band mountainShadowFlagBand = targetProduct.getBand(
                S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME);
        int[][] expectedShadow = {
                {1, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertEquals(expectedShadow[y][x], mountainShadowFlagBand.getSampleInt(x, y));
            }
        }
    }

    @Test
    public void testMountainShadowOp_ExistingSAO() throws FactoryException, TransformException, IOException {
        final int width = 4;
        final int height = 4;
        final Product product = new Product("MS_Test", "ms_test", width, height);
        final CrsGeoCoding crsGeoCoding =
                new CrsGeoCoding(CRS.decode("EPSG:32650"), width, height, 699960.0, 4000020.0, 10.0, 10.0, 0.0, 0.0);
        product.setSceneGeoCoding(crsGeoCoding);
        final Band slopeBand = new Band(SlopeAspectOrientationOp.SLOPE_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        float[] slopeData = new float[]{
                0.21798114f, 0.32035214f, 0.11440312f, 0.22131443f,
                0.10711748f, 0.22345093f, 0.14396477f, 0.124354996f,
                0.070593186f, 0.070593186f, 0.11134102f, 0.11134102f,
                0.11134102f, 0.049958397f, 0.0f, 0.14048971f};
        slopeBand.setDataElems(slopeData);
        product.addBand(slopeBand);
        final Band aspectBand = new Band(SlopeAspectOrientationOp.ASPECT_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        float[] aspectData = new float[]{
                -1.28474486f, -1.62733972f, 1.96140337f, 1.57079637f,
                -0.95054686f, -2.12064958f, 3.01189017f, 1.57079637f,
                0.78539819f, -2.3561945f, -2.67794514f, 2.67794514f,
                1.10714877f, -0.f, -3.14159274f, 2.3561945f};
        aspectBand.setDataElems(aspectData);
        product.addBand(aspectBand);
        final Band orientationBand = new Band(SlopeAspectOrientationOp.ORIENTATION_BAND_NAME, ProductData.TYPE_FLOAT32, width, height);
        float[] orientationData = new float[]{
                0.021341953f, 0.021341953f, 0.02063076f, 0.021341952f,
                0.02134193f, 0.02134193f, 0.02134193f, 0.02134193f,
                0.021341905f, 0.021341905f, 0.042664386f, 0.021341903f,
                0.02134188f, 0.021341879f, 0.02063069f, 0.021341879f};
        orientationBand.setDataElems(orientationData);
        product.addBand(orientationBand);
        final Band szaBand = new Band("sun_zenith", ProductData.TYPE_FLOAT32, width, height);
        float[] szaData = new float[]{
                85.001f, 85.002f, 85.003f, 85.004f,
                85.011f, 85.012f, 85.013f, 85.014f,
                85.021f, 85.022f, 85.023f, 85.024f,
                85.031f, 85.032f, 85.033f, 85.034f};
        szaBand.setDataElems(szaData);
        product.addBand(szaBand);
        final Band saaBand = new Band("sun_azimuth", ProductData.TYPE_FLOAT32, width, height);
        float[] saaData = new float[]{
                150.001f, 150.002f, 150.003f, 150.004f,
                150.011f, 150.012f, 150.013f, 150.014f,
                150.021f, 150.022f, 150.023f, 150.024f,
                150.031f, 150.032f, 150.033f, 150.034f};
        saaBand.setDataElems(saaData);
        product.addBand(saaBand);

        final Map<String, Object> parameters = new HashMap<>();
        final Product targetProduct = GPF.createProduct("Idepix.Sentinel2.MountainShadow", parameters, product);
        final String targetFilePath = targetDirectory.getPath() + File.separator + "ms_test.dim";
        ProductIO.writeProduct(targetProduct, targetFilePath, "BEAM-DIMAP");

        assertEquals(true, targetProduct.containsBand(S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME));
        final Band mountainShadowFlagBand = targetProduct.getBand(
                S2IdepixMountainShadowOp.MOUNTAIN_SHADOW_FLAG_BAND_NAME);
        int[][] expectedShadow = {
                {1, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                assertEquals(expectedShadow[y][x], mountainShadowFlagBand.getSampleInt(x, y));
            }
        }
    }

}
