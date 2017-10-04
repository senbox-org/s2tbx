package org.esa.s2tbx.grm.segmentation.tiles;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.grm.RegionMergingProcessingParameters;
import org.esa.s2tbx.grm.segmentation.*;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;
import org.esa.snap.utils.ObjectMemory;

import java.awt.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractTileSegmenter {
    private static final Logger logger = Logger.getLogger(AbstractTileSegmenter.class.getName());

    private final Path temporaryFolder;
    private final float threshold;
    private final boolean addFourNeighbors;
    private final boolean fastSegmentation;
    private final int imageWidth;
    private final int imageHeight;
    private final int totalIterationsForSecondSegmentation;
    private final int tileWidth;
    private final int tileHeight;
    private final int iterationsForEachSecondSegmentation;
    private final TileSegmenterMetadata tileSegmenterMetadata;
    private final int threadCount;
    private final Executor threadPool;

    protected AbstractTileSegmenter(RegionMergingProcessingParameters processingParameters, int totalIterationsForSecondSegmentation,
                                    float threshold, boolean fastSegmentation, Path temporaryParentFolder)
                                    throws IOException {

        this.imageWidth = processingParameters.getImageWidth();
        this.imageHeight = processingParameters.getImageHeight();
        this.tileWidth = processingParameters.getTileWidth();
        this.tileHeight = processingParameters.getTileHeight();
        this.totalIterationsForSecondSegmentation = totalIterationsForSecondSegmentation;
        this.fastSegmentation = fastSegmentation;
        this.threshold = threshold;

        String temporaryFolderName = "segmentation" + Long.toString(System.currentTimeMillis());
        this.temporaryFolder = Files.createDirectories(temporaryParentFolder.resolve(temporaryFolderName));

        this.addFourNeighbors = true;
        this.iterationsForEachSecondSegmentation = 3; // TODO: find a smart value
        this.tileSegmenterMetadata = new TileSegmenterMetadata();

        this.threadCount = processingParameters.getThreadCount();
        this.threadPool = processingParameters.getThreadPool();
    }

    protected abstract Node buildNode(int nodeId, BoundingBox box, Contour contour, int perimeter, int area, int numberOfComponentsPerPixel);

    public abstract AbstractSegmenter buildSegmenter(float threshold);

    public final int computeTileMargin() {
        return computeTileMargin(this.tileWidth, this.tileHeight);
    }

    public final int getImageHeight() {
        return this.imageHeight;
    }

    public final int getImageWidth() {
        return this.imageWidth;
    }

    public final int getTileHeight() {
        return this.tileHeight;
    }

    public final int getTileWidth() {
        return this.tileWidth;
    }

    public final ProcessingTile buildTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight) {
        int margin = computeTileMargin();
        return AbstractTileSegmenter.buildTile(tileLeftX, tileTopY, tileWidth, tileHeight, margin, this.imageWidth, this.imageHeight);
    }

    public final void checkTemporaryTileFiles(int iteration, int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int rowIndex, int columnIndex) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Check tile temporary files: row index: " + rowIndex + ", column index: " + columnIndex+", iteration: "+iteration+", bounds [x=" + tileLeftX+", y="+tileTopY+", width="+tileWidth+", height="+tileHeight+"]");
        }

        ProcessingTile tileToCheck = buildTile(tileLeftX, tileTopY, tileWidth, tileHeight);

        StringBuilder exceptionMessage = new StringBuilder();

        File nodesFile = new File(this.temporaryFolder.toFile(), tileToCheck.getNodeFileName());
        if (!nodesFile.exists()) {
            exceptionMessage.append(" The nodes file '"+nodesFile.getName()+"' is missing.");
        }
        File edgesFile = new File(this.temporaryFolder.toFile(), tileToCheck.getEdgeFileName());
        if (!edgesFile.exists()) {
            exceptionMessage.append(" The edges file '"+edgesFile.getName()+"' is missing.");
        }

        File nodeMarginsFile = new File(this.temporaryFolder.toFile(), tileToCheck.getNodeMarginFileName());
        if (!nodeMarginsFile.exists()) {
            exceptionMessage.append(" The node margins file '"+nodeMarginsFile.getName()+"' is missing.");
        }
        File edgeMarginsFile = new File(this.temporaryFolder.toFile(), tileToCheck.getEdgeMarginFileName());
        if (!edgeMarginsFile.exists()) {
            exceptionMessage.append(" The edge margins file '"+edgeMarginsFile.getName()+"' is missing.");
        }
        if (exceptionMessage.length() > 0) {
            exceptionMessage.insert(0, "Missing tile temporary files: row index: " + rowIndex+", column index: "+columnIndex+", iteration: "+iteration+".");
            throw new FileNotFoundException(exceptionMessage.toString());
        }
    }

    private void deleteTemporaryFolder() {
        boolean deleted = FileUtils.deleteTree(this.temporaryFolder.toFile());
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            if (deleted) {
                logger.log(Level.FINE, "Successfully deleted the temporary folder path '" + getTemporaryFolderPath()+"'");
            } else {
                logger.log(Level.FINE, "Failed to delete the temporary folder path '" + getTemporaryFolderPath()+"'");
            }
        }
    }

    private int runSecondSegmentationsInParallel() throws Exception {
        // run the second segmentation
        if (logger.isLoggable(Level.FINE)) {
            int tileCountX = this.tileSegmenterMetadata.getComputedTileCountX();
            int tileCountY = this.tileSegmenterMetadata.getComputedTileCountY();
            int tileMargin = computeTileMargin();
            float freeMemoryKiloBytes = Runtime.getRuntime().freeMemory() / 1024.0f;
            float totalMemoryKiloBytes = Runtime.getRuntime().totalMemory() / 1024.0f;
            float accumulatedKiloBytes = this.tileSegmenterMetadata.getAccumulatedMemory() / 1024.0f;
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Second segmentation for all tiles: tile column count: "+tileCountX+", tile row count: "+tileCountY+", total memory: "+totalMemoryKiloBytes+" KB, free memory: "+freeMemoryKiloBytes+" KB, acumulated memory: "+accumulatedKiloBytes+" KB, fusion: " + this.tileSegmenterMetadata.isFusion()+", margin: "+tileMargin+", number of second iterations: "+this.iterationsForEachSecondSegmentation);
        }

        int numberOfIterationsRemaining = this.totalIterationsForSecondSegmentation;
        int iteration = 0;
        while (numberOfIterationsRemaining >= this.iterationsForEachSecondSegmentation && this.tileSegmenterMetadata.canRunSecondPartialSegmentation()) {
            this.tileSegmenterMetadata.resetValues();
            iteration++;

            runSecondPartialSegmentationInParallel(iteration);

            numberOfIterationsRemaining -= this.iterationsForEachSecondSegmentation;
        }
        return numberOfIterationsRemaining;
    }

    public final void runFirstSegmentationsInParallel(SegmentationSourceProductPair segmentationSourceProducts) throws Exception {
        FirstTileParallelComputing tileFirstSegmentationHelper = new FirstTileParallelComputing(segmentationSourceProducts, this);
        tileFirstSegmentationHelper.executeInParallel(this.threadCount, this.threadPool);
    }

    public final void runDifferenceFirstSegmentationsInParallel(Product currentSourceProduct, String[] currentSourceBandNames,
                                                                Product previousSourceProduct, String[] previousSourceBandNames)
                                                                throws Exception {

        DifferenceFirstTileParallelComputing tileFirstSegmentationHelper = new DifferenceFirstTileParallelComputing(currentSourceProduct, currentSourceBandNames,
                                                                                     previousSourceProduct, previousSourceBandNames, this);
        tileFirstSegmentationHelper.executeInParallel(this.threadCount, this.threadPool);
    }

    private void runSecondPartialSegmentationInParallel(int iteration) throws Exception {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Before checking the tiles temporary files: iteration: " +iteration);
        }

        CheckTemporaryTileFilesParallelComputing helper = new CheckTemporaryTileFilesParallelComputing(iteration, this);
        helper.executeInParallel(0, null);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "After checking the tiles temporary files: iteration: " +iteration);
        }

        // log a message
        int tileCountX = this.tileSegmenterMetadata.getComputedTileCountX();
        int tileCountY = this.tileSegmenterMetadata.getComputedTileCountY();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run second segmentation: iteration: "+iteration+", tile column count: " +tileCountX+", tile row count: " + tileCountY + ", acumulated memory: " + this.tileSegmenterMetadata.getAccumulatedMemory()+", fusion: " + this.tileSegmenterMetadata.isFusion()+", margin: "+computeTileMargin());
        }

        int numberOfNeighborLayers = computeNumberOfNeighborLayers();

        runSecondSegmentationInParallel(iteration, this.threadCount, this.threadPool, numberOfNeighborLayers);

        // log a message
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Run second segmentation: (extract the stability margin for the next round): tile column count: " +tileCountX+", tile row count: " + tileCountY + ", acumulated memory: " + this.tileSegmenterMetadata.getAccumulatedMemory()+", fusion: " + this.tileSegmenterMetadata.isFusion());
        }

        // during this step we extract the stability margin for the next round
        SecondTileStabilityMarginParallelComputing segmentationHelper = new SecondTileStabilityMarginParallelComputing(iteration, numberOfNeighborLayers, this);
        segmentationHelper.executeInParallel(this.threadCount, this.threadPool);
    }

    private void runSecondSegmentationInParallel(int iteration, int threadCount, Executor threadPool, int numberOfNeighborLayers)
                                       throws Exception {

        SecondTileParallelComputing tileSecondSegmentationHelper = new SecondTileParallelComputing(iteration, numberOfNeighborLayers, this);
        tileSecondSegmentationHelper.executeInParallel(threadCount, threadPool);
    }

    public final void doClose() {
        // reset the references
        WeakReference<Path> referenceTemporaryFolder = new WeakReference<Path>(this.temporaryFolder);
        referenceTemporaryFolder.clear();
        WeakReference<TileSegmenterMetadata> referenceMetadata = new WeakReference<TileSegmenterMetadata>(this.tileSegmenterMetadata);
        referenceMetadata.clear();
    }

    public final AbstractSegmenter runSecondSegmentationsAndMergeGraphs() throws Exception {
        try {
            int numberOfRemainingIterations = runSecondSegmentationsInParallel();
            return mergeGraphsAndAchieveSegmentation(numberOfRemainingIterations);
        } finally {
            deleteTemporaryFolder();
        }
    }

    public void runTileFirstSegmentation(TileDataSource[] sourceTiles, ProcessingTile tileToProcess, int tileRowIndex, int tileColumnIndex)
                                         throws IllegalAccessException, IOException, InterruptedException {

        if (logger.isLoggable(Level.FINE)) {
            int iterationsForEachFirstSegmentation = computeNumberOfFirstIterations(this.tileWidth, this.tileHeight);
            int tileMargin = computeTileMargin();
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "First tile segmentation: row index: " + tileRowIndex + ", column index: " + tileColumnIndex + ", margin: " + tileMargin + ", bounds: " + tileRegionToString(tileToProcess.getRegion()) + ", first number of iterations: " + iterationsForEachFirstSegmentation);
        }

        ProcessingTile existingTile = null;
        synchronized (this.tileSegmenterMetadata) {
            existingTile = this.tileSegmenterMetadata.addTile(tileRowIndex, tileColumnIndex, tileToProcess);
        }
        if (existingTile != null) {
            throw new IllegalArgumentException("The tile has been already processed: row index: " + tileRowIndex + ", column index: " + tileColumnIndex + ", bounds: " + tileRegionToString(tileToProcess.getRegion()) + ".");
        } else {
            boolean success = false;
            try {
                int numberOfNeighborLayers = computeNumberOfNeighborLayers();

                AbstractSegmenter segmenter = buildSegmenter(this.threshold);
                int iterationsForEachFirstSegmentation = computeNumberOfFirstIterations(this.tileWidth, this.tileHeight);

                boolean complete = segmenter.update(sourceTiles, tileToProcess.getRegion(), iterationsForEachFirstSegmentation, this.fastSegmentation, this.addFourNeighbors);
                Graph graph = segmenter.getGraph();

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "First tile segmentation (after segmentation): row index: " + tileRowIndex + ", column index: " + tileColumnIndex + ", graph node count: " + graph.getNodeCount());
                }

                // rescale the graph to be in the reference of the image
                graph.rescaleGraph(tileToProcess, this.imageWidth);

                int graphNodeCountBeforeRemoving = graph.getNodeCount();

                // remove unstable segments
                graph.removeUnstableSegmentsInParallel(this.threadCount, this.threadPool, tileToProcess, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    int removedNodeCount = graphNodeCountBeforeRemoving - graph.getNodeCount();
                    logger.log(Level.FINEST, "First tile segmentation (after removing unstable nodes): row index: " + tileRowIndex + ", column index: " + tileColumnIndex + ", graph node count: " + graph.getNodeCount() + ", removed node count: " + removedNodeCount);
                }

                // extract stability margin for all borders different from 0 imageWidth-1 and imageHeight -1 and write them to the stability margin
                List<Node> nodesToIterate = graph.detectBorderNodes(this.threadCount, this.threadPool, tileToProcess, this.imageWidth, this.imageHeight);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "First tile segmentation (after detecting border nodes): graph node count: " + graph.getNodeCount() + ", border node count: " + nodesToIterate.size());
                }

                Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "First tile segmentation (after extract stability margin): graph node count: " + graph.getNodeCount() + ", stability margin node count: " + borderNodes.size());
                }

                writeGraph(graph, tileToProcess.getNodeFileName(), tileToProcess.getEdgeFileName());

                writeStabilityMargin(borderNodes, tileToProcess.getNodeMarginFileName(), tileToProcess.getEdgeMarginFileName());

                long graphMemory = ObjectMemory.computeSizeOf(graph);

                synchronized (this.tileSegmenterMetadata) {
                    this.tileSegmenterMetadata.addAccumulatedMemory(graphMemory, !complete);
                }

                graph.doClose();

                // reset the references
                WeakReference<AbstractSegmenter> referenceSegmenter = new WeakReference<AbstractSegmenter>(segmenter);
                referenceSegmenter.clear();
                WeakReference<Graph> referenceGraph = new WeakReference<Graph>(graph);
                referenceGraph.clear();
                WeakReference<List<Node>> referenceNodesToIterate = new WeakReference<List<Node>>(nodesToIterate);
                referenceNodesToIterate.clear();
                WeakReference<Int2ObjectMap<Node>> referenceBorderNodes = new WeakReference<Int2ObjectMap<Node>>(borderNodes);
                referenceBorderNodes.clear();

