package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.floats.FloatArrayList;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */

public class AveragePixelsSourceBands {
    //list containing the values used to compute standard deviation
    private final FloatArrayList standardDeviationPixelValues;

    private float sumValueB4Band;
    private float sumValueB8Band;
    private float sumValueB11Band;

    public AveragePixelsSourceBands() {
        this.standardDeviationPixelValues = new FloatArrayList();
        this.sumValueB4Band = 0.0f;
        this.sumValueB8Band = 0.0f;
        this.sumValueB11Band = 0.0f;
    }

    public void addPixelValuesBands(float valueB4Band, float valueB8Band, float valueB11Band){
        this.sumValueB4Band += valueB4Band;
        this.sumValueB8Band += valueB8Band;
        this.sumValueB11Band += valueB11Band;
        this.standardDeviationPixelValues.add(valueB8Band);
    }

    public float getMeanValueB4Band(){
        return this.sumValueB4Band/(float)standardDeviationPixelValues.size();
    }

    public float getMeanValueB8Band(){
        return this.sumValueB8Band/(float)standardDeviationPixelValues.size();
    }

    public float getMeanValueB11Band(){
        return this.sumValueB11Band/(float)standardDeviationPixelValues.size();
    }

    public float getMeanStandardDeviationB8Band(){
        // compute the standard deviation
        float averageStandardDeviationB8PixelValue = getMeanValueB8Band();
        float sum = 0.0f;
        for (float pixelValue : standardDeviationPixelValues) {
            float value = pixelValue - averageStandardDeviationB8PixelValue;
            sum += Math.pow((value), 2);
        }
        float average = sum / (float)standardDeviationPixelValues.size();
        return (float)Math.sqrt(average);
    }

}
