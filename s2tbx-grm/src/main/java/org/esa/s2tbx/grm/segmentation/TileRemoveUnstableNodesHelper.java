package org.esa.s2tbx.grm.segmentation;

import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
class TileRemoveUnstableNodesHelper extends AbstractTileGraphNodesHelper<Void> {
    private final List<Node> unstableNodes;
    private final int imageWidth;

    TileRemoveUnstableNodesHelper(Graph graph, ProcessingTile tile, int imageWidth) {
        super(graph, tile);

        this.imageWidth = imageWidth;
        this.unstableNodes = new ArrayList<Node>();
    }

    @Override
    protected Void finishProcesssing() {
        int nodeCount = this.unstableNodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.unstableNodes.get(i);
            node.setExpired(true);
            node.removeEdgeToUnstableNode();
        }

        this.graph.removeExpiredNodes();

        return null; // nothing to return
    }

    @Override
    protected void processNode(Node node) {
        BoundingBox box = node.getBox();
        if (box.getLeftX() >= this.tile.getImageLeftX() && box.getTopY() >= this.tile.getImageTopY() && box.getRightX() - 1 <= this.tile.getImageRightX()
                && box.getBottomY() - 1 <= this.tile.getImageBottomY()) {
            // do nothing; // => continue;
        } else if (box.getLeftX() > this.tile.getImageRightX() || box.getTopY() > this.tile.getImageBottomY() || box.getRightX() - 1 < this.tile.getImageLeftX()
                || box.getBottomY() - 1 < this.tile.getImageTopY()) {

            addUnstableNode(node);
        } else {
            if (!isStableNode(node.getId())) {
                Contour contour = node.getContour();
                if (contour.hasBorderSize()) {
                    // initialize the first move at prev
                    int previousMoveId = contour.getMove(0);
                    // declare the current pixel index
                    int currentCellId = node.getId();
                    // explore the contour
                    int contourSize = contour.computeContourBorderSize();
                    int moveIndex = 1;
                    while (moveIndex < contourSize) {
                        int currentMoveId = contour.getMove(moveIndex);
                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, this.imageWidth);
                        if (nextCellId != currentCellId) {
                            currentCellId = nextCellId;
                            if (isStableNode(currentCellId)) {
                                break; // => moveIndex < contourSize
                            }
                        }
                        previousMoveId = currentMoveId;
                        moveIndex++;
                    }
                    if (moveIndex >= contourSize) {
                        addUnstableNode(node);
                    }

//                    boolean stable = false;
//                    for (int moveIndex = 1; moveIndex < contourSize && !stable; moveIndex++) {
//                        int currentMoveId = contour.getMove(moveIndex);
//                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, imageWidth);
//                        if (nextCellId != currentCellId) {
//                            currentCellId = nextCellId;
//                            if (isStableNode(currentCellId)) {
//                                stable = true;
//                                //break;
//                            }
//                        }
//                        previousMoveId = currentMoveId;
//                    }
//                    if (!stable) {
//                        addUnstableNode(node);
//                    }
                }
            }
        }
    }

    private boolean isStableNode(int nodeId) {
        int rowPixelInImage = nodeId / this.imageWidth;
        int colPixelInImage = nodeId % this.imageWidth;
        if (rowPixelInImage >= this.tile.getImageTopY() && rowPixelInImage <= this.tile.getImageBottomY()
                && colPixelInImage >= this.tile.getImageLeftX() && colPixelInImage <= this.tile.getImageRightX()) {

            return true;
        }
        return false;
    }

    private void addUnstableNode(Node analyzedNode) {
        synchronized (this.unstableNodes) {
            this.unstableNodes.add(analyzedNode);
        }
    }
}
