package org.esa.s2tbx.grm.segmentation.tiles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TileSegmenterMetadata {
    private final Map<String, ProcessingTile> tilesMap;

    private long accumulatedMemory;
    private boolean isFusion;
    private int computedTileCountX;
    private int computedTileCountY;

    public TileSegmenterMetadata() {
        this.tilesMap = new HashMap<String, ProcessingTile>();
        this.computedTileCountX = 0;
        this.computedTileCountY = 0;

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

    public ProcessingTile removeTile(int rowIndex, int columnIndex) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        return this.tilesMap.remove(key);
    }

    public ProcessingTile getTileAt(int rowIndex, int columnIndex) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        return this.tilesMap.get(key);
    }

    public int getComputedTileCountX() {
        return this.computedTileCountX;
    }

    public int getComputedTileCountY() {
        return this.computedTileCountY;
    }

    public void addAccumulatedMemory(long accumulatedMemoryToAdd, boolean isFusion) {
        this.accumulatedMemory += accumulatedMemoryToAdd;
        if (isFusion) {
            this.isFusion = isFusion;
        }
    }

    public long getAccumulatedMemory() {
        return this.accumulatedMemory;
    }

    public boolean isFusion() {
        return this.isFusion;
    }

    public boolean canRunSecondPartialSegmentation() {
        return this.isFusion;// && (this.accumulatedMemory > (0.5f * Runtime.getRuntime().freeMemory()));
    }
}