//                segmenter = null;
//                graph = null;
//                nodesToIterate = null;
//                borderNodes = null;
//                System.gc();

                success = true;
            } finally {
                if (!success) {
                    ProcessingTile removedTile = null;
                    synchronized (this.tileSegmenterMetadata) {
                        removedTile = this.tileSegmenterMetadata.removeTile(tileRowIndex, tileColumnIndex);
                    }
                    if (removedTile != tileToProcess) {
                        throw new IllegalArgumentException("The removed tile is different: row index: " + tileRowIndex + ", column index: " + tileColumnIndex + ", bounds: " + tileRegionToString(removedTile.getRegion()) + ".");
                    }
                }
            }
        }
    }

    public final int computeIterationsForEachFirstSegmentation() {
        return computeNumberOfFirstIterations(this.tileWidth, this.tileHeight);
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

    private int computeNumberOfNeighborLayers() {
        return (int) (Math.pow(2, this.iterationsForEachSecondSegmentation + 1) - 2);
    }

    public final void runTileSecondSegmentation(int iteration, int rowIndex, int columnIndex, int numberOfNeighborLayers)
                                                throws IllegalAccessException, IOException, InterruptedException {

        ProcessingTile currentTile = this.tileSegmenterMetadata.getTileAt(rowIndex, columnIndex);
        if (currentTile == null) {
            throw new NullPointerException("The tile to process is null: row index: " + rowIndex + ", column index: " + columnIndex+", iteration: "+iteration);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Second tile segmentation: row index: " + rowIndex + ", column index: " + columnIndex+", iteration: "+iteration+", bounds: " +tileRegionToString(currentTile.getRegion())+", iterations for each second segmentation: "+this.iterationsForEachSecondSegmentation);
        }

        int tileCountX = this.tileSegmenterMetadata.getComputedTileCountX();
        int tileCountY = this.tileSegmenterMetadata.getComputedTileCountY();
        Graph graph = readGraphSecondPartialSegmentation(currentTile, rowIndex, columnIndex, tileCountX, tileCountY);

        Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMapInParallel(this.threadCount, this.threadPool, currentTile, rowIndex, columnIndex, tileCountX, tileCountY, this.imageWidth);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (after building border pixel map): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size());
        }

        int graphNodeCountBeforeRemoving = graph.getNodeCount();

        graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

        if (logger.isLoggable(Level.FINEST)) {
            int removedNodeCount = graphNodeCountBeforeRemoving - graph.getNodeCount();
            logger.log(Level.FINEST, "Second tile segmentation (after removing duplicate nodes): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", removed node count: "+removedNodeCount+", map size: "+borderPixelMap.size());
        }

        updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (after updating neighbors): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size());
        }

        // remove the useless nodes
        graphNodeCountBeforeRemoving = graph.getNodeCount();
        List<Node> nodesToIterate = graph.findUselessNodesInParallel(this.threadCount, this.threadPool, currentTile, this.imageWidth);
        Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);
        graph.removeUselessNodes(borderNodes, currentTile);

        if (logger.isLoggable(Level.FINEST)) {
            int removedNodeCount = graphNodeCountBeforeRemoving - graph.getNodeCount();
            logger.log(Level.FINEST, "Second tile segmentation (after removing useless nodes): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", removed node count: "+removedNodeCount+", number of neighbor layers: "+numberOfNeighborLayers);
        }

        // build the segmenter
        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
        boolean merged = segmenter.performAllIterationsWithLMBF(this.iterationsForEachSecondSegmentation);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (after segmentation): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", completed="+(!merged));
        }

        graphNodeCountBeforeRemoving = graph.getNodeCount();

        graph.removeUnstableSegmentsInParallel(this.threadCount, this.threadPool, currentTile, this.imageWidth);

        if (logger.isLoggable(Level.FINEST)) {
            int removedNodeCount = graphNodeCountBeforeRemoving - graph.getNodeCount();
            logger.log(Level.FINEST, "Second tile segmentation (after removing unstable nodes): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", removed node count: "+removedNodeCount);
        }

        writeGraph(graph, currentTile.getNodeFileName(), currentTile.getEdgeFileName());

        long graphMemory = ObjectMemory.computeSizeOf(graph);

        synchronized (this.tileSegmenterMetadata) {
            this.tileSegmenterMetadata.addAccumulatedMemory(graphMemory, merged);
        }

        graph.doClose();

        // reset the references
        WeakReference<AbstractSegmenter> referenceSegmenter = new WeakReference<AbstractSegmenter>(segmenter);
        referenceSegmenter.clear();
        WeakReference<Graph> referenceGraph = new WeakReference<Graph>(graph);
        referenceGraph.clear();
        WeakReference<Int2ObjectMap<List<Node>>> referenceBorderPixelMap = new WeakReference<Int2ObjectMap<List<Node>>>(borderPixelMap);
        referenceBorderPixelMap.clear();
        WeakReference<List<Node>> referenceNodesToIterate = new WeakReference<List<Node>>(nodesToIterate);
        referenceNodesToIterate.clear();
        WeakReference<Int2ObjectMap<Node>> referenceBorderNodes = new WeakReference<Int2ObjectMap<Node>>(borderNodes);
        referenceBorderNodes.clear();

//        segmenter = null;
//        graph = null;
//        borderPixelMap = null;
//        nodesToIterate = null;
//        borderNodes = null;
//        System.gc();
    }

    public final void runTileStabilityMarginSecondSegmentation(int iteration, int rowIndex, int columnIndex, int numberOfNeighborLayers) throws IOException, InterruptedException {
        ProcessingTile currentTile = this.tileSegmenterMetadata.getTileAt(rowIndex, columnIndex);
        if (currentTile == null) {
            throw new NullPointerException("The tile to process is null: row index: " + rowIndex + ", column index: " + columnIndex+", iteration: "+iteration);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Second tile segmentation (extract the stability margin): row index: " + rowIndex + ", column index: " + columnIndex+", iteration: "+iteration+", tile region: " +tileRegionToString(currentTile.getRegion()));
        }

        Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (extract the stability margin - after read graph): row index: " + rowIndex + ", column index: " + columnIndex +", graph node count: "+graph.getNodeCount());
        }

        List<Node> nodesToIterate = graph.detectBorderNodes(this.threadCount, this.threadPool, currentTile, this.imageWidth, this.imageHeight);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (extract the stability margin - after detecting border nodes): row index: " + rowIndex + ", column index: " + columnIndex+", border node count: " + nodesToIterate.size());
        }

        Int2ObjectMap<Node> borderNodes = extractStabilityMargin(nodesToIterate, numberOfNeighborLayers);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Second tile segmentation (extract the stability margin - after extracting border nodes): row index: " + rowIndex + ", column index: " + columnIndex+", node count to write for stability margin: " + borderNodes.size());
        }

        writeStabilityMargin(borderNodes, currentTile.getNodeMarginFileName(), currentTile.getEdgeMarginFileName());

        graph.doClose();

        // reset the references
        WeakReference<Graph> referenceGraph = new WeakReference<Graph>(graph);
        referenceGraph.clear();
        WeakReference<List<Node>> referenceNodesToIterate = new WeakReference<List<Node>>(nodesToIterate);
        referenceNodesToIterate.clear();
        WeakReference<Int2ObjectMap<Node>> referenceBorderNodes = new WeakReference<Int2ObjectMap<Node>>(borderNodes);
        referenceBorderNodes.clear();

