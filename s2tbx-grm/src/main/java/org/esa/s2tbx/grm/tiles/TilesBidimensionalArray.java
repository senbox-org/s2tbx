package org.esa.s2tbx.grm.tiles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TilesBidimensionalArray {
    private final Map<String, ProcessingTile> tilesMap;
    private int tileCountX;
    private int tileCountY;

    public TilesBidimensionalArray() {
        this.tilesMap = new HashMap<String, ProcessingTile>();
        this.tileCountX = 0;
        this.tileCountY = 0;
    }

    public ProcessingTile getTileAt(int rowIndex, int columnIndex) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        return this.tilesMap.get(key);
    }

    public void addTile(int rowIndex, int columnIndex, ProcessingTile tileToAdd) {
        String key = Integer.toString(rowIndex) + "|" + Integer.toString(columnIndex);
        this.tilesMap.put(key, tileToAdd);

        this.tileCountX = Math.max(this.tileCountX, columnIndex+1);
        this.tileCountY = Math.max(this.tileCountY, rowIndex+1);
    }

    public int getTileCountX() {
        return tileCountX;
    }

    public int getTileCountY() {
        return tileCountY;
    }
}
