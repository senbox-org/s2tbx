package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;

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

    public IntSet computeValidSegmentIds() {
        IntSet validSegmentIds = new IntOpenHashSet();
        ObjectIterator<Int2IntMap.Entry> it = this.validSegmentsMap.int2IntEntrySet().iterator();
        while (it.hasNext()) {
            Int2IntMap.Entry entry = it.next();
            if (entry.getIntValue() > 0) {
                validSegmentIds.add(entry.getIntKey());
            }
        }
        return validSegmentIds;
    }

    /**
     *
     * @param movingWindowValidSegmentIds a IntSet containing a list of segments from a tile
     * @param validSegmentIdsAfterTrimming a IntSet containing a list of valid segments after trimming operation from a tile
     */
    public void processMovingWindowValidSegments(IntSet movingWindowValidSegmentIds, IntSet validSegmentIdsAfterTrimming) {
        IntIterator itMovingWindow = movingWindowValidSegmentIds.iterator();
        while (itMovingWindow.hasNext()) {
            int validSegmentId = itMovingWindow.nextInt();
            int existingValue = 0;
            if (this.validSegmentsMap.containsKey(validSegmentId)) {
                existingValue = this.validSegmentsMap.get(validSegmentId);
            }

            if (validSegmentIdsAfterTrimming.contains(validSegmentId)) {
                // the segment from the moving window exists in the trimming set
                existingValue += 1;
            } else {
                existingValue -= 1;
            }
            this.validSegmentsMap.put(validSegmentId, existingValue);
        }
    }
}
