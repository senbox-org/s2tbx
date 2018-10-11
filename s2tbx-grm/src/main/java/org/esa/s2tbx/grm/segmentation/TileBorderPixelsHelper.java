package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
class TileBorderPixelsHelper extends AbstractTileGraphNodesHelper<Int2ObjectMap<List<Node>>> {
    private final Int2ObjectMap<List<Node>> borderPixelMap;
    private final int rowMin;
    private final int rowMax;
    private final int columnMin;
    private final int columnMax;
    private final int rowTileIndex;
    private final int columnTileIndex;
    private final int tileCountX;
    private final int tileCountY;
    private final int imageWidth;

    TileBorderPixelsHelper(Graph graph, ProcessingTile tile, int rowTileIndex, int columnTileIndex, int tileCountX, int tileCountY, int imageWidth) {
        super(graph, tile);

        this.rowTileIndex = rowTileIndex;
        this.columnTileIndex = columnTileIndex;
        this.tileCountX = tileCountX;
        this.tileCountY = tileCountY;
        this.imageWidth = imageWidth;

        this.borderPixelMap = new Int2ObjectLinkedOpenHashMap<List<Node>>(); // key = node id
        this.rowMin = (this.tile.getImageTopY() > 0) ? this.tile.getImageTopY() - 1 : this.tile.getImageTopY();
        this.rowMax = this.tile.getImageBottomY() + 1;
        this.columnMin = (this.tile.getImageLeftX() > 0) ? this.tile.getImageLeftX() - 1 : this.tile.getImageLeftX();
        this.columnMax = this.tile.getImageRightX() + 1;
    }

    @Override
    protected Int2ObjectMap<List<Node>> finishProcesssing() {
        return this.borderPixelMap;
    }

    @Override
    protected void processNode(Node node) {
        BoundingBox box = node.getBox();
        if (this.tile.isRegionInside(box)) {
            // do nothing; // => continue;
        } else {
            if (canAddBorderCell(node.getId())) {
                addNode(node, node.getId());
            }
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
                            addNode(node, currentCellId);
                        }
                    }
                    previousMoveId = currentMoveId;
                }
            }
        }
    }

    private boolean canAddBorderCell(int borderCellId) {
        int rowPixel = borderCellId / this.imageWidth;
        int columnPixel = borderCellId % this.imageWidth;
        if (this.rowTileIndex > 0 && (rowPixel == this.tile.getImageTopY() || rowPixel == rowMin)) {
            return true;
        } else if (this.columnTileIndex < this.tileCountX - 1 && (columnPixel == this.tile.getImageRightX() || columnPixel == columnMax)) {
            return true;
        } else if (this.rowTileIndex < this.tileCountY - 1 && (rowPixel == this.tile.getImageBottomY() || rowPixel == rowMax)) {
            return true;
        } else if (this.columnTileIndex > 0 && (columnPixel == this.tile.getImageLeftX() || columnPixel == columnMin)) {
            return true;
        }
        return false;
    }

    private void addNode(Node analyzedNode, int borderCellId) {
        synchronized (this.borderPixelMap) {
            List<Node> nodes = this.borderPixelMap.get(borderCellId);
            if (nodes == null) {
                nodes = new ArrayList<Node>();
                nodes.add(analyzedNode);
                this.borderPixelMap.put(borderCellId, nodes);
            } else {
                int nodeCount = nodes.size();
                int index = 0;
                while (index < nodeCount) {
                    if (nodes.get(index) == analyzedNode) {
                        break; // the node was found => index < nodeCount
                    }
                    index++;
                }
                if (index >= nodeCount) {
                    nodes.add(analyzedNode);
                }
            }
        }
    }
}
