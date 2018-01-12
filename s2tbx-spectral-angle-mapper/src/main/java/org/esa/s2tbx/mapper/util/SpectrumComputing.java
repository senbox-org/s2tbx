package org.esa.s2tbx.mapper.util;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;

import java.util.Arrays;

/**
 * Created by rdumitrascu on 1/11/2018.
 */
public class SpectrumComputing  implements Runnable{

    private final SpectrumClassReferencePixels spectrumClassReferencePixels;
    private final Product sourceProduct;
    private final String[] sourceBands;

    public SpectrumComputing(SpectrumClassReferencePixels spectrumPixels, Product sourceProduct, String[] sourceBands){
        this.spectrumClassReferencePixels = spectrumPixels;
        this.sourceProduct = sourceProduct;
        this.sourceBands = sourceBands;
    }

    public void execute(){
        Spectrum  spec = null;
        FloatArrayList pixelsValues = new FloatArrayList();
        FloatArrayList meanValues = new FloatArrayList();
        for (int index = 0; index < this.sourceProduct.getNumBands(); index++) {
            pixelsValues.clear();
            if (Arrays.asList(this.sourceBands).contains(this.sourceProduct.getBandAt(index).getName())) {
                Band band = this.sourceProduct.getBandAt(index);
                for (int intIndex = 0; intIndex < this.spectrumClassReferencePixels.getXPixelPositions().size(); intIndex++) {
                    int x = this.spectrumClassReferencePixels.getXPixelPositions().getInt(intIndex);
                    int y = this.spectrumClassReferencePixels.getYPixelPositions().getInt(intIndex);
                    pixelsValues.add(band.getSampleFloat(x, y));
                }
            }
            double sum = 0;
            for(float value : pixelsValues) {
                sum += value;
            }
            meanValues.add((float)sum/pixelsValues.size());
        }
        spec = new Spectrum(spectrumClassReferencePixels.getClassName(), meanValues.toFloatArray(new float[meanValues.size()]));
        SpectrumSingleton.getInstance().addElements(spec);
    }

    @Override
    public void run() {
        execute();
        System.out.println(Thread.currentThread().getName()+" End.");
    }
}
