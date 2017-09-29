package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * Class used for the operation of majority voting for a set of segments representing the total number
 * of segments validated after colorFiller operation
 *
 * @author Razvan Dumitrascu
 */

public class MajorityVotingValidSegments {
    private final Int2IntMap validSegmentsMap;

    public MajorityVotingValidSegments (){
        this.validSegmentsMap = new Int2IntOpenHashMap();
    }

    /**
     *
     * @return returns the Int2IntMap containing the valid segments after color Filler and the times they have been validated
     */
    public Int2IntMap getValidSegmentsMap() {
        return validSegmentsMap;
    }

    /**
     *
     * @return returns a specific value, representing the number of times the segment  has been validated, for a specific key(segment)
     */
    public int getValidSegmentsValue(int key) {
        int value = 0;
        for (Int2IntMap.Entry entry : this.validSegmentsMap.int2IntEntrySet()) {
            if (entry.getKey() == key) {
                value = entry.getValue();
            }
        }
        return value;
    }

    /**
     *
     * @param MovingWindowSegments a IntSet containing a list of segments from a tile
     * @param afterTrimmingSegments a IntSet containing a list of valid segments after trimming operation from a tile
     */
    public void computeWindowSegments(IntSet MovingWindowSegments, IntSet afterTrimmingSegments) {
        addSegmentsToMap(MovingWindowSegments);
        updateSegmentsValidity(MovingWindowSegments, afterTrimmingSegments);

    }

    private void updateSegmentsValidity(IntSet MovingWindowSegments, IntSet afterTrimmingSegments) {
        for (Object MovingWindowSegment : MovingWindowSegments) {
            int segmentValue = (int) MovingWindowSegment;
            this.validSegmentsMap.int2IntEntrySet().stream().filter(entry -> entry.getKey() == segmentValue).forEach(entry -> {
                int valid = entry.getIntValue();
                if (afterTrimmingSegments.contains(segmentValue)) {
                    entry.setValue(valid + 1);
                } else {
                    entry.setValue(valid - 1);
                }
            });
        }
    }

    private void addSegmentsToMap(IntSet movingWindowSegments) {
        for (Object movingWindowSegment : movingWindowSegments) {
            int value = (int) movingWindowSegment;
            if (!this.validSegmentsMap.containsKey(value)) {
                this.validSegmentsMap.put(value, 0);
            }
        }
    }
}
