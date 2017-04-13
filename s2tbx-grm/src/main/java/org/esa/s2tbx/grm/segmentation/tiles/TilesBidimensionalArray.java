package org.esa.s2tbx.grm.segmentation.tiles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TilesBidimensionalArray {
    private final Map<String, ProcessingTile> tilesMap;
    private int computedTileCountX;
    private int computedTileCountY;

    public TilesBidimensionalArray() {
        this.tilesMap = new HashMap<String, ProcessingTile>();
        this.computedTileCountX = 0;
        this.computedTileCountY = 0;
    }

    public ProcessingTile getTileAt(int rowIndex, int columnIndex) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        return this.tilesMap.get(key);
    }

    public ProcessingTile addTile(int rowIndex, int columnIndex, ProcessingTile tileToAdd) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        ProcessingTile oldTile = this.tilesMap.put(key, tileToAdd);

        this.computedTileCountX = Math.max(this.computedTileCountX, columnIndex+1);
        this.computedTileCountY = Math.max(this.computedTileCountY, rowIndex+1);

        return oldTile;
    }

    public int getComputedTileCountX() {
        return computedTileCountX;
    }

    public int getComputedTileCountY() {
        return computedTileCountY;
    }
}
