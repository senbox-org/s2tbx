package org.esa.s2tbx.grm.segmentation.tiles;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.grm.segmentation.*;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;
import org.esa.snap.utils.ObjectSizeCalculator;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractTileSegmenter {
    private static final Logger logger = Logger.getLogger(AbstractTileSegmenter.class.getName());

    private final Path temporaryFolderPath;
    private final float threshold;
    private final boolean addFourNeighbors;
    private final boolean fastSegmentation;

    private final int imageWidth;
    private final int imageHeight;
    private final int totalIterationsForSecondSegmentation;
    private final int iterationsForEachFirstSegmentation;
    private final int tileWidth;
    private final int tileHeight;
    private final int iterationsForEachSecondSegmentation;
    private final TilesBidimensionalArray tilesBidimensionalArray;
    private final long availableMemory;

    private long accumulatedMemory;
    private boolean isFusion;

    protected AbstractTileSegmenter(Dimension imageSize, Dimension tileSize, int totalIterationsForSecondSegmentation,
                                    int iterationsForEachFirstSegmentation, float threshold, boolean fastSegmentation)
                                    throws IOException {

        this.imageWidth = imageSize.width;
        this.imageHeight = imageSize.height;
        this.tileWidth = tileSize.width;
        this.tileHeight = tileSize.height;
        this.totalIterationsForSecondSegmentation = totalIterationsForSecondSegmentation;
        this.iterationsForEachFirstSegmentation = computeNumberOfFirstIterations(this.tileWidth, this.tileHeight);//iterationsForEachFirstSegmentation;
        this.fastSegmentation = fastSegmentation;
        this.threshold = threshold;

        this.addFourNeighbors = true;
        this.iterationsForEachSecondSegmentation = 3; // TODO: find a smart value
        this.tilesBidimensionalArray = new TilesBidimensionalArray();
        this.availableMemory = Runtime.getRuntime().totalMemory();
        this.temporaryFolderPath = Files.createTempDirectory("_temp");

        resetValues();
    }

    protected abstract Node buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel);

    public abstract AbstractSegmenter buildSegmenter(float threshold);

    public final int computeTileMargin() {
        return (int) (Math.pow(2, this.iterationsForEachFirstSegmentation + 1) - 2);
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getComputedTileCountX() {
        return this.tilesBidimensionalArray.getComputedTileCountX();
    }

    public int getComputedTileCountY() {
        return this.tilesBidimensionalArray.getComputedTileCountY();
    }

    public final ProcessingTile buildTile(int startX, int startY, int sizeX, int sizeY) {
        int margin = computeTileMargin();
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

    public final AbstractSegmenter runAllTilesSegmentation(Tile[] sourceTiles) throws IllegalAccessException, IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start running segmentation for all tiles: band count: " + sourceTiles.length + ", image width: " +this.imageWidth+", image height: "+this.imageHeight+", tile width: " + this.tileWidth+", tile height: "+this.tileHeight+", margin: "+computeTileMargin()+", number of first iterations: "+this.iterationsForEachFirstSegmentation);
        }

        // run the first partial segmentation
        runAllTilesFirstPartialSegmentation(sourceTiles);
        // run the second segmentation and merge the graph nodes
        return runAllTilesSecondSegmentation();
    }

    public final AbstractSegmenter runAllTilesSecondSegmentation() throws IllegalAccessException, IOException {
        if (logger.isLoggable(Level.FINE)) {
            int nbTilesX = this.tilesBidimensionalArray.getComputedTileCountX();
            int nbTilesY = this.tilesBidimensionalArray.getComputedTileCountY();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run second segmentation for all tiles. Total iterations: " + this.totalIterationsForSecondSegmentation + ", tile column count: " +nbTilesX+", tile row count: " + nbTilesY + ", acumulated memory:" + this.accumulatedMemory+", fusion: " + this.isFusion+", margin: "+computeTileMargin()+", number of second iterations: "+this.iterationsForEachSecondSegmentation);
        }

        int numberOfIterationsRemaining = this.totalIterationsForSecondSegmentation;
        int iteration = 0;
        while (this.accumulatedMemory > this.availableMemory && this.isFusion) {
            iteration++;
            runSecondPartialSegmentation(iteration);

            // update number of remaining iterations
            if (numberOfIterationsRemaining < this.iterationsForEachSecondSegmentation) {
                break;
            } else {
                numberOfIterationsRemaining -= this.iterationsForEachSecondSegmentation;
            }
        }
        if (this.accumulatedMemory > this.availableMemory) {
            throw new IllegalArgumentException("No more possible fusions, but can not store the output graph.");
        }
        try {
            return mergeAllGraphsAndAchieveSegmentation(numberOfIterationsRemaining);
        } finally {
            FileUtils.deleteTree(this.temporaryFolderPath.toFile());
        }
    }

    public final int computeTileRowIndex(ProcessingTile tile) {
        return tile.getImageTopY() / this.tileHeight;
    }

    public final int computeTileColumnIndex(ProcessingTile tile) {
        return tile.getImageLeftX() / this.tileWidth;
    }

    public ProcessingTile addTile(int tileRowIndex, int tileColumnIndex, ProcessingTile tileToAdd) {
        synchronized (this.tilesBidimensionalArray) {
            return this.tilesBidimensionalArray.addTile(tileRowIndex, tileColumnIndex, tileToAdd);
        }
    }

    public boolean canAddTile(int tileRowIndex, int tileColumnIndex) {
        synchronized (this.tilesBidimensionalArray) {
            ProcessingTile tile = this.tilesBidimensionalArray.getTileAt(tileRowIndex, tileColumnIndex);
            return (tile == null);
        }
    }

    public void runOneTileFirstSegmentation(Tile[] sourceTiles, ProcessingTile currentTile) throws IllegalAccessException, IOException {
        int tileRowIndex = computeTileRowIndex(currentTile);
        int tileColumnIndex = computeTileColumnIndex(currentTile);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, ""); // add an empty line
            logger.log(Level.FINEST, "Run tile first segmentation: tile region: " + tileRegionToString(currentTile.getRegion()) + ", tile row index: " +tileRowIndex+", tile column index: " + tileColumnIndex+", number of iterations: "+this.iterationsForEachFirstSegmentation+", margin: "+computeTileMargin()+", number of first iterations: "+this.iterationsForEachFirstSegmentation);
        }

        ProcessingTile existingTile = null;
        synchronized (this.tilesBidimensionalArray) {
            existingTile = this.tilesBidimensionalArray.getTileAt(tileRowIndex, tileColumnIndex);
        }
        if (existingTile == null) {
            throw new IllegalArgumentException("The tile with row index "+tileRowIndex+" and column index "+tileColumnIndex+" has not been added.");
        } else if (existingTile != currentTile) {
            throw new IllegalArgumentException("The tile with row index "+tileRowIndex+" and column index "+tileColumnIndex+" has already been computed.");
        }

        int numberOfNeighborLayers = computeNumberOfNeighborLayers();

        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        boolean complete = segmenter.update(sourceTiles, currentTile.getRegion(), this.iterationsForEachFirstSegmentation, this.fastSegmentation, this.addFourNeighbors);
        Graph graph = segmenter.getGraph();

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run tile first segmentation (after segmentation): graph node count: " + graph.getNodeCount());
        }

        // rescale the graph to be in the reference of the image
        graph.rescaleGraph(currentTile, this.imageWidth);

        // remove unstable segments
        graph.removeUnstableSegments(currentTile, this.imageWidth);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run tile first segmentation (after remove unstable nodes): graph node count: " + graph.getNodeCount());
        }

        long graphMemory = ObjectSizeCalculator.sizeOf(graph);

        synchronized (this.tilesBidimensionalArray) {
            this.accumulatedMemory += graphMemory;
            if (!complete) {
                this.isFusion = true;
            }
        }

        writeGraph(graph, currentTile.getNodeFileName(), currentTile.getEdgeFileName());

        // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
        List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run tile first segmentation (after detect border nodes): graph node count: "+graph.getNodeCount()+", boder node count: " + nodesToIterate.size());
        }

        Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run tile first segmentation: node count to write for stability margin: " + borderNodes.size());
        }

        writeStabilityMargin(borderNodes, currentTile.getNodeMarginFileName(), currentTile.getEdgeMarginFileName());
    }

    public int getIterationsForEachFirstSegmentation() {
        return iterationsForEachFirstSegmentation;
    }

    public int getTotalIterationsForSecondSegmentation() {
        return totalIterationsForSecondSegmentation;
    }

    public float getThreshold() {
        return threshold;
    }

    public boolean isFastSegmentation() {
        return fastSegmentation;
    }

    public boolean isAddFourNeighbors() {
        return addFourNeighbors;
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

    private void resetValues() {
        this.accumulatedMemory = 0;
        this.isFusion = false;
    }

    private int computeNumberOfNeighborLayers() {
        return (int) (Math.pow(2, this.iterationsForEachSecondSegmentation + 1) - 2);
    }

    private void runAllTilesFirstPartialSegmentation(Tile[] sourceTiles) throws IllegalAccessException, IOException {
        resetValues();

        int nbTilesX = this.imageWidth / this.tileWidth;
        int nbTilesY = this.imageHeight / this.tileHeight;

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run first segmentation for all tiles: band count: " + sourceTiles.length + ", tile column count: " +nbTilesX+", tile row count: " + nbTilesY+", margin: "+computeTileMargin());
        }

        for (int row = 0; row <nbTilesY; row++) {
            for (int col = 0; col<nbTilesX ; col++) {
                // compute current tile start and size
                int startX = col * this.tileWidth;
                int startY = row * this.tileHeight;
                int sizeX = this.tileWidth;
                int sizeY = this.tileHeight;
                // current tile size might be different for right and bottom borders
                if (col == nbTilesX - 1) {
                    sizeX += this.imageWidth % this.tileWidth;
                }
                if (row == nbTilesY - 1) {
                    sizeY += this.imageHeight % this.tileHeight;
                }

                ProcessingTile currentTile = buildTile(startX, startY, sizeX, sizeY);
                int tileColumnIndex = col;//computeTileColumnIndex(currentTile);
                int tileRowIndex = row;//computeTileRowIndex(currentTile);
                ProcessingTile oldTile = addTile(tileRowIndex, tileColumnIndex, currentTile);
                if (oldTile == null) {
                    runOneTileFirstSegmentation(sourceTiles, currentTile);
                } else {
                    if (logger.isLoggable(Level.FINE)) {
                        int tileMargin = computeTileMargin();
                        logger.log(Level.FINE, ""); // add an empty line
                        logger.log(Level.FINE, "Run tile first segmentation: tile already added: tile region: " + tileRegionToString(currentTile.getRegion()) + ", tile row index: " +tileRowIndex+", tile column index: " + tileColumnIndex+", number of iterations: "+this.iterationsForEachFirstSegmentation+", margin: "+computeTileMargin()+", number of first iterations: "+this.iterationsForEachFirstSegmentation);
                    }
                }
            }
        }
    }

    private void runSecondPartialSegmentation(int iteration) throws IllegalAccessException, IOException {
        int nbTilesX = this.tilesBidimensionalArray.getComputedTileCountX();
        int nbTilesY = this.tilesBidimensionalArray.getComputedTileCountY();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run second segmentation: iteration: "+iteration+", tile column count: " +nbTilesX+", tile row count: " + nbTilesY + ", acumulated memory: " + this.accumulatedMemory+", fusion: " + this.isFusion+", margin: "+computeTileMargin());
        }

        resetValues();

        int numberOfNeighborLayers = computeNumberOfNeighborLayers();

        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, ""); // add an empty line
                    logger.log(Level.FINEST, "Run second segmentation: iteration: "+iteration+", tile region: " +tileRegionToString(currentTile.getRegion())+ ", tile row index: " + row + ", tile column index: " + col);
                }

                Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after read graph): graph node count: " +graph.getNodeCount()+", tile row index: " + row + ", tile column index: " + col);
                }

                addStabilityMargin(graph, row, col, nbTilesX, nbTilesY);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after add stability margin): graph node count: " +graph.getNodeCount()+", tile row index: " + row + ", tile column index: " + col);
                }

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after building border pixel map): graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after removing duplicate nodes): graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);

                removeUselessNodes(graph, currentTile, this.imageWidth, numberOfNeighborLayers);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after removing useless nodes): graph node count: " +graph.getNodeCount()+", numberOfNeighborLayers: "+numberOfNeighborLayers+", tile row index: " + row + ", tile column index: " + col);
                }

                // build the segmenter
                AbstractSegmenter segmenter = buildSegmenter(this.threshold);
                segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
                boolean merged = segmenter.performAllIterationsWithLMBF(this.iterationsForEachSecondSegmentation);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after segmentation): graph node count: " +graph.getNodeCount()+", completed="+(!merged)+", tile row index: " + row + ", tile column index: " + col);
                }

                graph.removeUnstableSegments(currentTile, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (after removing unstable nodes): graph node count: " +graph.getNodeCount()+", tile row index: " + row + ", tile column index: " + col);
                }

                this.accumulatedMemory += ObjectSizeCalculator.sizeOf(graph);
                if (merged) {
                    this.isFusion = true;
                }

                writeGraph(graph, currentTile.getNodeFileName(), currentTile.getEdgeFileName());
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run second segmentation: (extract the stability margin for the next round): tile column count: " +nbTilesX+", tile row count: " + nbTilesY + ", acumulated memory:" + this.accumulatedMemory+", fusion: " + this.isFusion);
        }

        // during this step we extract the stability margin for the next round
        for(int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col<nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, ""); // add an empty line
                    logger.log(Level.FINEST, "Run second segmentation: (extract the stability margin): iteration: "+iteration+", tile region: " +tileRegionToString(currentTile.getRegion())+", tile row index: " + row + ", tile column index: " + col);
                }

                Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (extract the stability margin - after read graph): graph node count: "+graph.getNodeCount()+", tile row index: " + row + ", tile column index: " + col);
                }

                List<Node> nodesToIterate = graph.detectBorderNodes(currentTile, this.imageWidth, this.imageHeight);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (extract the stability margin): border node count: " + nodesToIterate.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Run second segmentation: (extract the stability margin): node count to write for stability margin: " + borderNodes.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                writeStabilityMargin(borderNodes, currentTile.getNodeMarginFileName(), currentTile.getEdgeMarginFileName());
            }
        }
    }

    private AbstractSegmenter mergeAllGraphsAndAchieveSegmentation(int numberOfIterations) throws IOException {
        Graph graph = new Graph();
        int nbTilesX = this.tilesBidimensionalArray.getComputedTileCountX();
        int nbTilesY = this.tilesBidimensionalArray.getComputedTileCountY();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge all graphs: number of iterations: "+numberOfIterations+", tile column count: " +nbTilesX+", tile row count: " + nbTilesY);
        }

        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                if (logger.isLoggable(Level.FINEST)) {
                    if (row ==0 && col == 0) {
                        logger.log(Level.FINEST, ""); // add an empty line
                    }
                    logger.log(Level.FINEST, "Merge all graphs: (before insert nodes from tile): number of iterations: "+numberOfIterations+", graph node count: "+graph.getNodeCount()+", tile region: "+tileRegionToString(currentTile.getRegion())+", tile row index: " + row + ", tile column index: " + col);
                }

                insertNodesFromTile(graph, currentTile, false);
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge all graphs: (removing duplicated nodes): number of iterations: "+numberOfIterations+", graph node count: "+graph.getNodeCount() + ", tile column count: " +nbTilesX+", tile row count: " + nbTilesY);
        }

        // removing duplicated nodes and updating neighbors
        for (int row = 0; row < nbTilesY; row++) {
            for (int col = 0; col < nbTilesX; col++) {
                ProcessingTile currentTile = this.tilesBidimensionalArray.getTileAt(row, col);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, ""); // add an empty line
                    logger.log(Level.FINEST, "Merge all graphs: (removing duplicated nodes): number of iterations: "+numberOfIterations+", tile region: " +tileRegionToString(currentTile.getRegion())+", tile row index: "+row+", tile column index: "+col);
                }

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMap(currentTile, row, col, nbTilesX, nbTilesY, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Merge all graphs: (removing duplicated nodes - after building border pixel map): graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Merge all graphs: (removing duplicated nodes - after removing duplicate nodes): graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size()+", tile row index: " + row + ", tile column index: " + col);
                }

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge all graphs: (before segmentation): number of iterations: "+numberOfIterations+", graph node count: "+graph.getNodeCount()+", tile column count: "+nbTilesX+", tile row count: "+nbTilesY);
        }

        // segmentation of the graph
        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
        segmenter.performAllIterationsWithLMBF(numberOfIterations);
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

    private static String tileRegionToString(BoundingBox region) {
        StringBuilder str = new StringBuilder();
        str.append("[x=")
                .append(region.getLeftX())
                .append(", y=")
                .append(region.getTopY())
                .append(", width=")
                .append(region.getWidth())
                .append(", height=")
                .append(region.getHeight())
                .append("]");
        return str.toString();
    }

    private void insertNodesFromTile(Graph graph, ProcessingTile tile, boolean margin) throws IOException {
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

    private void addStabilityMargin(Graph graph, int row, int col, int nbTilesX, int nbTilesY) throws IOException {
        // margin to retrieve at top
        if (row > 0) { // (startYWithoutMargin > 0) { //(row > 0) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row-1, col), true);
        }
        // margin to retrieve at right
        if (col < nbTilesX - 1) { //(finishXWithoutMargin < this.imageWidth) { //(col < nbTilesX - 1) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row, col+1), true);
        }
        // margin to retrieve at bottom
        if (row < nbTilesY - 1) { //(finishYWithoutMargin < this.imageHeight) { //(row < nbTilesY - 1) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col), true);
        }
        // margin to retrieve at left
        if (col > 0) { // (startXWithoutMargin > 0) { //(col > 0) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row, col-1), true);
        }
        // margin to retrieve at top right
        if (row > 0 && col < nbTilesX - 1) { // (startYWithoutMargin > 0 && finishXWithoutMargin < this.imageWidth) { //(row > 0 && col < nbTilesX - 1) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row-1, col+1), true);
        }
        // margin to retrieve at bottom right
        if (row < nbTilesY - 1 && col < nbTilesX - 1) { // (finishYWithoutMargin < this.imageHeight && finishXWithoutMargin < this.imageWidth) { //(row < nbTilesY - 1 && col < nbTilesX - 1) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col+1), true);
        }
        // margin to retrieve at bottom left
        if (row < nbTilesY - 1 && col > 0) { // (finishYWithoutMargin < this.imageHeight && startXWithoutMargin > 0) { //(row < nbTilesY - 1 && col > 0) {
            insertNodesFromTile(graph, this.tilesBidimensionalArray.getTileAt(row+1, col-1), true);
        }
        // margin to retrieve at top left
        if (row > 0 && col > 0) { // (startYWithoutMargin > 0 && startXWithoutMargin > 0) { //(row > 0 && col > 0) {
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
                IntIterator itCells = borderCells.iterator();
                while (itCells.hasNext()) {
                    int gridId = itCells.nextInt();
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

        Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfLayers);

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

    private static Int2ObjectMap<Node> extractStabilityMargin(List<Node> nodesToIterate, int numberOfLayers) {
        int defaultMissingValue = -1;
        Int2IntMap borderNodesValues = new Int2IntOpenHashMap(nodesToIterate.size());
        borderNodesValues.defaultReturnValue(defaultMissingValue);
        Int2ObjectMap<Node> borderNodes = new Int2ObjectLinkedOpenHashMap<Node>(nodesToIterate.size());
        for (int i=0; i<nodesToIterate.size(); i++) {
            Node node = nodesToIterate.get(i);
            borderNodesValues.put(node.getId(), 0);
            borderNodes.put(node.getId(), node);
        }
        for (int i=0; i<nodesToIterate.size(); i++) {
            exploreDFS(nodesToIterate.get(i), 0, borderNodesValues, borderNodes, defaultMissingValue, numberOfLayers);
        }
        return borderNodes;
    }

    private static void exploreDFS(Node node, int p, Int2IntMap borderNodesValues, Int2ObjectMap<Node> borderNodes, int defaultMissingValue, int numberOfLayers) {
        if (p > numberOfLayers) {
            return;
        } else {
            int value = borderNodesValues.get(node.getId());
            if (value != defaultMissingValue) {
                if (p <= value) {
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
                exploreDFS(edge.getTarget(), p + 1, borderNodesValues, borderNodes, defaultMissingValue, numberOfLayers);
            }
        }
    }

    private void writeGraph(Graph graph, String nodesPath, String edgesPath) throws IOException {
        BufferedOutputStreamWrapper nodesFileStream = null;
        BufferedOutputStreamWrapper edgesFileStream = null;
        try {
            File nodesFile = new File(this.temporaryFolderPath.toFile(), nodesPath);
            nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = new File(this.temporaryFolderPath.toFile(), edgesPath);
            edgesFileStream = new BufferedOutputStreamWrapper(edgesFile);

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
        } finally {
            if (nodesFileStream != null) {
                try {
                    nodesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
            if (edgesFileStream != null) {
                try {
                    edgesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    private void writeEdge(BufferedOutputStreamWrapper edgesFileStream, Edge edge) throws IOException {
        edgesFileStream.writeInt(edge.getTarget().getId());
        edgesFileStream.writeInt(edge.getBoundary());
    }

    private Graph readGraph(String nodesPath, String edgesPath) throws IOException {
        BufferedInputStreamWrapper nodesFileStream = null;
        BufferedInputStreamWrapper edgesFileStream = null;
        try {
            File nodesFile = new File(this.temporaryFolderPath.toFile(), nodesPath);
            nodesFileStream = new BufferedInputStreamWrapper(nodesFile);

            int nodeCount = nodesFileStream.readInt();
            Int2ObjectMap<Node> nodesMap = new Int2ObjectLinkedOpenHashMap<Node>(nodeCount);
            Graph graph = new Graph();
            for (int i=0; i<nodeCount; i++) {
                Node node = readNode(nodesFileStream);
                nodesMap.put(node.getId(), node);
                graph.addNode(node);
            }

            File edgesFile = new File(this.temporaryFolderPath.toFile(), edgesPath);
            edgesFileStream = new BufferedInputStreamWrapper(edgesFile);

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

            return graph;
        } finally {
            if (nodesFileStream != null) {
                try {
                    nodesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
            if (edgesFileStream != null) {
                try {
                    edgesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    private void writeStabilityMargin(Int2ObjectMap<Node> borderNodes, String nodesPath, String edgesPath) throws IOException {
        BufferedOutputStreamWrapper nodesFileStream = null;
        BufferedOutputStreamWrapper edgesFileStream = null;
        try {
            File nodesFile = new File(this.temporaryFolderPath.toFile(), nodesPath);
            nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = new File(this.temporaryFolderPath.toFile(), edgesPath);
            edgesFileStream = new BufferedOutputStreamWrapper(edgesFile);

            // write the number of nodes
            nodesFileStream.writeInt(borderNodes.size());

            ObjectIterator<Int2ObjectMap.Entry<Node>> it = borderNodes.int2ObjectEntrySet().iterator();
            while (it.hasNext()) {
                Int2ObjectMap.Entry<Node> entry = it.next();
                Node node = entry.getValue();
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
        } finally {
            if (nodesFileStream != null) {
                try {
                    nodesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
            if (edgesFileStream != null) {
                try {
                    edgesFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    private static int computeNumberOfFirstIterations(int tileWidth, int tileHeight) {
        int numberOfFirstIterations = 1;
        int maxMargin = Math.min(tileWidth, tileHeight) / 2;
        int currMargin = (int) (Math.pow(2, numberOfFirstIterations + 1) - 2);
        while (currMargin < maxMargin) {
            numberOfFirstIterations++;
            currMargin = (int) (Math.pow(2, numberOfFirstIterations + 1) - 2);
        }
        numberOfFirstIterations--;
        return numberOfFirstIterations;
    }
}
