package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.lang.ref.WeakReference;

/**
 * @author Jean Coravu
 */
public class OutputMaskMatrixHelper {
    private final int[] nodeIds;
    private final Contour[] nodeContours;
    private final int imageWidth;
    private final int imageHeight;

    public OutputMaskMatrixHelper(Graph graph, int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        int nodeCount = graph.getNodeCount();
        this.nodeIds = new int[nodeCount];
        this.nodeContours = new Contour[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            Node node = graph.getNodeAt(i);
            this.nodeIds[i] = node.getId();
            this.nodeContours[i] = node.getContour();
        }
    }

    public OutputMarkerMatrixHelper buildMaskMatrix() {
        int widthCount = this.imageWidth + 2;
        int heightCount = this.imageHeight + 2;

        int[][] mask = new int[heightCount][widthCount];
        int nodeCount = this.nodeIds.length;
        IntSet borderCells = new IntOpenHashSet();
        for (int i = 0; i < nodeCount; i++) {
            borderCells.clear();

            // add the first pixel to the border list
            if (borderCells.add(this.nodeIds[i])) {
                int gridX = this.nodeIds[i] % this.imageWidth;
                int gridY = this.nodeIds[i] / this.imageWidth;
                mask[gridY + 1][gridX + 1] = i + 1;
            }

            Contour contour = this.nodeContours[i];// node.getContour();
            if (contour.hasBorderSize()) {
                // initialize the first move at prev
                int previousMoveId = contour.getMove(0);

                // declare the current pixel index
                int currentCellId = this.nodeIds[i];

                // explore the contour
                int contourSize = contour.computeContourBorderSize();
                for (int moveIndex = 1; moveIndex < contourSize; moveIndex++) {
                    int currentMoveId = contour.getMove(moveIndex);

                    int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, this.imageWidth);
                    if (nextCellId != currentCellId) {
                        currentCellId = nextCellId;
                        if (borderCells.add(currentCellId)) {
                            int gridX = currentCellId % this.imageWidth;
                            int gridY = currentCellId / this.imageWidth;
                            mask[gridY + 1][gridX + 1] = i + 1;
                        }
                    }

                    previousMoveId = currentMoveId;
                }
            }
        }

        // fill the first and the last rows (the top and the bottom rows) in the mask matrix
        for (int x = 0; x < widthCount; x++) {
            mask[0][x] = nodeCount + 1;
            mask[heightCount - 1][x] = nodeCount + 1;
        }
        // fill the first and the last columns (the left and the right columns) in the mask matrix
        for (int y = 0; y < heightCount; y++) {
            mask[y][0] = nodeCount + 1;
            mask[y][widthCount - 1] = nodeCount + 1;
        }

        return new OutputMarkerMatrixHelper(this.imageWidth, this.imageHeight, nodeCount, mask);
    }

    public final void doClose() {
        WeakReference<int[]> referenceNodeIds = new WeakReference<int[]>(this.nodeIds);
        referenceNodeIds.clear();
        WeakReference<Contour[]> referenceNodeContours = new WeakReference<Contour[]>(this.nodeContours);
        referenceNodeContours.clear();
    }
}
