package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.snap.utils.ArrayListExtended;

import java.lang.ref.WeakReference;

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
    private int id;
    private final ArrayListExtended<Edge> edges;
    protected final float[] means;

    private int area;
    private int perimeter;
    private BoundingBox box;
    private Contour contour;

    private byte flags;

    protected Node(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel) {
        this.id = id;
        this.edges = new ArrayListExtended<Edge>(0);
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

    protected Node(int id, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        this.id = id;
        this.edges = new ArrayListExtended<Edge>(0);
        this.means = new float[numberOfComponentsPerPixel];

        this.contour = contour;

        // merged = true => force to compute costs for the first iteration
        this.flags = VALID_FLAG | MERGED_FLAG;

        this.area = area;
        this.perimeter = perimeter;
        this.box = box;
    }

    public abstract void updateSpecificAttributes(Node n2);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + this.hashCode() + "[id=" + id + "]";
    }

    public void initData(int index, float pixel) {
        this.means[index] = pixel;
    }

    public final int getNumberOfComponentsPerPixel() {
        return this.means.length;
    }

    public final void setMeansAt(int index, float value) {
        this.means[index] = value;
    }

    public final float getMeansAt(int index) {
        return this.means[index];
    }

    public boolean isMerged() {
        return ((this.flags & MERGED_FLAG) != 0);
    }

    public void setMerged(boolean merged) {
        if (merged) {
            this.flags = (byte) (this.flags | MERGED_FLAG);
        } else {
            this.flags = (byte) (this.flags & ~MERGED_FLAG);
        }
    }

    public boolean isExpired() {
        return ((this.flags & EXPIRED_FLAG) != 0);
    }

    public void setExpired(boolean expired) {
        if (expired) {
            this.flags = (byte) (this.flags | EXPIRED_FLAG);
        } else {
            this.flags = (byte) (this.flags & ~EXPIRED_FLAG);
        }
    }

    public boolean isValid() {
        return ((this.flags & VALID_FLAG) != 0);
    }

    public void setValid(boolean valid) {
        if (valid) {
            this.flags = (byte) (this.flags | VALID_FLAG);
        } else {
            this.flags = (byte) (this.flags & ~VALID_FLAG);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        int edgeCount = this.edges.size();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = this.edges.get(i);
            edge.setCostUpdated(false);
            Edge toNeigh = edge.getTarget().findEdge(this);
            toNeigh.setCostUpdated(false);
        }
    }

    public void swapEdges(int firstIndex, int secondIndex) {
        if (firstIndex < 0 || firstIndex >= this.edges.size()) {
            throw new IllegalArgumentException("The first index " + firstIndex + " is out of bounds. The maximum index is " + (this.edges.size() - 1));
        }
        if (secondIndex < 0 || secondIndex >= this.edges.size()) {
            throw new IllegalArgumentException("The second index " + secondIndex + " is out of bounds. The maximum index is " + (this.edges.size() - 1));
        }
        Edge auxEdge = this.edges.set(firstIndex, this.edges.get(secondIndex));
        this.edges.set(secondIndex, auxEdge);
    }

    /**
     * Check the local mutual best fitting.
     *
     * @param threshold
     * @return
     */
    public Node checkLMBF(float threshold) {
        if (isValid() && this.edges.size() > 0) {
            Edge firstEdge = this.edges.get(0);
            if (firstEdge.getCost() < threshold) {
                Node firstEdgeTarget = firstEdge.getTarget();
                if (firstEdgeTarget.isValid() && firstEdgeTarget.getEdgeCount() > 0) {
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
        int edgeCount = this.edges.size();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = this.edges.get(i);
            if (edge.getTarget() == target) {
                return edge;
            }
        }
        return null;
    }

    public int removeEdge(Node target) {
        int edgeCount = this.edges.size();
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = this.edges.get(i);
            if (edge.getTarget() == target) {
                // found the edge to the target node
                this.edges.remove(i);
                WeakReference<Edge> reference = new WeakReference<Edge>(edge);
                reference.clear();
                return i;
            }
        }
        return -1; // -1 => no edge removed
    }

    public final void removeEdgeToUnstableNode() {
        int edgeCount = this.edges.size();
        for (int j = 0; j < edgeCount; j++) {
            Edge edge = this.edges.get(j);
            Node nodeNeighbor = edge.getTarget();
            int removedEdgeIndex = nodeNeighbor.removeEdge(this);
            assert (removedEdgeIndex >= 0);
        }
    }

    public void doClose() {
        int edgeCount = this.edges.size();
        for (int j = 0; j < edgeCount; j++) {
            Edge edge = this.edges.get(j);
            if (this != edge.getTarget()) {
                // the target node is different
                edge.getTarget().removeEdge(this);
            }
            WeakReference<Edge> reference = new WeakReference<Edge>(edge);
            reference.clear();
        }
        this.edges.clearItems(); // remove all the edges
        WeakReference<ArrayListExtended<Edge>> reference = new WeakReference<ArrayListExtended<Edge>>(this.edges);
        reference.clear();
    }

    private void updateNeighbors(Node neighborToRemove) {
        // explore the neighbors of 'neighborToRemove'
        for (int i = 0; i < neighborToRemove.getEdgeCount(); i++) {
            Edge currentEdge = neighborToRemove.getEdgeAt(i);
            // retrieve the edge targeting node 'neighborToRemove'
            Node targetNodeOfCurrentEdge = currentEdge.getTarget();
            int removedEdgeIndex = targetNodeOfCurrentEdge.removeEdge(neighborToRemove);
            // if the edge targeting to node b is the first then the corresponding node is not valid anymore
            if (removedEdgeIndex == 0) {
                targetNodeOfCurrentEdge.setValid(false);
            }

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
                    Edge toNeighB = findEdge(targetNodeOfCurrentEdge);
                    toNeighB.setBoundary(toNeighB.getBoundary() + boundary);
                }
            }
        }
    }

    public final void updateInternalAttributes(Node targetNode, int imageWidth) {
        updateSpecificAttributes(targetNode);

        // first step consists of building the bounding box resulting from the fusion of the bounding boxes bbox1 and bbox2
        BoundingBox mergedBox = AbstractSegmenter.mergeBoundingBoxes(getBox(), targetNode.getBox());

        Contour mergedContour = mergeContour(mergedBox, getContour(), targetNode.getContour(), getId(), targetNode.getId(), imageWidth);

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

    private static Contour createNewContour(int nodeId, IntSet borderCells, int boxWidth, int boxHeight) {
        Contour newContour = new Contour();
        // the first move is always to the right
        newContour.pushRight(); //Push1(newContour);

        // previous move is to the right
        int currentMoveId = Contour.RIGHT_MOVE_INDEX;

        // local pixel id
        int currentNodeId = nodeId;

        // table containing id neighbors
        int[] neighbors = new int[8];
        while (true) {
            // compute neighbor' ids
            AbstractSegmenter.generateEightNeighborhood(neighbors, currentNodeId, boxWidth, boxHeight);

            if (currentMoveId == Contour.RIGHT_MOVE_INDEX) { // 1 => move to the right
                if (neighbors[1] != -1 && borderCells.contains(neighbors[1])) { // array index = 1 => top right
                    newContour.pushTop(); //Push0(newContour);
                    currentNodeId = currentNodeId - boxWidth + 1;
                    currentMoveId = Contour.TOP_MOVE_INDEX; // 0 => move to the top
                } else if (neighbors[2] != -1 && borderCells.contains(neighbors[2])) { // array index = 2 => right
                    newContour.pushRight(); //Push1(newContour);
                    currentNodeId++;
                    currentMoveId = Contour.RIGHT_MOVE_INDEX; // 1 => move to the right
                } else {
                    newContour.pushBottom(); //Push2(newContour);
                    currentMoveId = Contour.BOTTOM_MOVE_INDEX; // 2 => move to the bottom
                }
            } else if (currentMoveId == Contour.BOTTOM_MOVE_INDEX) { // 2 => move to the bottom
                if (neighbors[3] != -1 && borderCells.contains(neighbors[3])) { // array index = 3 => bottom right
                    newContour.pushRight(); //Push1(newContour);
                    currentNodeId = currentNodeId + boxWidth + 1;
                    currentMoveId = Contour.RIGHT_MOVE_INDEX; // 1 => move to the right
                } else if (neighbors[4] != -1 && borderCells.contains(neighbors[4])) { // array index = 4 => right
                    newContour.pushBottom(); //Push2(newContour);
                    currentNodeId += boxWidth;
                    currentMoveId = Contour.BOTTOM_MOVE_INDEX; // 2 => move to the bottom
                } else {
                    newContour.pushLeft(); //Push3(newContour);
                    currentMoveId = Contour.LEFT_MOVE_INDEX; // 3 => move to the left
                }
            } else if (currentMoveId == Contour.LEFT_MOVE_INDEX) { // 3 => move to the left
                if (neighbors[5] != -1 && borderCells.contains(neighbors[5])) { // array index = 5 => bottom left
                    newContour.pushBottom(); //Push2(newContour);
                    currentNodeId = currentNodeId + boxWidth - 1;
                    currentMoveId = Contour.BOTTOM_MOVE_INDEX; // 2 => move to the bottom
                } else if (neighbors[6] != -1 && borderCells.contains(neighbors[6])) { // array index = 6 => left
                    newContour.pushLeft(); //Push3(newContour);
                    currentNodeId--;
                    currentMoveId = Contour.LEFT_MOVE_INDEX; // 3 => move to the left
                } else {
                    newContour.pushTop(); //Push0(newContour);
                    currentMoveId = Contour.TOP_MOVE_INDEX; // 0 => move to the top
                }
            } else { // previous move = 0 => move to the top
                assert (currentMoveId == Contour.TOP_MOVE_INDEX);

                if (neighbors[7] != -1 && borderCells.contains(neighbors[7])) { // array index = 7 => top left
                    newContour.pushLeft(); //Push3(newContour);
                    currentNodeId = currentNodeId - boxWidth - 1;
                    currentMoveId = Contour.LEFT_MOVE_INDEX; // 3 => move to the left
                } else if (neighbors[0] != -1 && borderCells.contains(neighbors[0])) { // array index = 0 => top
                    newContour.pushTop(); //Push0(newContour);
                    currentNodeId -= boxWidth;
                    currentMoveId = Contour.TOP_MOVE_INDEX; // 0 => move to the top
                } else {
                    if (currentNodeId == nodeId) {
                        break; // exit the loop
                    } else {
                        newContour.pushRight(); //Push1(newContour);
                        currentMoveId = Contour.RIGHT_MOVE_INDEX; // 1 => move to the right
                    }
                }
            }
        }
        return newContour;
    }

    private static Contour mergeContour(BoundingBox mergedBox, Contour contour1, Contour contour2, int nodeId1, int nodeId2, int imageWidth) {
        // fill the cell matrix with the cells from both contours
        IntSet borderCells = new IntOpenHashSet();
        // fill with the cells of contour 1
        generateBorderCellsForContourFusion(borderCells, contour1, nodeId1, imageWidth, mergedBox);
        // fill with the cells of contour 2
        generateBorderCellsForContourFusion(borderCells, contour2, nodeId2, imageWidth, mergedBox);
        // create the new contour
        int id = gridToBBox(nodeId1, mergedBox, imageWidth);
        return createNewContour(id, borderCells, mergedBox.getWidth(), mergedBox.getHeight());
    }

    private static void generateBorderCellsForContourFusion(IntSet outputBorderCells, Contour contour, int startCellId, int width, BoundingBox mergedBox) {
        // add the first pixel to the border list
        outputBorderCells.add(gridToBBox(startCellId, mergedBox, width));

        if (contour.hasBorderSize()) { // contour size > 8 => more then 4 moves
            // initialize the first move at previous index
            int previousMoveId = contour.getMove(0);

            // declare the current pixel index
            int currentCellId = startCellId;

            // explore the contour
            int contourSize = contour.computeContourBorderSize();
            for (int contourIndex = 1; contourIndex < contourSize; contourIndex++) {
                int currentMoveId = contour.getMove(contourIndex);
//                assert (currentMoveId >= 0 && currentMoveId <= 3);
//
//                if (currentMoveId == Contour.TOP_MOVE_INDEX) { // top
//                    // impossible case is previous index = 2 (bottom)
//                    assert (previousMoveId != Contour.BOTTOM_MOVE_INDEX);
//
//                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
//                        currentCellId -= width; // go to the top
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    } else if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
//                        currentCellId = currentCellId - width + 1; // go to the top right
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    }
//                } else if (currentMoveId == Contour.RIGHT_MOVE_INDEX) { // right
//                    // impossible case is previous index = 3 (left)
//                    assert (previousMoveId != Contour.LEFT_MOVE_INDEX);
//
//                    if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
//                        currentCellId++; // go to the right
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    } else if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
//                        currentCellId = currentCellId + width + 1; // go to the bottom right
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    }
//                } else if (currentMoveId == Contour.BOTTOM_MOVE_INDEX) { // bottom
//                    // impossible case is previous index = 0 (top)
//                    assert (previousMoveId != Contour.TOP_MOVE_INDEX);
//
//                    if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
//                        currentCellId += width;
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
//                        currentCellId = currentCellId + width - 1; // go to the bottom left
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    }
//                } else { // current index = 3 (left)
//                    // impossible case is previous index = 1 (right)
//                    assert (previousMoveId != Contour.RIGHT_MOVE_INDEX);
//
//                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
//                        currentCellId = currentCellId - width - 1; // go to the top left
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
//                        currentCellId--; // go the to left
//                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
//                    }
//                }

                int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, width);
                if (nextCellId != currentCellId) {
                    currentCellId = nextCellId;
                    outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                }

                previousMoveId = currentMoveId;
            }
        }
    }

    private static int gridToBBox(int gridId, BoundingBox bbox, int gridWidth) {
        int gridX = gridId % gridWidth;
        int gridY = gridId / gridWidth;

        int bbX = gridX - bbox.getLeftX();
        int bbY = gridY - bbox.getTopY();

        return bbY * bbox.getWidth() + bbX;
    }
}


