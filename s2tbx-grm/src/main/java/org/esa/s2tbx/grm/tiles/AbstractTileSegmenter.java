package org.esa.s2tbx.grm.tiles;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.dataio.cache.S2CacheUtils;
import org.esa.s2tbx.grm.*;
import org.esa.snap.core.gpf.Tile;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author Jean Coravu
 */
public abstract class AbstractTileSegmenter {
    private final float threshold;
    private final boolean addFourNeighbors;
    private final boolean fastSegmentation;

    private final int imageWidth;
    private final int imageHeight;
    private final int numberOfIterations;
    private final int numberOfFirstIterations;
    private final int tileWidth;
    private final int tileHeight;
    private final int numberOfIterationsForPartialSegmentations;
    private final TilesBidimensionalArray tilesBidimensionalArray;
    private final long availableMemory;

    private long accumulatedMemory;
    private boolean isFusion;

    protected AbstractTileSegmenter(Dimension imageSize, Dimension tileSize, int numberOfIterations, int numberOfFirstIterations, float threshold, boolean fastSegmentation) {
        this.imageWidth = imageSize.width;
        this.imageHeight = imageSize.height;
        this.tileWidth = tileSize.width;
        this.tileHeight = tileSize.height;
        this.numberOfIterations = numberOfIterations;
        this.numberOfFirstIterations = numberOfFirstIterations;
        this.fastSegmentation = fastSegmentation;
        this.threshold = threshold;

        this.addFourNeighbors = true;
        this.numberOfIterationsForPartialSegmentations = 3; // TODO: find a smart value
        this.tilesBidimensionalArray = new TilesBidimensionalArray();
        this.availableMemory = Runtime.getRuntime().totalMemory();

        resetValues();
    }

