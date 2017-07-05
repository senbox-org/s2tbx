package org.esa.s2tbx.grm.segmentation;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
class TileBorderPixelsHelper implements NodeBorderCellsCallback {
    private final Int2ObjectMap<List<Node>> borderPixelMap;
    private final Graph graph;
    private final ProcessingTile tile;
    private final int rowTileIndex;
    private final int columnTileIndex;
    private final int tileCountX;
    private final int tileCountY;
    private final int imageWidth;

    private int threadCounter;
    private int graphNodesCounter;

    private final int rowMin;
    private final int rowMax;
    private final int columnMin;
    private final int columnMax;

    TileBorderPixelsHelper(Graph graph, ProcessingTile tile, int rowTileIndex, int columnTileIndex, int tileCountX, int tileCountY, int imageWidth) {
        this.graph = graph;
        this.tile = tile;
        this.rowTileIndex = rowTileIndex;
        this.columnTileIndex = columnTileIndex;
        this.tileCountX = tileCountX;
        this.tileCountY = tileCountY;
        this.imageWidth = imageWidth;

        this.threadCounter = 0;
        this.graphNodesCounter = 0;
        this.borderPixelMap = new Int2ObjectLinkedOpenHashMap<List<Node>>(); // key = node id

        this.rowMin = (this.tile.getImageTopY() > 0) ? this.tile.getImageTopY() - 1 : this.tile.getImageTopY();
        this.rowMax = this.tile.getImageBottomY() + 1;
        this.columnMin = (this.tile.getImageLeftX() > 0) ? this.tile.getImageLeftX() - 1 : this.tile.getImageLeftX();
        this.columnMax = this.tile.getImageRightX() + 1;
    }

    Int2ObjectMap<List<Node>> computeBorderPixelsUsingThreads(int threadCount, Executor threadPool) throws InterruptedException {
        for (int i=0; i<threadCount; i++) {
            TileBorderPixelsRunnable pixelsRunnable = new TileBorderPixelsRunnable(this);
            threadPool.execute(pixelsRunnable);
        }
        computeBorderPixels();
        return waitToFinish();
    }

    private synchronized void incrementThreadCounter() {
        this.threadCounter++;
    }

    private synchronized void decrementThreadCounter() {
        this.threadCounter--;
        if (this.threadCounter <= 0) {
            notifyAll();
        }
    }

    private synchronized Int2ObjectMap<List<Node>> waitToFinish() throws InterruptedException {
        if (this.threadCounter > 0) {
            wait();
        }
        return this.borderPixelMap;
    }

