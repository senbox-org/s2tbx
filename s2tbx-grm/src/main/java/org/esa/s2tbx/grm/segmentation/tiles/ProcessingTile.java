package org.esa.s2tbx.grm.segmentation.tiles;

import org.esa.s2tbx.grm.segmentation.BoundingBox;
import org.esa.s2tbx.grm.segmentation.Contour;

/**
 * @author Jean Coravu
 */
public class ProcessingTile {
    private BoundingBox region; // the image region

    private int imageTopY;
    private int imageBottomY;

    private int imageLeftX;
    private int imageRightX;

    private int marginLeftX;
    private int marginRightX;
    private int marginTopY;
    private int marginBottomY;

    private String nodeFileName;
    private String edgeFileName;
    private String nodeMarginFileName;
    private String edgeMarginFileName;

    public ProcessingTile() {
        this.imageTopY = 0;
        this.imageBottomY = 0;

        this.imageLeftX = 0;
        this.imageRightX = 0;

        this.marginLeftX = 0;
        this.marginRightX = 0;
        this.marginTopY = 0;
        this.marginBottomY = 0;
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
        return this.marginTopY;
        //return this.margin[Contour.TOP_MOVE_INDEX];
    }

    public int getBottomMargin() {
        return this.marginBottomY;
        //return this.margin[Contour.BOTTOM_MOVE_INDEX];
    }

    public int getLeftMargin() {
        return this.marginLeftX;
        //return this.margin[Contour.LEFT_MOVE_INDEX];
    }

    public int getRightMargin() {
        return this.marginRightX;
        //return this.margin[Contour.RIGHT_MOVE_INDEX];
    }

    public void setTopMargin(int marginValue) {
        this.marginTopY = marginValue;
        //this.margin[Contour.TOP_MOVE_INDEX] = marginValue;
    }

    public void setBottomMargin(int marginValue) {
        this.marginBottomY = marginValue;
        //this.margin[Contour.BOTTOM_MOVE_INDEX] = marginValue;
    }

    public void setLeftMargin(int marginValue) {
        this.marginLeftX = marginValue;
        //this.margin[Contour.LEFT_MOVE_INDEX] = marginValue;
    }

    public void setRightMargin(int marginValue) {
        this.marginRightX = marginValue;
        //this.margin[Contour.RIGHT_MOVE_INDEX] = marginValue;
    }

    public int getImageTopY() {
        return this.imageTopY;
        //return rows[0];
    }

    public void setImageTopY(int topY) {
        this.imageTopY = topY;
        //rows[0] = topY;
    }

    public int getImageBottomY() {
        return this.imageBottomY;
        //return rows[1];
    }

    public void setImageBottomY(int bottomY) {
        this.imageBottomY = bottomY;
        //rows[1] = bottomY;
    }

    public int getImageLeftX() {
        return this.imageLeftX;
        //return columns[0];
    }

    public void setImageLeftX(int leftX) {
        this.imageLeftX = leftX;
        //columns[0] = leftX;
    }

    public int getImageRightX() {
        return this.imageRightX;
        //return columns[1];
    }

    public void setImageRightX(int rightX) {
        this.imageRightX = rightX;
        //columns[1] = rightX;
    }

    public void setRegion(BoundingBox region) {
        this.region = region;
    }

    public BoundingBox getRegion() {
        return region;
    }

    public int getImageWidth() {
        return getImageRightX() - getImageLeftX() + 1;
    }

    public int getImageHeight() {
        return getImageBottomY() - getImageTopY() + 1;
    }
}
