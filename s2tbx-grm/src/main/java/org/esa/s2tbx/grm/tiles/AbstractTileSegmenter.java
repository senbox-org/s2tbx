package org.esa.s2tbx.grm.tiles;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.dataio.cache.S2CacheUtils;
import org.esa.s2tbx.grm.*;
import org.esa.snap.core.datamodel.Product;

import java.io.*;
import java.util.*;

/**
 * @author Jean Coravu
 */
public abstract class AbstractTileSegmenter {
    private final float threshold;
    private final boolean addFourNeighbors;

    int imageWidth = 100;
    int imageHeight = 100;
    int tileHeight = 0;
    int tileWidth = 0;
    int nbTilesX = 0;
    int nbTilesY = 0;

    protected AbstractTileSegmenter(float threshold) {
        this.threshold = threshold;
        this.addFourNeighbors = true;
    }

    protected abstract Node buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel);

    protected abstract AbstractSegmenter buildSegmenter(float threshold);

    public final AbstractSegmenter runSegmentation(Product product, int bandIndices[], int numberOfIterations, int numberOfFirstIterations, boolean fastSegmentation)
                                                   throws IllegalAccessException {

        this.imageWidth = product.getSceneRasterWidth();
        this.imageHeight = product.getSceneRasterHeight();
        this.tileWidth = this.imageWidth / 2;
        this.tileHeight = this.imageHeight / 2;
        this.nbTilesX = this.imageWidth / this.tileWidth;
        this.nbTilesY = this.imageHeight / this.tileHeight;

        int margin = (int) (Math.pow(2, numberOfFirstIterations + 1) - 2);

        long memory = Runtime.getRuntime().totalMemory();

        List<ProcessingTile> tiles = splitImage(this.imageWidth, this.imageHeight, this.tileWidth, this.tileHeight, margin, this.nbTilesX, this.nbTilesY);
        System.out.println("tile.count=" + tiles.size()+"  imageWidth="+imageWidth+"  imageHeight="+imageHeight+"  tileWidth="+tileWidth+"  tileHeight="+tileHeight+"  nbTilesX="+nbTilesX+"  nbTilesY="+nbTilesY+"  margin="+margin);

        if (tiles.size() > 1) {
            int numberOfIterationsForPartialSegmentations = 3; // TODO: find a smart value
            int numberOfIterationsRemaining = numberOfIterations;

            // run the first partial segmentation
            SegmentationResult result = runFirstPartialSegmentation(product, bandIndices, tiles, numberOfFirstIterations, numberOfIterationsForPartialSegmentations, fastSegmentation);

            while (result.getAccumulatedMemory() > memory && result.isFusion()) {
                result = runPartialSegmentation(tiles, numberOfIterationsForPartialSegmentations);

                // update number of remaining iterations
                if (numberOfIterationsRemaining < numberOfIterationsForPartialSegmentations) {
                    break;
                } else {
                    numberOfIterationsRemaining -= numberOfIterationsForPartialSegmentations;
                }
            }

            if (result.getAccumulatedMemory() <= memory) {
                return mergeAllGraphsAndAchieveSegmentation(tiles, numberOfIterationsRemaining);
            }
            throw new IllegalArgumentException("No more possible fusions, but can not store the output graph");
        } else {
            AbstractSegmenter segmenter = buildSegmenter(this.threshold);
            BoundingBox rectange = new BoundingBox(0, 0, this.imageWidth, this.imageHeight);
            segmenter.update(product, bandIndices, rectange, numberOfIterations, fastSegmentation, this.addFourNeighbors);
            return segmenter;
        }
    }

    private SegmentationResult runFirstPartialSegmentation(Product product, int bandIndices[], List<ProcessingTile> tiles, int numberOfFirstIterations,
                                                           int numberOfIterationsForPartialSegmentations, boolean fastSegmentation)
                                                           throws IllegalAccessException {

        System.out.println("--------runFirstPartialSegmentation numberOfFirstIterations="+numberOfFirstIterations+"  numberOfIterationsForPartialSegmentations="+numberOfIterationsForPartialSegmentations+"  time="+new Date(System.currentTimeMillis()));

        boolean isFusion = false;
        long accumulatedMemory = 0;
        int numberOfNeighborLayers = (int) (Math.pow(2, numberOfIterationsForPartialSegmentations + 1) - 2);

        for (int row = 0; row <this.nbTilesY; row++) {
            for (int col = 0; col<this.nbTilesX ; col++) {
                int tileIndex = row*this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                System.out.println("----update tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                AbstractSegmenter segmenter = buildSegmenter(this.threshold);
                boolean complete = segmenter.update(product, bandIndices, currentTile.region, numberOfFirstIterations, fastSegmentation, this.addFourNeighbors);
                if (!complete) {
                    isFusion = true;
                }

                Graph graph = segmenter.getGraph();

                // rescale the graph to be in the reference of the image
                graph.rescaleGraph(currentTile, row, col, this.tileWidth, this.tileHeight, this.imageWidth);

                // remove unstable segments
                graph.removeUnstableSegments(currentTile, this.imageWidth);

                accumulatedMemory = ObjectSizeCalculator.sizeOf(graph);

                writeGraph(graph, currentTile.nodeFileName, currentTile.edgeFileName);

                // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
                List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

                IntToObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

                writeStabilityMargin(borderNodes, currentTile.nodeMarginFileName, currentTile.edgeMarginFileName);
            }
        }
        return new SegmentationResult(isFusion, accumulatedMemory);
    }

    private SegmentationResult runPartialSegmentation(List<ProcessingTile> tiles, int numberOfIterationsForPartialSegmentations) throws IllegalAccessException {
        System.out.println("********* runPartialSegmentation  numberOfIterationsForPartialSegmentations="+numberOfIterationsForPartialSegmentations+"  time="+new Date(System.currentTimeMillis()));

        boolean isFusion = false;
        long accumulatedMemory = 0;
        int numberOfNeighborLayers = (int) (Math.pow(2, numberOfIterationsForPartialSegmentations + 1) - 2);

        for (int row = 0; row < this.nbTilesY; row++) {
            for (int col = 0; col < this.nbTilesX; col++) {
                int tileIndex = row*this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                Graph graph = readGraph(currentTile.nodeFileName, currentTile.edgeFileName);

                addStabilityMargin(graph, tiles, row, col, this.nbTilesX, this.nbTilesY);

                IntToObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, this.nbTilesX, this.nbTilesY, this.imageWidth);

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);

                removeUselessNodes(graph, currentTile, this.imageWidth, numberOfNeighborLayers);

                // build the segmenter
                AbstractSegmenter segmenter = buildSegmenter(this.threshold);
                segmenter.setGraph(graph, currentTile.getRegion().getWidth(), currentTile.getRegion().getHeight());
                boolean merged = segmenter.perfomAllIterationsWithLMBF(numberOfIterationsForPartialSegmentations);
                if (merged) {
                    isFusion = true;
                }

                graph.removeUnstableSegments(currentTile, this.imageWidth);

                accumulatedMemory = ObjectSizeCalculator.sizeOf(graph);

                writeGraph(graph, currentTile.nodeFileName, currentTile.edgeFileName);
            }
        }

        // during this step we extract the stability margin for the next round
        for(int row = 0; row < this.nbTilesY; row++) {
            for (int col = 0; col<this.nbTilesX; col++) {
                int tileIndex = row*this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                Graph graph = readGraph(currentTile.nodeFileName, currentTile.edgeFileName);

                List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

                IntToObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

                writeStabilityMargin(borderNodes, currentTile.nodeMarginFileName, currentTile.edgeMarginFileName);
            }
        }
        return new SegmentationResult(isFusion, accumulatedMemory);
    }

    private AbstractSegmenter mergeAllGraphsAndAchieveSegmentation(List<ProcessingTile> tiles, int numberOfIterations) {
        System.out.println("++++++++++ mergeAllGraphsAndAchieveSegmentation numberOfIterations="+numberOfIterations+"  time="+new Date(System.currentTimeMillis()));

        // read the graph
        int numberOfNodes = this.imageWidth * this.imageHeight;
        Graph graph = new Graph(numberOfNodes);

        for (int row = 0; row < this.nbTilesY; row++) {
            for (int col = 0; col < this.nbTilesX; col++) {
                int tileIndex = row * this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                insertNodesFromTile(graph, currentTile, false);
            }
        }

        // removing duplicated nodes and updating neighbors
        for (int row = 0; row < this.nbTilesY; row++) {
            for (int col = 0; col < this.nbTilesX; col++) {
                int tileIndex = row * this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                IntToObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, this.nbTilesX, this.nbTilesY, this.imageWidth);

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
            subgraph = readGraph(tile.nodeMarginFileName, tile.edgeMarginFileName);
        } else {
            subgraph = readGraph(tile.nodeFileName, tile.edgeFileName);
        }
        int nodeCount = subgraph.getNodeCount();
        for (int i=0; i<nodeCount; i++) {
            Node node = subgraph.getNodeAt(i);
            graph.addNode(node);
        }
    }

    private void addStabilityMargin(Graph graph, List<ProcessingTile> tiles, int row, int col, int nbTilesX, int nbTilesY) {
        // margin to retrieve at top
        if (row > 0) {
            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + col), true);
        }
        // margin to retrieve at right
        if (col < nbTilesX - 1) {
            insertNodesFromTile(graph, tiles.get(row * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom
        if (row < nbTilesY - 1) {
            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + col), true);
        }
        // margin to retrieve at left
        if (col > 0) {
            insertNodesFromTile(graph, tiles.get(row * nbTilesX + (col-1)), true);
        }
        // margin to retrieve at top right
        if (row > 0 && col < nbTilesX - 1) {
            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom right
        if (row < nbTilesY - 1 && col < nbTilesX - 1) {
            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + (col+1)), true);
        }
        // margin to retrieve at bottom left
        if (row < nbTilesY - 1 && col > 0) {
            insertNodesFromTile(graph, tiles.get((row+1) * nbTilesX + (col-1)), true);
        }
        // margin to retrieve at top left
        if (row > 0 && col > 0) {
            insertNodesFromTile(graph, tiles.get((row-1) * nbTilesX + (col-1)), true);
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
            } else if (borderNodes.containsKey(node.getId())) {
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

//    private void exploreDFS(Node node, int p, IntToObjectMap<Integer> borderNodesValues, IntToObjectMap<Node> borderNodes, int numberOfLayers) {
//        if (p > numberOfLayers) {
//            return;
//        } else {
//            Integer value = borderNodesValues.get(node.getId());
//            if (value != null) {
//                if (p <= value.intValue()) {
//                    borderNodesValues.put(node.getId(), p);
//                    borderNodes.put(node.getId(), node);
//                    int edgeCount = node.getEdgeCount();
//                    for (int i=0; i<edgeCount; i++) {
//                        Edge edge = node.getEdgeAt(i);
//                        exploreDFS(edge.getTarget(), p + 1, borderNodesValues, borderNodes, numberOfLayers);
//                    }
//                } else {
//                    return;
//                }
//            } else {
//                borderNodesValues.put(node.getId(), p);
//                borderNodes.put(node.getId(), node);
//                int edgeCount = node.getEdgeCount();
//                for (int i=0; i<edgeCount; i++) {
//                    Edge edge = node.getEdgeAt(i);
//                    exploreDFS(edge.getTarget(), p + 1, borderNodesValues, borderNodes, numberOfLayers);
//                }
//            }
//        }
//    }

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

    private static List<ProcessingTile> splitImage(int imageWidth, int imageHeight, int tileWidth, int tileHeight, int margin, int nbTilesX, int nbTilesY) {
        String temporaryFilesPrefix = "";
        List<ProcessingTile> tiles = new ArrayList<ProcessingTile>(nbTilesX * nbTilesY);
        for (int row=0; row<nbTilesY; row++) {
            for (int col=0; col<nbTilesX; col++) {
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
                ProcessingTile tile = new ProcessingTile();
                int tileIndex = tiles.size();

                // margin at the top ?
                if (row > 0) {
                    tile.setTopMargin(margin);
                    tile.rows[0] = row * tileHeight;
                } else {
                    // the tile is on the top row --> no top margin
                    tile.setTopMargin(0);
                    tile.rows[0] = 0;
                }

                // margin at the right
                if (col < nbTilesX - 1) {
                    tile.setRightMargin(margin);
                    tile.columns[1] = col * tileWidth + sizeX - 1; //sizeX
                } else {
                    // the tile is on the right column --> no right margin
                    tile.setRightMargin(0);
                    tile.columns[1] = imageWidth - 1;
                }

                // margin at the bottom
                if (row < nbTilesY - 1) {
                    tile.setBottomMargin(margin);
                    tile.rows[1] = row * tileHeight + sizeY - 1; // sizeY
                } else {
                    // the tile is on the bottom --> no bottom margin
                    tile.setBottomMargin(0);
                    tile.rows[1] = imageHeight - 1;
                }

                // margin at the left
                if (col > 0) {
                    tile.setLeftMargin(margin);
                    tile.columns[0] = col * tileWidth;
                } else {
                    // the tile is on the left --> no left margin
                    tile.setLeftMargin(0);
                    tile.columns[0] = 0;
                }

                // store the tile region
                int regionLeftX = startX - tile.getLeftMargin();
                int regionTopY = startY - tile.getTopMargin();
                int regionWidth = sizeX + tile.getLeftMargin() + tile.getRightMargin();
                int regionHeight = sizeY + tile.getTopMargin() + tile.getBottomMargin();
                tile.setRegion(new BoundingBox(regionLeftX, regionTopY, regionWidth, regionHeight));

                String suffix = Integer.toString(row) + "_" + Integer.toString(col) + ".bin";
                tile.nodeFileName = temporaryFilesPrefix + "_node_" + suffix;
                tile.edgeFileName = temporaryFilesPrefix + "_edge_" + suffix;
                tile.nodeMarginFileName = temporaryFilesPrefix + "_nodeMargin_" + suffix;
                tile.edgeMarginFileName = temporaryFilesPrefix + "_edgeMargin_" + suffix;

                tiles.add(tile);
            }
        }
        return tiles;
    }
}
