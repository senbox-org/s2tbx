package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
public class Graph {
    private final ArrayListExtended<Node> nodes;

    public Graph() {
        this.nodes = new ArrayListExtended<Node>();
    }

    public Node getNodeAt(int index) {
        return this.nodes.get(index);
    }

    public void addNode(Node nodeToAdd) {
        this.nodes.add(nodeToAdd);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int removeExpiredNodes() {
        int nodeCount = this.nodes.size();
        int lastIndexToCopy = -1;
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            if (node.isExpired()) {
                if (lastIndexToCopy == -1) {
                    lastIndexToCopy = i;
                }
            } else if (lastIndexToCopy > -1) {
                this.nodes.set(lastIndexToCopy, node);
                lastIndexToCopy++;
            }
        }
        if (lastIndexToCopy > - 1 && lastIndexToCopy < nodeCount) {
            this.nodes.removeItems(lastIndexToCopy, nodeCount);
        }
        return this.nodes.size();
    }

    void setValidFlagToAllNodes() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node n = this.nodes.get(i);
            n.setValid(true);
        }
    }

    void resetMergedFlagToAllNodes() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node n = this.nodes.get(i);
            n.setMerged(false);
        }
    }

    void resetCostUpdatedFlagToAllEdges() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node n = this.nodes.get(i);
            int edgeCount = n.getEdgeCount();
            for (int j=0; j<edgeCount; j++) {
                Edge edge = n.getEdgeAt(j);
                edge.setCostUpdated(false);
            }
        }
    }

    public List<Node> detectBorderNodes(int threadCount, Executor threadPool, ProcessingTile tile, int imageWidth, int imageHeight)
                                        throws InterruptedException {

        TileBorderNodesHelper helper = new TileBorderNodesHelper(this, tile, imageWidth, imageHeight);
        return helper.processInParallel(threadCount, threadPool);

//        List<Node> result = new ArrayList<Node>();
//        int nodeCount = this.nodes.size();
//        for (int i=0; i<nodeCount; i++) {
//            Node node = this.nodes.get(i);
//            BoundingBox box = node.getBox();
//            if (tile.isRegionInside(box)) {
//                continue; // the node is inside the tile
//            } else {
//                // the node is on the tile margin or outside the tile
//                IntSet borderCells = AbstractSegmenter.generateBorderCells(node, imageWidth);
//                IntIterator itCells = borderCells.iterator();
//                while (itCells.hasNext()) {
//                    int gridIdInImage = itCells.nextInt();
//                    int rowPixelInImage = gridIdInImage / imageWidth;
//                    int colPixelInImage = gridIdInImage % imageWidth;
//                    if (tile.getImageTopY() > 0 && rowPixelInImage == tile.getImageTopY()) {
//                        result.add(node);
//                        break;
//                    } else if (tile.getImageRightX() < imageWidth - 1 && colPixelInImage == tile.getImageRightX()) {
//                        result.add(node);
//                        break;
//                    } else if (tile.getImageBottomY() < imageHeight - 1 && rowPixelInImage == tile.getImageBottomY()) {
//                        result.add(node);
//                        break;
//                    } else if (tile.getImageLeftX() > 0 && colPixelInImage == tile.getImageLeftX()) {
//                        result.add(node);
//                        break;
//                    }
//                }
//            }
//        }
//
//        return result;
    }

    public Int2ObjectMap<List<Node>> buildBorderPixelMapInParallel(int threadCount, Executor threadPool, ProcessingTile tile, int rowTileIndex,
                                                                     int columnTileIndex, int tileCountX, int tileCountY, int imageWidth)
                                                                     throws InterruptedException {

        TileBorderPixelsHelper helper = new TileBorderPixelsHelper(this, tile, rowTileIndex, columnTileIndex, tileCountX, tileCountY, imageWidth);
        return helper.processInParallel(threadCount, threadPool);
    }

    public void removeDuplicatedNodes(Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth) {
        ObjectIterator<Int2ObjectMap.Entry<List<Node>>> it = borderPixelMap.int2ObjectEntrySet().iterator();
        IntSet borderCells = new IntOpenHashSet();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<Node>> entry = it.next();
            List<Node> nodes = entry.getValue();
            if (nodes.size() > 1) {
                Node refNode = nodes.get(0); // refNode
                // explore duplicated nodes
                for (int i=1; i<nodes.size(); i++) {
                    Node currentNode = nodes.get(i);
                    int edgeCount = currentNode.getEdgeCount();
                    for (int k=0; k<edgeCount; k++) {
                        Edge edge = currentNode.getEdgeAt(k);
                        Node neighNit = edge.getTarget();
                        int removedEdgeIndex = neighNit.removeEdge(currentNode);
                        assert(removedEdgeIndex >= 0);

                        Edge edgeToFirstNode = neighNit.findEdge(refNode);
                        if (edgeToFirstNode == null) {
                            // create an edge neighNit -> refNode
                            neighNit.addEdge(refNode, edge.getBoundary());
                            // create an edge refNode -> neighNit
                            refNode.addEdge(neighNit, edge.getBoundary());
                        }
                    }
                    currentNode.setExpired(true);
                }

                //TODO Jean new code
                borderCells.clear(); // clear the set
                if (borderCells.add(refNode.getId())) {
                    List<Node> resultNodes = borderPixelMap.get(refNode.getId());
                    if (resultNodes != null) {
                        resultNodes.clear();
                        resultNodes.add(refNode);
                    }
                }
                Contour contour = refNode.getContour();
                if (contour.hasBorderSize()) {
                    // initialize the first move at prev
                    int previousMoveId = contour.getMove(0);
                    // declare the current pixel index
                    int currentCellId = refNode.getId();
                    // explore the contour
                    int contourSize = contour.computeContourBorderSize();
                    for (int moveIndex = 1; moveIndex < contourSize; moveIndex++) {
                        int currentMoveId = contour.getMove(moveIndex);
                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, imageWidth);
                        if (nextCellId != currentCellId) {
                            currentCellId = nextCellId;
                            if (borderCells.add(currentCellId)) {
                                List<Node> resultNodes = borderPixelMap.get(currentCellId);
                                if (resultNodes != null) {
                                    resultNodes.clear();
                                    resultNodes.add(refNode);
                                }
                            }
                        }
                        previousMoveId = currentMoveId;
                    }
                }

//                //TODO Jean old code
//                IntSet borderCells = AbstractSegmenter.generateBorderCells(refNode, imageWidth);
//                IntIterator itCells = borderCells.iterator();
//                while (itCells.hasNext()) {
//                    int gridId = itCells.nextInt();
//                    List<Node> resultNodes = borderPixelMap.get(gridId);
//                    if (resultNodes != null) {
//                        resultNodes.clear();
//                        resultNodes.add(refNode);
//                    }
//                }
            }
        }
        removeExpiredNodes();
    }

    public void removeUnstableSegmentsInParallel(int threadCount, Executor threadPool, ProcessingTile tile, int imageWidth) throws InterruptedException {
        TileRemoveUnstableNodesHelper helper = new TileRemoveUnstableNodesHelper(this, tile, imageWidth);
        helper.processInParallel(threadCount, threadPool);

//        int nodeCount = this.nodes.size();
//        for (int i=0; i<nodeCount; i++) {
//            Node node = this.nodes.get(i);
//            BoundingBox box = node.getBox();
//            if (box.getLeftX() >= tile.getImageLeftX() && box.getTopY() >= tile.getImageTopY() && box.getRightX() - 1 <= tile.getImageRightX() && box.getBottomY() - 1 <= tile.getImageBottomY()) {
//                continue;
//            } else if (box.getLeftX() > tile.getImageRightX() || box.getTopY() > tile.getImageBottomY() || box.getRightX() - 1 < tile.getImageLeftX()
//                    || box.getBottomY() - 1 < tile.getImageTopY()) {
//                node.setExpired(true);
//                node.removeEdgeToUnstableNode();
//            } else {
//                boolean stable = false;
//                IntSet borderCells = AbstractSegmenter.generateBorderCells(node, imageWidth);
//                IntIterator itCells = borderCells.iterator();
//                while (itCells.hasNext()) {
//                    int gridIdInImage = itCells.nextInt();
//                    int rowPixelInImage = gridIdInImage / imageWidth;
//                    int colPixelInImage = gridIdInImage % imageWidth;
//                    if (rowPixelInImage >= tile.getImageTopY() && rowPixelInImage <= tile.getImageBottomY() && colPixelInImage >= tile.getImageLeftX() && colPixelInImage <= tile.getImageRightX()) {
//                        stable = true;
//                        break;
//                    }
//                }
//                if (!stable) {
//                    node.setExpired(true);
//                    node.removeEdgeToUnstableNode();
//                }
//            }
//        }
//        removeExpiredNodes();
    }

    public void addNodes(Graph subgraph) {
        this.nodes.addAll(subgraph.nodes);
    }

    public void rescaleGraph(ProcessingTile tile, int imageWidth) {
        BoundingBox region = tile.getRegion();
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);

            // start pixel index of the node (in the tile)
            int rowNodeTile = node.getId() / region.getWidth();
            int colNodeTile = node.getId() % region.getWidth();

            // start pixel index of the node (in the image)
            int rowNodeImg = region.getTopY() + rowNodeTile;// - tile.getTopMargin();
            int colNodeImg = region.getLeftX() + colNodeTile;// - tile.getLeftMargin();

            // set the node id in the image
            node.setId(rowNodeImg * imageWidth + colNodeImg);

            // change also its bounding box
            BoundingBox box = node.getBox();
            box.setLeftX(region.getLeftX() + box.getLeftX());// - tile.getLeftMargin());
            box.setTopY(region.getTopY() + box.getTopY());// - tile.getTopMargin());
        }
    }

    public List<Node> findUselessNodesInParallel(int threadCount, Executor threadPool, ProcessingTile tile, int imageWidth) throws InterruptedException {
        TileUselessNodesHelper helper = new TileUselessNodesHelper(this, tile, imageWidth);
        return helper.processInParallel(threadCount, threadPool);

//        List<Node> nodesToIterate = new ArrayList<Node>();
//        int nodeCount = this.nodes.size();
//        for (int i=0; i<nodeCount; i++) {
//            Node node = this.nodes.get(i);
//            BoundingBox box = node.getBox();
//
//            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
//                continue;
//            } else if (box.getLeftX() > tile.getImageRightX() || box.getTopY() > tile.getImageBottomY() || box.getRightX() - 1 < tile.getImageLeftX() || box.getBottomY() - 1 < tile.getImageTopY()) {
//                continue;
//            } else {
//                IntSet borderCells = AbstractSegmenter.generateBorderCells(node, imageWidth);
//                IntIterator itCells = borderCells.iterator();
//                while (itCells.hasNext()) {
//                    int gridId = itCells.nextInt();
//                    int rowPixel = gridId / imageWidth;
//                    int columnPixel = gridId % imageWidth;
//                    if (rowPixel == tile.getImageTopY() || rowPixel == tile.getImageBottomY()) {
//                        if (columnPixel >= tile.getImageLeftX() && columnPixel <= tile.getImageRightX()) {
//                            nodesToIterate.add(node);
//                            break;
//                        }
//                    } else if (columnPixel == tile.getImageLeftX() || columnPixel == tile.getImageRightX()) {
//                        if (rowPixel >= tile.getImageTopY() && rowPixel <= tile.getImageBottomY()) {
//                            nodesToIterate.add(node);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return nodesToIterate;
    }

    public void removeUselessNodes(Int2ObjectMap<Node> borderNodes, ProcessingTile tile) {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (!borderNodes.containsKey(node.getId())) {
                node.removeEdgeToUnstableNode();
                node.setExpired(true);
            }
        }

        removeExpiredNodes();
    }

    private static class ArrayListExtended<ItemType> extends ArrayList<ItemType> {

        ArrayListExtended() {
            super();
        }

        void removeItems(int fromIndex, int toIndex) {
            removeRange(fromIndex, toIndex);
        }
    }
}
