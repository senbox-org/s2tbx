package org.esa.s2tbx.grm;

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

    private static class ArrayListExtended<ItemType> extends ArrayList<ItemType> {

        ArrayListExtended(int initialCapacity) {
            super(initialCapacity);
        }

        void removeItems(int fromIndex, int toIndex) {
            removeRange(fromIndex, toIndex);
        }
    }
}
