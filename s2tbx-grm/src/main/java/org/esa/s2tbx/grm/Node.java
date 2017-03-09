package org.esa.s2tbx.grm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jean Coravu
 */
public abstract class Node {
    private static final byte VALID_FLAG = 1;
    private static final byte EXPIRED_FLAG = 2;
    private static final byte MERGED_FLAG = 4;

    /**
     * Node is identified by the location of the first pixel of the region.
     */
    private final int id;
    private final List<Edge> edges;
    protected final float[] means;

    private int area;
    private int perimeter;
    private BoundingBox box;
    private Contour contour;

    private byte flags;

    protected Node(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        this.id = id;
        this.edges = new ArrayList<Edge>();
        this.means = new float[numberOfComponentsPerPixel];

        this.contour = new Contour();
        this.contour.pushRight();
        this.contour.pushBottom();
        this.contour.pushLeft();
        this.contour.pushTop();

        // merged = true => force to compute costs for the first iteration
        this.flags = VALID_FLAG | MERGED_FLAG;

        this.area = 1;
        this.perimeter = 4;
        this.box = new BoundingBox(upperLeftX, upperLeftY, 1, 1);
    }

    public abstract void updateSpecificAttributes(Node n2);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + this.hashCode() + "[id="+id+"]";
    }

    public void initData(int index, float pixel) {
        this.means[index] = pixel;
    }

    public final int getNumberOfComponentsPerPixel() {
        return this.means.length;
    }

    public final float getMeansAt(int index) {
        return this.means[index];
    }

    public boolean isMerged() {
        return ((this.flags & MERGED_FLAG) != 0);
    }

    public void setMerged(boolean merged) {
        if (merged) {
            this.flags = (byte)(this.flags | MERGED_FLAG);
        } else {
            this.flags = (byte)(this.flags & ~MERGED_FLAG);
        }
    }

    public boolean isExpired() {
        return ((this.flags & EXPIRED_FLAG) != 0);
    }

    public void setExpired(boolean expired) {
        if (expired) {
            this.flags = (byte)(this.flags | EXPIRED_FLAG);
        } else {
            this.flags = (byte)(this.flags & ~EXPIRED_FLAG);
        }
    }

    public boolean isValid() {
        return ((this.flags & VALID_FLAG) != 0);
    }

    public void setValid(boolean valid) {
        if (valid) {
            this.flags = (byte)(this.flags | VALID_FLAG);
        } else {
            this.flags = (byte)(this.flags & ~VALID_FLAG);
        }
    }

    public int getId() {
        return id;
    }

    public Contour getContour() {
        return contour;
    }

    public int getPerimeter() {
        return perimeter;
    }

    public int getArea() {
        return area;
    }

    public BoundingBox getBox() {
        return box;
    }

    public void addEdge(Node target, int boundary) {
        this.edges.add(new Edge(target, boundary));
    }

    public Edge getEdgeAt(int index) {
        return this.edges.get(index);
    }

    public int getEdgeCount() {
        return this.edges.size();
    }

    public void resetCostUpdatedFlagToAllEdges() {
        for (Edge edge : this.edges) {
            edge.setCostUpdated(false);
            Edge toNeigh = edge.getTarget().findEdge(this);
            toNeigh.setCostUpdated(false);
        }
    }

    public void swapEdges(int firstIndex, int secondIndex) {
        if (firstIndex < 0 || firstIndex >= this.edges.size()) {
            throw new IllegalArgumentException("The first index " + firstIndex + " is out of bounds. The maximum index is " + (this.edges.size()-1));
        }
        if (secondIndex < 0 || secondIndex >= this.edges.size()) {
            throw new IllegalArgumentException("The second index " + secondIndex + " is out of bounds. The maximum index is " + (this.edges.size()-1));
        }
        Edge auxEdge = this.edges.set(firstIndex, this.edges.get(secondIndex));
        this.edges.set(secondIndex, auxEdge);
    }

    /**
     * Check the local mutual best fitting.
     * @param threshold
     * @return
     */
    public Node checkLMBF(float threshold) {
        if (isValid() && this.edges.size() > 0) {
            Edge firstEdge = this.edges.get(0);
            if (firstEdge.getCost() < threshold) {
                Node firstEdgeTarget = firstEdge.getTarget();
                if (firstEdgeTarget.isValid()) {
                    Node bestNode = firstEdgeTarget.getEdgeAt(0).getTarget();
                    if (this == bestNode) { // the same node
                        if (this.id < firstEdgeTarget.id) {
                            return this;
                        }
                        return firstEdgeTarget;
                    }
                }
            }
        }
        return null;
    }

    public Edge findEdge(Node target) {
        for (int i = 0; i < this.edges.size(); i++) {
            Edge edge = this.edges.get(i);
            if (edge.getTarget() == target) {
                return edge;
            }
        }
        return null;
    }

    private Edge removeEdge(Node target) {
        for (int i = 0; i < this.edges.size(); i++) {
            Edge edge = this.edges.get(i);
            if (edge.getTarget() == target) {
                this.edges.remove(i);
                // if the edge targeting to node b is the first then the corresponding node is not valid anymore
                if (i == 0) {
                    setValid(false);
                }
                return edge;
            }
        }
        return null;
    }

    private void updateNeighbors(Node neighborToRemove) {
        // explore the neighbors of 'neighborToRemove'
        for (int i=0; i<neighborToRemove.getEdgeCount(); i++) {
            Edge currentEdge = neighborToRemove.getEdgeAt(i);
            // retrieve the edge targeting node 'neighborToRemove'
            Node targetNodeOfCurrentEdge = currentEdge.getTarget();
            targetNodeOfCurrentEdge.removeEdge(neighborToRemove);

            // keep in memory the boundary between node b and node neigh_b
            int boundary = currentEdge.getBoundary();

            if (targetNodeOfCurrentEdge != this) {
                // retrieve the edge targeting to node a
                Edge toThis = targetNodeOfCurrentEdge.findEdge(this);
                if (toThis == null) {
                    // no edge exists between node a and node neigh_b.

                    // add an edge from node neigh_b targeting node a.
                    targetNodeOfCurrentEdge.addEdge(this, boundary);

                    // add an edge from this node targeting node neigh_b.
                    addEdge(targetNodeOfCurrentEdge, boundary);
                } else {
                    // an edge exists between node a and node neigh_b.

                    // increment the boundary of the edge from node neigh_b targeting to node a.
                    toThis.setBoundary(toThis.getBoundary() + boundary);

                    // increment the boundary of the edge from node a targeting to node neigh_b.
                    Edge toNeighB = this.findEdge(targetNodeOfCurrentEdge);
                    toNeighB.setBoundary(toNeighB.getBoundary() + boundary);
                }
            }
        }
    }

    public final void updateInternalAttributes(Node targetNode, int imageWidth) {
        updateSpecificAttributes(targetNode);

        // first step consists of building the bounding box resulting from the fusion of the bounding boxes bbox1 and bbox2
        BoundingBox mergedBox = AbstractSegmenter.mergeBoundingBoxes(getBox(), targetNode.getBox());

        Contour mergedContour = AbstractSegmenter.mergeContour(mergedBox, getContour(), targetNode.getContour(), getId(), targetNode.getId(), imageWidth);

        // step 1: update the bounding box
        this.box = mergedBox;

        // step 2: update the contour
        this.contour = mergedContour;

        // step 2 : update perimeter and area attributes
        Edge toB = findEdge(targetNode);
        this.perimeter += (targetNode.getPerimeter() - (2 * toB.getBoundary()));
        this.area += targetNode.getArea();

        // step 2: update the neighborhood
        updateNeighbors(targetNode);

        // step 3: update the node' states
        setValid(false);
        setMerged(true);

        targetNode.setValid(false);
        targetNode.setExpired(true);
    }
}
