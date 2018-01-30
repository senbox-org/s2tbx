package org.esa.s2tbx.mapper.common;

import org.esa.snap.core.gpf.annotations.Parameter;

/**
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumInput {
    @Parameter(pattern = "[a-zA-Z_0-9]*")
    private String name;
    @Parameter
    private int[] xPixelPolygonPositions;
    @Parameter
    private int[] yPixelPolygonPositions;
    @Parameter
    private boolean isShapeDefined;


    public SpectrumInput(String name, int[] xPixelPolygonPositions, int[] yPixelPolygonPositions) {
        assert name != null;

        this.name = name;
        this.xPixelPolygonPositions = xPixelPolygonPositions;
        this.yPixelPolygonPositions = yPixelPolygonPositions;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int[] getXPixelPolygonPositions() {
        return this.xPixelPolygonPositions;
    }

    public int[] getYPixelPolygonPositions() {
        return this.yPixelPolygonPositions;
    }

    public void setIsShapeDefined(boolean isShapeDefined) {
        this.isShapeDefined = isShapeDefined;
    }

    public void setXPixelPolygonPositionIndex(int index, int value){
        this.xPixelPolygonPositions[index] = value;
    }

    public void setYPixelPolygonPositionIndex(int index, int value){
        this.yPixelPolygonPositions[index] = value;
    }

    public boolean getIsShapeDefined(){
        return this.isShapeDefined;
    }
}
