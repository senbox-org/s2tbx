package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.grm.BoundingBox;
import org.esa.s2tbx.grm.Contour;

/**
 * @author Jean Coravu
 */
public class ProcessingTile {
    private final int[] rows; // lower and upper rows (-1 means that the row has not be considered)
    private final int[] columns; // lower and upper columns (-1 means that the row has not be considered)
    private final int[] margin; // the margin at top, left, bottom or right
    private BoundingBox region; // the image region

    private String nodeFileName;
    private String edgeFileName;
    private String nodeMarginFileName;
    private String edgeMarginFileName;

    public ProcessingTile() {
        rows = new int[2];
        columns = new int[2];
        margin = new int[4];
    }

    public String getNodeFileName() {
        return nodeFileName;
    }

    public void setNodeFileName(String nodeFileName) {
        this.nodeFileName = nodeFileName;
    }

    public String getEdgeFileName() {
        return edgeFileName;
    }

    public void setEdgeFileName(String edgeFileName) {
        this.edgeFileName = edgeFileName;
    }

    public String getNodeMarginFileName() {
        return nodeMarginFileName;
    }

    public void setNodeMarginFileName(String nodeMarginFileName) {
        this.nodeMarginFileName = nodeMarginFileName;
    }

    public String getEdgeMarginFileName() {
        return edgeMarginFileName;
    }

    public void setEdgeMarginFileName(String edgeMarginFileName) {
        this.edgeMarginFileName = edgeMarginFileName;
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

    public void setImageTopY(int topY) {
        rows[0] = topY;
    }

    public int getImageBottomY() {
        return rows[1];
    }

    public void setImageBottomY(int bottomY) {
        rows[1] = bottomY;
    }

    public int getImageLeftX() {
        return columns[0];
    }

    public void setImageLeftX(int leftX) {
        columns[0] = leftX;
    }

    public int getImageRightX() {
        return columns[1];
    }

    public void setImageRightX(int rightX) {
        columns[1] = rightX;
    }

    public void setRegion(BoundingBox region) {
        this.region = region;
    }

    public BoundingBox getRegion() {
        return region;
    }
}
