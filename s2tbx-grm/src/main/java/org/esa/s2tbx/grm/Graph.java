package org.esa.s2tbx.grm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Coravu
 */
public class Graph<NodeType extends Node> {
    private final List<NodeType> nodes;

    public Graph(int numberOfNodes) {
        this.nodes = new ArrayList<NodeType>(numberOfNodes);
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
        for (int k = this.nodes.size() - 1; k >= 0; k--) {
            NodeType node = this.nodes.get(k);
            if (node.isExpired()) {
                this.nodes.remove(k);
            }
        }
        return this.nodes.size();
    }

    public void setValidFlagToAllNodes() {
        for (int i=0; i<this.nodes.size(); i++) {
            NodeType n = this.nodes.get(i);
            n.setValid(true);
        }
    }

    public void resetMergedFlagToAllNodes() {
        for (int i=0; i<this.nodes.size(); i++) {
            NodeType n = this.nodes.get(i);
            n.setMerged(false);
        }
    }

    public void resetCostUpdatedFlagToAllEdges() {
        for (int i=0; i<this.nodes.size(); i++) {
            NodeType n = this.nodes.get(i);
            for (int j=0; j<n.getEdgeCount(); j++) {
                Edge edge = n.getEdgeAt(j);
                edge.setCostUpdated(false);
            }
        }
    }
}
