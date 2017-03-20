package org.esa.s2tbx.grm.tiles;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.dataio.cache.S2CacheUtils;
import org.esa.s2tbx.grm.*;
import org.esa.snap.core.datamodel.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by jcoravu on 10/3/2017.
 */
public abstract class AbstractTileSegmenter {
    private static final byte NEIGHBORHOOD_TOP_INDEX = 0;
    private static final byte NEIGHBORHOOD_TOP_RIGHT_INDEX = 1;
    private static final byte NEIGHBORHOOD_RIGHT_INDEX = 2;
    private static final byte NEIGHBORHOOD_BOTTOM_RIGHT_INDEX = 3;
    private static final byte NEIGHBORHOOD_BOTTOM_INDEX = 4;
    private static final byte NEIGHBORHOOD_BOTTOM_LEFT_INDEX = 5;
    private static final byte NEIGHBORHOOD_LEFT_INDEX = 6;
    private static final byte NEIGHBORHOOD_TOP_LEFT_INDEX = 7;

    int margin = 0;
    private final float threshold;

    int imageWidth = 100;
    int imageHeight = 100;
    int tileHeight = 0;
    int tileWidth = 0;
    int nbTilesX = 0;
    int nbTilesY = 0;

    protected AbstractTileSegmenter(float threshold) {
        this.threshold = threshold;
    }

    protected abstract AbstractSegmenter buildSegmenter(float threshold);

    public AbstractSegmenter runSegmentation(Product product, int bandIndices[], int numberOfIterations, int numberOfFirstIterations) throws IllegalAccessException {
        this.imageWidth = product.getSceneRasterWidth();
        this.imageHeight = product.getSceneRasterHeight();
        this.tileWidth = this.imageWidth / 2;
        this.tileHeight = this.imageHeight / 2;
        this.nbTilesX = this.imageWidth / this.tileWidth;
        this.nbTilesY = this.imageHeight / this.tileHeight;

        this.margin = (int) (Math.pow(2, numberOfFirstIterations + 1) - 2);

        boolean fastSegmentation = false;
        boolean addFourNeighbors = true;
        long memory = Runtime.getRuntime().totalMemory();

        List<ProcessingTile> tiles = splitImage(imageWidth, imageHeight, tileWidth, tileHeight, margin, this.nbTilesX, this.nbTilesY);
        System.out.println("tile.count=" + tiles.size()+"  imageWidth="+imageWidth+"  imageHeight="+imageHeight+"  tileWidth="+tileWidth+"  tileHeight="+tileHeight+"  nbTilesX="+nbTilesX+"  nbTilesY="+nbTilesY+"  margin="+margin);

        if (tiles.size() > 1) {
            int numberOfIterationsForPartialSegmentations = 3; // TODO: find a smart value
            int numberOfIterationsRemaining = numberOfIterations;

            // run the first partial segmentation
            SegmentationResult result = runFirstPartialSegmentation(product, bandIndices, tiles, numberOfFirstIterations,
                                                                    numberOfIterationsForPartialSegmentations, fastSegmentation, addFourNeighbors);

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
            AbstractSegmenter segmenter = buildSegmenter(threshold);
            BoundingBox rectange = new BoundingBox(0, 0, this.imageWidth, this.imageHeight);
            segmenter.update(product, bandIndices, rectange, numberOfIterations, fastSegmentation, addFourNeighbors);
            return segmenter;
        }
    }

