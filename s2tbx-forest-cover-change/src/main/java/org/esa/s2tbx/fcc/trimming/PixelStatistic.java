package org.esa.s2tbx.fcc.trimming;

/**
 * @author Jean Coravu
 */
public final class PixelStatistic {
    private int totalNumberPixels;
    private int pixelsInRage;

    public PixelStatistic(int totalNumberPixels, int pixelsInRage) {
        this.totalNumberPixels = totalNumberPixels;
        this.pixelsInRage = pixelsInRage;
    }

    public int getTotalNumberPixels() {
        return this.totalNumberPixels;
    }

    public void incrementTotalNumberPixels(){
        this.totalNumberPixels++;
    }

    public int getPixelsInRange() {
        return this.pixelsInRage;
    }

    public void incrementPixelsInRange(){
        this.pixelsInRage++;
    }

    public float computePixelsPercentage() {
        return ((float)this.pixelsInRage/(float)this.totalNumberPixels) * 100.0f;
    }
}
