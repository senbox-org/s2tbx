package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.matrix.IntMatrix;

import java.lang.ref.WeakReference;

/**
 * @author Jean Coravu
 */
public class OutputMarkerMatrixHelper {
    private final int[][] mask;
    private final int imageWidth;
    private final int imageHeight;
    private final int graphNodeCount;

    public OutputMarkerMatrixHelper(int imageWidth, int imageHeight, int graphNodeCount, int[][] mask) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.graphNodeCount = graphNodeCount;
        this.mask = mask;
    }

    public int getGraphNodeCount() {
        return graphNodeCount;
    }

    public final IntMatrix buildOutputMatrix() {
        int widthCount = this.imageWidth + 2;
        int heightCount = this.imageHeight + 2;
        int[][] marker = buildMarkerMatrix(widthCount, heightCount);

        IntMatrix result = new IntMatrix(this.imageHeight, this.imageWidth);
        for (int y = 1; y < heightCount - 1; y++) {
            for (int x = 1; x < widthCount - 1; x++) {
                result.setValueAt(y-1, x-1, marker[y][x]);
            }
        }

        WeakReference<int[][]> referenceMarkerMatrix = new WeakReference<int[][]>(marker);
        referenceMarkerMatrix.clear();

        return result;
    }

    public final ProductData buildOutputProductData() {
        int widthCount = this.imageWidth + 2;
        int heightCount = this.imageHeight + 2;
        int[][] marker = buildMarkerMatrix(widthCount, heightCount);

        int elementCount = this.imageWidth * this.imageHeight;
        ProductData data = ProductData.createInstance(ProductData.TYPE_INT32, elementCount);

        for (int y = 1; y < heightCount - 1; y++) {
            for (int x = 1; x < widthCount - 1; x++) {
                int elementIndex = (this.imageWidth * (y - 1)) + (x - 1);
                data.setElemIntAt(elementIndex, marker[y][x]);
                //result.setValueAt(y-1, x-1, marker[y][x]);
            }
        }

        WeakReference<int[][]> referenceMarkerMatrix = new WeakReference<int[][]>(marker);
        referenceMarkerMatrix.clear();

        return data;
    }

    public final void doClose() {
        WeakReference<int[][]> referenceMaskMatrix = new WeakReference<int[][]>(this.mask);
        referenceMaskMatrix.clear();
    }

    private int[][] buildMarkerMatrix(int widthCount, int heightCount) {
        // copy the first two rows and the last two rows in the marker matrix
        int[][] marker = new int[heightCount][widthCount];
        for (int x = 0; x < widthCount; x++) {
            marker[0][x] = mask[0][x];
            marker[1][x] = mask[1][x];
            marker[heightCount - 2][x] = mask[heightCount - 2][x];
            marker[heightCount - 1][x] = mask[heightCount - 1][x];
        }
        // fill the first two columns and the last two columns in the marker matrix
        for (int y = 0; y < heightCount; y++) {
            marker[y][0] = mask[y][0];
            marker[y][1] = mask[y][1];
            marker[y][widthCount - 2] = mask[y][widthCount - 2];
            marker[y][widthCount - 1] = mask[y][widthCount - 1];
        }
        // fill the center of the marker matrix
        for (int y = 2; y < heightCount - 1; y++) {
            for (int x = 2; x < widthCount - 1; x++) {
                marker[y][x] = this.graphNodeCount;
            }
        }

        // first step to compute the values in the marker matrix
        for (int y = 1; y < heightCount; y++) {
            for (int x = 1; x < widthCount; x++) {
                int pixel = marker[y][x];
                int leftPixel = marker[y][x - 1];
                int topPixel = marker[y - 1][x];
                int value = Math.min(Math.min(leftPixel, topPixel), pixel);
                marker[y][x] = Math.max(value, mask[y][x]);
            }
        }

        // second step to compute the values in the marker matrix
        IntPriorityQueue queue = new IntArrayFIFOQueue();
        for (int y = heightCount - 2; y > 0; y--) {
            for (int x = widthCount - 2; x > 0; x--) {
                int markerCurrentPixel = marker[y][x];
                int rightPixel = marker[y][x + 1];
                int bottomPixel = marker[y + 1][x];
                int value = Math.min(Math.min(rightPixel, bottomPixel), markerCurrentPixel);
                markerCurrentPixel = Math.max(value, mask[y][x]);
                marker[y][x] = markerCurrentPixel;

                if ((bottomPixel > markerCurrentPixel && bottomPixel > mask[y + 1][x]) || (rightPixel > markerCurrentPixel && rightPixel > mask[y][x + 1])) {
                    queue.enqueue(convertPointToId(x, y, widthCount));
                }
            }
        }

        // the third step to compute the values in the marker matrix
        while (!queue.isEmpty()) {
            int id = queue.dequeueInt();
            int x = id % widthCount;
            int y = id / widthCount;
            int markerCurrentPixel = marker[y][x];

            addToQueue(marker, mask, markerCurrentPixel, x, y - 1, widthCount, queue); // top
            addToQueue(marker, mask, markerCurrentPixel, x + 1, y, widthCount, queue); // right
            addToQueue(marker, mask, markerCurrentPixel, x, y + 1, widthCount, queue); // bottom
            addToQueue(marker, mask, markerCurrentPixel, x - 1, y, widthCount, queue); // left
        }

        return marker;
    }

    private static void addToQueue(int[][] marker, int[][] mask, int markerCurrentPixel, int x, int y, int widthCount, IntPriorityQueue queue) {
        int markerNeighborPixel = marker[y][x];
        int maskNeighborPixel = mask[y][x];
        if (markerNeighborPixel > markerCurrentPixel && markerNeighborPixel != maskNeighborPixel) {
            marker[y][x] = Math.max(markerCurrentPixel, maskNeighborPixel);
            queue.enqueue(convertPointToId(x, y, widthCount));
        }
    }

    private static int convertPointToId(int x, int y, int width) {
        return (y * width) + x;
    }
}
