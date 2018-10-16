package org.esa.s2tbx.mapper.pixels.mean;

import org.esa.s2tbx.mapper.AbstractOpTest;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.s2tbx.mapper.pixels.computing.SpectrumClassPixelsComputing;
import org.esa.s2tbx.mapper.pixels.computing.SpectrumClassReferencePixelsContainer;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Razvan Dumitrascu
 */

public class SpectrumComputingTest extends AbstractOpTest
{
    private SpectrumClassReferencePixelsContainer specPixelsContainer;
    private SpectrumContainer spectrumContainer;
    private int threadCount;

    @Test
    public void testSpectrumComputing() throws Exception {

        ProductReaderPlugIn productReaderPlugIn = buildDimapProductReaderPlugIn();

        File currentProductFile = this.SpectralAngleMapperTestsFolderPath.resolve("S2A_R093_T35UMP_20170628T092026.dim").toFile();
        Product sourceProduct = productReaderPlugIn.createReaderInstance().readProductNodes(currentProductFile, null);

        this.specPixelsContainer = new SpectrumClassReferencePixelsContainer();
        this.spectrumContainer = new SpectrumContainer();

        SpectrumInput[] spectrumInput = new SpectrumInput[3];
        spectrumInput[0] = new SpectrumInput("spec1", new int[]{10}, new int[]{10});
        spectrumInput[0].setIsShapeDefined(false);
        spectrumInput[1] = new SpectrumInput("spec2", new int[]{20, 21}, new int[]{20, 21});
        spectrumInput[1].setIsShapeDefined(false);
        spectrumInput[2] = new SpectrumInput("spec3", new int[]{30, 40, 35}, new int[]{30, 30, 40});
        spectrumInput[2].setIsShapeDefined(false);
        String[] referenceBands = new String[]{"B4","B8","B11","B12"};
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        for (SpectrumInput aSpectra : spectrumInput) {
            Runnable worker = new SpectrumClassPixelsComputing(aSpectra, this.specPixelsContainer);
            threadPool.execute(worker);
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        threadPool = Executors.newSingleThreadExecutor();
        for (int i = 0; i < spectrumInput.length; i++) {
            Runnable worker = new SpectrumComputing(this.specPixelsContainer.getElements().get(i), sourceProduct, referenceBands, this.spectrumContainer);
            threadPool.execute(worker);
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertEquals(3, spectrumContainer.getElements().size());
       for(int index = 0; index <  spectrumContainer.getElements().size(); index++){
           assertEquals(spectrumInput[index].getName(),spectrumContainer.getElements().get(index).getClassName() );
           assertEquals(4,spectrumContainer.getElements().get(index).getMeanValue().length );
       }
        assertEquals(0.0509f,spectrumContainer.getElements().get(0).getMeanValue()[0] );
        assertEquals(0.288f,spectrumContainer.getElements().get(0).getMeanValue()[1] );
        assertEquals(0.1864f,spectrumContainer.getElements().get(0).getMeanValue()[2] );
        assertEquals(0.099f,spectrumContainer.getElements().get(0).getMeanValue()[3] );

        assertEquals(0.050049998f,spectrumContainer.getElements().get(1).getMeanValue()[0] );
        assertEquals(0.34280002f,spectrumContainer.getElements().get(1).getMeanValue()[1] );
        assertEquals(0.1832f,spectrumContainer.getElements().get(1).getMeanValue()[2] );
        assertEquals(0.090450004f,spectrumContainer.getElements().get(1).getMeanValue()[3] );

        assertEquals(0.0713541f,spectrumContainer.getElements().get(2).getMeanValue()[0] );
        assertEquals(0.27785245f,spectrumContainer.getElements().get(2).getMeanValue()[1] );
        assertEquals(0.17900163f,spectrumContainer.getElements().get(2).getMeanValue()[2] );
        assertEquals(0.0998836f,spectrumContainer.getElements().get(2).getMeanValue()[3] );
    }
}
