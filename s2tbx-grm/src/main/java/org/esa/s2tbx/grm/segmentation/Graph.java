package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
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

    public List<Node> detectBorderNodes(ProcessingTile tile, int imageWidth, int imageHeight) {
        List<Node> result = new ArrayList<Node>();
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if (tile.isRegionInside(box)) {
            //if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                // the node is inside the tile
                continue;
            } else {
                // the node is on the tile margin or outside the tile
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridIdInImage = itCells.nextInt();
                    int rowPixelInImage = gridIdInImage / imageWidth;
                    int colPixelInImage = gridIdInImage % imageWidth;
                    if (tile.getImageTopY() > 0 && rowPixelInImage == tile.getImageTopY()) {
                        result.add(node);
                        break;
                    } else if (tile.getImageRightX() < imageWidth - 1 && colPixelInImage == tile.getImageRightX()) {
                        result.add(node);
                        break;
                    } else if (tile.getImageBottomY() < imageHeight - 1 && rowPixelInImage == tile.getImageBottomY()) {
                        result.add(node);
                        break;
                    } else if (tile.getImageLeftX() > 0 && colPixelInImage == tile.getImageLeftX()) {
                        result.add(node);
                        break;
                    }
                }
            }
        }

        return result;
    }

    public Int2ObjectMap<List<Node>> buildBorderPixelMapUsingThreads(int threadCount, Executor threadPool, ProcessingTile tile, int rowTileIndex,
                                                                     int columnTileIndex, int tileCountX, int tileCountY, int imageWidth)
                                                                     throws InterruptedException {
        TileBorderPixelsHelper helper = new TileBorderPixelsHelper(this, tile, rowTileIndex, columnTileIndex, tileCountX, tileCountY, imageWidth);
        return helper.computeBorderPixelsUsingThreads(threadCount, threadPool);
    }

    public Int2ObjectMap<List<Node>> buildBorderPixelMap(ProcessingTile tile, int rowTileIndex, int columnTileIndex, int tileCountX, int tileCountY, int imageWidth) {
        Int2ObjectMap<List<Node>> borderPixelMap = new Int2ObjectLinkedOpenHashMap<List<Node>>(); // key = node id

        int rowMin = (tile.getImageTopY() > 0) ? tile.getImageTopY() - 1 : tile.getImageTopY();
        int rowMax = tile.getImageBottomY() + 1;
        int colMin = (tile.getImageLeftX() > 0) ? tile.getImageLeftX() - 1 : tile.getImageLeftX();
        int colMax = tile.getImageRightX() + 1;

        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if (tile.isRegionInside(box)) {
            //if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                // the node is inside the tile
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridId = itCells.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    boolean addNode = false;
                    if (rowTileIndex > 0 && (rowPixel == tile.getImageTopY() || rowPixel == rowMin)) {
                        addNode = true;
                    } else if (columnTileIndex < tileCountX - 1 && (colPixel == tile.getImageRightX() || colPixel == colMax)) {
                        addNode = true;
                    } else if (rowTileIndex < tileCountY - 1 && (rowPixel == tile.getImageBottomY() || rowPixel == rowMax)) {
                        addNode = true;
                    } else if (columnTileIndex > 0 && (colPixel == tile.getImageLeftX() || colPixel == colMin)) {
                        addNode = true;
                    }
                    if (addNode) {
                        List<Node> nodes = borderPixelMap.get(gridId);
                        if (nodes == null) {
                            nodes = new ArrayList<Node>();
                            borderPixelMap.put(gridId, nodes);
                        }
                        nodes.add(node);
                    }
                }
            }
        }
        return borderPixelMap;
    }

    public void removeDuplicatedNodes(Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth) {
        ObjectIterator<Int2ObjectMap.Entry<List<Node>>> it = borderPixelMap.int2ObjectEntrySet().iterator();
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

                IntSet borderCells = AbstractSegmenter.generateBorderCells(refNode.getContour(), refNode.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridId = itCells.nextInt();
                    List<Node> resultNodes = borderPixelMap.get(gridId);
                    if (resultNodes != null) {
                        resultNodes.clear();
                        resultNodes.add(refNode);
                    }
                }
            }
        }
        removeExpiredNodes();
    }

    public void removeUnstableSegments(ProcessingTile tile, int imageWidth) {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if (box.getLeftX() >= tile.getImageLeftX() && box.getTopY() >= tile.getImageTopY() && box.getRightX() - 1 <= tile.getImageRightX() && box.getBottomY() - 1 <= tile.getImageBottomY()) {
                continue;
            } else if (box.getLeftX() > tile.getImageRightX() || box.getTopY() > tile.getImageBottomY() || box.getRightX() - 1 < tile.getImageLeftX()
                       || box.getBottomY() - 1 < tile.getImageTopY()) {
                node.setExpired(true);
                removeEdgeToUnstableNode(node);
            } else {
                boolean stable = false;
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridIdInImage = itCells.nextInt();
                    int rowPixelInImage = gridIdInImage / imageWidth;
                    int colPixelInImage = gridIdInImage % imageWidth;
                    if (rowPixelInImage >= tile.getImageTopY() && rowPixelInImage <= tile.getImageBottomY() && colPixelInImage >= tile.getImageLeftX() && colPixelInImage <= tile.getImageRightX()) {
                        stable = true;
                        break;
                    }
                }
                if (!stable) {
                    node.setExpired(true);
                    removeEdgeToUnstableNode(node);
                }
            }
        }
        removeExpiredNodes();
    }

    private void removeEdgeToUnstableNode(Node node) {
        int edgeCount = node.getEdgeCount();
        for (int j=0; j<edgeCount; j++) {
            Edge edge = node.getEdgeAt(j);
            Node nodeNeighbor = edge.getTarget();
            int removedEdgeIndex = nodeNeighbor.removeEdge(node);
            assert(removedEdgeIndex >= 0);
        }
    }

    public void addNodes(Graph subgraph) {
        int nodeCount = subgraph.getNodeCount();
        for (int i=0; i<nodeCount; i++) {
            Node node = subgraph.getNodeAt(i);
            addNode(node);
        }
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

    public List<Node> findUselessNodes(ProcessingTile tile, int imageWidth) {
        List<Node> nodesToIterate = new ArrayList<Node>();
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (box.getLeftX() > tile.getImageRightX() || box.getTopY() > tile.getImageBottomY() || box.getRightX() - 1 < tile.getImageLeftX() || box.getBottomY() - 1 < tile.getImageTopY()) {
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridId = itCells.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    if (rowPixel == tile.getImageTopY() || rowPixel == tile.getImageBottomY()) {
                        if (colPixel >= tile.getImageLeftX() && colPixel <= tile.getImageRightX()) {
                            nodesToIterate.add(node);
                            break;
                        }
                    } else if (colPixel == tile.getImageLeftX() || colPixel == tile.getImageRightX()) {
                        if (rowPixel >= tile.getImageTopY() && rowPixel <= tile.getImageBottomY()) {
                            nodesToIterate.add(node);
                            break;
                        }
                    }
                }
            }
        }
        return nodesToIterate;
    }

    public void removeUselessNodes(Int2ObjectMap<Node> borderNodes, ProcessingTile tile) {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            Node node = this.nodes.get(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (!borderNodes.containsKey(node.getId())) {
                removeEdgeToUnstableNode(node);
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
