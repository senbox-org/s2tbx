package org.esa.s2tbx.grm.segmentation;

import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
class TileBorderNodesHelper extends AbstractTileGraphNodesHelper<List<Node>> {
    private final List<Node> borderNodes;
    private final int imageWidth;
    private final int imageHeight;

    TileBorderNodesHelper(Graph graph, ProcessingTile tile, int imageWidth, int imageHeight) {
        super(graph, tile);

        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.borderNodes = new ArrayList<Node>();
    }

    @Override
    protected List<Node> finishProcesssing() {
        return this.borderNodes;
    }

    @Override
    protected void processNode(Node node) {
        BoundingBox box = node.getBox();
        if (this.tile.isRegionInside(box)) {
            //continue; // the node is inside the tile
        } else {
            // the node is on the tile margin or outside the tile
            if (isBorderCell(node.getId())) {
                addNode(node);
            } else {
                Contour contour = node.getContour();
                if (contour.hasBorderSize()) {
                    // initialize the first move at prev
                    int previousMoveId = contour.getMove(0);
                    // declare the current pixel index
                    int currentCellId = node.getId();
                    // explore the contour
                    int contourSize = contour.computeContourBorderSize();
                    for (int moveIndex = 1; moveIndex < contourSize; moveIndex++) {
                        int currentMoveId = contour.getMove(moveIndex);
                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, imageWidth);
                        if (nextCellId != currentCellId) {
                            currentCellId = nextCellId;
                            if (isBorderCell(currentCellId)) {
                                addNode(node);
                                break;
                            }
                        }
                        previousMoveId = currentMoveId;
                    }
                }
            }
        }
    }

    private boolean isBorderCell(int borderCellId) {
        int rowPixelInImage = borderCellId / this.imageWidth;
        int colPixelInImage = borderCellId % this.imageWidth;
        if (this.tile.getImageTopY() > 0 && rowPixelInImage == this.tile.getImageTopY()) {
            return true;
        } else if (this.tile.getImageRightX() < this.imageWidth - 1 && colPixelInImage == this.tile.getImageRightX()) {
            return true;
        } else if (this.tile.getImageBottomY() < this.imageHeight - 1 && rowPixelInImage == this.tile.getImageBottomY()) {
            return true;
        } else if (this.tile.getImageLeftX() > 0 && colPixelInImage == this.tile.getImageLeftX()) {
            return true;
        }
        return false;
    }

    private void addNode(Node analyzedNode) {
        synchronized (this.borderNodes) {
            this.borderNodes.add(analyzedNode);
        }
    }
}
