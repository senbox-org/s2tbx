package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.snap.utils.BufferedInputStreamWrapper;
import org.esa.snap.utils.BufferedOutputStreamWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jean Coravu
 */
public class TileSegmenterMetadata {
    public long availableMemory;
    public long accumulatedMemory;
    public boolean isFusion;
    private int computedTileCountX;
    private int computedTileCountY;
    private final Map<String, BoundingBox> tilesMap;

    public TileSegmenterMetadata() {
        this.tilesMap = new HashMap<String, BoundingBox>();
        this.computedTileCountX = 0;
        this.computedTileCountY = 0;

        this.availableMemory = Runtime.getRuntime().totalMemory();

        resetValues();
    }

    private TileSegmenterMetadata(long availableMemory, long accumulatedMemory, boolean isFusion) {
        this.availableMemory = availableMemory;
        this.accumulatedMemory = accumulatedMemory;
        this.isFusion = isFusion;

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

    public void writeMetadata(File parentFolder) throws IOException {
        BufferedOutputStreamWrapper metadataFileStream = null;
        try {
            File metadataFile = new File(parentFolder, "metadata.bin");
            metadataFileStream = new BufferedOutputStreamWrapper(metadataFile);
            metadataFileStream.writeLong(getAvailableMemory());
            metadataFileStream.writeLong(getAccumulatedMemory());
            metadataFileStream.writeBoolean(isFusion());
            metadataFileStream.writeInt(getComputedTileCountX());
            metadataFileStream.writeInt(getComputedTileCountY());

            metadataFileStream.writeInt(this.tilesMap.size());

            Iterator<Map.Entry<String, BoundingBox>> it = this.tilesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, BoundingBox> entry = it.next();
                String key = entry.getKey();
                BoundingBox tileRectangle = entry.getValue();

                int index = key.indexOf("|");
                String rowIndexAsString = key.substring(0, index);
                String columnIndexAsString = key.substring(index + 1);
                int rowIndex = Integer.parseInt(rowIndexAsString);
                int columnIndex = Integer.parseInt(columnIndexAsString);

                metadataFileStream.writeInt(rowIndex);
                metadataFileStream.writeInt(columnIndex);

                metadataFileStream.writeInt(tileRectangle.getLeftX());
                metadataFileStream.writeInt(tileRectangle.getTopY());
                metadataFileStream.writeInt(tileRectangle.getWidth());
                metadataFileStream.writeInt(tileRectangle.getHeight());
            }
        } finally {
            if (metadataFileStream != null) {
                try {
                    metadataFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }

    public static TileSegmenterMetadata readMetadata(File parentFolder) throws IOException {
        BufferedInputStreamWrapper metadataFileStream = null;
        try {
            File metadataFile = new File(parentFolder, "metadata.bin");
            metadataFileStream = new BufferedInputStreamWrapper(metadataFile);

            long availableMemory = metadataFileStream.readLong();
            long accumulatedMemory = metadataFileStream.readLong();
            boolean isFusion = metadataFileStream.readBoolean();
            int tileCountX = metadataFileStream.readInt();
            int tileCountY = metadataFileStream.readInt();

            TileSegmenterMetadata result = new TileSegmenterMetadata(availableMemory, accumulatedMemory, isFusion);

            int tileCount = metadataFileStream.readInt();
            for (int i=0; i<tileCount; i++) {
                int tileRowIndex = metadataFileStream.readInt();
                int tileColumnIndex = metadataFileStream.readInt();

                int tileLeftX = metadataFileStream.readInt();
                int tileTopY = metadataFileStream.readInt();
                int tileWidth = metadataFileStream.readInt();
                int tileHeight = metadataFileStream.readInt();

                result.addTile(tileRowIndex, tileColumnIndex, tileLeftX, tileTopY, tileWidth, tileHeight);
            }
            return result;
        } finally {
            if (metadataFileStream != null) {
                try {
                    metadataFileStream.close();
                } catch (IOException exception) {
                    // ignore exception
                }
            }
        }
    }
}
