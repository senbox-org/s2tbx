package org.esa.s2tbx.fcc.intern;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class PixelsSourceBands {
    private float valueB4Band;
    private float valueB8Band;
    private float valueB11Band;
    private float valueB12Band;

    public PixelsSourceBands(){

    }
    public PixelsSourceBands(float valueB4Band, float valueB8Band, float valueB11Band, float valueB12Band){
        this.valueB4Band = valueB4Band;
        this.valueB8Band = valueB8Band;
        this.valueB11Band = valueB11Band;
        this.valueB12Band = valueB12Band;
    }

    public float getValueB4Band(){
        return this.valueB4Band;
    }

    public float getValueB8Band(){
        return this.valueB8Band;
    }

    public float getValueB11Band(){
        return this.valueB11Band;
    }

    public float getValueB12Band(){
        return this.valueB12Band;
    }
}
