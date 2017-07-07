package org.esa.s2tbx.grm.segmentation.tiles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TileSegmenterMetadata {
    private final long totalMemory;
    private final Map<String, ProcessingTile> tilesMap;

    private long accumulatedMemory;
    private boolean isFusion;
    private int computedTileCountX;
    private int computedTileCountY;

    public TileSegmenterMetadata() {
        this.tilesMap = new HashMap<String, ProcessingTile>();
        this.computedTileCountX = 0;
        this.computedTileCountY = 0;

        this.totalMemory = Runtime.getRuntime().totalMemory();

        resetValues();
    }

    public void resetValues() {
        this.accumulatedMemory = 0;
        this.isFusion = false;
    }

    public ProcessingTile addTile(int rowIndex, int columnIndex, ProcessingTile tileToAdd) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        ProcessingTile oldValue = this.tilesMap.put(key, tileToAdd);

        this.computedTileCountX = Math.max(this.computedTileCountX, columnIndex+1);
        this.computedTileCountY = Math.max(this.computedTileCountY, rowIndex+1);

        return oldValue;
    }

    public ProcessingTile getTileAt(int rowIndex, int columnIndex) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        return this.tilesMap.get(key);
    }

    public int getComputedTileCountX() {
        return computedTileCountX;
    }

    public int getComputedTileCountY() {
        return computedTileCountY;
    }

    public void addAccumulatedMemory(long accumulatedMemoryToAdd) {
        this.accumulatedMemory += accumulatedMemoryToAdd;
    }

    public long getAccumulatedMemory() {
        return accumulatedMemory;
    }

    public void setFusion(boolean fusion) {
        isFusion = fusion;
    }

    public boolean isFusion() {
        return isFusion;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public boolean canRunSecondPartialSegmentation() {
        return (this.accumulatedMemory > this.totalMemory) && this.isFusion;
    }
}
