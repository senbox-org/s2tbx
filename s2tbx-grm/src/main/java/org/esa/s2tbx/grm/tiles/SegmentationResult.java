package org.esa.s2tbx.grm.tiles;

/**
 * @author Jean Coravu
 */
public class SegmentationResult {
    private boolean isFusion = false;
    private long accumulatedMemory = 0;

    public SegmentationResult(boolean isFusion, long accumulatedMemory) {
        this.isFusion = isFusion;
        this.accumulatedMemory = accumulatedMemory;
    }

    public boolean isFusion() {
        return isFusion;
    }

    public long getAccumulatedMemory() {
        return accumulatedMemory;
    }
}
