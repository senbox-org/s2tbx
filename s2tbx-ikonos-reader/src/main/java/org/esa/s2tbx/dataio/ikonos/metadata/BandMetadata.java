package org.esa.s2tbx.dataio.ikonos.metadata;


/**
 * Class to parse metadata files for specific image files
 *
 * @author Denisa Stefanescu
 */
public class BandMetadata {

    private int bitsPerPixel;
    private int numColumns;
    private int numLines;
    private double pixelSizeX;
    private double pixelSizeY;
    private String imageFileName;
    private double nominalAzimuth;
    private double sunAngleAzimuth;
    private double nominalElevation;
    private double sunAngleElevation;

    public BandMetadata(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumLines() {
        return numLines;
    }

    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public double getPixelSizeX() {
        return pixelSizeX;
    }

    public void setPixelSizeX(double pixelSizeX) {
        this.pixelSizeX = pixelSizeX;
    }

    public double getPixelSizeY() {
        return pixelSizeY;
    }

    public void setPixelSizeY(double pixelSizeY) {
        this.pixelSizeY = pixelSizeY;
    }

    public double getNominalAzimuth() {
        return nominalAzimuth;
    }

    public void setNominalAzimuth(double nominalAzimuth) {
        this.nominalAzimuth = nominalAzimuth;
    }

    public double getSunAngleAzimuth() {
        return sunAngleAzimuth;
    }

    public void setSunAngleAzimuth(double sunAngleAzimuth) {
        this.sunAngleAzimuth = sunAngleAzimuth;
    }

    public double getNominalElevation() {
        return nominalElevation;
    }

    public void setNominalElevation(double nominalElevation) {
        this.nominalElevation = nominalElevation;
    }

    public double getSunAngleElevation() {
        return sunAngleElevation;
    }


    public void setSunAngleElevation(double sunAngleElevation) {
        this.sunAngleElevation = sunAngleElevation;
    }

}
