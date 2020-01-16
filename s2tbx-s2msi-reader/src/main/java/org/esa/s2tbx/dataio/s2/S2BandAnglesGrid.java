package org.esa.s2tbx.dataio.s2;

/**
 * Created by obarrile on 24/06/2016.
 */
public class S2BandAnglesGrid {

    private int width;
    private int height;
    private String prefix;
    private S2BandConstants band;
    public float originX;
    public float originY;
    private float resX;
    private float resY;
    private float[] data;

    public S2BandAnglesGrid(String prefix, S2BandConstants band, int width, int height, float originX, float originY, float resX, float resY, float[] values) {

        this.width=width;
        this.height= height;
        this.prefix= prefix;
        this.band=band;
        this.originX=originX;
        this.originY=originY;
        this.resX=resX;
        this.resY=resY;
        data = new float[width*height];
        System.arraycopy(values,0,data,0,width*height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public S2BandConstants getBand() {
        return band;
    }

    public void setBand(S2BandConstants band) {
        this.band = band;
    }


    public float getResolutionX() {
        return resX;
    }

    public void setResX(float resX) {
        this.resX = resX;
    }

    public float getResolutionY() {
        return resY;
    }

    public void setResY(float resY) {
        this.resY = resY;
    }

    public float[] getData() {
        return data;
    }
 }