    private void computeBorderPixels() {
//        int rowMin = (this.tile.getImageTopY() > 0) ? this.tile.getImageTopY() - 1 : this.tile.getImageTopY();
//        int rowMax = this.tile.getImageBottomY() + 1;
//        int columnMin = (this.tile.getImageLeftX() > 0) ? this.tile.getImageLeftX() - 1 : this.tile.getImageLeftX();
//        int columnMax = this.tile.getImageRightX() + 1;

        int nodeCount = this.graph.getNodeCount();
        int index = -1;
        do {
            synchronized (this) {
                if (this.graphNodesCounter < nodeCount) {
                    index = this.graphNodesCounter;
                    this.graphNodesCounter++;
                } else {
                    index = -1;
                }
            }
            if (index >= 0) {
                Node node = this.graph.getNodeAt(index);
                BoundingBox box = node.getBox();
                if (this.tile.isRegionInside(box)) {
                //if (box.getLeftX() > this.tile.getImageLeftX() && box.getTopY() > this.tile.getImageTopY() && box.getRightX() - 1 < this.tile.getImageRightX() && box.getBottomY() - 1 < this.tile.getImageBottomY()) {
                    continue;
                } else {
                    AbstractSegmenter.generateBorderCells(node, this.imageWidth, this);

//                    IntSet borderCells = AbstractSegmenter.generateBorderCells(node.getContour(), node.getId(), this.imageWidth);
//                    IntIterator itCells = borderCells.iterator();
//                    while (itCells.hasNext()) {
//                        int gridId = itCells.nextInt();
//                        int rowPixel = gridId / this.imageWidth;
//                        int columnPixel = gridId % this.imageWidth;
//                        boolean addNode = false;
//                        if (this.rowTileIndex > 0 && (rowPixel == this.tile.getImageTopY() || rowPixel == rowMin)) {
//                            addNode = true;
//                        } else if (this.columnTileIndex < this.tileCountX - 1 && (columnPixel == this.tile.getImageRightX() || columnPixel == columnMax)) {
//                            addNode = true;
//                        } else if (this.rowTileIndex < this.tileCountY - 1 && (rowPixel == this.tile.getImageBottomY() || rowPixel == rowMax)) {
//                            addNode = true;
//                        } else if (this.columnTileIndex > 0 && (columnPixel == this.tile.getImageLeftX() || columnPixel == columnMin)) {
//                            addNode = true;
//                        }
//                        if (addNode) {
//                            synchronized (this.borderPixelMap) {
//                                List<Node> nodes = this.borderPixelMap.get(gridId);
//                                if (nodes == null) {
//                                    nodes = new ArrayList<Node>();
//                                    this.borderPixelMap.put(gridId, nodes);
//                                }
//                                nodes.add(node);
//                            }
//                        }
//                    }
                }
            }
        } while (index >= 0);
    }

    @Override
    public void addBorderCellId(Node analyzedNode, int borderCellId) {
        int rowPixel = borderCellId / this.imageWidth;
        int columnPixel = borderCellId % this.imageWidth;
        if (this.rowTileIndex > 0 && (rowPixel == this.tile.getImageTopY() || rowPixel == rowMin)) {
            addNode(analyzedNode, borderCellId);
        } else if (this.columnTileIndex < this.tileCountX - 1 && (columnPixel == this.tile.getImageRightX() || columnPixel == columnMax)) {
            addNode(analyzedNode, borderCellId);
        } else if (this.rowTileIndex < this.tileCountY - 1 && (rowPixel == this.tile.getImageBottomY() || rowPixel == rowMax)) {
            addNode(analyzedNode, borderCellId);
        } else if (this.columnTileIndex > 0 && (columnPixel == this.tile.getImageLeftX() || columnPixel == columnMin)) {
            addNode(analyzedNode, borderCellId);
        }
    }

    private void addNode(Node analyzedNode, int borderCellId) {
        synchronized (this.borderPixelMap) {
            List<Node> nodes = this.borderPixelMap.get(borderCellId);
            if (nodes == null) {
                nodes = new ArrayList<Node>();
                this.borderPixelMap.put(borderCellId, nodes);
                nodes.add(analyzedNode);
            } else {
                boolean found = false;
                int nodeCount = nodes.size();
                for (int i=0; i<nodeCount && !found; i++) {
                    if (nodes.get(i) == analyzedNode) {
//                        System.out.println("********************** found node borderCellId="+borderCellId+"  node.id="+analyzedNode.getId());
                        found = true;
                    }
                }
                if (!found) {
                    nodes.add(analyzedNode);
                }
            }
        }
    }

    private static class TileBorderPixelsRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(TileBorderPixelsRunnable.class.getName());

        private final TileBorderPixelsHelper tileBorderPixelsHelper;

        TileBorderPixelsRunnable(TileBorderPixelsHelper tileBorderPixelsHelper) {
            this.tileBorderPixelsHelper = tileBorderPixelsHelper;
            this.tileBorderPixelsHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            try {
                this.tileBorderPixelsHelper.computeBorderPixels();
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to compute the border pixels.", exception);
            } finally {
                this.tileBorderPixelsHelper.decrementThreadCounter();
            }
        }
    }
}
