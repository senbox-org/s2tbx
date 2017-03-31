package org.esa.s2tbx.grm.segmentation;

/**
 * @author Jean Coravu
 */
public class BoundingBox {
    private int leftX;
    private int topY;
    private int width;
    private int height;

    public BoundingBox(int leftX, int topY, int width, int height) {
        this.leftX = leftX;
        this.topY = topY;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName())
                .append("@")
                .append(this.hashCode())
                .append(" [x=")
                .append(leftX)
                .append(", y=")
                .append(topY)
                .append(", width=")
                .append(width)
                .append(", height=")
                .append(height)
                .append("]");
        return str.toString();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLeftX() {
        return leftX;
    }

    public int getTopY() {
        return topY;
    }

    public int getRightX() {
        return this.leftX + this.width;
    }

    public int getBottomY() {
        return this.topY + this.height;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }
}
