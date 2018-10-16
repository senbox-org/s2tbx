package org.esa.s2tbx.mapper;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Razvan Dumitrascu
 */
public class SpectralAngleMapperOpTest  extends AbstractOpTest{

    @Test
    public void testSpectralAngleMapperOp() throws Exception {
        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File currentProductFile = this.SpectralAngleMapperTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
        Product sourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        SpectrumInput[] spectrumInput = new SpectrumInput[3];
        spectrumInput[0] = new SpectrumInput("spec1", new int[]{10}, new int[]{10});
        spectrumInput[0].setIsShapeDefined(false);
        spectrumInput[1] = new SpectrumInput("spec2", new int[]{20, 21}, new int[]{20, 21});
        spectrumInput[1].setIsShapeDefined(false);
        spectrumInput[2] = new SpectrumInput("spec3", new int[]{30, 40, 35}, new int[]{30, 30, 40});
        spectrumInput[2].setIsShapeDefined(false);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("referenceBands", new String[]{"B4","B8","B11","B12"});
        parameters.put("thresholds", "0.08,0.08,0.08");
        parameters.put("spectra",spectrumInput);
        parameters.put("hiddenSpectra",spectrumInput);
        parameters.put("resampleType", "None");

        Map<String, Product> sourceProducts = new HashMap<String, Product>();
        sourceProducts.put("sourceProduct", sourceProduct);

        // create the operator
        Operator operator = GPF.getDefaultInstance().createOperator("SpectralAngleMapperOp", parameters, sourceProducts, null);

        // execute the operator
        operator.execute(ProgressMonitor.NULL);

        // get the operator target product
        Product targetProduct = operator.getTargetProduct();

        assertNotNull(targetProduct);

        assertEquals(549, targetProduct.getSceneRasterWidth());
        assertEquals(549, targetProduct.getSceneRasterHeight());

        assertEquals(1, targetProduct.getNumBands());

        Band band = targetProduct.getBandAt(0);
        assertNotNull(band);

        assertEquals(ProductData.TYPE_INT32, band.getDataType());

        long size = targetProduct.getSceneRasterWidth() * targetProduct.getSceneRasterHeight();
        assertEquals(size, band.getNumDataElems());

        checkBand(band);
    }

    private static void checkBand(Band band) {
        assertEquals(200, band.getSampleInt(10, 10));
        assertEquals(400, band.getSampleInt(20, 20));
        assertEquals(400, band.getSampleInt(21, 21));
        assertEquals(600, band.getSampleInt(32, 32));
        assertEquals(0, band.getSampleInt(504, 220));
        assertEquals(0, band.getSampleInt(223, 384));

        assertEquals(400, band.getSampleInt(219, 384));
        assertEquals(400, band.getSampleInt(138, 163));
        assertEquals(0, band.getSampleInt(244, 156));
        assertEquals(400, band.getSampleInt(93, 358));

        assertEquals(0, band.getSampleInt(198, 161));
        assertEquals(400, band.getSampleInt(39, 509));
        assertEquals(0, band.getSampleInt(214, 240));
        assertEquals(0, band.getSampleInt(207, 386));

        assertEquals(0, band.getSampleInt(175, 166));
        assertEquals(0, band.getSampleInt(202, 137));
        assertEquals(0, band.getSampleInt(500, 367));
        assertEquals(0, band.getSampleInt(31, 480));

        assertEquals(400, band.getSampleInt(1, 1));
        assertEquals(600, band.getSampleInt(63, 368));
        assertEquals(400, band.getSampleInt(475, 63));
        assertEquals(0, band.getSampleInt(438, 213));
    }
}
