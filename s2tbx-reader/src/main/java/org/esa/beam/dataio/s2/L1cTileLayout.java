package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
public class L1cTileLayout {
    /** Width of L1C tile  */
    int width;
    /** Height of L1C tile  */
    int height;
    /** Width of internal JP2 tiles  */
    int tileWidth;
    /** Height of internal JP2 tiles  */
    int tileHeight;
    /** Width of internal JP2 X-tiles  */
    int numXTiles;
    /** Number of internal JP2 Y-tiles  */
    int numYTiles;
    int numResolutions;

    public L1cTileLayout(int width, int height, int tileWidth, int tileHeight, int numXTiles, int numYTiles, int numResolutions) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numXTiles = numXTiles;
        this.numYTiles = numYTiles;
        this.numResolutions = numResolutions;
    }
}
