package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.BoundingBox;
import org.esa.s2tbx.grm.Contour;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcoravu on 9/3/2017.
 */
public class ImageSplitter {
    private static final byte NEIGHBORHOOD_TOP_INDEX = 0;
    private static final byte NEIGHBORHOOD_TOP_RIGHT_INDEX = 1;
    private static final byte NEIGHBORHOOD_RIGHT_INDEX = 2;
    private static final byte NEIGHBORHOOD_BOTTOM_RIGHT_INDEX = 3;
    private static final byte NEIGHBORHOOD_BOTTOM_INDEX = 4;
    private static final byte NEIGHBORHOOD_BOTTOM_LEFT_INDEX = 5;
    private static final byte NEIGHBORHOOD_LEFT_INDEX = 6;
    private static final byte NEIGHBORHOOD_TOP_LEFT_INDEX = 7;

//    enum NeighborhoodRelativePosition{
//        NBH_TOP,
//        NBH_TOP_RIGHT,
//        NBH_RIGHT,
//        NBH_BOTTOM_RIGHT,
//        NBH_BOTTOM,
//        NBH_BOTTOM_LEFT,
//        NBH_LEFT,
//        NBH_TOP_LEFT
//    };
    public ImageSplitter() {
    }

    public static List<ProcessingTile> splitImage(int imageWidth, int imageHeight, int tileWidth, int tileHeight, int margin) {
        int xTiles = imageWidth / tileWidth;
        if (imageWidth % tileWidth != 0) {
            xTiles++;
        }
        int yTiles = imageHeight / tileHeight;
        if (imageHeight % tileHeight != 0) {
            yTiles++;
        }

        List<ProcessingTile> tiles = new ArrayList<ProcessingTile>(xTiles * yTiles);
        for (int y=0; y<yTiles; y++) {
            for (int x=0; x<xTiles; x++) {
                // compute current tile start and size
                int startX = x * tileWidth;
                int startY = y * tileHeight;
                int sizeX = tileWidth;
                int sizeY = tileHeight;

                // current tile size might be different for right and bottom borders
                if (x == xTiles - 1) {
                    sizeX += imageWidth % tileWidth;
                }
                if (y == yTiles - 1) {
                    sizeY += imageHeight % tileHeight;
                }
                ProcessingTile tile = null;
                int tileIndex = tiles.size();

                // margin at the top ?
                if (y > 0) {
                    tile.margin[Contour.TOP_MOVE_INDEX] = margin;
                    tile.rows[0] = y * tileHeight;
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_INDEX] = tileIndex - xTiles;
                } else {
                    // the tile is on the top row --> no top margin
                    tile.margin[Contour.TOP_MOVE_INDEX] = 0;
                    tile.rows[0] = 0;
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_INDEX] = -1;
                }

			    // margin at the right
                if (x < xTiles - 1) {
                    tile.margin[Contour.RIGHT_MOVE_INDEX] = margin;
                    tile.columns[1] = x * tileWidth + sizeX - 1; //sizeX
                    tile.tileNeighbors[NEIGHBORHOOD_RIGHT_INDEX] = tileIndex + 1;
                } else {
                    // the tile is on the right column --> no right margin
                    tile.margin[Contour.RIGHT_MOVE_INDEX] = 0;
                    tile.columns[1] = imageWidth - 1;
                    tile.tileNeighbors[NEIGHBORHOOD_RIGHT_INDEX] = -1;
                }

                // margin at the bottom
                if (y < yTiles - 1) {
                    tile.margin[Contour.BOTTOM_MOVE_INDEX] = margin;
                    tile.rows[1] = y * tileHeight + sizeY - 1; // sizeY
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_INDEX] = tileIndex + xTiles;
                } else {
                    // the tile is on the bottom --> no bottom margin
                    tile.margin[Contour.BOTTOM_MOVE_INDEX] = 0;
                    tile.rows[1] = imageHeight - 1;
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_INDEX] = -1;
                }

                // margin at the left
                if (x > 0) {
                    tile.margin[Contour.LEFT_MOVE_INDEX] = margin;
                    tile.columns[0] = x * tileWidth;
                    tile.tileNeighbors[NEIGHBORHOOD_LEFT_INDEX] = tileIndex-1;
                } else {
                    // the tile is on the left --> no left margin
                    tile.margin[Contour.LEFT_MOVE_INDEX] = 0;
                    tile.columns[0] = 0;
                    tile.tileNeighbors[NEIGHBORHOOD_LEFT_INDEX] = -1;
                }

                // store the tile region
                int upperLeftX = startX - tile.margin[Contour.LEFT_MOVE_INDEX];
                int upperLeftY = startY - tile.margin[Contour.TOP_MOVE_INDEX];
                int width = sizeX + tile.margin[Contour.LEFT_MOVE_INDEX] + tile.margin[Contour.RIGHT_MOVE_INDEX];
                int height = sizeY + tile.margin[Contour.TOP_MOVE_INDEX] + tile.margin[Contour.BOTTOM_MOVE_INDEX];
                BoundingBox region = new BoundingBox(upperLeftX, upperLeftY, width, height);
                tile.region = region;

			    // is there a neighbor at the rop right
                if (y > 0 && x < xTiles - 1) {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_RIGHT_INDEX] = tileIndex - xTiles + 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_RIGHT_INDEX] = -1;
                }

			    // is there a neighbor at the bottom right
                if (x < xTiles - 1 && y < yTiles - 1) {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_RIGHT_INDEX] = tileIndex + xTiles + 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_RIGHT_INDEX] = -1;
                }

			    // is there a neighbor at the bottom left
                if (y < yTiles - 1 && x > 0) {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_LEFT_INDEX] = tileIndex + xTiles - 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_BOTTOM_LEFT_INDEX] = -1;
                }

			    // is there a neighbor at the top left
                if (x > 0 && y > 0) {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_LEFT_INDEX] = tileIndex - xTiles - 1;
                } else {
                    tile.tileNeighbors[NEIGHBORHOOD_TOP_LEFT_INDEX] = -1;
                }

                tiles.add(tile);
            }
        }
        return tiles;
    }
}
