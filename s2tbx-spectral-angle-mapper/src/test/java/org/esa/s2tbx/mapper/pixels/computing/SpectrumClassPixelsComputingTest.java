package org.esa.s2tbx.mapper.pixels.computing;

import org.esa.s2tbx.mapper.AbstractOpTest;
import org.esa.s2tbx.mapper.common.SpectrumInput;
import org.esa.s2tbx.mapper.pixels.mean.SpectrumContainer;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Razvan Dumitrascu
 */

public class SpectrumClassPixelsComputingTest {

    private SpectrumClassReferencePixelsContainer specPixelsContainer;
    private int threadCount;

    @Test
    public void testSpectrumClassPixelsComputing() throws Exception {

        this.specPixelsContainer = new SpectrumClassReferencePixelsContainer();
        this.threadCount = Runtime.getRuntime().availableProcessors();

        SpectrumInput[] spectrumInput = new SpectrumInput[3];
        spectrumInput[0] = new SpectrumInput("spec1", new int[]{10}, new int[]{10});
        spectrumInput[0].setIsShapeDefined(false);
        spectrumInput[1] = new SpectrumInput("spec2", new int[]{20, 21}, new int[]{20, 21});
        spectrumInput[1].setIsShapeDefined(false);
        spectrumInput[2] = new SpectrumInput("spec3", new int[]{30, 40, 35}, new int[]{30, 30, 40});
        spectrumInput[2].setIsShapeDefined(false);

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
        assertEquals(3, specPixelsContainer.getElements().size());
        for (int index =0; index < specPixelsContainer.getElements().size(); index++) {
            assertEquals(spectrumInput[index].getName(),specPixelsContainer.getElements().get(index).getClassName() );
        }
        assertEquals(10,specPixelsContainer.getElements().get(0).getMaxXPosition());
        assertEquals(10,specPixelsContainer.getElements().get(0).getMinXPosition());
        assertEquals(10,specPixelsContainer.getElements().get(0).getMaxYPosition());
        assertEquals(10,specPixelsContainer.getElements().get(0).getMinYPosition());
        assertEquals(1,specPixelsContainer.getElements().get(0).getXPixelPositions().size());
        assertEquals(1,specPixelsContainer.getElements().get(0).getYPixelPositions().size());
        assertEquals(10,specPixelsContainer.getElements().get(0).getXPixelPositions().getInt(0));
        assertEquals(10,specPixelsContainer.getElements().get(0).getYPixelPositions().getInt(0));

        assertEquals(0,specPixelsContainer.getElements().get(1).getMaxXPosition());
        assertEquals(0,specPixelsContainer.getElements().get(1).getMinXPosition());
        assertEquals(0,specPixelsContainer.getElements().get(1).getMaxYPosition());
        assertEquals(0,specPixelsContainer.getElements().get(1).getMinYPosition());
        assertEquals(2,specPixelsContainer.getElements().get(1).getXPixelPositions().size());
        assertEquals(2,specPixelsContainer.getElements().get(1).getYPixelPositions().size());
        assertEquals(20,specPixelsContainer.getElements().get(1).getXPixelPositions().getInt(0));
        assertEquals(20,specPixelsContainer.getElements().get(1).getYPixelPositions().getInt(0));

        assertEquals(40,specPixelsContainer.getElements().get(2).getMaxXPosition());
        assertEquals(30,specPixelsContainer.getElements().get(2).getMinXPosition());
        assertEquals(40,specPixelsContainer.getElements().get(2).getMaxYPosition());
        assertEquals(30,specPixelsContainer.getElements().get(2).getMinYPosition());
        assertEquals(61,specPixelsContainer.getElements().get(2).getXPixelPositions().size());
        assertEquals(61,specPixelsContainer.getElements().get(2).getYPixelPositions().size());
        assertEquals(30,specPixelsContainer.getElements().get(2).getXPixelPositions().getInt(0));
        assertEquals(30,specPixelsContainer.getElements().get(2).getYPixelPositions().getInt(0));

    }
}
