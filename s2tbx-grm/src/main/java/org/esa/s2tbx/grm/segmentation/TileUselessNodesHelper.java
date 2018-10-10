package org.esa.s2tbx.grm.segmentation;

import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
class TileUselessNodesHelper extends AbstractTileGraphNodesHelper<List<Node>> {
    private final List<Node> uselessNodes;
    private final int imageWidth;

    TileUselessNodesHelper(Graph graph, ProcessingTile tile, int imageWidth) {
        super(graph, tile);

        this.imageWidth = imageWidth;
        this.uselessNodes = new ArrayList<Node>();
    }

    @Override
    protected List<Node> finishProcesssing() {
        return this.uselessNodes;
    }

    @Override
    protected void processNode(Node node) {
        BoundingBox box = node.getBox();

        if (box.getLeftX() > this.tile.getImageLeftX() && box.getTopY() > this.tile.getImageTopY() && box.getRightX() - 1 < this.tile.getImageRightX()
                && box.getBottomY() - 1 < this.tile.getImageBottomY()) {
            //continue;
        } else if (box.getLeftX() > this.tile.getImageRightX() || box.getTopY() > this.tile.getImageBottomY() || box.getRightX() - 1 < this.tile.getImageLeftX()
                || box.getBottomY() - 1 < this.tile.getImageTopY()) {
            //continue;
        } else {
            if (canAddBorderCell(node.getId())) {
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
                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, this.imageWidth);
                        if (nextCellId != currentCellId) {
                            currentCellId = nextCellId;
                            if (canAddBorderCell(currentCellId)) {
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

    public boolean canAddBorderCell(int borderCellId) {
        int rowPixel = borderCellId / this.imageWidth;
        int columnPixel = borderCellId % this.imageWidth;
        if (rowPixel == this.tile.getImageTopY() || rowPixel == this.tile.getImageBottomY()) {
            if (columnPixel >= this.tile.getImageLeftX() && columnPixel <= this.tile.getImageRightX()) {
                return true;
            }
        } else if (columnPixel == this.tile.getImageLeftX() || columnPixel == this.tile.getImageRightX()) {
            if (rowPixel >= this.tile.getImageTopY() && rowPixel <= this.tile.getImageBottomY()) {
                return true;
            }
        }
        return false;
    }

    private void addNode(Node analyzedNode) {
        synchronized (this.uselessNodes) {
            this.uselessNodes.add(analyzedNode);
        }
    }
}
