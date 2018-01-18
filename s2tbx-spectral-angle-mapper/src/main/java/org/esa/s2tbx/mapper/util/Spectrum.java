package org.esa.s2tbx.mapper.util;

/**
 *
 * @author Razvan Dumitrascu
 */
public class Spectrum {

    private String className;
    //mean value for each band
    private float[] meanValue;
    //minimum value for each band

    public Spectrum() {}

    public Spectrum(String className, float[] meanValue){
        this.setClassName(className);
        this.setMeanValue(meanValue);
    }

    public String getClassName() {
        return className;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    public float[] getMeanValue() {
        return meanValue;
    }

    private void setMeanValue(float[] meanValue) {
        this.meanValue = meanValue;
    }
}
