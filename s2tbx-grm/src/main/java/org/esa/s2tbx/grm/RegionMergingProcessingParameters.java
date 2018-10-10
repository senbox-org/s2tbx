package org.esa.s2tbx.grm;

import java.util.concurrent.Executor;

/**
 * @author Jean Coravu
 */
public class RegionMergingProcessingParameters {
    private final int imageWidth;
    private final int imageHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final int threadCount;
    private final Executor threadPool;

    public RegionMergingProcessingParameters(int threadCount, Executor threadPool, int imageWidth, int imageHeight, int tileWidth, int tileHeight) {
        this.threadCount = threadCount;
        this.threadPool = threadPool;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public Executor getThreadPool() {
        return threadPool;
    }
}
