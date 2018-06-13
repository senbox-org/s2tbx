package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class PotentialCloudShadowAreaIdentifierTest {

    @Test
    public void testGetRelativePath_saa_305_corner() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 10, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0, 1.0),
                new Point2D.Double(2.0, 1.0), new Point2D.Double(3.0, 2.0), new Point2D.Double(4.0, 3.0),
                new Point2D.Double(5.0, 3.0), new Point2D.Double(6.0, 4.0), new Point2D.Double(7.0, 5.0),
                new Point2D.Double(8.0, 5.0), new Point2D.Double(9.0, 6.0), new Point2D.Double(10.0, 7.0),
                new Point2D.Double(11.0, 8.0), new Point2D.Double(12.0, 8.0), new Point2D.Double(13.0, 9.0),
                new Point2D.Double(14.0, 10.0), new Point2D.Double(15.0, 10.0), new Point2D.Double(16.0, 11.0),
                new Point2D.Double(17.0, 12.0), new Point2D.Double(18.0, 12.0), new Point2D.Double(19.0, 13.0)};

        Point2D[] cloudPath = getCloudPath(305f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_corner() throws Exception {
        int[][] expectedPositions = {
                {64, 85, 106, 107, 128, 149, 150, 171, 192, 213, 214, 235, 278, 299, 83, 84, 105, 148, 169, 170, 191, 212, 233, 234, 255, 298, 319, 65, 86, 108, 129, 151, 172, 193, 215, 236, 258, 279},
                {68, 89, 110, 111, 154, 175, 196, 217, 218, 239, 87, 88, 109, 130, 131, 173, 174, 195, 216, 237, 238, 259, 69, 90, 112, 155, 176, 197, 219},
                {74, 95, 116, 117, 138, 159, 93, 94, 115, 158, 179, 75, 96, 118, 139},
                {78, 99, 97, 98, 119, 79},
                {144, 165, 186, 187, 208, 229, 230, 251, 293, 294, 315, 358, 379, 163, 164, 185, 206, 207, 228, 249, 250, 271, 292, 313, 314, 335, 378, 399, 145, 166, 188, 209, 231, 295, 316, 338, 359},
                {148, 169, 190, 191, 212, 233, 234, 255, 297, 298, 319, 167, 168, 189, 210, 211, 232, 254, 275, 296, 317, 318, 339, 149, 170, 192, 213, 235, 299},
                {154, 175, 196, 197, 218, 239, 173, 174, 195, 216, 217, 238, 259, 155, 176, 198, 219},
                {158, 179, 177, 178, 199, 159},
                {264, 285, 306, 307, 328, 349, 350, 371, 392, 283, 284, 305, 348, 369, 370, 391, 265, 286, 308, 329, 351, 372, 393},
                {268, 289, 310, 311, 354, 375, 396, 287, 288, 309, 330, 331, 373, 374, 395, 269, 290, 312, 355, 376, 397},
                {274, 295, 316, 317, 338, 359, 293, 294, 315, 358, 379, 275, 296, 318, 339},
                {278, 299, 297, 298, 319, 279},
                {344, 365, 386, 387, 363, 364, 385, 345, 366, 388},
                {348, 369, 390, 391, 367, 368, 389, 349, 370, 392},
                {354, 375, 396, 397, 373, 374, 395, 355, 376, 398},
                {358, 379, 377, 378, 399, 359}
        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 10, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_55_corner() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 10, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(-1.0, 1.0),
                new Point2D.Double(-2.0, 1.0), new Point2D.Double(-3.0, 2.0), new Point2D.Double(-4.0, 3.0),
                new Point2D.Double(-5.0, 3.0), new Point2D.Double(-6.0, 4.0), new Point2D.Double(-7.0, 5.0),
                new Point2D.Double(-8.0, 5.0), new Point2D.Double(-9.0, 6.0), new Point2D.Double(-10.0, 7.0),
                new Point2D.Double(-11.0, 8.0), new Point2D.Double(-12.0, 8.0), new Point2D.Double(-13.0, 9.0),
                new Point2D.Double(-14.0, 10.0), new Point2D.Double(-15.0, 10.0), new Point2D.Double(-16.0, 11.0),
                new Point2D.Double(-17.0, 12.0), new Point2D.Double(-18.0, 12.0), new Point2D.Double(-19.0, 13.0)};

        Point2D[] cloudPath = getCloudPath(55f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_corner() throws Exception {
        int[][] expectedPositions = {
                {61, 60, 81, 80, 82, 100},
                {65, 64, 83, 102, 101, 120, 85, 84, 103, 121, 140, 86, 104, 141},
                {71, 70, 89, 108, 107, 125, 144, 163, 162, 181, 200, 91, 90, 109, 128, 145, 164, 183, 182, 201, 220, 92, 110, 129, 165, 184, 202, 221},
                {75, 74, 93, 112, 111, 130, 129, 148, 167, 166, 185, 204, 203, 222, 221, 240, 95, 94, 113, 131, 150, 149, 168, 187, 186, 205, 224, 223, 241, 260, 96, 114, 151, 169, 188, 206, 225, 261, 280},
                {141, 140, 161, 160, 162, 180},
                {145, 144, 163, 182, 181, 200, 165, 164, 183, 202, 201, 220, 166, 184, 203, 221},
                {151, 150, 169, 188, 187, 206, 205, 224, 261, 280, 171, 170, 189, 208, 207, 226, 225, 244, 281, 300, 172, 190, 209, 227, 245, 264, 282, 301},
                {155, 154, 173, 192, 191, 210, 209, 228, 265, 284, 283, 302, 301, 320, 175, 174, 193, 212, 211, 230, 229, 248, 285, 304, 303, 321, 340, 176, 194, 213, 231, 249, 268, 286, 305, 341, 360},
                {261, 260, 281, 280, 282, 300},
                {265, 264, 283, 302, 301, 320, 285, 284, 303, 321, 340, 286, 304, 341},
                {271, 270, 289, 308, 307, 325, 344, 363, 362, 381, 291, 290, 309, 328, 345, 364, 383, 382, 292, 310, 329, 365, 384},
                {275, 274, 293, 312, 311, 330, 329, 348, 367, 366, 385, 295, 294, 313, 331, 350, 349, 368, 387, 386, 296, 314, 351, 369, 388},
                {341, 340, 361, 360, 362, 380},
                {345, 344, 363, 382, 381, 365, 364, 383, 366, 384},
                {351, 350, 369, 388, 387, 371, 370, 389, 372, 390},
                {355, 354, 373, 392, 391, 375, 374, 393, 376, 394},
        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 10, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_145_corner() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 0, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(-1.0, -1.0),
                new Point2D.Double(-1.0, -2.0), new Point2D.Double(-2.0, -3.0), new Point2D.Double(-3.0, -4.0),
                new Point2D.Double(-3.0, -5.0), new Point2D.Double(-4.0, -6.0), new Point2D.Double(-5.0, -7.0),
                new Point2D.Double(-5.0, -8.0), new Point2D.Double(-6.0, -9.0), new Point2D.Double(-7.0, -10.0),
                new Point2D.Double(-8.0, -11.0), new Point2D.Double(-8.0, -12.0), new Point2D.Double(-9.0, -13.0),
                new Point2D.Double(-10.0, -14.0), new Point2D.Double(-10.0, -15.0), new Point2D.Double(-11.0, -16.0),
                new Point2D.Double(-12.0, -17.0), new Point2D.Double(-12.0, -18.0), new Point2D.Double(-13.0, -19.0)};

        Point2D[] cloudPath = getCloudPath(145f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_corner() throws Exception {
        int[][] expectedPositions = {
                {21, 1, 41, 0, 22, 2},
                {25, 5, 45, 4, 26, 6},
                {31, 11, 51, 10, 32, 12},
                {35, 15, 55, 14, 36, 16},
                {101, 81, 60, 121, 80, 102, 82, 61, 40},
                {105, 85, 64, 22, 2, 125, 84, 1, 106, 86, 65, 44, 23, 3},
                {111, 91, 70, 49, 28, 8, 131, 90, 69, 48, 7, 112, 92, 71, 50, 29, 9},
                {115, 95, 74, 32, 12, 135, 94, 11, 116, 96, 75, 54, 33, 13},
                {221, 201, 180, 241, 200, 222, 202, 181, 160},
                {225, 205, 184, 163, 101, 80, 245, 204, 183, 162, 121, 100, 226, 206, 185, 164, 102, 81, 60, 40},
                {231, 211, 190, 169, 148, 128, 107, 86, 65, 45, 24, 3, 251, 210, 189, 168, 106, 85, 44, 23, 2, 232, 212, 191, 170, 149, 129, 108, 87, 25, 4},
                {235, 215, 194, 173, 111, 90, 69, 49, 28, 7, 255, 214, 193, 172, 131, 110, 89, 48, 27, 6, 236, 216, 195, 174, 112, 91, 70, 50, 29, 8},
                {301, 281, 260, 321, 280, 302, 282, 261, 240},
                {305, 285, 264, 222, 202, 181, 160, 325, 284, 201, 180, 306, 286, 265, 244, 223, 203, 182, 161, 140, 120},
                {311, 291, 270, 249, 228, 208, 187, 166, 145, 125, 104, 83, 21, 0, 331, 290, 269, 248, 207, 186, 165, 124, 103, 82, 41, 20, 312, 292, 271, 250, 229, 209, 188, 167, 105, 84, 22, 1},
                {315, 295, 274, 232, 212, 191, 170, 149, 129, 108, 87, 25, 4, 335, 294, 211, 190, 169, 128, 107, 86, 45, 24, 3, 316, 296, 275, 254, 233, 213, 192, 171, 150, 130, 109, 88, 26, 5}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 0, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_225_corner() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 0, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0, -1.0),
                new Point2D.Double(2.0, -2.0), new Point2D.Double(3.0, -3.0), new Point2D.Double(4.0, -4.0),
                new Point2D.Double(5.0, -5.0), new Point2D.Double(6.0, -6.0), new Point2D.Double(7.0, -7.0),
                new Point2D.Double(8.0, -8.0), new Point2D.Double(9.0, -9.0), new Point2D.Double(10.0, -10.0),
                new Point2D.Double(11.0, -11.0), new Point2D.Double(12.0, -12.0), new Point2D.Double(13.0, -13.0),
                new Point2D.Double(14.0, -14.0), new Point2D.Double(15.0, -15.0), new Point2D.Double(16.0, -16.0),
                new Point2D.Double(17.0, -17.0), new Point2D.Double(18.0, -18.0), new Point2D.Double(19.0, -19.0)};

        Point2D[] cloudPath = getCloudPath(225f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_corner() throws Exception {
        int[][] expectedPositions = {
                {23, 4, 24, 5, 44, 25, 6},
                {27, 8, 28, 9, 48, 29, 10},
                {33, 14, 34, 15, 54, 35, 16},
                {37, 18, 38, 19, 58, 39},
                {103, 84, 65, 27, 8, 104, 85, 28, 9, 124, 105, 86, 48, 29, 10},
                {107, 88, 69, 50, 31, 12, 108, 89, 70, 51, 32, 13, 128, 109, 90, 71, 33, 14},
                {113, 94, 75, 37, 18, 114, 95, 38, 19, 134, 115, 96, 58, 39},
                {117, 98, 79, 118, 99, 138, 119},
                {223, 204, 185, 166, 128, 109, 90, 71, 32, 13, 224, 205, 186, 167, 148, 129, 110, 91, 33, 14, 244, 225, 206, 187, 168, 149, 130, 111, 92, 34, 15},
                {227, 208, 189, 170, 151, 113, 94, 75, 36, 17, 228, 209, 190, 171, 114, 95, 37, 18, 248, 229, 210, 191, 172, 134, 115, 96, 38, 19},
                {233, 214, 195, 176, 138, 119, 234, 215, 196, 177, 158, 139, 254, 235, 216, 197, 178, 159},
                {237, 218, 199, 238, 219, 258, 239},
                {303, 284, 265, 227, 208, 189, 170, 151, 112, 93, 74, 55, 36, 17, 304, 285, 228, 209, 190, 171, 113, 94, 75, 37, 18, 324, 305, 286, 248, 229, 210, 191, 172, 114, 95, 38, 19},
                {307, 288, 269, 250, 231, 212, 193, 174, 155, 116, 97, 78, 59, 308, 289, 270, 251, 232, 213, 194, 175, 117, 98, 79, 328, 309, 290, 271, 233, 214, 195, 176, 118, 99},
                {313, 294, 275, 237, 218, 199, 314, 295, 238, 219, 334, 315, 296, 258, 239},
                {317, 298, 279, 318, 299, 338, 319}
        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 0, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_305_center() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0, 1.0),
                new Point2D.Double(2.0, 1.0), new Point2D.Double(3.0, 2.0), new Point2D.Double(4.0, 3.0),
                new Point2D.Double(5.0, 3.0), new Point2D.Double(6.0, 4.0), new Point2D.Double(7.0, 5.0),
                new Point2D.Double(8.0, 5.0), new Point2D.Double(9.0, 6.0), new Point2D.Double(10.0, 7.0),
                new Point2D.Double(11.0, 8.0), new Point2D.Double(12.0, 8.0), new Point2D.Double(13.0, 9.0),
                new Point2D.Double(14.0, 10.0), new Point2D.Double(15.0, 10.0), new Point2D.Double(16.0, 11.0),
                new Point2D.Double(17.0, 12.0), new Point2D.Double(18.0, 12.0), new Point2D.Double(19.0, 13.0)};

        Point2D[] cloudPath = getCloudPath(305f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_center() throws Exception {
        int[][] expectedPositions = {
                {64, 85, 106, 107, 128, 149, 150, 171, 192, 213, 214, 235, 278, 299, 83, 84, 105, 148, 169, 170, 191, 212, 233, 234, 255, 298, 319, 65, 86, 108, 129, 151, 172, 193, 215, 236, 258, 279},
                {68, 89, 110, 111, 154, 175, 196, 217, 218, 239, 87, 88, 109, 130, 131, 173, 174, 195, 216, 237, 238, 259, 69, 90, 112, 155, 176, 197, 219},
                {74, 95, 116, 117, 138, 159, 93, 94, 115, 158, 179, 75, 96, 118, 139},
                {78, 99, 97, 98, 119, 79},
                {144, 165, 186, 187, 208, 229, 230, 251, 293, 294, 315, 358, 379, 163, 164, 185, 206, 207, 228, 249, 250, 271, 292, 313, 314, 335, 378, 399, 145, 166, 188, 209, 231, 295, 316, 338, 359},
                {148, 169, 190, 191, 212, 233, 234, 255, 297, 298, 319, 167, 168, 189, 210, 211, 232, 254, 275, 296, 317, 318, 339, 149, 170, 192, 213, 235, 299},
                {154, 175, 196, 197, 218, 239, 173, 174, 195, 216, 217, 238, 259, 155, 176, 198, 219},
                {158, 179, 177, 178, 199, 159},
                {264, 285, 306, 307, 328, 349, 350, 371, 392, 283, 284, 305, 348, 369, 370, 391, 265, 286, 308, 329, 351, 372, 393},
                {268, 289, 310, 311, 354, 375, 396, 287, 288, 309, 330, 331, 373, 374, 395, 269, 290, 312, 355, 376, 397},
                {274, 295, 316, 317, 338, 359, 293, 294, 315, 358, 379, 275, 296, 318, 339},
                {278, 299, 297, 298, 319, 279},
                {344, 365, 386, 387, 363, 364, 385, 345, 366, 388},
                {348, 369, 390, 391, 367, 368, 389, 349, 370, 392},
                {354, 375, 396, 397, 373, 374, 395, 355, 376, 398},
                {358, 379, 377, 378, 399, 359}

        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_55_center() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(-1.0, 1.0),
                new Point2D.Double(-2.0, 1.0), new Point2D.Double(-3.0, 2.0), new Point2D.Double(-4.0, 3.0),
                new Point2D.Double(-5.0, 3.0), new Point2D.Double(-6.0, 4.0), new Point2D.Double(-7.0, 5.0),
                new Point2D.Double(-8.0, 5.0), new Point2D.Double(-9.0, 6.0), new Point2D.Double(-10.0, 7.0),
                new Point2D.Double(-11.0, 8.0), new Point2D.Double(-12.0, 8.0), new Point2D.Double(-13.0, 9.0),
                new Point2D.Double(-14.0, 10.0), new Point2D.Double(-15.0, 10.0), new Point2D.Double(-16.0, 11.0),
                new Point2D.Double(-17.0, 12.0), new Point2D.Double(-18.0, 12.0), new Point2D.Double(-19.0, 13.0)};

        Point2D[] cloudPath = getCloudPath(55f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_center() throws Exception {
        int[][] expectedPositions = {
                {355, 354, 373, 392, 391, 375, 374, 393, 376, 394},
                {65, 64, 83, 102, 101, 120, 85, 84, 103, 121, 140, 86, 104, 141},
                {71, 70, 89, 108, 107, 125, 144, 163, 162, 181, 200, 91, 90, 109, 128, 145, 164, 183, 182, 201, 220, 92, 110, 129, 165, 184, 202, 221},
                {75, 74, 93, 112, 111, 130, 129, 148, 167, 166, 185, 204, 203, 222, 221, 240, 95, 94, 113, 131, 150, 149, 168, 187, 186, 205, 224, 223, 241, 260, 96, 114, 151, 169, 188, 206, 225, 261, 280},
                {145, 144, 163, 182, 181, 200, 165, 164, 183, 202, 201, 220, 166, 184, 203, 221},
                {151, 150, 169, 188, 187, 206, 205, 224, 261, 280, 171, 170, 189, 208, 207, 226, 225, 244, 281, 300, 172, 190, 209, 227, 245, 264, 282, 301},
                {155, 154, 173, 192, 191, 210, 209, 228, 265, 284, 283, 302, 301, 320, 175, 174, 193, 212, 211, 230, 229, 248, 285, 304, 303, 321, 340, 176, 194, 213, 231, 249, 268, 286, 305, 341, 360},
                {265, 264, 283, 302, 301, 320, 285, 284, 303, 321, 340, 286, 304, 341},
                {271, 270, 289, 308, 307, 325, 344, 363, 362, 381, 291, 290, 309, 328, 345, 364, 383, 382, 292, 310, 329, 365, 384},
                {275, 274, 293, 312, 311, 330, 329, 348, 367, 366, 385, 295, 294, 313, 331, 350, 349, 368, 387, 386, 296, 314, 351, 369, 388},
                {345, 344, 363, 382, 381, 365, 364, 383, 366, 384},
                {351, 350, 369, 388, 387, 371, 370, 389, 372, 390}
        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_145_center() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(-1.0, -1.0),
                new Point2D.Double(-1.0, -2.0), new Point2D.Double(-2.0, -3.0), new Point2D.Double(-3.0, -4.0),
                new Point2D.Double(-3.0, -5.0), new Point2D.Double(-4.0, -6.0), new Point2D.Double(-5.0, -7.0),
                new Point2D.Double(-5.0, -8.0), new Point2D.Double(-6.0, -9.0), new Point2D.Double(-7.0, -10.0),
                new Point2D.Double(-8.0, -11.0), new Point2D.Double(-8.0, -12.0), new Point2D.Double(-9.0, -13.0),
                new Point2D.Double(-10.0, -14.0), new Point2D.Double(-10.0, -15.0), new Point2D.Double(-11.0, -16.0),
                new Point2D.Double(-12.0, -17.0), new Point2D.Double(-12.0, -18.0), new Point2D.Double(-13.0, -19.0)};

        Point2D[] cloudPath = getCloudPath(145f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_center() throws Exception {
        int[][] expectedPositions = {
                {315, 295, 274, 232, 212, 191, 170, 149, 129, 108, 87, 25, 4, 335, 294, 211, 190, 169, 128, 107, 86, 45, 24, 3, 316, 296, 275, 254, 233, 213, 192, 171, 150, 130, 109, 88, 26, 5},
                {105, 85, 64, 22, 2, 125, 84, 1, 106, 86, 65, 44, 23, 3},
                {111, 91, 70, 49, 28, 8, 131, 90, 69, 48, 7, 112, 92, 71, 50, 29, 9},
                {115, 95, 74, 32, 12, 135, 94, 11, 116, 96, 75, 54, 33, 13},
                {225, 205, 184, 163, 101, 80, 245, 204, 183, 162, 121, 100, 226, 206, 185, 164, 102, 81, 60, 40},
                {231, 211, 190, 169, 148, 128, 107, 86, 65, 45, 24, 3, 251, 210, 189, 168, 106, 85, 44, 23, 2, 232, 212, 191, 170, 149, 129, 108, 87, 25, 4},
                {235, 215, 194, 173, 111, 90, 69, 49, 28, 7, 255, 214, 193, 172, 131, 110, 89, 48, 27, 6, 236, 216, 195, 174, 112, 91, 70, 50, 29, 8},
                {305, 285, 264, 222, 202, 181, 160, 325, 284, 201, 180, 306, 286, 265, 244, 223, 203, 182, 161, 140, 120},
                {311, 291, 270, 249, 228, 208, 187, 166, 145, 125, 104, 83, 21, 0, 331, 290, 269, 248, 207, 186, 165, 124, 103, 82, 41, 20, 312, 292, 271, 250, 229, 209, 188, 167, 105, 84, 22, 1}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testGetRelativePath_saa_225_center() {
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        Point2D[] expectedCloudPath = new Point2D[]{new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0, -1.0),
                new Point2D.Double(2.0, -2.0), new Point2D.Double(3.0, -3.0), new Point2D.Double(4.0, -4.0),
                new Point2D.Double(5.0, -5.0), new Point2D.Double(6.0, -6.0), new Point2D.Double(7.0, -7.0),
                new Point2D.Double(8.0, -8.0), new Point2D.Double(9.0, -9.0), new Point2D.Double(10.0, -10.0),
                new Point2D.Double(11.0, -11.0), new Point2D.Double(12.0, -12.0), new Point2D.Double(13.0, -13.0),
                new Point2D.Double(14.0, -14.0), new Point2D.Double(15.0, -15.0), new Point2D.Double(16.0, -16.0),
                new Point2D.Double(17.0, -17.0), new Point2D.Double(18.0, -18.0), new Point2D.Double(19.0, -19.0)};

        Point2D[] cloudPath = getCloudPath(225f, sourceRectangle, targetRectangle);

        assertEquals(expectedCloudPath.length, cloudPath.length);
        for (int i = 0; i < cloudPath.length; i++) {
            assertEquals(expectedCloudPath[i].getX(), cloudPath[i].getX());
            assertEquals(expectedCloudPath[i].getY(), cloudPath[i].getY());
        }
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_center() throws Exception {
        int[][] expectedPositions = {
                {317, 298, 279, 318, 299, 338, 319},
                {103, 84, 65, 27, 8, 104, 85, 28, 9, 124, 105, 86, 48, 29, 10},
                {107, 88, 69, 50, 31, 12, 108, 89, 70, 51, 32, 13, 128, 109, 90, 71, 33, 14},
                {113, 94, 75, 37, 18, 114, 95, 38, 19, 134, 115, 96, 58, 39},
                {117, 98, 79, 118, 99, 138, 119},
                {223, 204, 185, 166, 128, 109, 90, 71, 32, 13, 224, 205, 186, 167, 148, 129, 110, 91, 33, 14, 244, 225, 206, 187, 168, 149, 130, 111, 92, 34, 15},
                {227, 208, 189, 170, 151, 113, 94, 75, 36, 17, 228, 209, 190, 171, 114, 95, 37, 18, 248, 229, 210, 191, 172, 134, 115, 96, 38, 19},
                {233, 214, 195, 176, 138, 119, 234, 215, 196, 177, 158, 139, 254, 235, 216, 197, 178, 159},
                {237, 218, 199, 238, 219, 258, 239},
                {303, 284, 265, 227, 208, 189, 170, 151, 112, 93, 74, 55, 36, 17, 304, 285, 228, 209, 190, 171, 113, 94, 75, 37, 18, 324, 305, 286, 248, 229, 210, 191, 172, 114, 95, 38, 19},
                {307, 288, 269, 250, 231, 212, 193, 174, 155, 116, 97, 78, 59, 308, 289, 270, 251, 232, 213, 194, 175, 117, 98, 79, 328, 309, 290, 271, 233, 214, 195, 176, 118, 99},
                {313, 294, 275, 237, 218, 199, 314, 295, 238, 219, 334, 315, 296, 258, 239}
        };
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedPositions);
    }

    private Point2D[] getCloudPath(float saa, Rectangle sourceRectangle, Rectangle targetRectangle) {
        S2IdepixPreCloudShadowOp.searchBorderRadius = 10;
        S2IdepixPreCloudShadowOp.spatialResolution = 60;
        final float[] sunZenith = createSmoothGrid(19.7446f, 19.6652f, 19.6997f, 19.6202f, 20, 20);
        final float[] sunAzimuth = createSmoothGrid(saa, saa, saa, saa, 20, 20);
        final float[] elevation = createSmoothGrid(800, 850, 900, 950, 20, 20);
        int center_index = 9 * 20 + 9;
        float sunZenithMean = sunZenith[center_index];
        float sunAzimuthMean = sunAzimuth[center_index];
        final List<Float> altitudes = Arrays.asList(ArrayUtils.toObject(elevation));
        float minAltitude = Collections.min(altitudes);
        return CloudShadowUtils.getRelativePath(minAltitude, sunZenithMean * MathUtils.DTOR, sunAzimuthMean * MathUtils.DTOR,
                S2IdepixPreCloudShadowOp.maxcloudTop, sourceRectangle, targetRectangle, 20, 20,
                S2IdepixPreCloudShadowOp.spatialResolution, true, false);
    }

    private void testPotentialCloudShadowArea(float saa, Rectangle sourceRectangle, Rectangle targetRectangle,
                                              int[][] expectedPotentialShadowPositions) {
        S2IdepixPreCloudShadowOp.searchBorderRadius = 10;
        S2IdepixPreCloudShadowOp.spatialResolution = 60;
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
        int center_index = 9 * 20 + 9;
        float sunZenithMean = sunZenith[center_index];
        float sunAzimuthMean = sunAzimuth[center_index];
        Point2D[] cloudPath = getCloudPath(saa, sourceRectangle, targetRectangle);

        final Map[] potentialShadowPositionsMap =
                PotentialCloudShadowAreaIdentifier.identifyPotentialCloudShadowsPLUS(sourceRectangle, targetRectangle,
                        sunZenithMean, sunAzimuthMean, latitude, longitude, elevation, flagArray, cloudIdArea, cloudPath);

        final Map<Integer, List<Integer>> potentialShadowPositions = potentialShadowPositionsMap[0];

        assertEquals(expectedPotentialShadowPositions.length, potentialShadowPositions.size());


        int i = 0;
        for (int key : potentialShadowPositions.keySet()) {
            List<Integer> positions = potentialShadowPositions.get(key);
            assertEquals(expectedPotentialShadowPositions[i].length, positions.size());
            for (int j = 0; j < positions.size(); j++) {
                assertEquals(expectedPotentialShadowPositions[i][j], (int) positions.get(j));
                cloudIdArea[positions.get(j)] = -1;
            }
            i++;
        }
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