package org.esa.snap.utils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractMatrixCellsHelper {
    private final int columnCount;
    private final int rowCount;

    private int rowIndex;
    private int columnIndex;
    private int threadCounter;
    private Exception threadException;

    protected AbstractMatrixCellsHelper(int columnCount, int rowCount) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;

        this.rowIndex = 0;
        this.columnIndex = 0;
        this.threadCounter = 0;
    }

    protected abstract void runTile(int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException;

    public final void executeInParallel(int threadCount, Executor threadPool) throws Exception {
        for (int i=0; i<threadCount; i++) {
            MatrixCellRunnable segmentationRunnable = new MatrixCellRunnable(this);
            threadPool.execute(segmentationRunnable);
        }
        executeSegmentation();
        waitToFinish();
    }

    private synchronized void incrementThreadCounter() {
        this.threadCounter++;
    }

    private synchronized void decrementThreadCounter(Exception threadException) {
        if (this.threadException == null) {
            this.threadException = threadException;
        }
        this.threadCounter--;
        if (this.threadCounter <= 0) {
            notifyAll();
        }
    }

    private synchronized void waitToFinish() throws Exception {
        if (this.threadException != null) {
            throw this.threadException;
        }
        if (this.threadCounter > 0) {
            wait();
        }
    }

    private void executeSegmentation() throws Exception {
        int localRowIndex = -1;
        int localColumnIndex = -1;
        do {
            localRowIndex = -1;
            localColumnIndex = -1;
            synchronized (this) {
                if (this.rowIndex < this.rowCount) {
                    if (this.columnIndex < this.columnCount) {
                        localColumnIndex = this.columnIndex;
                        localRowIndex = this.rowIndex;
                    } else {
                        this.columnIndex = 0; // reset the column index
                        localColumnIndex = this.columnIndex;

                        this.rowIndex++; // increment the row index
                        if (this.rowIndex < this.rowCount) {
                            localRowIndex = this.rowIndex;
                        }
                    }
                    this.columnIndex++;
                }
            }
            if (localRowIndex >= 0 && localColumnIndex >= 0) {
                runTile(localRowIndex, localColumnIndex);
            }
        } while (localRowIndex >= 0 && localColumnIndex >= 0);
    }

    private static class MatrixCellRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(MatrixCellRunnable.class.getName());

        private final AbstractMatrixCellsHelper imageTilesHelper;

        public MatrixCellRunnable(AbstractMatrixCellsHelper imageTilesHelper) {
            this.imageTilesHelper = imageTilesHelper;
            this.imageTilesHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            Exception threadException = null;
            try {
                this.imageTilesHelper.executeSegmentation();
            } catch (Exception exception) {
                threadException = exception;
                logger.log(Level.SEVERE, "Failed to execute the image tiles.", exception);
            } finally {
                this.imageTilesHelper.decrementThreadCounter(threadException);
            }
        }
    }
}
