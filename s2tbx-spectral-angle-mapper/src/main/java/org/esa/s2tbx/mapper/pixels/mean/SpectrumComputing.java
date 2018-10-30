package org.esa.s2tbx.mapper.pixels.mean;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.esa.s2tbx.mapper.pixels.computing.SpectrumClassReferencePixels;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;

/**
 *  Computes the mean value for each source band for a specific region defined by the user
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumComputing  implements Runnable{

    private final SpectrumClassReferencePixels spectrumClassReferencePixels;
    private final SpectrumContainer spectrumContainer;
    private final Product sourceProduct;
    private final String[] sourceBands;

    public SpectrumComputing(SpectrumClassReferencePixels spectrumPixels, Product sourceProduct, String[] sourceBands, SpectrumContainer spectrumContainer){
        this.spectrumClassReferencePixels = spectrumPixels;
        this.spectrumContainer = spectrumContainer;
        this.sourceProduct = sourceProduct;
        this.sourceBands = sourceBands;
    }

    private void execute(){
        Spectrum spec;
        FloatArrayList pixelsValues = new FloatArrayList();
        FloatArrayList meanValues = new FloatArrayList();
        for (String sourceBand : this.sourceBands) {
            pixelsValues.clear();
            Band band = this.sourceProduct.getBand(sourceBand);
            for (int intIndex = 0; intIndex < this.spectrumClassReferencePixels.getXPixelPositions().size(); intIndex++) {
                int x = this.spectrumClassReferencePixels.getXPixelPositions().getInt(intIndex);
                int y = this.spectrumClassReferencePixels.getYPixelPositions().getInt(intIndex);
                pixelsValues.add(band.getSampleFloat(x, y));
            }
            double sum = 0;
            for (float value : pixelsValues) {
                sum += value;
            }
            meanValues.add((float) sum / pixelsValues.size());
        }
        spec = new Spectrum(spectrumClassReferencePixels.getClassName(), meanValues.toFloatArray(new float[meanValues.size()]));
        this.spectrumContainer.addElements(spec);
    }

    @Override
    public void run() {
        execute();
    }
}
