package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.BoundingBox;

/**
 * Created by jcoravu on 9/3/2017.
 */
public class ProcessingTile {
    public int[] rows; // lower and upper rows (-1 means that the row has not be considered)
    public int[] columns; // lower and upper columns (-1 means that the row has not be considered)
    public int[] tileNeighbors; // tile Neighbors at (top, top right, right, bottom right, bottom, bottom left, left, top left)
    public int[] margin; // Is there a margin at top, left, bottom or right
    public BoundingBox region; // The image region
    public String nodeFileName;
    public String edgeFileName;

    public String nodeMarginFileName;
    public String edgeMarginFileName;

    public ProcessingTile() {
        rows = new int[2];
        columns = new int[2];
        tileNeighbors = new int[8];
        margin = new int[4];
    }
}
