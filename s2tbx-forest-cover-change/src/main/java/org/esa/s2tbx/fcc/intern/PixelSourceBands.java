package org.esa.s2tbx.fcc.intern;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class PixelSourceBands {
    private double meanValueB4Band;
    private double meanvalueB8Band;
    private double meanValueB11Band;
    private double standardDeviationValueB8Band;

    public PixelSourceBands(){

    }
    public PixelSourceBands(double valueB4Band, double valueB8Band, double valueB11Band, double valueB12Band){
        this.meanValueB4Band = valueB4Band;
        this.meanvalueB8Band = valueB8Band;
        this.meanValueB11Band = valueB11Band;
        this.standardDeviationValueB8Band = valueB12Band;
    }

    public double getMeanValueB4Band(){
        return this.meanValueB4Band;
    }

    public double getMeanValueB8Band(){
        return this.meanvalueB8Band;
    }

    public double getMeanValueB11Band(){
        return this.meanValueB11Band;
    }

    public double getStandardDeviationValueB8Band(){
        return this.standardDeviationValueB8Band;
    }
}
