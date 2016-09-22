package org.esa.s2tbx.reflectance2radiance;

import junit.framework.TestCase;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.image.Raster;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu.
 */
public class ReflectanceToRadianceOpTest extends TestCase {
    private static final String SOLLAR_IRRADIANCE_ATTRIBUTE_NAME = "solarIrradiance";
    private static final String U_ATTRIBUTE_NAME = "u";
    private static final String INCIDENCE_ANGLE_ATTRIBUTE_NAME = "incidenceAngle";
    private static final String SOURCE_BAND_NAMES_ATTRIBUTE_NAME = "sourceBandNames";

    public ReflectanceToRadianceOpTest() {
    }

    public void testSentinelProduct() {
        int sceneRasterWidth = 4;
        int sceneRasterHeight = 4;
        String sourceBandName = "B1";
        Product sourceProduct = new Product("IndexTest", "IndexTestType", sceneRasterWidth, sceneRasterHeight);

        int bandIndex = sourceProduct.getBandGroup().getNodeCount();
        Band band = buildBand("sun_zenith", 2, 3, 475, 1, 9);
        band.setSpectralBandIndex(bandIndex);
        sourceProduct.addBand(band);

        band = buildBand(sourceBandName, sceneRasterWidth, sceneRasterHeight, 650, 4, 20);
        band.setSpectralBandIndex(bandIndex);
        sourceProduct.addBand(band);

        Map<String, Object> annotatedFields = new HashMap<String, Object>();
        annotatedFields.put(SOLLAR_IRRADIANCE_ATTRIBUTE_NAME, 1.0f);
        annotatedFields.put(U_ATTRIBUTE_NAME, 1.0f);
        annotatedFields.put(INCIDENCE_ANGLE_ATTRIBUTE_NAME, 1.0f);
        annotatedFields.put(SOURCE_BAND_NAMES_ATTRIBUTE_NAME, new String[]{sourceBandName});

        float[] expectedValues = new float[] {
                1.2727431f, 1.6117876f, 1.9505881f, 2.2890952f,
                2.6290536f, 2.9673457f, 3.3052614f, 3.6427512f,
                3.9832635f, 4.3204722f, 4.6571727f, 4.9933157f,
                5.33458f, 5.670376f, 6.0055313f, 6.339997f
        };

        ReflectanceToRadianceOp operator = buildOperator(sourceProduct, annotatedFields);

        Product targetProduct = operator.getTargetProduct();
        Band targetBand = targetProduct.getBandAt(0);

        checkExpectedValues(expectedValues, targetBand);
    }

    public void testProductWithIncidenceAngle() {
        int sceneRasterWidth = 4;
        int sceneRasterHeight = 4;
        String sourceBandName = "Band1";
        Product sourceProduct = new Product("IndexTest", "IndexTestType", sceneRasterWidth, sceneRasterHeight);

        int bandIndex = sourceProduct.getBandGroup().getNodeCount();
        Band band = buildBand(sourceBandName, sceneRasterWidth, sceneRasterHeight, 325, 5, 25);
        band.setSpectralBandIndex(bandIndex);
        sourceProduct.addBand(band);

        Map<String, Object> annotatedFields = new HashMap<String, Object>();
        annotatedFields.put(SOLLAR_IRRADIANCE_ATTRIBUTE_NAME, 1.2f);
        annotatedFields.put(U_ATTRIBUTE_NAME, 1.45f);
        annotatedFields.put(INCIDENCE_ANGLE_ATTRIBUTE_NAME, 3.21f);
        annotatedFields.put(SOURCE_BAND_NAMES_ATTRIBUTE_NAME, new String[]{sourceBandName});

        float[] expectedValues = new float[] {
                2.7649512f, 3.5022717f, 4.239592f, 4.976912f,
                5.714233f, 6.451553f, 7.1888733f, 7.9261937f,
                8.663514f, 9.400834f, 10.138155f, 10.875476f,
                11.612795f, 12.350116f, 13.087437f, 13.824756f
        };

        ReflectanceToRadianceOp operator = buildOperator(sourceProduct, annotatedFields);

        Product targetProduct = operator.getTargetProduct();
        Band targetBand = targetProduct.getBandAt(0);

        checkExpectedValues(expectedValues, targetBand);
    }

