package org.esa.s2tbx.grm.segmentation;

import java.lang.ref.WeakReference;
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

    public final boolean update(TileDataSource[] sourceTiles, BoundingBox rectange, int numberOfIterations, boolean fastSegmentation, boolean addFourNeighbors) {
        initNodes(sourceTiles, rectange, addFourNeighbors);

        boolean merged = false;
        if (fastSegmentation) {
            merged = performAllIterationsWithBF(numberOfIterations);
        } else {
            merged = performAllIterationsWithLMBF(numberOfIterations);
        }

        return !merged;
    }

    public final void setGraph(Graph graph, int imageWidth, int imageHeight) {
        this.graph = graph;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public final void doClose() {
        this.graph.doClose();

        WeakReference<Graph> reference = new WeakReference<Graph>(this.graph);
        reference.clear();
    }

    public final OutputMaskMatrixHelper buildOutputMaskMatrixHelper() {
        return new OutputMaskMatrixHelper(this.graph, this.imageWidth, this.imageHeight);
    }

    public Graph getGraph() {
        return graph;
    }

    public boolean performAllIterationsWithLMBF(int numberOfIterations) {
        if (logger.isLoggable(Level.FINER)) {
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

    private void initNodes(TileDataSource[] sourceTiles, BoundingBox rectange, boolean addFourNeighbors) {
        this.imageWidth = rectange.getWidth();
        this.imageHeight = rectange.getHeight();

        int numberOfComponentsPerPixel = sourceTiles.length;
        int numberOfNodes = this.imageWidth * this.imageHeight;
        this.graph = new Graph(numberOfNodes);

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
                    if (edgeFromNeighborToR != null) {
                        edgeFromNeighborToR.setCost(merginCost);
                        edgeFromNeighborToR.setCostUpdated(true);
                    }
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

    public static BoundingBox mergeBoundingBoxes(BoundingBox bb1, BoundingBox bb2) {
        int minimumLeftUpperX = Math.min(bb1.getLeftX(), bb2.getLeftX());
        int minimumLeftUpperY = Math.min(bb1.getTopY(), bb2.getTopY());
        int maximumWidth = Math.max(bb1.getLeftX() + bb1.getWidth(), bb2.getLeftX() + bb2.getWidth());
        int maximummHeight = Math.max(bb1.getTopY() + bb1.getHeight(), bb2.getTopY() + bb2.getHeight());

        int width = maximumWidth - minimumLeftUpperX;
        int height = maximummHeight - minimumLeftUpperY;
        return new BoundingBox(minimumLeftUpperX, minimumLeftUpperY, width, height);
    }

    public static void generateEightNeighborhood(int[] neighborhood, int id, int width, int height) {
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
