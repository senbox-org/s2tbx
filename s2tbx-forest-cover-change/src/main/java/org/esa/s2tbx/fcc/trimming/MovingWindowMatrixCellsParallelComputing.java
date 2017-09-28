package org.esa.s2tbx.fcc.trimming;

import org.esa.snap.utils.AbstractMatrixCellsParallelComputing;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 */

public abstract class MovingWindowMatrixCellsParallelComputing {
    private final int columnCount;
    private final int rowCount;

    private int currentRowIndex;
    private int currentColumnIndex;
    private int threadCounter;
    private Exception threadException;

    protected MovingWindowMatrixCellsParallelComputing(int columnCount, int rowCount) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;

        this.currentRowIndex = 0;
        this.currentColumnIndex = 0;
        this.threadCounter = 0;
    }

    protected abstract void runTile(int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException;

    public final void executeInParallel(int threadCount, Executor threadPool) throws Exception {
        for (int i=0; i<threadCount; i++) {
            MovingWindowMatrixCellsParallelComputing.MatrixCellRunnable segmentationRunnable = new MovingWindowMatrixCellsParallelComputing.MatrixCellRunnable(this);
            threadPool.execute(segmentationRunnable);
        }
        try {
            execute();
        } catch (Exception exception) {
            synchronized (this) {
                this.threadException = exception;
            }
        } catch (Throwable throwable) {
            synchronized (this) {
                this.threadException = new Exception(throwable);
            }
        } finally {
            waitToFinish();
        }
    }

    private synchronized void incrementThreadCounter() {
        this.threadCounter++;
    }

    private synchronized void decrementThreadCounter(Exception threadException) {
        this.threadCounter--;
        if (this.threadException == null) {
            this.threadException = threadException;
        }
        if (this.threadCounter <= 0) {
            notifyAll();
        }
    }

    private synchronized void waitToFinish() throws Exception {
        if (this.threadCounter > 0) {
            wait();
        }
        if (this.threadException != null) {
            throw this.threadException;
        }
    }

    private void execute() throws Exception {
        int localRowIndex = -1;
        int localColumnIndex = -1;
        do {
            localRowIndex = -1;
            localColumnIndex = -1;
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentRowIndex < this.rowCount) {
                    if (this.currentColumnIndex < this.columnCount) {
                        localColumnIndex = this.currentColumnIndex;
                        localRowIndex = this.currentRowIndex;
                    } else {
                        this.currentColumnIndex = 0; // reset the column index
                        localColumnIndex = this.currentColumnIndex;

                        this.currentRowIndex++; // increment the row index
                        if (this.currentRowIndex < this.rowCount) {
                            localRowIndex = this.currentRowIndex;
                        }
                    }
                    this.currentColumnIndex++;
                }
            }
            if (localRowIndex >= 0 && localColumnIndex >= 0) {
                runTile(localRowIndex, localColumnIndex);
            }
        } while (localRowIndex >= 0 && localColumnIndex >= 0);
    }

    private static class MatrixCellRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(MovingWindowMatrixCellsParallelComputing.MatrixCellRunnable.class.getName());

        private final MovingWindowMatrixCellsParallelComputing imageTilesHelper;

        public MatrixCellRunnable(MovingWindowMatrixCellsParallelComputing imageTilesHelper) {
            this.imageTilesHelper = imageTilesHelper;
            this.imageTilesHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            Exception threadException = null;
            try {
                this.imageTilesHelper.execute();
            } catch (Exception exception) {
                threadException = exception;
                logger.log(Level.SEVERE, "Failed to execute the image tiles.", threadException);
            } catch (Throwable throwable) {
                threadException = new Exception(throwable);
                logger.log(Level.SEVERE, "Failed to execute the image tiles.", threadException);
            } finally {
                this.imageTilesHelper.decrementThreadCounter(threadException);
            }
        }
    }
}
