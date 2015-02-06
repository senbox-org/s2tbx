package jp2;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
* @author Norman Fomferra
*/
public class TileLayout {
    /** Width of L1C tile  */
    public final int width;
    /** Height of L1C tile  */
    public final int height;
    /** Width of internal JP2 tiles  */
    public final int tileWidth;
    /** Height of internal JP2 tiles  */
    public final int tileHeight;
    /** Width of internal JP2 X-tiles  */
    public final int numXTiles;
    /** Number of internal JP2 Y-tiles  */
    public final int numYTiles;
    public final int numResolutions;

    public TileLayout(int width, int height, int tileWidth, int tileHeight, int numXTiles, int numYTiles, int numResolutions) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numXTiles = numXTiles;
        this.numYTiles = numYTiles;
        this.numResolutions = numResolutions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TileLayout)) return false;

        TileLayout that = (TileLayout) o;

        if (height != that.height) return false;
        if (numResolutions != that.numResolutions) return false;
        if (numXTiles != that.numXTiles) return false;
        if (numYTiles != that.numYTiles) return false;
        if (tileHeight != that.tileHeight) return false;
        if (tileWidth != that.tileWidth) return false;
        if (width != that.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + tileWidth;
        result = 31 * result + tileHeight;
        result = 31 * result + numXTiles;
        result = 31 * result + numYTiles;
        result = 31 * result + numResolutions;
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
