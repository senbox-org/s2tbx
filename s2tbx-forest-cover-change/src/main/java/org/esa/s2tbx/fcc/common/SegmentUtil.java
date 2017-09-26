package org.esa.s2tbx.fcc.common;

/**
 * Created by rdumitrascu on 9/26/2017.
 */

public class SegmentUtil {

    int segmentId;
    int numberOfPixels;
    int validSegmentState;

    public SegmentUtil(int segmentId) {
        this.segmentId = segmentId;
    }

    public void setNumberOfPixels( int numberOfPixels){
        this.numberOfPixels = numberOfPixels;
    }
    public int getNumberOfPixels(){
        return this.numberOfPixels;
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
