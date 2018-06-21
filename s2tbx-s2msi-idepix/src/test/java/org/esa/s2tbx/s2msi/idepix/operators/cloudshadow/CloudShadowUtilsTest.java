package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.math.MathUtils;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CloudShadowUtilsTest {

    @Test
    public void testGetRelativePath_45() throws Exception {
        final List<Point> list = new LinkedList<>();
        final Point lastPoint = new Point();
        for (int i = 0; i < 10; i++) {
            lastPoint.x = -i;
            lastPoint.y = i;
            list.add(new Point(lastPoint));
        }
        final Point2D[] expectedPath = list.toArray(new Point[0]);
        testRelativePath(45, expectedPath);
    }

    @Test
    public void testGetRelativePath_90() throws Exception {
        final List<Point> list = new LinkedList<>();
        final Point lastPoint = new Point();
        for (int i = 0; i < 10; i++) {
            lastPoint.x = -i;
            lastPoint.y = 0;
            list.add(new Point(lastPoint));
        }
        final Point2D[] expectedPath = list.toArray(new Point[0]);
        testRelativePath(90, expectedPath);
    }


    private void testRelativePath(float saa, Point2D[] expectedPath) {
        S2IdepixPreCloudShadowOp.searchBorderRadius = 5;
        S2IdepixPreCloudShadowOp.spatialResolution = 60;
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        float minAltitude = 0;
        float sunZenithMean = 19.7446f;

        final Point2D[] cloudShadowRelativePath = CloudShadowUtils.getRelativePath(
                minAltitude, sunZenithMean * MathUtils.DTOR, saa * MathUtils.DTOR,
                S2IdepixPreCloudShadowOp.maxcloudTop, targetRectangle, targetRectangle, 20,
                20, S2IdepixPreCloudShadowOp.spatialResolution, true, false);
        assertEquals(expectedPath.length, cloudShadowRelativePath.length);
        for (int i = 0; i < expectedPath.length; i++) {
            assertEquals(expectedPath[i].getX(), cloudShadowRelativePath[i].getX());
            assertEquals(expectedPath[i].getY(), cloudShadowRelativePath[i].getY());
        }
    }
}