//        graph = null;
//        nodesToIterate = null;
//        borderNodes = null;
//        System.gc();
    }

    private AbstractSegmenter mergeGraphsAndAchieveSegmentation(int numberOfRemainingIterations) throws IOException, InterruptedException {
        int tileCountX = this.tileSegmenterMetadata.getComputedTileCountX();
        int tileCountY = this.tileSegmenterMetadata.getComputedTileCountY();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge graphs: tile column count: " +tileCountX+", tile row count: " + tileCountY+", number of remaining iterations: "+numberOfRemainingIterations);
        }

        Graph graph = new Graph(0);
        for (int rowIndex = 0; rowIndex < tileCountY; rowIndex++) {
            for (int columnIndex = 0; columnIndex < tileCountX; columnIndex++) {
                ProcessingTile currentTile = this.tileSegmenterMetadata.getTileAt(rowIndex, columnIndex);
                if (currentTile == null) {
                    throw new NullPointerException("The tile to process is null: row index: " + rowIndex + ", column index: " + columnIndex);
                }

                Graph subgraph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());
                graph.addNodes(subgraph);

                // reset the reference
                WeakReference<Graph> referenceSubgraph = new WeakReference<Graph>(subgraph);
                referenceSubgraph.clear();

                if (logger.isLoggable(Level.FINE)) {
                    if (rowIndex ==0 && columnIndex == 0) {
                        logger.log(Level.FINE, ""); // add an empty line
                    }
                    logger.log(Level.FINE, "Merge graphs (read tile graph): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: "+graph.getNodeCount()+", added node count: "+subgraph.getNodeCount()+", tile region: "+tileRegionToString(currentTile.getRegion()));
                }
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge graphs (remove duplicated nodes): tile column count: " +tileCountX+", tile row count: " + tileCountY + ", graph node count: "+graph.getNodeCount() + ", number of remaining iterations: "+numberOfRemainingIterations);
        }

        // removing duplicated nodes and updating neighbors
        for (int rowIndex = 0; rowIndex < tileCountY; rowIndex++) {
            for (int columnIndex = 0; columnIndex < tileCountX; columnIndex++) {
                ProcessingTile currentTile = this.tileSegmenterMetadata.getTileAt(rowIndex, columnIndex);
                if (currentTile == null) {
                    throw new NullPointerException("The tile to process is null: row index: " + rowIndex + ", column index: " + columnIndex);
                }

                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, ""); // add an empty line
                    logger.log(Level.FINE, "Merge graphs (build border pixel map): row index: "+rowIndex+", column index: "+columnIndex+", graph node count: " +graph.getNodeCount()+", tile region: " +tileRegionToString(currentTile.getRegion()));
                }

                Int2ObjectMap<List<Node>> borderPixelMap = graph.buildBorderPixelMapInParallel(this.threadCount, this.threadPool, currentTile, rowIndex, columnIndex, tileCountX, tileCountY, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Merge graphs (after building border pixel map): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size());
                }

                int graphNodeCount = graph.getNodeCount();

                graph.removeDuplicatedNodes(borderPixelMap, this.imageWidth);

                if (logger.isLoggable(Level.FINEST)) {
                    int removedNodeCount = graphNodeCount - graph.getNodeCount();
                    logger.log(Level.FINEST, "Merge graphs (after removing duplicate nodes): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", removed node count: "+removedNodeCount+", map size: "+borderPixelMap.size());
                }

                updateNeighborsOfNoneDuplicatedNodes(borderPixelMap, this.imageWidth, this.imageHeight);

                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Merge graphs (after updating neighbors): row index: " + rowIndex + ", column index: " + columnIndex+", graph node count: " +graph.getNodeCount()+", map size: "+borderPixelMap.size());
                }

                // reset the reference
                WeakReference<Int2ObjectMap<List<Node>>> referenceBorderPixelMap = new WeakReference<Int2ObjectMap<List<Node>>>(borderPixelMap);
                referenceBorderPixelMap.clear();
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Merge graphs (before final segmentation): tile column count: "+tileCountX+", tile row count: "+tileCountY+", graph node count: "+graph.getNodeCount()+", number of remaining iterations: "+numberOfRemainingIterations);
        }

        // segmentation of the graph
        AbstractSegmenter segmenter = buildSegmenter(this.threshold);
        segmenter.setGraph(graph, this.imageWidth, this.imageHeight);
        segmenter.performAllIterationsWithLMBF(numberOfRemainingIterations);
        return segmenter;
    }

    private Graph readGraphMarginsFromTile(int rowIndex, int columnIndex) throws IOException {
        ProcessingTile tile = this.tileSegmenterMetadata.getTileAt(rowIndex, columnIndex);
        if (tile == null) {
            throw new NullPointerException("The tile to read the graph is null: row index: " + rowIndex + ", column index: " + columnIndex);
        }
        return readGraph(tile.getNodeMarginFileName(), tile.getEdgeMarginFileName());
    }

    private Graph readGraphSecondPartialSegmentation(ProcessingTile currentTile, int rowIndex, int columnIndex, int tileCountX, int tileCountY) throws IOException {
        Graph graph = readGraph(currentTile.getNodeFileName(), currentTile.getEdgeFileName());

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run second segmentation: (after read graph): graph node count: " +graph.getNodeCount()+", tile row index: " + rowIndex + ", tile column index: " + columnIndex);
        }

        // add the stability margin to retrieve at top
        if (rowIndex > 0) { // (startYWithoutMargin > 0) { //(row > 0) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex-1, columnIndex);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at right
        if (columnIndex < tileCountX - 1) { //(finishXWithoutMargin < this.imageWidth) { //(col < nbTilesX - 1) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex, columnIndex+1);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at bottom
        if (rowIndex < tileCountY - 1) { //(finishYWithoutMargin < this.imageHeight) { //(row < nbTilesY - 1) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex+1, columnIndex);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at left
        if (columnIndex > 0) { // (startXWithoutMargin > 0) { //(col > 0) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex, columnIndex-1);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at top right
        if (rowIndex > 0 && columnIndex < tileCountX - 1) { // (startYWithoutMargin > 0 && finishXWithoutMargin < this.imageWidth) { //(row > 0 && col < nbTilesX - 1) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex-1, columnIndex+1);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at bottom right
        if (rowIndex < tileCountY - 1 && columnIndex < tileCountX - 1) { // (finishYWithoutMargin < this.imageHeight && finishXWithoutMargin < this.imageWidth) { //(row < nbTilesY - 1 && col < nbTilesX - 1) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex+1, columnIndex+1);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at bottom left
        if (rowIndex < tileCountY - 1 && columnIndex > 0) { // (finishYWithoutMargin < this.imageHeight && startXWithoutMargin > 0) { //(row < nbTilesY - 1 && col > 0) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex+1, columnIndex-1);
            graph.addNodes(subgraph);
        }
        // add the stability margin to retrieve at top left
        if (rowIndex > 0 && columnIndex > 0) { // (startYWithoutMargin > 0 && startXWithoutMargin > 0) { //(row > 0 && col > 0) {
            Graph subgraph = readGraphMarginsFromTile(rowIndex-1, columnIndex-1);
            graph.addNodes(subgraph);
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Run second segmentation: (after add stability margin): graph node count: " +graph.getNodeCount()+", tile row index: " + rowIndex + ", tile column index: " + columnIndex);
        }

        return graph;
    }

    private void writeGraph(Graph graph, String nodesPath, String edgesPath) throws IOException {
        BufferedOutputStreamWrapper nodesFileStream = null;
        BufferedOutputStreamWrapper edgesFileStream = null;
        try {
            File nodesFile = this.temporaryFolder.resolve(nodesPath).toFile();
            nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = this.temporaryFolder.resolve(edgesPath).toFile();
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
            File nodesFile = this.temporaryFolder.resolve(nodesPath).toFile();
            nodesFileStream = new BufferedInputStreamWrapper(nodesFile);

            int nodeCount = nodesFileStream.readInt();
            Int2ObjectMap<Node> nodesMap = new Int2ObjectLinkedOpenHashMap<Node>(nodeCount);
            Graph graph = new Graph(nodeCount);
            for (int i=0; i<nodeCount; i++) {
                Node node = readNode(nodesFileStream);
                nodesMap.put(node.getId(), node);
                graph.addNode(node);
            }

            File edgesFile = this.temporaryFolder.resolve(edgesPath).toFile();
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
                    if (targetNode != null) {
                        node.addEdge(targetNode, boundary);
                    }
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
            File nodesFile = this.temporaryFolder.resolve(nodesPath).toFile();
            nodesFileStream = new BufferedOutputStreamWrapper(nodesFile);

            File edgesFile = this.temporaryFolder.resolve(edgesPath).toFile();
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

    private String getTemporaryFolderPath() {
        return this.temporaryFolder.toFile().getAbsolutePath();
    }

    public static int computeTileMargin(int tileWidth, int tileHeight) {
        int iterationsForEachFirstSegmentation = computeNumberOfFirstIterations(tileWidth, tileHeight);
        return (int) (Math.pow(2, iterationsForEachFirstSegmentation + 1) - 2);
    }

    public static ProcessingTile buildTile(int tileLeftX, int tileTopY, int tileWidth, int tileHeight, int tileMargin, int imageWidth, int imageHeight) {
        // compute current tile start and size
        ProcessingTile tile = new ProcessingTile();

        int finishX = tileLeftX + tileWidth;
        if (finishX > imageWidth) {
            finishX = imageWidth;
        }

        int finishY = tileTopY + tileHeight;
        if (finishY > imageHeight) {
            finishY = imageHeight;
        }

        // margin at the top
        if (tileTopY > 0) { //(row > 0) {
            tile.setTopMargin(tileMargin);
            tile.setImageTopY(tileTopY); // tile.rows[0] = startY;//row * tileHeight;
        } else {
            // the tile is on the top row --> no top margin
            tile.setTopMargin(0);
            tile.setImageTopY(0); // tile.rows[0] = 0;
        }

        // margin at the right
        if (finishX < imageWidth) { //(col < nbTilesX - 1) {
            tile.setRightMargin(tileMargin);
            tile.setImageRightX(tileLeftX + tileWidth - 1); // tile.columns[1] = startX + sizeX - 1; //sizeX
        } else {
            // the tile is on the right column --> no right margin
            tile.setRightMargin(0);
            tile.setImageRightX(imageWidth - 1); // tile.columns[1] = imageWidth - 1;
        }

        // margin at the bottom
        if (finishY < imageHeight) { // (row < nbTilesY - 1) {
            tile.setBottomMargin(tileMargin);
            tile.setImageBottomY(tileTopY + tileHeight - 1); // tile.rows[1] = startY + sizeY - 1; // sizeY
        } else {
            // the tile is on the bottom --> no bottom margin
            tile.setBottomMargin(0);
            tile.setImageBottomY(imageHeight - 1); // tile.rows[1] = imageHeight - 1;
        }

        // margin at the left
        if (tileLeftX > 0) { //(col > 0) {
            tile.setLeftMargin(tileMargin);
            tile.setImageLeftX(tileLeftX); // tile.columns[0] = startX;//col * tileWidth;
        } else {
            // the tile is on the left --> no left margin
            tile.setLeftMargin(0);
            tile.setImageLeftX(0); // tile.columns[0] = 0;
        }

        // store the tile region
        int regionLeftX = tileLeftX - tile.getLeftMargin();
        int regionTopY = tileTopY - tile.getTopMargin();
        int regionWidth = tileWidth + tile.getLeftMargin() + tile.getRightMargin();
        int regionHeight = tileHeight + tile.getTopMargin() + tile.getBottomMargin();
        int regionRightX = regionLeftX + regionWidth;
        if (regionRightX > imageWidth) {
            regionWidth = imageWidth - regionLeftX;
        }
        int regionBottomY = regionTopY + regionHeight;
        if (regionBottomY > imageHeight) {
            regionHeight = imageHeight - regionTopY;
        }

        tile.setRegion(new BoundingBox(regionLeftX, regionTopY, regionWidth, regionHeight));

        String temporaryFilesPrefix = "";
        String suffix = Integer.toString(tileLeftX) + "_" + Integer.toString(tileTopY) + ".bin";
        tile.setNodeFileName(temporaryFilesPrefix + "_node_" + suffix);
        tile.setEdgeFileName(temporaryFilesPrefix + "_edge_" + suffix);
        tile.setNodeMarginFileName(temporaryFilesPrefix + "_nodeMargin_" + suffix);
        tile.setEdgeMarginFileName(temporaryFilesPrefix + "_edgeMargin_" + suffix);

        return tile;
    }

    private static int computeBoundary(int gridId, Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth, int imageHeight, int[] cellNeighborhood, Node firstNeighborNode) {
        int boundary = 0;
        List<Node> resultNodes = borderPixelMap.get(gridId);
        if (resultNodes != null) {
            AbstractSegmenter.generateFourNeighborhood(cellNeighborhood, gridId, imageWidth, imageHeight);
            for (int k = 0; k < cellNeighborhood.length; k++) {
                if (cellNeighborhood[k] > -1) {
                    List<Node> cellNeighborNodes = borderPixelMap.get(cellNeighborhood[k]);
                    if (cellNeighborNodes != null && cellNeighborNodes.get(0) == firstNeighborNode) {
                        boundary++;
                    }
                }
            }
        }
        return boundary;
    }

    private static void updateNeighborsOfNoneDuplicatedNodes(Int2ObjectMap<List<Node>> borderPixelMap, int imageWidth, int imageHeight) {
        int[] neighborhood = new int[4];
        int[] cellNeighborhood = new int[4];
        IntSet borderCells = new IntOpenHashSet();

        ObjectIterator<Int2ObjectMap.Entry<List<Node>>> it = borderPixelMap.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<List<Node>> entry = it.next();
            int nodeId = entry.getIntKey();
            List<Node> nodes = entry.getValue();
            AbstractSegmenter.generateFourNeighborhood(neighborhood, nodeId, imageWidth, imageHeight);
            for (int j = 0; j < neighborhood.length; j++) {
                if (neighborhood[j] > -1) {
                    List<Node> neighborNodes = borderPixelMap.get(neighborhood[j]);
                    if (neighborNodes != null) {
                        Node currentNode = nodes.get(0); // currNode
                        Node firstNeighborNode = neighborNodes.get(0); // neigh
                        if (currentNode != firstNeighborNode) {
                            Edge edge = currentNode.findEdge(firstNeighborNode);
                            if (edge == null) {
                                borderCells.clear(); // clear the set
                                int boundary = 0;
                                if (borderCells.add(currentNode.getId())) {
                                    boundary += computeBoundary(currentNode.getId(), borderPixelMap, imageWidth, imageHeight, cellNeighborhood, firstNeighborNode);
                                }
                                Contour contour = currentNode.getContour();
                                if (contour.hasBorderSize()) {
                                    // initialize the first move at prev
                                    int previousMoveId = contour.getMove(0);
                                    // declare the current pixel index
                                    int currentCellId = currentNode.getId();
                                    // explore the contour
                                    int contourSize = contour.computeContourBorderSize();
                                    for (int moveIndex = 1; moveIndex < contourSize; moveIndex++) {
                                        int currentMoveId = contour.getMove(moveIndex);
                                        int nextCellId = Contour.computeNextCellId(previousMoveId, currentMoveId, currentCellId, imageWidth);
                                        if (nextCellId != currentCellId) {
                                            currentCellId = nextCellId;
                                            if (borderCells.add(currentCellId)) {
                                                boundary += computeBoundary(currentCellId, borderPixelMap, imageWidth, imageHeight, cellNeighborhood, firstNeighborNode);
                                            }
                                        }
                                        previousMoveId = currentMoveId;
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
