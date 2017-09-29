package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.snap.utils.matrix.IntMatrix;

import java.util.Iterator;

/**
 * Created by rdumitrascu on 9/29/2017.
 */
public class MajorityVotingValidSegments {
    private final Int2IntMap validSegmentsMap;

    public MajorityVotingValidSegments (){
        this.validSegmentsMap = new Int2IntOpenHashMap();
    }

    public void clearMap(){
        if (this.validSegmentsMap.size() != 0) {
            this.validSegmentsMap.clear();
        }
    }

    public void computeWindowSegments(IntSet MovingWindowSegments, IntSet afterTrimmingSegments) {
        IntSet invalidSegmentsAfterTrimming = computeDifferenceIntSet(MovingWindowSegments,afterTrimmingSegments );
        addSegmentsToMap(MovingWindowSegments);
        updateSegmentsValidity(invalidSegmentsAfterTrimming, afterTrimmingSegments);

    }

    private void updateSegmentsValidity(IntSet invalidSegmentsAfterTrimming, IntSet afterTrimmingSegments) {
        addSegmentValidity(afterTrimmingSegments);
        decrementSegmentValidity(invalidSegmentsAfterTrimming);
    }

    public Int2IntMap getValidSegmentsMap() {
        return validSegmentsMap;
    }


    private void decrementSegmentValidity(IntSet invalidSegmentsAfterTrimming) {
        Iterator it = invalidSegmentsAfterTrimming.iterator();
        while (it.hasNext()) {
            int segmentValue = (int) it.next();
            ObjectIterator<Int2IntMap.Entry> iter = this.validSegmentsMap.int2IntEntrySet().iterator();
            while(iter.hasNext()) {
                Int2IntMap.Entry entry= iter.next();
                if(entry.getKey() == segmentValue) {
                    int valid = entry.getIntValue();
                    this.validSegmentsMap.replace(segmentValue, valid, valid-1);
                }

            }

        }
    }

    private void addSegmentValidity(IntSet afterTrimmingSegments) {
        Iterator it = afterTrimmingSegments.iterator();
        while (it.hasNext()) {
            int segmentValue = (int) it.next();
            ObjectIterator<Int2IntMap.Entry> iter = this.validSegmentsMap.int2IntEntrySet().iterator();
            while(iter.hasNext()) {
                Int2IntMap.Entry entry= iter.next();
                if(entry.getKey() == segmentValue) {
                    int valid = entry.getIntValue();
                    this.validSegmentsMap.replace(segmentValue, valid, valid+1);
                }

            }

        }
    }


    private IntSet computeDifferenceIntSet(IntSet movingWindowSegments, IntSet afterTrimmingSegments) {
        IntSet diffSet  = new IntOpenHashSet();
        Iterator it = movingWindowSegments.iterator();
        while(it.hasNext()) {
            int value = (int)it.next();
            if(!afterTrimmingSegments.contains(value)) {
                diffSet.add(value);
            }
        }
        return diffSet;
    }

    private void addSegmentsToMap(IntSet movingWindowSegments) {
        Iterator it = movingWindowSegments.iterator();
        while(it.hasNext()) {
            int value = (int)it.next();
            if (!this.validSegmentsMap.containsKey(value)){
                this.validSegmentsMap.put(value, 0);
            }
        }
    }
}
