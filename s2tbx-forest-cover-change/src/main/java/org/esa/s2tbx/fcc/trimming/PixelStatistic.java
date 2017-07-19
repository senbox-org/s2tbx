package org.esa.s2tbx.fcc.trimming;

/**
 * @author Jean Coravu
 */
public final class PixelStatistic {
    int totalNumberPixels;
    int pixelsInRage;

    public PixelStatistic(int totalNumberPixels, int pixelsInRage) {
        this.totalNumberPixels = totalNumberPixels;
        this.pixelsInRage = pixelsInRage;
    }

    public int getTotalNumberPixels() {
        return totalNumberPixels;
    }

    public void incrementTotalNumberPixels(){
        this.totalNumberPixels++;
    }

    public int getPixelsInRange() {
        return pixelsInRage;
    }

    public void incrementPixelsInRange(){
        this.pixelsInRage++;
    }
}
