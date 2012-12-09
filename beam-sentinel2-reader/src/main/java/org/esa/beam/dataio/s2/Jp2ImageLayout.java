package org.esa.beam.dataio.s2;

/**
* @author Norman Fomferra
*/
class Jp2ImageLayout {
    int width;
    int height;
    int tileWidth;
    int tileHeight;
    int numXTiles;
    int numYTiles;
    int numResolutions;

    Jp2ImageLayout(int width, int height, int tileWidth, int tileHeight, int numXTiles, int numYTiles, int numResolutions) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numXTiles = numXTiles;
        this.numYTiles = numYTiles;
        this.numResolutions = numResolutions;
    }
}