    public void testProductWithoutIncidenceAngle() {
        int sceneRasterWidth = 4;
        int sceneRasterHeight = 4;
        String sourceBandName = "Band1";
        Product sourceProduct = new Product("IndexTest", "IndexTestType", sceneRasterWidth, sceneRasterHeight);

        int bandIndex = sourceProduct.getBandGroup().getNodeCount();
        Band band = buildBand(sourceBandName, sceneRasterWidth, sceneRasterHeight, 325, 5, 25);
        band.setSpectralBandIndex(bandIndex);
        sourceProduct.addBand(band);

        Map<String, Object> annotatedFields = new HashMap<String, Object>();
        annotatedFields.put(SOLLAR_IRRADIANCE_ATTRIBUTE_NAME, 1.2f);
        annotatedFields.put(U_ATTRIBUTE_NAME, 1.45f);
        annotatedFields.put(INCIDENCE_ANGLE_ATTRIBUTE_NAME, 0.0f);
        annotatedFields.put(SOURCE_BAND_NAMES_ATTRIBUTE_NAME, new String[]{sourceBandName});

        float[] expectedValues = new float[] {
                2.7649512f, 3.5022717f, 4.239592f, 4.976912f,
                5.714233f, 6.451553f, 7.1888733f, 7.9261937f,
                8.663514f, 9.400834f, 10.138155f, 10.875476f,
                11.612795f, 12.350116f, 13.087437f, 13.824756f
        };

        ReflectanceToRadianceOp operator = buildOperator(sourceProduct, annotatedFields);

        try {
            Product targetProduct = operator.getTargetProduct();
            Band targetBand = targetProduct.getBandAt(0);

            checkExpectedValues(expectedValues, targetBand);
        } catch (OperatorException exception) {
            assertEquals("Please specify the incidence angle.", exception.getMessage());
        }
    }

    private static ReflectanceToRadianceOp buildOperator(Product sourceProduct, Map<String, Object> annotatedFields) {
        ReflectanceToRadianceOp operator = new ReflectanceToRadianceOp();
        operator.setSourceProduct(sourceProduct);
        try {
            for (Map.Entry<String, Object> entry : annotatedFields.entrySet()) {
                Field field = operator.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (field.isAnnotationPresent(Parameter.class)) {
                    field.set(operator, entry.getValue());
                }
            }
        } catch (Exception e) {
            throw new OperatorException("Failed to set the operator parameter values.", e);
        }
        return operator;
    }

    private static void checkExpectedValues(float[] expectedValues, Band targetBand) {
        Raster data = targetBand.getSourceImage().getData();
        int rasterWidth = targetBand.getRasterWidth();
        int rasterHeight = targetBand.getRasterHeight();
        for (int i = 0; i < expectedValues.length; i++) {
            float aFloat = data.getSampleFloat(i % rasterHeight, i / rasterWidth, 0);
            assertTrue(aFloat == expectedValues[i]);
        }
    }

    private static Band buildBand(String bandName, int width, int height, float wavelength, float minSampleValue, float maxSampleValue) {
        int numElements = width * height;
        Band band = new Band(bandName, ProductData.TYPE_FLOAT32, width, height);
        band.setSpectralWavelength(wavelength);
        float[] sample = sampleData(minSampleValue, maxSampleValue, numElements);
        band.setRasterData(ProductData.createInstance(sample));
        return band;
    }

    private static float[] sampleData(float min, float max, int elements) {
        float[] values = new float[elements];
        float step = (max - min) / (elements - 1);
        for (int i = 0; i < elements; i++) {
            values[i] = min + i * step;
        }
        return values;
    }
}
