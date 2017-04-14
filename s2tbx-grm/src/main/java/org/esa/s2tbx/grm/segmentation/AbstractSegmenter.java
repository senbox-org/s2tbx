package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.Tile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractSegmenter {
    private static final Logger logger = Logger.getLogger(AbstractSegmenter.class.getName());

    protected final float threshold;

    private Graph graph;
    private int imageWidth;
    private int imageHeight;

    protected AbstractSegmenter(float threshold) {
        this.threshold = threshold;
    }

    protected abstract float computeMergingCost(Node n1, Node n2);

    protected abstract Node buildNode(int id, int upperLeftX, int upperLeftY, int numberOfComponentsPerPixel);

    public final boolean update(Tile[] sourceTiles, BoundingBox rectange, int numberOfIterations, boolean fastSegmentation, boolean addFourNeighbors) {
        initNodes(sourceTiles, rectange, addFourNeighbors);

        boolean merged = false;
        if (fastSegmentation) {
            merged = performAllIterationsWithBF(numberOfIterations);
        } else {
            merged = performAllIterationsWithLMBF(numberOfIterations);
        }

        return !merged;
    }

    public void setGraph(Graph graph, int imageWidth, int imageHeight) {
        this.graph = graph;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public final void fillBandData(Band targetBand) {
        if (targetBand.getRasterWidth() != this.imageWidth) {
            throw new IllegalArgumentException("Different band width.");
        }
        if (targetBand.getRasterHeight() != this.imageHeight) {
            throw new IllegalArgumentException("Different band height.");
        }
        int widthCount = this.imageWidth + 2;
        int heightCount = this.imageHeight + 2;
        int[][] marker = buildMarkerMatrix();

        int dataType = targetBand.getDataType();
        ProductData data = targetBand.createCompatibleRasterData();
        targetBand.setData(data);
        for (int y = 1; y < heightCount - 1; y++) {
            for (int x = 1; x < widthCount - 1; x++) {
                if (dataType == ProductData.TYPE_INT32) {
                    targetBand.setPixelInt(x - 1, y - 1, marker[y][x]);
                } else if (dataType == ProductData.TYPE_FLOAT32) {
                    targetBand.setPixelFloat(x - 1, y - 1, marker[y][x]);
                } else if (dataType == ProductData.TYPE_FLOAT64) {
                    targetBand.setPixelDouble(x - 1, y - 1, marker[y][x]);
                } else {
                    throw new IllegalArgumentException("Unknown band data type " + dataType + ".");
                }
            }
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public boolean performAllIterationsWithLMBF(int numberOfIterations) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "");
            logger.log(Level.FINER, "Perform iterations with LMBF: number of iterations: "+numberOfIterations);
        }
        int iterations = 0;
        boolean merged = true;
        while (merged && (this.graph.getNodeCount() > 1) && (numberOfIterations <= 0 || iterations < numberOfIterations)) {
            iterations++;
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Iterations with LMBF: iteration: " + iterations + ", graph node count: " +this.graph.getNodeCount()+", number of iterations: "+numberOfIterations);
            }
            merged = perfomOneIterationWithLMBF();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Iterations with LMBF: after segmentation graph node count: " +this.graph.getNodeCount());
        }
        return merged;
    }

    private boolean performAllIterationsWithBF(int numberOfIterations) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "");
            logger.log(Level.FINER, "Perform iterations with BF: number of iterations: "+numberOfIterations);
        }
        int iterations = 0;
        boolean merged = true;
        while (merged && (this.graph.getNodeCount() > 1) && (numberOfIterations <= 0 || iterations < numberOfIterations)) {
            iterations++;
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Iterations with BF: iteration: " + iterations + ", graph node count: " +this.graph.getNodeCount()+", number of iterations: "+numberOfIterations);
            }
            merged = perfomOneIterationWithBF();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Iterations with BF: after segmentation graph node count: " +this.graph.getNodeCount());
        }
        return merged;
    }

    private boolean perfomOneIterationWithBF() {
        boolean merged = false;

        for (int i = 0; i < this.graph.getNodeCount(); i++) {
            Node currentNode = this.graph.getNodeAt(i);
            if (currentNode.isValid()) {
                // this segment is marked as used
                currentNode.setValid(false);

                // compute cost with all its neighbors
                updateMergingCostsUsingBF(currentNode);

                // get the most similar segment
                Edge firstEdge = currentNode.getEdgeAt(0);
                Node firstEdgeTarget = firstEdge.getTarget();

                if (firstEdge.getCost() < this.threshold && !firstEdgeTarget.isExpired()) {
                    merged = true;

                    Node nodeToUpdate = null;
                    Node targetNode = null;
                    if (currentNode.getId() < firstEdgeTarget.getId()) {
                        nodeToUpdate = currentNode;
                        targetNode = firstEdgeTarget;
                    } else {
                        nodeToUpdate = firstEdgeTarget;
                        targetNode = currentNode;
                    }
                    nodeToUpdate.updateInternalAttributes(targetNode, this.imageWidth);
                    nodeToUpdate.resetCostUpdatedFlagToAllEdges();
                }
            }
        }

        int remainingNodes = this.graph.removeExpiredNodes();
        if (remainingNodes < 2) {
            return false;
        }
        this.graph.setValidFlagToAllNodes();

        return merged;
    }

    private void updateMergingCostsUsingBF(Node node) {
        float minimumCost = Float.MAX_VALUE;
        int minimumIndex = -1;
        for (int i = 0; i < node.getEdgeCount(); i++) {
            Edge edge = node.getEdgeAt(i);
            // compute the cost if the neighbor is not expired and the cost has to be updated
            if (!edge.getTarget().isExpired()) {
                Node neighborR = edge.getTarget();

                // compute the cost if necessary
                if (!edge.isCostUpdated()) {
                    float mergingCost = computeMergingCost(node, neighborR);
                    edge.setCost(mergingCost);
                    edge.setCostUpdated(true);

                    Edge edgeFromNeighborToR = neighborR.findEdge(node);
                    edgeFromNeighborToR.setCost(mergingCost);
                    edgeFromNeighborToR.setCostUpdated(true);
                }

                // check if the cost of the edge is the minimum
                if (minimumCost > edge.getCost()) {
                    minimumCost = edge.getCost();
                    minimumIndex = i;
                }
            }
        }
        if (minimumIndex > 0) {
            node.swapEdges(0, minimumIndex);
        }
    }

    private void initNodes(Tile[] sourceTiles, BoundingBox rectange, boolean addFourNeighbors) {
        this.imageWidth = rectange.getWidth();
        this.imageHeight = rectange.getHeight();

        int numberOfComponentsPerPixel = sourceTiles.length;
        int numberOfNodes = this.imageWidth * this.imageHeight;
        this.graph = new Graph();

        for (int i = 0; i < numberOfNodes; i++) {
            int upperLeftX = i % this.imageWidth;
            int upperLeftY = i / this.imageWidth;
            Node node = buildNode(i, upperLeftX, upperLeftY, numberOfComponentsPerPixel);
            this.graph.addNode(node);
        }

        int neighborCount = addFourNeighbors ? 4 : 8;
        int[] neighborhood = new int[neighborCount];
        for (int i = 0; i < numberOfNodes; i++) {
            Node node = this.graph.getNodeAt(i);

            if (addFourNeighbors) {
                generateFourNeighborhood(neighborhood, node.getId(), this.imageWidth, this.imageHeight);
            } else {
                generateEightNeighborhood(neighborhood, node.getId(), this.imageWidth, this.imageHeight);
            }
            for (int j = 0; j < neighborhood.length; j++) {
                int neighbourNodeIndex = neighborhood[j];
                if (neighbourNodeIndex > -1) {
                    Node neighbourNode = this.graph.getNodeAt(neighbourNodeIndex);
                    node.addEdge(neighbourNode, 1);
                }
            }

            int x = rectange.getLeftX() + (node.getId() % this.imageWidth);
            int y = rectange.getTopY() + (node.getId() / this.imageWidth);
            for (int b = 0; b < sourceTiles.length; b++) {
                float pixel = sourceTiles[b].getSampleFloat(x, y);
                node.initData(b, pixel);
            }
        }
    }

    private int[][] buildMarkerMatrix() {
        int widthCount = this.imageWidth + 2;
        int heightCount = this.imageHeight + 2;
        int[][] mask = new int[heightCount][widthCount];
        int[][] marker = new int[heightCount][widthCount];

        int nodeCount = this.graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = this.graph.getNodeAt(i);
            IntSet borderCells = generateBorderCells(node.getContour(), node.getId(), this.imageWidth);
            IntIterator itCells = borderCells.iterator();
            while (itCells.hasNext()) {
                int gridId = itCells.nextInt();
                int gridX = gridId % this.imageWidth;
                int gridY = gridId / this.imageWidth;
                mask[gridY + 1][gridX + 1] = i + 1;
            }
        }
        // fill the first and the last rows (the top and the bottom rows) in the mask matrix
        for (int x = 0; x < widthCount; x++) {
            mask[0][x] = nodeCount + 1;
            mask[heightCount - 1][x] = nodeCount + 1;
        }
        // fill the first and the last columns (the left and the right columns) in the mask matrix
        for (int y = 0; y < heightCount; y++) {
            mask[y][0] = nodeCount + 1;
            mask[y][widthCount - 1] = nodeCount + 1;
        }

        // copy the first two rows and the last two rows in the marker matrix
        for (int x = 0; x < widthCount; x++) {
            marker[0][x] = mask[0][x];
            marker[1][x] = mask[1][x];
            marker[heightCount - 2][x] = mask[heightCount - 2][x];
            marker[heightCount - 1][x] = mask[heightCount - 1][x];
        }
        // fill the first two columns and the last two columns in the marker matrix
        for (int y = 0; y < heightCount; y++) {
            marker[y][0] = mask[y][0];
            marker[y][1] = mask[y][1];
            marker[y][widthCount - 2] = mask[y][widthCount - 2];
            marker[y][widthCount - 1] = mask[y][widthCount - 1];
        }
        // fill the center of the marker matrix
        for (int y = 2; y < heightCount - 1; y++) {
            for (int x = 2; x < widthCount - 1; x++) {
                marker[y][x] = nodeCount;
            }
        }

        // first step to compute the values in the marker matrix
        for (int y = 1; y < heightCount; y++) {
            for (int x = 1; x < widthCount; x++) {
                int pixel = marker[y][x];
                int leftPixel = marker[y][x - 1];
                int topPixel = marker[y - 1][x];
                int value = Math.min(Math.min(leftPixel, topPixel), pixel);
                marker[y][x] = Math.max(value, mask[y][x]);
            }
        }

        // second step to compute the values in the marker matrix
        IntPriorityQueue queue = new IntArrayFIFOQueue();
        for (int y = heightCount - 2; y > 0; y--) {
            for (int x = widthCount - 2; x > 0; x--) {
                int markerCurrentPixel = marker[y][x];
                int rightPixel = marker[y][x + 1];
                int bottomPixel = marker[y + 1][x];
                int value = Math.min(Math.min(rightPixel, bottomPixel), markerCurrentPixel);
                markerCurrentPixel = Math.max(value, mask[y][x]);
                marker[y][x] = markerCurrentPixel;

                if ((bottomPixel > markerCurrentPixel && bottomPixel > mask[y + 1][x]) || (rightPixel > markerCurrentPixel && rightPixel > mask[y][x + 1])) {
                    queue.enqueue(convertPointToId(x, y, widthCount));
                }
            }
        }

        // the third step to compute the values in the marker matrix
        while (!queue.isEmpty()) {
            int id = queue.dequeueInt();
            int x = id % widthCount;
            int y = id / widthCount;
            int markerCurrentPixel = marker[y][x];

            addToQueue(marker, mask, markerCurrentPixel, x, y - 1, widthCount, queue); // top
            addToQueue(marker, mask, markerCurrentPixel, x + 1, y, widthCount, queue); // right
            addToQueue(marker, mask, markerCurrentPixel, x, y + 1, widthCount, queue); // bottom
            addToQueue(marker, mask, markerCurrentPixel, x - 1, y, widthCount, queue); // left
        }

        return marker;
    }

    private void updateMergingCostsUsingLMBF() {
        this.graph.resetCostUpdatedFlagToAllEdges();

        int nodeCount = this.graph.getNodeCount();
        int minimumId = 0;
        for (int k = 0; k < nodeCount; k++) {
            Node node = this.graph.getNodeAt(k);
            float minimumCost = Float.MAX_VALUE;
            int minimumIndex = -1;

            node.setExpired(false);
            node.setValid(true);

            for (int i = 0; i < node.getEdgeCount(); i++) {
                Edge edge = node.getEdgeAt(i);
                Node neighborNode = edge.getTarget();

                // compute the cost if necessary
                if (!edge.isCostUpdated() && (neighborNode.isMerged() || node.isMerged())) {
                    float merginCost = computeMergingCost(node, neighborNode);
                    edge.setCost(merginCost);
                    edge.setCostUpdated(true);

                    Edge edgeFromNeighborToR = neighborNode.findEdge(node);
                    edgeFromNeighborToR.setCost(merginCost);
                    edgeFromNeighborToR.setCostUpdated(true);
                }

                // check if the cost of the edge is the minimum
                if (minimumCost > edge.getCost()) {
                    minimumCost = edge.getCost();
                    minimumId = neighborNode.getId();
                    minimumIndex = i;
                } else if (minimumCost == edge.getCost()) {
                    if (minimumId > neighborNode.getId()) {
                        minimumId = neighborNode.getId();
                        minimumIndex = i;
                    }
                }
            }
            if (minimumIndex > 0) {
                node.swapEdges(0, minimumIndex);
            }
        }

        this.graph.resetMergedFlagToAllNodes(); // reset the merge flag for all the regions.
    }

    private boolean perfomOneIterationWithLMBF() {
        updateMergingCostsUsingLMBF(); // update the costs of merging between adjacent nodes

        int nodeCount = this.graph.getNodeCount();
        boolean merged = false;
        for (int k = 0; k < nodeCount; k++) {
            Node node = this.graph.getNodeAt(k);
            Node resultNode = node.checkLMBF(this.threshold);
            if (resultNode != null) {
                Node firstEdgeTarget = resultNode.getEdgeAt(0).getTarget();
                resultNode.updateInternalAttributes(firstEdgeTarget, this.imageWidth);
                merged = true;
            }
        }
        int remainingNodes = this.graph.removeExpiredNodes();
        if (remainingNodes < 2) {
            return false;
        }
        return merged;
    }

    private static void addToQueue(int[][] marker, int[][] mask, int markerCurrentPixel, int x, int y, int widthCount, IntPriorityQueue queue) {
        int markerNeighborPixel = marker[y][x];
        int maskNeighborPixel = mask[y][x];
        if (markerNeighborPixel > markerCurrentPixel && markerNeighborPixel != maskNeighborPixel) {
            marker[y][x] = Math.max(markerCurrentPixel, maskNeighborPixel);
            queue.enqueue(convertPointToId(x, y, widthCount));
        }
    }

    private static int convertPointToId(int x, int y, int width) {
        return (y * width) + x;
    }

    private static int gridToBBox(int gridId, BoundingBox bbox, int gridWidth) {
        int gridX = gridId % gridWidth;
        int gridY = gridId / gridWidth;

        int bbX = gridX - bbox.getLeftX();
        int bbY = gridY - bbox.getTopY();

        return bbY * bbox.getWidth() + bbX;
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
        for (; ;) {
            // compute neighbor' ids
            generateEightNeighborhood(neighbors, currentNodeId, boxWidth, boxHeight);

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

    public static Contour mergeContour(BoundingBox mergedBox, Contour contour1, Contour contour2, int nodeId1, int nodeId2, int imageWidth) {
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
        int id = gridToBBox(startCellId, mergedBox, width);
        outputBorderCells.add(id);

        if (contour.size() > 8) { // contour size > 8 => more then 4 moves
            // initialize the first move at previous index
            int previousMoveId = contour.getMove(0);

            // declare the current pixel index
            int currentCellId = startCellId;

            // explore the contour
            for (int contourIndex = 1; contourIndex < contour.size() / 2; contourIndex++) {
                int currentMoveId = contour.getMove(contourIndex);
                assert (currentMoveId >= 0 && currentMoveId <= 3);

                if (currentMoveId == Contour.TOP_MOVE_INDEX) { // top
                    // impossible case is previous index = 2 (bottom)
                    assert (previousMoveId != Contour.BOTTOM_MOVE_INDEX);

                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
                        currentCellId -= width; // go to the top
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    } else if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
                        currentCellId = currentCellId - width + 1; // go to the top right
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    }
                } else if (currentMoveId == Contour.RIGHT_MOVE_INDEX) { // right
                    // impossible case is previous index = 3 (left)
                    assert (previousMoveId != Contour.LEFT_MOVE_INDEX);

                    if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
                        currentCellId++; // go to the right
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    } else if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
                        currentCellId = currentCellId + width + 1; // go to the bottom right
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    }
                } else if (currentMoveId == Contour.BOTTOM_MOVE_INDEX) { // bottom
                    // impossible case is previous index = 0 (top)
                    assert (previousMoveId != Contour.TOP_MOVE_INDEX);

                    if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
                        currentCellId += width;
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
                        currentCellId = currentCellId + width - 1; // go to the bottom left
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    }
                } else { // current index = 3 (left)
                    // impossible case is previous index = 1 (right)
                    assert (previousMoveId != Contour.RIGHT_MOVE_INDEX);

                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
                        currentCellId = currentCellId - width - 1; // go to the top left
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
                        currentCellId--; // go the to left
                        outputBorderCells.add(gridToBBox(currentCellId, mergedBox, width));
                    }
                }
                previousMoveId = currentMoveId;
            }
        }
    }

    public static BoundingBox mergeBoundingBoxes(BoundingBox bb1, BoundingBox bb2) {
        int minimumLeftUpperX = Math.min(bb1.getLeftX(), bb2.getLeftX());
        int minimumLeftUpperY = Math.min(bb1.getTopY(), bb2.getTopY());
        int maximumWidth = Math.max(bb1.getLeftX() + bb1.getWidth(), bb2.getLeftX() + bb2.getWidth());
        int maximummHeight = Math.max(bb1.getTopY() + bb1.getHeight(), bb2.getTopY() + bb2.getHeight());

        int width = maximumWidth - minimumLeftUpperX;
        int height = maximummHeight - minimumLeftUpperY;
        return new BoundingBox(minimumLeftUpperX, minimumLeftUpperY, width, height);
    }

    public static IntSet generateBorderCells(Contour contour, int startCellId, int width) {
        IntSet borderCells = new IntOpenHashSet();

        // add the first pixel to the border list
        borderCells.add(startCellId);

        if (contour.size() > 8) {
            // initialize the first move at prev
            int previousMoveId = contour.getMove(0);

            // declare the current pixel index
            int currentCellId = startCellId;

            // Explore the contour
            for (int contourIndex = 1; contourIndex < contour.size() / 2; contourIndex++) {
                int currentMoveId = contour.getMove(contourIndex);
                assert (currentMoveId >= 0 && currentMoveId <= 3);

                if (currentMoveId == Contour.TOP_MOVE_INDEX) { // top
                    // impossible case is previous index = 2 (bottom)
                    assert (previousMoveId != Contour.BOTTOM_MOVE_INDEX);

                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
                        currentCellId -= width; // go to the top
                        borderCells.add(currentCellId);
                    } else if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
                        currentCellId = currentCellId - width + 1; // go to the top right
                        borderCells.add(currentCellId);
                    }
                } else if (currentMoveId == Contour.RIGHT_MOVE_INDEX) { // right
                    // impossible case is previous index = 3 (left)
                    assert (previousMoveId != Contour.LEFT_MOVE_INDEX);

                    if (previousMoveId == Contour.RIGHT_MOVE_INDEX) {
                        currentCellId++; // go to the right
                        borderCells.add(currentCellId);
                    } else if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
                        currentCellId = currentCellId + width + 1; // go to the bottom right
                        borderCells.add(currentCellId);
                    }
                } else if (currentMoveId == Contour.BOTTOM_MOVE_INDEX) { // bottom
                    // impossible case is previous index = 0 (top)
                    assert (previousMoveId != Contour.TOP_MOVE_INDEX);

                    if (previousMoveId == Contour.BOTTOM_MOVE_INDEX) {
                        currentCellId += width;
                        borderCells.add(currentCellId);
                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
                        currentCellId = currentCellId + width - 1; // go to the bottom left
                        borderCells.add(currentCellId);
                    }
                } else { // current index = 3 (left)
                    // impossible case is previous index = 1 (right)
                    assert (previousMoveId != Contour.RIGHT_MOVE_INDEX);

                    if (previousMoveId == Contour.TOP_MOVE_INDEX) {
                        currentCellId = currentCellId - width - 1;  // go to the top left
                        borderCells.add(currentCellId);
                    } else if (previousMoveId == Contour.LEFT_MOVE_INDEX) {
                        currentCellId--; // go the to left
                        borderCells.add(currentCellId);
                    }
                }

                previousMoveId = currentMoveId;
            }
        }
        return borderCells;
    }

    private static void generateEightNeighborhood(int[] neighborhood, int id, int width, int height) {
        int x = id % width;
        int y = id / width;

        neighborhood[0] = (y > 0 ? (id - width) : -1); // top
        neighborhood[1] = ((y > 0 && x < (width - 1)) ? (id - width + 1) : -1); // top right
        neighborhood[2] = (x < (width - 1) ? (id + 1) : -1); // right
        neighborhood[3] = ((x < (width - 1) && y < (height - 1)) ? (id + 1 + width) : -1); // bottom right
        neighborhood[4] = (y < (height - 1) ? (id + width) : -1); // bottom
        neighborhood[5] = ((y < (height - 1) && x > 0) ? (id + width - 1) : -1); // bottom left
        neighborhood[6] = (x > 0 ? (id - 1) : -1); // left
        neighborhood[7] = ((x > 0 && y > 0) ? (id - width - 1) : -1); // top left
    }

    public static void generateFourNeighborhood(int[] neighborhood, int id, int width, int height) {
        int x = id % width;
        int y = id / width;

        neighborhood[0] = (y > 0 ? (id - width) : -1); // top
        neighborhood[1] = (x < (width - 1) ? (id + 1) : -1); // right
        neighborhood[2] = (y < (height - 1) ? (id + width) : -1); // bottom
        neighborhood[3] = (x > 0 ? (id - 1) : -1); // left
    }
}
