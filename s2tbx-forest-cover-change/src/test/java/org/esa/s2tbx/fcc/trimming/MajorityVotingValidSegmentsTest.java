package org.esa.s2tbx.fcc.trimming;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jean Coravu
 */
public class MajorityVotingValidSegmentsTest {

    public MajorityVotingValidSegmentsTest() {
    }

    @Test
    public void testMajorityVotingValidSegments() {
        MajorityVotingValidSegments majorityVotingValidSegments = new MajorityVotingValidSegments();

        IntSet movingWindowValidSegmentIds = new IntOpenHashSet();
        movingWindowValidSegmentIds.add(1);
        movingWindowValidSegmentIds.add(2);
        movingWindowValidSegmentIds.add(3);
        movingWindowValidSegmentIds.add(4);
        movingWindowValidSegmentIds.add(5);
        movingWindowValidSegmentIds.add(6);
        movingWindowValidSegmentIds.add(7);
        movingWindowValidSegmentIds.add(8);
        movingWindowValidSegmentIds.add(9);
        movingWindowValidSegmentIds.add(10);
        movingWindowValidSegmentIds.add(11);
        movingWindowValidSegmentIds.add(12);
        movingWindowValidSegmentIds.add(13);
        movingWindowValidSegmentIds.add(14);
        movingWindowValidSegmentIds.add(15);
        movingWindowValidSegmentIds.add(16);
        movingWindowValidSegmentIds.add(17);
        movingWindowValidSegmentIds.add(18);
        movingWindowValidSegmentIds.add(19);
        movingWindowValidSegmentIds.add(20);

        IntSet validSegmentIdsAfterTrimming = new IntOpenHashSet();
        validSegmentIdsAfterTrimming.add(1);
        validSegmentIdsAfterTrimming.add(2);
        validSegmentIdsAfterTrimming.add(4);
        validSegmentIdsAfterTrimming.add(6);
        validSegmentIdsAfterTrimming.add(7);
        validSegmentIdsAfterTrimming.add(8);
        validSegmentIdsAfterTrimming.add(9);
        validSegmentIdsAfterTrimming.add(12);
        validSegmentIdsAfterTrimming.add(14);
        validSegmentIdsAfterTrimming.add(15);
        validSegmentIdsAfterTrimming.add(16);
        validSegmentIdsAfterTrimming.add(17);
        validSegmentIdsAfterTrimming.add(18);
        validSegmentIdsAfterTrimming.add(19);

        majorityVotingValidSegments.processMovingWindowValidSegments(movingWindowValidSegmentIds, validSegmentIdsAfterTrimming);

        movingWindowValidSegmentIds = new IntOpenHashSet();
        movingWindowValidSegmentIds.add(10);
        movingWindowValidSegmentIds.add(11);
        movingWindowValidSegmentIds.add(12);
        movingWindowValidSegmentIds.add(13);
        movingWindowValidSegmentIds.add(14);
        movingWindowValidSegmentIds.add(15);
        movingWindowValidSegmentIds.add(16);
        movingWindowValidSegmentIds.add(17);
        movingWindowValidSegmentIds.add(18);
        movingWindowValidSegmentIds.add(19);
        movingWindowValidSegmentIds.add(20);

        validSegmentIdsAfterTrimming = new IntOpenHashSet();
        validSegmentIdsAfterTrimming.add(1);
        validSegmentIdsAfterTrimming.add(2);
        validSegmentIdsAfterTrimming.add(4);
        validSegmentIdsAfterTrimming.add(6);
        validSegmentIdsAfterTrimming.add(7);
        validSegmentIdsAfterTrimming.add(8);
        validSegmentIdsAfterTrimming.add(9);
        validSegmentIdsAfterTrimming.add(12);
        validSegmentIdsAfterTrimming.add(14);

        majorityVotingValidSegments.processMovingWindowValidSegments(movingWindowValidSegmentIds, validSegmentIdsAfterTrimming);

        IntSet validSegmentIds = majorityVotingValidSegments.computeValidSegmentIds();

        assertNotNull(validSegmentIds);

        assertEquals(9, validSegmentIds.size());

        assertTrue(validSegmentIds.contains(1));
        assertTrue(validSegmentIds.contains(12));
        assertTrue(validSegmentIds.contains(9));
        assertTrue(validSegmentIds.contains(14));
        assertTrue(validSegmentIds.contains(8));
        assertTrue(validSegmentIds.contains(6));
        assertTrue(validSegmentIds.contains(2));
        assertTrue(validSegmentIds.contains(4));
        assertTrue(validSegmentIds.contains(7));
    }
}
