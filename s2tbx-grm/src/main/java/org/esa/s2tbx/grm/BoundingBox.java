package org.esa.s2tbx.grm;

/**
 * @author Jean Coravu
 */
public class BoundingBox {
    private int upperLeftX;
    private int upperLeftY;
    private int width;
    private int height;

    public BoundingBox(int upperLeftX, int upperLeftY, int width, int height) {
        this.upperLeftX = upperLeftX;
        this.upperLeftY = upperLeftY;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getUpperLeftX() {
        return upperLeftX;
    }

    public int getUpperLeftY() {
        return upperLeftY;
    }

    public int getUpperRightX() {
        return this.upperLeftX + this.width;
    }

    public int getLowerRightY() {
        return this.upperLeftY + this.height;
    }
}