    private SegmentationResult runFirstPartialSegmentation(Product product, int bandIndices[], List<ProcessingTile> tiles, int numberOfFirstIterations,
                                            int numberOfIterationsForPartialSegmentations, boolean fastSegmentation, boolean addFourNeighbors) throws IllegalAccessException {

        long t0 = System.currentTimeMillis();
        System.out.println(" time="+new Date(System.currentTimeMillis()));
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
                boolean complete = segmenter.update(product, bandIndices, currentTile.region, numberOfFirstIterations, fastSegmentation, addFourNeighbors);
                if (!complete) {
                    isFusion = true;
                }

                Graph graph = segmenter.getGraph();

                System.out.println("----rescale graph nodeCount="+graph.getNodeCount() +"  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                // rescale the graph to be in the reference of the image
                graph.rescaleGraph(currentTile, row, col, tileWidth, tileHeight, imageWidth);

                System.out.println("----removeUnstableSegments graph nodeCount="+graph.getNodeCount() +"  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                // remove unstable segments
                graph.removeUnstableSegments(currentTile, imageWidth);

                System.out.println("----accumulatedMemory graph nodeCount="+graph.getNodeCount() +"  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                accumulatedMemory = ObjectSizeCalculator.sizeOf(graph);

                System.out.println("----writeGraph graph nodeCount="+graph.getNodeCount() +"  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                writeGraph(graph, currentTile.nodeFileName, currentTile.edgeFileName);

                System.out.println("----detectBorderNodes graph nodeCount="+graph.getNodeCount() +"  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));
                // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
                Object2IntMap<Node> borderNodesMap = graph.detectBorderNodes(currentTile, imageWidth, imageHeight);

                System.out.println("----extractStabilityMargin graph nodeCount="+graph.getNodeCount()+" borderNodesMap.size="+borderNodesMap.size() +" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                extractStabilityMargin(borderNodesMap, numberOfNeighborLayers);

                System.out.println("----writeStabilityMargin graph nodeCount="+graph.getNodeCount()+" borderNodesMap.size="+borderNodesMap.size() +" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                writeStabilityMargin(borderNodesMap, currentTile.nodeMarginFileName, currentTile.edgeMarginFileName);
            }
        }
        return new SegmentationResult(isFusion, accumulatedMemory);
    }

    private SegmentationResult runPartialSegmentation(List<ProcessingTile> tiles, int numberOfIterationsForPartialSegmentations) throws IllegalAccessException {
        System.out.println("********* runPartialSegmentation  numberOfIterationsForPartialSegmentations="+numberOfIterationsForPartialSegmentations+"  time="+new Date(System.currentTimeMillis()));

        boolean isFusion = false;
        long accumulatedMemory = 0;
        int numberOfNeighborLayers = (int) (Math.pow(2, numberOfIterationsForPartialSegmentations + 1) - 2);
        for (int row = 0; row < nbTilesY; ++row) {
            for (int col = 0; col < nbTilesX; col++) {
                int tileIndex = row*this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                System.out.println("********* readGraph  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                Graph graph = readGraph(currentTile.nodeFileName, currentTile.edgeFileName);

                System.out.println("********* addStabilityMargin graph nodeCount="+graph.getNodeCount() +" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                addStabilityMargin(graph, tiles, row, col, nbTilesX, nbTilesY);

                System.out.println("********* buildBorderPixelMap graph nodeCount="+graph.getNodeCount() +" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, imageWidth);

                System.out.println("********* removeDuplicatedNodes graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                graph.removeDuplicatedNodes(borderPixelMap, imageWidth);

                System.out.println("********* updateNeighborsOfNoneDuplicatedNodes graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, imageWidth, imageHeight);

                System.out.println("********* removeUselessNodes graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                removeUselessNodes(graph, currentTile, imageWidth, numberOfNeighborLayers);

                System.out.println("********* perfomAllIterationsWithLMBF graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                // build the segmenter
                AbstractSegmenter segmenter = buildSegmenter(threshold);
                segmenter.setGraph(graph, currentTile.region.getWidth(), currentTile.region.getHeight());
                boolean merged = segmenter.perfomAllIterationsWithLMBF(numberOfIterationsForPartialSegmentations);
                if (merged) {
                    isFusion = true;
                }

                System.out.println("********* removeUnstableSegments graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                graph.removeUnstableSegments(currentTile, imageWidth);

                System.out.println("********* accumulatedMemory graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                accumulatedMemory = ObjectSizeCalculator.sizeOf(graph);

                System.out.println("********* writeGraph graph nodeCount="+graph.getNodeCount() +" borderPixelMap.size="+borderPixelMap.size()+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                writeGraph(graph, currentTile.nodeFileName, currentTile.edgeFileName);
            }
        }

        // during this step we extract the stability margin for the next round
        for(int row = 0; row < nbTilesY; ++row) {
            for (int col = 0; col<nbTilesX; col++) {
                ProcessingTile currentTile = tiles.get(row * nbTilesX + col); // tiles[row*nbTilesX + col];

                System.out.println("********* extract the stability margin for the next round  tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                Graph graph = readGraph(currentTile.nodeFileName, currentTile.edgeFileName);

                Object2IntMap<Node> borderNodesMap = graph.detectBorderNodes(currentTile, imageWidth, imageHeight);

                extractStabilityMargin(borderNodesMap, numberOfNeighborLayers);

                writeStabilityMargin(borderNodesMap, currentTile.nodeMarginFileName, currentTile.edgeMarginFileName);
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

                System.out.println("++++++++++ insertNodesFromTile"+" tileRow="+row+"  tileColumn="+col+"  time="+new Date(System.currentTimeMillis()));

                insertNodesFromTile(graph, currentTile, false);
            }
        }

        System.out.println("++++++++++ removing duplicated nodes and updating neighbors"+"  time="+new Date(System.currentTimeMillis()));

        // removing duplicated nodes and updating neighbors
        for (int row = 0; row < this.nbTilesY; ++row) {
            for (int col = 0; col < this.nbTilesX; col++) {
                int tileIndex = row * this.nbTilesX + col;
                ProcessingTile currentTile = tiles.get(tileIndex);

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, imageWidth);

                graph.removeDuplicatedNodes(borderPixelMap, imageWidth);

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, imageWidth, imageHeight);
            }
        }

        System.out.println("++++++++++ start perfomAllIterationsWithLMBF numberOfIterations="+numberOfIterations+"  time="+new Date(System.currentTimeMillis()));

        // segmentation of the graph
        AbstractSegmenter segmenter = buildSegmenter(threshold);
        segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
        segmenter.perfomAllIterationsWithLMBF(numberOfIterations);

        System.out.println("++++++++++ finish perfomAllIterationsWithLMBF numberOfIterations="+numberOfIterations+"  time="+new Date(System.currentTimeMillis()));
        return segmenter;
    }

    private static void updateNeighborsOfNoneDuplicatedNodes(Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth, int imageHeight) {
        int[] neighborhood = new int[4];
        int[] cellNeighborhood = new int[4];
        ObjectIterator<Int2ObjectMap.Entry<List<Node>>> it = borderPixelMap.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<Node>> entry = it.next();
            int nodeId = entry.getIntKey();
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
        Object2IntMap<Node> marginNodes = new Object2IntArrayMap<Node>();
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
                            marginNodes.put(node, 0); // the node is on the margin
                            break;
                        }
                    } else if (colPixel == tile.getImageLeftX() || colPixel == tile.getImageRightX()) {
                        if (rowPixel >= tile.getImageTopY() && rowPixel <= tile.getImageBottomY()) {
                            marginNodes.put(node, 0); // the node is on the margin
                            break;
                        }
                    }
                }
            }
        }

        extractStabilityMargin(marginNodes, numberOfLayers);

        nodeCount = graph.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = graph.getNodeAt(i);
            BoundingBox box = node.getBox();

            if (box.getLeftX() > tile.getImageLeftX() && box.getTopY() > tile.getImageTopY() && box.getRightX() - 1 < tile.getImageRightX() && box.getBottomY() - 1 < tile.getImageBottomY()) {
                continue;
            } else if (marginNodes.containsKey(node)) {
                graph.removeEdgeToUnstableNode(node);
                node.setExpired(true);
            }
        }

        graph.removeExpiredNodes();
    }

    private void extractStabilityMargin(Object2IntMap<Node> borderNodesMap, int pmax) {
        List<Node> nodesToIterate = new ArrayList<Node>(borderNodesMap.size());
        ObjectIterator<Object2IntMap.Entry<Node>> it = borderNodesMap.object2IntEntrySet().iterator();
        while (it.hasNext()) {
            Object2IntMap.Entry<Node> entry = it.next();
            Node node = entry.getKey();
            nodesToIterate.add(node);
        }
        for (int i=0; i<nodesToIterate.size(); i++) {
            exploreDFS(nodesToIterate.get(i), 0, borderNodesMap, pmax);
        }
    }

    private void exploreDFS(Node node, int p, Object2IntMap<Node> borderNodesMap, int pmax) {
        if (p > pmax) {
            return;
        } else {
            if (borderNodesMap.containsKey(node)) {
                int value = borderNodesMap.getInt(node);
                if (p <= value) {
                    borderNodesMap.put(node, p);
                    int edgeCount = node.getEdgeCount();
                    for (int i=0; i<edgeCount; i++) {
                        Edge edge = node.getEdgeAt(i);
                        exploreDFS(edge.getTarget(), p + 1, borderNodesMap, pmax);
                    }
                } else {
                    return;
                }
            } else {
                borderNodesMap.put(node, p);
                int edgeCount = node.getEdgeCount();
                for (int i=0; i<edgeCount; i++) {
                    Edge edge = node.getEdgeAt(i);
                    exploreDFS(edge.getTarget(), p + 1, borderNodesMap, pmax);
                }
            }
        }
    }

    protected void writeNode(RandomAccessFile nodesFileStream, Node nodeToWrite) throws IOException {
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

    protected Node readNode(RandomAccessFile nodesFileStream) throws IOException {
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

    protected Node buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel) {
        return null;
    }

    private void writeGraph(Graph graph, String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        tempFolder.mkdir();
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            RandomAccessFile nodesFileStream = new RandomAccessFile(nodesFile, "rw");
            File edgesFile = new File(tempFolder, edgesPath);
            RandomAccessFile edgesFileStream = new RandomAccessFile(edgesFile, "rw");

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

    private void writeEdge(RandomAccessFile edgesFileStream, Edge edge) throws IOException {
        edgesFileStream.writeInt(edge.getTarget().getId());
        edgesFileStream.writeInt(edge.getBoundary());
    }

    private Graph readGraph(String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            RandomAccessFile nodesFileStream = new RandomAccessFile(nodesFile, "r");
            File edgesFile = new File(tempFolder, edgesPath);
            RandomAccessFile edgesFileStream = new RandomAccessFile(edgesFile, "r");

            int nodeCount = nodesFileStream.readInt();
            Int2ObjectMap<Node> nodesMap = new Int2ObjectArrayMap<Node>(nodeCount); // key = node id
            Graph graph = new Graph(nodeCount);
            for (int i=0; i<nodeCount; i++) {
                Node node = readNode(nodesFileStream);
                nodesMap.put(node.getId(), node);
                graph.addNode(node);
            }

            for (int i=0; i<nodeCount; i++) {
                Node node = graph.getNodeAt(i);
                int nodeId = edgesFileStream.readInt();
                assert(node.getId() == nodeId);

                int edgeCount = edgesFileStream.readInt();
                for (int k=0; k<edgeCount; k++) {
                    int targetNodeId = edgesFileStream.readInt();
                    Node targetNode = nodesMap.get(targetNodeId);
                    int boundary = edgesFileStream.readInt();
                    node.addEdge(targetNode, boundary);
                }
            }

            nodesFileStream.close();
            edgesFileStream.close();
            return graph;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeStabilityMargin(Object2IntMap<Node> stabilityMargin, String nodesPath, String edgesPath) {
        File cacheFolder = S2CacheUtils.getSentinel2CacheFolder();
        File tempFolder = new File(cacheFolder, "image-segmentation");
        tempFolder.mkdir();
        try {
            File nodesFile = new File(tempFolder, nodesPath);
            RandomAccessFile nodesFileStream = new RandomAccessFile(nodesFile, "rw");
            File edgesFile = new File(tempFolder, edgesPath);
            RandomAccessFile edgesFileStream = new RandomAccessFile(edgesFile, "rw");

            // write the number of nodes
            nodesFileStream.writeInt(stabilityMargin.size());

            ObjectIterator<Object2IntMap.Entry<Node>> it = stabilityMargin.object2IntEntrySet().iterator();
            while (it.hasNext()) {
                Object2IntMap.Entry<Node> entry = it.next();
                Node node = entry.getKey();
                int nodeId = entry.getIntValue();

                writeNode(nodesFileStream, node);

                // write the node id in the edge file
                edgesFileStream.writeInt(node.getId());

                int edgeCountToWrite = 0;
                int edgeCount = node.getEdgeCount();
                for (int k=0; k<edgeCount; k++) {
                    Edge edge = node.getEdgeAt(k);
                    Node targetNode = edge.getTarget();
                    if (stabilityMargin.containsKey(targetNode)) {
                        edgeCountToWrite++;
                    }
                }

                // write only edges pointing to nodes which are in the stability margin.
                edgesFileStream.writeInt(edgeCountToWrite);

                for (int k=0; k<edgeCount; k++) {
                    Edge edge = node.getEdgeAt(k);
                    Node targetNode = edge.getTarget();
                    if (stabilityMargin.containsKey(targetNode)) {
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
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_INDEX] = tileIndex - nbTilesX;
                } else {
                    // the tile is on the top row --> no top margin
                    tile.setTopMargin(0);
                    tile.rows[0] = 0;
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_INDEX] = -1;
                }

                // margin at the right
                if (col < nbTilesX - 1) {
                    tile.setRightMargin(margin);
                    tile.columns[1] = col * tileWidth + sizeX - 1; //sizeX
                    tile.tileNeighbors[NEIGHBORHOOD_RIGHT_INDEX] = tileIndex + 1;
                } else {
                    // the tile is on the right column --> no right margin
                    tile.setRightMargin(0);
                    tile.columns[1] = imageWidth - 1;
                    tile.tileNeighbors[NEIGHBORHOOD_RIGHT_INDEX] = -1;
                }

                // margin at the bottom
                if (row < nbTilesY - 1) {
                    tile.setBottomMargin(margin);
                    tile.rows[1] = row * tileHeight + sizeY - 1; // sizeY
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_INDEX] = tileIndex + nbTilesX;
                } else {
                    // the tile is on the bottom --> no bottom margin
                    tile.setBottomMargin(0);
                    tile.rows[1] = imageHeight - 1;
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_INDEX] = -1;
                }

                // margin at the left
                if (col > 0) {
                    tile.setLeftMargin(margin);
                    tile.columns[0] = col * tileWidth;
                    tile.tileNeighbors[NEIGHBORHOOD_LEFT_INDEX] = tileIndex-1;
                } else {
                    // the tile is on the left --> no left margin
                    tile.setLeftMargin(0);
                    tile.columns[0] = 0;
                    tile.tileNeighbors[NEIGHBORHOOD_LEFT_INDEX] = -1;
                }

                // store the tile region
                int regionLeftX = startX - tile.getLeftMargin();
                int regionTopY = startY - tile.getTopMargin();
                int regionWidth = sizeX + tile.getLeftMargin() + tile.getRightMargin();
                int regionHeight = sizeY + tile.getTopMargin() + tile.getBottomMargin();
                tile.setRegion(new BoundingBox(regionLeftX, regionTopY, regionWidth, regionHeight));

                // is there a neighbor at the rop right
                if (row > 0 && col < nbTilesX - 1) {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_RIGHT_INDEX] = tileIndex - nbTilesX + 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_RIGHT_INDEX] = -1;
                }

                // is there a neighbor at the bottom right
                if (col < nbTilesX - 1 && row < nbTilesY - 1) {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_RIGHT_INDEX] = tileIndex + nbTilesX + 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_RIGHT_INDEX] = -1;
                }

                // is there a neighbor at the bottom left
                if (row < nbTilesY - 1 && col > 0) {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_LEFT_INDEX] = tileIndex + nbTilesX - 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_LEFT_INDEX] = -1;
                }

                // is there a neighbor at the top left
                if (col > 0 && row > 0) {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_LEFT_INDEX] = tileIndex - nbTilesX - 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_LEFT_INDEX] = -1;
                }

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
