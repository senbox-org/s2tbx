package org.esa.s2tbx.dataio.s2;


/**
 * Created by obarrile on 18/05/2017.
 */
public class S2BandAnglesGridByDetector extends S2BandAnglesGrid {

    private int detectorId;

    public S2BandAnglesGridByDetector(String prefix, S2BandConstants band, int detectorId, int width, int height, float originX, float originY, float resX, float resY, float[] values) {
        super(prefix, band, width, height, originX, originY, resX, resY, values);
        this.detectorId = detectorId;
    }

    public int getDetectorId() {
        return detectorId;
    }

}
