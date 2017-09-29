package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.junit.Test;

import java.awt.Rectangle;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Tonio Fincke
 */
public class PotentialCloudShadowAreaIdentifierTest {

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_corner() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 0, 4, 4,
                0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4, 4, 4,
                0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4,
                0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 0, 0, 0, 0, 0, 0, 3, 3,
                0, 0, 0, 0, 5, 5, 0, 0, 6, 6, 1, 1, 0, 0, 7, 7, 0, 0, 8, 8,
                0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 7, 7, 7, 7, 8, 8, 8,
                0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 0, 7, 7, 7, 7, 8,
                0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 7, 7, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 2, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 6, 6, 0, 0, 1, 7,
                0, 0, 0, 0, 9, 9, 0, 0, 10, 10, 0, 5, 0, 0, 11, 11, 0, 0, 12, 12,
                0, 0, 0, 9, 9, 9, 9, 10, 10, 10, 10, 0, 5, 11, 11, 11, 11, 12, 12, 12,
                0, 0, 0, 0, 0, 9, 9, 9, 9, 10, 10, 10, 10, 5, 5, 11, 11, 11, 11, 12,
                0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 10, 10, 0, 0, 0, 5, 0, 0, 11, 11,
                0, 0, 0, 0, 13, 13, 0, 0, 14, 14, 9, 9, 0, 0, 15, 15, 0, 0, 16, 16,
                0, 0, 0, 13, 13, 13, 13, 14, 14, 14, 14, 9, 9, 15, 15, 15, 15, 16, 16, 16,
                0, 0, 0, 0, 0, 13, 13, 13, 13, 14, 14, 14, 14, 9, 0, 15, 15, 15, 15, 16};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 10, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_corner() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 0, 4, 4, 0, 0, 0, 0,
                1, 1, 1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4, 4, 4, 4, 0, 0, 0,
                1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4, 4, 4, 4, 0, 0, 0, 0, 0,
                2, 2, 0, 0, 0, 3, 0, 0, 3, 3, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0,
                5, 5, 0, 0, 6, 6, 0, 0, 4, 4, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0,
                5, 5, 5, 6, 6, 6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0,
                5, 6, 6, 6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0,
                6, 6, 6, 6, 4, 7, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0,
                6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0,
                4, 4, 0, 0, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                9, 9, 0, 0, 10, 10, 0, 0, 8, 0, 11, 11, 0, 0, 12, 12, 0, 0, 0, 0,
                9, 9, 9, 10, 10, 10, 10, 0, 0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0,
                9, 10, 10, 10, 10, 8, 0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0, 0, 0,
                10, 10, 0, 0, 0, 11, 0, 0, 11, 11, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0,
                13, 13, 0, 0, 14, 14, 0, 0, 12, 12, 15, 15, 0, 0, 16, 16, 0, 0, 0, 0,
                13, 13, 13, 14, 14, 14, 14, 12, 12, 15, 15, 15, 15, 16, 16, 16, 16, 0, 0, 0,
                13, 14, 14, 14, 14, 12, 12, 15, 15, 15, 15, 16, 16, 16, 16, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 10, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_corner() throws Exception {
        int[] expectedCloudShadowIdArray = {
                1, 1, 1, 6, 2, 2, 2, 7, 7, 7, 3, 3, 3, 8, 4, 4, 4, 0, 0, 0,
                15, 1, 1, 6, 11, 2, 2, 12, 7, 7, 0, 3, 3, 8, 0, 4, 4, 0, 0, 0,
                5, 1, 0, 0, 6, 2, 0, 0, 7, 7, 7, 3, 0, 0, 8, 4, 0, 0, 0, 0,
                5, 5, 0, 0, 6, 6, 0, 0, 0, 7, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0,
                5, 5, 5, 15, 6, 6, 6, 11, 16, 12, 7, 7, 7, 0, 8, 8, 8, 0, 0, 0,
                10, 5, 5, 15, 15, 6, 6, 11, 11, 16, 12, 7, 7, 0, 0, 8, 8, 0, 0, 0,
                14, 5, 0, 0, 15, 6, 0, 0, 11, 11, 16, 7, 0, 0, 0, 8, 0, 0, 0, 0,
                14, 0, 0, 0, 0, 15, 0, 0, 11, 11, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                9, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0, 0, 0,
                9, 9, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0, 0,
                9, 9, 9, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0,
                0, 9, 9, 14, 0, 10, 10, 0, 15, 15, 0, 11, 11, 16, 0, 12, 12, 0, 0, 0,
                13, 9, 0, 0, 14, 10, 0, 0, 15, 15, 15, 11, 0, 0, 16, 12, 0, 0, 0, 0,
                13, 13, 0, 0, 14, 14, 0, 0, 0, 15, 15, 15, 0, 0, 16, 16, 0, 0, 0, 0,
                13, 13, 13, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 16, 16, 16, 0, 0, 0,
                0, 13, 13, 0, 0, 14, 14, 0, 0, 0, 0, 15, 15, 0, 0, 16, 16, 0, 0, 0,
                0, 13, 0, 0, 0, 14, 0, 0, 0, 0, 0, 15, 0, 0, 0, 16, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 0, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_corner() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 1, 1, 1, 0, 2, 2, 2, 0, 6, 6, 3, 3, 3, 10, 4, 4,
                0, 0, 0, 1, 1, 1, 0, 2, 2, 2, 0, 6, 6, 3, 3, 3, 10, 4, 4, 4,
                0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 6, 6, 0, 0, 3, 13, 0, 0, 4, 14,
                0, 0, 0, 0, 0, 5, 0, 0, 0, 6, 6, 6, 0, 0, 13, 7, 0, 0, 14, 8,
                0, 0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 9, 13, 7, 7, 7, 14, 8, 8,
                0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 9, 13, 7, 7, 7, 14, 8, 8, 8,
                0, 0, 0, 0, 5, 0, 0, 0, 6, 9, 9, 0, 0, 0, 7, 0, 0, 0, 8, 11,
                0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 0, 10, 0, 0, 0, 14, 0, 0, 11, 11,
                0, 0, 0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0,
                0, 0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 12,
                0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 12, 12,
                0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 12, 12, 12,
                0, 0, 0, 0, 9, 0, 0, 0, 10, 0, 14, 14, 0, 0, 11, 0, 0, 0, 12, 0,
                0, 0, 0, 0, 0, 13, 0, 0, 0, 14, 14, 14, 0, 0, 0, 15, 0, 0, 0, 16,
                0, 0, 0, 0, 13, 13, 13, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 16, 16,
                0, 0, 0, 13, 13, 13, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 16, 16, 16,
                0, 0, 0, 0, 13, 0, 0, 0, 14, 0, 0, 0, 0, 0, 15, 0, 0, 0, 16, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 0, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_center() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0,
                0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 0, 0, 0, 0, 0, 0, 3, 3,
                0, 0, 0, 0, 5, 5, 0, 0, 6, 6, 1, 1, 0, 0, 7, 7, 0, 0, 3, 3,
                0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 7, 7, 7, 7, 0, 0, 3,
                0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 0, 7, 7, 7, 7, 0,
                0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 1, 7, 7, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 6, 6, 6, 6, 1, 2, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 6, 6, 0, 0, 1, 7,
                0, 0, 0, 0, 9, 9, 0, 0, 10, 10, 0, 5, 0, 0, 11, 11, 0, 0, 1, 1,
                0, 0, 0, 9, 9, 9, 9, 10, 10, 10, 10, 0, 5, 11, 11, 11, 11, 6, 6, 6,
                0, 0, 0, 0, 0, 9, 9, 9, 9, 10, 10, 10, 10, 5, 5, 11, 11, 11, 11, 6,
                0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 10, 10, 0, 0, 0, 5, 0, 0, 11, 11,
                0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9, 0, 0, 10, 10, 0, 0, 11, 11,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9, 10, 10, 10, 10, 0, 5, 11,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 5};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_center() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 3, 3, 0, 0, 4, 4, 0, 0, 0, 0,
                0, 0, 0, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4, 4, 4, 4, 0, 0, 0,
                0, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 4, 4, 4, 4, 0, 0, 0, 0, 0,
                2, 2, 0, 0, 0, 3, 0, 0, 3, 3, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0,
                2, 2, 0, 0, 6, 6, 0, 0, 4, 4, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0,
                0, 0, 3, 6, 6, 6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0,
                0, 6, 6, 6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0,
                6, 6, 6, 6, 4, 7, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0,
                6, 6, 4, 4, 7, 7, 7, 7, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0,
                4, 4, 0, 0, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                4, 7, 0, 0, 10, 10, 0, 0, 8, 0, 11, 11, 0, 0, 12, 12, 0, 0, 0, 0,
                7, 7, 7, 10, 10, 10, 10, 0, 0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0,
                7, 10, 10, 10, 10, 8, 0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0, 0, 0,
                10, 10, 0, 0, 0, 11, 0, 0, 11, 11, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0,
                10, 10, 0, 0, 11, 11, 0, 0, 12, 12, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0,
                8, 0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 11, 11, 11, 11, 12, 12, 12, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_center() throws Exception {
        int[] expectedCloudShadowIdArray = {
                15, 6, 6, 6, 11, 16, 12, 7, 7, 7, 0, 8, 8, 8, 0, 0, 0, 0, 0, 0,
                15, 15, 6, 6, 11, 11, 16, 12, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0, 0, 0,
                10, 15, 0, 0, 6, 11, 0, 0, 7, 7, 7, 0, 0, 0, 8, 0, 0, 0, 0, 0,
                10, 0, 0, 0, 6, 6, 0, 0, 0, 7, 7, 7, 0, 0, 8, 8, 0, 0, 0, 0,
                10, 10, 15, 15, 6, 6, 6, 11, 16, 12, 7, 7, 7, 0, 8, 8, 8, 0, 0, 0,
                10, 10, 10, 15, 15, 6, 6, 11, 11, 16, 12, 7, 7, 0, 0, 8, 8, 0, 0, 0,
                14, 10, 0, 0, 15, 6, 0, 0, 11, 11, 16, 7, 0, 0, 0, 8, 0, 0, 0, 0,
                14, 0, 0, 0, 0, 15, 0, 0, 11, 11, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                14, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0, 0, 0,
                14, 14, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0, 0,
                0, 14, 14, 14, 10, 10, 10, 15, 15, 15, 11, 11, 11, 16, 12, 12, 12, 0, 0, 0,
                0, 0, 14, 14, 0, 10, 10, 0, 15, 15, 0, 11, 11, 16, 0, 12, 12, 0, 0, 0,
                0, 0, 0, 0, 14, 10, 0, 0, 15, 15, 15, 11, 0, 0, 16, 12, 0, 0, 0, 0,
                0, 0, 0, 0, 14, 14, 0, 0, 0, 15, 15, 15, 0, 0, 16, 16, 0, 0, 0, 0,
                0, 0, 0, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 16, 16, 16, 0, 0, 0,
                0, 0, 0, 0, 0, 14, 14, 0, 0, 0, 0, 15, 15, 0, 0, 16, 16, 0, 0, 0,
                0, 0, 0, 0, 0, 14, 0, 0, 0, 0, 0, 15, 0, 0, 0, 16, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_center() throws Exception {
        int[] expectedCloudShadowIdArray = {
                0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 0, 10, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 0, 10, 7, 7, 7,
                0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 6, 6, 0, 0, 0, 13, 0, 0, 7, 14,
                0, 0, 0, 0, 0, 5, 0, 0, 0, 6, 6, 6, 0, 0, 13, 7, 0, 0, 14, 14,
                0, 0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 9, 13, 7, 7, 7, 14, 14, 14,
                0, 0, 0, 5, 5, 5, 0, 6, 6, 6, 9, 9, 13, 7, 7, 7, 14, 14, 14, 11,
                0, 0, 0, 0, 5, 0, 0, 0, 6, 9, 9, 0, 0, 0, 7, 0, 0, 0, 11, 11,
                0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 0, 10, 0, 0, 0, 14, 0, 0, 11, 11,
                0, 0, 0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0,
                0, 0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 15,
                0, 0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 15, 15,
                0, 0, 0, 9, 9, 9, 0, 10, 10, 10, 0, 14, 14, 11, 11, 11, 0, 15, 15, 15,
                0, 0, 0, 0, 9, 0, 0, 0, 10, 0, 14, 14, 0, 0, 11, 0, 0, 0, 15, 0,
                0, 0, 0, 0, 0, 13, 0, 0, 0, 14, 14, 14, 0, 0, 0, 15, 0, 0, 0, 0,
                0, 0, 0, 0, 13, 13, 13, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 0, 0,
                0, 0, 0, 13, 13, 13, 0, 14, 14, 14, 0, 0, 0, 15, 15, 15, 0, 0, 0, 0,
                0, 0, 0, 0, 13, 0, 0, 0, 14, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedCloudShadowIdArray);
    }

    private void testPotentialCloudShadowArea(float saa, Rectangle sourceRectangle, Rectangle targetRectangle,
                                              int[] expectedCloudShadowIdArray) throws IOException {
        S2IdepixCloudShadowOp.searchBorderRadius = 10;
        S2IdepixCloudShadowOp.spatialResolution = 60;
        final float[] sunZenith = createSmoothGrid(19.7446f, 19.6652f, 19.6997f, 19.6202f, 20, 20);
        final float[] sunAzimuth = createSmoothGrid(saa, saa, saa, saa, 20, 20);
        final float[] latitude = createSmoothGrid(31.630919f, 31.630814f, 31.620094f, 31.619989f, 20, 20);
        final float[] longitude = createSmoothGrid(-7.945212f, -7.932560f, -7.945334f, -7.932683f, 20, 20);
        final float[] elevation = createSmoothGrid(800, 850, 900, 950, 20, 20);
        int[] flagArray = new int[20 * 20];
        int[] cloudIdArea = new int[20 * 20];
        int[] coordinateStartPoints = new int[]{2, 6, 12, 16};
        int count = 1;
        for (int coordinateStartPoint0 : coordinateStartPoints) {
            for (int coordinateStartPoint1 : coordinateStartPoints) {
                for (int y = coordinateStartPoint0; y < coordinateStartPoint0 + 2; y++) {
                    for (int x = coordinateStartPoint1; x < coordinateStartPoint1 + 2; x++) {
                        int index = y * 20 + x;
                        flagArray[index] = PreparationMaskBand.CLOUD_FLAG;
                        cloudIdArea[index] = count;
                    }
                }
                count++;
            }
        }
        int[] cloudShadowIdArray = new int[20 * 20];
        PotentialCloudShadowAreaIdentifier.identifyPotentialCloudShadows(20, 20, sourceRectangle, targetRectangle,
                                                                         sunZenith, sunAzimuth, latitude, longitude, elevation,
                                                                         flagArray, cloudIdArea, cloudShadowIdArray, count);
        assertArrayEquals(expectedCloudShadowIdArray, cloudShadowIdArray);
    }

    private float[] createSmoothGrid(float topLeft, float topRight, float bottomLeft, float bottomRight, int width, int height) {
        float[] grid = new float[width * height];
        for (int y = 0; y < height; y++) {
            float verticalWeight = (float) y / (float) (height - 1);
            for (int x = 0; x < width; x++) {
                float horizontalWeight = (float) x / (float) (width - 1);
                grid[x + (width * y)] =
                        topLeft * (1 - verticalWeight) * (1 - horizontalWeight) +
                                topRight * (1 - verticalWeight) * horizontalWeight +
                                bottomLeft * verticalWeight * (1 - horizontalWeight) +
                                bottomRight * verticalWeight * horizontalWeight;
            }
        }
        return grid;
    }

}