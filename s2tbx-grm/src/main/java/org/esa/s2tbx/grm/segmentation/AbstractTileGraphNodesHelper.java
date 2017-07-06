package org.esa.s2tbx.grm.segmentation;

import org.esa.s2tbx.grm.segmentation.tiles.ProcessingTile;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
abstract class AbstractTileGraphNodesHelper<ResultType> {
    protected final Graph graph;
    protected final ProcessingTile tile;

    private int threadCounter;
    private int graphNodesCounter;

    protected AbstractTileGraphNodesHelper(Graph graph, ProcessingTile tile) {
        this.graph = graph;
        this.tile = tile;

        this.threadCounter = 0;
        this.graphNodesCounter = 0;
    }

    protected abstract void processNode(Node node);

    protected abstract ResultType finishProcesssing();

    final ResultType processInParallel(int threadCount, Executor threadPool) throws InterruptedException {
        for (int i=0; i<threadCount; i++) {
            TileGraphNodesRunnable pixelsRunnable = new TileGraphNodesRunnable(this);
            threadPool.execute(pixelsRunnable);
        }
        computeBorderPixels();
        waitToFinish();
        return finishProcesssing();
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

    private synchronized void waitToFinish() throws InterruptedException {
        if (this.threadCounter > 0) {
            wait();
        }
    }

    private void computeBorderPixels() {
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
                processNode(node);
            }
        } while (index >= 0);
    }

    private static class TileGraphNodesRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(TileGraphNodesRunnable.class.getName());

        private final AbstractTileGraphNodesHelper tileBorderPixelsHelper;

        TileGraphNodesRunnable(AbstractTileGraphNodesHelper tileBorderPixelsHelper) {
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
