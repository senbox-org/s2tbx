package org.esa.s2tbx.grm;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.grm.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
public class Graph<NodeType extends Node> {
    private final ArrayListExtended<NodeType> nodes;

    public Graph(int numberOfNodes) {
        this.nodes = new ArrayListExtended<NodeType>(numberOfNodes);
    }

    public NodeType getNodeAt(int index) {
        return this.nodes.get(index);
    }

    public void addNode(NodeType nodeToAdd) {
        this.nodes.add(nodeToAdd);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int removeExpiredNodes() {
        int nodeCount = this.nodes.size();
        int lastIndexToCopy = -1;
        for (int i=0; i<nodeCount; i++) {
            NodeType node = this.nodes.get(i);
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

    public void setValidFlagToAllNodes() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType n = this.nodes.get(i);
            n.setValid(true);
        }
    }

    public void resetMergedFlagToAllNodes() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType n = this.nodes.get(i);
            n.setMerged(false);
        }
    }

    public void resetCostUpdatedFlagToAllEdges() {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType n = this.nodes.get(i);
            int edgeCount = n.getEdgeCount();
            for (int j=0; j<edgeCount; j++) {
                Edge edge = n.getEdgeAt(j);
                edge.setCostUpdated(false);
            }
        }
    }

    public Object2IntMap<Node> detectBorderNodes(ProcessingTile tile, int imageWidth, int imageHeight) {
        Object2IntMap<Node> borderNodesMap = new Object2IntArrayMap<Node>();
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if(box.getUpperLeftX() > tile.columns[0] && box.getUpperLeftY() > tile.rows[0] && box.getUpperRightX() - 1 < tile.columns[1] && box.getLowerRightY() - 1 < tile.rows[1]) {
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator it = borderCells.iterator();
                while (it.hasNext()) {
                    int gridId = it.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    if (tile.rows[0] > 0 && rowPixel == tile.rows[0]) {
                        borderNodesMap.put(node, 0);
                        break;
                    } else if(tile.columns[1] < imageWidth - 1 && colPixel == tile.columns[1]) {
                        borderNodesMap.put(node, 0);
                        break;
                    } else if(tile.rows[1] < imageHeight - 1 && rowPixel == tile.rows[1]) {
                        borderNodesMap.put(node, 0);
                        break;
                    } else if(tile.columns[0] > 0 && colPixel == tile.columns[0]) {
                        borderNodesMap.put(node, 0);
                        break;
                    } else {
                        continue;
                    }
                }
            }
        }

        return borderNodesMap;
    }

    public Int2ObjectMap<List<Node>> buildBorderPixelMap(ProcessingTile tile, int rowTile, int colTile, int nbTilesX, int nbTilesY, int imageWidth) {
        Int2ObjectMap<List<Node>> borderPixelMap = new Int2ObjectArrayMap<List<Node>>();

        int rowMin = (tile.rows[0] > 0) ? tile.rows[0] - 1 : tile.rows[0];
        int rowMax = tile.rows[1] + 1;
        int colMin = (tile.columns[0] > 0) ? tile.columns[0] - 1 : tile.columns[0];
        int colMax = tile.columns[1] + 1;

        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if (box.getUpperLeftX() > tile.columns[0] && box.getUpperLeftY() > tile.rows[0] && box.getUpperRightX() - 1 < tile.columns[1] && box.getLowerRightY() - 1 < tile.rows[1]) {
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator it = borderCells.iterator();
                while (it.hasNext()) {
                    int gridId = it.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    boolean addNode = false;
                    if (rowTile > 0 && (rowPixel == tile.rows[0] || rowPixel == rowMin)) {
                        addNode = true;
                    } else if (colTile < nbTilesX - 1 && ( colPixel == tile.columns[1] || colPixel == colMax)) {
                        addNode = true;
                    } else if (rowTile < nbTilesY - 1 && (rowPixel == tile.rows[1] || rowPixel == rowMax)) {
                        addNode = true;
                    } else if (colTile > 0 && ( colPixel == tile.columns[0] || colPixel == colMin)) {
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
                Node firstNode = nodes.get(0); // refNode
                for (int i=1; i<nodes.size(); i++) {
                    Node currentNode = nodes.get(i);
                    int edgeCount = currentNode.getEdgeCount();
                    for (int k=0; k<edgeCount; k++) {
                        Edge edge = currentNode.getEdgeAt(k);
                        Node neighNit = edge.getTarget();
                        int removedEdgeIndex = neighNit.removeEdge(currentNode);
                        assert(removedEdgeIndex >= 0);

                        Edge edgeToFirstNode = neighNit.findEdge(firstNode);
                        if (edgeToFirstNode == null) {
                            // create an edge neighNit -> refNode
                            neighNit.addEdge(firstNode, edge.getBoundary());
                            // create an edge refNode -> neighNit
                            firstNode.addEdge(neighNit, edge.getBoundary());
                        }
                    }
                    currentNode.setExpired(true);
                }

                IntSet borderCells = AbstractSegmenter.generateBorderCells(firstNode.getContour(), firstNode.getId(), imageWidth);
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridId = itCells.nextInt();
                    List<Node> resultNodes = borderPixelMap.get(gridId);
                    if (resultNodes != null) {
                        resultNodes.clear();
                        resultNodes.add(firstNode);
                    }
                }
            }
        }
    }


    public void removeUnstableSegments(ProcessingTile tile, int imageWidth) {
        int nodeCount = this.nodes.size();
        for (int i=0; i<nodeCount; i++) {
            NodeType node = this.nodes.get(i);
            BoundingBox box = node.getBox();
            if (box.getUpperLeftX() >= tile.columns[0] && box.getUpperLeftY() >= tile.rows[0] && box.getUpperRightX() - 1 <= tile.columns[1] && box.getLowerRightY() - 1 <= tile.rows[1]) {
                continue;
            } else if (box.getUpperLeftX() > tile.columns[1] || box.getUpperLeftY() > tile.rows[1] || box.getUpperRightX() - 1 < tile.columns[0]
                       || box.getLowerRightY() - 1 < tile.rows[0]) {
                node.setExpired(true);
                removeEdgeToUnstableNode(node);
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                boolean stable = false;
                IntIterator it = borderCells.iterator();
                while (it.hasNext()) {
                    int gridId = it.nextInt();
                    int rowPixel = gridId / imageWidth;
                    int colPixel = gridId % imageWidth;
                    if (rowPixel >= tile.rows[0] && rowPixel <= tile.rows[1] && colPixel >= tile.columns[0] && colPixel <= tile.columns[1]) {
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

    public void removeEdgeToUnstableNode(Node node) {
        int edgeCount = node.getEdgeCount();
        for (int j=0; j<edgeCount; j++) {
            Edge edge = node.getEdgeAt(j);
            Node nodeNeighbor = edge.getTarget();
            int removedEdgeIndex = nodeNeighbor.removeEdge(node);
            assert(removedEdgeIndex >= 0);
        }
    }

//    template<class TSegmenter>
//    void RemoveEdgeToUnstableNode(typename TSegmenter::NodePointerType nodePtr)
//    {
//        for(auto& edg : nodePtr->m_Edges)
//        {
//            auto nodeNeighbor = edg.GetRegion();
//            auto EdgeToNode = grm::GraphOperations<TSegmenter>::FindEdge(nodeNeighbor, nodePtr);
//            assert(EdgeToNode != nodeNeighbor->m_Edges.end());
//            nodeNeighbor->m_Edges.erase(EdgeToNode);
//        }
//    }

    private static class ArrayListExtended<ItemType> extends ArrayList<ItemType> {

        ArrayListExtended(int initialCapacity) {
            super(initialCapacity);
        }

        void removeItems(int fromIndex, int toIndex) {
            removeRange(fromIndex, toIndex);
        }
    }
}
