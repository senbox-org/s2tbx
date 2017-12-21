package org.esa.s2tbx.mapper.util;

/**
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumClass {

    private String className;
    //mean value for each band
    private double[] meanValue;
    //standard deviation value for each band
    private double[] standardDeviationValue;
    //minimum value for each band
    private double[] maximumValue;
    //maximum value for each band
    private double[] minimumDeviationValue;

    public SpectrumClass() {}

    public SpectrumClass(String className, double[] meanValue){
        this.className = className;
        this.meanValue = meanValue;
    }

}
