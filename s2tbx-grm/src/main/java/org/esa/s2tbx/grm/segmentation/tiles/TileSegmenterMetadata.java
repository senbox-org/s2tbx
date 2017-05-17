package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.BoundingBox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TileSegmenterMetadata {
    private final long availableMemory;
    private final Map<String, BoundingBox> tilesMap;

    private long accumulatedMemory;
    private boolean isFusion;
    private int computedTileCountX;
    private int computedTileCountY;

    public TileSegmenterMetadata() {
        this.tilesMap = new HashMap<String, BoundingBox>();
        this.computedTileCountX = 0;
        this.computedTileCountY = 0;

        this.availableMemory = Runtime.getRuntime().totalMemory();

        resetValues();
    }

    public void resetValues() {
        this.accumulatedMemory = 0;
        this.isFusion = false;
    }

    public void addTile(int rowIndex, int columnIndex, int leftX, int topY, int width, int height) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        this.tilesMap.put(key, new BoundingBox(leftX, topY, width, height));

        this.computedTileCountX = Math.max(this.computedTileCountX, columnIndex+1);
        this.computedTileCountY = Math.max(this.computedTileCountY, rowIndex+1);
    }

    public BoundingBox getTileAt(int rowIndex, int columnIndex) {
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

    public long getAvailableMemory() {
        return availableMemory;
    }

}
