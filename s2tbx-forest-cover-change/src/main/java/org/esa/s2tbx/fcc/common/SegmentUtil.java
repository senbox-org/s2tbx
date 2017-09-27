package org.esa.s2tbx.fcc.common;

/**
 * Created by rdumitrascu on 9/26/2017.
 */

public class SegmentUtil {

    int segmentId;
    int validSegmentState;

    public SegmentUtil(int segmentId) {
        this.segmentId = segmentId;
    }

    public void incrementValidSegmentState() {
        this.validSegmentState++;
    }
    public void decrementValidSegmentState() {
        this.validSegmentState--;
    }

    public int getValidSegmentState() {
        return this.validSegmentState;
    }

}