    protected abstract Node buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel);

    protected abstract AbstractSegmenter buildSegmenter(float threshold);

    public final AbstractSegmenter runAllTilesSegmentation(Tile[] sourceTiles) throws IllegalAccessException {
        // run the first partial segmentation
        runAllTilesFirstPartialSegmentation(sourceTiles);

        return runAllTilesSecondSegmentation();
    }

    public final AbstractSegmenter runAllTilesSecondSegmentation() throws IllegalAccessException {
        //TODO Jean remove
        this.accumulatedMemory = this.accumulatedMemory * this.accumulatedMemory;

        int numberOfIterationsRemaining = this.numberOfIterations;
        while (this.accumulatedMemory > this.availableMemory && this.isFusion) {
            runPartialSegmentation();

            // update number of remaining iterations
            if (numberOfIterationsRemaining < this.numberOfIterationsForPartialSegmentations) {
                break;
            } else {
                numberOfIterationsRemaining -= this.numberOfIterationsForPartialSegmentations;
            }
        }
        if (this.accumulatedMemory <= this.availableMemory) {
            return mergeAllGraphsAndAchieveSegmentation(numberOfIterationsRemaining);
        }
        throw new IllegalArgumentException("No more possible fusions, but can not store the output graph.");
    }

    private void resetValues() {
        this.accumulatedMemory = 0;
        this.isFusion = false;
    }

    private void runAllTilesFirstPartialSegmentation(Tile[] sourceTiles) throws IllegalAccessException {
        resetValues();

        int nbTilesX = this.imageWidth / this.tileWidth;
        int nbTilesY = this.imageHeight / this.tileHeight;

        System.out.println("--------runFirstPartialSegmentation nbTilesX="+nbTilesX+"  nbTilesY="+nbTilesY);

        for (int row = 0; row <nbTilesY; row++) {
            for (int col = 0; col<nbTilesX ; col++) {
                // compute current tile start and size
                int startX = col * tileWidth;
                int startY = row * tileHeight;
                int sizeX = tileWidth;
                int sizeY = tileHeight;
                // current tile size might be different for right and bottom borders
                if (col == nbTilesX - 1) {
                    sizeX += imageWidth % tileWidth;
                }
                if (row == nbTilesY - 1) {
                    sizeY += imageHeight % tileHeight;
                }

                ProcessingTile currentTile = buildTile(startX, startY, sizeX, sizeY);
                runOneTileFirstSegmentation(sourceTiles, currentTile);
            }
        }
    }

    public void runOneTileFirstSegmentation(Tile[] sourceTiles, ProcessingTile currentTile) throws IllegalAccessException {
        int row = currentTile.getImageTopY() / this.tileHeight;
        int col = currentTile.getImageLeftX() / this.tileWidth;
        this.tilesBidimensionalArray.addTile(row, col, currentTile);

        System.out.println("runOneTileFirstSegmentation row="+row+"  col="+col+"  tile.region="+currentTile.getRegion());

        int numberOfNeighborLayers = (int) (Math.pow(2, this.numberOfIterationsForPartialSegmentations + 1) - 2);

        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        boolean complete = segmenter.update(sourceTiles, currentTile.getRegion(), this.numberOfFirstIterations, this.fastSegmentation, this.addFourNeighbors);
        if (!complete) {
            this.isFusion = true;
        }
        Graph graph = segmenter.getGraph();

        // rescale the graph to be in the reference of the image
        graph.rescaleGraph(currentTile, this.imageWidth);

        // remove unstable segments
        graph.removeUnstableSegments(currentTile, this.imageWidth);

        this.accumulatedMemory += ObjectSizeCalculator.sizeOf(graph);

        writeGraph(graph, currentTile.getNodeFileName(), currentTile.getEdgeFileName());

        // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
        List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

        IntToObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

        writeStabilityMargin(borderNodes, currentTile.getNodeMarginFileName(), currentTile.getEdgeMarginFileName());
    }

    private void runPartialSegmentation() throws IllegalAccessException {
        resetValues();

        int numberOfNeighborLayers = (int) (Math.pow(2, this.numberOfIterationsForPartialSegmentations + 1) - 2);

        int nbTilesX = this.tilesBidimensionalArray.getTileCountX();
        int nbTilesY = this.tilesBidimensionalArray.getTileCountY();

        System.out.println("********* runPartialSegmentation nbTilesX="+nbTilesX+"  nbTilesY="+nbTilesY);

        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

                addStabilityMargin(graph, row, col, nbTilesX, nbTilesY);

                IntToObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, this.imageWidth);

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);

                removeUselessNodes(graph, currentTile, this.imageWidth, numberOfNeighborLayers);

                // build the segmenter
                AbstractSegmenter segmenter = buildSegmenter(this.threshold);
                segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
                boolean merged = segmenter.perfomAllIterationsWithLMBF(numberOfIterationsForPartialSegmentations);
                if (merged) {
                    this.isFusion = true;
                }

                graph.removeUnstableSegments(currentTile, this.imageWidth);

                this.accumulatedMemory += ObjectSizeCalculator.sizeOf(graph);

                writeGraph(graph, currentTile.getNodeFileName(), currentTile.getEdgeFileName());
            }
        }

        // during this step we extract the stability margin for the next round
        for(int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col<nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

                List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

                IntToObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

                writeStabilityMargin(borderNodes, currentTile.getNodeMarginFileName(), currentTile.getEdgeMarginFileName());
            }
        }
    }

    private AbstractSegmenter mergeAllGraphsAndAchieveSegmentation(int numberOfIterations) {
        int numberOfNodes = this.imageWidth * this.imageHeight;
        Graph graph = new Graph(numberOfNodes);
        int nbTilesX = this.tilesBidimensionalArray.getTileCountX();
        int nbTilesY = this.tilesBidimensionalArray.getTileCountY();

        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                insertNodesFromTile(graph, currentTile, false);
            }
        }

        // removing duplicated nodes and updating neighbors
        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                IntToObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, this.imageWidth);

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);
            }
        }

        // segmentation of the graph
        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
        segmenter.perfomAllIterationsWithLMBF(numberOfIterations);
        return segmenter;
    }

    private static void updateNeighborsOfNoneDuplicatedNodes(IntToObjectMap<List<Node>> borderPixelMap, int imageWidth, int imageHeight) {
        int[] neighborhood = new int[4];
        int[] cellNeighborhood = new int[4];
        Iterator<IntToObjectMap.Entry<List<Node>>> itValues = borderPixelMap.entriesIterator();
        while (itValues.hasNext()) {
            IntToObjectMap.Entry<List<Node>> entry = itValues.next();
            int nodeId = entry.getKey();
            List<Node> nodes = entry.getValue();
            AbstractSegmenter.generateFourNeighborhood(neighborhood, nodeId, imageWidth, imageHeight);
            for(int j = 0; j < neighborhood.length; j++) {
                if (neighborhood[j] > -1) {
                    List<Node> neighborNodes = borderPixelMap.get(neighborhood[j]);
                    if (neighborNodes != null) {
                        Node currentNode = nodes.get(0); // currNode
                        Node firstNeighborNode = neighborNodes.get(0); // neigh
                        if (currentNode != firstNeighborNode) {
                            Edge edge = currentNode.findEdge(firstNeighborNode);
                            if (edge == null) {
                                int boundary = 0;
                                IntSet borderCells = AbstractSegmenter.generateBorderCells(currentNode.getContour(), currentNode.getId(), imageWidth);
                                IntIterator itCells = borderCells.iterator();
                                while (itCells.hasNext()) {
                                    int gridId = itCells.nextInt();
                                    List<Node> resultNodes = borderPixelMap.get(gridId);
                                    if (resultNodes != null) {
                                        AbstractSegmenter.generateFourNeighborhood(cellNeighborhood, gridId, imageWidth, imageHeight);
                                        for(int k = 0; k < cellNeighborhood.length; k++) {
                                            if (cellNeighborhood[k] > -1) {
                                                List<Node> cellNeighborNodes = borderPixelMap.get(cellNeighborhood[k]);
                                                if (cellNeighborNodes != null && cellNeighborNodes.get(0) == firstNeighborNode) {
                                                    boundary++;
                                                }
                                            }
                                        }
                                    }
                                }
                                currentNode.addEdge(firstNeighborNode, boundary);
                                firstNeighborNode.addEdge(currentNode, boundary);
                            }
                        }
                    }
                }
            }
        }
    }

    private void insertNodesFromTile(Graph graph, ProcessingTile tile, boolean margin) {
        Graph subgraph = null;
        if (margin) {
            subgraph = readGraph(tile.getNodeMarginFileName(), tile.getEdgeMarginFileName());
        } else {
            subgraph = readGraph(tile.getNodeFileName(), tile.getEdgeFileName());
        }
        int nodeCount = subgraph.getNodeCount();
        for (int i=0; i<nodeCount; i++) {
            Node node = subgraph.getNodeAt(i);
            graph.addNode(node);
        }
    }

    private void addStabilityMargin(Graph graph, int row, int col, int nbTilesX, int nbTilesY) {
        // margin to retrieve at top
        if (row > 0) { // (startYWithoutMargin > 0) { //(row > 0) {
//            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + col), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row-1, col), true);
        }
        // margin to retrieve at right
        if (col < nbTilesX - 1) { //(finishXWithoutMargin < this.imageWidth) { //(col < nbTilesX - 1) {
//            insertNodesFromTile(graph, tiles.get(row * nbTilesX + (col+1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row, col+1), true);
        }
        // margin to retrieve at bottom
        if (row < nbTilesY - 1) { //(finishYWithoutMargin < this.imageHeight) { //(row < nbTilesY - 1) {
//            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + col), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col), true);
        }
        // margin to retrieve at left
        if (col > 0) { // (startXWithoutMargin > 0) { //(col > 0) {
//            insertNodesFromTile(graph, tiles.get(row * nbTilesX + (col-1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row, col-1), true);
        }
        // margin to retrieve at top right
        if (row > 0 && col < nbTilesX - 1) { // (startYWithoutMargin > 0 && finishXWithoutMargin < this.imageWidth) { //(row > 0 && col < nbTilesX - 1) {
//            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + (col+1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row-1, col+1), true);
        }
        // margin to retrieve at bottom right
        if (row < nbTilesY - 1 && col < nbTilesX - 1) { // (finishYWithoutMargin < this.imageHeight && finishXWithoutMargin < this.imageWidth) { //(row < nbTilesY - 1 && col < nbTilesX - 1) {
//            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + (col+1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col+1), true);
        }
        // margin to retrieve at bottom left
        if (row < nbTilesY - 1 && col > 0) { // (finishYWithoutMargin < this.imageHeight && startXWithoutMargin > 0) { //(row < nbTilesY - 1 && col > 0) {
//            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + (col-1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col-1), true);
        }
        // margin to retrieve at top left
        if (row > 0 && col > 0) { // (startYWithoutMargin > 0 && startXWithoutMargin > 0) { //(row > 0 && col > 0) {
//            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + (col-1)), true);
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row-1, col-1), true);
        }
    }

    private void removeUselessNodes(Graph graph, ProcessingTile tile, int imageWidth, int numberOfLayers) {
        List<Node> nodesToIterate = new ArrayList<Node>();
        int nodeCount = graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = graph.getNodeAt(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (box.getLeftX() > tile.getImageRightX() || box.getTopY() > tile.getImageBottomY() || box.getRightX() - 1 < tile.getImageLeftX() || box.getBottomY() - 1 < tile.getImageTopY()) {
                continue;
            } else {
                IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), imageWidth);
                IntIterator it = borderCells.iterator();
                while (it.hasNext()) {
                    int gridId = it.nextInt();
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

        IntToObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfLayers);

        nodeCount = graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = graph.getNodeAt(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (!borderNodes.containsKey(node.getId())) {
                graph.removeEdgeToUnstableNode(node);
                node.setExpired(true);
            }
        }

        graph.removeExpiredNodes();
    }

    private IntToObjectMap<Node> extractStabilityMargin(List<Node> nodesToIterate, int numberOfLayers) {
        IntToObjectMap<Integer> borderNodesValues = new IntToObjectMap<>(nodesToIterate.size());
        IntToObjectMap<Node> borderNodes = new IntToObjectMap<>(nodesToIterate.size());
        for (int i=0; i<nodesToIterate.size(); i++) {
            Node node = nodesToIterate.get(i);
            borderNodesValues.put(node.getId(), 0);
            borderNodes.put(node.getId(), node);
        }
        for (int i=0; i<nodesToIterate.size(); i++) {
            exploreDFS(nodesToIterate.get(i), 0, borderNodesValues, borderNodes, numberOfLayers);
        }
        return borderNodes;
    }

    private void exploreDFS(Node node, int p, IntToObjectMap<Integer> borderNodesValues, IntToObjectMap<Node> borderNodes, int numberOfLayers) {
        if (p > numberOfLayers) {
            return;
        } else {
            Integer value = borderNodesValues.get(node.getId());
            if (value != null) {
                if (p <= value.intValue()) {
                } else {
                    return;
                }
            } else {
            }
            borderNodesValues.put(node.getId(), p);
            borderNodes.put(node.getId(), node);
            int edgeCount = node.getEdgeCount();
            for (int i=0; i<edgeCount; i++) {
                Edge edge = node.getEdgeAt(i);
                exploreDFS(edge.getTarget(), p + 1, borderNodesValues, borderNodes, numberOfLayers);
            }
        }
    }

    protected void writeNode(BufferedOutputStreamWrapper nodesFileStream, Node nodeToWrite) throws IOException {
        nodesFileStream.writeInt(nodeToWrite.getId());
        nodesFileStream.writeInt(nodeToWrite.getPerimeter());
        nodesFileStream.writeInt(nodeToWrite.getArea());

        BoundingBox box = nodeToWrite.getBox();
        nodesFileStream.writeInt(box.getLeftX());
        nodesFileStream.writeInt(box.getTopY());
        nodesFileStream.writeInt(box.getWidth());
        nodesFileStream.writeInt(box.getHeight());

        Contour contour = nodeToWrite.getContour();
        nodesFileStream.writeInt(contour.size());
        byte[] bits = contour.getBits();
        nodesFileStream.writeInt(bits.length);
        nodesFileStream.write(bits);

        nodesFileStream.writeInt(nodeToWrite.getNumberOfComponentsPerPixel());
    }

    protected Node readNode(BufferedInputStreamWrapper nodesFileStream) throws IOException {
        int nodeId = nodesFileStream.readInt();
        int perimeter = nodesFileStream.readInt();
        int area = nodesFileStream.readInt();

        int upperLeftX = nodesFileStream.readInt();
        int upperLeftY = nodesFileStream.readInt();
        int width = nodesFileStream.readInt();
        int height = nodesFileStream.readInt();
        BoundingBox box = new BoundingBox(upperLeftX, upperLeftY, width, height);

        int contourSize = nodesFileStream.readInt();
        int byteCount = nodesFileStream.readInt();
        byte[] bits = new byte[byteCount];
        nodesFileStream.readFully(bits);
        Contour contour = new Contour(contourSize, bits);

        int numberOfComponentsPerPixel = nodesFileStream.readInt();

        return buildNode(nodeId, box, contour, perimeter, area, numberOfComponentsPerPixel);
    }

    private void writeGraph(Graph graph, String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        tempFolder.mkdir();
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            BufferedOutputStreamWrapper nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = new File(tempFolder, edgesPath);
            BufferedOutputStreamWrapper edgesFileStream = new BufferedOutputStreamWrapper(edgesFile);

            int nodeCount = graph.getNodeCount();
            nodesFileStream.writeInt(nodeCount);

            for (int i=0; i<nodeCount; i++) {
                Node node = graph.getNodeAt(i);
                writeNode(nodesFileStream, node);

                // write the node id in the edge file
                edgesFileStream.writeInt(node.getId());

                int edgeCount = node.getEdgeCount();
                edgesFileStream.writeInt(edgeCount);
                for (int k=0; k<edgeCount; k++) {
                    Edge edge = node.getEdgeAt(k);
                    writeEdge(edgesFileStream, edge);
                }
            }
            nodesFileStream.close();
            edgesFileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEdge(BufferedOutputStreamWrapper edgesFileStream, Edge edge) throws IOException {
        edgesFileStream.writeInt(edge.getTarget().getId());
        edgesFileStream.writeInt(edge.getBoundary());
    }

    private Graph readGraph(String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            BufferedInputStreamWrapper nodesFileStream = new BufferedInputStreamWrapper(nodesFile);

            int nodeCount = nodesFileStream.readInt();
            IntToObjectMap<Node> nodesMap = new IntToObjectMap<Node>(nodeCount);
            Graph graph = new Graph(nodeCount);
            for (int i=0; i<nodeCount; i++) {
                Node node = readNode(nodesFileStream);
                nodesMap.put(node.getId(), node);
                graph.addNode(node);
            }
            nodesFileStream.close();

            File edgesFile = new File(tempFolder, edgesPath);
            BufferedInputStreamWrapper edgesFileStream = new BufferedInputStreamWrapper(edgesFile);

            for (int i=0; i<nodeCount; i++) {
                Node node = graph.getNodeAt(i);
                int nodeId = edgesFileStream.readInt();
                assert(node.getId() == nodeId);

                int edgeCount = edgesFileStream.readInt();
                for (int k=0; k<edgeCount; k++) {
                    int targetNodeId = edgesFileStream.readInt();
                    int boundary = edgesFileStream.readInt();
                    Node targetNode = nodesMap.get(targetNodeId);
                    node.addEdge(targetNode, boundary);
                }
            }

            edgesFileStream.close();
            return graph;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeStabilityMargin(IntToObjectMap<Node> borderNodes, String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        tempFolder.mkdir();
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            BufferedOutputStreamWrapper nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = new File(tempFolder, edgesPath);
            BufferedOutputStreamWrapper edgesFileStream = new BufferedOutputStreamWrapper(edgesFile);

            // write the number of nodes
            nodesFileStream.writeInt(borderNodes.size());

            Iterator<Node> itValues = borderNodes.valuesIterator();
            while (itValues.hasNext()) {
                Node node = itValues.next();
                writeNode(nodesFileStream, node);

                // write the node id in the edge file
                edgesFileStream.writeInt(node.getId());

                int edgeCountToWrite = 0;
                int edgeCount = node.getEdgeCount();
                for (int k=0; k<edgeCount; k++) {
                    Edge edge = node.getEdgeAt(k);
                    Node targetNode = edge.getTarget();
                    if (borderNodes.containsKey(targetNode.getId())) {
                        edgeCountToWrite++;
                    }
                }

                // write only edges pointing to nodes which are in the stability margin.
                edgesFileStream.writeInt(edgeCountToWrite);

                for (int k=0; k<edgeCount; k++) {
                    Edge edge = node.getEdgeAt(k);
                    Node targetNode = edge.getTarget();
                    if (borderNodes.containsKey(targetNode.getId())) {
                        writeEdge(edgesFileStream, edge);
                    }
                }
            }
            nodesFileStream.close();
            edgesFileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final ProcessingTile buildTile(int startX, int startY, int sizeX, int sizeY) {
        int margin = (int) (Math.pow(2, this.numberOfFirstIterations + 1) - 2);
        // compute current tile start and size
        ProcessingTile tile = new ProcessingTile();
        int finishX = startX + sizeX;
        int finishY = startY + sizeY;
        // margin at the top
        if (startY > 0) { //(row > 0) {
            tile.setTopMargin(margin);
            tile.setImageTopY(startY); // tile.rows[0] = startY;//row * tileHeight;
        } else {
            // the tile is on the top row --> no top margin
            tile.setTopMargin(0);
            tile.setImageTopY(0); // tile.rows[0] = 0;
        }

        // margin at the right
        if (finishX < this.imageWidth) { //(col < nbTilesX - 1) {
            tile.setRightMargin(margin);
            tile.setImageRightX(startX + sizeX - 1); // tile.columns[1] = startX + sizeX - 1; //sizeX
        } else {
            // the tile is on the right column --> no right margin
            tile.setRightMargin(0);
            tile.setImageRightX(this.imageWidth - 1); // tile.columns[1] = imageWidth - 1;
        }

        // margin at the bottom
        if (finishY < this.imageHeight) { // (row < nbTilesY - 1) {
            tile.setBottomMargin(margin);
            tile.setImageBottomY(startY + sizeY - 1); // tile.rows[1] = startY + sizeY - 1; // sizeY
        } else {
            // the tile is on the bottom --> no bottom margin
            tile.setBottomMargin(0);
            tile.setImageBottomY(this.imageHeight - 1); // tile.rows[1] = imageHeight - 1;
        }

        // margin at the left
        if (startX > 0) { //(col > 0) {
            tile.setLeftMargin(margin);
            tile.setImageLeftX(startX); // tile.columns[0] = startX;//col * tileWidth;
        } else {
            // the tile is on the left --> no left margin
            tile.setLeftMargin(0);
            tile.setImageLeftX(0); // tile.columns[0] = 0;
        }

        // store the tile region
        int regionLeftX = startX - tile.getLeftMargin();
        int regionTopY = startY - tile.getTopMargin();
        int regionWidth = sizeX + tile.getLeftMargin() + tile.getRightMargin();
        int regionHeight = sizeY + tile.getTopMargin() + tile.getBottomMargin();
        tile.setRegion(new BoundingBox(regionLeftX, regionTopY, regionWidth, regionHeight));

        String temporaryFilesPrefix = "";
        String suffix = Integer.toString(startX) + "_" + Integer.toString(startY) + ".bin";
        tile.setNodeFileName(temporaryFilesPrefix + "_node_" + suffix);
        tile.setEdgeFileName(temporaryFilesPrefix + "_edge_" + suffix);
        tile.setNodeMarginFileName(temporaryFilesPrefix + "_nodeMargin_" + suffix);
        tile.setEdgeMarginFileName(temporaryFilesPrefix + "_edgeMargin_" + suffix);

        return tile;
    }
}
