package org.esa.snap.utils;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractParallelComputing {

    private int threadCounter;
    protected Exception threadException;

    protected AbstractParallelComputing() {
        this.threadCounter = 0;
    }

    protected abstract void execute() throws Exception;

    public final void executeInParallel(int threadCount, Executor threadPool) throws Exception {
        for (int i=0; i<threadCount; i++) {
            ItemRunnable runnable = new ItemRunnable(this);
            threadPool.execute(runnable);
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

    private static class ItemRunnable implements Runnable {
        //private static final Logger logger = Logger.getLogger(ItemRunnable.class.getName());

        private final AbstractParallelComputing itemHelper;

        public ItemRunnable(AbstractParallelComputing imageTilesHelper) {
            this.itemHelper = imageTilesHelper;
            this.itemHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            Exception threadException = null;
            try {
                this.itemHelper.execute();
            } catch (Exception exception) {
                threadException = exception;
                //logger.log(Level.SEVERE, "Failed to execute the image tiles.", threadException);
            } catch (Throwable throwable) {
                threadException = new Exception(throwable);
                //logger.log(Level.SEVERE, "Failed to execute the image tiles.", threadException);
            } finally {
                this.itemHelper.decrementThreadCounter(threadException);
            }
        }
    }
}
