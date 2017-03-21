package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.BoundingBox;
import org.esa.s2tbx.grm.Contour;

/**
 * @author Jean Coravu
 */
public class ProcessingTile {
    public int[] rows; // lower and upper rows (-1 means that the row has not be considered)
    public int[] columns; // lower and upper columns (-1 means that the row has not be considered)
    private int[] margin; // Is there a margin at top, left, bottom or right
    public BoundingBox region; // The image region
    public String nodeFileName;
    public String edgeFileName;

    public String nodeMarginFileName;
    public String edgeMarginFileName;

    public ProcessingTile() {
        rows = new int[2];
        columns = new int[2];
        margin = new int[4];
    }

    public int getTopMargin() {
        return this.margin[Contour.TOP_MOVE_INDEX];
    }

    public int getBottomMargin() {
        return this.margin[Contour.BOTTOM_MOVE_INDEX];
    }

    public int getLeftMargin() {
        return this.margin[Contour.LEFT_MOVE_INDEX];
    }

    public int getRightMargin() {
        return this.margin[Contour.RIGHT_MOVE_INDEX];
    }

    public void setTopMargin(int marginValue) {
        this.margin[Contour.TOP_MOVE_INDEX] = marginValue;
    }

    public void setBottomMargin(int marginValue) {
        this.margin[Contour.BOTTOM_MOVE_INDEX] = marginValue;
    }

    public void setLeftMargin(int marginValue) {
        this.margin[Contour.LEFT_MOVE_INDEX] = marginValue;
    }

    public void setRightMargin(int marginValue) {
        this.margin[Contour.RIGHT_MOVE_INDEX] = marginValue;
    }

    public int getImageTopY() {
        return rows[0];
    }

    public int getImageBottomY() {
        return rows[1];
    }

    public int getImageLeftX() {
        return columns[0];
    }

    public int getImageRightX() {
        return columns[1];
    }

    public void setRegion(BoundingBox region) {
        this.region = region;
    }

    public BoundingBox getRegion() {
        return region;
    }
}
