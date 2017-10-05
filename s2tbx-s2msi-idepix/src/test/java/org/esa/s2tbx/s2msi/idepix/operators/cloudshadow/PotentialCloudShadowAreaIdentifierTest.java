package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.junit.Test;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class PotentialCloudShadowAreaIdentifierTest {

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_corner() throws Exception {
        int[][] expectedPositions = {
                {64, 85, 106, 107, 128, 149, 150, 171, 192, 213, 214, 235, 278, 299, 65, 86, 108, 129, 151, 172, 193,
                        215, 236, 258, 279, 83, 84, 105, 148, 169, 170, 191, 212, 233, 234, 255, 298, 319},
                {68, 89, 110, 111, 154, 175, 196, 217, 218, 239, 69, 90, 112, 155, 176, 197, 219, 87, 88, 109, 130, 131,
                        173, 174, 195, 216, 237, 238, 259},
                {74, 95, 116, 117, 138, 159, 75, 96, 118, 139, 93, 94, 115, 158, 179},
                {78, 99, 79, 97, 98, 119},
                {144, 165, 186, 187, 208, 229, 230, 251, 293, 294, 315, 358, 379, 145, 166, 188, 209, 231, 295, 316, 338,
                        359, 163, 164, 185, 206, 207, 228, 249, 250, 271, 292, 313, 314, 335, 378, 399},
                {148, 169, 190, 191, 212, 233, 234, 255, 297, 298, 319, 149, 170, 192, 213, 235, 299, 167, 168, 189, 210,
                        211, 232, 254, 275, 296, 317, 318, 339},
                {154, 175, 196, 197, 218, 239, 155, 176, 198, 219, 173, 174, 195, 216, 217, 238, 259},
                {158, 179, 159, 177, 178, 199},
                {264, 285, 306, 307, 328, 349, 350, 371, 392, 265, 286, 308, 329, 351, 372, 393, 283, 284, 305, 348,
                        369, 370, 391},
                {268, 289, 310, 311, 354, 375, 396, 269, 290, 312, 355, 376, 397, 287, 288, 309, 330, 331, 373, 374, 395},
                {274, 295, 316, 317, 338, 359, 275, 296, 318, 339, 293, 294, 315, 358, 379},
                {278, 299, 279, 297, 298, 319},
                {344, 365, 386, 387, 345, 366, 388, 363, 364, 385},
                {348, 369, 390, 391, 349, 370, 392, 367, 368, 389},
                {354, 375, 396, 397, 355, 376, 398, 373, 374, 395},
                {358, 379, 359, 377, 378, 399}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 10, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_corner() throws Exception {
        int[][] expectedPositions = {
                {61, 80, 60, 82, 81, 100},
                {65, 84, 103, 102, 121, 120, 64, 83, 101, 86, 85, 104, 141, 140},
                {71, 90, 109, 108, 145, 164, 163, 182, 201, 200, 70, 89, 107, 125, 144, 162, 181, 92, 91, 110, 129, 128,
                        165, 184, 183, 202, 221, 220},
                {75, 94, 113, 112, 131, 130, 149, 168, 167, 186, 205, 204, 223, 222, 241, 260, 74, 93, 111, 129, 148,
                        166, 185, 203, 221, 240, 96, 95, 114, 151, 150, 169, 188, 187, 206, 225, 224, 261, 280},
                {141, 160, 140, 162, 161, 180},
                {145, 164, 183, 182, 201, 200, 144, 163, 181, 166, 165, 184, 203, 202, 221, 220},
                {151, 170, 189, 188, 207, 206, 225, 244, 281, 280, 150, 169, 187, 205, 224, 261, 172, 171, 190, 209, 208,
                        227, 226, 245, 264, 282, 301, 300},
                {155, 174, 193, 192, 211, 210, 229, 248, 285, 284, 303, 302, 321, 340, 154, 173, 191, 209, 228, 265, 283,
                        301, 320, 176, 175, 194, 213, 212, 231, 230, 249, 268, 286, 305, 304, 341, 360},
                {261, 280, 260, 282, 281, 300},
                {265, 284, 303, 302, 321, 320, 264, 283, 301, 286, 285, 304, 341, 340},
                {271, 290, 309, 308, 345, 364, 363, 382, 270, 289, 307, 325, 344, 362, 381, 292, 291, 310, 329, 328, 365,
                        384, 383},
                {275, 294, 313, 312, 331, 330, 349, 368, 367, 386, 274, 293, 311, 329, 348, 366, 385, 296, 295, 314, 351,
                        350, 369, 388, 387},
                {341, 360, 340, 362, 361, 380},
                {345, 364, 383, 382, 344, 363, 381, 366, 365, 384},
                {351, 370, 389, 388, 350, 369, 387, 372, 371, 390},
                {355, 374, 393, 392, 354, 373, 391, 376, 375, 394}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 10, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_corner() throws Exception {
        int[][] expectedPositions = {
                {22, 1, 41, 21, 0, 2},
                {26, 5, 45, 25, 4, 6},
                {32, 11, 51, 31, 10, 12},
                {36, 15, 55, 35, 14, 16},
                {102, 81, 60, 121, 101, 80, 82, 61, 40},
                {106, 85, 64, 23, 2, 125, 105, 84, 22, 1, 86, 65, 44, 3},
                {112, 91, 70, 49, 29, 8, 131, 111, 90, 69, 48, 28, 7, 92, 71, 50, 9},
                {116, 95, 74, 33, 12, 135, 115, 94, 32, 11, 96, 75, 54, 13},
                {222, 201, 180, 241, 221, 200, 202, 181, 160},
                {226, 205, 184, 163, 101, 80, 60, 245, 225, 204, 183, 162, 121, 100, 206, 185, 164, 102, 81, 40},
                {232, 211, 190, 169, 149, 128, 107, 86, 45, 24, 3, 251, 231, 210, 189, 168, 148, 106, 85, 65, 44, 23, 2,
                        212, 191, 170, 129, 108, 87, 25, 4},
                {236, 215, 194, 173, 111, 90, 70, 49, 28, 7, 255, 235, 214, 193, 172, 131, 110, 89, 69, 48, 27, 6, 216,
                        195, 174, 112, 91, 50, 29, 8},
                {302, 281, 260, 321, 301, 280, 282, 261, 240},
                {306, 285, 264, 223, 202, 181, 160, 140, 325, 305, 284, 222, 201, 180, 286, 265, 244, 203, 182, 161, 120},
                {312, 291, 270, 249, 229, 208, 187, 166, 125, 104, 83, 21, 0, 331, 311, 290, 269, 248, 228, 207, 186,
                        165, 145, 124, 103, 82, 41, 20, 292, 271, 250, 209, 188, 167, 105, 84, 22, 1},
                {316, 295, 274, 233, 212, 191, 170, 150, 129, 108, 87, 25, 4, 335, 315, 294, 232, 211, 190, 169, 149,
                        128, 107, 86, 45, 24, 3, 296, 275, 254, 213, 192, 171, 130, 109, 88, 26, 5}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(0, 0, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_corner() throws Exception {
        int[][] expectedPositions = {
                {24, 5, 44, 25, 6, 23, 4},
                {28, 9, 48, 29, 10, 27, 8},
                {34, 15, 54, 35, 16, 33, 14},
                {38, 19, 58, 39, 37, 18},
                {104, 85, 28, 9, 124, 105, 86, 48, 29, 10, 103, 84, 65, 27, 8},
                {108, 89, 70, 51, 32, 13, 128, 109, 90, 71, 33, 14, 107, 88, 69, 50, 31, 12},
                {114, 95, 38, 19, 134, 115, 96, 58, 39, 113, 94, 75, 37, 18},
                {118, 99, 138, 119, 117, 98, 79},
                {224, 205, 186, 167, 148, 129, 110, 91, 33, 14, 244, 225, 206, 187, 168, 149, 130, 111, 92, 34, 15, 223,
                        204, 185, 166, 128, 109, 90, 71, 32, 13},
                {228, 209, 190, 171, 114, 95, 37, 18, 248, 229, 210, 191, 172, 134, 115, 96, 38, 19, 227, 208, 189, 170,
                        151, 113, 94, 75, 36, 17},
                {234, 215, 196, 177, 158, 139, 254, 235, 216, 197, 178, 159, 233, 214, 195, 176, 138, 119},
                {238, 219, 258, 239, 237, 218, 199},
                {304, 285, 228, 209, 190, 171, 113, 94, 75, 37, 18, 324, 305, 286, 248, 229, 210, 191, 172, 114, 95, 38,
                        19, 303, 284, 265, 227, 208, 189, 170, 151, 112, 93, 74, 55, 36, 17},
                {308, 289, 270, 251, 232, 213, 194, 175, 117, 98, 79, 328, 309, 290, 271, 233, 214, 195, 176, 118, 99,
                        307, 288, 269, 250, 231, 212, 193, 174, 155, 116, 97, 78, 59},
                {314, 295, 238, 219, 334, 315, 296, 258, 239, 313, 294, 275, 237, 218, 199},
                {318, 299, 338, 319, 317, 298, 279}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(10, 0, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_305_center() throws Exception {
        int[][] expectedPositions = {
                {64, 85, 106, 107, 128, 149, 150, 171, 192, 213, 214, 235, 278, 299, 65, 86, 108, 129, 151, 172, 193,
                        215, 236, 258, 279, 83, 84, 105, 148, 169, 170, 191, 212, 233, 234, 255, 298, 319},
                {68, 89, 110, 111, 154, 175, 196, 217, 218, 239, 69, 90, 112, 155, 176, 197, 219, 87, 88, 109, 130, 131,
                        173, 174, 195, 216, 237, 238, 259},
                {74, 95, 116, 117, 138, 159, 75, 96, 118, 139, 93, 94, 115, 158, 179},
                {144, 165, 186, 187, 208, 229, 230, 251, 293, 294, 315, 358, 379, 145, 166, 188, 209, 231, 295, 316,
                        338, 359, 163, 164, 185, 206, 207, 228, 249, 250, 271, 292, 313, 314, 335, 378, 399},
                {148, 169, 190, 191, 212, 233, 234, 255, 297, 298, 319, 149, 170, 192, 213, 235, 299, 167, 168, 189,
                        210, 211, 232, 254, 275, 296, 317, 318, 339},
                {154, 175, 196, 197, 218, 239, 155, 176, 198, 219, 173, 174, 195, 216, 217, 238, 259},
                {264, 285, 306, 307, 328, 349, 350, 371, 392, 265, 286, 308, 329, 351, 372, 393, 283, 284, 305, 348,
                        369, 370, 391},
                {268, 289, 310, 311, 354, 375, 396, 269, 290, 312, 355, 376, 397, 287, 288, 309, 330, 331, 373, 374, 395},
                {274, 295, 316, 317, 338, 359, 275, 296, 318, 339, 293, 294, 315, 358, 379}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(305f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_55_center() throws Exception {
        int[][] expectedPositions = {
                {65, 84, 103, 102, 121, 120, 64, 83, 101, 86, 85, 104, 141, 140},
                {71, 90, 109, 108, 145, 164, 163, 182, 201, 200, 70, 89, 107, 125, 144, 162, 181, 92, 91, 110, 129, 128,
                        165, 184, 183, 202, 221, 220},
                {75, 94, 113, 112, 131, 130, 149, 168, 167, 186, 205, 204, 223, 222, 241, 260, 74, 93, 111, 129, 148,
                        166, 185, 203, 221, 240, 96, 95, 114, 151, 150, 169, 188, 187, 206, 225, 224, 261, 280},
                {145, 164, 183, 182, 201, 200, 144, 163, 181, 166, 165, 184, 203, 202, 221, 220},
                {151, 170, 189, 188, 207, 206, 225, 244, 281, 280, 150, 169, 187, 205, 224, 261, 172, 171, 190, 209,
                        208, 227, 226, 245, 264, 282, 301, 300},
                {155, 174, 193, 192, 211, 210, 229, 248, 285, 284, 303, 302, 321, 340, 154, 173, 191, 209, 228, 265,
                        283, 301, 320, 176, 175, 194, 213, 212, 231, 230, 249, 268, 286, 305, 304, 341, 360},
                {265, 284, 303, 302, 321, 320, 264, 283, 301, 286, 285, 304, 341, 340},
                {271, 290, 309, 308, 345, 364, 363, 382, 270, 289, 307, 325, 344, 362, 381, 292, 291, 310, 329, 328,
                        365, 384, 383},
                {275, 294, 313, 312, 331, 330, 349, 368, 367, 386, 274, 293, 311, 329, 348, 366, 385, 296, 295, 314,
                        351, 350, 369, 388, 387}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(55f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_145_center() throws Exception {
        int[][] expectedPositions = {
                {316, 295, 274, 233, 212, 191, 170, 150, 129, 108, 87, 25, 4, 335, 315, 294, 232, 211, 190, 169, 149,
                        128, 107, 86, 45, 24, 3, 296, 275, 254, 213, 192, 171, 130, 109, 88, 26, 5},
                {106, 85, 64, 23, 2, 125, 105, 84, 22, 1, 86, 65, 44, 3},
                {112, 91, 70, 49, 29, 8, 131, 111, 90, 69, 48, 28, 7, 92, 71, 50, 9},
                {116, 95, 74, 33, 12, 135, 115, 94, 32, 11, 96, 75, 54, 13},
                {226, 205, 184, 163, 101, 80, 60, 245, 225, 204, 183, 162, 121, 100, 206, 185, 164, 102, 81, 40},
                {232, 211, 190, 169, 149, 128, 107, 86, 45, 24, 3, 251, 231, 210, 189, 168, 148, 106, 85, 65, 44, 23, 2,
                        212, 191, 170, 129, 108, 87, 25, 4},
                {236, 215, 194, 173, 111, 90, 70, 49, 28, 7, 255, 235, 214, 193, 172, 131, 110, 89, 69, 48, 27, 6, 216,
                        195, 174, 112, 91, 50, 29, 8},
                {306, 285, 264, 223, 202, 181, 160, 140, 325, 305, 284, 222, 201, 180, 286, 265, 244, 203, 182, 161, 120},
                {312, 291, 270, 249, 229, 208, 187, 166, 125, 104, 83, 21, 0, 331, 311, 290, 269, 248, 228, 207, 186,
                        165, 145, 124, 103, 82, 41, 20, 292, 271, 250, 209, 188, 167, 105, 84, 22, 1}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(145f, sourceRectangle, targetRectangle, expectedPositions);
    }

    @Test
    public void testIdentifyPotentialCloudShadowArea_saa_225_center() throws Exception {
        int[][] expectedPositions = {
                {104, 85, 28, 9, 124, 105, 86, 48, 29, 10, 103, 84, 65, 27, 8},
                {108, 89, 70, 51, 32, 13, 128, 109, 90, 71, 33, 14, 107, 88, 69, 50, 31, 12},
                {114, 95, 38, 19, 134, 115, 96, 58, 39, 113, 94, 75, 37, 18},
                {224, 205, 186, 167, 148, 129, 110, 91, 33, 14, 244, 225, 206, 187, 168, 149, 130, 111, 92, 34, 15, 223,
                        204, 185, 166, 128, 109, 90, 71, 32, 13},
                {228, 209, 190, 171, 114, 95, 37, 18, 248, 229, 210, 191, 172, 134, 115, 96, 38, 19, 227, 208, 189, 170,
                        151, 113, 94, 75, 36, 17},
                {234, 215, 196, 177, 158, 139, 254, 235, 216, 197, 178, 159, 233, 214, 195, 176, 138, 119},
                {304, 285, 228, 209, 190, 171, 113, 94, 75, 37, 18, 324, 305, 286, 248, 229, 210, 191, 172, 114, 95, 38,
                        19, 303, 284, 265, 227, 208, 189, 170, 151, 112, 93, 74, 55, 36, 17},
                {308, 289, 270, 251, 232, 213, 194, 175, 117, 98, 79, 328, 309, 290, 271, 233, 214, 195, 176, 118, 99,
                        307, 288, 269, 250, 231, 212, 193, 174, 155, 116, 97, 78, 59},
                {314, 295, 238, 219, 334, 315, 296, 258, 239, 313, 294, 275, 237, 218, 199}};
        final Rectangle sourceRectangle = new Rectangle(20, 20);
        final Rectangle targetRectangle = new Rectangle(5, 5, 10, 10);
        testPotentialCloudShadowArea(225f, sourceRectangle, targetRectangle, expectedPositions);
    }

    private void testPotentialCloudShadowArea(float saa, Rectangle sourceRectangle, Rectangle targetRectangle,
                                              int[][] expectedPotentialShadowPositions) throws IOException {
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
        final Collection<List<Integer>> potentialShadowPositions =
                PotentialCloudShadowAreaIdentifier.identifyPotentialCloudShadows(20, 20, sourceRectangle, targetRectangle,
                                                                                 sunZenith, sunAzimuth, latitude, longitude,
                                                                                 elevation, flagArray, cloudIdArea);
        assertEquals(expectedPotentialShadowPositions.length, potentialShadowPositions.size());
        int i = 0;
        for (List<Integer> positions : potentialShadowPositions) {
            assertEquals(expectedPotentialShadowPositions[i].length, positions.size());
            for (int j = 0; j < positions.size(); j++) {
                assertEquals(expectedPotentialShadowPositions[i][j], (int) positions.get(j));
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