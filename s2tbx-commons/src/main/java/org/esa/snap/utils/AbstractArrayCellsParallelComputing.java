package org.esa.snap.utils;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractArrayCellsParallelComputing {
    private final int cellCount;

    private int threadCounter;
    private int currentCellIndex;
    private Exception threadException;

    protected AbstractArrayCellsParallelComputing(int cellCount) {
        this.cellCount = cellCount;
        this.threadCounter = 0;
        this.currentCellIndex = 0;
    }

    protected abstract void computeCell(int localIndex);

    public final void executeInParallel(int threadCount, Executor threadPool) throws Exception {
        for (int i=0; i<threadCount; i++) {
            ArrayCellsParallelComputingRunnable runnable = new ArrayCellsParallelComputingRunnable(this);
            threadPool.execute(runnable);
        }
        execute();
        waitToFinish();
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

    private void execute() {
        int index = -1;
        do {
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentCellIndex < this.cellCount) {
                    index = this.currentCellIndex;
                    this.currentCellIndex++;
                } else {
                    index = -1;
                }
            }
            if (index >= 0) {
                computeCell(index);
            }
        } while (index >= 0);
    }

    private static class ArrayCellsParallelComputingRunnable implements Runnable {
        private static final Logger logger = Logger.getLogger(ArrayCellsParallelComputingRunnable.class.getName());

        private final AbstractArrayCellsParallelComputing arrayCellsParallelComputing;

        ArrayCellsParallelComputingRunnable(AbstractArrayCellsParallelComputing arrayCellsParallelComputing) {
            this.arrayCellsParallelComputing = arrayCellsParallelComputing;
            this.arrayCellsParallelComputing.incrementThreadCounter();
        }

        @Override
        public void run() {
            Exception threadException = null;
            try {
                this.arrayCellsParallelComputing.execute();
            } catch (Exception exception) {
                threadException = exception;
                logger.log(Level.SEVERE, "Failed to compute the array cell.", exception);
            } finally {
                this.arrayCellsParallelComputing.decrementThreadCounter(threadException);
            }
        }
    }
}
